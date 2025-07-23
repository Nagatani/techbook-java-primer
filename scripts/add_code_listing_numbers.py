#!/usr/bin/env python3
"""
コードブロックに番号を付与するスクリプト
既存の番号付きコードは保持し、番号なしコードに連番を付与
"""

import os
import re
from pathlib import Path

def add_listing_numbers_to_file(filepath):
    """ファイル内のコードブロックに番号を付与"""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 章番号を取得
    filename = os.path.basename(filepath)
    chapter_match = re.match(r'chapter(\d+)', filename)
    if not chapter_match:
        return 0
    
    chapter_num = int(chapter_match.group(1))
    
    # 既存の番号付きコードブロックを検出して最大番号を取得
    existing_numbers = []
    pattern = r'\*\*(?:リスト|サンプルコード|コード)\s*' + str(chapter_num) + r'[-\.]\s*(\d+)\*\*'
    for match in re.finditer(pattern, content):
        existing_numbers.append(int(match.group(1)))
    
    max_number = max(existing_numbers) if existing_numbers else 0
    next_number = max_number + 1
    
    # コードブロックを処理
    lines = content.split('\n')
    modified_lines = []
    i = 0
    changes_made = 0
    
    while i < len(lines):
        line = lines[i]
        
        # コードブロックの開始を検出
        if line.strip() == '```java' or (line.strip().startswith('```') and len(line.strip()) > 3):
            # 直前の行を確認（既に番号があるか）
            has_number = False
            if i > 0:
                prev_line = lines[i-1]
                if re.search(r'\*\*(?:リスト|サンプルコード|コード)\s*\d+[-\.]\d+\*\*', prev_line):
                    has_number = True
            
            # 番号がない場合は追加
            if not has_number:
                # 適切な位置に番号を挿入
                # 空行や説明文の後に挿入
                insert_pos = len(modified_lines)
                
                # 説明文を探す（コードブロックの前の非空行）
                description = ""
                for j in range(i-1, max(0, i-10), -1):
                    if lines[j].strip() and not lines[j].startswith('#'):
                        # 最初の非空行を説明として使用
                        break
                
                modified_lines.append("")
                modified_lines.append(f'<span class="listing-number">**リスト{chapter_num}.{next_number}**</span>')
                modified_lines.append("")
                
                next_number += 1
                changes_made += 1
        
        modified_lines.append(line)
        i += 1
    
    # ファイルを更新
    if changes_made > 0:
        modified_content = '\n'.join(modified_lines)
        # 余分な空行を整理
        modified_content = re.sub(r'\n\n\n+', '\n\n', modified_content)
        
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(modified_content)
    
    return changes_made

def main():
    manuscripts_dir = Path('/Users/nagatani/github/techbook-java-primer/manuscripts')
    
    # 優先的に処理する章（番号なしコードが多い章）
    priority_chapters = ['chapter20', 'chapter06', 'chapter08']
    
    # 章ファイルを処理
    total_changes = 0
    
    # 優先章から処理
    for chapter_prefix in priority_chapters:
        for filepath in manuscripts_dir.glob(f'{chapter_prefix}*.md'):
            print(f"処理中: {filepath.name}")
            changes = add_listing_numbers_to_file(filepath)
            if changes > 0:
                print(f"  → {changes}個のコードブロックに番号を付与")
                total_changes += changes
    
    # その他の章を処理
    for filepath in sorted(manuscripts_dir.glob('chapter*.md')):
        if not any(filepath.name.startswith(prefix) for prefix in priority_chapters):
            print(f"処理中: {filepath.name}")
            changes = add_listing_numbers_to_file(filepath)
            if changes > 0:
                print(f"  → {changes}個のコードブロックに番号を付与")
                total_changes += changes
    
    print(f"\n総計: {total_changes}個のコードブロックに番号を付与しました。")

if __name__ == '__main__':
    main()