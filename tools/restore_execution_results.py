#!/usr/bin/env python3
"""
実行結果のラベルを復元するスクリプト
コードブロックの前に適切な「実行結果：」を追加
"""

import re
import sys
from pathlib import Path

def restore_execution_results(content):
    """実行結果のラベルを復元する"""
    
    # パターン1: Javaコードブロックの後に続く実行結果のコードブロック
    # ```java
    # ...
    # ```
    # 
    # ```
    # 出力内容
    # ```
    content = re.sub(
        r'(```java[\s\S]*?```)\n\n(```\n(?!java|xml|html|css|javascript|python|bash|sh))',
        r'\1\n\n実行結果：\n\2',
        content,
        flags=re.MULTILINE
    )
    
    # パターン2: main メソッドを含むJavaコードの後の実行結果
    # 既に「実行結果：」がある場合はスキップ
    lines = content.split('\n')
    new_lines = []
    i = 0
    
    while i < len(lines):
        new_lines.append(lines[i])
        
        # Javaコードブロックの終了を検出
        if lines[i].strip() == '```' and i > 0:
            # 前のコードブロックがJavaで、main メソッドを含んでいるか確認
            j = i - 1
            is_java_with_main = False
            while j >= 0 and lines[j].strip() != '```java':
                if 'public static void main' in lines[j]:
                    is_java_with_main = True
                j -= 1
            
            # 次の行を確認
            if is_java_with_main and i + 2 < len(lines):
                # 空行の後にコードブロックが続く場合
                if lines[i + 1].strip() == '' and lines[i + 2].strip() == '```':
                    # すでに「実行結果：」がない場合のみ追加
                    if i + 3 < len(lines) and '実行結果' not in lines[i + 1]:
                        new_lines.append('')
                        new_lines.append('実行結果：')
                        i += 1
                        continue
        
        i += 1
    
    return '\n'.join(new_lines)

def process_file(file_path):
    """ファイルを処理する"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # 変換実施
        restored_content = restore_execution_results(content)
        
        # バックアップ作成
        backup_path = file_path.with_suffix(file_path.suffix + '.backup_restore')
        with open(backup_path, 'w', encoding='utf-8') as f:
            f.write(content)
        
        # 変換後の内容を書き込み
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(restored_content)
        
        print(f"✓ {file_path} の実行結果ラベルを復元しました（バックアップ: {backup_path}）")
        
        # 変換箇所を表示
        if content != restored_content:
            added_count = restored_content.count('実行結果：') - content.count('実行結果：')
            if added_count > 0:
                print(f"    {added_count}箇所の実行結果ラベルを復元")
        
    except Exception as e:
        print(f"✗ エラー: {file_path} - {e}")

def main():
    """メイン処理"""
    if len(sys.argv) < 2:
        print("使用方法: python restore_execution_results.py <ファイルパス> [ファイルパス2 ...]")
        sys.exit(1)
    
    for file_path_str in sys.argv[1:]:
        file_path = Path(file_path_str)
        if file_path.exists() and file_path.is_file():
            process_file(file_path)
        else:
            print(f"✗ ファイルが見つかりません: {file_path}")

if __name__ == "__main__":
    main()