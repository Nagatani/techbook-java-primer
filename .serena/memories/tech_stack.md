# 技術スタック

## ビルドシステム
- **Vivliostyle CLI** v9.1.1: CSS組版システム（PDFとHTML生成）
- **Node.js** v22.11.0以上
- **npm** v11.4.1

## テーマ
- **@vivliostyle/theme-techbook** v2.0.0: 技術書用テーマ
- カスタムテーマ: `./theme/my-theme/` でカスタマイズ

## リンティング・フォーマット
- **textlint** v15.2.0: 日本語原稿のリンティング
- プリセット:
  - @textlint-ja/textlint-rule-preset-ai-writing
  - textlint-rule-preset-ja-technical-writing
  - textlint-rule-preset-japanese
  - textlint-rule-preset-jtf-style
- 追加ルール:
  - textlint-rule-ja-hiraku: 「〜することが」を「〜することを」に
  - textlint-rule-no-mixed-zenkaku-and-hankaku-alphabet
  - textlint-rule-prh: 表記ゆれチェック
  - textlint-rule-spellcheck-tech-word

## 主要ファイル形式
- 原稿: Markdown (.md)
- 設定: JavaScript (.js)
- リント設定: JSON (.json)
- 出力: PDF, HTML