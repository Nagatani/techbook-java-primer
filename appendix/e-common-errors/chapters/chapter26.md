# 第26章 統合プロジェクトの技術選定と実装：よくあるエラーと対処法

書籍『Java入門』第26章「統合プロジェクトの技術選定と実装」で扱う内容に関連して、選んだライブラリを組み込むときに出会いやすいエラーとその対処法をまとめています。
エラーメッセージは、Java 25（日本語の環境）、sqlite-jdbc 3.54.1.0、Apache Commons CSV 1.14.1で確かめたものです。
JDBCそのもののエラー（表がない、制約の違反など）は、第24章のファイルにまとめています。

## SQLiteのドライバーが見つからない

### エラーメッセージ

```
Exception in thread "main" java.sql.SQLException: No suitable driver found for jdbc:sqlite:student_management.db
```

### 原因と対処法

- sqlite-jdbcのJARが、実行のときのクラスパスにない。JDBCのAPIはJDKにあるので、コンパイルは通る
- 学生管理システムの初版（フェーズ6）は、`java --enable-native-access=ALL-UNNAMED -cp ".:sqlite-jdbc-3.54.1.0.jar" StudentManagementGUI`のように実行する（Windowsではクラスパスを`;`で区切る）
- 手順は`sample-code/integrated-project/phase6/README.md`にある

## 起動時にrestricted methodの警告が出る

### エラーメッセージ

```
WARNING: A restricted method in java.lang.System has been called
WARNING: java.lang.System::load has been called by org.sqlite.SQLiteJDBCLoader in an unnamed module (file:.../sqlite-jdbc-3.54.1.0.jar)
WARNING: Use --enable-native-access=ALL-UNNAMED to avoid a warning for callers in this module
WARNING: Restricted methods will be blocked in a future release unless native access is enabled
```

### 原因と対処法

- sqlite-jdbcは、C言語で書かれたSQLiteをネイティブライブラリとして読み込む。Java 25では、ネイティブライブラリを読み込むクラスについて、許可していないと警告が出る
- 警告だけで、プログラムは動く。最後の行のとおり、将来の版では許可しないと止まるようになる予定
- 起動のコマンドに`--enable-native-access=ALL-UNNAMED`を付ける。jpackageで配るときは`--java-options "--enable-native-access=ALL-UNNAMED"`を指定する（第26章の設計判断記録の「影響」）
- ネイティブライブラリを使わないH2を選べば、この警告は出ない（第26章の「SQLiteとH2を比べる」）

## CSVの最初の列が見つからない（BOM）

### エラー例

```java
CSVFormat format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).get();
try (CSVParser parser = format.parse(reader)) {
    for (CSVRecord record : parser) {
        IO.println(record.get("学生ID"));
    }
}
```

### エラーメッセージ

```
Exception in thread "main" java.lang.IllegalArgumentException: Mapping for 学生ID not found, expected one of [学生ID, 氏名]
	at org.apache.commons.csv.CSVRecord.get(CSVRecord.java:132)
```

### 原因

- 見出しに「学生ID」があるように見えるのに、見つからないと言われる
- Excelで「CSV UTF-8」形式を選んで保存すると、ファイルの先頭にBOM（U+FEFF）という目に見えない文字が付く。Commons CSVはBOMを取り除かないので、1つ目の見出しが「BOM＋学生ID」になる。メッセージの`[学生ID, 氏名]`の1つ目にも、目に見えないBOMが入っている

### 対処法

- commons-ioの`BOMInputStream`を通してから読む（第26章のサンプルコード26-3）。commons-ioはCommons CSVが依存しているので、追加のJARは要らない
- 自分で読む場合は、1行目の最初の文字が`'﻿'`なら取り除く

## Commons CSVのクラスが見つからない

### エラーメッセージ

```
エラー: パッケージorg.apache.commons.csvは存在しません
import org.apache.commons.csv.CSVFormat;
                             ^
```

### 原因と対処法

- コンパイルのときのクラスパスにcommons-csvのJARがない
- `BOMInputStream`を使うなら、commons-ioも必要になる。Mavenを使えば、commons-csvの依存として自動で入る
- JARを手で集める場合は、`-cp "commons-csv-1.14.1.jar:commons-io-2.20.0.jar:commons-codec-1.19.0.jar"`のように、依存のJARもすべて並べる（`exercises/chapter26/README.md`の実行方法）
