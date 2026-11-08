<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第2章のサンプルコード

「Java基本文法 変数・制御構造・メソッドの基礎」の章のサンプルコードです。

本文の「サンプルコード2-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 21以降）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [2-1](./02-01/) | Sample.java | `java -cp out Sample` |
| [2-2](./02-02/) | TypeConversionExample.java | `java -cp out TypeConversionExample` |
| [2-3](./02-03/) | IOExample.java | `java -cp out IOExample` |
| [2-4](./02-04/) | SwitchExample.java | `java -cp out SwitchExample` |
| [2-5](./02-05/) | Calculator.java | `java -cp out Calculator` |
| [2-6](./02-06/) | InputExample.java | `java -cp out InputExample` |
