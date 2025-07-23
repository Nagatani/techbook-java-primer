#!/usr/bin/env python3
"""
Detailed analysis of code blocks with 'none' language type.
Categorizes them by content type.
"""

import os
import re
from collections import defaultdict

def find_none_blocks(content: str, filename: str):
    """Find all code blocks without language specification."""
    blocks = []
    lines = content.split('\n')
    i = 0
    
    while i < len(lines):
        if lines[i].strip() == '```':  # Code block without language
            start_line = i
            i += 1
            code_lines = []
            
            # Collect the code block content
            while i < len(lines) and not lines[i].strip().startswith('```'):
                code_lines.append(lines[i])
                i += 1
            
            if i < len(lines):  # Found closing fence
                code_content = '\n'.join(code_lines).strip()
                blocks.append({
                    'file': filename,
                    'line': start_line + 1,
                    'content': code_content,
                    'preview': (code_content[:80] + '...') if len(code_content) > 80 else code_content
                })
        i += 1
    
    return blocks

def categorize_block(content: str) -> str:
    """Categorize the content of a none block."""
    content_lower = content.lower()
    
    # Check for common patterns
    if re.search(r'error:|exception|エラー', content, re.IGNORECASE):
        return 'error_message'
    elif re.search(r'^\$|^>|^#\s', content, re.MULTILINE):
        return 'command_line'
    elif re.search(r'https?://', content):
        return 'url'
    elif re.search(r'^\d+\.\s|^-\s|^・', content, re.MULTILINE):
        return 'list_or_steps'
    elif re.search(r'[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}', content):
        return 'email'
    elif len(content.split('\n')) == 1 and len(content) < 100:
        return 'single_line'
    elif re.search(r'(出力|結果|Output)[:：]', content):
        return 'output_result'
    elif re.search(r'[┌─┐│└┘├┤┬┴┼]|[╔═╗║╚╝╠╣╦╩╬]', content):
        return 'ascii_art_or_diagram'
    else:
        return 'other'

def main():
    manuscripts_dir = '/Users/nagatani/github/techbook-java-primer/manuscripts'
    
    # Get all chapter files
    chapter_files = []
    for filename in os.listdir(manuscripts_dir):
        if filename.startswith('chapter') and filename.endswith('.md'):
            chapter_files.append(filename)
    
    chapter_files.sort()
    
    # Collect all none blocks
    all_none_blocks = []
    
    for filename in chapter_files:
        filepath = os.path.join(manuscripts_dir, filename)
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        none_blocks = find_none_blocks(content, filename)
        all_none_blocks.extend(none_blocks)
    
    # Categorize blocks
    categories = defaultdict(list)
    for block in all_none_blocks:
        category = categorize_block(block['content'])
        categories[category].append(block)
    
    # Print analysis
    print("=== ANALYSIS OF 'NONE' LANGUAGE BLOCKS ===\n")
    print(f"Total 'none' blocks found: {len(all_none_blocks)}\n")
    
    # Sort categories by count
    sorted_categories = sorted(categories.items(), key=lambda x: len(x[1]), reverse=True)
    
    for category, blocks in sorted_categories:
        print(f"\n{category.upper()} ({len(blocks)} blocks):")
        print("-" * 60)
        
        # Show first 3 examples
        for i, block in enumerate(blocks[:3]):
            print(f"  {block['file']}, line {block['line']}:")
            print(f"    {block['preview']}")
            if i < len(blocks[:3]) - 1:
                print()
        
        if len(blocks) > 3:
            print(f"  ... and {len(blocks) - 3} more")
    
    # Summary recommendations
    print("\n=== RECOMMENDATIONS ===\n")
    print("Based on the content analysis, these 'none' blocks should likely have:")
    print("- error_message: Keep as 'none' (these are error outputs)")
    print("- command_line: Consider using 'bash' or 'shell'")
    print("- output_result: Keep as 'none' (these are program outputs)")
    print("- single_line: Keep as 'none' (short snippets)")
    print("- list_or_steps: Consider using 'text' or keep as 'none'")
    print("- ascii_art_or_diagram: Keep as 'none'")
    print("- other: Review individually")
    
    # Check which ones should have サンプルコード numbering
    print("\n=== NUMBERING ANALYSIS ===\n")
    print("Blocks that might need サンプルコード numbering:")
    
    # Categories that typically should NOT have numbering
    no_numbering_categories = {'error_message', 'command_line', 'url', 'email', 
                              'single_line', 'output_result', 'ascii_art_or_diagram'}
    
    needs_review = []
    for category, blocks in categories.items():
        if category not in no_numbering_categories:
            needs_review.extend(blocks)
    
    if needs_review:
        print(f"\nFound {len(needs_review)} blocks that might need numbering:")
        for block in needs_review[:5]:
            print(f"  {block['file']}, line {block['line']}: {block['preview']}")
        if len(needs_review) > 5:
            print(f"  ... and {len(needs_review) - 5} more")
    else:
        print("\nAll 'none' blocks appear to be correctly unnumbered (error messages, outputs, etc.)")

if __name__ == "__main__":
    main()