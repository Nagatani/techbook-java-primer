<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第13章のサンプルコード

「例外処理 障害に強いプログラムの構築」の章のサンプルコードです。

本文の「サンプルコード13-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 21以降）。

```
javac -d out *.java
```

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [13-1](./13-01/) | TryCatchSample.java | `java -cp out TryCatchSample` |
| [13-3](./13-03/) | TryWithResourcesSample.java | （mainメソッドなし） |
| [13-4](./13-04/) | ThrowsExample.java | `java -cp out ThrowsExample` |
| [13-5](./13-05/) | BankAccount.java | （mainメソッドなし） |
| [13-6](./13-06/) | UserService.java | （mainメソッドなし） |
| [13-7](./13-07/) | RetryableOperation.java | `java -cp out RetryableOperation` |
| [13-8](./13-08/) | CircuitBreaker.java | （mainメソッドなし） |
| [13-9](./13-09/) | ExceptionAggregator.java | （mainメソッドなし） |
| [13-10](./13-10/) | MultiResourceExample.java | （mainメソッドなし） |
| [13-11](./13-11/) | ResourcePool.java | （mainメソッドなし） |
| [13-14](./13-14/) | BusinessException.java<br>InsufficientBalanceException.java<br>SystemException.java | （mainメソッドなし） |
| [13-25](./13-25/) | CustomException.java | （mainメソッドなし） |
| [13-26](./13-26/) | CustomException.java | （mainメソッドなし） |
