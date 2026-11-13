<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第22章のサンプルコード

「ドキュメントと外部ライブラリ チーム開発を支える技術」の章のサンプルコードです。

本文の「サンプルコード22-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 25以降。標準入出力に`IO`クラスを使うため）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [22-2](./22-02/) | Task.java | （mainメソッドなし） |
| [22-4](./22-04/) | GsonAdvancedExample.java<br>Task.java | `java -cp out GsonAdvancedExample`<br>外部ライブラリ: com.google.code.gson:gson:2.15.0 |
| [22-6](./22-06/) | JacksonExample.java | `java -cp out JacksonExample`<br>外部ライブラリ: com.fasterxml.jackson.core:jackson-databind:2.23.0, com.fasterxml.jackson.core:jackson-core:2.23.0, com.fasterxml.jackson.core:jackson-annotations:2.23 |
| [22-8](./22-08/) | CommonsLangExample.java | `java -cp out CommonsLangExample`<br>外部ライブラリ: org.apache.commons:commons-lang3:3.22.0 |
| [22-10](./22-10/) | CommonsIOExample.java | `java -cp out CommonsIOExample`<br>外部ライブラリ: commons-io:commons-io:2.22.0 |
| [22-12](./22-12/) | OkHttpExample.java | `java -cp out OkHttpExample`<br>外部ライブラリ: com.squareup.okhttp3:okhttp:4.12.0, com.squareup.okio:okio-jvm:3.6.0, org.jetbrains.kotlin:kotlin-stdlib:1.8.21 |
| [22-14](./22-14/) | LombokExample.java<br>User.java | `java -cp out LombokExample`<br>外部ライブラリ: org.projectlombok:lombok:1.18.48 |
| [22-15](./22-15/) | Product.java<br>ProductExample.java | `java -cp out ProductExample`<br>外部ライブラリ: org.projectlombok:lombok:1.18.48 |
| [22-17](./22-17/) | GuavaExample.java | `java -cp out GuavaExample`<br>外部ライブラリ: com.google.guava:guava:33.8.0-jre, com.google.guava:failureaccess:1.0.3 |
