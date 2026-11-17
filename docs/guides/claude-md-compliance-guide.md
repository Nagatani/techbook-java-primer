# CLAUDE.mdルール準拠性チェックガイド

このドキュメントでは、CLAUDE.mdの表記ルールへの違反を検出する`npm run check:compliance`と、その使い方を説明します（2026-11-16更新）。

## 概要

技術書「Java入門」では、一貫した文章品質を保つためにCLAUDE.mdで定義されたルールに従って執筆しています。
`check:compliance`（`scripts/check-claude-md-compliance.js`）は、textlintでは検出できない書式の違反を検出します。
共有のpre-commitフック（`scripts/git-hooks/pre-commit`）が`check:structure`とあわせて実行し、エラーがあるとコミットを止めます。
GitHub ActionsのCI（`.github/workflows/check.yml`）では実行していません。

## 検査する内容

検査の定義は、スクリプトの`VIOLATION_PATTERNS`にあります。

### エラー（コミットが止まる）

- `bold-colon-list`
    + リスト項目での強調とコロンの組み合わせ（`- **保守性の低下**：…`）
- `colon-continuation`
    + 「以下〜：」「次〜：」「下記〜：」で終わるリスト導入文
- `bold-as-heading`
    + 強調表記（`**…**`だけの行）を見出しとして使う
- `incorrect-code-listing`
    + 「リスト5-1」形式のコード番号（「サンプルコード5-1」にする）
- `title-colon-list`
    + 「項目タイトル： 説明文」形式のリスト項目（括弧内のコロンは除く）
- `trailing-colon-structure-break`
    + リスト項目の末尾のコロン
- `kome-note`
    + 「※」の注意書き（脚注か本文の文にする）
- `external-markdown-link`
    + 外部URLへのMarkdownのリンク（PDFでクリックできない脚注になる。URLは脚注にインラインコードで書く）

### 警告（コミットは止まらない）

- `bold-info-prefix`
    + 太字の情報プレフィックス（**注意**、**重要**など）
- `list-colon-ending`
    + リスト項目の末尾のコロン
- `excessive-bold-in-lists`
    + リスト項目の先頭の強調
- `list-dash-structure`
    + 「項目 - 説明」形式のリスト項目

コードブロック、HTMLコメント、引用（`>`）内の強調、用語集の「- **用語** - 第N章」の行は除外します。
用語集（`manuscripts/glossary.md`）は形式上、警告が多数出ますが、問題にしていません。

## ツールの使用方法

### 検査

```bash
# manuscripts/の全ファイルを検査する
npm run check:compliance

# ディレクトリや1つのファイルを指定して検査する（1つ目の引数が対象）
node scripts/check-claude-md-compliance.js manuscripts/chapter05-inheritance-and-polymorphism.md

# JSON形式でレポートを出力する（対象を先に書く。出力先はスクラッチ用のディレクトリにする）
node scripts/check-claude-md-compliance.js manuscripts --json --output /path/to/scratch/report.json
```

対象を省略して`--json`から書くと、`--json`が対象として扱われ、1つのファイルも検査されません。

### 自動修正（`fix:compliance`）

`npm run fix:compliance`（`scripts/fix-claude-md-violations.js`）は、リスト導入文のコロンなどを正規表現で書き換えます。
過去に正規表現による一括置換が文言の欠落や不自然な文を生んだため（Issue #013）、原稿の修正には使わず、違反は1か所ずつ文脈を読んで手で直します（`docs/guides/proofreading-workflow.md`）。
実行する場合は、対象ファイルと同じ場所に`backup/`ディレクトリができるので、`git diff`で全変更箇所を確かめ、`backup/`を消してから`git status`で確認します。

## 手で直すときのパターン

1. リスト項目の強調とコロン、「項目タイトル： 説明文」形式
    ```markdown
    # 修正前
    - **保守性の低下**：同じロジックが複数箇所に散在する

    # 修正後
    - 保守性の低下
        + 同じロジックが複数箇所に散在する
    ```

2. コロンで終わるリスト導入文
    ```markdown
    # 修正前
    以下のような問題があります：

    # 修正後
    主な問題は次のとおりです。
    ```

3. 見出しとしての強調表記
    ```markdown
    # 修正前
    **重要なポイント**

    # 修正後
    ### 重要なポイント
    ```

## ルールの追加・修正

新しいルールを追加する場合は、`scripts/check-claude-md-compliance.js`の`VIOLATION_PATTERNS`配列に追加します。
このスクリプトはpre-commitフックで全員の手元で動くため、誤検出がないかを`npm run check:compliance`で確かめてから変更します。
誤検出でコミットが止まった場合も、`--no-verify`で省かずにスクリプトを直します。

```javascript
{
    id: 'new-rule',
    pattern: /パターン/g,
    description: 'ルールの説明',
    severity: 'error', // または 'warning'
    suggestion: '修正提案'
}
```

## 関連ドキュメント

- `CLAUDE.md`：プロジェクト全体のルール
- `docs/guides/writing-guidelines-textlint-compliance.md`：textlintに対応した文章の書き方
- `docs/guides/proofreading-workflow.md`：校正の手順と注意点
- `scripts/README.md`：スクリプトの一覧
