<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第7章のサンプルコード

「抽象クラスとインターフェイス 柔軟なシステム設計の契約的なプログラミングの基礎」の章のサンプルコードです。

本文の「サンプルコード7-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 25以降。標準入出力に`IO`クラスを使うため）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [7-1](./07-01/) | Shape.java | （mainメソッドなし） |
| [7-2](./07-02/) | Circle.java<br>Rectangle.java<br>Shape.java | （mainメソッドなし） |
| [7-3](./07-03/) | Circle.java<br>Main.java<br>Rectangle.java<br>Shape.java | `java -cp out Main` |
| [7-4](./07-04/) | Archivable.java<br>Exportable.java | （mainメソッドなし） |
| [7-5](./07-05/) | Archivable.java<br>Exportable.java<br>TextDocument.java | （mainメソッドなし） |
| [7-6](./07-06/) | DefaultMethodDemo.java | `java -cp out DefaultMethodDemo` |
| [7-7](./07-07/) | DataProcessor.java | （mainメソッドなし） |
| [7-8](./07-08/) | A.java | （mainメソッドなし） |
| [7-9](./07-09/) | BaseClass.java | （mainメソッドなし） |
| [7-10](./07-10/) | JsonDemo.java<br>JsonSerializable.java<br>User.java | `java -cp out JsonDemo` |
| [7-11](./07-11/) | DefensiveDemo.java<br>DefensiveInterface.java | `java -cp out DefensiveDemo` |
| [7-12](./07-12/) | Timestamped.java | （mainメソッドなし） |
| [7-13](./07-13/) | Measurable.java | （mainメソッドなし） |
| [7-14](./07-14/) | BadUserService.java<br>User.java | （mainメソッドなし） |
| [7-15](./07-15/) | ServiceConfig.java<br>ServiceV1.java | （mainメソッドなし） |
| [7-16](./07-16/) | FactoryDemo.java<br>ShapeFactory.java | `java -cp out FactoryDemo` |
| [7-17](./07-17/) | DocumentFactory.java | （mainメソッドなし） |
| [7-18](./07-18/) | DatabaseConnectionFactory.java | （mainメソッドなし） |
