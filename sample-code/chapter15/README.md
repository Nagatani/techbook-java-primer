<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第15章のサンプルコード

「マルチスレッドプログラミング 並行処理と同期制御の実践」の章のサンプルコードです。

本文の「サンプルコード15-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 21以降）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [15-1](./15-01/) | SingleThreadExample.java | `java -cp out SingleThreadExample` |
| [15-4](./15-04/) | UnsafeCounter.java | （mainメソッドなし） |
| [15-9](./15-09/) | Main.java | `java -cp out Main` |
| [15-10](./15-10/) | SynchronizedCounter.java | （mainメソッドなし） |
| [15-11](./15-11/) | BankAccount.java | （mainメソッドなし） |
| [15-12](./15-12/) | VolatileDemo.java | `java -cp out VolatileDemo` |
| [15-13](./15-13/) | AtomicCounterExample.java | （mainメソッドなし） |
| [15-14](./15-14/) | ExecutorExample.java | `java -cp out ExecutorExample` |
| [15-15](./15-15/) | ProducerConsumerExample.java | `java -cp out ProducerConsumerExample` |
| [15-16](./15-16/) | FutureCallableExample.java | `java -cp out FutureCallableExample` |
| [15-17](./15-17/) | CompletableFutureExample.java | `java -cp out CompletableFutureExample` |
| [15-19](./15-19/) | ParallelPerformanceExample.java | `java -cp out ParallelPerformanceExample` |
| [15-20](./15-20/) | ThreadSafeSingletonExamples.java | `java -cp out ThreadSafeSingletonExamples` |
| [15-21](./15-21/) | ConcurrencyPitfallsAndSolutions.java | `java -cp out ConcurrencyPitfallsAndSolutions` |
| [15-22](./15-22/) | ParallelDataPipeline.java | `java -cp out ParallelDataPipeline` |
| [15-23](./15-23/) | ResiliencePatterns.java | `java -cp out ResiliencePatterns` |
| [15-24](./15-24/) | Counter.java | （mainメソッドなし） |
