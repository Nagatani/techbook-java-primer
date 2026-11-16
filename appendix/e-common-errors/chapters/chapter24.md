# 第24章 データベースプログラミング：よくあるエラーと対処法

書籍『Java入門』第24章「データベースプログラミング」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。
エラーメッセージは、Java 25とH2 2.5.252（日本語の環境）で確かめたものです。H2のメッセージは、日本語と英語の両方が表示されることがあります。

JDBCの`SQLException`には、メッセージのほかに`getSQLState()`で取り出せる5文字のコード（SQLState）があります。
先頭の2文字でおおまかな種類がわかります（`42`は文法やアクセスの誤り、`23`は制約の違反、`08`は接続の問題）。

## No suitable driver found

### エラーメッセージ

```
Exception in thread "main" java.sql.SQLException: No suitable driver found for jdbc:h2:mem:testdb
	at java.sql/java.sql.DriverManager.getConnection(DriverManager.java:638)
```

### 原因

- 接続URLに合うJDBCドライバーが、実行のときのクラスパスにない
- コンパイルは通る。JDBCのAPI（`java.sql`）はJDKに入っていて、ドライバーは実行のときに初めて探すから
- 接続URLの書き間違い（`jdbc:h2:`を`jdbc:h2/`と書くなど）でも同じメッセージになる

### 対処法

- `java -cp .:h2-2.5.252.jar ConnectExample`のように、実行のときにもドライバーのJARを加える（Windowsでは`;`で区切る）
- Mavenでは`pom.xml`の依存を確かめる。`<scope>test</scope>`になっていると、テスト以外では使えない
- 接続URLの形を、データベースの説明（第24章の表）と見比べる

## Table "USERS" not found

### エラーメッセージ

```
org.h2.jdbc.JdbcSQLSyntaxErrorException: Table "USERS" not found (this database is empty); SQL statement:
SELECT * FROM users [42104-252]
```

### 原因

- 表を作る前に使っている
- メモリ上のデータベース（`jdbc:h2:mem:testdb`）は、最後の接続を閉じると表ごと消える。表を作った接続を閉じてから、別の接続で使うと見つからない
- 別のデータベースに接続している（URLの名前が違う、ファイルの場所が違う）

### 対処法

- 表を作る処理を、使う処理と同じ接続の中で先に実行する
- 接続を開き直しても残したいときは、URLに`;DB_CLOSE_DELAY=-1`を付けるか、ファイルのデータベース（`jdbc:h2:./testdb`）を使う
- 表を作るSQLに`CREATE TABLE IF NOT EXISTS`を使うと、何度実行しても失敗しない

## 一意性制約・主キーの違反

### エラーメッセージ

```
org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException: ユニークインデックス、またはプライマリキー違反: "PUBLIC.CONSTRAINT_4D INDEX PUBLIC.CONSTRAINT_INDEX_4 ON PUBLIC.USERS(USERNAME NULLS FIRST) VALUES ( /* 1 */ 'tanaka' )"
Unique index or primary key violation: ... ; SQL statement:
INSERT INTO users VALUES (2, 'tanaka') [23505-252]
```

### 原因

- `UNIQUE`や`PRIMARY KEY`の列に、すでにある値を追加しようとした
- データベースが正しく守ってくれた結果なので、プログラムの誤りとは限らない。利用者が同じ名前を入力した場合にも起こる

### 対処法

コンパイルできる全文：[chapter24-code/DuplicateUser/](chapter24-code/DuplicateUser/)

制約の違反は`SQLIntegrityConstraintViolationException`（H2の例外はその子クラス）で届くので、それを受け止めて利用者に伝わる言葉にします。

```java
try (PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username) VALUES (?)")) {
    ps.setString(1, username);
    ps.executeUpdate();
    return username + "を登録しました";
} catch (SQLIntegrityConstraintViolationException e) {
    return username + "は使われています（" + e.getSQLState() + "）";
}
```

実行結果：

```
tanakaを登録しました
tanakaは使われています（23505）
```

先に`SELECT`で重複を確かめる方法もありますが、確かめてから追加するまでの間にほかの処理が同じ値を追加すると防げません。最後はデータベースの制約に任せます。

## 外部キー制約の違反

### エラーメッセージ

```
org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException: 参照整合性制約違反: "CONSTRAINT_48C: PUBLIC.POSTS FOREIGN KEY(USER_ID) REFERENCES PUBLIC.USERS(ID) (99)"
Referential integrity constraint violation: ... ; SQL statement:
INSERT INTO posts VALUES (1, 99) [23506-252]
```

### 原因

- 外部キーの列に、参照先の表にない値（存在しないユーザーのID 99）を入れようとした
- 逆に、参照されている行を先に削除しようとしても同じ種類のエラーになる（`ON DELETE CASCADE`を付けていない場合）

### 対処法

- 参照先の行を先に追加する。削除は参照する側（`posts`）から先に行う
- 親の行を消したら子の行も消してよいなら、外部キーに`ON DELETE CASCADE`を付ける
- SQLiteは既定で外部キーを検査しない。接続の直後に`PRAGMA foreign_keys = ON`を実行する（第26章のサンプルコード26-1）

## 列の名前にした語で文法エラーになる

### エラーメッセージ

```
org.h2.jdbc.JdbcSQLSyntaxErrorException: SQLステートメントに文法エラーがあります "CREATE TABLE logs ([*]day DATE)"; 期待されるステートメント "identifier"
Syntax error in SQL statement "CREATE TABLE logs ([*]day DATE)"; expected "identifier"; SQL statement:
CREATE TABLE logs (day DATE) [42001-252]
```

### 原因

- `DAY`はSQLの予約語で、そのままでは列の名前に使えない。`USER`・`ORDER`・`VALUE`・`YEAR`なども同じ
- `[*]`の印が、文法の誤りを見つけた位置を示している
- どの語が予約語かは、データベースごとに違う（H2で通るSQLがMySQLで通らない、あるいはその逆がある）

### 対処法

- `walk_date`・`order_date`のように、意味のわかる別の名前にする（第24章のサンプルコード24-12）
- どうしても使いたい場合は`"day"`のように二重引用符で囲む。ただし、大文字と小文字が区別されるようになり、ほかのSQLでも毎回囲む必要がある

## commitを忘れて変更が消える

### エラー例

```java
conn.setAutoCommit(false);
stmt.execute("INSERT INTO users VALUES (1)");
// commitを呼ばずに接続を閉じる
```

### 現れ方

エラーも例外も出ません。
接続を開き直して数えると、追加したはずの行がありません（`DB_CLOSE_DELAY=-1`で開き直して確かめたもの）。

```
件数: 0
```

### 原因

- `setAutoCommit(false)`にすると、`commit`を呼ぶまで変更は確定しない
- 確定しないまま接続を閉じると、変更は取り消される

### 対処法

- トランザクションの形を決めておき、毎回同じ形で書く（第24章のサンプルコード24-10。成功したら`commit`、例外なら`rollback`、`finally`で自動コミットを元に戻す）
- 1つのSQLだけを実行する処理なら、自動コミットのまま使う

## 有効なデータがありません（No data is available）

### エラー例

```java
try (ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
    IO.println(rs.getInt("id"));   // nextを呼んでいない
}
```

### エラーメッセージ

```
Exception in thread "main" org.h2.jdbc.JdbcSQLNonTransientException: 有効なデータがありません
No data is available [2000-252]
```

### 原因

- `ResultSet`は、最初は1行目の手前を指している。`next()`を呼ぶ前に列の値を取り出そうとした
- `next()`が`false`を返した（行がない）あとに値を取り出した場合も同じ

### 対処法

- 複数の行は`while (rs.next()) { ... }`、1行だけなら`if (rs.next()) { ... } else { ... }`の形で読む
- `COUNT(*)`のように必ず1行が返るSQLでも、`rs.next()`を1回呼んでから値を取り出す
