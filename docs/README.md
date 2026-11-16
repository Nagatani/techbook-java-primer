# ドキュメント管理ディレクトリ

このディレクトリには、書籍プロジェクトの管理資料を置きます。書籍の本文は`manuscripts/`にあり、このディレクトリの内容は書籍に含まれません。

作業のルールの正は、リポジトリ直下の`CLAUDE.md`です。この文書は、どの資料がどこにあるかの案内です。

## プロジェクト概要（2026-11-16時点）

- 書籍名：Java入門（大学2年生のための実践的プログラミング入門 オブジェクト指向言語 Java）
- 著者：Hidehiro Nagatani
- 対象読者：C言語で手続き型プログラミングを学んだ学生
- 構成：全23章と付録A〜H（付録BはB.01〜B.15）。章タイトルの正は各章ファイル（`manuscripts/chapterXX-*.md`）の1行目
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
| `claude-md-compliance-guide.md` | `npm run check:compliance`（pre-commitフックで実行する表記ルールの検査）の内容と使い方 |
| `summaries/README.md` | 章ごとの要約と、章全体を読まずに作業する方法 |
| `analysis/issues-tracking.md` | 課題の一覧と状況 |
| `guidelines/` | 箇条書き・コードブロック・コラムの書き方 |

2025年の計画・分析・手順書（旧24章構成の章番号や、いまは使わない手順を含むもの）は、2026-11-16に`archive/`へ移しました。作業の手順としては使いません。

## ディレクトリ構成

- `analysis/`：課題の管理（`issues-tracking.md`）、プロジェクトの現状の要約（`comprehensive-project-analysis.md`の冒頭）、`check:java`のレポート（`java-sample-check.md`、生成物）
- `book-index/`：原稿の節ごとの行範囲（`npm run index`で生成）
- `summaries/`：章・付録ごとの要約（索引のハッシュで古くなったことを検出する）
- `guidelines/`：コードのスタイル、コラム、箇条書きの書き方
- `planning/`：演習の作り直しの手順（`exercise-review-brief.md`、2026-11-12）
- `archive/`：役目を終えた2024〜2025年の計画・分析・手順書と、図の検討メモ（元の場所と同じ構成で置いている。章番号などは古い）
- `diagrams/`：図の元データ（Mermaidの`chapterXX-内容.mmd`）と設定。`npm run mermaid:convert`が`manuscripts/images/diagrams/`のSVGを作る。本で使わない図は`diagrams/unused/`
- `lecture-materials/`：元の講義資料（読み取り専用。変更しない）

## 主なコマンド

```bash
npm run build                    # PDFを生成する
npm run build4web                # Web版を生成する（GitHub Pagesへのデプロイで使う）
npm run preview                  # プレビュー
npm run lint                     # textlintで原稿を検査する
npm run check:compliance         # CLAUDE.mdの表記ルールへの違反を検査する（pre-commitフックでも実行）
npm run check:structure          # 章・演習・付録B・用語集・相互参照の食い違いを検査する
npm run check:samples -- --no-run   # サンプルコードの抜粋の一致とコンパイルを検査する
npm run check:public-code -- --no-test   # 付録・演習のコードのコンパイルを検査する
npm run check:diagrams           # 図の元データ・画像・原稿の対応と文字の大きさを検査する
npm run index && npm run index:check  # 索引を作り直し、要約が古くなっていないか確かめる
```

コマンドの詳細は`CLAUDE.md`の「Commands」を参照してください。

---

最終更新：2026-11-16
