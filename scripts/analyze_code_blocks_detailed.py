#!/usr/bin/env python3
"""
Detailed analysis of code blocks to understand numbering patterns.
"""

import os
import re
from collections import defaultdict

def extract_code_blocks_with_context(content: str, filename: str):
    """Extract code blocks with surrounding context."""
    blocks = []
    lines = content.split('\n')
    
    # Pattern to match code blocks
    code_block_pattern = re.compile(r'^```(\w*)')
    sample_code_pattern = re.compile(r'サンプルコード(\d+[-−]\d+)')
    
    i = 0
    while i < len(lines):
        match = code_block_pattern.match(lines[i].strip())
        if match:
            language = match.group(1) or 'none'
            start_line = i
            
            # Check for サンプルコード in previous 5 lines
            has_number = False
            number_text = ""
            for j in range(max(0, i - 5), i):
                sample_match = sample_code_pattern.search(lines[j])
                if sample_match:
                    has_number = True
                    number_text = sample_match.group(0)
                    break
            
            # Find the closing fence
            i += 1
            code_lines = []
            while i < len(lines) and not lines[i].strip().startswith('```'):
                code_lines.append(lines[i])
                i += 1
            
            if i < len(lines):  # Found closing fence
                code_content = '\n'.join(code_lines).strip()
                
                # Categorize the content
                category = categorize_content(code_content, language)
                
                blocks.append({
                    'file': filename,
                    'line': start_line + 1,
                    'language': language,
                    'has_number': has_number,
                    'number': number_text,
                    'content_preview': (code_content[:100] + '...') if len(code_content) > 100 else code_content,
                    'category': category,
                    'lines_count': len(code_lines)
                })
        i += 1
    
    return blocks

def categorize_content(content: str, language: str) -> str:
    """Categorize the content based on language and content."""
    if language != 'none':
        return f"code_{language}"
    
    # For 'none' blocks, analyze content
    content_lower = content.lower()
    
    if not content.strip():
        return 'empty'
    elif re.search(r'error:|exception|エラー|例外', content, re.IGNORECASE):
        return 'error_output'
    elif re.search(r'^\$|^>|^#\s', content, re.MULTILINE):
        return 'terminal_output'
    elif re.search(r'^[┌─┐│└┘├┤┬┴┼╔═╗║╚╝╠╣╦╩╬]', content, re.MULTILINE):
        return 'diagram'
    elif re.search(r'(出力|結果|Output|実行結果)[:：]', content):
        return 'program_output'
    elif len(content.split('\n')) == 1 and len(content) < 50:
        return 'single_line_text'
    else:
        return 'text_content'

def analyze_numbering_patterns():
    """Analyze which types of blocks have numbering."""
    manuscripts_dir = '/Users/nagatani/github/techbook-java-primer/manuscripts'
    
    # Get all chapter files
    chapter_files = []
    for filename in os.listdir(manuscripts_dir):
        if filename.startswith('chapter') and filename.endswith('.md'):
            chapter_files.append(filename)
    
    chapter_files.sort()
    
    # Collect all blocks
    all_blocks = []
    for filename in chapter_files:
        filepath = os.path.join(manuscripts_dir, filename)
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        blocks = extract_code_blocks_with_context(content, filename)
        all_blocks.extend(blocks)
    
    # Analyze by category
    by_category = defaultdict(lambda: {'total': 0, 'numbered': 0, 'unnumbered': 0, 'examples': []})
    
    for block in all_blocks:
        category = block['category']
        by_category[category]['total'] += 1
        
        if block['has_number']:
            by_category[category]['numbered'] += 1
        else:
            by_category[category]['unnumbered'] += 1
            if len(by_category[category]['examples']) < 3:
                by_category[category]['examples'].append(block)
    
    # Print analysis
    print("=== CODE BLOCK NUMBERING ANALYSIS BY CONTENT TYPE ===\n")
    
    # Sort by total count
    sorted_categories = sorted(by_category.items(), key=lambda x: x[1]['total'], reverse=True)
    
    print(f"{'Category':<20} {'Total':<8} {'Numbered':<10} {'Unnumbered':<12} {'% Numbered'}")
    print("-" * 65)
    
    for category, stats in sorted_categories:
        total = stats['total']
        numbered = stats['numbered']
        unnumbered = stats['unnumbered']
        percentage = (numbered / total * 100) if total > 0 else 0
        
        print(f"{category:<20} {total:<8} {numbered:<10} {unnumbered:<12} {percentage:>6.1f}%")
    
    # Show categories with unnumbered blocks
    print("\n=== CATEGORIES WITH UNNUMBERED BLOCKS ===\n")
    
    for category, stats in sorted_categories:
        if stats['unnumbered'] > 0:
            print(f"\n{category} ({stats['unnumbered']} unnumbered):")
            print("-" * 50)
            
            for example in stats['examples']:
                print(f"  File: {example['file']}, Line: {example['line']}")
                print(f"  Language: {example['language']}")
                print(f"  Content: {example['content_preview']}")
                print()
    
    # Specific recommendations
    print("\n=== RECOMMENDATIONS ===\n")
    
    code_categories = [cat for cat, _ in sorted_categories if cat.startswith('code_')]
    non_code_categories = [cat for cat, _ in sorted_categories if not cat.startswith('code_')]
    
    print("Code blocks (with language specified):")
    for cat in code_categories:
        stats = by_category[cat]
        if stats['unnumbered'] == 0:
            print(f"  ✓ {cat}: All {stats['total']} blocks are numbered")
        else:
            print(f"  ✗ {cat}: {stats['unnumbered']} blocks need numbering")
    
    print("\nNon-code blocks (language='none'):")
    for cat in non_code_categories:
        stats = by_category[cat]
        if cat in ['error_output', 'terminal_output', 'program_output', 'diagram', 'single_line_text']:
            print(f"  ✓ {cat}: Correctly unnumbered ({stats['total']} blocks)")
        elif stats['unnumbered'] > 0:
            print(f"  ? {cat}: Review needed - {stats['unnumbered']} unnumbered blocks")

if __name__ == "__main__":
    analyze_numbering_patterns()