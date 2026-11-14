# 付録E データベースプログラミング - 演習課題

書籍『Java入門』付録E「データベースプログラミング」の演習課題です。
付録Eの[README](../README.md)で説明したJDBCの使い方（接続、CRUD、PreparedStatement、DAOパターン、java.time、トランザクション、バッチ処理）を、身近な題材で確かめます。
コネクションプール（HikariCP）は使いません。

## 準備

どの課題も、組み込みデータベースのH2（2.5.252）を、メモリ上のデータベース（`jdbc:h2:mem:名前`）として使います。
データベースサーバーを用意する必要はなく、プログラムを終えるとデータは消えます。

### H2のJARを入手する

Maven Centralから`h2-2.5.252.jar`を取得し、このフォルダー（`appendix/e-database-programming/exercises/`）に置きます。
各課題のREADMEのコマンドは、JARをここに置いた場合の例です。
ブラウザーで次のURLを開いてもダウンロードできます。

```bash
cd appendix/e-database-programming/exercises
curl -O https://repo1.maven.org/maven2/com/h2database/h2/2.5.252/h2-2.5.252.jar
```

Windows 10以降のPowerShellでは、`curl`の代わりに`curl.exe`と書きます（`curl`だけでは別のコマンドが動きます）。
JARはリポジトリに含めていないので、`git add`しないようにしてください。

### クラスパスを指定してコンパイル・実行する

H2のJARを`-cp`（クラスパス）に加えてコンパイルし、実行するときは今いるフォルダー（`.`）とJARの両方を指定します。
たとえば、解答例のフォルダーでは次のようにします。

macOS・Linux（区切りは`:`）:

```bash
cd solutions
javac -cp ../h2-2.5.252.jar *.java
java -cp .:../h2-2.5.252.jar SolutionCheck
```

Windows（区切りは`;`）:

```bash
cd solutions
javac -cp ..\h2-2.5.252.jar *.java
java -cp .;..\h2-2.5.252.jar SolutionCheck
```

実行時にJARを指定し忘れると、`No suitable driver found for jdbc:h2:mem:...`という`SQLException`になります。
これは、接続URLに合うJDBCドライバーがクラスパスにないことを表します。

Mavenを使う場合は、付録EのサンプルコードE-1の依存を`pom.xml`に加えます。

## 基礎課題（[basic/](basic/)）

- 接続と時間割のテーブル（`DriverManager`、`CREATE TABLE`、メモリ上のデータベースが接続を閉じると消えること）
- ToDoリストのCRUD（`PreparedStatement`、`getGeneratedKeys`、変更した行数による判定）
- 図書の検索とSQL Injection（`LIKE ?`、入力をSQLの文字列につなぐ書き方との比較）
- 成績をオブジェクトにする（`ResultSet`からRecordへ、NULLの列と`getObject`、`GROUP BY`での集計）
- 座席のまとめ予約（`setAutoCommit(false)`・`commit`・`rollback`）

## 発展課題（[advanced/](advanced/)）

- 予定表のDAO（DAOのインターフェイスと実装クラス、`LocalDateTime`の読み書き、日付の範囲と時間帯の重なりの検索）
- 1年分の歩数をバッチ処理で登録する（`addBatch`・`executeBatch`、トランザクションとの組み合わせ、`BatchUpdateException`）
- このフォルダーのJavaファイルは解答例です（課題ごとに`task1-schedule-dao/`・`task2-batch-steps/`）

## チャレンジ課題（[challenge/](challenge/)）

- 図書の貸出管理（外部キーと`INNER JOIN`、貸出・返却のトランザクション、自分の例外クラス、`LocalDate`での延滞の計算、一括登録と集計）
- このフォルダーのJavaファイルは解答例です

## 解答例（[solutions/](solutions/)）

- 基礎課題の課題1〜5の解答例（実行例はこの解答例の出力）
- 解答例が期待どおりに動くかを確かめる`SolutionCheck.java`
