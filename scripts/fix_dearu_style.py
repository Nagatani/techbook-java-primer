#!/usr/bin/env python3
"""
である調をですます調に修正するスクリプト
リスト項目内は除外（意図的にである調を使用）
"""

import os
import re
from pathlib import Path

def fix_dearu_in_file(filepath):
    """ファイル内のである調をですます調に修正"""
    with open(filepath, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    modified_lines = []
    changes_made = 0
    
    # 修正パターン
    replacements = [
        (r'である。', 'です。'),
        (r'であった。', 'でした。'),
        (r'ではない。', 'ではありません。'),
        (r'となる。', 'となります。'),
        (r'できる。', 'できます。'),
        (r'する。', 'します。'),
        (r'ない。', 'ありません。'),
        (r'ある。', 'あります。'),
    ]
    
    for i, line in enumerate(lines):
        # リスト項目の判定（- や * や数字. で始まる行）
        is_list_item = re.match(r'^[\s]*[-*]|\d+\.', line)
        
        # コードブロック内は除外
        if line.strip().startswith('```'):
            modified_lines.append(line)
            continue
        
        # リスト項目でない場合のみである調を修正
        if not is_list_item:
            modified_line = line
            for pattern, replacement in replacements:
                # 文末のパターンのみ置換（文中は除外）
                if re.search(pattern + r'$', modified_line.rstrip()):
                    modified_line = re.sub(pattern, replacement, modified_line)
                    changes_made += 1
                    break
            modified_lines.append(modified_line)
        else:
            modified_lines.append(line)
    
    # 変更があった場合のみファイルを更新
    if changes_made > 0:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.writelines(modified_lines)
        return changes_made
    return 0

def main():
    manuscripts_dir = Path('/Users/nagatani/github/techbook-java-primer/manuscripts')
    
    # 章ファイルのみを対象
    chapter_files = sorted(manuscripts_dir.glob('chapter*.md'))
    
    total_changes = 0
    modified_files = []
    
    for filepath in chapter_files:
        print(f"処理中: {filepath.name}")
        changes = fix_dearu_in_file(filepath)
        if changes > 0:
            modified_files.append((filepath.name, changes))
            total_changes += changes
    
    # 結果の表示
    print("\n=== 修正結果 ===")
    print(f"総修正数: {total_changes}箇所\n")
    
    if modified_files:
        print("修正されたファイル:")
        for filename, count in modified_files:
            print(f"  - {filename}: {count}箇所")
    else:
        print("修正箇所はありませんでした。")

if __name__ == '__main__':
    main()