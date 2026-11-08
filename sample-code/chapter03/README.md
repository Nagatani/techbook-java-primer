<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第3章のサンプルコード

「オブジェクト指向の考え方 よりよいプログラミング技法を求めて」の章のサンプルコードです。

本文の「サンプルコード3-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 21以降）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [3-3](./03-03/) | ProceduralExample.java | `java -cp out ProceduralExample` |
| [3-4](./03-04/) | ObjectOrientedExample.java<br>Student.java | `java -cp out ObjectOrientedExample` |
| [3-5](./03-05/) | BankAccount.java | （mainメソッドなし） |
| [3-6](./03-06/) | BankAccount.java<br>BankAccountTest.java | `java -cp out BankAccountTest` |
| [3-7](./03-07/) | Book.java<br>Product.java | （mainメソッドなし） |
| [3-9](./03-09/) | BankTransferPayment.java<br>CreditCardPayment.java<br>PaymentMethod.java | （mainメソッドなし） |
| [3-10](./03-10/) | BankTransferPayment.java<br>CreditCardPayment.java<br>PaymentMethod.java<br>PolymorphismTest.java | `java -cp out PolymorphismTest` |
| [3-11](./03-11/) | Student.java | （mainメソッドなし） |
| [3-12](./03-12/) | DefaultValueDemo.java | `java -cp out DefaultValueDemo` |
| [3-14](./03-14/) | Product.java | （mainメソッドなし） |
| [3-15](./03-15/) | Product.java<br>ProductTest.java | `java -cp out ProductTest` |
| [3-17](./03-17/) | Rectangle.java<br>RectangleExample.java | `java -cp out RectangleExample` |
| [3-22](./03-22/) | Person.java | （mainメソッドなし） |
| [3-23](./03-23/) | Person.java<br>PersonTest.java | `java -cp out PersonTest` |
| [3-24](./03-24/) | Book.java | （mainメソッドなし） |
| [3-25](./03-25/) | Book.java<br>BookTest.java | `java -cp out BookTest` |
| [3-26](./03-26/) | OverloadExample.java<br>PrintHelper.java | `java -cp out OverloadExample` |
| [3-31](./03-31/) | Person.java | （mainメソッドなし） |
| [3-32](./03-32/) | Book.java | （mainメソッドなし） |
| [3-33](./03-33/) | Book.java<br>BookClassTest.java | `java -cp out BookClassTest` |
| [3-35](./03-35/) | Student.java | （mainメソッドなし） |
| [3-36](./03-36/) | Student.java<br>StudentAccessTest.java | `java -cp out StudentAccessTest` |
| [3-37](./03-37/) | BadExample.java<br>Product.java | `java -cp out BadExample` |
| [3-38](./03-38/) | Product.java | （mainメソッドなし） |
| [3-40](./03-40/) | MathHelper.java<br>StaticExample.java | `java -cp out StaticExample` |
| [3-41](./03-41/) | StaticMemberExample.java | `java -cp out StaticMemberExample` |
| [3-42](./03-42/) | MathUtils.java | （mainメソッドなし） |
| [3-43](./03-43/) | Student.java | （mainメソッドなし） |
| [3-44](./03-44/) | Constants.java | （mainメソッドなし） |
| [3-47](./03-47/) | Student.java | （mainメソッドなし） |
| [3-48](./03-48/) | NullCheckExample.java<br>Student.java | `java -cp out NullCheckExample` |
| [3-50](./03-50/) | Book.java | （mainメソッドなし） |
| [3-51](./03-51/) | Book.java<br>BookLibraryTest.java | `java -cp out BookLibraryTest` |
| [3-52](./03-52/) | ShoppingCart.java | （mainメソッドなし） |
| [3-54](./03-54/) | StringComparison.java | `java -cp out StringComparison` |
| [3-55](./03-55/) | Student.java | （mainメソッドなし） |
| [3-65](./03-65/) | MethodsPractice.java | `java -cp out MethodsPractice` |
