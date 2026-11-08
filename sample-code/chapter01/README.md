<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第1章のサンプルコード

「Java入門と開発環境構築 プログラミングの新たな世界への第一歩」の章のサンプルコードです。

本文の「サンプルコード1-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 21以降）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [1-2](./01-02/) | Product.java | （mainメソッドなし） |
| [1-3](./01-03/) | Main.java<br>Product.java | `java -cp out Main` |
| [1-4](./01-04/) | HelloWorld.java | `java -cp out HelloWorld` |
| [1-9](./01-09/) | StandardOutput.java | `java -cp out StandardOutput` |
| [1-10](./01-10/) | InputExample.java | `java -cp out InputExample` |
| [1-16](./01-16/) | DebugExample.java | `java -cp out DebugExample` |
| [1-17](./01-17/) | StepByStepDebug.java | `java -cp out StepByStepDebug` |
