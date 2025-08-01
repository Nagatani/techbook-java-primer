# CLAUDE.mdルール準拠性チェックガイド

このドキュメントでは、CLAUDE.mdルール違反を防止し、品質を維持するためのツールとワークフローについて説明します。

## 概要

技術書「Java入門」では、一貫した文章品質を保つためにCLAUDE.mdで定義されたルールに従って執筆しています。これらのルールは、textlintでは検出できない書式的な違反を防止し、読みやすく自然な日本語文章を維持するために設計されています。

## 主要なルール

### 1. 強調表記（太字）の制限
- ❌ `**重要**`のような太字を見出しとして使用
- ❌ `- **保守性の低下**：` のようなリスト項目での強調とコロンの組み合わせ
- ✅ 適切なMarkdown見出し記法（`### 重要なポイント`）
- ✅ コードリスト番号（`**サンプルコード5-1**`）は例外として許可

### 2. コロン（：）使用の制限
- ❌ `以下のような問題があります：`
- ❌ `次の技術を学習します：`
- ✅ `以下のような問題があります。主な問題は次の通りです。`
- ✅ `次の技術を学習します。具体的な内容は以下の通りです。`

### 3. 文体統一
- 本文：ですます調で統一
- リスト項目：である調を使用（意図的な使い分け）
- 脚注（`<span class="footnote">`）：ですます調（例外なし）

## ツールの使用方法

### 自動チェック

```bash
# 全ファイルのルール準拠性をチェック
npm run check:compliance

# 特定のディレクトリをチェック
node scripts/check-claude-md-compliance.js manuscripts

# JSON形式でレポート出力
node scripts/check-claude-md-compliance.js --json --output report.json
```

### 自動修正

```bash
# 自動修正可能な違反を修正
npm run fix:compliance

# 修正後の検証をスキップ
node scripts/fix-claude-md-violations.js --skip-verify

# 古いバックアップファイルをクリーンアップ
node scripts/fix-claude-md-violations.js --cleanup
```

### 統合チェック

```bash
# CLAUDE.mdルール + textlint の組み合わせチェック
npm run pre-commit
```

## ワークフロー

### 日常的な作業

1. **執筆前の確認**
   ```bash
   npm run check:compliance
   ```

2. **執筆後の検証**
   ```bash
   npm run pre-commit
   ```

3. **修正が必要な場合**
   ```bash
   npm run fix:compliance
   git diff  # 変更内容を確認
   npm run lint  # textlintで最終確認
   ```

### コミット前の必須チェック

```bash
# 1. CLAUDE.mdルール準拠性チェック
npm run check:compliance

# 2. textlintによる文章校正
npm run lint

# 3. 問題がなければコミット
git add .
git commit -m "..."
```

## チェック内容の詳細

### エラーレベル（修正必須）

1. **bold-colon-list**: リスト項目での強調とコロンの組み合わせ
2. **colon-continuation**: コロンで終わるリスト導入文
3. **bold-as-heading**: 強調表記を見出しとして使用
4. **incorrect-code-listing**: 「リスト」形式のコードリスト番号

### 警告レベル（推奨修正）

1. **bold-info-prefix**: 太字の情報プレフィックス（**注意**、**重要**など）
2. **list-colon-ending**: リスト項目の末尾コロン使用

## 自動修正の対象

以下のパターンは自動修正されます：

```markdown
# 修正前 → 修正後
以下のような問題があります： → 以下のような問題があります。
**リスト5-1** → **サンプルコード5-1**
- **保守性の低下**：同じロジック... → （手動修正推奨）
```

## 手動修正が必要なパターン

複雑な構文や文脈に依存する違反は手動修正が必要です：

1. **リスト項目の強調表記**
   ```markdown
   # 修正前
   - **保守性の低下**：同じロジックが複数箇所に散在する
   
   # 修正後
   - 保守性の低下により、同じロジックが複数箇所に散在する
   ```

2. **見出しとしての強調表記**
   ```markdown
   # 修正前
   **重要なポイント**
   
   # 修正後
   ### 重要なポイント
   ```

## トラブルシューティング

### よくある問題

1. **誤検出される場合**
   - 用語集（glossary.md）の項目は除外対象
   - コードブロック内は除外対象
   - 引用（`>`）内の**注意**などは許可

2. **修正が反映されない場合**
   ```bash
   # キャッシュクリア
   rm -rf node_modules/.cache
   
   # 再チェック
   npm run check:compliance
   ```

3. **バックアップファイルが蓄積する場合**
   ```bash
   # 7日以上古いバックアップを削除
   node scripts/fix-claude-md-violations.js --cleanup
   ```

## CI/CD統合

### GitHub Actions例

```yaml
name: Quality Check
on: [push, pull_request]
jobs:
  quality:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - run: npm install
      - name: CLAUDE.md Compliance Check
        run: npm run check:compliance
      - name: Textlint Check
        run: npm run lint
```

### pre-commitフック

```bash
# .git/hooks/pre-commit
#!/bin/sh
npm run pre-commit
```

## 開発・メンテナンス

### ルールの追加・修正

新しいルールを追加する場合は、`scripts/check-claude-md-compliance.js`の`VIOLATION_PATTERNS`配列に追加してください：

```javascript
{
    id: 'new-rule',
    pattern: /パターン/g,
    description: 'ルールの説明',
    severity: 'error', // または 'warning'
    suggestion: '修正提案'
}
```

### テスト

```bash
# テスト用のMarkdownファイルでチェック
echo "以下の問題があります：" > test.md
node scripts/check-claude-md-compliance.js test.md
rm test.md
```

## 関連ドキュメント

- [CLAUDE.md](../CLAUDE.md) - プロジェクト全体のルール
- [writing-guidelines-textlint-compliance.md](./writing-guidelines-textlint-compliance.md) - textlint対応ガイドライン
- [manuscript-modification-protocol.md](./manuscript-modification-protocol.md) - 原稿修正プロトコル

---

このガイドにより、CLAUDE.mdルールに準拠した高品質な技術書の執筆・保守が可能になります。