#!/usr/bin/env python3
"""
安全な自動校正ツール - 明確で安全な変換のみを実行
"""
import re
import sys
from pathlib import Path

# 安全な変換パターンのみ（文脈に依存しない明確なもの）
SAFE_PATTERNS = [
    # 明らかな冗長表現の簡潔化（文脈に依存しない）
    (r'することができます', 'できます'),
    (r'することが可能です', '可能です'),
    (r'することが必要です', '必要です'),
    (r'する必要があります', '必要です'),
    (r'されることになります', 'されます'),
    (r'することになります', 'します'),
    (r'実装されています', '実装されます'),
    (r'使用されています', '使用されます'),
    (r'定義されています', '定義されます'),
    (r'提供されています', '提供されます'),
    
    # 明確な言い換え
    (r'を行うことで', 'を行うと'),
    (r'を使用することで', 'を使用すると'),
    (r'を実行することで', 'を実行すると'),
    (r'ということです', 'です'),
    (r'ということになります', 'となります'),
    
    # 動詞の簡潔化
    (r'実装を行います', '実装します'),
    (r'処理を行います', '処理します'),
    (r'設定を行います', '設定します'),
    (r'確認を行います', '確認します'),
    (r'検証を行います', '検証します'),
]

# 危険な変換（文脈依存のため除外）
UNSAFE_PATTERNS = [
    # これらは絶対に自動変換しない
    'より',  # 比較と手段の両方の意味
    'から',  # 起点と理由の両方の意味
    'ため',  # 目的と原因の両方の意味
    'において',  # 文脈により適切な場合がある
    'における',  # 文脈により適切な場合がある
    'および',  # 技術文書では適切な場合がある
    'または',  # 技術文書では適切な場合がある
    'によって',  # 重要な因果関係を表す
    'により',  # 重要な因果関係を表す
    'に対して',  # 明確な対象指定
    'に対する',  # 明確な対象指定
    'に関して',  # フォーマルな文書で適切
    'に関する',  # フォーマルな文書で適切
]

def apply_safe_replacements(content):
    """安全な置換のみを適用し、変更内容を詳細に記録"""
    modified = content
    changes = []
    change_locations = []
    
    lines = modified.split('\n')
    
    for pattern, replacement in SAFE_PATTERNS:
        new_lines = []
        in_code_block = False
        
        for line_num, line in enumerate(lines, 1):
            if line.startswith('```'):
                in_code_block = not in_code_block
                new_lines.append(line)
                continue
            
            if not in_code_block and pattern in line:
                # 変更前後を記録
                old_line = line
                new_line = re.sub(pattern, replacement, line)
                if new_line != old_line:
                    changes.append({
                        'line': line_num,
                        'pattern': pattern,
                        'replacement': replacement,
                        'before': old_line.strip(),
                        'after': new_line.strip()
                    })
                    line = new_line
            
            new_lines.append(line)
        
        lines = new_lines
    
    return '\n'.join(lines), changes

def review_changes(changes):
    """変更内容をレビュー用に整形"""
    if not changes:
        return "変更なし"
    
    review = []
    review.append(f"## 変更箇所: {len(changes)}件\n")
    
    for i, change in enumerate(changes, 1):
        review.append(f"### 変更 {i}")
        review.append(f"- 行番号: {change['line']}")
        review.append(f"- パターン: '{change['pattern']}' → '{change['replacement']}'")
        review.append(f"- 変更前: {change['before']}")
        review.append(f"- 変更後: {change['after']}")
        review.append("")
    
    return '\n'.join(review)

def main():
    if len(sys.argv) < 2:
        print("使用方法: python safe_auto_proofread.py <ファイルパス>")
        sys.exit(1)
    
    file_path = Path(sys.argv[1])
    if not file_path.exists():
        print(f"エラー: ファイルが見つかりません: {file_path}")
        sys.exit(1)
    
    # ファイルを読み込み
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 安全な変換のみ適用
    modified_content, changes = apply_safe_replacements(content)
    
    # レビュー用のレポートを生成
    review_report = review_changes(changes)
    
    # レポートを表示
    print("=" * 60)
    print("安全な自動校正レポート")
    print("=" * 60)
    print(review_report)
    
    if changes:
        print("\n変更を適用しますか？ (y/n): ", end='')
        response = input().strip().lower()
        
        if response == 'y':
            # バックアップを作成
            backup_path = file_path.with_suffix(file_path.suffix + '.backup')
            with open(backup_path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"バックアップを作成しました: {backup_path}")
            
            # 変更を適用
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(modified_content)
            print(f"変更を適用しました: {file_path}")
        else:
            print("変更をキャンセルしました")

if __name__ == "__main__":
    main()