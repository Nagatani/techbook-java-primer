#!/usr/bin/env python3
"""
コードリスト番号の重複を修正するスクリプト
サンプルコード形式をすべてリスト形式に統一し、連番を正しく付け直す
"""

import re
from pathlib import Path

def get_chapter_number(filename):
    """ファイル名から章番号を取得"""
    match = re.match(r'chapter(\d+)', filename)
    return int(match.group(1)) if match else None

def fix_code_listings(filepath):
    """コードリスト番号を修正"""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    chapter_num = get_chapter_number(filepath.name)
    if chapter_num is None:
        return 0
    
    # すべてのコードリスト番号を収集（サンプルコード、リスト、コード）
    pattern = r'<span class="listing-number">\*\*(?:サンプルコード|リスト|コード)\s*\d+[-\.]\s*\d+\*\*</span>'
    listings = list(re.finditer(pattern, content))
    
    if not listings:
        return 0
    
    # 行番号でソート
    listings.sort(key=lambda m: m.start())
    
    # 新しい番号を付与
    modified_content = content
    offset = 0
    
    for i, match in enumerate(listings, 1):
        old_text = match.group()
        new_text = f'<span class="listing-number">**リスト{chapter_num}.{i}**</span>'
        
        # 位置を調整して置換
        start = match.start() + offset
        end = match.end() + offset
        
        modified_content = modified_content[:start] + new_text + modified_content[end:]
        
        # オフセットを更新
        offset += len(new_text) - len(old_text)
    
    # ファイルを更新
    if content != modified_content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(modified_content)
        
        return len(listings)
    
    return 0

def main():
    manuscripts_dir = Path('/Users/nagatani/github/techbook-java-primer/manuscripts')
    
    # 重複が確認された章を優先的に処理
    priority_chapters = ['chapter02', 'chapter12']  # chapter12は既に修正済みだが確認のため
    
    total_changes = 0
    
    # 優先章から処理
    for chapter_prefix in priority_chapters:
        for filepath in manuscripts_dir.glob(f'{chapter_prefix}*.md'):
            print(f"処理中: {filepath.name}")
            changes = fix_code_listings(filepath)
            if changes > 0:
                print(f"  → {changes}個のコードリスト番号を統一・修正")
                total_changes += changes
    
    # その他の章も処理（すべてリスト形式に統一）
    for filepath in sorted(manuscripts_dir.glob('chapter*.md')):
        if not any(filepath.name.startswith(prefix) for prefix in priority_chapters):
            print(f"処理中: {filepath.name}")
            changes = fix_code_listings(filepath)
            if changes > 0:
                print(f"  → {changes}個のコードリスト番号を統一・修正")
                total_changes += changes
    
    print(f"\n総計: {total_changes}個のコードリスト番号を統一・修正しました。")

if __name__ == '__main__':
    main()