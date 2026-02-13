#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# kb_index_chroma_from_jsonl.py
# Reads refined KB JSONL, writes Chroma collection, emits MANIFEST.
# Adds: --print-config and richer metadata extraction from top-level fields.

from __future__ import annotations
import argparse, json, math, os, sys
from typing import Dict, Any, Iterable, List

def parse_args():
    p = argparse.ArgumentParser(description="Index refined KB JSONL into a Chroma collection.")
    p.add_argument("--print-config", action="store_true",
                   help="Print resolved configuration and exit (no writes).")
    return p.parse_args()

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

TEXT_KEYS = {"text", "page_content", "content", "chunk", "body"}
ADMIN_KEYS = {"id", "tokens_estimate", "chunk_index", "chunk_count_in_section",
              "char_span_in_section", "deprecated", "deprecation_note",
              "version_introduced", "version_deprecated", "version_removed"}

def read_jsonl(path: str) -> Iterable[Dict[str, Any]]:
    with open(path, "r", encoding="utf-8") as f:
        for line in f:
            line=line.strip()
            if not line: continue
            try: yield json.loads(line)
            except Exception: continue

def extract_text(obj: Dict[str, Any]) -> str:
    for k in TEXT_KEYS:
        v = obj.get(k)
        if isinstance(v, str) and v.strip(): return v
    return ""

def extract_id(obj: Dict[str, Any], i: int) -> str:
    v = obj.get("id")
    if isinstance(v, (str, int)): return str(v)
    return f"auto-{i}"

def _is_primitive(v: Any) -> bool:
    if isinstance(v, (str, int, bool)): return True
    if isinstance(v, float): return not math.isnan(v)
    return False

def extract_metadata(obj: Dict[str, Any]) -> Dict[str, Any]:
    md: Dict[str, Any] = {}
    # Merge incoming metadata (rare)
    if isinstance(obj.get("metadata"), dict):
        for k, v in obj["metadata"].items():
            if v is None: continue
            md[str(k)] = v if _is_primitive(v) else json.dumps(v, ensure_ascii=False)
    # Add top-level fields
    for k, v in obj.items():
        if k in TEXT_KEYS or k in ADMIN_KEYS or k == "metadata": continue
        if v is None: continue
        md[str(k)] = v if _is_primitive(v) else json.dumps(v, ensure_ascii=False)
    # Ensure anchors
    if "source_path" not in md and obj.get("source_path"): md["source_path"] = obj["source_path"]
    if "source_url" not in md and obj.get("source_url"):   md["source_url"] = obj["source_url"]
    return md or {"note": "no_metadata"}

def main() -> None:
    args = parse_args()

    if args.print_config:
        print(f"[KB-INDEX] store=Chroma collection={COLLECTION}")
        print(f"[KB-INDEX] embed_model={MODEL_NAME} batch={BATCH_SIZE}")
        print(f"[KB-INDEX] index_jsonl={INDEX_PATH}")
        print(f"[KB-INDEX] persist_dir={PERSIST_DIR}")
        print(f"[KB-INDEX] source: sha={SRC_SHA} ref={SRC_REF} repo={SRC_REPO}")
        sys.exit(0)

    if not os.path.isfile(INDEX_PATH) or os.path.getsize(INDEX_PATH) == 0:
        print(f"::error :: index.jsonl missing or empty: {INDEX_PATH}", file=sys.stderr)
        sys.exit(1)

    docs, metadatas, ids = [], [], []

    for i, obj in enumerate(read_jsonl(INDEX_PATH)):
        txt = extract_text(obj)
        if not txt.strip(): continue
        did = extract_id(obj, i)
        md  = extract_metadata(obj)
        docs.append(txt); metadatas.append(md); ids.append(did)

    if not docs:
        print("::error :: No usable text chunks found.", file=sys.stderr)
        sys.exit(1)

    from sentence_transformers import SentenceTransformer
    import chromadb
    from chromadb.config import Settings

    os.makedirs(PERSIST_DIR, exist_ok=True)
    client = chromadb.PersistentClient(path=PERSIST_DIR, settings=Settings(anonymized_telemetry=False))
    try: client.delete_collection(COLLECTION)
    except Exception: pass
    try:
        col = client.create_collection(COLLECTION, metadata={"hnsw:space":"cosine"})
    except TypeError:
        col = client.create_collection(name=COLLECTION, metadata={"hnsw:space":"cosine"})

    model = SentenceTransformer(MODEL_NAME)

    total = 0
    B = BATCH_SIZE
    for s in range(0, len(docs), B):
        batch_docs  = docs[s:s+B]
        batch_ids   = ids[s:s+B]
        batch_metas = metadatas[s:s+B]
        emb = model.encode(batch_docs, normalize_embeddings=True, show_progress_bar=False)
        col.add(ids=batch_ids, documents=batch_docs, metadatas=batch_metas, embeddings=emb.tolist())
        total += len(batch_docs)

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

    names=[]
    for c in client.list_collections():
        n = getattr(c, "name", None) or (isinstance(c, dict) and c.get("name"))
        if n: names.append(n)

    print(f"::notice :: Built Chroma at '{PERSIST_DIR}' | collection='{COLLECTION}' | chunks={total} | collections={names}")

if __name__ == "__main__":
    main()