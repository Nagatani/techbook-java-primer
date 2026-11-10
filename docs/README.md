# ドキュメント管理ディレクトリ

このディレクトリには、書籍プロジェクトの管理資料を置きます。書籍の本文は`manuscripts/`にあり、このディレクトリの内容は書籍に含まれません。

作業のルールの正は、リポジトリ直下の`CLAUDE.md`です。この文書は、どの資料がどこにあるかの案内です。

## プロジェクト概要（2026-11-10時点）

- 書籍名：Java入門（大学2年生のための実践的プログラミング入門 オブジェクト指向言語 Java）
- 著者：Hidehiro Nagatani
- 対象読者：C言語で手続き型プログラミングを学んだ学生
- 構成：全23章と付録A〜H
- 対象のJava：Java 25（公開するコードはJava 25でコンパイル・実行できる状態を保つ）
- 組版：Vivliostyle（設定は`vivliostyle.config.js`、テーマは`theme/my-theme/theme.css`、出力は`output/techbook-java-primer.pdf`）

## まず読む資料

| 資料 | 内容 |
|---|---|
| `../CLAUDE.md` | 執筆・コード・Git・依存関係のルール、コマンド一覧 |
| `../TODO.md` | 現在の作業項目 |
| `writing-guidelines-textlint-compliance.md` | 原稿を書く・直すときの文章の規約（必読） |
| `proofreading-workflow.md` | 校正の手順と注意点 |
| `code-verification.md` | 公開コードをJava 25で確かめる手順と、見つかった誤りのパターン |
| `summaries/README.md` | 章ごとの要約と、章全体を読まずに作業する方法 |
| `analysis/issues-tracking.md` | 課題の一覧と状況 |

## ディレクトリ構成

- `analysis/`：プロジェクトの分析資料と課題の管理（作業のたびに更新する）
- `book-index/`：原稿の節ごとの行範囲（`npm run index`で生成）
- `summaries/`：章・付録ごとの要約（索引のハッシュで古くなったことを検出する）
- `guidelines/`：コードのスタイル、コラム、箇条書きの書き方
- `planning/`、`reference/`：初期の計画・分析資料（2024〜2025年時点の内容で、章番号などは古いことがある）
- `diagrams/`：図の資料
- `lecture-materials/`：元の講義資料（読み取り専用。変更しない）

## 主なコマンド

```bash
npm run build                    # PDFを生成する
npm run build4web                # Web版を生成する（GitHub Pagesへのデプロイで使う）
npm run preview                  # プレビュー
npm run lint                     # textlintで原稿を検査する
npm run check:structure          # 章・演習・付録B・用語集・相互参照の食い違いを検査する
npm run check:samples -- --no-run   # サンプルコードの抜粋の一致とコンパイルを検査する
npm run index && npm run index:check  # 索引を作り直し、要約が古くなっていないか確かめる
```

コマンドの詳細は`CLAUDE.md`の「Commands」を参照してください。

---

最終更新：2026-11-10
