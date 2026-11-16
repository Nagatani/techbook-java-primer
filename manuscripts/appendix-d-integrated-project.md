# <b>付録D</b> <span>統合プロジェクトのソースコード</span> <small>学生管理システムの各フェーズ</small>

## 付録Dについて

本付録では、第24章と第25章で題材にした「学生管理システム」のソースコードを紹介します。
設計の考え方や技術の選び方は第24章と第25章で解説しているので、本付録ではコードの場所と動かし方だけをまとめます。

### オンライン資料へのアクセス

ソースコードは、GitHubリポジトリで提供しています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/d-integrated-project/`

リポジトリには、6つのフェーズのディレクトリがあります。
各ディレクトリには、そのフェーズで動かすのに必要なJavaファイルがすべて入っています。
前のフェーズのファイルも複製して入れてあるので、各ディレクトリだけでコンパイル・実行できます。

### フェーズの構成

各フェーズのコードは、そのフェーズまでに学んだ機能だけで書いた初版です。
第24章では、初版に残る設計の問題（大学院生が成績を持てない、GPAを重ねて保存している、など）を事例として取り上げています。

1. フェーズ1（`phase1/`）
    + 学生のクラスと、動作を確かめるプログラム（第3・4章）
2. フェーズ2（`phase2/`）
    + 人・学生・大学院生の継承階層と、成績のインターフェイス（第5〜7章）
3. フェーズ3（`phase3/`）
    + 学生の一覧を管理するリポジトリと、Stream APIによる検索・集計（第9〜12章）
4. フェーズ4（`phase4/`）
    + 独自の例外と、CSVファイルの読み書き（第13・14章）
5. フェーズ5（`phase5/`）
    + Swingの画面（第17〜20章）
6. フェーズ6（`phase6/`）
    + SQLiteデータベースへの保存（付録E）

### 実行のしかた

各フェーズのディレクトリで、`javac *.java`でコンパイルし、READMEに書いたクラスを`java`コマンドで実行します。
Java 25でコンパイル・実行できます。
フェーズ6は、SQLiteのJDBCドライバー（`org.xerial:sqlite-jdbc`）のJARファイルが必要です<span class="footnote">入手方法と実行のコマンドは、フェーズ6のREADME.mdに書いています。場所は`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/d-integrated-project/phase6/`です。</span>。

### 改善版のコード

第24章と第25章で示した改善版のコード（合成による学生のクラス、リポジトリのインターフェイス、SQLiteとCommons CSVを使う実装など）は、各章のサンプルコードとして公開しています。

- 第24章のサンプルコード
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/sample-code/chapter24/`
- 第25章のサンプルコード
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/sample-code/chapter25/`

初版のアプリケーション全体を改善版に書き換える作業は、第24章と第25章の章末演習の課題にしています。

### 問題や質問がある場合

本付録の内容に関する問題や質問がある場合は、次のURLからGitHubのIssueを作成してください。<br>
`https://github.com/Nagatani/techbook-java-primer/issues`
