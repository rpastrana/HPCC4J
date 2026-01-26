from __future__ import annotations
import argparse, json, os, sys
from typing import Any, Dict, List
import chromadb

def load_manifest(db_dir: str) -> Dict[str, Any]:
    path = os.path.join(db_dir, "MANIFEST.json")
    if os.path.isfile(path):
        with open(path, "r", encoding="utf-8") as f:
            return json.load(f)
    return {}

def fmt_row(i, doc, meta, dist=None):
    src = meta.get("source_path") or meta.get("source") or meta.get("path") or "unknown"
    head = doc.strip().splitlines()[0][:120]
    d = f"  (distance: {dist:.4f})" if dist is not None else ""
    return f"{i+1}. {src}{d}\n    {head}"

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--db", required=True)
    ap.add_argument("--collection", default="hpcckb")
    ap.add_argument("--question", required=True)
    ap.add_argument("--top-k", type=int, default=5)
    args = ap.parse_args()

    client = chromadb.PersistentClient(path=args.db)
    manifest = load_manifest(args.db)
    collection_name = manifest.get("collection") or args.collection

    try:
        col = client.get_collection(collection_name)
    except Exception:
        print(f"::error :: Collection '{collection_name}' not found in {args.db}", file=sys.stderr)
        sys.exit(1)

    res = col.query(query_texts=[args.question], n_results=args.top_k, include=["distances", "documents", "metadatas", "ids"])
    docs = res.get("documents", [[]])[0]
    metas = res.get("metadatas", [[]])[0]
    dists = res.get("distances", [[]])[0]

    # Build a concise, grounded answer (heuristic; no LLM)
    answer_lines = [f"### Question", args.question, "", "### Top Matches"]
    for i, (doc, meta, dist) in enumerate(zip(docs, metas, dists)):
        answer_lines.append(fmt_row(i, doc, meta, dist))
    answer_lines += ["", "### Grounded Answer (heuristic)",
                     "Below are the most relevant excerpts; see sources above."]

    os.makedirs("dist", exist_ok=True)
    with open("dist/answer.md", "w", encoding="utf-8") as f:
        f.write("\n".join(answer_lines))
    print("\n".join(answer_lines))

if __name__ == "__main__":
    main()