# 第23章 ビルドとデプロイ：よくあるエラーと対処法

書籍『Java入門』第23章「ビルドとデプロイ」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。
エラーメッセージは、Java 25（日本語の環境）で確かめたものです。

## 「メイン・マニフェスト属性がありません」

### エラー例

```bash
javac App.java
jar cf app.jar App.class
java -jar app.jar
```

### エラーメッセージ

```
app.jarにメイン・マニフェスト属性がありません
```

英語の環境では`no main manifest attribute, in app.jar`と表示されます。

### 原因

- `java -jar`は、JARの中のマニフェスト（`META-INF/MANIFEST.MF`）の`Main-Class`を見て、どのクラスの`main`メソッドから始めるかを決める
- `jar cf`（`m`なし）で作ると、`Main-Class`のないマニフェストになる
- マニフェストのファイルを渡していても、最後の行のあとに改行がないと、その行は読み飛ばされる。`Main-Class: App`が1行だけのファイルで改行を忘れると、同じエラーになる

### 対処法

```bash
# manifest.txt の中身は「Main-Class: App」と改行
jar cfm app.jar manifest.txt App.class
java -jar app.jar
```

- `jar --create --file app.jar --main-class App App.class`のように、`--main-class`（短い形は`e`）で指定すれば、マニフェストのファイルを書かなくてよい
- 作ったJARのマニフェストは`unzip -p app.jar META-INF/MANIFEST.MF`などで確かめられる

## 外部ライブラリのクラスが見つからない（NoClassDefFoundError）

### エラー例

```bash
javac -cp gson-2.11.0.jar JsonApp.java
jar cfm json.jar manifest.txt JsonApp.class
java -cp gson-2.11.0.jar -jar json.jar
```

### エラーメッセージ

```
Exception in thread "main" java.lang.NoClassDefFoundError: com/google/gson/Gson
	at JsonApp.main(JsonApp.java:4)
Caused by: java.lang.ClassNotFoundException: com.google.gson.Gson
```

### 原因

- コンパイルのときにはライブラリをクラスパスに加えたが、実行のときにはクラスパスにない
- `java -jar`で実行すると、`-cp`の指定は無視される。クラスパスはJARのマニフェストの`Class-Path`だけで決まる

### 対処法

- マニフェストに`Class-Path: lib/gson-2.11.0.jar`のように書き、JARの横の`lib`ディレクトリにライブラリを置いて配る（第23章の「外部ライブラリを使うアプリケーションのJAR」）
- `-jar`を使わず、`java -cp json.jar:gson-2.11.0.jar JsonApp`のように実行する（Windowsでは`;`で区切る）
- ライブラリをまとめて1つのJARにする（Fat JAR。付録D）

## リソースファイルが読めない（getResourceAsStreamがnullを返す）

### エラー例

```java
Properties config = new Properties();
try (InputStream in = ResourceApp.class.getResourceAsStream("/config.properties")) {
    config.load(in);
}
```

### エラーメッセージ

```
Exception in thread "main" java.lang.NullPointerException: inStream parameter is null
	at java.base/java.util.Objects.requireNonNull(Objects.java:246)
	at java.base/java.util.Properties.load(Properties.java:409)
	at ResourceApp.main(ResourceApp.java:7)
```

### 原因

- `getResourceAsStream`は、リソースが見つからないと例外ではなく`null`を返す
- JARを作るときに`config.properties`を入れ忘れている、またはJARの中の場所（パス）が違う
- IDEでは動くのにJARでは動かない場合は、IDEのプロジェクトのフォルダーから読めていただけのことが多い

### 対処法

- `jar tf app.jar`でJARの中身を表示し、`config.properties`がどこに入っているかを確かめる
- `/`で始まるパスはJARの根元から、`/`で始まらないパスはそのクラスのパッケージのディレクトリから探す
- `null`のときに理由のわかる例外を投げる

```java
InputStream in = ResourceApp.class.getResourceAsStream("/config.properties");
if (in == null) {
    throw new IllegalStateException("config.propertiesがJARに含まれていません");
}
```

## 古いJavaで実行できない（UnsupportedClassVersionError）

### エラーメッセージ

Java 25でコンパイルしたクラスを、Java 24で実行した場合です（Dockerのeclipse-temurin:24-jdkで確かめたもの）。

```
Error: LinkageError occurred while loading main class V
	java.lang.UnsupportedClassVersionError: V has been compiled by a more recent version of the Java Runtime (class file version 69.0), this version of the Java Runtime only recognizes class file versions up to 68.0
```

### 原因

- クラスファイルには、どの版のJavaでコンパイルしたかを表す番号（Java 25は69、Java 24は68、Java 21は65）が入っている
- 古い版のJavaは、自分より新しい番号のクラスファイルを読めない
- JARを受け取った人のパソコンに、古いJavaしか入っていないときに起こる

### 対処法

- 受け取る人に、同じかより新しいJavaを入れてもらう
- `javac --release 21`のように、動かしたい環境の版を指定してコンパイルする。ただし、その版にないAPI（Java 25の`IO`クラスなど）は使えない
- Javaの実行環境ごと配る（第23章の`jpackage`）。受け取る人はJavaを入れなくてよい

## jpackageでインストーラーを作れない

### 原因と対処法

- `jpackage`は、作るOSの上でだけ、そのOS向けのパッケージを作れる。macOSの上でWindowsの`.exe`は作れない
- Windowsの`exe`・`msi`にはWiX Toolset、Linuxの`deb`には`dpkg-deb`と`fakeroot`、`rpm`には`rpmbuild`が必要。ないと、ツールが見つからないというエラーで止まる
- まず`--type app-image`で、インストーラーではないアプリケーションのフォルダーを作れるかを確かめる。これが作れれば、残りはOSごとのツールの問題に絞れる
