#!/usr/bin/env python3
"""
第4章用の自然な日本語表現への自動校正ツール
"""
import re
import sys
from pathlib import Path

# 自然な日本語表現への変換パターン
NATURAL_JAPANESE_PATTERNS = [
    # 冗長表現の簡潔化
    (r'することができます', 'できます'),
    (r'することが可能です', '可能です'),
    (r'されることになります', 'されます'),
    (r'することになります', 'します'),
    (r'することで', 'すると'),  # 文脈による
    (r'することによって', 'すると'),  # 文脈による
    (r'することが必要です', '必要です'),
    (r'する必要があります', '必要です'),
    (r'である必要があります', 'でなければなりません'),
    (r'となることがあります', 'となる場合があります'),
    (r'されている場合があります', 'される場合があります'),
    (r'実装されています', '実装されます'),
    (r'使用されています', '使用されます'),
    (r'定義されています', '定義されます'),
    (r'提供されています', '提供されます'),
    (r'設定されています', '設定されます'),
    (r'管理されています', '管理されます'),
    (r'保存されています', '保存されます'),
    (r'記録されています', '記録されます'),
    
    # より自然な助詞への変更
    (r'を行うことで', 'を行うと'),
    (r'を使用することで', 'を使用すると'),
    (r'を実行することで', 'を実行すると'),
    (r'における', 'での'),  # 文脈による
    (r'に対する', 'への'),  # 文脈による
    (r'に対して', 'に'),  # 文脈による
    (r'より', 'から'),  # 文脈による「A」より「B」→「A」から「B」
    
    # 堅い表現を柔らかく
    (r'において', 'で'),
    (r'に関して', 'について'),
    (r'に関する', 'についての'),
    (r'および', 'と'),
    (r'ならびに', 'と'),
    (r'または', 'か'),
    (r'もしくは', 'か'),
    
    # 回りくどい表現の簡潔化
    (r'どのように(.+)するか', r'\1する方法'),
    (r'なぜ(.+)なのか', r'\1する理由'),
    (r'いつ(.+)するか', r'\1するタイミング'),
    (r'何を(.+)するか', r'\1する対象'),
    
    # その他の簡潔化
    (r'という機能', 'の機能'),
    (r'というメソッド', 'メソッド'),
    (r'というクラス', 'クラス'),
    (r'ということです', 'です'),
    (r'ということになります', 'となります'),
    (r'実装を行います', '実装します'),
    (r'処理を行います', '処理します'),
    (r'設定を行います', '設定します'),
    (r'確認を行います', '確認します'),
    (r'検証を行います', '検証します'),
]

def apply_natural_japanese(content):
    """自然な日本語表現への変換を適用"""
    modified = content
    changes = []
    
    # パターンマッチングによる置換
    for pattern, replacement in NATURAL_JAPANESE_PATTERNS:
        # コードブロック内は除外
        lines = modified.split('\n')
        in_code_block = False
        new_lines = []
        
        for line in lines:
            if line.startswith('```'):
                in_code_block = not in_code_block
                new_lines.append(line)
                continue
            
            if not in_code_block and pattern in line:
                new_line = re.sub(pattern, replacement, line)
                if new_line != line:
                    changes.append(f"置換: '{pattern}' → '{replacement}'")
                    line = new_line
            
            new_lines.append(line)
        
        modified = '\n'.join(new_lines)
    
    return modified, changes

def process_chunk(file_path):
    """チャンクファイルを校正"""
    print(f"\n処理中: {file_path}")
    
    # ファイルを読み込み
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 自然な日本語表現への変換
    modified_content, changes = apply_natural_japanese(content)
    
    if changes:
        # 変更があった場合のみファイルを更新
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(modified_content)
        
        print(f"  変更数: {len(changes)}")
        for change in changes[:5]:  # 最初の5件のみ表示
            print(f"    - {change}")
        if len(changes) > 5:
            print(f"    ... 他 {len(changes) - 5} 件")
    else:
        print("  変更なし")
    
    return len(changes)

def main():
    """メイン処理"""
    chunks_dir = Path("proofreading_chunks")
    
    # chapter04のチャンクファイルを取得（チャンク3以降）
    chunk_files = sorted([
        f for f in chunks_dir.glob("chapter04-classes-and-instances_chunk_*.md")
        if int(f.stem.split('_')[-1]) >= 3  # チャンク3以降
    ])
    
    print(f"第4章の自動校正を開始します")
    print(f"対象チャンク数: {len(chunk_files)}")
    
    total_changes = 0
    for chunk_file in chunk_files:
        changes = process_chunk(chunk_file)
        total_changes += changes
    
    print(f"\n=== 校正完了 ===")
    print(f"総変更数: {total_changes}")
    print("\n次のステップ:")
    print("1. 各チャンクの内容を確認してください")
    print("2. 必要に応じて手動で微調整してください")
    print("3. マージコマンドでファイルを統合してください")

if __name__ == "__main__":
    main()