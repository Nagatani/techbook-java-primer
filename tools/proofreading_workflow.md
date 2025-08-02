# 日本語技術書の校正ワークフロー

## 概要
このドキュメントは、大規模な日本語技術書の校正作業を効率的かつ正確に行うための標準手順を定めたものです。

## 前提条件
- 対象：日本語技術書（特にプログラミング教科書）
- ツール：textlint、Python校正ツール群
- 原則：自然な日本語表現を重視し、機械的な置換は避ける

## 校正作業の全体フロー

### Phase 1: 事前準備と分析

#### 1.1 プロジェクト状況の確認
```bash
# textlintエラーの確認
npm run lint manuscripts/target_file.md

# ファイルサイズの確認
wc -l manuscripts/target_file.md
ls -lh manuscripts/target_file.md
```

#### 1.2 大規模ファイルの分割判定
```bash
# 効率的な校正計画の作成
python3 tools/efficient_proofreading.py analyze manuscripts/target_file.md
```

判定基準：
- 500行以上 → 分割推奨
- 20KB以上 → 分割推奨
- 1000行以上 → 分割必須

### Phase 2: ファイル分割（必要な場合）

#### 2.1 分割の実行
```bash
# 新しい分割ツール（推奨）- 無駄な改行を防ぐ
python3 tools/split_manuscript_properly.py split manuscripts/target_file.md

# または従来のツール
python3 tools/manuscript_proofreader.py manuscripts/target_file.md
```

**重要**: `split_manuscript_properly.py`を使用することで、結合時にコードブロック内や文章中に無駄な改行が入る問題を防げます。

#### 2.2 分割結果の確認
- メタデータファイルで分割状況を確認
- 各チャンクのサイズと範囲を把握

### Phase 3: 校正作業の実施

#### 3.1 校正の優先順位

**高優先度（安全な修正）**
1. 明らかな冗長表現
   - 「することができます」→「できます」
   - 「することが必要です」→「必要です」
   - 「されることになります」→「されます」

2. textlintエラーの修正
   - コロンパターン：「実行結果：」→ 見出し化
   - リスト文体：である調への統一

**中優先度（文脈確認必要）**
3. 曖昧表現の具体化
   - 「適切な」→ 具体的な基準を明示
   - 「効率的な」→ 何がどう効率的かを明示

**低優先度（慎重に対応）**
4. 助詞の変更（特に注意）
   - 「より」「から」「ため」などは文脈依存
   - 機械的な置換は禁止

#### 3.2 校正の実施方法

**手動校正（推奨）**
```bash
# チャンクごとに読み込んで校正
vim proofreading_chunks/target_file_chunk_001.md

# MultiEditツールで複数箇所を一括修正
# ただし、各修正は個別に検討すること
```

**自動校正（限定的使用）**
```bash
# 安全な変換のみを実行
python3 tools/safe_auto_proofread.py target_file.md

# 結果を必ず確認してから適用
```

### Phase 4: 品質確認

#### 4.1 変更内容の検証

**必須確認項目**
1. 日本語として自然か
2. 意味が変わっていないか
3. 技術的正確性が保たれているか
4. 前後の文脈と整合性があるか

**確認方法**
```bash
# 変更箇所の差分確認
git diff --word-diff manuscripts/target_file.md

# 音読テスト（重要）
# 変更後の文を声に出して読み、違和感がないか確認
```

#### 4.2 よくある失敗パターンと対策

**失敗例1：機械的な助詞の置換**
- ❌ 「より高度な」→「から高度な」
- ❌ 「継承により」→「継承にから」
- ✅ 対策：助詞は文脈を理解してから変更

**失敗例2：文法的に誤った簡潔化**
- ❌ 「保つことができます」→「保つできます」
- ✅ 正解：「保つことができます」→「保てます」

### Phase 5: マージと最終確認

#### 5.1 チャンクのマージ（分割した場合）
```bash
# 新しいマージツール（推奨）- 無駄な改行なし
python3 tools/split_manuscript_properly.py merge target_file manuscripts/target_file_merged.md

# または従来のツール
python3 tools/manuscript_proofreader.py manuscripts/target_file.md --merge --output-dir ./proofreading_chunks

# 元ファイルに上書き
cp manuscripts/target_file_merged.md manuscripts/target_file.md
```

#### 5.2 textlint最終検証
```bash
# エラーが減少していることを確認
npm run lint manuscripts/target_file.md
```

### Phase 6: レポート作成

#### 6.1 校正レポートの生成
```bash
python3 tools/efficient_proofreading.py report manuscripts/target_file.md
```

#### 6.2 レポートに含めるべき内容
- 修正項目数と種類
- 主な改善点
- 残存するtextlintエラー
- 今後の推奨事項

## チェックリスト

### 校正前チェックリスト
- [ ] プロジェクトのガイドライン（CLAUDE.md）を確認
- [ ] textlintの現在のエラー数を記録
- [ ] ファイルサイズを確認し、分割の必要性を判断
- [ ] バックアップを作成

### 校正中チェックリスト
- [ ] 各変更の妥当性を個別に確認
- [ ] 助詞の変更は特に慎重に
- [ ] 技術用語は変更しない
- [ ] コードブロック内は校正対象外

### 校正後チェックリスト
- [ ] 全変更箇所をgit diffで確認
- [ ] 音読して自然さを確認
- [ ] textlintで改善を確認
- [ ] レポートを作成

## 利用可能なツール

### 1. manuscript_proofreader.py
- 大規模ファイルの分割・マージ
- チャンク管理とメタデータ追跡

### 2. efficient_proofreading.py
- 校正計画の作成
- 進捗管理
- レポート生成

### 3. safe_auto_proofread.py
- 安全な自動校正（限定的）
- 危険な変換を除外
- 確認後に適用

### 4. proofreading_checklist.md
- 詳細な確認項目
- 品質保証プロセス

## 重要な注意事項

### やってはいけないこと
1. **文脈を考慮しない機械的置換**
2. **助詞の一律変換**
3. **技術用語の変更**
4. **コードブロック内の修正**
5. **確認なしの自動校正適用**

### 必ずやるべきこと
1. **変更前後の文脈確認**
2. **音読による自然さの確認**
3. **技術的正確性の維持**
4. **バックアップの作成**
5. **段階的な修正と確認**

## トラブルシューティング

### Q: 自動校正で不自然な変換が発生した
A: 即座に元に戻し、手動で個別に修正する

### Q: textlintエラーが減らない
A: 一部のエラーは技術文書として適切な場合がある。無理に修正しない

### Q: 大きなファイルで作業が困難
A: 必ず分割ツールを使用し、チャンクごとに作業する

## 次回セッション用クイックスタート

```bash
# 1. 対象ファイルの分析
python3 tools/efficient_proofreading.py analyze manuscripts/chapter_XX.md

# 2. 分割が必要な場合
python3 tools/manuscript_proofreader.py manuscripts/chapter_XX.md

# 3. 校正実施
# - 手動で各チャンクを確認・修正
# - または安全な自動校正を適用

# 4. マージと検証
python3 tools/manuscript_proofreader.py manuscripts/chapter_XX.md --merge --output-dir ./proofreading_chunks
npm run lint manuscripts/chapter_XX.md

# 5. レポート作成
python3 tools/efficient_proofreading.py report manuscripts/chapter_XX.md
```