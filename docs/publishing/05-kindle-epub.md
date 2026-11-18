# 電子書籍（EPUB）とKindle

`npm run build:epub`で、Kindle（KDPの電子書籍）やほかのEPUBリーダーで読めるEPUBを作ります。リーダーで文字の大きさを変えると行が組み直される形（リフロー型）なので、紙面とは見た目が違います。

## 作る

```bash
npm run build:epub   # → output/techbook-java-primer.epub
```

次の順に処理が進みます。

1. `npm run mermaid:convert`で図のSVGを作り直す
2. `scripts/epub-assets.js`が、表紙の画像と図のPNGを`manuscripts/images/epub/`に作る（リポジトリには入れない）。あわせて作業用のディレクトリ`.vivliostyle-epub/`を空にする
3. Vivliostyleが正規版のEPUBを作る

紙面のPDFとの違いは次のとおりです。

- テーマは`theme/epub-theme.css`だけを使う。Kindleが対応しないCSS（変数、Q・mmの単位、`::before`の生成内容）は使わない
- 節番号（「5章 - 3」）とコードの行番号は、ビルド時にHTMLへ書き込む（`scripts/vfm-epub.js`）
- 脚注は章の末尾に「注」としてまとめる。Kindleでは本文の番号を押すとポップアップで出る
- 目次は原稿の`table-of-contents.md`ではなく、Vivliostyleが章と節から作る。リーダーの目次メニューにもこれが使われる
- 表紙は原稿の表紙を描いた画像（`manuscripts/images/epub/cover.jpg`、1801×2560px）
- 図はPNG（2倍の解像度、白地）。MermaidのSVGは文字の描き方がKindleで表示されないため

## 確かめる

### 1. epubcheckで検証する

epubcheckは、EPUBが規格どおりかを検査するW3Cの公式のツールです。KDPは入稿時に独自の変換を行うので必須ではありませんが、ほかの電子書店に出すときは求められることが多く、通しておくと安心です。

```bash
brew install epubcheck                        # 初回だけ（Javaが必要）
epubcheck output/techbook-java-primer.epub
```

`No errors or warnings detected.`と出れば合格です。エラーが出たら、表示されたファイル名（`chapter05-....xhtml`など）と行番号から原因を探します。原因が原稿のHTMLの書き方ならそこを直し、ビルドの処理（`scripts/vfm-*.js`）ならClaudeに表示をそのまま渡して直してもらってください。

2026-11-18の作成時は、この環境でepubcheckを動かせなかったため、XHTMLの整形式、リンク先・画像・manifestの食い違い、表紙画像の登録だけを独自に確かめています。初回は必ずepubcheckを通してください。

### 2. Kindle Previewerで見る

Kindle Previewer 3（Amazonの無料のアプリ。「Kindle Previewer」で検索するとKDPのページから入手できる）でEPUBを開きます。開くとKindleの形式への変換が行われ、問題があれば一覧に出ます。

端末の種類（タブレット、スマートフォン、Kindle端末）を切り替えて、次の点を見ます。

- 表紙
    + 本の一覧と最初のページに表紙が出ている
- 目次
    + 目次メニューに章と節が並び、押すとその場所へ移る
    + 本の中の目次のページも同じように使える
- 章の頭と節
    + 章の題名と副題が崩れていない
    + 節の見出しに「5章 - 3」の番号が付いている
- コード
    + 行番号が付き、等幅でそろっている
    + 長い行が折り返したとき、続きの行が行番号の右から始まっている
    + 文字の大きさを大きくしても読める
- 脚注
    + 本文の番号を押すとポップアップで出る（Kindle端末とアプリ）
    + 「注」の番号から本文へ戻れる
- 図・表
    + 図の文字が読める。拡大できる
    + 表が画面の幅に収まらないときに崩れていない
- 夜間モード（黒い地）
    + 本文とコードが読める
    + 図は白地のまま出る（白地のPNGにしてある）
- 索引と奥付
    + 奥付の発行日と表示が正しい

### 3. ほかのリーダーで見る（任意）

KindleでないEPUBリーダーでも見ておくと、規格どおりに作れているかの目安になります。macOSならブックで開けます。

```bash
open -a Books output/techbook-java-primer.epub
```

## KDPに入稿する

KDPの本棚で「Kindle電子書籍」を新しく作り、次のように設定します。

1. 本の詳細
    + 書名・著者名・内容紹介・キーワード・カテゴリーを入れる（ペーパーバックと同じ本として紐づけるなら、同じ書名と著者名にする）
2. コンテンツ
    + DRM（コピーの防止）を付けるか選ぶ。後から変えられない
    + 原稿に`output/techbook-java-primer.epub`をアップロードする
    + 表紙に`manuscripts/images/epub/cover.jpg`をアップロードする（`npm run build:epub`のたびに作り直される。KDPは高さ2560pxを推奨している）
    + 電子書籍にISBNは要らない
3. プレビュー
    + KDPのオンラインのプレビューアーで、Kindle Previewerと同じ点をざっと見る
4. 価格
    + ロイヤリティを70%にすると、ファイルの大きさに応じた配信の費用が差し引かれる（2026-11-18時点のEPUBは約6.9MB）。35%と70%で手取りを見比べて決める

## 内容を直して入れ替えるとき

1. 原稿を直し、[01-checks.md](01-checks.md)の検査を通す
2. `npm run build:epub`で作り直し、epubcheckとKindle Previewerで直した箇所を確かめる
3. KDPの本棚で原稿を差し替える。購入済みの読者に更新を届けるには、KDPのサポートに連絡して更新の通知を依頼する（誤りの修正の場合）

## よくある問題

| 症状 | 原因と対処 |
|---|---|
| 図の文字が出ない | 図がPNGに置き換わっていない。原稿で図を`images/diagrams/*.svg`の形で参照しているか確かめる |
| 前のビルドで消したファイルがEPUBに入っている | `.vivliostyle-epub/`が空にされていない。`npm run build:epub`を使い、`npx vivliostyle build`を直接呼ばない |
| 表紙がリーダーの一覧に出ない | `vivliostyle.config.js`の`cover.src`が`manuscripts/`から見たパスになっているか確かめる |
| 紙面用のCSSを直したのにEPUBが変わらない | EPUBは`theme/epub-theme.css`だけを使う。EPUBの見た目はこのファイルで直す |

---

最終更新：2026-11-19
