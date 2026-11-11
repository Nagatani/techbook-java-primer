<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第6章のサンプルコード

「不変性とfinalキーワード 安全で予測可能なコードの設計」の章のサンプルコードです。

本文の「サンプルコード6-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 25以降。標準入出力に`IO`クラスを使うため）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [6-1](./06-01/) | MutablePoint.java<br>ThreadSafetyProblem.java | `java -cp out ThreadSafetyProblem` |
| [6-3](./06-03/) | ImmutablePoint.java | （mainメソッドなし） |
| [6-4](./06-04/) | Constants.java | （mainメソッドなし） |
| [6-5](./06-05/) | SecurityManager.java | （mainメソッドなし） |
| [6-6](./06-06/) | ImmutablePerson.java | （mainメソッドなし） |
| [6-7](./06-07/) | ImmutablePerson.java | （mainメソッドなし） |
| [6-8](./06-08/) | ImmutableExample.java<br>ImmutablePerson.java | `java -cp out ImmutableExample` |
| [6-9](./06-09/) | DateRange.java | （mainメソッドなし） |
| [6-10](./06-10/) | Book.java | （mainメソッドなし） |
| [6-11](./06-11/) | Book.java<br>BuilderExample.java | `java -cp out BuilderExample` |
