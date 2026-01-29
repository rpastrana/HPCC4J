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
    except Exception:
        raise ImportError("LangChain prompts not found. Ensure langchain is installed.")
    try:
        from langchain_core.output_parsers import StrOutputParser  # type: ignore
    except Exception:
        # Older fallback
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
