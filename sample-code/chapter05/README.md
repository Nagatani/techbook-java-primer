<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第5章のサンプルコード

「継承とポリモーフィズム コードの再利用と柔軟性の実現」の章のサンプルコードです。

本文の「サンプルコード5-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 21以降）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [5-1](./05-01/) | PaymentProcessor.java<br>PaymentResult.java | （mainメソッドなし） |
| [5-2](./05-02/) | CreditCardPayment.java<br>PayPalPayment.java<br>PaymentProcessor.java<br>PaymentResult.java | （mainメソッドなし） |
| [5-4](./05-04/) | PDFDocument.java<br>WordDocument.java | （mainメソッドなし） |
| [5-5](./05-05/) | ExcelDocument.java | （mainメソッドなし） |
| [5-6](./05-06/) | Document.java | （mainメソッドなし） |
| [5-7](./05-07/) | Document.java<br>ExcelDocument.java<br>PDFDocument.java<br>WordDocument.java | （mainメソッドなし） |
| [5-8](./05-08/) | Developer.java<br>Employee.java<br>Manager.java | （mainメソッドなし） |
| [5-9](./05-09/) | MyStack.java<br>StackProblem.java | `java -cp out StackProblem` |
| [5-10](./05-10/) | MyStack.java | （mainメソッドなし） |
| [5-12](./05-12/) | Rectangle.java<br>RectangleSquareTest.java<br>Square.java | `java -cp out RectangleSquareTest` |
| [5-13](./05-13/) | Shape.java | （mainメソッドなし） |
| [5-15](./05-15/) | MyStack.java | （mainメソッドなし） |
| [5-16](./05-16/) | ApprovingEmployee.java<br>Employee.java<br>ReportingEmployee.java | （mainメソッドなし） |
| [5-18](./05-18/) | Character.java<br>CharacterOverrideTest.java<br>Knight.java<br>Wizard.java | `java -cp out CharacterOverrideTest` |
| [5-24](./05-24/) | DrawingAppBefore.java | `java -cp out DrawingAppBefore` |
| [5-25](./05-25/) | DrawingAppAfter.java | `java -cp out DrawingAppAfter` |
