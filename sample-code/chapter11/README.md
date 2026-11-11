<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第11章のサンプルコード

「ラムダ式と関数インターフェイス 関数型プログラミングへの誘い」の章のサンプルコードです。

本文の「サンプルコード11-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 25以降。標準入出力に`IO`クラスを使うため）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [11-1](./11-01/) | LambdaIntroExample.java | `java -cp out LambdaIntroExample` |
| [11-4](./11-04/) | FunctionalInterfaceExample.java | `java -cp out FunctionalInterfaceExample` |
| [11-6](./11-06/) | Models.java<br>MultiConditionSortExample.java | `java -cp out MultiConditionSortExample` |
| [11-7](./11-07/) | StandardFunctionalInterfaces.java | `java -cp out StandardFunctionalInterfaces` |
| [11-8](./11-08/) | MethodReferenceExample.java | `java -cp out MethodReferenceExample` |
| [11-9](./11-09/) | LambdaVsMethodReference.java<br>Person.java | `java -cp out LambdaVsMethodReference` |
| [11-10](./11-10/) | RunnableLambdaExample.java | `java -cp out RunnableLambdaExample` |
| [11-11](./11-11/) | MethodReferenceConcurrencyExample.java | `java -cp out MethodReferenceConcurrencyExample` |
| [11-12](./11-12/) | User.java<br>ValidationFramework.java | `java -cp out ValidationFramework` |
| [11-13](./11-13/) | HigherOrderFunctionExample.java<br>TradingTypes.java | `java -cp out HigherOrderFunctionExample` |
| [11-18](./11-18/) | ResilientService.java | （mainメソッドなし） |
| [11-20](./11-20/) | MiddlewareChain.java<br>Support.java | `java -cp out MiddlewareChain` |
| [11-22](./11-22/) | AccountState.java<br>EventSourcedAccount.java | （mainメソッドなし） |
| [11-25](./11-25/) | FunctionalBuilder.java<br>Person.java | （mainメソッドなし） |
