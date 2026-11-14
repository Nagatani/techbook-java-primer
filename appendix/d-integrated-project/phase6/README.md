# フェーズ6: データベース統合

このフェーズでは、SQLiteデータベースを使用してデータを永続化します。
SQLiteはサーバーを用意しなくてよい組み込み型のデータベースで、データを1つのファイルに保存します。
付録EはH2で例を動かしますが、JDBCの使い方は同じです。

## SQLite JDBCドライバーの入手

Maven Centralから`org.xerial:sqlite-jdbc`のjarファイルを入手し、このディレクトリに置きます（2026年11月時点の最新版は3.54.1.0）。
入手先は次のページです。

- `https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/`
- `https://github.com/xerial/sqlite-jdbc/releases`

## 実行方法

```bash
# コンパイル（JDBCのAPIはJDKに含まれるので、ドライバーのjarは不要）
javac *.java

# データベース統合のテスト
java --enable-native-access=ALL-UNNAMED -cp ".:sqlite-jdbc-3.54.1.0.jar" Phase6Test

# GUIアプリケーションの実行
java --enable-native-access=ALL-UNNAMED -cp ".:sqlite-jdbc-3.54.1.0.jar" StudentManagementGUI
```

- Windowsでは、クラスパスの区切りを`:`ではなく`;`にする（`-cp ".;sqlite-jdbc-3.54.1.0.jar"`）
- sqlite-jdbcはネイティブライブラリを読み込むため、Java 25では`--enable-native-access=ALL-UNNAMED`を付けないと、起動時に「WARNING: A restricted method in java.lang.System has been called」などの警告が出る（動作には影響しない）
- ドライバーのjarがクラスパスにないと、データベースに接続できない。`Phase6Test`は「No suitable driver found」と表示して終わり、GUIはエラーのダイアログを出して起動しない

## データベースのファイル

- GUIは、実行したディレクトリの`student_management.db`にデータを保存する。次に起動したときは、このファイルから読み込む。学生が1人もいないときだけ、サンプルの学生を3人登録する
- `Phase6Test`は、テスト用の`test_student_management.db`を使い、最後に削除する。GUIのデータは変えない
- このディレクトリの`Phase3Test`と`Phase4Test`は、メモリ上のデータベース（`jdbc:sqlite::memory:`）を使う
- 以前の版で作った`student_management.db`は、表の構造が異なるため使えない。削除してから起動する

## 学習ポイント

- JDBCを使用したデータベース接続
- SQLの実行（CREATE、INSERT、SELECT、DELETEと、UPSERT（`INSERT ... ON CONFLICT DO UPDATE`））
- トランザクション管理（学生と成績の保存・削除を、まとめて確定するか取り消す）
- 外部キー制約（SQLiteは既定で検査しないので、接続の直後に`PRAGMA foreign_keys = ON`を実行する）
- PreparedStatementによるSQLインジェクション対策
- データベースとオブジェクトのマッピング
- データベースへの保存に失敗したとき、メモリ上の変更を元に戻して食い違いを防ぐ方法

## ファイル構成

- `DatabaseManager.java`: データベース操作クラス（接続先のURLを指定できる）
- `StudentService.java`: データベース対応版サービスクラス
- `StudentManagementGUI.java`、`GradeManagementDialog.java`: データベース対応版のGUI（終了時に接続を閉じる）
- `Phase6Test.java`: データベース統合を確かめるプログラム
- `StudentTest.java`: JUnitを使わずに`GradeableStudent`の動作を確かめる例（JUnitを使ったテストは第21章で学ぶ）
- フェーズ5までのファイルも複製して含めている

## データベース構造

- `students`: 学生の基本情報（学生ID、氏名、年齢、学部、GPA）
- `grades`: 成績（学生ID、科目名、成績）。学生IDは`students`を参照する外部キー

保存するのは成績を持つ学生（`GradeableStudent`）だけです。
大学院生（`GraduateStudent`）は成績管理に対応していないため、このフェーズでは保存しません。
