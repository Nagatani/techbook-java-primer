# 出版作業マニュアル

著者が本を出版するときに使う手順書です。出力の形ごとに、使うコマンドと、できたものを確かめる方法をファイルに分けてまとめています。

作業のルール（コミットの書き方、原稿の規約など）の正はリポジトリ直下の`CLAUDE.md`です。この手順書は、出版の作業でそのルールのどこを使うかを案内するものです。

## 手順書の一覧

| ファイル | 作業 | 主なコマンド |
|---|---|---|
| [01-checks.md](01-checks.md) | 出版の前に行う原稿とコードの検査（どの出力でも最初に行う） | `npm run lint`、`npm run check:*` |
| [02-pdf.md](02-pdf.md) | PDF（ALPHA版・正規版）を作り、紙面を目で確かめる | `npm run build`、`npm run build:release` |
| [03-web.md](03-web.md) | Web版（GitHub Pages）の公開 | `npm run build4web`、mainへのpush |
| [04-kdp-paperback.md](04-kdp-paperback.md) | Amazon KDPのペーパーバック（本文PDFと表紙）の入稿 | `npm run build:kdp` |
| [05-kindle-epub.md](05-kindle-epub.md) | 電子書籍（EPUB）の作成とKindleへの入稿 | `npm run build:epub` |
| [06-press.md](06-press.md) | 印刷所へ入稿するPDF（トンボ・塗り足し・PDF/X-1a） | `npm run build:press` |
| [07-release.md](07-release.md) | 出版の日に書き換えるところと、版を重ねるときの手順 | ― |

## 出力の一覧

| 出力 | コマンド | 出力先 | 版 | 用途 |
|---|---|---|---|---|
| ALPHA版のPDF | `npm run build` | `output/techbook-java-primer.pdf` | alpha | Webでの無料配布 |
| Web版（HTML） | `npm run build4web` | `dist/` | alpha | GitHub Pages（mainへのpushで自動公開） |
| 正規版のPDF | `npm run build:release` | `output/techbook-java-primer-release.pdf` | release | 電子版のPDF、確認用 |
| KDPの本文PDF | `npm run build:kdp` | `output/techbook-java-primer-kdp.pdf` | release | KDPのペーパーバック |
| EPUB | `npm run build:epub` | `output/techbook-java-primer.epub` | release | Kindleなどの電子書籍 |
| 入稿用PDF | `npm run build:press` | `output/techbook-java-primer-press.pdf` | release | 印刷所（PDF/X-1a） |

版（`BOOK_EDITION`）によって変わるのは次の3か所です。

- 表紙の「ALPHA VERSION／Web配布版」の表示（ALPHA版だけに出る）
- 奥付の日付と版の表示（ALPHA版はビルドした日と「ALPHA版（Web配布版）」、正規版は`manuscripts/colophon.md`に書いた発行日）
- 奥付の「本書は執筆中のALPHA版です」の注意書き（ALPHA版だけに出る）

`output/`と`dist/`はリポジトリに入れません（`.gitignore`）。

## 出版の流れ

初めて出版するときは、次の順に進めます。

1. [07-release.md](07-release.md)の「出版の前に決めること」を決め、奥付の発行日などを書き換える
2. [01-checks.md](01-checks.md)の検査をすべて通す
3. [02-pdf.md](02-pdf.md)で正規版のPDFを作り、紙面を通して目で確かめる（KDPの本文PDFと入稿用PDFは同じ組版なので、ここで見た結果がそのまま当てはまる）
4. 出す形に合わせて[04-kdp-paperback.md](04-kdp-paperback.md)・[05-kindle-epub.md](05-kindle-epub.md)・[06-press.md](06-press.md)のどれかに進む
5. 出版したら[07-release.md](07-release.md)の「出版した後」を行う

## 準備（初めて作業する端末で1回だけ）

```bash
npm ci                                       # 依存を package-lock.json どおりに入れる
git config core.hooksPath scripts/git-hooks  # コミットの前の検査を有効にする
```

ほかに必要なものは次のとおりです。

- Node.js 24（CIと同じ版）
- Java 25（JDK。サンプルコードの検査に使う。付録Aの手順と同じSDKMANで入れてよい）
- Docker（`build:press`だけで使う）
- epubcheckとKindle Previewer（EPUBの確認だけで使う。入れ方は[05-kindle-epub.md](05-kindle-epub.md)）

## KDPなど外部サービスの仕様について

KDPの数値（のどの最小値、ページ数の上限、背幅の計算、表紙の画像の大きさなど）は、2026-11-18にKDPのヘルプで確かめた値です。サービスの仕様は変わることがあるので、入稿する前にKDPのヘルプで最新の値を確かめてください。値が変わっていたら、この手順書と`theme/my-theme/kdp-theme.css`の冒頭のコメントを直します。

---

最終更新：2026-11-19
