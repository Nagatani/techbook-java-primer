<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第10章のサンプルコード

「ジェネリクス 型安全なプログラミングの実現」の章のサンプルコードです。

本文の「サンプルコード10-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 25以降。標準入出力に`IO`クラスを使うため）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [10-2](./10-02/) | CollectionProblem.java | `java -cp out CollectionProblem` |
| [10-3](./10-03/) | CollectionSolution.java | `java -cp out CollectionSolution` |
| [10-4](./10-04/) | Box.java<br>BoxExample.java | `java -cp out BoxExample` |
| [10-6](./10-06/) | GenericMethodExample.java | `java -cp out GenericMethodExample` |
| [10-7](./10-07/) | BoundedTypeExample.java<br>NumericBox.java | `java -cp out BoundedTypeExample` |
| [10-8](./10-08/) | WildcardExample.java | `java -cp out WildcardExample` |
| [10-9](./10-09/) | VarBasicExample.java | `java -cp out VarBasicExample` |
| [10-10](./10-10/) | VarLimitations.java | （mainメソッドなし） |
| [10-11](./10-11/) | VarReadabilityExample.java | （mainメソッドなし） |
| [10-12](./10-12/) | VarWithGenericsExample.java | `java -cp out VarWithGenericsExample` |
