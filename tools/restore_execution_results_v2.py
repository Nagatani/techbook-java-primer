#!/usr/bin/env python3
"""
実行結果のラベルを復元するスクリプト（改良版）
Javaコードブロックの後の出力に「実行結果：」を追加
"""

import re
import sys
from pathlib import Path

def restore_execution_results(content):
    """実行結果のラベルを復元する"""
    
    # Javaコードブロックの後に続く出力コードブロックに「実行結果：」を追加
    # パターン: ```で終わり、空行、```で始まる（言語指定なし）
    pattern = r'(```java[\s\S]*?```)\n\n(```\n(?:(?!```)[\s\S])*?```)'
    
    def replace_func(match):
        java_code = match.group(1)
        output_block = match.group(2)
        
        # main メソッドが含まれているか、テストクラスかチェック
        if 'public static void main' in java_code or 'Test' in java_code:
            # 実行結果：が既に含まれていない場合のみ追加
            if '実行結果：' not in match.group(0):
                return f"{java_code}\n\n実行結果：\n{output_block}"
        
        return match.group(0)
    
    content = re.sub(pattern, replace_func, content, flags=re.MULTILINE)
    
    # 使用例と実行結果のパターン
    # Javaコードの後に別のJavaコードがあり、その後に実行結果がある場合
    pattern2 = r'(```java[\s\S]*?// .*Test\.java[\s\S]*?```)\n\n(```\n(?:(?!```)[\s\S])*?```)'
    
    def replace_func2(match):
        test_code = match.group(1)
        output_block = match.group(2)
        
        # Test.javaファイルの後の出力は実行結果
        if '実行結果：' not in match.group(0):
            return f"{test_code}\n\n実行結果：\n{output_block}"
        
        return match.group(0)
    
    content = re.sub(pattern2, replace_func2, content, flags=re.MULTILINE)
    
    return content

def process_file(file_path):
    """ファイルを処理する"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # 変換実施
        restored_content = restore_execution_results(content)
        
        # バックアップ作成
        backup_path = file_path.with_suffix(file_path.suffix + '.backup_restore_v2')
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
        print("使用方法: python restore_execution_results_v2.py <ファイルパス> [ファイルパス2 ...]")
        sys.exit(1)
    
    for file_path_str in sys.argv[1:]:
        file_path = Path(file_path_str)
        if file_path.exists() and file_path.is_file():
            process_file(file_path)
        else:
            print(f"✗ ファイルが見つかりません: {file_path}")

if __name__ == "__main__":
    main()