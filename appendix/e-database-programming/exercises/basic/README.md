# 付録E 基礎課題

## 学習目標

- `DriverManager.getConnection`でH2のメモリ上のデータベースに接続し、try-with-resourcesで確実に閉じる
- `Statement`でテーブルを作り、`executeUpdate`の戻り値（変更した行数）を使う
- `PreparedStatement`の`?`に値を渡して、追加・検索・更新・削除（CRUD）を行う
- 入力をSQLの文字列につなぐと何が起こるかを確かめ、`PreparedStatement`で防ぐ
- `ResultSet`の1行をRecordのオブジェクトにし、NULLの列を正しく扱う
- `setAutoCommit(false)`・`commit()`・`rollback()`で、複数の更新をまとめて成功または失敗させる

## 課題一覧

各課題の「作成するファイル」は、自分で作成するファイルの名前の例です。
課題ごとの解答例は[solutions/](../solutions/)にあり、実行例はその解答例を動かした出力です。
H2のJARの入手方法とコンパイルのしかたは、[演習全体のREADME](../README.md)を見てください。

どの課題も、メモリ上のデータベース（`jdbc:h2:mem:名前`、ユーザー名`sa`、パスワードは空文字列）を使います。
プログラムを終えるとデータは消えるので、テーブルの作成と見本のデータの登録もプログラムのなかで行います。

### 課題1: 接続と時間割のテーブル

**作成するファイル**: `ConnectAndCreate.java`

データベースに接続し、授業の時間割を入れるテーブルを作ります。

**要求仕様**:

- `jdbc:h2:mem:timetable`に接続し、`DatabaseMetaData`からデータベース製品の名前を表示する
- `courses`テーブル（ID・授業名・曜日・時限・教室）を`CREATE TABLE`で作る。教室だけは空（NULL）でもよい列にする
- `Statement`の`executeUpdate`で4件の授業を登録し、戻り値を足し合わせて登録した件数を表示する
- `SELECT COUNT(*)`で件数を数えて表示する
- 同じテーブルをもう一度作ろうとして`SQLException`になることを確かめ、`getSQLState()`の値を表示する
- 接続を閉じたあとでつなぎ直し、テーブルが消えていることを確かめる

**実行例**:

```text
接続しました
データベース製品: H2
coursesテーブルを作成しました
4件の授業を登録しました
登録されている授業: 4件
もう一度作成すると失敗しました（SQLState: 42S01）

接続を閉じて、つなぎ直します
coursesテーブルはありません（SQLState: 42S04）
```

`getMessage()`の文章はH2の版や実行する環境の言語で変わるので、ここでは決まった値の`getSQLState()`を表示しています。
`42S01`は「テーブルがすでにある」、`42S04`は「テーブルが見つからない（データベースが空）」を表すH2の値です。

### 課題2: ToDoリストのCRUD

**作成するファイル**: `TodoCrud.java`

ToDoを追加・一覧・完了・削除するメソッドを作り、mainで順に呼び出します。

**要求仕様**:

- `todos`テーブルはID（データベースが振る）・題名・完了したかどうか（`BOOLEAN`、既定は`FALSE`）を持つ
- `add(conn, 題名)`は`PreparedStatement`で1件追加し、`getGeneratedKeys`で受け取ったIDを返す
- `listAll(conn)`は「`[ ] 1 レポートを出す`」「`[x] 2 図書館で本を返す`」の形の文字列をID順の`List`で返す
- `markDone(conn, id)`と`delete(conn, id)`は、変更した行があれば`true`、IDが見つからなければ`false`を返す
- `deleteDone(conn)`は完了したToDoをまとめて削除し、削除した件数を返す

**実行例**:

```text
=== 追加（CREATE） ===
追加しました: 1 レポートを出す
追加しました: 2 図書館で本を返す
追加しました: 3 サークルの会費を払う
追加しました: 4 英単語を30個覚える

=== 一覧（READ） ===
[ ] 1 レポートを出す
[ ] 2 図書館で本を返す
[ ] 3 サークルの会費を払う
[ ] 4 英単語を30個覚える

=== 完了にする（UPDATE） ===
2を完了: true
4を完了: true
9を完了: false
[ ] 1 レポートを出す
[x] 2 図書館で本を返す
[ ] 3 サークルの会費を払う
[x] 4 英単語を30個覚える

=== 削除（DELETE） ===
1を削除: true
1をもう一度削除: false
完了したものを削除: 2件
[ ] 3 サークルの会費を払う
```

### 課題3: 図書の検索とSQL Injection

**作成するファイル**: `BookSearch.java`

図書の検索を`PreparedStatement`で作り、入力をSQLの文字列につなぐ書き方と比べます。

**要求仕様**:

- `books`テーブル（ID・題名・著者・出版年）に5冊を登録する。そのうち1冊は、題名と著者に引用符（`'`）を含む「Let's Learn SQL」（著者 O'Brien）にする
- `searchByTitle(conn, キーワード)`は、題名にキーワードを含む本の題名をID順に返す。`LIKE ?`とし、`%`は値の側に付けて渡す
- `findByAuthorSince(conn, 著者, 年)`は、著者が一致し、その年以降に出版された本の題名を出版年の順に返す（`?`を2つ使う）
- 比べるために、入力を文字列としてSQLにつなぐ`searchByTitleUnsafe`（悪い例）も作る
- `' OR '1'='1`という入力と、`Let's`という入力を両方の書き方に渡し、結果の違いを表示する

**実行例**:

```text
=== 題名で検索 ===
「Java」: 2件 [Java入門, やさしいJavaの練習帳]
「入門」: 2件 [Java入門, データベース入門]
「料理」: 0件 []

=== 著者と出版年で検索 ===
山田一郎、2020年以降: [Java入門]
O'Brien、2000年以降: [Let's Learn SQL]

=== 悪意のある入力 ===
入力: ' OR '1'='1
文字列をつなぐ書き方: 5件（全件が見えてしまう）
PreparedStatement: 0件

=== 引用符を含む入力 ===
文字列をつなぐ書き方: SQLの文法エラー（SQLState: 42000）
PreparedStatement: 1件
```

文字列をつなぐ書き方では、入力の`'`がSQLの文字列の終わりとして読まれます。
`PreparedStatement`では、入力はすべて値として渡されるので、`'`を含む題名もそのまま探せます。

### 課題4: 成績をオブジェクトにする

**作成するファイル**: `ScoreReport.java`

`ResultSet`の1行をRecordのオブジェクトにし、科目ごとの集計をSQLで行います。

**要求仕様**:

- `scores`テーブル（学籍番号・名前・科目・点数）に、2科目×4人の成績を登録する。欠席した人の点数はNULLにする
- Record`Score(学籍番号, 名前, 科目, 点数)`を作る。点数は欠席を表せるよう`Integer`にし、`getObject("score", Integer.class)`で読む
- `findBySubject(conn, 科目)`は、その科目の成績を点数の高い順の`List<Score>`で返す。欠席者は最後に置く（`NULLS LAST`）
- `getInt`でNULLの列を読むと0になり、`wasNull()`で見分けることを確かめる
- Record`SubjectSummary`を作り、`summarize(conn)`で科目ごとの受験者数・欠席者数・平均点・最高点を、`GROUP BY`で集計して返す

**実行例**:

```text
=== プログラミングの成績（点数の高い順） ===
S002 井上彩 92点
S001 青木健 78点
S004 江藤美咲 65点
S003 上田翔 欠席

=== getIntとgetObjectの違い（S003のプログラミング） ===
getInt: 0（wasNull: true）
getObject: null

=== 科目ごとの集計 ===
プログラミング: 受験3人、欠席1人、平均78.3点、最高92点
統計学: 受験3人、欠席1人、平均81.0点、最高88点
```

`COUNT(score)`・`AVG(score)`・`MAX(score)`はNULLの行を数えないので、欠席者は平均点に入りません。
`getInt`で読んでいたら、欠席が0点として扱われてしまいます。

### 課題5: 座席のまとめ予約とトランザクション

**作成するファイル**: `SeatReservation.java`

グループで複数の座席をまとめて予約します。
1席でも予約できなければ、どの席も予約しないようにします。

**要求仕様**:

- `seats`テーブル（座席番号・予約した人の名前）に、6つの座席（A1〜A4、B1、B2）を空き（名前がNULL）で登録する
- `reserve(conn, 名前, 座席番号のList)`は、`setAutoCommit(false)`にしてから1席ずつ予約し、すべて成功したら`commit()`する
- 1席の予約は`UPDATE seats SET reserved_by = ? WHERE seat_no = ? AND reserved_by IS NULL`で行い、変更した行が0なら（予約済みか存在しない席）「A2は予約できません」という`SQLException`を投げる
- 例外が出たら`rollback()`で、それまでに予約した席も取り消してから例外を投げ直す。最後に自動コミットの設定を元に戻す
- 比べるために、自動コミットのまま1席ずつ予約する`reserveWithoutTransaction`（悪い例）も作り、途中で失敗すると一部の席だけが予約されて残ることを確かめる
- `cancel(conn, 名前)`は、その人の予約を取り消し、取り消した席の数を返す

**実行例**:

```text
=== 初めの座席 ===
A1:空き A2:空き A3:空き A4:空き B1:空き B2:空き

=== トランザクションで予約する ===
山田: [A1, A2]を予約しました
佐藤: A2は予約できません。[A3, A2]の予約をすべて取り消しました
A1:山田 A2:山田 A3:空き A4:空き B1:空き B2:空き
佐藤: [A3, A4]を予約しました
A1:山田 A2:山田 A3:佐藤 A4:佐藤 B1:空き B2:空き

=== トランザクションを使わない場合 ===
鈴木: A4は予約できません
A1:山田 A2:山田 A3:佐藤 A4:佐藤 B1:鈴木 B2:空き
B1だけが予約された状態で残っています

=== 取り消し ===
鈴木の予約を取り消しました: 1席
A1:山田 A2:山田 A3:佐藤 A4:佐藤 B1:空き B2:空き
```

佐藤さんの1回目の予約では、A3の予約が済んだあとでA2が失敗します。
ロールバックによってA3も空きに戻るので、一部の席だけが予約された状態は残りません。

## 実装のヒント

```java
try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:todo", "sa", "");
     PreparedStatement pstmt = conn.prepareStatement(
             "INSERT INTO todos (title) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
    pstmt.setString(1, title);      // ?の番号は1から数える
    pstmt.executeUpdate();
    try (ResultSet keys = pstmt.getGeneratedKeys()) {
        keys.next();
        int id = keys.getInt(1);    // データベースが振ったID
    }
}
```

- `executeUpdate`は変更した行数を返す。`UPDATE`や`DELETE`で0が返れば、条件に合う行がなかったことがわかる
- `ResultSet`は、最初に`next()`を呼ぶまでどの行も指していない。1行だけの結果でも`next()`を呼んでから読む
- `getInt`・`getDouble`・`getBoolean`は、NULLの列を0や`false`として返す。NULLを区別したいときは`getObject(列名, Integer.class)`で読む
- `PreparedStatement`の`setObject(番号, null)`でNULLを渡せる。型を明示したいときは`setNull(番号, Types.INTEGER)`を使う
- トランザクションの形は付録EのサンプルコードE-19と同じにする。`finally`で自動コミットの設定を元に戻すのを忘れない
- 解答例は、確かめやすいようにデータベースを使う処理を`Connection`を受け取る`static`メソッドに分けている。[solutions/](../solutions/)の`SolutionCheck.java`は、そのメソッドを呼んで結果を確かめる

## 完了チェックリスト

- [ ] 接続・`Statement`・`PreparedStatement`・`ResultSet`をすべてtry-with-resourcesで閉じている
- [ ] 利用者の入力を受け取るSQLは、すべて`PreparedStatement`の`?`で値を渡している
- [ ] `executeUpdate`の戻り値で、対象の行が見つかったかどうかを判定している
- [ ] NULLになりうる列を`getObject`で読み、欠席を0点として扱っていない
- [ ] 座席の予約で1席でも失敗したとき、ほかの席の予約が残らない

**次のステップ**: 基礎課題が完了したら、[発展課題](../advanced/)でDAOパターンとjava.time、バッチ処理に取り組みましょう。
