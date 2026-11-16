# 第25章 発展課題 - H2データベースに保存するリポジトリ

## 学習目標
- インターフェイスの実装を加えるだけで、保存先を差し替えられることを確かめる
- 同じJDBCでも、データベースによってSQLの書き方（方言）が違うことを知る
- 本物のデータベース（メモリ上）を使って、SQLが正しいかを確かめる

## 課題1: H2StudentRepositoryを作る

第25章のサンプルコード25-1の`JdbcStudentRepository`はSQLiteに保存します。
同じ`StudentRepository`インターフェイスを実装し、H2データベース（付録E）に保存する`H2StudentRepository`を作ってください。
サンプルコード25-1の全文（`sample-code/chapter25/25-01/`）をコピーして始めます。

**要件:**

- `StudentRepository`の4つのメソッドを実装する。`StudentService`は変えない
- 学生の行と成績の行は、トランザクションでまとめて保存する
- `findAll`は登録した順に返す。同じ学生を保存し直しても順番は変わらない
- 学生を削除すると、その学生の成績も削除される
- 動作を確かめるプログラム`H2RepositoryCheck`を作り、`jdbc:h2:mem:students`（メモリ上のデータベース）で確かめる

**ヒント:**

- SQLiteの`INSERT ... ON CONFLICT DO UPDATE`に当たる処理は、H2では`MERGE INTO 表 (列…) KEY (キーの列) VALUES (…)`で書ける
- SQLiteの`rowid`はH2にはない。登録した順を保つには、自動で番号を振る列（`BIGINT AUTO_INCREMENT`）を加え、その列で並べる
- SQLiteと違い、H2は外部キーの制約を既定で検査する。`PRAGMA foreign_keys = ON`は要らない（H2ではエラーになる）
- `TEXT`や`REAL`の代わりに、`VARCHAR(100)`や`DOUBLE PRECISION`を使うと、どのデータベースでも意味が通じやすい

**考えてみよう:**

SQLiteとH2の違いは、どのクラスに現れましたか。
第24章で保存の窓口をインターフェイスにしていなかったら、どこを直すことになったかを考えてください。
