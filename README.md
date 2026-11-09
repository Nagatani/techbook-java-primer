# techbook-java-primer

大学2年生のための実践的プログラミング入門 オブジェクト指向言語 Java

## オンライン閲覧

https://vivliostyle.org/viewer/#src=https://nagatani.github.io/techbook-java-primer/publication.json


## 開発環境

Node.js 20以上

### セットアップ

```
npm ci
git config core.hooksPath scripts/git-hooks
```

2行目で、共有のpre-commitフック（`scripts/git-hooks/pre-commit`）を有効にします。
コミットのたびに、章・演習・付録B・用語集・相互参照ガイドの食い違い（`npm run check:structure`）と、CLAUDE.mdのルール違反を1秒ほどで検査し、問題があればコミットを止めます。
誤検出でコミットが止まった場合は、`--no-verify`で省かずに検査スクリプトを直してください。

pushとプルリクエストでは、GitHub Actions（`.github/workflows/check.yml`）が同じ検査に加えて、索引とサンプルコードを検査します。

## ビルドコマンド

### build
PDF形式で書籍を生成します。出力先: `output/techbook-java-primer.pdf`

```
npm run build
```

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
