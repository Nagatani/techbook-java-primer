#!/usr/bin/env python3
"""
Analyze code blocks in manuscript files to identify which have サンプルコード numbering.
Reports statistics on numbered vs unnumbered blocks by language type.
"""

import os
import re
from collections import defaultdict
from typing import Dict, List, Tuple
import json

def find_code_blocks(content: str) -> List[Tuple[int, str, str]]:
    """
    Find all code blocks in the content.
    Returns list of (line_number, language, code_content) tuples.
    """
    blocks = []
    lines = content.split('\n')
    i = 0
    
    while i < len(lines):
        if lines[i].strip().startswith('```'):
            start_line = i
            # Extract language from the opening fence
            match = re.match(r'^```(\w+)?', lines[i].strip())
            language = match.group(1) if match and match.group(1) else 'none'
            
            # Find the closing fence
            i += 1
            code_lines = []
            while i < len(lines) and not lines[i].strip().startswith('```'):
                code_lines.append(lines[i])
                i += 1
            
            if i < len(lines):  # Found closing fence
                blocks.append((start_line, language, '\n'.join(code_lines)))
        i += 1
    
    return blocks

def check_sample_code_before(lines: List[str], block_line: int) -> Tuple[bool, str]:
    """
    Check if there's a サンプルコード reference before the code block.
    Returns (has_number, number_text).
    """
    # Look backwards up to 5 lines for a サンプルコード reference
    for i in range(max(0, block_line - 5), block_line):
        line = lines[i]
        # Look for pattern like サンプルコードX-Y
        match = re.search(r'サンプルコード(\d+[-−]\d+)', line)
        if match:
            return True, match.group(0)
    
    return False, ""

def analyze_manuscript_file(filepath: str) -> Dict:
    """Analyze a single manuscript file."""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    lines = content.split('\n')
    code_blocks = find_code_blocks(content)
    
    results = {
        'total_blocks': len(code_blocks),
        'by_language': defaultdict(lambda: {'total': 0, 'numbered': 0, 'unnumbered': 0, 'examples': []})
    }
    
    for line_num, language, code_content in code_blocks:
        has_number, number_text = check_sample_code_before(lines, line_num)
        
        results['by_language'][language]['total'] += 1
        if has_number:
            results['by_language'][language]['numbered'] += 1
        else:
            results['by_language'][language]['unnumbered'] += 1
            # Store first few lines of unnumbered blocks as examples
            if len(results['by_language'][language]['examples']) < 3:
                preview = code_content.strip().split('\n')[0][:50] + '...' if code_content.strip() else '[empty]'
                results['by_language'][language]['examples'].append({
                    'line': line_num + 1,
                    'preview': preview
                })
    
    return results

def main():
    manuscripts_dir = '/Users/nagatani/github/techbook-java-primer/manuscripts'
    
    # Get all chapter files
    chapter_files = []
    for filename in os.listdir(manuscripts_dir):
        if filename.startswith('chapter') and filename.endswith('.md'):
            chapter_files.append(filename)
    
    chapter_files.sort()
    
    # Overall statistics
    overall_stats = defaultdict(lambda: {'total': 0, 'numbered': 0, 'unnumbered': 0})
    chapter_details = {}
    
    print("Analyzing code blocks in manuscript files...\n")
    
    for filename in chapter_files:
        filepath = os.path.join(manuscripts_dir, filename)
        results = analyze_manuscript_file(filepath)
        chapter_details[filename] = results
        
        # Aggregate statistics
        for language, stats in results['by_language'].items():
            overall_stats[language]['total'] += stats['total']
            overall_stats[language]['numbered'] += stats['numbered']
            overall_stats[language]['unnumbered'] += stats['unnumbered']
    
    # Sort languages by total count
    sorted_languages = sorted(overall_stats.items(), key=lambda x: x[1]['total'], reverse=True)
    
    # Print overall statistics
    print("=== OVERALL STATISTICS ===\n")
    print(f"{'Language':<15} {'Total':<8} {'Numbered':<10} {'Unnumbered':<12} {'% Numbered'}")
    print("-" * 60)
    
    total_all = 0
    numbered_all = 0
    unnumbered_all = 0
    
    for language, stats in sorted_languages:
        total = stats['total']
        numbered = stats['numbered']
        unnumbered = stats['unnumbered']
        percentage = (numbered / total * 100) if total > 0 else 0
        
        total_all += total
        numbered_all += numbered
        unnumbered_all += unnumbered
        
        print(f"{language:<15} {total:<8} {numbered:<10} {unnumbered:<12} {percentage:>6.1f}%")
    
    print("-" * 60)
    percentage_all = (numbered_all / total_all * 100) if total_all > 0 else 0
    print(f"{'TOTAL':<15} {total_all:<8} {numbered_all:<10} {unnumbered_all:<12} {percentage_all:>6.1f}%")
    
    # Print languages that need attention (less than 100% numbered)
    print("\n=== LANGUAGES NEEDING NUMBERING ===\n")
    needs_attention = [(lang, stats) for lang, stats in sorted_languages 
                      if stats['unnumbered'] > 0]
    
    if needs_attention:
        for language, stats in needs_attention:
            unnumbered = stats['unnumbered']
            print(f"\n{language}: {unnumbered} unnumbered blocks")
            
            # Show examples from chapters
            print("  Examples:")
            example_count = 0
            for chapter, details in chapter_details.items():
                if language in details['by_language'] and details['by_language'][language]['examples']:
                    for example in details['by_language'][language]['examples']:
                        print(f"    - {chapter}, line {example['line']}: {example['preview']}")
                        example_count += 1
                        if example_count >= 3:
                            break
                if example_count >= 3:
                    break
    else:
        print("All code blocks are properly numbered!")
    
    # Save detailed results to JSON
    output_file = '/Users/nagatani/github/techbook-java-primer/scripts/code_block_analysis.json'
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump({
            'overall_stats': dict(overall_stats),
            'chapter_details': chapter_details
        }, f, ensure_ascii=False, indent=2)
    
    print(f"\nDetailed results saved to: {output_file}")

if __name__ == "__main__":
    main()