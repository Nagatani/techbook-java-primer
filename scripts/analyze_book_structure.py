#!/usr/bin/env python3
"""
書籍の構造を分析するスクリプト
各章の節構成、コードブロック数、演習の有無などを調査
"""

import os
import re
from pathlib import Path
import json

def analyze_markdown_file(filepath):
    """Markdownファイルを分析"""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # ファイル名から章番号を取得
    filename = os.path.basename(filepath)
    chapter_match = re.match(r'chapter(\d+)', filename)
    chapter_num = int(chapter_match.group(1)) if chapter_match else 0
    
    # タイトルを取得（最初の#行）
    title_match = re.search(r'^#\s+(.+)$', content, re.MULTILINE)
    title = title_match.group(1) if title_match else "不明"
    
    # 節（##）の数と内容を取得
    sections = re.findall(r'^##\s+(.+)$', content, re.MULTILINE)
    
    # コードブロックの数をカウント
    code_blocks = len(re.findall(r'```[\s\S]*?```', content))
    
    # 演習セクションの有無と位置を確認
    exercise_match = re.search(r'^##\s*演習.*$', content, re.MULTILINE)
    has_exercise = bool(exercise_match)
    exercise_position = exercise_match.start() if exercise_match else -1
    
    # 付録参照を抽出
    appendix_refs = re.findall(r'付録[A-G](?:\.\d+)?', content)
    
    # 章間参照を抽出（第X章への参照）
    chapter_refs = re.findall(r'第(\d+)章', content)
    
    # GitHubリンクを抽出
    github_links = re.findall(r'https://github\.com/[^\s\)]+', content)
    
    # 行数
    line_count = len(content.splitlines())
    
    return {
        'filename': filename,
        'chapter_num': chapter_num,
        'title': title,
        'line_count': line_count,
        'sections': sections,
        'section_count': len(sections),
        'code_blocks': code_blocks,
        'has_exercise': has_exercise,
        'exercise_at_end': exercise_position > len(content) * 0.8 if has_exercise else None,
        'appendix_refs': list(set(appendix_refs)),
        'chapter_refs': list(set(map(int, chapter_refs))),
        'github_links': github_links
    }

def analyze_appendix_file(filepath):
    """付録ファイルを分析"""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    filename = os.path.basename(filepath)
    
    # タイトルを取得
    title_match = re.search(r'^#\s+(.+)$', content, re.MULTILINE)
    title = title_match.group(1) if title_match else "不明"
    
    # 節の数
    sections = re.findall(r'^##\s+(.+)$', content, re.MULTILINE)
    
    # コードブロックの数
    code_blocks = len(re.findall(r'```[\s\S]*?```', content))
    
    # 行数
    line_count = len(content.splitlines())
    
    return {
        'filename': filename,
        'title': title,
        'line_count': line_count,
        'sections': sections,
        'section_count': len(sections),
        'code_blocks': code_blocks
    }

def main():
    manuscripts_dir = Path('/Users/nagatani/github/techbook-java-primer/manuscripts')
    
    # 章ファイルの分析
    chapter_files = sorted(manuscripts_dir.glob('chapter*.md'))
    chapters_data = []
    
    for filepath in chapter_files:
        data = analyze_markdown_file(filepath)
        chapters_data.append(data)
    
    # 付録ファイルの分析
    appendix_files = sorted(manuscripts_dir.glob('appendix-[a-g]*.md'))
    appendix_data = []
    
    for filepath in appendix_files:
        if 'index' not in filepath.name:
            data = analyze_appendix_file(filepath)
            appendix_data.append(data)
    
    # 結果の出力
    print("# 書籍構造分析レポート\n")
    
    print("## 章構成")
    print(f"総章数: {len(chapters_data)}\n")
    
    total_code_blocks = 0
    for chapter in chapters_data:
        print(f"### 第{chapter['chapter_num']}章: {chapter['title']}")
        print(f"- 行数: {chapter['line_count']:,}")
        print(f"- 節数: {chapter['section_count']}")
        print(f"- コードブロック数: {chapter['code_blocks']}")
        print(f"- 演習: {'あり' if chapter['has_exercise'] else 'なし'}")
        if chapter['has_exercise']:
            print(f"  - 位置: {'章末' if chapter['exercise_at_end'] else '章の途中'}")
        if chapter['appendix_refs']:
            print(f"- 付録参照: {', '.join(chapter['appendix_refs'])}")
        if chapter['chapter_refs']:
            print(f"- 章間参照: 第{', '.join(map(str, sorted(chapter['chapter_refs'])))}章")
        print()
        total_code_blocks += chapter['code_blocks']
    
    print(f"\n**全章のコードブロック総数: {total_code_blocks}**\n")
    
    print("\n## 付録構成")
    print(f"付録数: {len(appendix_data)}\n")
    
    for appendix in appendix_data:
        print(f"### {appendix['filename']}")
        print(f"- タイトル: {appendix['title']}")
        print(f"- 行数: {appendix['line_count']:,}")
        print(f"- 節数: {appendix['section_count']}")
        print(f"- コードブロック数: {appendix['code_blocks']}")
        print()
    
    # 問題点の検出
    print("\n## 検出された潜在的問題")
    
    # 演習が章末にない章
    non_end_exercises = [ch for ch in chapters_data if ch['has_exercise'] and not ch['exercise_at_end']]
    if non_end_exercises:
        print("\n### 演習が章末にない章:")
        for ch in non_end_exercises:
            print(f"- 第{ch['chapter_num']}章")
    
    # 演習がない章
    no_exercises = [ch for ch in chapters_data if not ch['has_exercise']]
    if no_exercises:
        print("\n### 演習セクションがない章:")
        for ch in no_exercises:
            print(f"- 第{ch['chapter_num']}章")
    
    # JSON形式でも保存
    output_data = {
        'chapters': chapters_data,
        'appendices': appendix_data,
        'total_code_blocks': total_code_blocks
    }
    
    with open('book_structure_analysis.json', 'w', encoding='utf-8') as f:
        json.dump(output_data, f, ensure_ascii=False, indent=2)
    
    print("\n分析結果をbook_structure_analysis.jsonに保存しました。")

if __name__ == '__main__':
    main()