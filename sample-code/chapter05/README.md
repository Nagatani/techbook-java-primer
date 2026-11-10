<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第5章のサンプルコード

「継承とポリモーフィズム コードの再利用と柔軟性の実現」の章のサンプルコードです。

本文の「サンプルコード5-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 25以降。標準入出力に`IO`クラスを使うため）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [5-1](./05-01/) | PaymentProcessor.java<br>PaymentResult.java | （mainメソッドなし） |
| [5-2](./05-02/) | CreditCardPayment.java<br>PayPalPayment.java<br>PaymentProcessor.java<br>PaymentResult.java | （mainメソッドなし） |
| [5-3](./05-03/) | CreditCardPayment.java<br>PayPalPayment.java<br>PaymentExample.java<br>PaymentProcessor.java<br>PaymentResult.java | `java -cp out PaymentExample` |
| [5-4](./05-04/) | PDFDocument.java<br>WordDocument.java | （mainメソッドなし） |
| [5-6](./05-06/) | ExcelDocument.java | （mainメソッドなし） |
| [5-7](./05-07/) | Document.java | （mainメソッドなし） |
| [5-8](./05-08/) | Document.java<br>ExcelDocument.java<br>PDFDocument.java<br>WordDocument.java | （mainメソッドなし） |
| [5-9](./05-09/) | Document.java<br>ExcelDocument.java<br>PDFDocument.java<br>RefactoredDocumentTest.java<br>WordDocument.java | `java -cp out RefactoredDocumentTest` |
| [5-10](./05-10/) | Developer.java<br>Employee.java<br>Manager.java | （mainメソッドなし） |
| [5-11](./05-11/) | Developer.java<br>Employee.java<br>EmployeeTest.java<br>Manager.java | `java -cp out EmployeeTest` |
| [5-12](./05-12/) | MyStack.java<br>StackProblem.java | `java -cp out StackProblem` |
| [5-13](./05-13/) | MyStack.java | （mainメソッドなし） |
| [5-14](./05-14/) | Employee.java<br>EmployeeManagementSystem.java<br>RegularEmployee.java<br>SystemAdministrator.java | （mainメソッドなし） |
| [5-15](./05-15/) | Rectangle.java<br>RectangleSquareTest.java<br>Square.java | `java -cp out RectangleSquareTest` |
| [5-17](./05-17/) | Shape.java | （mainメソッドなし） |
| [5-19](./05-19/) | MyStack.java | （mainメソッドなし） |
| [5-20](./05-20/) | ApprovingEmployee.java<br>Employee.java<br>ReportingEmployee.java | （mainメソッドなし） |
| [5-22](./05-22/) | Character.java<br>CharacterOverrideTest.java<br>Knight.java<br>Wizard.java | `java -cp out CharacterOverrideTest` |
| [5-26](./05-26/) | UserSystemBefore.java<br>Users.java | `java -cp out UserSystemBefore` |
| [5-27](./05-27/) | User.java<br>UserSystemAfter.java<br>Users.java | `java -cp out UserSystemAfter` |
| [5-28](./05-28/) | DrawingAppBefore.java | `java -cp out DrawingAppBefore` |
| [5-29](./05-29/) | DrawingAppAfter.java | `java -cp out DrawingAppAfter` |
