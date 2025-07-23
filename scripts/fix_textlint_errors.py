#!/usr/bin/env python3
"""
Fix textlint errors - sentence length and other issues.
"""

import os
import re

def fix_long_sentences(content: str, filename: str) -> tuple[str, int]:
    """Fix sentences that exceed 100 characters."""
    lines = content.split('\n')
    fixes = 0
    
    # Known patterns for fixing long sentences
    patterns = [
        # Appendix patterns - split at natural break points
        (r'(付録.+では、)(.+について.+します。)', r'\1\n\2'),
        (r'(リポジトリで.+提供しており、)(.+できます。)', r'\1\n\2'),
        (r'(について.+解説し、)(.+提供します。)', r'\1\n\2'),
        (r'(を通じて、)(.+学習できます。)', r'\1\n\2'),
        (r'(環境構築から.+まで、)(.+解説します。)', r'\1\n\2'),
        
        # Split at conjunction points
        (r'(、そして)(.+)', r'。\n\2'),
        (r'(、また)(.+)', r'。\n\2'),
        (r'(、さらに)(.+)', r'。\n\2'),
        
        # Split at natural break points in explanations
        (r'(について.{40,}し、)(.{40,})', r'\1\n\2'),
        (r'(.{40,}ため、)(.{40,})', r'\1\n\2'),
        (r'(.{40,}により、)(.{40,})', r'\1\n\2'),
    ]
    
    # Process each line
    for i, line in enumerate(lines):
        if len(line) > 100:
            original_line = line
            
            # Try each pattern
            for pattern, replacement in patterns:
                if re.search(pattern, line):
                    line = re.sub(pattern, replacement, line)
                    if line != original_line:
                        fixes += 1
                        break
            
            # If still too long, try more aggressive splitting
            if len(line) > 100 and '、' in line:
                # Count commas
                comma_count = line.count('、')
                if comma_count >= 3:
                    # Split at the middle comma
                    parts = line.split('、')
                    mid = len(parts) // 2
                    line = '、'.join(parts[:mid]) + '。\n' + '、'.join(parts[mid:])
                    fixes += 1
            
            lines[i] = line
    
    return '\n'.join(lines), fixes

def fix_comma_overuse(content: str) -> tuple[str, int]:
    """Fix sentences with too many commas (4 or more)."""
    lines = content.split('\n')
    fixes = 0
    
    for i, line in enumerate(lines):
        comma_count = line.count('、')
        if comma_count >= 4:
            # Split at strategic points
            # Find the position after the 2nd or 3rd comma
            comma_positions = [m.start() for m in re.finditer('、', line)]
            if len(comma_positions) >= 4:
                split_pos = comma_positions[2] if len(comma_positions) > 5 else comma_positions[1]
                lines[i] = line[:split_pos+1].rstrip('、') + '。\n' + line[split_pos+1:].lstrip()
                fixes += 1
    
    return '\n'.join(lines), fixes

def fix_redundant_expressions(content: str) -> tuple[str, int]:
    """Fix redundant expressions like 'する必要があります'."""
    fixes = 0
    
    redundant_patterns = [
        (r'する必要があります', r'します'),
        (r'しなければなりません', r'します'),
        (r'していただく必要があります', r'してください'),
        (r'考える必要があります', r'考えます'),
        (r'理解する必要があります', r'理解します'),
        (r'使用する必要があります', r'使用します'),
        (r'実装する必要があります', r'実装します'),
        (r'注意する必要があります', r'注意します'),
    ]
    
    for pattern, replacement in redundant_patterns:
        count = len(re.findall(pattern, content))
        content = re.sub(pattern, replacement, content)
        fixes += count
    
    return content, fixes

def main():
    # Files to fix based on the error report
    files_to_fix = [
        'appendix-a-environment-setup.md',
        'appendix-b-index-revised.md',
        'appendix-c-theoretical-foundations.md',
        'appendix-d-integrated-project.md',
        'appendix-e-database-programming.md',
        'appendix-g-build-and-deploy.md',
        'appendix-index.md',
        'chapter02-getting-started.md',
        'chapter03-oop-basics.md',
        'chapter04-classes-and-instances.md',
    ]
    
    manuscripts_dir = '/Users/nagatani/github/techbook-java-primer/manuscripts'
    total_fixes = 0
    
    for filename in files_to_fix:
        filepath = os.path.join(manuscripts_dir, filename)
        if not os.path.exists(filepath):
            continue
            
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        
        # Apply fixes
        content, sentence_fixes = fix_long_sentences(content, filename)
        content, comma_fixes = fix_comma_overuse(content)
        content, redundant_fixes = fix_redundant_expressions(content)
        
        file_fixes = sentence_fixes + comma_fixes + redundant_fixes
        
        if content != original_content:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Fixed {filename}: {file_fixes} issues")
            total_fixes += file_fixes
    
    print(f"\nTotal fixes: {total_fixes}")

if __name__ == "__main__":
    main()