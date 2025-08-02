#!/usr/bin/env python3
"""
textlintのコロンパターンエラーを安全に修正するツール
"""
import re
import sys
from pathlib import Path

# コロンパターンの修正ルール
COLON_PATTERNS = [
    # 実行結果パターン
    {
        'pattern': r'^実行結果（([^）]+)）：\s*$',
        'replacement': lambda m: f'#### 実行結果\n{m.group(1)}の場合の結果を示します。'
    },
    {
        'pattern': r'^実行結果：\s*$',
        'replacement': '#### 実行結果\n以下に実行結果を示します。'
    },
    # テストコード付きパターン
    {
        'pattern': r'^実行結果（テストコード付き）：\s*$',
        'replacement': '#### 実行結果\n以下はテストコードを含む実行結果です。'
    },
    # 定義パターン
    {
        'pattern': r'^(.+クラスの定義)（([^）]+)）：\s*$',
        'replacement': lambda m: f'#### {m.group(1)}（{m.group(2)}）\n必要なクラス定義を以下に示します。'
    },
    # 一般的なパターン
    {
        'pattern': r'^(.+)（([^）]+)）：\s*$',
        'replacement': lambda m: f'#### {m.group(1)}\n{m.group(2)}について以下に示します。'
    },
]

def fix_colon_patterns(content):
    """コロンパターンを修正"""
    lines = content.split('\n')
    modified_lines = []
    changes = []
    
    for i, line in enumerate(lines):
        modified = False
        for rule in COLON_PATTERNS:
            match = re.match(rule['pattern'], line)
            if match:
                if callable(rule['replacement']):
                    new_line = rule['replacement'](match)
                else:
                    new_line = rule['replacement']
                
                changes.append({
                    'line': i + 1,
                    'before': line,
                    'after': new_line
                })
                modified_lines.append(new_line)
                modified = True
                break
        
        if not modified:
            modified_lines.append(line)
    
    return '\n'.join(modified_lines), changes

def main():
    if len(sys.argv) < 2:
        print("使用方法: python fix_textlint_colons.py <ファイルパス>")
        sys.exit(1)
    
    file_path = Path(sys.argv[1])
    if not file_path.exists():
        print(f"エラー: ファイルが見つかりません: {file_path}")
        sys.exit(1)
    
    # ファイルを読み込み
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # コロンパターンを修正
    modified_content, changes = fix_colon_patterns(content)
    
    if not changes:
        print("修正対象のコロンパターンは見つかりませんでした。")
        return
    
    # 変更内容を表示
    print(f"=== {len(changes)}件のコロンパターンを検出 ===\n")
    for change in changes:
        print(f"行 {change['line']}:")
        print(f"  変更前: {change['before']}")
        print(f"  変更後: {change['after']}")
        print()
    
    print("変更を適用しますか？ (y/n): ", end='')
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