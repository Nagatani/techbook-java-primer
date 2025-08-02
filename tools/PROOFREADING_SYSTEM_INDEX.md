# 校正システムインデックス

このファイルは、校正作業に必要なすべてのファイルとその役割を明確にするインデックスです。

## 必須ファイル一覧と役割

### 1. メインワークフロー
- **`proofreading_workflow.md`** - 校正作業の詳細な手順とフェーズ管理
  - Phase 1-6の詳細手順
  - チェックリスト
  - 失敗パターンと対策

### 2. 具体的なルール定義
- **`proofreading_rules.md`** - 分割基準と校正ルールの詳細
  - チャンクサイズ（250-300行）
  - 安全な変換パターン
  - 文脈依存の変換パターン

### 3. 品質保証
- **`proofreading_checklist.md`** - 必須確認項目と除外パターン
  - 自動変換してはいけないパターン
  - 変更後の確認項目
  - 品質保証プロセス

### 4. 実装ツール

#### 基本ツール
- **`manuscript_proofreader.py`** - ファイル分割・マージ
- **`efficient_proofreading.py`** - 分析・レポート生成

#### 自動修正ツール
- **`fix_textlint_colons.py`** - コロンパターンの見出し化
  - 「実行結果：」→「#### 実行結果」
  - パターンマッチングによる自動検出
- **`safe_auto_proofread.py`** - 安全な自動校正
  - 冗長表現の修正
  - 危険なパターンの除外

#### 補助ツール
- **`auto_proofread.py`** - 基本的な自動校正
- **`chapter04_auto_proofread.py`** - 章固有の校正ルール

### 5. 計画・サマリー
- **`textlint_fixes_plan.md`** - textlintエラーの修正計画
- **`textlint_fix_summary.md`** - 修正結果のサマリー

### 6. ドキュメント
- **`PROOFREADING_INSTRUCTIONS.md`** - 全体的な指示
- **`README_PROOFREADING.md`** - 校正システムの概要

## 新しいセッションでの使用方法

### 基本的な指示
```
以下のファイルに従って第○章の校正を実施してください：
1. tools/proofreading_workflow.md（メイン手順）
2. tools/proofreading_rules.md（詳細ルール）
3. tools/proofreading_checklist.md（品質保証）

特に重要：
- コロンパターンは tools/fix_textlint_colons.py を使用して見出し化
- 冗長表現は tools/safe_auto_proofread.py で安全に修正
```

### 具体的な作業手順
```bash
# 1. 分析
python3 tools/efficient_proofreading.py analyze manuscripts/chapterXX.md

# 2. 分割（必要な場合）
python3 tools/manuscript_proofreader.py manuscripts/chapterXX.md

# 3. コロンパターンの修正
python3 tools/fix_textlint_colons.py manuscripts/chapterXX.md

# 4. 安全な自動校正
python3 tools/safe_auto_proofread.py manuscripts/chapterXX.md

# 5. 手動校正（チェックリストに従う）

# 6. マージと検証
python3 tools/manuscript_proofreader.py manuscripts/chapterXX.md --merge --output-dir ./proofreading_chunks
```

## 重要な原則

1. **ファイルを削除しない** - すべてのファイルには役割がある
2. **参照関係を維持** - 各ファイルは相互に参照される
3. **段階的なアプローチ** - 自動→確認→手動の順序を守る
4. **安全性を優先** - 不確実な変更は行わない

## エラー防止策

### やってはいけないこと
- ファイルの統合や削除
- 自動修正結果の無確認適用
- コンテキストを無視した一括置換

### 必ずやること
- すべての関連ファイルを確認
- 変更前後の差分確認
- 音読による自然さの確認
- チェックリストの完全実施