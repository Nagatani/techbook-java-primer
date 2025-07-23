#!/usr/bin/env python3
"""
chapter12のコードリスト番号を修正するスクリプト
サンプルコード13-X → サンプルコード12-X に変更
"""

import re
from pathlib import Path

def fix_chapter12_listings(filepath):
    """chapter12のサンプルコード番号を修正"""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # サンプルコード13-X を サンプルコード12-X に置換
    pattern = r'(\*\*サンプルコード)13(-\d+\*\*)'
    replacement = r'\g<1>12\g<2>'
    
    modified_content = re.sub(pattern, replacement, content)
    
    # 変更があった場合のみファイルを更新
    if content != modified_content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(modified_content)
        
        # 変更数をカウント
        changes = len(re.findall(pattern, content))
        return changes
    
    return 0

def main():
    # chapter12ファイルを処理
    chapter12_file = Path('/Users/nagatani/github/techbook-java-primer/manuscripts/chapter12-lambda-and-functional-interfaces.md')
    
    if chapter12_file.exists():
        print(f"処理中: {chapter12_file.name}")
        changes = fix_chapter12_listings(chapter12_file)
        if changes > 0:
            print(f"  → {changes}個のサンプルコード番号を修正しました")
        else:
            print("  → 修正対象が見つかりませんでした")
    else:
        print(f"エラー: {chapter12_file} が見つかりません")

if __name__ == '__main__':
    main()