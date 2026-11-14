# 付録E データベースプログラミング - 解答例

[基礎課題](../basic/)の解答例です。
基礎課題のREADMEの実行例は、この解答例を動かした出力です。
データベースはメモリ上のH2を使うので、実行してもファイルは作られません。

## 解答例一覧

| 課題 | ファイル | 主な内容 |
|------|----------|----------|
| 課題1: 接続と時間割のテーブル | `ConnectAndCreate.java` | `DriverManager`・`DatabaseMetaData`、`CREATE TABLE`、`executeUpdate`の戻り値、`getSQLState` |
| 課題2: ToDoリストのCRUD | `TodoCrud.java` | `PreparedStatement`、`getGeneratedKeys`、変更した行数による判定 |
| 課題3: 図書の検索とSQL Injection | `BookSearch.java` | `LIKE ?`、複数の`?`、文字列をつなぐ書き方との比較 |
| 課題4: 成績をオブジェクトにする | `ScoreReport.java` | `ResultSet`からRecordへ、`getObject`とNULL、`GROUP BY`での集計 |
| 課題5: 座席のまとめ予約 | `SeatReservation.java` | `setAutoCommit(false)`・`commit`・`rollback`、トランザクションを使わない場合との比較 |

`SolutionCheck.java`は、各課題の解答例が期待どおりに動くかを確かめるプログラムです。
確認ごとに別の名前のメモリ上のデータベースを使い、期待する値と実際の値を比べて「OK」か「NG」を表示します。
第21章で学ぶJUnitを使うと、同じ確認をテストとして書けます。

## 実行方法

H2のJAR（`h2-2.5.252.jar`）を親のフォルダー（`exercises/`）に置いた場合の例です。
入手方法は[演習全体のREADME](../README.md)にあります。

macOS・Linuxでは次のようにします。

```bash
cd appendix/e-database-programming/exercises/solutions
javac -cp ../h2-2.5.252.jar *.java
java -cp .:../h2-2.5.252.jar ConnectAndCreate
java -cp .:../h2-2.5.252.jar TodoCrud
java -cp .:../h2-2.5.252.jar BookSearch
java -cp .:../h2-2.5.252.jar ScoreReport
java -cp .:../h2-2.5.252.jar SeatReservation
```

Windowsでは、クラスパスの区切りを`;`にします。

```bash
cd appendix\e-database-programming\exercises\solutions
javac -cp ..\h2-2.5.252.jar *.java
java -cp .;..\h2-2.5.252.jar TodoCrud
```

動作確認のプログラムは次のように実行します。
すべて「OK」になり、最後に「39件中39件OK」と表示されれば、解答例は期待どおりに動いています。

```bash
java -cp .:../h2-2.5.252.jar SolutionCheck
```

## 実装のポイント

- 接続・`PreparedStatement`・`ResultSet`はtry-with-resourcesで閉じる。`ResultSet`は、それを作った`PreparedStatement`を閉じると一緒に閉じられるが、明示して閉じておくと読みやすい
- 解答例は、データベースを使う処理を`Connection`を受け取るメソッドに分けている。mainはそのメソッドを順に呼んで表示するだけにしたので、`SolutionCheck`から同じメソッドを確かめられる
- `SQLException`の`getMessage()`は、H2の版や実行する環境の言語で文章が変わる。プログラムで判定するときは`getSQLState()`か`getErrorCode()`を使う
- `searchByTitleUnsafe`と`reserveWithoutTransaction`は、比べるための悪い例である。実際のプログラムでは書かない
- 座席の予約の`reserve`は、決まりに合わないとき（予約済みの席）にも`SQLException`を投げている。理由をデータベースの障害と分けて扱いたいときは、チャレンジ課題の`LoanException`のように自分の例外クラスを作る
