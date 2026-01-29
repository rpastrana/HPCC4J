#!/usr/bin/env python3
# .github/scripts/rag_agent.py
# Simple RAG agent that consumes a persisted Chroma index and answers with citations.

import os
import sys
from typing import List

# --- LangChain imports (support both 0.2.x and 0.3.x lines) ---
try:
    # 0.3.x
    from langchain_core.prompts import ChatPromptTemplate
    from langchain_core.output_parsers import StrOutputParser
    from langchain_core.runnables import RunnablePassthrough
except ImportError:
    # 0.2.x
    from langchain.prompts import ChatPromptTemplate
    from langchain.schema.output_parser import StrOutputParser
    from langchain.schema.runnable import RunnablePassthrough

from langchain_community.vectorstores import Chroma
from langchain_huggingface import HuggingFaceEmbeddings
from langchain_openai import ChatOpenAI, AzureChatOpenAI


# -------------------------
# Configuration (env-driven)
# -------------------------

DB_DIR = os.environ.get("KB_DB_DIR", ".kb_index")
EMBED_MODEL = os.environ.get("KB_EMBED_MODEL", "sentence-transformers/all-MiniLM-L6-v2")
COLLECTION = os.environ.get("KB_COLLECTION_NAME", "hpcckb")

# Retrieval knobs
RETRIEVER_K = int(os.environ.get("KB_RETRIEVER_K", "6"))
RETRIEVER_FETCH_K = int(os.environ.get("KB_RETRIEVER_FETCH_K", "20"))

# LLM selection
LLM_PROVIDER = os.environ.get("LLM_PROVIDER", "openai").lower()  # 'openai' or 'azure'
OPENAI_MODEL = os.environ.get("OPENAI_MODEL", "gpt-4o-mini")     # cost-friendly default
TEMPERATURE = float(os.environ.get("LLM_TEMPERATURE", "0.2"))

# Azure OpenAI envs (if you choose provider=azure)
# AZURE_OPENAI_API_KEY, AZURE_OPENAI_ENDPOINT, AZURE_OPENAI_API_VERSION, AZURE_OPENAI_DEPLOYMENT


# ------------------------------------
# Build retriever over persisted index
# ------------------------------------

def build_retriever():
    # Use the SAME embedding model you used during indexing
    embeddings = HuggingFaceEmbeddings(model_name=EMBED_MODEL)

    # Load persisted vector store
    db = Chroma(
        collection_name=COLLECTION,
        embedding_function=embeddings,
        persist_directory=DB_DIR,
    )

    # MMR retrieval (diverse context)
    retriever = db.as_retriever(
        search_type="mmr",
        search_kwargs={"k": RETRIEVER_K, "fetch_k": RETRIEVER_FETCH_K}
    )
    return retriever


def format_docs(docs) -> str:
    """Turn retrieved docs into a single string with clear source tags."""
    lines: List[str] = []
    for d in docs:
        src = d.metadata.get("source", "unknown")
        chunk_i = d.metadata.get("chunk", d.metadata.get("chunk_index", "?"))
        lines.append(f"[Source: {src} | chunk {chunk_i}]\n{d.page_content}\n")
    return "\n---\n".join(lines)


# -------------------------
# LLM setup
# -------------------------

def build_llm():
    if LLM_PROVIDER == "azure":
        # Requires: AZURE_OPENAI_* env vars + a deployment name
        deployment = os.environ.get("AZURE_OPENAI_DEPLOYMENT", OPENAI_MODEL)
        return AzureChatOpenAI(
            azure_deployment=deployment,
            temperature=TEMPERATURE,
            timeout=60,
        )
    # Default: OpenAI
    return ChatOpenAI(
        model=OPENAI_MODEL,
        temperature=TEMPERATURE,
        timeout=60,
    )


# -------------------------
# Prompt (grounded, with citations)
# -------------------------

PROMPT = ChatPromptTemplate.from_messages(
    [
        (
            "system",
            "You are an HPCC Systems assistant. Answer using ONLY the provided context. "
            "If the answer is not in the context, say you don't know. "
            "Cite sources inline like (source: <path>) and include a final 'Sources' section "
            "listing the unique file paths you used.",
        ),
        (
            "human",
            "Question:\n{question}\n\n"
            "Context:\n{context}\n\n"
            "Answer:"
        ),
    ]
)


# -------------------------
# Build the RAG chain (LCEL)
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
# CLI entrypoints
# -------------------------

def answer_question(q: str):
    chain, retriever = build_chain()
    # Grab the top docs (for separate source printing, optional)
    docs = retriever.invoke(q)
    answer = chain.invoke(q)

    # Extract and print unique sources from retrieved docs
    unique_sources = []
    for d in docs:
        src = d.metadata.get("source")
        if src and src not in unique_sources:
            unique_sources.append(src)

    print("\n=== Answer ===\n")
    print(answer.strip())
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

    # Interactive mode
    print("RAG Agent (HPCC KB). Type 'exit' to quit.\n")
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
