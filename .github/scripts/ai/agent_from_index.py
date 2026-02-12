#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from __future__ import annotations
import argparse, json, math, os, sys
from typing import Any, Dict, List
import chromadb
from chromadb.config import Settings
from sentence_transformers import SentenceTransformer
from tqdm import tqdm

def read_jsonl(path: str):
    with open(path, "r", encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            try:
                yield json.loads(line)
            except Exception:
                continue

def extract_text(obj: Dict[str, Any]) -> str:
    for k in ("text","content","page_content","chunk","body"):
        v = obj.get(k)
        if isinstance(v, str) and v.strip():
            return v
    return ""

def extract_id(obj: Dict[str,Any], i: int) -> str:
    return str(obj.get("id") or f"auto-{i}")

def _is_primitive(v: Any) -> bool:
    if isinstance(v, (str, int, bool)):
        return True
    if isinstance(v, float):
        return not math.isnan(v)
    return False

def sanitize_metadata(meta: Any, *, doc_id: str) -> Dict[str, Any]:
    """
    Ensure metadata is a non-empty dict with primitive values.
    - If empty/missing, inject {"doc_id": doc_id}
    - Coerce keys to strings; drop None; stringify non-primitives
    """
    out: Dict[str, Any] = {}
    if isinstance(meta, dict):
        for k, v in meta.items():
            if k is None or v is None:
                continue
            key = str(k)
            if _is_primitive(v):
                out[key] = v
            else:
                out[key] = json.dumps(v, ensure_ascii=False)
    if not out:
        out = {"doc_id": doc_id}
    return out

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--index", required=True, help="Path to index.jsonl")
    ap.add_argument("--persist", default=".kb_index_ephemeral", help="Where to build temporary Chroma store")
    ap.add_argument("--collection", default="hpcckb")
    ap.add_argument("--model", default="sentence-transformers/all-MiniLM-L6-v2")
    ap.add_argument("--batch-size", type=int, default=256)
    ap.add_argument("--question", required=True)
    ap.add_argument("--top-k", type=int, default=5)
    args = ap.parse_args()

    if not os.path.isfile(args.index) or os.path.getsize(args.index) == 0:
        print(f"::error :: index.jsonl missing or empty: {args.index}", file=sys.stderr)
        sys.exit(1)

    os.makedirs(args.persist, exist_ok=True)
    client = chromadb.PersistentClient(path=args.persist, settings=Settings(anonymized_telemetry=False))
    try:
        client.delete_collection(args.collection)
    except Exception:
        pass
    col = client.create_collection(args.collection, metadata={"hnsw:space":"cosine"})

    # Load docs
    ids, docs, metas_raw = [], [], []
    for i, obj in enumerate(read_jsonl(args.index)):
        txt = extract_text(obj)
        if not txt.strip():
            continue
        did = extract_id(obj, i)
        ids.append(did)
        docs.append(txt)
        metas_raw.append(obj.get("metadata"))

    if not docs:
        print(f"::error :: No documents found in {args.index}", file=sys.stderr)
        sys.exit(1)

    model = SentenceTransformer(args.model)
    B = args.batch_size
    for s in tqdm(range(0, len(docs), B), desc="Embedding"):
        batch_docs = docs[s:s+B]
        batch_ids  = ids[s:s+B]
        batch_meta_raw = metas_raw[s:s+B]
        batch_metas = [sanitize_metadata(m, doc_id=did) for m, did in zip(batch_meta_raw, batch_ids)]

        emb = model.encode(batch_docs, normalize_embeddings=True, show_progress_bar=False)
        col.add(ids=batch_ids, documents=batch_docs, metadatas=batch_metas, embeddings=emb.tolist())

    # NOTE: 'ids' is not a valid include key in recent Chroma versions.
    res = col.query(
        query_texts=[args.question],
        n_results=args.top_k,
        include=["distances", "documents", "metadatas"],
    )

    docs_r = res.get("documents", [[]])[0]
    metas_r = res.get("metadatas", [[]])[0]
    dists_r = res.get("distances", [[]])[0]

    def fmt_row(i, doc, meta, dist):
        src = (meta or {}).get("source_path") or (meta or {}).get("source") or (meta or {}).get("path") or (meta or {}).get("doc_id") or "unknown"
        head = (doc or "").strip().splitlines()[0][:120]
        return f"{i+1}. {src}  (distance: {dist:.4f})\n    {head}"

    lines = [f"### Question", args.question, "", "### Top Matches"]
    for i, (d, m, dist) in enumerate(zip(docs_r, metas_r, dists_r)):
        lines.append(fmt_row(i, d, m, dist))
    lines += ["", "### Grounded Answer (heuristic)",
              "Below are the most relevant excerpts; see sources above."]

    os.makedirs("dist", exist_ok=True)
    with open("dist/answer.md", "w", encoding="utf-8") as f:
        f.write("\n".join(lines))
    print("\n".join(lines))

if __name__ == "__main__":
    main()