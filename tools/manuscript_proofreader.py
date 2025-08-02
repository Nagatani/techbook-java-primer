#!/usr/bin/env python3
"""
大きな原稿ファイルを適切なサイズに分割して校正作業を行うツール

使用方法:
    python manuscript_proofreader.py <manuscript_file> [options]

オプション:
    --chunk-lines: 1チャンクあたりの行数（デフォルト: 200）
    --output-dir: 分割ファイルの出力ディレクトリ（デフォルト: ./proofreading_chunks/）
    --merge: 校正済みファイルをマージ
"""

import os
import sys
import argparse
from pathlib import Path
import re
from typing import List, Tuple
import json
from datetime import datetime


class ManuscriptProofreader:
    """原稿校正のための分割・管理クラス"""
    
    def __init__(self, chunk_lines: int = 200):
        """
        Args:
            chunk_lines: 1チャンクあたりの最大行数
        """
        self.chunk_lines = chunk_lines
        self.metadata = {
            "created_at": datetime.now().isoformat(),
            "chunks": []
        }
        
    def split_manuscript(self, filepath: Path, output_dir: Path) -> List[Path]:
        """
        原稿ファイルを適切なサイズに分割
        
        Args:
            filepath: 原稿ファイルのパス
            output_dir: 出力ディレクトリ
            
        Returns:
            分割されたファイルのパスリスト
        """
        # 出力ディレクトリの作成
        output_dir.mkdir(parents=True, exist_ok=True)
        
        # 原稿を読み込み
        with open(filepath, 'r', encoding='utf-8') as f:
            lines = f.readlines()
        
        print(f"原稿ファイル: {filepath}")
        print(f"総行数: {len(lines)}")
        
        # メタデータの初期化
        self.metadata["original_file"] = str(filepath)
        self.metadata["total_lines"] = len(lines)
        
        chunks = self._smart_split(lines)
        chunk_files = []
        
        for i, (start_line, chunk) in enumerate(chunks, 1):
            # チャンクファイル名
            chunk_filename = f"{filepath.stem}_chunk_{i:03d}.md"
            chunk_path = output_dir / chunk_filename
            
            # チャンクの情報を追加
            chunk_info = {
                "filename": chunk_filename,
                "chunk_number": i,
                "start_line": start_line + 1,  # 1-indexed
                "end_line": start_line + len(chunk),
                "lines": len(chunk),
                "first_content": chunk[0][:50].strip() if chunk else "",
                "status": "pending"  # pending, in_progress, completed
            }
            
            # ヘッダー情報を追加
            header = self._create_chunk_header(filepath, i, len(chunks), chunk_info)
            
            # チャンクファイルを書き出し
            with open(chunk_path, 'w', encoding='utf-8') as f:
                f.write(header)
                f.writelines(chunk)
                f.write(self._create_chunk_footer(i, len(chunks)))
            
            chunk_files.append(chunk_path)
            self.metadata["chunks"].append(chunk_info)
            
            print(f"  チャンク {i:3d}: {chunk_info['lines']:4d} 行 "
                  f"(行 {chunk_info['start_line']:5d} - {chunk_info['end_line']:5d}) "
                  f"-> {chunk_filename}")
        
        # メタデータを保存
        metadata_path = output_dir / f"{filepath.stem}_metadata.json"
        with open(metadata_path, 'w', encoding='utf-8') as f:
            json.dump(self.metadata, f, ensure_ascii=False, indent=2)
        
        print(f"\n分割完了: {len(chunks)} 個のチャンクを作成しました")
        print(f"メタデータ: {metadata_path}")
        
        return chunk_files
    
    def _smart_split(self, lines: List[str]) -> List[Tuple[int, List[str]]]:
        """
        文章の区切りを考慮した賢い分割
        
        Args:
            lines: 原稿の行リスト
            
        Returns:
            (開始行番号, チャンク行リスト) のリスト
        """
        chunks = []
        current_chunk = []
        current_start = 0
        
        for i, line in enumerate(lines):
            current_chunk.append(line)
            
            # チャンクサイズに達した場合
            if len(current_chunk) >= self.chunk_lines:
                # 適切な区切り位置を探す
                split_point = self._find_split_point(current_chunk)
                
                if split_point > 0:
                    # 区切り位置で分割
                    chunks.append((current_start, current_chunk[:split_point]))
                    current_chunk = current_chunk[split_point:]
                    current_start = current_start + split_point
                else:
                    # 区切りが見つからない場合は強制分割
                    chunks.append((current_start, current_chunk))
                    current_chunk = []
                    current_start = i + 1
        
        # 残りのチャンクを追加
        if current_chunk:
            chunks.append((current_start, current_chunk))
        
        return chunks
    
    def _find_split_point(self, chunk: List[str]) -> int:
        """
        チャンクの適切な分割位置を見つける
        
        優先順位:
        1. 見出し（#）の前
        2. 空行
        3. 段落の終わり（。で終わる行）
        
        Args:
            chunk: チャンクの行リスト
            
        Returns:
            分割位置のインデックス（見つからない場合は-1）
        """
        # 後ろから探す（チャンクの最後の方で区切りたい）
        search_start = max(0, len(chunk) - 50)  # 最後の50行を探索
        
        # 見出しを探す
        for i in range(len(chunk) - 1, search_start, -1):
            if chunk[i].strip().startswith('#'):
                return i
        
        # 空行を探す
        for i in range(len(chunk) - 1, search_start, -1):
            if chunk[i].strip() == '' and i < len(chunk) - 1:
                return i + 1
        
        # 段落の終わりを探す
        for i in range(len(chunk) - 1, search_start, -1):
            if chunk[i].strip().endswith('。'):
                return i + 1
        
        return -1
    
    def _create_chunk_header(self, filepath: Path, chunk_num: int, 
                           total_chunks: int, chunk_info: dict) -> str:
        """チャンクファイルのヘッダーを作成"""
        header = f"""<!-- 
校正チャンク情報
================
元ファイル: {filepath.name}
チャンク: {chunk_num}/{total_chunks}
行範囲: {chunk_info['start_line']} - {chunk_info['end_line']}
作成日時: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

"""
        return header
    
    def _create_chunk_footer(self, chunk_num: int, total_chunks: int) -> str:
        """チャンクファイルのフッターを作成"""
        footer = f"""

<!-- 
================
チャンク {chunk_num}/{total_chunks} の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
"""
        return footer
    
    def merge_chunks(self, output_dir: Path, original_file: Path) -> Path:
        """
        校正済みチャンクをマージして元のファイル形式に戻す
        
        Args:
            output_dir: チャンクファイルのディレクトリ
            original_file: 元ファイルのパス
            
        Returns:
            マージされたファイルのパス
        """
        # メタデータを読み込み
        metadata_path = output_dir / f"{original_file.stem}_metadata.json"
        with open(metadata_path, 'r', encoding='utf-8') as f:
            metadata = json.load(f)
        
        merged_lines = []
        
        # チャンクを順番に読み込んでマージ
        for chunk_info in metadata["chunks"]:
            chunk_path = output_dir / chunk_info["filename"]
            
            with open(chunk_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # ヘッダーとフッターを除去
            content = self._remove_chunk_markers(content)
            merged_lines.append(content)
        
        # マージしたファイルを保存
        merged_path = output_dir / f"{original_file.stem}_merged.md"
        with open(merged_path, 'w', encoding='utf-8') as f:
            f.write(''.join(merged_lines))
        
        print(f"マージ完了: {merged_path}")
        return merged_path
    
    def _remove_chunk_markers(self, content: str) -> str:
        """チャンクのヘッダーとフッターを除去"""
        # ヘッダーの除去
        header_pattern = r'^<!--\s*\n校正チャンク情報.*?-->\n\n'
        content = re.sub(header_pattern, '', content, flags=re.MULTILINE | re.DOTALL)
        
        # フッターの除去
        footer_pattern = r'\n\n<!--\s*\n=+\nチャンク.*?-->\s*$'
        content = re.sub(footer_pattern, '', content, flags=re.MULTILINE | re.DOTALL)
        
        return content
    
    def get_status_report(self, output_dir: Path, original_file: Path) -> str:
        """校正の進捗状況をレポート"""
        metadata_path = output_dir / f"{original_file.stem}_metadata.json"
        with open(metadata_path, 'r', encoding='utf-8') as f:
            metadata = json.load(f)
        
        total_chunks = len(metadata["chunks"])
        completed = sum(1 for chunk in metadata["chunks"] if chunk["status"] == "completed")
        in_progress = sum(1 for chunk in metadata["chunks"] if chunk["status"] == "in_progress")
        pending = sum(1 for chunk in metadata["chunks"] if chunk["status"] == "pending")
        
        report = f"""
校正進捗レポート
==============
元ファイル: {metadata['original_file']}
総行数: {metadata['total_lines']}
チャンク数: {total_chunks}

進捗状況:
  完了: {completed}/{total_chunks} ({completed/total_chunks*100:.1f}%)
  作業中: {in_progress}
  未着手: {pending}

チャンク詳細:
"""
        
        for chunk in metadata["chunks"]:
            status_mark = "✓" if chunk["status"] == "completed" else "○" if chunk["status"] == "pending" else "..."
            report += f"  [{status_mark}] チャンク {chunk['chunk_number']:3d}: "
            report += f"行 {chunk['start_line']:5d}-{chunk['end_line']:5d} "
            report += f"({chunk['lines']:3d}行) - {chunk['filename']}\n"
        
        return report


def main():
    parser = argparse.ArgumentParser(
        description='大きな原稿ファイルを分割して校正作業を支援するツール'
    )
    parser.add_argument('manuscript', type=str, help='原稿ファイルのパス')
    parser.add_argument('--chunk-lines', type=int, default=200,
                       help='1チャンクあたりの行数（デフォルト: 200）')
    parser.add_argument('--output-dir', type=str, 
                       default='./proofreading_chunks',
                       help='出力ディレクトリ（デフォルト: ./proofreading_chunks/）')
    parser.add_argument('--merge', action='store_true',
                       help='校正済みファイルをマージ')
    parser.add_argument('--status', action='store_true',
                       help='校正の進捗状況を表示')
    
    args = parser.parse_args()
    
    manuscript_path = Path(args.manuscript)
    output_dir = Path(args.output_dir)
    
    if not manuscript_path.exists():
        print(f"エラー: ファイルが見つかりません: {manuscript_path}")
        sys.exit(1)
    
    proofreader = ManuscriptProofreader(chunk_lines=args.chunk_lines)
    
    if args.merge:
        # チャンクをマージ
        proofreader.merge_chunks(output_dir, manuscript_path)
    elif args.status:
        # ステータスレポートを表示
        report = proofreader.get_status_report(output_dir, manuscript_path)
        print(report)
    else:
        # 原稿を分割
        chunk_files = proofreader.split_manuscript(manuscript_path, output_dir)
        print(f"\n次のステップ:")
        print(f"1. {output_dir} 内の各チャンクファイルを順番に校正してください")
        print(f"2. 校正が完了したら、以下のコマンドでマージできます:")
        print(f"   python {sys.argv[0]} {args.manuscript} --merge --output-dir {args.output_dir}")


if __name__ == "__main__":
    main()