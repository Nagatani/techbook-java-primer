#!/usr/bin/env python3
"""
コードリスト番号の現状を分析するスクリプト
"""

import os
import re
from pathlib import Path
from collections import defaultdict

def analyze_code_listings(filepath):
    """ファイル内のコードブロックとリスト番号を分析"""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 章番号を取得
    filename = os.path.basename(filepath)
    chapter_match = re.match(r'chapter(\d+)', filename)
    chapter_num = int(chapter_match.group(1)) if chapter_match else 0
    
    # コードブロックを検出
    code_blocks = re.findall(r'```[\s\S]*?```', content)
    total_blocks = len(code_blocks)
    
    # リスト番号付きコードブロックを検出
    # パターン: **リストX.Y** または **リストX-Y** または **サンプルコードX.Y** など
    numbered_pattern = r'\*\*(?:リスト|サンプルコード|コード)\s*(\d+)[-\.]\s*(\d+)\*\*'
    numbered_blocks = re.findall(numbered_pattern, content)
    
    # リスト番号の詳細を収集
    listing_numbers = []
    for match in re.finditer(numbered_pattern, content):
        chapter, number = match.groups()
        listing_numbers.append({
            'chapter': int(chapter),
            'number': int(number),
            'format': match.group(0)
        })
    
    return {
        'chapter': chapter_num,
        'total_blocks': total_blocks,
        'numbered_blocks': len(numbered_blocks),
        'unnumbered_blocks': total_blocks - len(numbered_blocks),
        'listing_numbers': listing_numbers
    }

def main():
    manuscripts_dir = Path('/Users/nagatani/github/techbook-java-primer/manuscripts')
    
    # 章ファイルのみを対象
    chapter_files = sorted(manuscripts_dir.glob('chapter*.md'))
    
    all_results = {}
    total_blocks = 0
    total_numbered = 0
    
    # 章ごとの番号の連続性チェック
    chapter_sequences = defaultdict(list)
    
    for filepath in chapter_files:
        result = analyze_code_listings(filepath)
        all_results[filepath.name] = result
        total_blocks += result['total_blocks']
        total_numbered += result['numbered_blocks']
        
        # 番号の連続性を記録
        for listing in result['listing_numbers']:
            chapter_sequences[listing['chapter']].append(listing['number'])
    
    # 結果の表示
    print("# コードリスト番号分析レポート\n")
    print(f"総コードブロック数: {total_blocks}")
    print(f"番号付きブロック数: {total_numbered} ({total_numbered/total_blocks*100:.1f}%)")
    print(f"番号なしブロック数: {total_blocks - total_numbered} ({(total_blocks - total_numbered)/total_blocks*100:.1f}%)")
    print("\n## 章ごとの詳細\n")
    
    for filename, result in all_results.items():
        if result['total_blocks'] > 0:
            print(f"### {filename}")
            print(f"- コードブロック総数: {result['total_blocks']}")
            print(f"- 番号付き: {result['numbered_blocks']}")
            print(f"- 番号なし: {result['unnumbered_blocks']}")
            
            if result['listing_numbers']:
                numbers = [l['number'] for l in result['listing_numbers'] if l['chapter'] == result['chapter']]
                if numbers:
                    print(f"- 番号範囲: {min(numbers)} - {max(numbers)}")
    
    # 番号の連続性チェック
    print("\n## 番号の連続性チェック\n")
    issues_found = False
    
    for chapter, numbers in sorted(chapter_sequences.items()):
        numbers = sorted(set(numbers))  # 重複を除去してソート
        gaps = []
        
        for i in range(len(numbers) - 1):
            if numbers[i+1] - numbers[i] > 1:
                gaps.append(f"{numbers[i]}と{numbers[i+1]}の間")
        
        if gaps:
            issues_found = True
            print(f"第{chapter}章: 番号の飛び - {', '.join(gaps)}")
    
    if not issues_found:
        print("番号の飛びは検出されませんでした。")
    
    # 形式の統一性チェック
    print("\n## 形式の種類\n")
    format_types = defaultdict(int)
    for result in all_results.values():
        for listing in result['listing_numbers']:
            # 形式を簡略化して分類
            if 'リスト' in listing['format']:
                if '-' in listing['format']:
                    format_types['リストX-Y'] += 1
                else:
                    format_types['リストX.Y'] += 1
            elif 'サンプルコード' in listing['format']:
                format_types['サンプルコード'] += 1
            elif 'コード' in listing['format']:
                format_types['コード'] += 1
    
    for format_type, count in sorted(format_types.items()):
        print(f"- {format_type}: {count}個")

if __name__ == '__main__':
    main()