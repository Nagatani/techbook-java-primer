#!/usr/bin/env python3
"""
効率的な校正作業を支援するワークフロー管理ツール
"""
import json
import os
from pathlib import Path
from typing import Dict, List, Optional
import subprocess
from datetime import datetime

class EfficientProofreader:
    def __init__(self, base_dir: str = "."):
        self.base_dir = Path(base_dir)
        self.chunks_dir = self.base_dir / "proofreading_chunks"
        self.manuscripts_dir = self.base_dir / "manuscripts"
        self.progress_file = self.chunks_dir / "proofreading_progress.json"
        
    def analyze_manuscript(self, file_path: str) -> Dict:
        """原稿ファイルを分析して校正が必要か判定"""
        file_path = Path(file_path)
        
        if not file_path.exists():
            return {"error": f"File not found: {file_path}"}
            
        # ファイルサイズと行数を取得
        file_size = file_path.stat().st_size
        with open(file_path, 'r', encoding='utf-8') as f:
            lines = f.readlines()
            line_count = len(lines)
            
        # 分割が必要か判定
        needs_split = line_count > 500 or file_size > 20 * 1024  # 500行または20KB以上
        
        # textlintエラーをチェック
        lint_errors = self._check_textlint(str(file_path))
        
        return {
            "file": str(file_path),
            "line_count": line_count,
            "file_size": file_size,
            "needs_split": needs_split,
            "textlint_errors": lint_errors,
            "recommended_chunks": (line_count // 250) + 1 if needs_split else 1
        }
    
    def _check_textlint(self, file_path: str) -> int:
        """textlintでエラー数を確認"""
        try:
            result = subprocess.run(
                ["npm", "run", "lint", file_path],
                capture_output=True,
                text=True
            )
            # エラー数を出力から抽出（簡易的な実装）
            error_count = result.stdout.count("✘") + result.stderr.count("✘")
            return error_count
        except:
            return -1  # エラーチェック失敗
    
    def create_proofreading_plan(self, file_path: str) -> Dict:
        """校正計画を作成"""
        analysis = self.analyze_manuscript(file_path)
        
        if "error" in analysis:
            return analysis
            
        plan = {
            "file": analysis["file"],
            "created_at": datetime.now().isoformat(),
            "analysis": analysis,
            "workflow": []
        }
        
        if analysis["needs_split"]:
            plan["workflow"] = [
                {"step": 1, "action": "split", "command": f"python3 tools/manuscript_proofreader.py split {file_path}"},
                {"step": 2, "action": "proofread_chunks", "estimated_time": f"{analysis['recommended_chunks'] * 10} minutes"},
                {"step": 3, "action": "merge", "command": f"python3 tools/manuscript_proofreader.py merge proofreading_chunks/{Path(file_path).stem}"},
                {"step": 4, "action": "validate", "command": f"npm run lint {file_path}"}
            ]
        else:
            plan["workflow"] = [
                {"step": 1, "action": "proofread_direct", "estimated_time": "10 minutes"},
                {"step": 2, "action": "validate", "command": f"npm run lint {file_path}"}
            ]
        
        # 計画を保存
        plan_file = self.chunks_dir / f"{Path(file_path).stem}_plan.json"
        self.chunks_dir.mkdir(exist_ok=True)
        with open(plan_file, 'w', encoding='utf-8') as f:
            json.dump(plan, f, ensure_ascii=False, indent=2)
            
        return plan
    
    def track_progress(self, chunk_file: str, status: str, changes: List[Dict] = None):
        """チャンクごとの進捗を記録"""
        progress = {}
        
        # 既存の進捗を読み込み
        if self.progress_file.exists():
            with open(self.progress_file, 'r', encoding='utf-8') as f:
                progress = json.load(f)
        
        # 進捗を更新
        progress[chunk_file] = {
            "status": status,
            "updated_at": datetime.now().isoformat(),
            "changes": changes or []
        }
        
        # 保存
        with open(self.progress_file, 'w', encoding='utf-8') as f:
            json.dump(progress, f, ensure_ascii=False, indent=2)
    
    def get_progress_summary(self) -> Dict:
        """全体の進捗状況をサマリー"""
        if not self.progress_file.exists():
            return {"error": "No progress tracked yet"}
            
        with open(self.progress_file, 'r', encoding='utf-8') as f:
            progress = json.load(f)
            
        total_chunks = len(progress)
        completed = sum(1 for p in progress.values() if p["status"] == "completed")
        in_progress = sum(1 for p in progress.values() if p["status"] == "in_progress")
        pending = total_chunks - completed - in_progress
        
        return {
            "total_chunks": total_chunks,
            "completed": completed,
            "in_progress": in_progress,
            "pending": pending,
            "completion_rate": f"{(completed / total_chunks * 100):.1f}%" if total_chunks > 0 else "0%"
        }
    
    def generate_proofreading_report(self, original_file: str) -> str:
        """校正レポートを生成"""
        report_lines = [
            f"# 校正レポート: {Path(original_file).name}",
            f"生成日時: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}",
            "",
            "## 進捗状況",
        ]
        
        summary = self.get_progress_summary()
        if "error" not in summary:
            report_lines.extend([
                f"- 総チャンク数: {summary['total_chunks']}",
                f"- 完了: {summary['completed']}",
                f"- 進行中: {summary['in_progress']}",
                f"- 未着手: {summary['pending']}",
                f"- 完了率: {summary['completion_rate']}",
                ""
            ])
        
        # 変更内容のサマリー
        if self.progress_file.exists():
            with open(self.progress_file, 'r', encoding='utf-8') as f:
                progress = json.load(f)
                
            report_lines.extend(["## 主な変更内容", ""])
            
            change_types = {}
            for chunk_data in progress.values():
                for change in chunk_data.get("changes", []):
                    reason = change.get("reason", "その他")
                    change_types[reason] = change_types.get(reason, 0) + 1
            
            for reason, count in sorted(change_types.items(), key=lambda x: x[1], reverse=True):
                report_lines.append(f"- {reason}: {count}件")
        
        return "\n".join(report_lines)

def main():
    """CLI インターフェース"""
    import argparse
    
    parser = argparse.ArgumentParser(description="効率的な校正作業支援ツール")
    parser.add_argument("command", choices=["analyze", "plan", "progress", "report"],
                       help="実行するコマンド")
    parser.add_argument("file", nargs="?", help="対象ファイル")
    parser.add_argument("--chunk", help="チャンクファイル（progressコマンド用）")
    parser.add_argument("--status", help="ステータス（progressコマンド用）")
    
    args = parser.parse_args()
    proofreader = EfficientProofreader()
    
    if args.command == "analyze":
        if not args.file:
            print("Error: ファイルを指定してください")
            return
        result = proofreader.analyze_manuscript(args.file)
        print(json.dumps(result, ensure_ascii=False, indent=2))
        
    elif args.command == "plan":
        if not args.file:
            print("Error: ファイルを指定してください")
            return
        plan = proofreader.create_proofreading_plan(args.file)
        print(json.dumps(plan, ensure_ascii=False, indent=2))
        
    elif args.command == "progress":
        if args.chunk and args.status:
            proofreader.track_progress(args.chunk, args.status)
            print(f"Progress updated for {args.chunk}")
        else:
            summary = proofreader.get_progress_summary()
            print(json.dumps(summary, ensure_ascii=False, indent=2))
            
    elif args.command == "report":
        if not args.file:
            print("Error: ファイルを指定してください")
            return
        report = proofreader.generate_proofreading_report(args.file)
        print(report)

if __name__ == "__main__":
    main()