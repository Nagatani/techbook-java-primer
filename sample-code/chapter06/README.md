<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第6章のサンプルコード

「不変性とfinalキーワード 安全で予測可能なコードの設計」の章のサンプルコードです。

本文の「サンプルコード6-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 21以降）。

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
| [6-6](./06-06/) | ImmutablePerson.java<br>String.java | （mainメソッドなし） |
| [6-7](./06-07/) | ImmutablePerson.java | （mainメソッドなし） |
| [6-8](./06-08/) | ImmutableExample.java<br>ImmutablePerson.java | `java -cp out ImmutableExample` |
| [6-9](./06-09/) | DateRange.java | （mainメソッドなし） |
| [6-10](./06-10/) | Book.java | （mainメソッドなし） |
| [6-11](./06-11/) | Book.java<br>BuilderExample.java | `java -cp out BuilderExample` |
| [6-14](./06-14/) | FinalCorrectExample.java | `java -cp out FinalCorrectExample` |
| [6-16](./06-16/) | InitializedFinalExample.java | （mainメソッドなし） |
| [6-19](./06-19/) | ImmutablePerson.java | （mainメソッドなし） |
| [6-20](./06-20/) | BrokenImmutableEvent.java | （mainメソッドなし） |
| [6-22](./06-22/) | ImmutableEvent.java | （mainメソッドなし） |
| [6-23](./06-23/) | ModernImmutableEvent.java | （mainメソッドなし） |
| [6-24](./06-24/) | FinalArrayMisunderstanding.java | `java -cp out FinalArrayMisunderstanding` |
| [6-25](./06-25/) | ImmutableArray.java | （mainメソッドなし） |
| [6-26](./06-26/) | ListImmutabilityMisunderstanding.java | `java -cp out ListImmutabilityMisunderstanding` |
| [6-27](./06-27/) | ImmutablePerson.java<br>MutableEmployee.java | （mainメソッドなし） |
| [6-28](./06-28/) | ImmutablePerson.java | （mainメソッドなし） |
| [6-29](./06-29/) | IneffientImmutableClass.java | （mainメソッドなし） |
| [6-30](./06-30/) | EfficientImmutableClass.java | （mainメソッドなし） |
| [6-31](./06-31/) | OptimizedImmutableClass.java | （mainメソッドなし） |
