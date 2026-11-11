<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第17章のサンプルコード

「GUIプログラミングの基礎 Swingで作るデスクトップアプリ」の章のサンプルコードです。

本文の「サンプルコード17-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 25以降。標準入出力に`IO`クラスを使うため）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [17-1](./17-01/) | HelloSwing.java | `java -cp out HelloSwing` |
| [17-2](./17-02/) | EDTBasicExample.java | `java -cp out EDTBasicExample` |
| [17-3](./17-03/) | WindowSettings.java | `java -cp out WindowSettings` |
| [17-4](./17-04/) | HelloLabel.java | `java -cp out HelloLabel` |
| [17-5](./17-05/) | MultipleComponents.java | `java -cp out MultipleComponents` |
| [17-6](./17-06/) | AdvancedLabel.java | `java -cp out AdvancedLabel` |
| [17-7](./17-07/) | AdvancedButton.java | `java -cp out AdvancedButton` |
| [17-8](./17-08/) | TextInputComponents.java | `java -cp out TextInputComponents` |
| [17-9](./17-09/) | SelectionComponents.java | `java -cp out SelectionComponents` |
| [17-10](./17-10/) | BorderLayoutExample.java | `java -cp out BorderLayoutExample` |
| [17-11](./17-11/) | GridLayoutExample.java | `java -cp out GridLayoutExample` |
| [17-12](./17-12/) | FlowLayoutExample.java | `java -cp out FlowLayoutExample` |
| [17-13](./17-13/) | ComplexLayoutExample.java | `java -cp out ComplexLayoutExample` |
| [17-14](./17-14/) | CombinedLayoutExample.java | `java -cp out CombinedLayoutExample` |
| [17-15](./17-15/) | DrawingPanel.java<br>DrawingPanelExample.java | `java -cp out DrawingPanelExample` |
