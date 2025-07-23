#!/usr/bin/env python3
"""
である調の使用箇所を検出するスクリプト
リスト項目内は除外（意図的にである調を使用）
"""

import os
import re
from pathlib import Path

def find_dearu_in_file(filepath):
    """ファイル内のである調を検出（リスト項目を除く）"""
    with open(filepath, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    dearu_occurrences = []
    in_list = False
    
    # である調のパターン
    dearu_patterns = [
        r'である。',
        r'であった。',
        r'ではない。',
        r'となる。',
        r'できる。',
        r'する。',
        r'ない。',
        r'ある。'
    ]
    
    for i, line in enumerate(lines):
        # リスト項目の判定（- や * や数字. で始まる行）
        is_list_item = re.match(r'^[\s]*[-*]|\d+\.', line)
        
        # コードブロック内は除外
        if line.strip().startswith('```'):
            continue
        
        # リスト項目でない場合のみである調をチェック
        if not is_list_item:
            for pattern in dearu_patterns:
                if re.search(pattern, line):
                    # 前後の文脈も含めて記録
                    context_start = max(0, i - 1)
                    context_end = min(len(lines), i + 2)
                    context = ''.join(lines[context_start:context_end])
                    
                    dearu_occurrences.append({
                        'line_num': i + 1,
                        'line': line.strip(),
                        'pattern': pattern,
                        'context': context
                    })
                    break
    
    return dearu_occurrences

def main():
    manuscripts_dir = Path('/Users/nagatani/github/techbook-java-primer/manuscripts')
    
    # 章ファイルのみを対象
    chapter_files = sorted(manuscripts_dir.glob('chapter*.md'))
    
    all_occurrences = {}
    total_count = 0
    
    for filepath in chapter_files:
        occurrences = find_dearu_in_file(filepath)
        if occurrences:
            all_occurrences[filepath.name] = occurrences
            total_count += len(occurrences)
    
    # 結果の表示
    print("# である調使用箇所レポート（リスト項目を除く）\n")
    print(f"総検出数: {total_count}箇所\n")
    
    for filename, occurrences in all_occurrences.items():
        print(f"\n## {filename} ({len(occurrences)}箇所)\n")
        
        for occ in occurrences[:5]:  # 各ファイル最初の5件のみ表示
            print(f"### 行{occ['line_num']}: {occ['pattern']}")
            print(f"該当行: {occ['line']}")
            print(f"文脈:")
            print("```")
            print(occ['context'].strip())
            print("```\n")
        
        if len(occurrences) > 5:
            print(f"... 他{len(occurrences) - 5}箇所\n")
    
    # パターン別集計
    print("\n## パターン別集計\n")
    pattern_counts = {}
    for occurrences in all_occurrences.values():
        for occ in occurrences:
            pattern = occ['pattern']
            pattern_counts[pattern] = pattern_counts.get(pattern, 0) + 1
    
    for pattern, count in sorted(pattern_counts.items(), key=lambda x: x[1], reverse=True):
        print(f"- {pattern}: {count}箇所")

if __name__ == '__main__':
    main()