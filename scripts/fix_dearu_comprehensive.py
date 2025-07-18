#!/usr/bin/env python3
"""
リスト項目の文体を「である調」に統一する包括的なスクリプト
"""

import re
import os
import sys

def fix_list_dearu(content):
    """リスト項目を「である調」に修正"""
    lines = content.split('\n')
    modified_lines = []
    
    # More comprehensive patterns to fix
    replacements = [
        # Basic です endings
        (r'です。$', 'である。'),
        (r'です$', 'である'),
        
        # Basic masu form endings
        (r'ます。$', 'る。'),
        (r'ます$', 'る'),
        (r'います。$', 'いる。'),
        (r'います$', 'いる'),
        (r'えます。$', 'える。'),
        (r'えます$', 'える'),
        (r'します。$', 'する。'),
        (r'します$', 'する'),
        (r'なります。$', 'なる。'),
        (r'なります$', 'なる'),
        (r'きます。$', 'くる。'),
        (r'きます$', 'くる'),
        (r'できます。$', 'できる。'),
        (r'できます$', 'できる'),
        (r'あります。$', 'ある。'),
        (r'あります$', 'ある'),
        (r'れます。$', 'れる。'),
        (r'れます$', 'れる'),
        (r'られます。$', 'られる。'),
        (r'られます$', 'られる'),
        
        # Special patterns
        (r'みます。$', 'みる。'),
        (r'みます$', 'みる'),
        (r'たいです。$', 'たい。'),
        (r'たいです$', 'たい'),
        (r'います(?=[が、])', 'いる'),
        (r'れています(?=[が、])', 'れている'),
        (r'ています(?=[が、])', 'ている'),
        
        # Common expressions
        (r'学びます。$', '学ぶ。'),
        (r'学びます$', '学ぶ'),
        (r'覚えます。$', '覚える。'),
        (r'覚えます$', '覚える'),
        (r'理解します。$', '理解する。'),
        (r'理解します$', '理解する'),
        (r'実装します。$', '実装する。'),
        (r'実装します$', '実装する'),
        (r'説明します。$', '説明する。'),
        (r'説明します$', '説明する'),
        (r'使います。$', '使う。'),
        (r'使います$', '使う'),
        (r'書きます。$', '書く。'),
        (r'書きます$', '書く'),
        (r'行います。$', '行う。'),
        (r'行います$', '行う'),
        (r'作ります。$', '作る。'),
        (r'作ります$', '作る'),
        (r'使用します。$', '使用する。'),
        (r'使用します$', '使用する'),
        (r'活用します。$', '活用する。'),
        (r'活用します$', '活用する'),
        (r'表示します。$', '表示する。'),
        (r'表示します$', '表示する'),
        (r'記述します。$', '記述する。'),
        (r'記述します$', '記述する'),
        (r'利用します。$', '利用する。'),
        (r'利用します$', '利用する'),
        (r'習得します。$', '習得する。'),
        (r'習得します$', '習得する'),
        (r'実行します。$', '実行する。'),
        (r'実行します$', '実行する'),
        (r'処理します。$', '処理する。'),
        (r'処理します$', '処理する'),
        (r'確認します。$', '確認する。'),
        (r'確認します$', '確認する'),
        (r'開発します。$', '開発する。'),
        (r'開発します$', '開発する'),
        (r'作成します。$', '作成する。'),
        (r'作成します$', '作成する'),
        (r'把握します。$', '把握する。'),
        (r'把握します$', '把握する'),
        (r'身につけます。$', '身につける。'),
        (r'身につけます$', '身につける'),
        (r'目指します。$', '目指す。'),
        (r'目指します$', '目指す'),
        (r'持っています。$', '持っている。'),
        (r'持っています$', '持っている'),
        (r'向上します。$', '向上する。'),
        (r'向上します$', '向上する'),
        (r'進みます。$', '進む。'),
        (r'進みます$', '進む'),
        (r'なっています。$', 'なっている。'),
        (r'なっています$', 'なっている'),
        (r'されています。$', 'されている。'),
        (r'されています$', 'されている'),
        (r'なりました。$', 'なった。'),
        (r'なりました$', 'なった'),
        
        # Parenthetical endings
        (r'ます）', 'る）'),
        (r'います）', 'いる）'),
        (r'えます）', 'える）'),
        (r'します）', 'する）'),
        (r'なります）', 'なる）'),
        (r'できます）', 'できる）'),
        (r'あります）', 'ある）'),
        (r'れます）', 'れる）'),
        (r'られます）', 'られる）'),
    ]
    
    for i, line in enumerate(lines):
        # Check if line is a list item
        # Match bullet lists (- ) and numbered lists (1. )
        list_match = re.match(r'^(\s*[-*]\s+|\s*\d+\.\s+)', line)
        
        if list_match:
            # Skip if line is in a code block
            if '```' in line or '**サンプルコード' in line or '**リスト' in line:
                modified_lines.append(line)
                continue
            
            # Skip if line ends with colon (list introduction)
            if line.rstrip().endswith(':') or line.rstrip().endswith('：'):
                modified_lines.append(line)
                continue
            
            # Apply replacements
            modified_line = line
            
            # First check for special patterns like "できます。これは" -> "できる。これは"
            modified_line = re.sub(r'できます。', 'できる。', modified_line)
            modified_line = re.sub(r'います。', 'いる。', modified_line)
            modified_line = re.sub(r'ます。', 'る。', modified_line)
            modified_line = re.sub(r'です。', 'である。', modified_line)
            
            # Then apply other replacements
            for pattern, replacement in replacements:
                modified_line = re.sub(pattern, replacement, modified_line)
            
            modified_lines.append(modified_line)
        else:
            modified_lines.append(line)
    
    return '\n'.join(modified_lines)

def process_file(filepath):
    """Process a single markdown file"""
    print(f"Processing {filepath}...")
    
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    modified_content = fix_list_dearu(content)
    
    if content != modified_content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(modified_content)
        print(f"  ✓ Modified {filepath}")
        return True
    else:
        print(f"  - No changes needed in {filepath}")
        return False

def main():
    """Main function"""
    manuscripts_dir = "/Users/nagatani/github/techbook-java-primer/manuscripts"
    
    # Files with remaining errors
    files_to_process = [
        "chapter02-getting-started.md",
        "chapter03-oop-basics.md",
        "chapter08-enums.md",
        "chapter24-build-and-deploy.md",
        "chapter15-file-io.md",
        "chapter14-exception-handling.md",
        "chapter13-advanced-collections.md",
        "chapter23-documentation-and-libraries.md",
        "chapter18-gui-basics.md",
        "chapter11-generics.md",
        "chapter07-abstract-classes-and-interfaces.md",
        "appendix-a-environment-setup.md"
    ]
    
    total_modified = 0
    
    for filename in files_to_process:
        filepath = os.path.join(manuscripts_dir, filename)
        if os.path.exists(filepath):
            if process_file(filepath):
                total_modified += 1
        else:
            print(f"  ✗ File not found: {filepath}")
    
    print(f"\nTotal files modified: {total_modified}")

if __name__ == "__main__":
    main()