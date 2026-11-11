<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第8章のサンプルコード

「Javaの特殊なクラス形式 列挙型とレコードによる効率的な実装」の章のサンプルコードです。

本文の「サンプルコード8-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 25以降。標準入出力に`IO`クラスを使うため）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [8-1](./08-01/) | DayOfWeek.java | （mainメソッドなし） |
| [8-2](./08-02/) | DayOfWeek.java<br>EnumExample.java | `java -cp out EnumExample` |
| [8-3](./08-03/) | DayOfWeek.java<br>EnumMethodsExample.java | `java -cp out EnumMethodsExample` |
| [8-4](./08-04/) | Planet.java | （mainメソッドなし） |
| [8-5](./08-05/) | TrafficLight.java<br>TrafficLightDemo.java | `java -cp out TrafficLightDemo` |
| [8-6](./08-06/) | Operation.java<br>OperationDemo.java | `java -cp out OperationDemo` |
| [8-7](./08-07/) | DatabaseConnection.java | （mainメソッドなし） |
| [8-8](./08-08/) | PaymentStrategy.java<br>PaymentStrategyDemo.java | `java -cp out PaymentStrategyDemo` |
| [8-9](./08-09/) | DocumentType.java<br>Printable.java | （mainメソッドなし） |
| [8-10](./08-10/) | EnumCollectionExample.java | `java -cp out EnumCollectionExample` |
| [8-11](./08-11/) | Person.java | （mainメソッドなし） |
| [8-13](./08-13/) | Person.java<br>RecordExample.java | `java -cp out RecordExample` |
| [8-14](./08-14/) | BankAccount.java | （mainメソッドなし） |
| [8-15](./08-15/) | Rectangle.java | （mainメソッドなし） |
| [8-16](./08-16/) | Temperature.java | （mainメソッドなし） |
| [8-17](./08-17/) | Configuration.java | （mainメソッドなし） |
| [8-18](./08-18/) | PatternMatchingExample.java<br>Point.java | `java -cp out PatternMatchingExample` |
| [8-19](./08-19/) | LocalRecordExample.java | （mainメソッドなし） |
| [8-20](./08-20/) | Address.java<br>Company.java<br>Employee.java | （mainメソッドなし） |
| [8-22](./08-22/) | Money.java | （mainメソッドなし） |
| [8-23](./08-23/) | Circle.java<br>Rectangle.java<br>Shape.java<br>ShapeCalculator.java<br>Triangle.java | （mainメソッドなし） |
| [8-24](./08-24/) | Customer.java<br>Order.java<br>OrderItem.java<br>OrderProcessor.java | （mainメソッドなし） |
| [8-25](./08-25/) | Order.java<br>OrderDemo.java<br>OrderStatus.java | `java -cp out OrderDemo` |
| [8-26](./08-26/) | CharacterClass.java<br>CharacterStats.java<br>GameCharacter.java<br>GameDemo.java | `java -cp out GameDemo` |
| [8-27](./08-27/) | OuterClass.java | `java -cp out OuterClass` |
| [8-28](./08-28/) | MathOperations.java | `java -cp out MathOperations` |
| [8-29](./08-29/) | LocalClassExample.java | （mainメソッドなし） |
| [8-30](./08-30/) | AnonymousClassExample.java | `java -cp out AnonymousClassExample` |
| [8-31](./08-31/) | StringList.java | `java -cp out StringList` |
