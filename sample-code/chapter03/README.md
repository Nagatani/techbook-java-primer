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
| [3-10](./03-10/) | Student.java | （mainメソッドなし） |
| [3-11](./03-11/) | DefaultValueDemo.java | `java -cp out DefaultValueDemo` |
| [3-13](./03-13/) | Product.java | （mainメソッドなし） |
| [3-14](./03-14/) | Product.java<br>ProductTest.java | `java -cp out ProductTest` |
| [3-16](./03-16/) | Rectangle.java<br>RectangleExample.java | `java -cp out RectangleExample` |
| [3-21](./03-21/) | Person.java | （mainメソッドなし） |
| [3-22](./03-22/) | Person.java<br>PersonTest.java | `java -cp out PersonTest` |
| [3-23](./03-23/) | Book.java | （mainメソッドなし） |
| [3-24](./03-24/) | Book.java<br>BookTest.java | `java -cp out BookTest` |
| [3-25](./03-25/) | OverloadExample.java<br>PrintHelper.java | `java -cp out OverloadExample` |
| [3-30](./03-30/) | Book.java | （mainメソッドなし） |
| [3-31](./03-31/) | Book.java<br>BookClassTest.java | `java -cp out BookClassTest` |
| [3-32](./03-32/) | Book.java<br>BookTest.java | `java -cp out BookTest` |
| [3-33](./03-33/) | Student.java | （mainメソッドなし） |
| [3-34](./03-34/) | Student.java<br>StudentAccessTest.java | `java -cp out StudentAccessTest` |
| [3-35](./03-35/) | BadExample.java<br>Product.java | `java -cp out BadExample` |
| [3-36](./03-36/) | Product.java | （mainメソッドなし） |
| [3-38](./03-38/) | MathHelper.java<br>StaticExample.java | `java -cp out StaticExample` |
| [3-39](./03-39/) | StaticMemberExample.java | `java -cp out StaticMemberExample` |
| [3-40](./03-40/) | MathUtils.java | （mainメソッドなし） |
| [3-41](./03-41/) | Student.java | （mainメソッドなし） |
| [3-42](./03-42/) | Constants.java | （mainメソッドなし） |
| [3-45](./03-45/) | Student.java | （mainメソッドなし） |
| [3-46](./03-46/) | NullCheckExample.java<br>Student.java | `java -cp out NullCheckExample` |
| [3-48](./03-48/) | Book.java | （mainメソッドなし） |
| [3-49](./03-49/) | Book.java<br>BookLibraryTest.java | `java -cp out BookLibraryTest` |
| [3-50](./03-50/) | CartItem.java<br>ShoppingCart.java | （mainメソッドなし） |
| [3-52](./03-52/) | StringComparison.java | `java -cp out StringComparison` |
