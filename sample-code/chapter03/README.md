<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第3章のサンプルコード

「オブジェクト指向の考え方 よりよいプログラミング技法を求めて」の章のサンプルコードです。

本文の「サンプルコード3-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 25以降。標準入出力に`IO`クラスを使うため）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [3-3](./03-03/) | ObjectOrientedExample.java<br>Student.java | `java -cp out ObjectOrientedExample` |
| [3-4](./03-04/) | BankAccount.java | （mainメソッドなし） |
| [3-5](./03-05/) | BankAccount.java<br>BankAccountTest.java | `java -cp out BankAccountTest` |
| [3-6](./03-06/) | Book.java<br>Product.java | （mainメソッドなし） |
| [3-8](./03-08/) | BankTransferPayment.java<br>CreditCardPayment.java<br>PaymentMethod.java | （mainメソッドなし） |
| [3-9](./03-09/) | BankTransferPayment.java<br>CreditCardPayment.java<br>PaymentMethod.java<br>PolymorphismTest.java | `java -cp out PolymorphismTest` |
| [3-11](./03-11/) | Student.java | （mainメソッドなし） |
| [3-12](./03-12/) | DefaultValueDemo.java | `java -cp out DefaultValueDemo` |
| [3-14](./03-14/) | Rectangle.java<br>RectangleExample.java | `java -cp out RectangleExample` |
| [3-19](./03-19/) | Person.java | （mainメソッドなし） |
| [3-20](./03-20/) | Person.java<br>PersonTest.java | `java -cp out PersonTest` |
| [3-21](./03-21/) | Book.java | （mainメソッドなし） |
| [3-22](./03-22/) | Book.java<br>BookTest.java | `java -cp out BookTest` |
| [3-23](./03-23/) | OverloadExample.java<br>PrintHelper.java | `java -cp out OverloadExample` |
| [3-28](./03-28/) | Book.java | （mainメソッドなし） |
| [3-29](./03-29/) | Book.java<br>BookTest.java | `java -cp out BookTest` |
| [3-30](./03-30/) | Student.java | （mainメソッドなし） |
| [3-31](./03-31/) | Student.java<br>StudentAccessTest.java | `java -cp out StudentAccessTest` |
| [3-32](./03-32/) | BadExample.java<br>Product.java | `java -cp out BadExample` |
| [3-33](./03-33/) | MathHelper.java<br>StaticExample.java | `java -cp out StaticExample` |
| [3-34](./03-34/) | StaticMemberExample.java | `java -cp out StaticMemberExample` |
| [3-35](./03-35/) | MathUtils.java | （mainメソッドなし） |
| [3-36](./03-36/) | Student.java | （mainメソッドなし） |
| [3-37](./03-37/) | Constants.java | （mainメソッドなし） |
| [3-40](./03-40/) | Student.java | （mainメソッドなし） |
| [3-41](./03-41/) | NullCheckExample.java<br>Student.java | `java -cp out NullCheckExample` |
| [3-43](./03-43/) | Book.java | （mainメソッドなし） |
| [3-44](./03-44/) | Book.java<br>BookLibraryTest.java | `java -cp out BookLibraryTest` |
| [3-45](./03-45/) | CartItem.java<br>ShoppingCart.java | （mainメソッドなし） |
| [3-47](./03-47/) | StringComparison.java | `java -cp out StringComparison` |
