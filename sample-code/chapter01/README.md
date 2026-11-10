<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第1章のサンプルコード

「Java入門と開発環境構築 プログラミングの新たな世界への第一歩」の章のサンプルコードです。

本文の「サンプルコード1-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 25以降。標準入出力に`IO`クラスを使うため）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [1-2](./01-02/) | Product.java | （mainメソッドなし） |
| [1-3](./01-03/) | Main.java<br>Product.java | `java -cp out Main` |
| [1-4](./01-04/) | HelloWorld.java | `java -cp out HelloWorld` |
| [1-7](./01-07/) | Hello.java | `java -cp out Hello` |
| [1-8](./01-08/) | Greeting.java | `java -cp out Greeting` |
| [1-9](./01-09/) | InstanceMain.java | `java -cp out InstanceMain` |
| [1-12](./01-12/) | StandardOutput.java | `java -cp out StandardOutput` |
| [1-13](./01-13/) | InputExample.java | `java -cp out InputExample` |
