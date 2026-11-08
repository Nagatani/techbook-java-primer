<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第9章のサンプルコード

「コレクションフレームワーク 柔軟なデータ構造と操作の基礎」の章のサンプルコードです。

本文の「サンプルコード9-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 21以降）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [9-1](./09-01/) | StudentManager.java | `java -cp out StudentManager` |
| [9-6](./09-06/) | StudentManagerImproved.java | `java -cp out StudentManagerImproved` |
| [9-7](./09-07/) | ArrayListExample.java | `java -cp out ArrayListExample` |
| [9-10](./09-10/) | HashSetExample.java | `java -cp out HashSetExample` |
| [9-13](./09-13/) | HashMapExample.java | `java -cp out HashMapExample` |
