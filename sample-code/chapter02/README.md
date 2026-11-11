<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第2章のサンプルコード

「Java基本文法 変数・制御構造・メソッドの基礎」の章のサンプルコードです。

本文の「サンプルコード2-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 25以降。標準入出力に`IO`クラスを使うため）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [2-9](./02-09/) | Sample.java | `java -cp out Sample` |
| [2-13](./02-13/) | TypeConversionExample.java | `java -cp out TypeConversionExample` |
| [2-18](./02-18/) | IOExample.java | `java -cp out IOExample` |
| [2-20](./02-20/) | SafeInputExample.java | `java -cp out SafeInputExample` |
| [2-23](./02-23/) | SwitchExample.java | `java -cp out SwitchExample` |
| [2-32](./02-32/) | Calculator.java | `java -cp out Calculator` |
| [2-47](./02-47/) | ImportExample.java | `java -cp out ImportExample` |
