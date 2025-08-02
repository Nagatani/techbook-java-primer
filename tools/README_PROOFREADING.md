# 校正ツール群の使い方

## 概要
このディレクトリには、日本語技術書の校正を支援するツール群が含まれています。
大規模なMarkdownファイルを効率的に校正するための分割・管理・検証機能を提供します。

## ツール一覧

### 1. manuscript_proofreader.py
**目的**: 大規模ファイルの分割とマージ

**使用方法**:
```bash
# ファイルを分割
python3 tools/manuscript_proofreader.py manuscripts/target.md

# 校正後にマージ
python3 tools/manuscript_proofreader.py manuscripts/target.md --merge --output-dir ./proofreading_chunks
```

**特徴**:
- 250行を基準に自然な区切りで分割
- メタデータによるチャンク管理
- 見出しや空行を優先した分割

### 2. efficient_proofreading.py
**目的**: 校正作業の計画と進捗管理

**使用方法**:
```bash
# ファイル分析
python3 tools/efficient_proofreading.py analyze manuscripts/target.md

# 校正計画作成
python3 tools/efficient_proofreading.py plan manuscripts/target.md

# 進捗確認
python3 tools/efficient_proofreading.py progress

# レポート生成
python3 tools/efficient_proofreading.py report manuscripts/target.md
```

### 3. safe_auto_proofread.py
**目的**: 安全な自動校正（限定的使用）

**使用方法**:
```bash
python3 tools/safe_auto_proofread.py manuscripts/target.md
```

**安全な変換のみ**:
- 「することができます」→「できます」
- 「する必要があります」→「必要です」
- 文脈に依存しない明確な冗長表現のみ

### 4. fix_textlint_colons.py
**目的**: textlintのコロンエラー修正

**使用方法**:
```bash
python3 tools/fix_textlint_colons.py manuscripts/target.md
```

**修正パターン**:
- 「実行結果：」→ 見出し化
- 「〜の定義：」→ 見出し化

## ドキュメント

### proofreading_workflow.md
完全な校正作業フローを記載。新しいセッションでもこれを参照すれば、一貫した品質の校正が可能。

### proofreading_checklist.md
校正時の詳細なチェック項目。品質保証のための必須確認事項。

### proofreading_rules.md
プロジェクト固有の校正ルール。安全な変換と危険な変換の区別。

## クイックスタートガイド

### 小規模ファイル（500行未満）の場合
```bash
# 1. textlintチェック
npm run lint manuscripts/target.md

# 2. 直接編集
vim manuscripts/target.md

# 3. 検証
npm run lint manuscripts/target.md
```

### 大規模ファイル（500行以上）の場合
```bash
# 1. 分析と分割
python3 tools/efficient_proofreading.py analyze manuscripts/target.md
python3 tools/manuscript_proofreader.py manuscripts/target.md

# 2. チャンクごとに校正
# proofreading_chunks/内の各ファイルを編集

# 3. マージ
python3 tools/manuscript_proofreader.py manuscripts/target.md --merge --output-dir ./proofreading_chunks

# 4. 上書きと検証
cp proofreading_chunks/target_merged.md manuscripts/target.md
npm run lint manuscripts/target.md
```

## 注意事項

### 絶対にやってはいけないこと
1. 文脈を考慮しない機械的な置換
2. 「より」「から」「ため」などの助詞の一律変換
3. 技術用語の変更
4. 確認なしの自動校正の適用

### 推奨事項
1. 必ずバックアップを作成
2. 変更は段階的に実施
3. 音読で自然さを確認
4. git diffで全変更を確認

## トラブルシューティング

**Q: 校正後に不自然な日本語になった**
A: git checkoutで元に戻し、該当箇所を手動で修正

**Q: textlintエラーが解決しない**
A: 一部のエラーは技術文書として適切な場合があるため、無理に修正しない

**Q: チャンクのマージに失敗した**
A: メタデータファイルを確認し、全チャンクが存在することを確認

## 関連ファイル
- `/docs/writing-guidelines-textlint-compliance.md`: textlint準拠ガイドライン
- `/CLAUDE.md`: プロジェクト全体のガイドライン
- `/.textlintrc.json`: textlint設定ファイル