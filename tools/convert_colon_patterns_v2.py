#!/usr/bin/env python3
"""
コロンパターンを説明文形式に変換するスクリプト（v2）
CLAUDE.mdのルールに従い、太字強調の見出し的使用を避ける
"""

import re
import sys
from pathlib import Path

def convert_colon_patterns(content):
    """コロンパターンを変換する"""
    
    # 実行結果：パターンを削除（コードブロックの前のみ）
    content = re.sub(
        r'^#{3,6}\s+実行結果\s*\n+```',
        r'```',
        content,
        flags=re.MULTILINE
    )
    
    # 使用例：/ 使用例と実行結果：パターンを削除（コードブロックの前のみ）
    content = re.sub(
        r'^#{3,6}\s+使用例(と実行結果)?\s*\n+```',
        r'```',
        content,
        flags=re.MULTILINE
    )
    
    # 問題：を説明文に変換
    content = re.sub(
        r'^#{3,6}\s+問題\s*\n',
        r'問題は以下の通りです。\n',
        content,
        flags=re.MULTILINE
    )
    
    # 解決策：を説明文に変換
    content = re.sub(
        r'^#{3,6}\s+解決策\s*\n',
        r'解決策は以下の通りです。\n',
        content,
        flags=re.MULTILINE
    )
    
    # 重要なポイント：を削除（リストが続く場合）
    content = re.sub(
        r'^#{3,6}\s+重要なポイント\s*\n(?=\s*[-*])',
        r'',
        content,
        flags=re.MULTILINE
    )
    
    # 改善点：を説明文に変換
    content = re.sub(
        r'^#{3,6}\s+改善点\s*\n',
        r'この実装での改善点は以下の通りです。\n',
        content,
        flags=re.MULTILINE
    )
    
    # 残る問題：を説明文に変換
    content = re.sub(
        r'^#{3,6}\s+残る問題\s*\n',
        r'まだ解決されていない問題は以下の通りです。\n',
        content,
        flags=re.MULTILINE
    )
    
    # 太字強調パターンも説明文に変換
    content = re.sub(
        r'^\*\*問題\*\*\s*\n',
        r'問題は以下の通りです。\n',
        content,
        flags=re.MULTILINE
    )
    
    content = re.sub(
        r'^\*\*解決策\*\*\s*\n',
        r'解決策は以下の通りです。\n',
        content,
        flags=re.MULTILINE
    )
    
    content = re.sub(
        r'^\*\*改善点\*\*\s*\n',
        r'この実装での改善点は以下の通りです。\n',
        content,
        flags=re.MULTILINE
    )
    
    content = re.sub(
        r'^\*\*残る問題\*\*\s*\n',
        r'まだ解決されていない問題は以下の通りです。\n',
        content,
        flags=re.MULTILINE
    )
    
    # 重要なポイントのリスト（前に太字がある場合）
    content = re.sub(
        r'^\*\*重要なポイント\*\*\s*\n(?=\s*[-*])',
        r'',
        content,
        flags=re.MULTILINE
    )
    
    # 問題・解決策の説明文にスペースがある場合の処理
    content = re.sub(
        r'^\*\*(問題|解決策)\*\*\s+',
        lambda m: f'{m.group(1)}は、',
        content,
        flags=re.MULTILINE
    )
    
    return content

def process_file(file_path):
    """ファイルを処理する"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # 変換実施
        converted_content = convert_colon_patterns(content)
        
        # バックアップ作成
        backup_path = file_path.with_suffix(file_path.suffix + '.backup_v2')
        with open(backup_path, 'w', encoding='utf-8') as f:
            f.write(content)
        
        # 変換後の内容を書き込み
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(converted_content)
        
        print(f"✓ {file_path} を変換しました（バックアップ: {backup_path}）")
        
        # 変換箇所を表示
        if content != converted_content:
            print("  変換された箇所を確認中...")
            # 簡易的な差分表示
            pattern = re.compile(r'(\*\*(問題|解決策|改善点|残る問題|重要なポイント)\*\*|^#{3,6}\s+(問題|解決策|改善点|残る問題|重要なポイント))', re.MULTILINE)
            matches = pattern.findall(content)
            if matches:
                print(f"    {len(matches)}箇所の変換を実施")
        
    except Exception as e:
        print(f"✗ エラー: {file_path} - {e}")

def main():
    """メイン処理"""
    if len(sys.argv) < 2:
        print("使用方法: python convert_colon_patterns_v2.py <ファイルパス> [ファイルパス2 ...]")
        sys.exit(1)
    
    for file_path_str in sys.argv[1:]:
        file_path = Path(file_path_str)
        if file_path.exists() and file_path.is_file():
            process_file(file_path)
        else:
            print(f"✗ ファイルが見つかりません: {file_path}")

if __name__ == "__main__":
    main()