#!/usr/bin/env python3
"""
Check for content duplication across merged chapters.
"""

import os
import re
from collections import defaultdict
import hashlib

def extract_code_blocks(content):
    """Extract code blocks from markdown content."""
    code_blocks = []
    pattern = r'```(?:java)?\n(.*?)\n```'
    matches = re.findall(pattern, content, re.DOTALL)
    for match in matches:
        # Normalize whitespace and clean up
        cleaned = '\n'.join(line.strip() for line in match.strip().split('\n') if line.strip())
        if cleaned:
            code_blocks.append(cleaned)
    return code_blocks

def extract_headers(content):
    """Extract section headers from markdown content."""
    headers = []
    for line in content.split('\n'):
        if line.startswith('#'):
            headers.append(line.strip())
    return headers

def find_similar_content(files):
    """Find similar content across files."""
    code_blocks_by_file = {}
    headers_by_file = {}
    
    # Read all files
    for filepath in files:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
            code_blocks_by_file[filepath] = extract_code_blocks(content)
            headers_by_file[filepath] = extract_headers(content)
    
    # Find duplicate code blocks
    code_hash_to_files = defaultdict(list)
    for filepath, blocks in code_blocks_by_file.items():
        for block in blocks:
            # Create hash of normalized code
            block_hash = hashlib.md5(block.encode()).hexdigest()
            code_hash_to_files[block_hash].append((filepath, block[:100] + '...' if len(block) > 100 else block))
    
    # Find similar headers
    header_to_files = defaultdict(list)
    for filepath, headers in headers_by_file.items():
        for header in headers:
            # Normalize header (remove # and extra spaces)
            normalized = re.sub(r'^#+\s*', '', header).strip()
            if normalized:
                header_to_files[normalized].append(filepath)
    
    return code_hash_to_files, header_to_files

def main():
    # Target files to check
    target_files = [
        'chapter02-getting-started.md',
        'chapter03-oop-basics.md',
        'chapter04-classes-and-instances.md',
        'chapter16-multithreading.md',
        'chapter17-gui-basics.md',
        'chapter18-gui-event-handling.md'
    ]
    
    # Check if files exist
    existing_files = [f for f in target_files if os.path.exists(f)]
    
    print("=== Content Duplication Analysis ===")
    print(f"Checking {len(existing_files)} files...\n")
    
    code_duplicates, header_duplicates = find_similar_content(existing_files)
    
    # Report duplicate code blocks
    print("=== Duplicate Code Blocks ===")
    duplicate_count = 0
    for block_hash, occurrences in code_duplicates.items():
        if len(occurrences) > 1:
            duplicate_count += 1
            print(f"\nDuplicate #{duplicate_count}:")
            for filepath, preview in occurrences:
                print(f"  - {filepath}")
                print(f"    Preview: {preview[:60]}...")
    
    if duplicate_count == 0:
        print("No duplicate code blocks found.")
    
    # Report duplicate headers
    print("\n\n=== Duplicate Section Headers ===")
    dup_header_count = 0
    for header, files in header_duplicates.items():
        if len(files) > 1 and not header.startswith('第'):  # Ignore chapter titles
            dup_header_count += 1
            print(f'\n"{header}" appears in:')
            for filepath in files:
                print(f"  - {filepath}")
    
    if dup_header_count == 0:
        print("No duplicate section headers found.")
    
    # Check for specific patterns
    print("\n\n=== Pattern Analysis ===")
    patterns_to_check = [
        ('BankAccount class', r'class\s+BankAccount'),
        ('Person class', r'class\s+Person'),
        ('オブジェクト指向の説明', r'オブジェクト指向.*基本概念'),
        ('カプセル化の説明', r'カプセル化.*基本概念'),
        ('スレッドの作成', r'Thread.*作成'),
        ('イベント処理の説明', r'イベント処理.*基本'),
    ]
    
    for pattern_name, pattern in patterns_to_check:
        print(f"\n{pattern_name}:")
        found_in = []
        for filepath in existing_files:
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
                if re.search(pattern, content, re.IGNORECASE | re.DOTALL):
                    # Count occurrences
                    matches = re.findall(pattern, content, re.IGNORECASE | re.DOTALL)
                    found_in.append(f"{filepath} ({len(matches)} occurrences)")
        
        if found_in:
            for location in found_in:
                print(f"  - {location}")
        else:
            print("  Not found")

if __name__ == '__main__':
    main()