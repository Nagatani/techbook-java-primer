<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第20章のサンプルコード

「JTableとJListの基礎 表形式データの表示」の章のサンプルコードです。

本文の「サンプルコード20-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 21以降）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [20-1](./20-01/) | BasicTableExample.java | `java -cp out BasicTableExample` |
| [20-2](./20-02/) | Person.java<br>PersonTableModel.java<br>SortableTableExample.java | `java -cp out SortableTableExample` |
| [20-3](./20-03/) | BasicTreeExample.java | `java -cp out BasicTreeExample` |
| [20-4](./20-04/) | DynamicTreeExample.java | `java -cp out DynamicTreeExample` |
| [20-5](./20-05/) | AdvancedListExample.java | `java -cp out AdvancedListExample` |
| [20-6](./20-06/) | CustomEditorExample.java | `java -cp out CustomEditorExample` |
| [20-7](./20-07/) | IntegratedDataManagementApp.java<br>Task.java | `java -cp out IntegratedDataManagementApp` |
