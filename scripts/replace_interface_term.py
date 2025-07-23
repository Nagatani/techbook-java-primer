#!/usr/bin/env python3
"""
「インターフェース」を「インターフェイス」に置換するスクリプト
コードブロック内は対象外とする
"""

import os
import re
from pathlib import Path

def replace_interface_in_file(filepath):
    """ファイル内のインターフェースをインターフェイスに置換"""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # コードブロックを一時的に保護
    code_blocks = []
    code_pattern = r'```[\s\S]*?```'
    
    def store_code_block(match):
        code_blocks.append(match.group(0))
        return f'<<<CODE_BLOCK_{len(code_blocks)-1}>>>'
    
    # コードブロックを置換
    protected_content = re.sub(code_pattern, store_code_block, content)
    
    # インターフェース → インターフェイス
    modified_content = protected_content.replace('インターフェース', 'インターフェイス')
    
    # コードブロックを復元
    for i, block in enumerate(code_blocks):
        modified_content = modified_content.replace(f'<<<CODE_BLOCK_{i}>>>', block)
    
    # 変更があった場合のみファイルを更新
    if content != modified_content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(modified_content)
        return True
    return False

def main():
    manuscripts_dir = Path('/Users/nagatani/github/techbook-java-primer/manuscripts')
    
    # 対象ファイル
    target_files = list(manuscripts_dir.glob('*.md'))
    
    modified_files = []
    
    for filepath in sorted(target_files):
        print(f"処理中: {filepath.name}")
        if replace_interface_in_file(filepath):
            modified_files.append(filepath.name)
    
    print("\n=== 結果 ===")
    print(f"処理ファイル数: {len(target_files)}")
    print(f"修正ファイル数: {len(modified_files)}")
    
    if modified_files:
        print("\n修正されたファイル:")
        for filename in modified_files:
            print(f"  - {filename}")

if __name__ == '__main__':
    main()