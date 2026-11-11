<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第21章のサンプルコード

「ユニットテストと品質保証 信頼性の高いソフトウェアを作る」の章のサンプルコードです。

本文の「サンプルコード21-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 25以降。標準入出力に`IO`クラスを使うため）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [21-1](./21-01/) | AverageCheck.java | `java -cp out AverageCheck` |
| [21-2](./21-02/) | Calculator.java | （mainメソッドなし） |
| [21-3](./21-03/) | Calculator.java<br>CalculatorManualTest.java | `java -cp out CalculatorManualTest` |
| [21-4](./21-04/) | UserRepository.java<br>UserService.java | （mainメソッドなし） |
| [21-7](./21-07/) | UserRepository.java<br>UserService.java | （mainメソッドなし） |
| [21-8](./21-08/) | UserRepository.java<br>UserRepositoryStub.java<br>UserService.java<br>UserServiceManualTest.java | `java -cp out UserServiceManualTest` |
| [21-10](./21-10/) | StringCalculator.java | （mainメソッドなし） |
| [21-12](./21-12/) | UserRepository.java<br>UserService.java<br>UserServiceTest.java | （mainメソッドなし）<br>外部ライブラリ: org.junit.jupiter:junit-jupiter-api:5.13.4, org.apiguardian:apiguardian-api:1.1.2, org.opentest4j:opentest4j:1.3.0, org.mockito:mockito-core:5.20.0, org.mockito:mockito-junit-jupiter:5.20.0 |
