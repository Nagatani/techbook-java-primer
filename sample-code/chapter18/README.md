<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第18章のサンプルコード

「基本的なイベント処理 ユーザー操作への応答の基礎」の章のサンプルコードです。

本文の「サンプルコード18-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 25以降。標準入出力に`IO`クラスを使うため）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [18-1](./18-01/) | ButtonEventExample.java | `java -cp out ButtonEventExample` |
| [18-3](./18-03/) | GreetingApp.java | `java -cp out GreetingApp` |
| [18-4](./18-04/) | WindowEventExample.java | `java -cp out WindowEventExample` |
| [18-5](./18-05/) | BasicMouseEventExample.java | `java -cp out BasicMouseEventExample` |
| [18-6](./18-06/) | BasicKeyEventExample.java | `java -cp out BasicKeyEventExample` |
| [18-7](./18-07/) | ItemListenerExample.java | `java -cp out ItemListenerExample` |
| [18-8](./18-08/) | FocusListenerExample.java | `java -cp out FocusListenerExample` |
| [18-9](./18-09/) | ChangeListenerExample.java | `java -cp out ChangeListenerExample` |
