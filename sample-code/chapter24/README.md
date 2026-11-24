<!-- このファイルは scripts/sample-code.js sync が生成します -->
# 第24章のサンプルコード

「データベースプログラミング JDBCで実現するデータの永続化」の章のサンプルコードです。

本文の「サンプルコード24-N」の全文です。ディレクトリ名がサンプルコードの番号に対応します。
本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています。

各ディレクトリで次のようにコンパイルし、表の「実行」のコマンドで実行します（Java 25以降。標準入出力に`IO`クラスを使うため）。

```
javac -d out *.java
```

外部ライブラリを使うサンプルは、表に示したライブラリ（Maven Centralの「グループ:名前:バージョン」）のJARを `-cp` に加えてコンパイル・実行します。

| サンプルコード | ファイル | 実行 |
|---|---|---|
| [24-4](./24-04/) | ConnectExample.java | `java -cp out ConnectExample`<br>外部ライブラリ: com.h2database:h2:2.5.252 |
| [24-5](./24-05/) | CrudExample.java | `java -cp out CrudExample`<br>外部ライブラリ: com.h2database:h2:2.5.252 |
| [24-6](./24-06/) | LoginExample.java | `java -cp out LoginExample`<br>外部ライブラリ: com.h2database:h2:2.5.252 |
| [24-7](./24-07/) | DaoExample.java<br>JdbcUserDao.java<br>User.java<br>UserDao.java | `java -cp out DaoExample`<br>外部ライブラリ: com.h2database:h2:2.5.252 |
| [24-8](./24-08/) | DaoExample.java<br>JdbcUserDao.java<br>User.java<br>UserDao.java | `java -cp out DaoExample`<br>外部ライブラリ: com.h2database:h2:2.5.252 |
| [24-9](./24-09/) | DateRangeExample.java | `java -cp out DateRangeExample`<br>外部ライブラリ: com.h2database:h2:2.5.252 |
| [24-10](./24-10/) | TransferExample.java | `java -cp out TransferExample`<br>外部ライブラリ: com.h2database:h2:2.5.252 |
| [24-11](./24-11/) | BatchExample.java | `java -cp out BatchExample`<br>外部ライブラリ: com.h2database:h2:2.5.252 |
