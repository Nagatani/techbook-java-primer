<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第26章のサンプルコード

「統合プロジェクトの技術選定と実装 ライブラリを選び、作り、振り返る」の章のサンプルコードです。

本文の「サンプルコード26-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 25以降。標準入出力に`IO`クラスを使うため）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [26-1](./26-01/) | DuplicateStudentException.java<br>Enrollment.java<br>Grade.java<br>Graduate.java<br>JdbcDemo.java<br>JdbcStudentRepository.java<br>RepositoryException.java<br>Student.java<br>StudentRepository.java<br>StudentService.java<br>Transcript.java<br>Undergraduate.java | `java -cp out JdbcDemo`<br>外部ライブラリ: org.xerial:sqlite-jdbc:3.54.1.0 |
| [26-2](./26-02/) | SplitProblemDemo.java | `java -cp out SplitProblemDemo` |
| [26-3](./26-03/) | CsvReaderDemo.java<br>CsvRow.java<br>StudentCsvReader.java | `java -cp out CsvReaderDemo`<br>外部ライブラリ: org.apache.commons:commons-csv:1.14.1, commons-io:commons-io:2.20.0, commons-codec:commons-codec:1.19.0 |
| [26-4](./26-04/) | DuplicateStudentException.java<br>Enrollment.java<br>Grade.java<br>Graduate.java<br>InMemoryStudentRepository.java<br>Student.java<br>StudentRepository.java<br>StudentService.java<br>StudentServiceTest.java<br>Transcript.java<br>Undergraduate.java | （mainメソッドなし）<br>外部ライブラリ: org.junit.jupiter:junit-jupiter-api:6.1.3, org.junit.platform:junit-platform-commons:6.1.3, org.jspecify:jspecify:1.0.0, org.apiguardian:apiguardian-api:1.1.2, org.opentest4j:opentest4j:1.3.0 |
