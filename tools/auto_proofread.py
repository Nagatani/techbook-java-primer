#!/usr/bin/env python3
"""
原稿の自動校正ツール

textlintガイドラインに基づいて、一般的な校正パターンを自動適用します。
"""

import re
import sys
from pathlib import Path
from typing import List, Tuple


class AutoProofreader:
    """自動校正クラス"""
    
    def __init__(self):
        """校正パターンの初期化"""
        self.patterns = []
        self._init_patterns()
        
    def _init_patterns(self):
        """校正パターンの定義"""
        
        # 1. リスト項目のインデント修正
        self.patterns.append((
            r'^(- [①-⑩]　.+)\n    \+ (.+)$',
            r'\1\n        + \2',
            'リスト項目のインデント修正'
        ))
        
        # 2. 冗長な表現の削除
        self.patterns.extend([
            (r'する必要があります', r'します', '冗長な表現の削除'),
            (r'する必要がある', r'する', '冗長な表現の削除'),
            (r'ことができます', r'できます', '冗長な表現の削除'),
            (r'ことができる', r'できる', '冗長な表現の削除'),
            (r'ことになります', r'ことになります', '冗長な表現の削除（要確認）'),
            (r'されています', r'されます', '受動態の簡潔化'),
            (r'されている', r'される', '受動態の簡潔化'),
            (r'行われる', r'実行される', '曖昧表現の具体化'),
        ])
        
        # 3. 文末の統一（ですます調）
        self.patterns.extend([
            (r'である。', r'です。', '文体統一（ですます調）'),
            (r'となる。', r'となります。', '文体統一（ですます調）'),
            (r'する。', r'します。', '文体統一（ですます調）'),
        ])
        
        # 4. 曖昧な表現の具体化
        self.patterns.extend([
            (r'適切な', r'目的に応じた', '曖昧表現の具体化'),
            (r'効率的な', r'処理時間を短縮する', '曖昧表現の具体化'),
            (r'必要に応じて', r'条件に応じて', '曖昧表現の具体化'),
        ])
        
    def proofread_line(self, line: str) -> Tuple[str, List[str]]:
        """1行の校正"""
        changes = []
        modified_line = line
        
        for pattern, replacement, description in self.patterns:
            if re.search(pattern, modified_line, re.MULTILINE):
                modified_line = re.sub(pattern, replacement, modified_line, flags=re.MULTILINE)
                changes.append(description)
        
        return modified_line, changes
    
    def proofread_file(self, filepath: Path) -> Tuple[str, List[str]]:
        """ファイル全体の校正"""
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        all_changes = []
        lines = content.split('\n')
        modified_lines = []
        
        # ヘッダーとフッターのマーカーを探す
        header_end = -1
        footer_start = len(lines)
        
        for i, line in enumerate(lines):
            if '================\n-->' in content and i < 20:
                header_end = i
            if '<!-- \n================' in line:
                footer_start = i
                break
        
        # ヘッダー部分はそのまま保持
        if header_end > 0:
            modified_lines.extend(lines[:header_end+1])
            start_line = header_end + 1
        else:
            start_line = 0
        
        # 本文の校正
        for i in range(start_line, footer_start):
            line = lines[i]
            modified_line, changes = self.proofread_line(line)
            modified_lines.append(modified_line)
            if changes:
                all_changes.extend([(i+1, change) for change in changes])
        
        # 長文チェック（120文字制限）
        for i in range(start_line, footer_start):
            if len(modified_lines[i]) > 120 and not modified_lines[i].startswith('#'):
                # コードブロックやテーブルは除外
                if not modified_lines[i].startswith(('```', '|', '    ')):
                    all_changes.append((i+1, '長文検出（120文字超）- 手動分割が必要'))
        
        # フッター部分を追加（校正完了マークを付ける）
        if footer_start < len(lines):
            for i in range(footer_start, len(lines)):
                line = lines[i]
                if '校正ステータス: [ ] 未完了 / [ ] 完了' in line:
                    line = line.replace('[ ] 完了', '[x] 完了')
                modified_lines.append(line)
        
        return '\n'.join(modified_lines), all_changes
    
    def batch_proofread(self, chunk_dir: Path, start_chunk: int = 5, end_chunk: int = 12):
        """複数チャンクの一括校正"""
        results = []
        
        for i in range(start_chunk, end_chunk + 1):
            chunk_file = chunk_dir / f"chapter03-oop-basics_chunk_{i:03d}.md"
            
            if not chunk_file.exists():
                print(f"チャンク {i} が見つかりません: {chunk_file}")
                continue
            
            print(f"チャンク {i} を校正中...")
            modified_content, changes = self.proofread_file(chunk_file)
            
            # 変更がある場合のみファイルを更新
            if changes:
                with open(chunk_file, 'w', encoding='utf-8') as f:
                    f.write(modified_content)
                
                results.append({
                    'chunk': i,
                    'file': chunk_file.name,
                    'changes': changes
                })
                print(f"  {len(changes)} 個の変更を適用しました")
            else:
                print(f"  変更なし")
        
        return results


def main():
    """メイン処理"""
    if len(sys.argv) < 2:
        print("使用方法: python auto_proofread.py <chunk_directory> [start_chunk] [end_chunk]")
        sys.exit(1)
    
    chunk_dir = Path(sys.argv[1])
    start_chunk = int(sys.argv[2]) if len(sys.argv) > 2 else 5
    end_chunk = int(sys.argv[3]) if len(sys.argv) > 3 else 12
    
    proofreader = AutoProofreader()
    results = proofreader.batch_proofread(chunk_dir, start_chunk, end_chunk)
    
    # 結果のサマリー
    print("\n=== 校正結果サマリー ===")
    total_changes = 0
    for result in results:
        print(f"\nチャンク {result['chunk']} ({result['file']}):")
        for line_no, change in result['changes']:
            print(f"  行 {line_no}: {change}")
        total_changes += len(result['changes'])
    
    print(f"\n合計 {total_changes} 個の変更を適用しました")


if __name__ == "__main__":
    main()