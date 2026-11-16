<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第25章のサンプルコード

「統合プロジェクトの設計 学生管理システムで考える設計の進め方」の章のサンプルコードです。

本文の「サンプルコード25-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 25以降。標準入出力に`IO`クラスを使うため）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [25-1](./25-01/) | DesignDemo.java<br>Enrollment.java<br>Grade.java<br>Graduate.java<br>Student.java<br>Transcript.java<br>Undergraduate.java | `java -cp out DesignDemo` |
| [25-2](./25-02/) | DesignDemo.java<br>Enrollment.java<br>Grade.java<br>Graduate.java<br>Student.java<br>Transcript.java<br>Undergraduate.java | `java -cp out DesignDemo` |
| [25-3](./25-03/) | DuplicateStudentException.java<br>Enrollment.java<br>Grade.java<br>Graduate.java<br>InMemoryStudentRepository.java<br>RepositoryDemo.java<br>Student.java<br>StudentRepository.java<br>StudentService.java<br>Transcript.java<br>Undergraduate.java | `java -cp out RepositoryDemo` |
| [25-4](./25-04/) | DuplicateStudentException.java<br>Enrollment.java<br>Grade.java<br>Graduate.java<br>ImportDemo.java<br>ImportError.java<br>ImportReport.java<br>InMemoryStudentRepository.java<br>Student.java<br>StudentCsvImporter.java<br>StudentRepository.java<br>StudentService.java<br>Transcript.java<br>Undergraduate.java | `java -cp out ImportDemo` |
