#!/usr/bin/env python3
import os
import re
from collections import defaultdict
import json

# Define the prescriptive patterns to search for
patterns = {
    "心構え": r"心構え",
    "べき/べきだ/べきです": r"べき(?:だ|です)?(?![ル])",
    "なければなりません/なければならない": r"なければな(?:りません|らない)",
    "必須": r"必須",
    "重要です/重要な": r"重要(?:です|な|である)?",
    "覚えておきたい/覚えておく": r"覚えて(?:おきたい|おく|おいて)",
    "理解しておけば十分です": r"理解して(?:おけば十分|いれば十分)",
    "必要です/必要な/必要があります": r"必要(?:です|な|である|があ[りる](?:ます)?)?",
    "することが大切": r"することが大切",
    "しなければいけません": r"しなければいけ(?:ません|ない)",
    "ことが重要": r"ことが重要",
    "ことが必要": r"ことが必要",
    "注意が必要": r"注意が必要",
    "理解する必要": r"理解する必要",
    "把握する必要": r"把握する必要",
    "考える必要": r"考える必要",
    "検討する必要": r"検討する必要"
}

def analyze_chapter(file_path):
    """Analyze a single chapter for prescriptive patterns"""
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Get chapter title
    title_match = re.search(r'^#\s+(.+)$', content, re.MULTILINE)
    chapter_title = title_match.group(1) if title_match else os.path.basename(file_path)
    
    # Count patterns
    pattern_counts = {}
    examples = defaultdict(list)
    
    for pattern_name, pattern_regex in patterns.items():
        matches = list(re.finditer(pattern_regex, content))
        pattern_counts[pattern_name] = len(matches)
        
        # Collect up to 3 examples with context
        for i, match in enumerate(matches[:3]):
            start = max(0, match.start() - 50)
            end = min(len(content), match.end() + 50)
            context = content[start:end].replace('\n', ' ').strip()
            # Clean up the context
            if start > 0:
                context = "..." + context
            if end < len(content):
                context = context + "..."
            examples[pattern_name].append(context)
    
    # Calculate total prescriptive expressions
    total = sum(pattern_counts.values())
    
    # Get word count for density calculation
    word_count = len(re.findall(r'[ぁ-んァ-ヶー一-龠０-９a-zA-Z0-9]+', content))
    
    return {
        'title': chapter_title,
        'file': os.path.basename(file_path),
        'pattern_counts': pattern_counts,
        'total_prescriptive': total,
        'word_count': word_count,
        'prescriptive_density': (total / word_count * 1000) if word_count > 0 else 0,  # per 1000 words
        'examples': dict(examples)
    }

def main():
    # Get all chapter files
    manuscripts_dir = '/Users/nagatani/github/techbook-java-primer/manuscripts'
    chapter_files = sorted([f for f in os.listdir(manuscripts_dir) if f.startswith('chapter') and f.endswith('.md')])
    
    # Analyze each chapter
    results = []
    for chapter_file in chapter_files:
        file_path = os.path.join(manuscripts_dir, chapter_file)
        result = analyze_chapter(file_path)
        results.append(result)
    
    # Sort by total prescriptive count
    results.sort(key=lambda x: x['total_prescriptive'], reverse=True)
    
    # Print summary
    print("# Prescriptive Language Analysis Report\n")
    print("## Summary by Chapter (Sorted by Total Prescriptive Count)\n")
    
    print("| Chapter | Total | Density | Most Frequent Patterns |")
    print("|---------|-------|---------|------------------------|")
    
    for result in results:
        # Find top 3 patterns
        top_patterns = sorted(result['pattern_counts'].items(), key=lambda x: x[1], reverse=True)[:3]
        top_patterns_str = ", ".join([f"{p[0]} ({p[1]})" for p in top_patterns if p[1] > 0])
        
        print(f"| {result['file']} | {result['total_prescriptive']} | {result['prescriptive_density']:.1f} | {top_patterns_str} |")
    
    print("\n## Pattern Distribution Across All Chapters\n")
    
    # Aggregate pattern counts
    total_by_pattern = defaultdict(int)
    for result in results:
        for pattern, count in result['pattern_counts'].items():
            total_by_pattern[pattern] += count
    
    # Sort by total count
    sorted_patterns = sorted(total_by_pattern.items(), key=lambda x: x[1], reverse=True)
    
    print("| Pattern | Total Count | Chapters with Pattern |")
    print("|---------|-------------|----------------------|")
    
    for pattern, total_count in sorted_patterns:
        if total_count > 0:
            chapters_with_pattern = [r['file'] for r in results if r['pattern_counts'][pattern] > 0]
            chapter_count = len(chapters_with_pattern)
            print(f"| {pattern} | {total_count} | {chapter_count} chapters |")
    
    print("\n## Detailed Analysis by Chapter\n")
    
    # Print detailed analysis for top 5 chapters
    for i, result in enumerate(results[:5]):
        print(f"### {i+1}. {result['title']} ({result['file']})")
        print(f"- Total prescriptive expressions: {result['total_prescriptive']}")
        print(f"- Prescriptive density: {result['prescriptive_density']:.1f} per 1000 words")
        print(f"- Word count: {result['word_count']:,}")
        print("\n**Pattern breakdown:**")
        
        for pattern, count in sorted(result['pattern_counts'].items(), key=lambda x: x[1], reverse=True):
            if count > 0:
                print(f"- {pattern}: {count} occurrences")
                if pattern in result['examples']:
                    print("  Examples:")
                    for example in result['examples'][pattern]:
                        print(f"    - {example}")
        print()
    
    # Save detailed results to JSON for further analysis
    with open('prescriptive_language_data.json', 'w', encoding='utf-8') as f:
        json.dump(results, f, ensure_ascii=False, indent=2)
    
    print(f"\nDetailed data saved to prescriptive_language_data.json")

if __name__ == "__main__":
    main()