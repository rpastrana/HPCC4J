#!/usr/bin/env python3
# .github/scripts/rag_agent.py
# RAG agent that consumes a persisted Chroma index and answers with citations.
# - No API keys required (local HuggingFace Transformers model).
# - Compatible with LangChain 0.2.x and 0.3.x.
#
# Usage:
#   python .github/scripts/rag_agent.py "your question here"
#   # or interactive:
#   python .github/scripts/rag_agent.py
#
# Environment variables (defaults shown):
#   KB_DB_DIR=".kb_index"                   # persisted Chroma directory
#   KB_EMBED_MODEL="sentence-transformers/all-MiniLM-L6-v2"
#   KB_COLLECTION_NAME="hpcckb"
#   KB_RETRIEVER_K="6"
#   KB_RETRIEVER_FETCH_K="20"
#   LLM_MODEL="TinyLlama/TinyLlama-1.1B-Chat-v1.0"   # small CPU-friendly default
#   LLM_TEMPERATURE="0.2"
#
# Optional performance tuning:
#   HF_HOME=...                             # HuggingFace cache directory
#   TRANSFORMERS_CACHE=...                  # model cache directory

import os
import sys
from typing import List

# -------- LangChain core imports with compatibility for 0.2.x / 0.3.x --------
try:
    # LangChain 0.3.x
    from langchain_core.prompts import ChatPromptTemplate
    from langchain_core.output_parsers import StrOutputParser
    from langchain_core.runnables import RunnablePassthrough
except Exception:
    # LangChain 0.2.x fallbacks
    try:
        from langchain.prompts import ChatPromptTemplate  # type: ignore
    except Exception as e:
        raise ImportError("LangChain prompts not found. Ensure langchain is installed.") from e
    try:
        from langchain_core.output_parsers import StrOutputParser  # type: ignore
    except Exception:
        from langchain.schema.output_parser import StrOutputParser  # type: ignore
    try:
        from langchain_core.runnables import RunnablePassthrough  # type: ignore
    except Exception:
        from langchain.schema.runnable import RunnablePassthrough  # type: ignore

# Vector store + embeddings
from langchain_community.vectorstores import Chroma
from langchain_huggingface import HuggingFaceEmbeddings, HuggingFacePipeline

# Local LLM (Transformers)
import torch
from transformers import AutoModelForCausalLM, AutoTokenizer, pipeline


# -------------------------
# Configuration (env-driven)
# -------------------------

DB_DIR = os.environ.get("KB_DB_DIR", ".kb_index")
EMBED_MODEL = os.environ.get("KB_EMBED_MODEL", "sentence-transformers/all-MiniLM-L6-v2")
COLLECTION = os.environ.get("KB_COLLECTION_NAME", "hpcckb")

RETRIEVER_K = int(os.environ.get("KB_RETRIEVER_K", "6"))
RETRIEVER_FETCH_K = int(os.environ.get("KB_RETRIEVER_FETCH_K", "20"))

LLM_MODEL = os.environ.get("LLM_MODEL", "TinyLlama/TinyLlama-1.1B-Chat-v1.0")
LLM_TEMPERATURE = float(os.environ.get("LLM_TEMPERATURE", "0.2"))

# Quiet Chroma telemetry (belt & suspenders with your workflow env)
os.environ.setdefault("ANONYMIZED_TELEMETRY", "false")
os.environ.setdefault("CHROMA_ANONYMIZED_TELEMETRY", "false")
os.environ.setdefault("CHROMA_TELEMETRY_IMPLEMENTATION", "none")


# -------------------------
# Retriever over Chroma
# -------------------------

def build_retriever():
    # Use the SAME embedding model as the index
    embeddings = HuggingFaceEmbeddings(model_name=EMBED_MODEL)

    db = Chroma(
        collection_name=COLLECTION,
        embedding_function=embeddings,
        persist_directory=DB_DIR,
    )

    # MMR for diversity (helps avoid redundant chunks)
    retriever = db.as_retriever(
        search_type="mmr",
        search_kwargs={"k": RETRIEVER_K, "fetch_k": RETRIEVER_FETCH_K}
    )
    return retriever


def format_docs(docs) -> str:
    """Concatenate retrieved docs with inline source and chunk id markers."""
    lines: List[str] = []
    for d in docs:
        src = d.metadata.get("source", "unknown")
        chunk_i = d.metadata.get("chunk", d.metadata.get("chunk_index", "?"))
        lines.append(f"[Source: {src} | chunk {chunk_i}]\n{d.page_content}\n")
    return "\n---\n".join(lines)


# -------------------------
# Local HF LLM (no API keys)
# -------------------------

def _select_dtype() -> torch.dtype:
    if torch.cuda.is_available():
        return torch.float16
    # bfloat16 can be faster on some CPUs supporting AVX512/BF16, but float32 is safest
    return torch.float32


def build_llm():
    """
    Build a local HuggingFace text-generation pipeline and wrap it with LangChain.
    Defaults to a very small chat model to keep CPU inference feasible on GitHub runners.
    You can override via LLM_MODEL env var (e.g., 'microsoft/Phi-2' or 'Qwen/Qwen2.5-1.5B-Instruct').
    """
    print(f"Loading local HF model: {LLM_MODEL}")
    dtype = _select_dtype()
    device = 0 if torch.cuda.is_available() else -1  # -1 = CPU

    # Some small instruct-tuned models may require trust_remote_code=True for custom generation heads.
    # Keep False by default for safety; set env TRUST_REMOTE_CODE=true to enable if needed.
    trust_remote_code = os.environ.get("TRUST_REMOTE_CODE", "false").lower() == "true"

    tokenizer = AutoTokenizer.from_pretrained(LLM_MODEL, trust_remote_code=trust_remote_code)
    model = AutoModelForCausalLM.from_pretrained(
        LLM_MODEL,
        torch_dtype=dtype,
        device_map="auto" if device == 0 else None,
        trust_remote_code=trust_remote_code,
    )

    gen_pipe = pipeline(
        "text-generation",
        model=model,
        tokenizer=tokenizer,
        max_new_tokens=512,
        temperature=LLM_TEMPERATURE,
        do_sample=LLM_TEMPERATURE > 0,
        top_p=0.95,
        repetition_penalty=1.05,
        pad_token_id=tokenizer.eos_token_id if tokenizer.eos_token_id is not None else None,
        device=device,
    )

    return HuggingFacePipeline(pipeline=gen_pipe)


# -------------------------
# Grounded prompt (citations)
# -------------------------

PROMPT = ChatPromptTemplate.from_messages(
    [
        (
            "system",
            "You are an HPCC Systems expert. Use ONLY the provided context to answer. "
            "If the answer is not in the context, say you don't know. "
            "Cite sources inline like (source: <path>) and end with a 'Sources' section "
            "listing the unique file paths used."
        ),
        (
            "human",
            "Question:\n{question}\n\n"
            "Context:\n{context}\n\n"
            "Provide a concise, actionable answer with citations."
        ),
    ]
)


# -------------------------
# Build the RAG chain
# -------------------------

def build_chain():
    retriever = build_retriever()
    llm = build_llm()

    chain = {
        "context": retriever | format_docs,
        "question": RunnablePassthrough(),
    } | PROMPT | llm | StrOutputParser()

    return chain, retriever


# -------------------------
# CLI helpers
# -------------------------

def answer_question(q: str):
    chain, retriever = build_chain()
    docs = retriever.invoke(q)
    answer = chain.invoke(q)

    # Ensure sources are visible even if the model omits them
    unique_sources = []
    for d in docs:
        src = d.metadata.get("source")
        if src and src not in unique_sources:
            unique_sources.append(src)

    print("\n=== Answer ===\n")
    print(answer.strip())
    if unique_sources:
        print("\n--- Top sources (retrieved) ---")
        for s in unique_sources:
            print(s)


def main():
    if not os.path.isdir(DB_DIR):
        print(f"ERROR: Persisted index directory not found: {DB_DIR}", file=sys.stderr)
        sys.exit(2)

    if len(sys.argv) > 1:
        question = " ".join(sys.argv[1:]).strip()
        if not question:
            print("Provide a non-empty question.")
            sys.exit(1)
        answer_question(question)
        return

    # Interactive REPL
    print("RAG Agent (HPCC KB, local HF model). Type 'exit' to quit.\n")
    while True:
        try:
            q = input("> ").strip()
        except (EOFError, KeyboardInterrupt):
            print()
            break
        if not q or q.lower() in {"exit", "quit"}:
            break
        answer_question(q)


if __name__ == "__main__":
    main()
