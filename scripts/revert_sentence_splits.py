#!/usr/bin/env python3
"""
Revert inappropriate sentence splits caused by automated fixes.
This script identifies and fixes sentences that were incorrectly split.
"""

import os
import re

def revert_sentence_splits(content: str) -> tuple[str, int]:
    """
    Revert sentences that were inappropriately split.
    Look for patterns where a sentence was split with 。\n instead of 、
    """
    fixes = 0
    lines = content.split('\n')
    result = []
    i = 0
    
    while i < len(lines):
        current_line = lines[i]
        
        # Check if current line ends with 。 and next line exists
        if i + 1 < len(lines) and current_line.endswith('。'):
            next_line = lines[i + 1]
            
            # Pattern 1: Split at から。
            if current_line.endswith('から。') and next_line and not next_line.startswith('#'):
                # Merge lines with 、
                merged = current_line[:-1] + next_line
                result.append(merged)
                fixes += 1
                i += 2
                continue
                
            # Pattern 2: Split at について。
            if current_line.endswith('について。') and next_line and not next_line.startswith('#'):
                merged = current_line[:-1] + '、' + next_line
                result.append(merged)
                fixes += 1
                i += 2
                continue
                
            # Pattern 3: Split at し。
            if current_line.endswith('し。') and next_line and not next_line.startswith('#'):
                merged = current_line[:-1] + '、' + next_line
                result.append(merged)
                fixes += 1
                i += 2
                continue
                
            # Pattern 4: Split at により。
            if current_line.endswith('により。') and next_line and not next_line.startswith('#'):
                merged = current_line[:-1] + '、' + next_line
                result.append(merged)
                fixes += 1
                i += 2
                continue
                
            # Pattern 5: Split at 、
            if '、' in current_line and next_line and not next_line.startswith('#'):
                # Check if this looks like an inappropriate split
                if (len(current_line) < 60 and len(next_line) < 60 and 
                    not next_line.startswith('-') and not next_line.startswith(' ') and
                    not next_line.startswith('*')):
                    # This might be a split that should be merged
                    if current_line.endswith('。') and next_line[0].islower():
                        merged = current_line[:-1] + '、' + next_line
                        result.append(merged)
                        fixes += 1
                        i += 2
                        continue
        
        # Pattern 6: Obvious splits (short line ending with 。 followed by continuation)
        if (i + 1 < len(lines) and current_line.endswith('。') and 
            len(current_line) < 50 and len(lines[i + 1]) > 0 and
            not lines[i + 1].startswith('#') and not lines[i + 1].startswith(' ') and
            not lines[i + 1].startswith('-') and not lines[i + 1].startswith('*')):
            
            # Common patterns that indicate inappropriate splits
            split_patterns = [
                r'開発環境.*から。$',
                r'について.*解説します。$',
                r'提供しており。$',
                r'について詳しく解説します。$',
                r'基本原則。$',
                r'実践的.*から。$',
                r'環境構築.*から。$',
                r'例えば。$',
                r'Javaは。$',
                r'JVMは。$',
                r'これは。$',
                r'また。$',
                r'最後に.*管理。$',
                r'データ型.*double。$',
                r'制御構造.*for文。$',
                r'習得し。$',
                r'これは。$',
                r'依存せず。$',
            ]
            
            for pattern in split_patterns:
                if re.search(pattern, current_line):
                    next_line = lines[i + 1]
                    merged = current_line[:-1] + '、' + next_line
                    result.append(merged)
                    fixes += 1
                    i += 2
                    break
            else:
                result.append(current_line)
                i += 1
        else:
            result.append(current_line)
            i += 1
    
    return '\n'.join(result), fixes

def main():
    # Files that were modified
    files_to_fix = [
        'appendix-a-environment-setup.md',
        'appendix-b-index-revised.md',
        'appendix-c-theoretical-foundations.md',
        'appendix-d-integrated-project.md',
        'appendix-e-database-programming.md',
        'appendix-g-build-and-deploy.md',
        'appendix-index.md',
        'chapter01-introduction.md',
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
        content, fixes = revert_sentence_splits(content)
        
        if content != original_content:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Reverted {filename}: {fixes} splits")
            total_fixes += fixes
    
    print(f"\nTotal reverted splits: {total_fixes}")

if __name__ == "__main__":
    main()