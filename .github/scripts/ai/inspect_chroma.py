#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from __future__ import annotations
import argparse, json, os, sys
from typing import List, Dict, Any

def list_collections(persist_path: str) -> List[str]:
    try:
        import chromadb
    except Exception as e:
        print(f"::error :: chromadb import failed: {e}", file=sys.stderr)
        sys.exit(1)
    client = chromadb.PersistentClient(path=persist_path)
    names: List[str] = []
    for c in client.list_collections():
        n = getattr(c, "name", None) or (isinstance(c, dict) and c.get("name"))
        if n:
            names.append(n)
    return names

def load_manifest(persist_path: str) -> Dict[str, Any] | None:
    m = os.path.join(persist_path, "MANIFEST.json")
    if os.path.isfile(m):
        try:
            with open(m, "r", encoding="utf-8") as f:
                return json.load(f)
        except Exception as e:
            print(f"::warning :: Failed to read MANIFEST.json: {e}", file=sys.stderr)
    return None

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--db", default=".kb_index", help="Chroma persist directory")
    ap.add_argument("--print-manifest", action="store_true")
    ap.add_argument("--require-collection", action="store_true", help="Exit 1 if no collections exist")
    args = ap.parse_args()

    if not os.path.isdir(args.db):
        print(f"::error :: Persist dir not found: {args.db}", file=sys.stderr)
        sys.exit(1)

    names = list_collections(args.db)
    print(f"[inspect] collections: {names}")

    man = load_manifest(args.db)
    if args.print-manifest:
        if man:
            print("[inspect] MANIFEST:", json.dumps(man, indent=2))
        else:
            print("[inspect] MANIFEST: <missing>")

    if args.require-collection and not names:
        print("::error :: No Chroma collections found.", file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    main()