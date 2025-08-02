#!/usr/bin/env python3
"""
原稿ファイルを適切にチャンクに分割するツール
無駄な改行を入れずに分割・結合できるようにする
"""

import os
import json
from datetime import datetime
import argparse

def split_manuscript(input_file, output_dir, chunk_size=250):
    """
    原稿ファイルをチャンクに分割する
    
    Args:
        input_file: 入力ファイルパス
        output_dir: 出力ディレクトリ
        chunk_size: 1チャンクあたりの最大行数
    """
    # 出力ディレクトリを作成
    os.makedirs(output_dir, exist_ok=True)
    
    # ファイル名から拡張子を除いたベース名を取得
    base_name = os.path.splitext(os.path.basename(input_file))[0]
    
    # ファイルを読み込む（改行を保持）
    with open(input_file, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    total_lines = len(lines)
    chunks = []
    chunk_number = 1
    current_line = 0
    
    while current_line < total_lines:
        # チャンクの終了位置を決定
        end_line = min(current_line + chunk_size, total_lines)
        
        # コードブロック内での分割を避ける
        in_code_block = False
        for i in range(current_line, end_line):
            if lines[i].strip().startswith('```'):
                in_code_block = not in_code_block
        
        # コードブロック内で終わる場合は、コードブロックの終了まで含める
        if in_code_block:
            for i in range(end_line, total_lines):
                if lines[i].strip().startswith('```'):
                    end_line = i + 1
                    break
        
        # チャンクの内容を作成（ヘッダーとフッターを追加）
        chunk_content = []
        chunk_content.append(f"""<!-- 
校正チャンク情報
================
元ファイル: {os.path.basename(input_file)}
チャンク: {chunk_number}/{(total_lines + chunk_size - 1) // chunk_size}
行範囲: {current_line + 1} - {end_line}
作成日時: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

""")
        
        # 実際のコンテンツを追加（改行を保持）
        chunk_content.extend(lines[current_line:end_line])
        
        # フッターを追加（最後のチャンクでない場合のみ改行を追加）
        if end_line < total_lines:
            chunk_content.append(f"""

<!-- 
================
チャンク {chunk_number}/{(total_lines + chunk_size - 1) // chunk_size} の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
""")
        else:
            # 最後のチャンクは改行なしでフッターを追加
            if not chunk_content[-1].endswith('\n'):
                chunk_content.append('\n')
            chunk_content.append(f"""
<!-- 
================
チャンク {chunk_number}/{(total_lines + chunk_size - 1) // chunk_size} の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
""")
        
        # チャンクファイルを書き出す
        chunk_filename = f"{base_name}_chunk_{chunk_number:03d}.md"
        chunk_path = os.path.join(output_dir, chunk_filename)
        
        with open(chunk_path, 'w', encoding='utf-8') as f:
            f.writelines(chunk_content)
        
        # メタデータを記録
        chunks.append({
            'filename': chunk_filename,
            'chunk_number': chunk_number,
            'start_line': current_line + 1,
            'end_line': end_line,
            'lines': end_line - current_line,
            'first_content': lines[current_line].strip()[:50] if current_line < total_lines else '',
            'status': 'pending'
        })
        
        current_line = end_line
        chunk_number += 1
    
    # メタデータファイルを保存
    metadata = {
        'original_file': input_file,
        'total_lines': total_lines,
        'chunks': chunks,
        'created_at': datetime.now().isoformat()
    }
    
    metadata_path = os.path.join(output_dir, f"{base_name}_metadata.json")
    with open(metadata_path, 'w', encoding='utf-8') as f:
        json.dump(metadata, f, ensure_ascii=False, indent=2)
    
    print(f"ファイルを {len(chunks)} 個のチャンクに分割しました")
    print(f"出力先: {output_dir}")
    print(f"メタデータ: {metadata_path}")

def merge_chunks(output_dir, base_name, output_file):
    """
    チャンクを結合して元のファイルを復元する
    
    Args:
        output_dir: チャンクが保存されているディレクトリ
        base_name: ファイルのベース名
        output_file: 出力ファイルパス
    """
    # メタデータを読み込む
    metadata_path = os.path.join(output_dir, f"{base_name}_metadata.json")
    with open(metadata_path, 'r', encoding='utf-8') as f:
        metadata = json.load(f)
    
    merged_content = []
    
    for chunk_info in metadata['chunks']:
        chunk_path = os.path.join(output_dir, chunk_info['filename'])
        
        with open(chunk_path, 'r', encoding='utf-8') as f:
            lines = f.readlines()
        
        # ヘッダーとフッターを除去
        content_start = 0
        content_end = len(lines)
        
        # ヘッダーを探す
        for i, line in enumerate(lines):
            if line.strip() == '-->' and i > 0:
                content_start = i + 2  # ヘッダーの後の空行をスキップ
                break
        
        # フッターを探す
        for i in range(len(lines) - 1, -1, -1):
            if lines[i].strip() == '<!--' and i < len(lines) - 1:
                # フッター開始位置の前の改行も除去
                content_end = i
                while content_end > 0 and lines[content_end - 1].strip() == '':
                    content_end -= 1
                break
        
        # コンテンツを追加
        merged_content.extend(lines[content_start:content_end])
    
    # 結合したコンテンツを書き出す
    with open(output_file, 'w', encoding='utf-8') as f:
        f.writelines(merged_content)
    
    print(f"チャンクを結合しました: {output_file}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="原稿ファイルの分割と結合")
    subparsers = parser.add_subparsers(dest='command', help='実行するコマンド')
    
    # 分割コマンド
    split_parser = subparsers.add_parser('split', help='ファイルをチャンクに分割')
    split_parser.add_argument('input_file', help='入力ファイルパス')
    split_parser.add_argument('-o', '--output-dir', default='proofreading_chunks', help='出力ディレクトリ')
    split_parser.add_argument('-s', '--chunk-size', type=int, default=250, help='チャンクサイズ（行数）')
    
    # 結合コマンド
    merge_parser = subparsers.add_parser('merge', help='チャンクを結合')
    merge_parser.add_argument('base_name', help='ファイルのベース名')
    merge_parser.add_argument('output_file', help='出力ファイルパス')
    merge_parser.add_argument('-d', '--chunks-dir', default='proofreading_chunks', help='チャンクディレクトリ')
    
    args = parser.parse_args()
    
    if args.command == 'split':
        split_manuscript(args.input_file, args.output_dir, args.chunk_size)
    elif args.command == 'merge':
        merge_chunks(args.chunks_dir, args.base_name, args.output_file)
    else:
        parser.print_help()