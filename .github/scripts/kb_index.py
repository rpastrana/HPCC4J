# .github/scripts/kb_index.py
# Build a persisted vector index from files under kb/** using Chroma + HuggingFace embeddings.
# Uses sentence-transformers/all-MiniLM-L6-v2 (no API key required).

import os
from glob import glob
from typing import Iterator, Tuple

from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_huggingface import HuggingFaceEmbeddings   # updated import
from langchain_community.vectorstores import Chroma

KB_DIR = os.environ.get("KB_DIR", "kb")
DB_DIR = os.environ.get("KB_DB_DIR", ".kb_index")
CHUNK_SIZE = int(os.environ.get("KB_CHUNK_SIZE", 900))
CHUNK_OVERLAP = int(os.environ.get("KB_CHUNK_OVERLAP", 120))
MODEL_NAME = os.environ.get("KB_EMBED_MODEL", "sentence-transformers/all-MiniLM-L6-v2")
SUPPORTED_EXTS = [ext.strip() for ext in os.environ.get("KB_FILE_EXTS", ".md,.txt").split(",") if ext.strip()]

def iter_docs() -> Iterator[Tuple[str, str]]:
    paths = []
    for ext in SUPPORTED_EXTS:
        pattern = os.path.join(KB_DIR, "**", f"*{ext}")
        paths.extend(glob(pattern, recursive=True))
    for p in sorted(set(paths)):
        try:
            with open(p, "r", encoding="utf-8", errors="ignore") as fh:
                yield p, fh.read()
        except Exception as e:
            print(f"WARN: unable to read {p}: {e}")

def main():
    # Ensure target directory exists so the uploader can find it
    os.makedirs(DB_DIR, exist_ok=True)

    texts, metas = [], []
    splitter = RecursiveCharacterTextSplitter(chunk_size=CHUNK_SIZE, chunk_overlap=CHUNK_OVERLAP)

    count_files = 0
    for path, content in iter_docs():
        count_files += 1
        chunks = splitter.split_text(content or "")
        for i, chunk in enumerate(chunks):
            texts.append(chunk)
            metas.append({"source": path, "chunk": i})

    if not texts:
        print(f"No KB content found under '{KB_DIR}' with extensions {SUPPORTED_EXTS}.")
        return

    print(f"Loaded {count_files} file(s) -> {len(texts)} chunk(s). Building embeddings with {MODEL_NAME}...")
    embeddings = HuggingFaceEmbeddings(model_name=MODEL_NAME)

    # Build and persist Chroma index
    db = Chroma.from_texts(texts=texts, embedding=embeddings, metadatas=metas, persist_directory=DB_DIR)
    db.persist()  # <-- force flush to disk

    # Sanity check: list what we wrote so the next step can see it
    print("Persisted files under .kb_index:")
    try:
        for root, dirs, files in os.walk(DB_DIR):
            for f in files:
                print(os.path.join(root, f))
    except Exception as e:
        print(f"WARN: listing {DB_DIR} failed: {e}")

    print(f"Indexed {len(texts)} chunks into '{DB_DIR}'.")

if __name__ == "__main__":
    main()
