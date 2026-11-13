<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第12章のサンプルコード

「Stream APIと高度なコレクション操作 宣言的なデータ処理技法を覚えよう」の章のサンプルコードです。

本文の「サンプルコード12-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 25以降。標準入出力に`IO`クラスを使うため）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [12-4](./12-04/) | AdvancedSortExample.java | `java -cp out AdvancedSortExample` |
| [12-5](./12-05/) | StreamExample.java<br>Student.java | `java -cp out StreamExample` |
| [12-6](./12-06/) | OptionalCreationExample.java | `java -cp out OptionalCreationExample` |
| [12-7](./12-07/) | OptionalRetrievalExample.java | `java -cp out OptionalRetrievalExample` |
| [12-8](./12-08/) | OptionalPresenceExample.java | `java -cp out OptionalPresenceExample` |
| [12-9](./12-09/) | OptionalTransformationExample.java | `java -cp out OptionalTransformationExample` |
| [12-10](./12-10/) | OptionalStreamExample.java | `java -cp out OptionalStreamExample` |
| [12-11](./12-11/) | OptionalPracticalExample.java | `java -cp out OptionalPracticalExample` |
| [12-12](./12-12/) | OptionalAntiPatternsExample.java | `java -cp out OptionalAntiPatternsExample` |
| [12-14](./12-14/) | ParallelStreamExample.java | `java -cp out ParallelStreamExample` |
| [12-16](./12-16/) | FlatMapExample.java | `java -cp out FlatMapExample` |
| [12-17](./12-17/) | ReduceExample.java<br>Student.java | `java -cp out ReduceExample` |
| [12-18](./12-18/) | CollectorsExample.java<br>Student.java | `java -cp out CollectorsExample` |
| [12-19](./12-19/) | NullSafeProcessingExample.java | `java -cp out NullSafeProcessingExample` |
| [12-20](./12-20/) | ShortCircuitExample.java | `java -cp out ShortCircuitExample` |
| [12-21](./12-21/) | ToArrayExample.java | `java -cp out ToArrayExample` |
| [12-22](./12-22/) | ComplexProcessingExample.java<br>Student.java | `java -cp out ComplexProcessingExample` |
