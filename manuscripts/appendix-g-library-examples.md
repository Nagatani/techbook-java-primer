# <b>付録G</b> <span>外部ライブラリの使用例</span> <small>Jackson・Apache Commons・OkHttp・Lombok・Guava・HikariCP</small>

## 付録Gについて

本付録では、第22章と第24章で要点や表だけを紹介した外部ライブラリについて、依存関係の書き方とコードの例を示します。
本書の演習では使いませんが、ほかの人のコードを読むときに出会うことの多いライブラリです。

### 内容

1. OkHttp
    + HTTP通信（GETとPOST、非同期の呼び出し）
2. Lombok
    + アノテーションによるgetter・コンストラクターなどの生成と、JDK 23以降で注釈処理を有効にする設定
3. Guava
    + `Multimap`・`BiMap`・`Table`などのコレクション、文字列の分割と結合、キャッシュ
4. Jackson
    + `ObjectMapper`によるJSONの変換と、Gsonとの書き方の違い（`@JsonProperty`・`@JsonIgnore`・`TypeReference`）
5. Apache Commons
    + Commons Langの文字列・配列の操作と`toString`の生成、Commons I/Oのファイル・パス・ストリームの操作
6. HikariCP
    + コネクションプールの設定と、プールから接続を借りて返す使い方

### 関連する章

- 第22章「ドキュメントと外部ライブラリ」
    + Mavenによる依存関係の管理、Gsonの使い方とライブラリの選び方、項目1〜5の要点
- 第24章「データベースプログラミング」
    + JDBCによる接続とtry-with-resources、項目6の要点
- 第26章「統合プロジェクトの技術選定と実装」
    + 標準API・自作・ライブラリを比べて選ぶ進め方

### オンライン資料

本付録の解説とコンパイルできる全文は、GitHubリポジトリで公開しています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/g-library-examples/`
