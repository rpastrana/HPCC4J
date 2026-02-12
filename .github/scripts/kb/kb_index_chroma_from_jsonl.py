#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
kb_index_chroma_from_jsonl.py

Reads a refined KB index (JSONL), computes embeddings with SentenceTransformers,
writes a persistent Chroma collection, and emits a MANIFEST.json with metadata.

Env:
  KB_INDEX_JSONL   -> path to index.jsonl (default: .kb_index_enhanced/index.jsonl)
  KB_DB_DIR        -> output Chroma persist dir (default: .kb_index)
  KB_EMBED_MODEL   -> sentence-transformers model (default: sentence-transformers/all-MiniLM-L6-v2)
  KB_COLLECTION    -> collection name (default: hpcckb)
  KB_BATCH_SIZE    -> embedding batch size (default: 256)
  KB_SOURCE_SHA    -> git sha (optional)
  KB_SOURCE_REF    -> git ref (optional)
  KB_SOURCE_REPO   -> repo name (optional)
"""

from __future__ import annotations
import json
import math
import os
import sys
from typing import Dict, Any, Iterable, List

def evar(name: str, default: str | None = None) -> str:
    v = os.environ.get(name)
    return v if v is not None else (default or "")

INDEX_PATH   = evar("KB_INDEX_JSONL", ".kb_index_enhanced/index.jsonl")
PERSIST_DIR  = evar("KB_DB_DIR", ".kb_index")
MODEL_NAME   = evar("KB_EMBED_MODEL", "sentence-transformers/all-MiniLM-L6-v2")
COLLECTION   = evar("KB_COLLECTION", "hpcckb")
BATCH_SIZE   = int(evar("KB_BATCH_SIZE", "256") or "256")
SRC_SHA      = evar("KB_SOURCE_SHA", "")
SRC_REF      = evar("KB_SOURCE_REF", "")
SRC_REPO     = evar("KB_SOURCE_REPO", "")

# ----------------------------- helpers ---------------------------------------

def read_jsonl(path: str) -> Iterable[Dict[str, Any]]:
    with open(path, "r", encoding="utf-8") as f:
        for line in f:
            line=line.strip()
            if not line:
                continue
            try:
                yield json.loads(line)
            except Exception:
                continue

def extract_text(obj: Dict[str, Any]) -> str:
    for k in ("text","page_content","content","chunk","body"):
        v = obj.get(k)
        if isinstance(v, str) and v.strip():
            return v
    return ""

def extract_id(obj: Dict[str, Any], i: int) -> str:
    v = obj.get("id")
    if isinstance(v, (str,int)):
        return str(v)
    return f"auto-{i}"

def _is_primitive(v: Any) -> bool:
    # Allow str, int, float (not NaN), bool
    if isinstance(v, (str, int, bool)):
        return True
    if isinstance(v, float):
        return not math.isnan(v)
    return False

def sanitize_metadata(meta: Any, *, doc_id: str, idx: int) -> Dict[str, Any]:
    """
    Ensure metadata is a non-empty dict with primitive values.
    - If empty/missing, inject {"doc_id": doc_id}
    - Coerce keys to strings; drop None; stringify non-primitives
    """
    out: Dict[str, Any] = {}
    if isinstance(meta, dict):
        for k, v in meta.items():
            if k is None:
                continue
            key = str(k)
            if v is None:
                continue
            if _is_primitive(v):
                out[key] = v
            else:
                # stringify lists/dicts/objects to satisfy Chroma validator
                out[key] = json.dumps(v, ensure_ascii=False)
    # Guarantee non-empty
    if not out:
        out = {"doc_id": doc_id}
    return out

# ------------------------------ main -----------------------------------------

def main() -> None:
    if not os.path.isfile(INDEX_PATH) or os.path.getsize(INDEX_PATH) == 0:
        print(f"::error :: index.jsonl missing or empty: {INDEX_PATH}", file=sys.stderr)
        sys.exit(1)

    docs: List[str] = []
    metas_raw: List[Any] = []
    ids: List[str] = []

    for i, obj in enumerate(read_jsonl(INDEX_PATH)):
        txt = extract_text(obj)
        if not txt.strip():
            continue
        did = extract_id(obj, i)
        docs.append(txt)
        metas_raw.append(obj.get("metadata"))
        ids.append(did)

    if not docs:
        print("::error :: No usable text chunks found in index.jsonl.", file=sys.stderr)
        sys.exit(1)

    # Build Chroma (with explicit embeddings)
    from sentence_transformers import SentenceTransformer
    import chromadb
    from chromadb.config import Settings

    os.makedirs(PERSIST_DIR, exist_ok=True)
    client = chromadb.PersistentClient(path=PERSIST_DIR, settings=Settings(anonymized_telemetry=False))

    # Reset collection to avoid duplicates across runs
    try:
        client.delete_collection(COLLECTION)
    except Exception:
        pass
    try:
        col = client.create_collection(COLLECTION, metadata={"hnsw:space":"cosine"})
    except TypeError:
        # older API signature
        col = client.create_collection(name=COLLECTION, metadata={"hnsw:space":"cosine"})

    model = SentenceTransformer(MODEL_NAME)

    total = 0
    B = BATCH_SIZE
    for s in range(0, len(docs), B):
        batch_docs  = docs[s:s+B]
        batch_ids   = ids[s:s+B]
        batch_metas_raw = metas_raw[s:s+B]

        # Sanitize metadatas to satisfy Chroma's validator
        batch_metas = [sanitize_metadata(m, doc_id=did, idx=(s+i))
                       for i, (m, did) in enumerate(zip(batch_metas_raw, batch_ids))]

        emb = model.encode(batch_docs, normalize_embeddings=True, show_progress_bar=False)
        col.add(ids=batch_ids,
                documents=batch_docs,
                metadatas=batch_metas,
                embeddings=emb.tolist())
        total += len(batch_docs)

    # MANIFEST
    manifest = {
        "collection": COLLECTION,
        "embed_model": MODEL_NAME,
        "count": total,
        "source_index": os.path.abspath(INDEX_PATH),
        "source_sha": SRC_SHA,
        "source_ref": SRC_REF,
        "source_repo": SRC_REPO,
    }
    with open(os.path.join(PERSIST_DIR, "MANIFEST.json"), "w", encoding="utf-8") as f:
        json.dump(manifest, f, ensure_ascii=False, indent=2)

    # Sanity: list collections
    names=[]
    for c in client.list_collections():
        n = getattr(c,"name",None) or (isinstance(c,dict) and c.get("name"))
        if n: names.append(n)
    print(f"::notice :: Built Chroma at '{PERSIST_DIR}' | collection='{COLLECTION}' | chunks={total} | collections={names}")

if __name__ == "__main__":
    main()