#!/usr/bin/env python3
import re
import os
from collections import defaultdict
from pathlib import Path

def analyze_bold_text(manuscript_dir):
    """Analyze bold text patterns in markdown files."""
    
    # Pattern to match bold text
    bold_pattern = r'\*\*([^*]+)\*\*'
    
    # Patterns to exclude
    exclude_patterns = [
        r'^リスト\d+-\d+$',
        r'^サンプルコード\d+-\d+$',
        r'^リスト[A-Z]+-\d+$',
        r'^サンプルコード[A-Z]+-\d+$'
    ]
    
    results = {
        'total_count': 0,
        'by_file': defaultdict(list),
        'by_category': defaultdict(list),
        'unique_texts': set()
    }
    
    # Iterate through all markdown files
    for md_file in Path(manuscript_dir).glob('*.md'):
        with open(md_file, 'r', encoding='utf-8') as f:
            content = f.read()
            
        # Find all bold text
        matches = re.findall(bold_pattern, content)
        
        for match in matches:
            # Skip if it matches exclude patterns
            if any(re.match(pattern, match) for pattern in exclude_patterns):
                continue
                
            results['total_count'] += 1
            results['by_file'][md_file.name].append(match)
            results['unique_texts'].add(match)
            
            # Categorize by pattern
            if match.endswith('：'):
                results['by_category']['Bold followed by colon'].append(match)
            elif '原因と対処' in match or 'エラーメッセージ' in match:
                results['by_category']['Error handling'].append(match)
            elif '目的' in match or '実装のポイント' in match:
                results['by_category']['Exercise/Implementation guide'].append(match)
            elif len(match) <= 10:
                results['by_category']['Technical terms (short)'].append(match)
            elif '（' in match and '）' in match:
                results['by_category']['Terms with parentheses'].append(match)
            else:
                results['by_category']['General emphasis'].append(match)
    
    return results

def print_analysis(results):
    """Print analysis results."""
    print(f"=== Bold Text Analysis Report ===\n")
    print(f"Total bold text instances: {results['total_count']}")
    print(f"Unique bold texts: {len(results['unique_texts'])}\n")
    
    print("=== Usage by Category ===")
    for category, items in sorted(results['by_category'].items(), key=lambda x: len(x[1]), reverse=True):
        print(f"\n{category}: {len(items)} instances")
        # Show top 5 examples
        unique_items = list(set(items))[:5]
        for item in unique_items:
            count = items.count(item)
            print(f"  - {item} ({count}x)")
    
    print("\n=== Files with Most Bold Text ===")
    sorted_files = sorted(results['by_file'].items(), key=lambda x: len(x[1]), reverse=True)[:10]
    for filename, items in sorted_files:
        print(f"\n{filename}: {len(items)} instances")
        # Count frequency of each text
        text_counts = defaultdict(int)
        for item in items:
            text_counts[item] += 1
        # Show top 3 most frequent
        top_items = sorted(text_counts.items(), key=lambda x: x[1], reverse=True)[:3]
        for text, count in top_items:
            print(f"  - {text} ({count}x)")

if __name__ == "__main__":
    manuscript_dir = "/Users/nagatani/github/techbook-java-primer/manuscripts"
    results = analyze_bold_text(manuscript_dir)
    print_analysis(results)