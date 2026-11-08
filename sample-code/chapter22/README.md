<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第22章のサンプルコード

「ドキュメントと外部ライブラリ チーム開発を支える技術」の章のサンプルコードです。

本文の「サンプルコード22-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 21以降）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [22-2](./22-02/) | Task.java | （mainメソッドなし） |
| [22-5](./22-05/) | GsonAdvancedExample.java<br>Task.java | `java -cp out GsonAdvancedExample`<br>外部ライブラリ: com.google.code.gson:gson:2.10.1 |
| [22-7](./22-07/) | JacksonExample.java | `java -cp out JacksonExample`<br>外部ライブラリ: com.fasterxml.jackson.core:jackson-databind:2.15.2, com.fasterxml.jackson.core:jackson-core:2.15.2, com.fasterxml.jackson.core:jackson-annotations:2.15.2 |
| [22-9](./22-09/) | CommonsLangExample.java | `java -cp out CommonsLangExample`<br>外部ライブラリ: org.apache.commons:commons-lang3:3.12.0 |
| [22-11](./22-11/) | CommonsIOExample.java | `java -cp out CommonsIOExample`<br>外部ライブラリ: commons-io:commons-io:2.11.0 |
| [22-13](./22-13/) | OkHttpExample.java | `java -cp out OkHttpExample`<br>外部ライブラリ: com.squareup.okhttp3:okhttp:4.11.0, com.squareup.okio:okio-jvm:3.2.0, org.jetbrains.kotlin:kotlin-stdlib:1.8.21 |
| [22-18](./22-18/) | GuavaExample.java | `java -cp out GuavaExample`<br>外部ライブラリ: com.google.guava:guava:32.1.1-jre |
| [22-25](./22-25/) | UserService.java | （mainメソッドなし） |
| [22-29](./22-29/) | UserService.java | （mainメソッドなし） |
| [22-30](./22-30/) | UserService.java | （mainメソッドなし） |
