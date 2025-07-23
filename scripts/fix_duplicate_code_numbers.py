#!/usr/bin/env python3
"""
重複したコード番号を修正するスクリプト
連続する2つのlisting-numberタグのうち、最初のものを削除する
"""

import re
from pathlib import Path

def fix_duplicate_numbers(filepath):
    """重複したコード番号を修正"""
    with open(filepath, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    modified_lines = []
    i = 0
    duplicates_fixed = 0
    
    while i < len(lines):
        # 現在の行がlisting-numberタグかチェック
        if i + 2 < len(lines) and '<span class="listing-number">' in lines[i]:
            # 次の行が空行で、その次もlisting-numberタグの場合
            if lines[i+1].strip() == '' and '<span class="listing-number">' in lines[i+2]:
                # 最初のタグと空行をスキップ（削除）
                duplicates_fixed += 1
                i += 2  # 2行スキップして、2番目のタグから継続
                continue
        
        modified_lines.append(lines[i])
        i += 1
    
    if duplicates_fixed > 0:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.writelines(modified_lines)
    
    return duplicates_fixed

def main():
    manuscripts_dir = Path('/Users/nagatani/github/techbook-java-primer/manuscripts')
    
    total_fixed = 0
    
    # すべての章ファイルを処理
    for filepath in sorted(manuscripts_dir.glob('chapter*.md')):
        print(f"処理中: {filepath.name}")
        fixed = fix_duplicate_numbers(filepath)
        if fixed > 0:
            print(f"  → {fixed}個の重複番号を修正")
            total_fixed += fixed
    
    print(f"\n総計: {total_fixed}個の重複番号を修正しました。")

if __name__ == '__main__':
    main()