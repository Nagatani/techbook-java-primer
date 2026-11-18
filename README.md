# techbook-java-primer

大学2年生のための実践的プログラミング入門 オブジェクト指向言語 Java

## オンライン閲覧

https://vivliostyle.org/viewer/#src=https://nagatani.github.io/techbook-java-primer/publication.json


## ライセンス

書籍の文章・図は著作権で保護されています（All Rights Reserved）。`sample-code/`・`appendix/`・`exercises/`のソースコードとビルドの設定ファイルはMITライセンスです。詳しくは`LICENSE`と`LICENSE-CODE`を参照してください。

## 開発環境

- Node.js 22.12以上（`@vivliostyle/cli` 11の要件。CIとGitHub Pagesへのデプロイは24を使う）
- JDK 25（サンプルコード・付録・演習のコードの検査に使う）

### セットアップ

```
npm ci
git config core.hooksPath scripts/git-hooks
```

2行目で、共有のpre-commitフック（`scripts/git-hooks/pre-commit`）を有効にします。
コミットのたびに、章・演習・付録B・用語集・相互参照ガイドの食い違い（`npm run check:structure`）と、CLAUDE.mdのルール違反を1秒ほどで検査し、問題があればコミットを止めます。
誤検出でコミットが止まった場合は、`--no-verify`で省かずに検査スクリプトを直してください。

pushとプルリクエストでは、GitHub Actions（`.github/workflows/check.yml`）が、構成の食い違い（`check:structure`）に加えて、索引と要約、サンプルコードの抜粋とコンパイル、付録・演習のコードのコンパイル、図の元データと画像の対応を検査します。
サンプルコードの実行結果の照合、付録・演習のテスト、textlintは参考として実行します。
検査のコマンドの一覧は`CLAUDE.md`の「Commands」にあります。

## ビルドコマンド

### build
PDF形式で書籍を生成します。Webで配布するALPHA版（表紙に「ALPHA VERSION」、奥付にビルドした日付）になります。出力先: `output/techbook-java-primer.pdf`

```
npm run build
```

### build:release
出版する正規版（電子版・オンデマンド印刷）のPDFを生成します。表紙のALPHA版の表示を外し、奥付を発行日の形にします。出力先: `output/techbook-java-primer-release.pdf`

```
npm run build:release
```

版は環境変数`BOOK_EDITION`（`alpha`か`release`）で選びます。原稿の中で片方の版だけに出す要素には`class="edition-alpha"`か`class="edition-release"`を付けます（`scripts/vfm-edition.js`）。

### build:kdp
Amazon KDPのペーパーバック（オンデマンド印刷）に入稿する本文のPDFを、正規版で生成します。出力先: `output/techbook-java-primer-kdp.pdf`

```
npm run build:kdp
```

KDPの入稿条件に合わせて、通常のPDFと次の点を変えています（`theme/my-theme/kdp-theme.css`・`kdp-title-theme.css`）。

- 1ページ目を紺の表紙から白地の本扉にする。表紙はKDPに別のファイルで入稿する
- 裁ち落としなし、トンボなしで、判型はB5（176×250mm）のまま。KDPでは判型を「カスタム」で176×250mmと指定する
- のどを24mm、小口を18mmにする。KDPはのどの最小値をページ数で決めており、701〜828ページでは22.3mm。本文の幅は通常のPDFと同じ
- 上端の柱を紙の端から8mmまで下げる（KDPは文字を端から6.4mm以上離すことを求める）

カラーの紙面ですが、KDPのカラー印刷はページ数の上限が白黒より小さいため、白黒印刷での入稿を想定しています（色はグレーで刷られます）。
ページ数がKDPの区切り（700ページなど）をまたいで変わったら、のどの幅を見直してください。

### build:press
入稿用に、正規版をトンボと塗り足し（3mm）付きのPDF/X-1aで生成します（Dockerが必要）。出力先: `output/techbook-java-primer-press.pdf`

```
npm run build:press
```

トンボだけを付けたい場合などは、`npm run build -- --crop-marks --bleed 3mm`のように、`--`の後ろにVivliostyleのオプションを渡せます。

### build4web
Web公開用にHTML形式で生成します。出力先: `dist/`

```
npm run build4web
```

### preview
ローカルプレビューサーバーを起動します。http://localhost:13000 で閲覧可能

```
npm run preview
```

### lint
原稿のテキストをチェックします

```
npm run lint
```

### lint:fix
原稿のテキストエラーを自動修正します

```
npm run lint:fix
```
