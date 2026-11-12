<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第11章のサンプルコード

「ラムダ式と関数型インターフェイス 関数型プログラミングへの誘い」の章のサンプルコードです。

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
| [11-5](./11-05/) | ComparatorLambdaExample.java<br>Student.java | `java -cp out ComparatorLambdaExample` |
| [11-6](./11-06/) | Models.java<br>MultiConditionSortExample.java | `java -cp out MultiConditionSortExample` |
| [11-7](./11-07/) | StandardFunctionalInterfaces.java | `java -cp out StandardFunctionalInterfaces` |
| [11-8](./11-08/) | FunctionComposition.java | `java -cp out FunctionComposition` |
| [11-9](./11-09/) | MethodReferenceExample.java | `java -cp out MethodReferenceExample` |
| [11-10](./11-10/) | LambdaVsMethodReference.java<br>Person.java | `java -cp out LambdaVsMethodReference` |
| [11-11](./11-11/) | RunnableLambdaExample.java | `java -cp out RunnableLambdaExample` |
| [11-12](./11-12/) | ExecutorLambdaExample.java | `java -cp out ExecutorLambdaExample` |
| [11-13](./11-13/) | User.java<br>ValidationFramework.java | `java -cp out ValidationFramework` |
| [11-14](./11-14/) | HigherOrderFunctionExample.java<br>TradingTypes.java | `java -cp out HigherOrderFunctionExample` |
| [11-15](./11-15/) | NestedLambdaExample.java | `java -cp out NestedLambdaExample` |
| [11-16](./11-16/) | SideEffectExample.java | `java -cp out SideEffectExample` |
| [11-17](./11-17/) | ResilientService.java | （mainメソッドなし） |
| [11-18](./11-18/) | EventProcessor.java | `java -cp out EventProcessor` |
| [11-19](./11-19/) | MiddlewareChain.java<br>Support.java | `java -cp out MiddlewareChain` |
| [11-20](./11-20/) | DataProcessingTemplate.java | `java -cp out DataProcessingTemplate` |
| [11-21](./11-21/) | AccountState.java<br>EventSourcedAccount.java | （mainメソッドなし） |
| [11-22](./11-22/) | ProcessorFactory.java | `java -cp out ProcessorFactory` |
| [11-23](./11-23/) | LambdaCapture.java | `java -cp out LambdaCapture` |
| [11-24](./11-24/) | FunctionalBuilder.java<br>Person.java | （mainメソッドなし） |
| [11-25](./11-25/) | ConfigurationBuilder.java | `java -cp out ConfigurationBuilder` |
| [11-26](./11-26/) | OrderPipeline.java | `java -cp out OrderPipeline` |
