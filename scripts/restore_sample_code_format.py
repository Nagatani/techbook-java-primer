#!/usr/bin/env python3
"""
コードリスト番号を「リスト」から「サンプルコード」形式に戻すスクリプト
リストX.Y → サンプルコードX-Y
"""

import re
from pathlib import Path

def restore_sample_code_format(filepath):
    """リスト形式をサンプルコード形式に戻す"""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # リストX.Y をサンプルコードX-Y に変換
    pattern = r'(\*\*リスト)(\d+)\.(\d+)(\*\*)'
    replacement = r'**サンプルコード\2-\3**'
    
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
    manuscripts_dir = Path('/Users/nagatani/github/techbook-java-primer/manuscripts')
    
    total_changes = 0
    
    # すべての章ファイルを処理
    for filepath in sorted(manuscripts_dir.glob('chapter*.md')):
        print(f"処理中: {filepath.name}")
        changes = restore_sample_code_format(filepath)
        if changes > 0:
            print(f"  → {changes}個のリスト番号をサンプルコード形式に復元")
            total_changes += changes
    
    print(f"\n総計: {total_changes}個のリスト番号をサンプルコード形式に復元しました。")

if __name__ == '__main__':
    main()