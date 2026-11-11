<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第4章のサンプルコード

「クラスとインスタンス オブジェクトの生成と連携」の章のサンプルコードです。

本文の「サンプルコード4-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 25以降。標準入出力に`IO`クラスを使うため）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [4-1](./04-01/) | BankAccountV0.java<br>ProblemDemo.java | `java -cp out ProblemDemo` |
| [4-2](./04-02/) | BankAccount.java | `java -cp out BankAccountTest` |
| [4-3](./04-03/) | DataProcessor.java | （mainメソッドなし） |
| [4-6](./04-06/) | MathUtils.java | `java -cp out MathUtilsTest` |
| [4-7](./04-07/) | Product.java | `java -cp out ProductTest` |
| [4-8](./04-08/) | Employee.java | （mainメソッドなし） |
| [4-9](./04-09/) | BankAccountV1.java | （mainメソッドなし） |
| [4-10](./04-10/) | BankAccountV1.java<br>BankAccountV1Test.java | `java -cp out BankAccountV1Test` |
| [4-11](./04-11/) | BankAccountV2.java | （mainメソッドなし） |
| [4-12](./04-12/) | BankAccountV2.java<br>BankAccountV2Test.java | `java -cp out BankAccountV2Test` |
| [4-13](./04-13/) | BankAccountV3.java | （mainメソッドなし） |
| [4-20](./04-20/) | Example.java | （mainメソッドなし） |
| [4-22](./04-22/) | User.java | （mainメソッドなし） |
| [4-23](./04-23/) | User.java<br>UserTest.java | `java -cp out UserTest` |
| [4-24](./04-24/) | Book.java | （mainメソッドなし） |
| [4-26](./04-26/) | DatabaseConfig.java | （mainメソッドなし） |
| [4-27](./04-27/) | EventProcessor.java | （mainメソッドなし） |
| [4-28](./04-28/) | HttpClient.java | （mainメソッドなし） |
| [4-29](./04-29/) | Logger.java | （mainメソッドなし） |
| [4-33](./04-33/) | BankAccount.java | （mainメソッドなし） |
