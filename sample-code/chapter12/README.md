<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第12章のサンプルコード

「Stream APIと高度なコレクション操作 宣言的なデータ処理技法を覚えよう」の章のサンプルコードです。

本文の「サンプルコード12-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 21以降）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [12-3](./12-03/) | AdvancedSortExample.java | `java -cp out AdvancedSortExample` |
| [12-7](./12-07/) | AdvancedSortExample.java<br>StreamExample.java | `java -cp out StreamExample` |
| [12-8](./12-08/) | OptionalCreationExample.java | `java -cp out OptionalCreationExample` |
| [12-9](./12-09/) | OptionalRetrievalExample.java | `java -cp out OptionalRetrievalExample` |
| [12-10](./12-10/) | OptionalPresenceExample.java | `java -cp out OptionalPresenceExample` |
| [12-11](./12-11/) | OptionalTransformationExample.java | `java -cp out OptionalTransformationExample` |
| [12-12](./12-12/) | OptionalStreamExample.java | `java -cp out OptionalStreamExample` |
| [12-13](./12-13/) | OptionalPracticalExample.java | `java -cp out OptionalPracticalExample` |
| [12-14](./12-14/) | OptionalAntiPatternsExample.java | `java -cp out OptionalAntiPatternsExample` |
| [12-16](./12-16/) | ParallelStreamExample.java | `java -cp out ParallelStreamExample` |
| [12-18](./12-18/) | FlatMapExample.java | `java -cp out FlatMapExample` |
| [12-19](./12-19/) | AdvancedSortExample.java<br>ReduceExample.java | `java -cp out ReduceExample` |
| [12-20](./12-20/) | AdvancedSortExample.java<br>CollectorsExample.java | `java -cp out CollectorsExample` |
| [12-21](./12-21/) | AdvancedSortExample.java<br>OptionalExample.java | `java -cp out OptionalExample` |
| [12-22](./12-22/) | AdvancedSortExample.java<br>OptionalChainingExample.java | `java -cp out OptionalChainingExample` |
| [12-23](./12-23/) | NullSafeProcessingExample.java | `java -cp out NullSafeProcessingExample` |
| [12-24](./12-24/) | ShortCircuitExample.java | `java -cp out ShortCircuitExample` |
| [12-25](./12-25/) | ToArrayExample.java | `java -cp out ToArrayExample` |
| [12-26](./12-26/) | AdvancedSortExample.java<br>ComplexProcessingExample.java | `java -cp out ComplexProcessingExample` |
