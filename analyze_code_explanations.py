#!/usr/bin/env python3
"""
Analyze Java code blocks in markdown files to identify those lacking proper explanations.
"""

import re
import os
from pathlib import Path

def analyze_markdown_file(filepath):
    """Analyze a single markdown file for Java code blocks and their explanations."""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    lines = content.split('\n')
    issues = []
    
    # Find all Java code blocks
    i = 0
    while i < len(lines):
        if lines[i].strip() == '```java':
            # Found a Java code block
            code_start = i
            
            # Check preceding lines for explanation
            has_preceding_explanation = False
            for j in range(max(0, i-5), i):
                line = lines[j].strip()
                # Check if there's meaningful text (not just formatting or empty)
                if (line and 
                    not line.startswith('#') and 
                    not line.startswith('<span') and 
                    not line.startswith('```') and
                    not re.match(r'^[\d\-\*]+$', line) and
                    len(line) > 10):
                    has_preceding_explanation = True
                    break
            
            # Find the end of code block
            code_end = i + 1
            while code_end < len(lines) and lines[code_end].strip() != '```':
                code_end += 1
            
            # Extract first few lines of code for context
            code_preview = []
            for j in range(code_start + 1, min(code_start + 4, code_end)):
                if lines[j].strip():
                    code_preview.append(lines[j])
            
            # Check following lines for explanation
            has_following_explanation = False
            for j in range(code_end + 1, min(code_end + 6, len(lines))):
                line = lines[j].strip()
                if (line and 
                    not line.startswith('#') and 
                    not line.startswith('<span') and 
                    not line.startswith('```') and
                    not re.match(r'^[\d\-\*]+$', line) and
                    len(line) > 10):
                    has_following_explanation = True
                    break
            
            # Record issues
            if not has_preceding_explanation or not has_following_explanation:
                issue = {
                    'line': code_start + 1,  # 1-based line number
                    'code_preview': code_preview[:2],
                    'has_preceding': has_preceding_explanation,
                    'has_following': has_following_explanation,
                    'preceding_context': lines[max(0, code_start-3):code_start],
                    'following_context': lines[code_end+1:min(code_end+4, len(lines))]
                }
                issues.append(issue)
            
            i = code_end + 1
        else:
            i += 1
    
    return issues

def main():
    manuscripts_dir = Path('/Users/nagatani/github/techbook-java-primer/manuscripts')
    
    # Process all chapter files
    for chapter_file in sorted(manuscripts_dir.glob('chapter*.md')):
        issues = analyze_markdown_file(chapter_file)
        
        if issues:
            print(f"\n{'='*80}")
            print(f"File: {chapter_file.name}")
            print(f"{'='*80}")
            
            for issue in issues:
                print(f"\n** Line {issue['line']}: Java code block with insufficient explanation")
                
                if not issue['has_preceding']:
                    print("   - Missing or insufficient preceding explanation")
                if not issue['has_following']:
                    print("   - Missing or insufficient following explanation")
                
                print("\n   Code preview:")
                for line in issue['code_preview']:
                    print(f"   {line}")
                
                print("\n   Preceding context:")
                for line in issue['preceding_context']:
                    print(f"   {line}")
                
                print("\n   Following context:")
                for line in issue['following_context']:
                    print(f"   {line}")

if __name__ == '__main__':
    main()