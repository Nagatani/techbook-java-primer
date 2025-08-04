#!/usr/bin/env python3
"""
コロンパターンを文章/リスト形式に変換するスクリプト
見出しではなく、太字強調や削除により書籍の見やすさを向上させる
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
    
    # 問題：/ 解決策：を太字強調に変換
    content = re.sub(
        r'^#{3,6}\s+(問題|解決策)\s*\n',
        r'**\1**  \n',
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
    
    # 改善点：/ 残る問題：を太字強調に変換
    content = re.sub(
        r'^#{3,6}\s+(改善点|残る問題)\s*\n',
        r'**\1**\n',
        content,
        flags=re.MULTILINE
    )
    
    # その他の一般的なコロンパターン（段落が続く場合）
    content = re.sub(
        r'^#{3,6}\s+(.+?)：\s*\n(?!```)',
        r'**\1**  \n',
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
        backup_path = file_path.with_suffix(file_path.suffix + '.backup')
        with open(backup_path, 'w', encoding='utf-8') as f:
            f.write(content)
        
        # 変換後の内容を書き込み
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(converted_content)
        
        print(f"✓ {file_path} を変換しました（バックアップ: {backup_path}）")
        
        # 変換箇所を表示
        if content != converted_content:
            print("  変換された箇所:")
            # 簡易的な差分表示
            lines_before = content.split('\n')
            lines_after = converted_content.split('\n')
            for i, (before, after) in enumerate(zip(lines_before, lines_after)):
                if before != after and before.strip().endswith('：'):
                    print(f"    行{i+1}: {before.strip()} → {after.strip()}")
        
    except Exception as e:
        print(f"✗ エラー: {file_path} - {e}")

def main():
    """メイン処理"""
    if len(sys.argv) < 2:
        print("使用方法: python convert_colon_patterns.py <ファイルパス> [ファイルパス2 ...]")
        sys.exit(1)
    
    for file_path_str in sys.argv[1:]:
        file_path = Path(file_path_str)
        if file_path.exists() and file_path.is_file():
            process_file(file_path)
        else:
            print(f"✗ ファイルが見つかりません: {file_path}")

if __name__ == "__main__":
    main()