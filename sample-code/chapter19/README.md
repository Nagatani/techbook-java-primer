<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第19章のサンプルコード

「高度なイベント処理パターン プロフェッショナルなGUIアプリケーションの実装」の章のサンプルコードです。

本文の「サンプルコード19-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 21以降）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [19-1](./19-01/) | MouseEventCompleteExample.java | `java -cp out MouseEventCompleteExample` |
| [19-2](./19-02/) | KeyboardEventAdvancedExample.java | `java -cp out KeyboardEventAdvancedExample` |
| [19-3](./19-03/) | DocumentListenerExample.java | `java -cp out DocumentListenerExample` |
| [19-4](./19-04/) | CustomEventExample.java | `java -cp out CustomEventExample` |
| [19-5](./19-05/) | JavaBeansEventExample.java | `java -cp out JavaBeansEventExample` |
| [19-6](./19-06/) | DragAndDropExample.java | `java -cp out DragAndDropExample` |
| [19-7](./19-07/) | EventOptimizationExample.java | `java -cp out EventOptimizationExample` |
| [19-8](./19-08/) | SwingWorkerExample.java | `java -cp out SwingWorkerExample` |
