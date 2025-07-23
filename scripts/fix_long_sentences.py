#!/usr/bin/env python3
"""
100文字を超える文を修正するスクリプト
文を適切な位置で分割し、読みやすくする
"""

import re
from pathlib import Path

def split_long_sentence(sentence):
    """長い文を適切に分割"""
    # 句読点での分割候補を探す
    # 「、」で分割できる場合
    parts = sentence.split('、')
    if len(parts) > 2:
        # 中間地点で分割
        mid = len(parts) // 2
        first = '、'.join(parts[:mid]) + '。'
        second = '、'.join(parts[mid:])
        return first + second
    
    # 「が」「けれど」「しかし」などの接続助詞で分割
    connectors = ['が、', 'けれど', 'しかし', 'ただし', 'また、', 'さらに、', 'そして、']
    for connector in connectors:
        if connector in sentence:
            parts = sentence.split(connector, 1)
            if len(parts) == 2:
                return parts[0] + '。' + connector[0] + parts[1]
    
    # 「ため」「ので」「から」で分割
    reasons = ['ため、', 'ので、', 'から、']
    for reason in reasons:
        if reason in sentence:
            parts = sentence.split(reason, 1)
            if len(parts) == 2 and len(parts[0]) > 40:
                return parts[0] + reason[:-1] + 'ます。' + parts[1]
    
    return sentence

def fix_long_sentences_in_file(filepath):
    """ファイル内の長い文を修正"""
    with open(filepath, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    modified_lines = []
    changes_made = 0
    
    for line in lines:
        # コードブロック内はスキップ
        if line.strip().startswith('```'):
            modified_lines.append(line)
            continue
        
        # リスト項目はスキップ（CLAUDE.mdのルールに従う）
        if line.strip().startswith('-') or line.strip().startswith('*'):
            modified_lines.append(line)
            continue
        
        # 100文字を超える文を検出
        if len(line.strip()) > 100 and line.strip().endswith('。'):
            # 句点で終わる通常の文
            modified = split_long_sentence(line.strip())
            if modified != line.strip():
                modified_lines.append(modified + '\n')
                changes_made += 1
                print(f"  修正: {line.strip()[:50]}...")
                print(f"  → {modified[:50]}...")
            else:
                modified_lines.append(line)
        else:
            modified_lines.append(line)
    
    if changes_made > 0:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.writelines(modified_lines)
    
    return changes_made

def main():
    manuscripts_dir = Path('/Users/nagatani/github/techbook-java-primer/manuscripts')
    
    # textlintで文長エラーが報告されたファイルを優先的に処理
    priority_files = [
        'appendix-a-environment-setup.md',
        'appendix-b-index-revised.md',
        'appendix-c-theoretical-foundations.md',
        'appendix-d-integrated-project.md',
        'appendix-e-database-programming.md',
        'appendix-g-build-and-deploy.md',
        'appendix-index.md',
        'chapter01-introduction.md',
        'chapter02-getting-started.md',
        'chapter03-oop-basics.md',
    ]
    
    total_changes = 0
    
    for filename in priority_files:
        filepath = manuscripts_dir / filename
        if filepath.exists():
            print(f"処理中: {filename}")
            changes = fix_long_sentences_in_file(filepath)
            if changes > 0:
                print(f"  → {changes}個の長い文を修正")
                total_changes += changes
    
    print(f"\n総計: {total_changes}個の長い文を修正しました。")

if __name__ == '__main__':
    main()