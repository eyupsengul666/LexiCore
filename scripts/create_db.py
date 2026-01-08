#!/usr/bin/env python3
"""
LexiCore Pre-populated Database Generator
Creates a Room-compatible SQLite database.

Usage:
    cd scripts/
    python3 create_db.py
"""
import sqlite3
import os

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.dirname(SCRIPT_DIR)

WORDS_FILE = os.path.join(SCRIPT_DIR, "words.txt")
DB_FILE = os.path.join(PROJECT_ROOT, "app/src/main/assets/database/lexicore.db")

def main():
    os.makedirs(os.path.dirname(DB_FILE), exist_ok=True)
    
    if os.path.exists(DB_FILE):
        os.remove(DB_FILE)
        print(f"Removed old database: {DB_FILE}")
    
    conn = sqlite3.connect(DB_FILE)
    cursor = conn.cursor()
    
    cursor.execute("PRAGMA page_size = 4096")
    cursor.execute("PRAGMA journal_mode = OFF")
    cursor.execute("PRAGMA synchronous = OFF")
    cursor.execute("PRAGMA user_version = 2")
    
    cursor.execute("CREATE TABLE IF NOT EXISTS android_metadata (locale TEXT)")
    cursor.execute("DELETE FROM android_metadata")
    cursor.execute("INSERT INTO android_metadata VALUES ('tr_TR')")
    
    cursor.execute("""
        CREATE TABLE words (
            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            word TEXT NOT NULL,
            length INTEGER NOT NULL
        )
    """)
    
    unique_words = set()
    data_to_insert = []
    
    with open(WORDS_FILE, 'r', encoding='utf-8') as f:
        for line in f:
            word = line.strip().lower()
            if word and word not in unique_words:
                unique_words.add(word)
                data_to_insert.append((word, len(word)))
    
    cursor.executemany("INSERT INTO words (word, length) VALUES (?, ?)", data_to_insert)
    conn.commit()
    
    cursor.execute("CREATE INDEX index_words_length_word ON words(length, word)")
    cursor.execute("CREATE UNIQUE INDEX index_words_word ON words(word)")
    
    cursor.execute("ANALYZE")
    cursor.execute("VACUUM")
    
    cursor.execute("SELECT COUNT(*) FROM words")
    count = cursor.fetchone()[0]
    
    cursor.execute("SELECT MIN(length), MAX(length) FROM words")
    min_len, max_len = cursor.fetchone()
    
    conn.close()
    
    db_size = os.path.getsize(DB_FILE) / 1024 / 1024
    
    print(f"Database created: {DB_FILE}")
    print(f"Word count: {count:,}")
    print(f"Length range: {min_len}-{max_len} characters")
    print(f"File size: {db_size:.2f} MB")
    print(f"Indexes: index_words_length_word, index_words_word")

if __name__ == "__main__":
    main()
