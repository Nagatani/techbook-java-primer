# 第22章 ドキュメントと外部ライブラリ：よくあるエラーと対処法

書籍『Java入門』第22章「ドキュメントと外部ライブラリ」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

ドキュメント作成と外部ライブラリの使用において、よく遭遇するエラーとその対処法を説明します。

## Javadocコメントの構文エラー

Javadocコメントの構文が正しくありません。

### エラー例

コンパイルできる全文：[chapter22-code/UserService/](chapter22-code/UserService/)

```java
/**
 * ユーザー情報を処理するクラス
 * @param name ユーザー名
 * @return なし
 */
public class UserService {
    // ...
}
```

クラスのドキュメントでは、メソッドの引数を表す`@param name`（型パラメーターを表す`@param <T>`は除く）と、戻り値を表す`@return`は使えません。
なお、`/** */`の中に`//`でコメントを書いても、Javadocの文章として出力されるだけです。

### エラーメッセージ

Java 25の`javadoc`では、警告ではなくエラーになり、ドキュメントは生成されません。
次は英語の環境での表示です。日本語の環境では「エラー: 無効な@paramの使用」のように日本語で表示されます。

```
UserService.java:3: error: invalid use of @param
UserService.java:4: error: invalid use of @return
UserService.java:4: warning: Tag @return cannot be used in class documentation.  It can only be used in the following types of documentation: method.
UserService.java:6: warning: use of default constructor, which does not provide a comment
2 errors
2 warnings
```

最後の警告は、コメントのない既定のコンストラクターについてのもので、この誤りとは別のものです。

### 対処法

```java
/**
 * ユーザー情報を処理するクラス
 * <p>
 * このクラスは、ユーザーの作成、更新、削除などの
 * 基本的なCRUD操作を提供する
 * </p>
 * 
 * @author 開発者名
 * @version 1.0
 * @since 2023-01-01
 */
public class UserService {
    
    /**
     * 新しいユーザーを作成する
     * 
     * @param name ユーザー名（null不可）
     * @param email メールアドレス（null不可）
     * @return 作成されたユーザーオブジェクト
     * @throws IllegalArgumentException 引数がnullの場合
     */
    public User createUser(String name, String email) {
        // ...
    }
}
```

## Javadocの文字化け

日本語のJavadocが文字化けします。

Java 18からは、`javadoc`がソースファイルを読むときの既定の文字コードもUTF-8です。
そのため、文字化けは主に、ソースファイルをShift_JISなどUTF-8以外で保存している場合や、Java 17以前のJDKを使っている場合に起こります。

### エラー例

```bash
javadoc -d docs src/*.java
# ソースファイルの文字コードと読むときの文字コードが違うと、日本語が文字化けする
```

### 対処法

```bash
# ソースの文字コード（-encoding）と、生成するHTMLの文字コード（-docencoding、-charset）を指定する
javadoc -d docs -encoding UTF-8 -charset UTF-8 -docencoding UTF-8 src/*.java
```

Mavenの`maven-javadoc-plugin`は、ソースの文字コードに`project.build.sourceEncoding`の値を使います。
`pom.xml`に次の設定を書いておくと、コンパイルとJavadocの生成の両方でUTF-8が使われます。

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

`mvn javadoc:javadoc -Dfile.encoding=UTF-8`のように`-D`で`file.encoding`を指定しても、Mavenのプロパティが1つ増えるだけで、Javadocの文字コードは変わりません。

## JavadocでのHTMLタグの誤用

JavadocでHTMLタグを間違って使っています。

### エラー例

コンパイルできる全文：[chapter22-code/UserService2/](chapter22-code/UserService2/)

```java
/**
 * ユーザー情報を<strong>処理するクラス
 * <p>主な機能
 * <ul>
 * <li>ユーザー作成
 * <li>ユーザー更新
 * </ul>
 */
public class UserService {
    // ...
}
```

`<strong>`を閉じていないため、その後ろの`<p>`や`<ul>`まで`<strong>`の中にあることになります。
`<strong>`のような文字の装飾（インライン要素）の中には、段落やリスト（ブロック要素）を置けません。
`<li>`や`<p>`の閉じタグはHTMLでは省略できるので、それだけではエラーになりません。

### エラーメッセージ

Java 25の`javadoc`では、次のエラーになり、ドキュメントは生成されません。

```
UserService.java:3: error: block element not allowed within inline element <strong>: p
UserService.java:4: error: tag not allowed here: <ul>
UserService.java:2: error: element not closed: strong
UserService.java:9: warning: use of default constructor, which does not provide a comment
3 errors
1 warning
```

### 対処法

`<strong>`を閉じます。`<li>`と`<p>`も閉じておくと、範囲がはっきりします。

コンパイルできる全文：[chapter22-code/UserService3/](chapter22-code/UserService3/)

```java
/**
 * ユーザー情報を<strong>処理する</strong>クラス
 * <p>主な機能</p>
 * <ul>
 * <li>ユーザー作成</li>
 * <li>ユーザー更新</li>
 * <li>ユーザー削除</li>
 * </ul>
 */
public class UserService {
    // ...
}
```

## ライブラリのバージョン競合

異なるバージョンのライブラリが競合します。
jackson-databind 2.15.0は同じ版のjackson-coreを前提にしていますが、次の例ではjackson-coreを2.12.0と直接指定しています。
Mavenは直接指定した版を優先するため、実行時には2.12.0のjackson-coreが使われます。

### エラー例

```xml
<dependencies>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-core</artifactId>
        <version>2.12.0</version>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.15.0</version>  <!-- 異なるバージョン -->
    </dependency>
</dependencies>
```

### エラーメッセージ

コンパイルは通り、単純な変換も動きます。
しかし、2.15で加わったjackson-coreのクラスを使う処理（`ObjectMapper.convertValue`など）を呼ぶと、実行時に次のエラーになります。

```
java.lang.NoClassDefFoundError: com/fasterxml/jackson/core/StreamReadConstraints
```

どのメソッドで失敗するかは使う機能によって変わり、`NoSuchMethodError`になることもあります。

### 対処法

```xml
<properties>
    <jackson.version>2.23.0</jackson.version>
</properties>

<dependencies>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-core</artifactId>
        <version>${jackson.version}</version>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>${jackson.version}</version>
    </dependency>
</dependencies>
```

## 推移的な依存関係の問題

間接的に依存するライブラリの版が食い違い、意図しない古い版が選ばれます。

### エラー例

次の`pom.xml`は、commons-configuration 1.10とhttpclient 4.5.14に直接依存しています。
commons-configurationはcommons-logging 1.1.1に、httpclientはcommons-logging 1.2に依存しています。

```xml
<dependencies>
    <dependency>
        <groupId>commons-configuration</groupId>
        <artifactId>commons-configuration</artifactId>
        <version>1.10</version>
    </dependency>
    <dependency>
        <groupId>org.apache.httpcomponents</groupId>
        <artifactId>httpclient</artifactId>
        <version>4.5.14</version>
    </dependency>
</dependencies>
```

`mvn dependency:tree`の出力は次のとおりです（Maven 3.10.0、maven-dependency-plugin 3.11.0で実行。行頭の`[INFO]`は省略）。

```
com.example:myapp:jar:1.0.0
+- commons-configuration:commons-configuration:jar:1.10:compile
|  +- commons-lang:commons-lang:jar:2.6:compile
|  \- commons-logging:commons-logging:jar:1.1.1:compile
\- org.apache.httpcomponents:httpclient:jar:4.5.14:compile
   +- org.apache.httpcomponents:httpcore:jar:4.4.16:compile
   \- commons-codec:commons-codec:jar:1.11:compile
```

Mavenは、同じライブラリの版が複数あるとき、依存の木で直接の依存に近いほうを選びます。
近さが同じなら、`pom.xml`で先に書いたほうが選ばれます。
この例ではcommons-logging 1.1.1が選ばれ、httpclientが前提にしている1.2は使われません。
通常の表示では、選ばれなかった1.2は木に現れません。

`-Dverbose`を付けると、選ばれなかった版も`omitted for conflict`として表示されます。

```bash
mvn dependency:tree -Dverbose
```

```
com.example:myapp:jar:1.0.0
+- commons-configuration:commons-configuration:jar:1.10:compile
|  +- commons-lang:commons-lang:jar:2.6:compile
|  \- commons-logging:commons-logging:jar:1.1.1:compile
\- org.apache.httpcomponents:httpclient:jar:4.5.14:compile
   +- org.apache.httpcomponents:httpcore:jar:4.4.16:compile
   +- (commons-logging:commons-logging:jar:1.2:compile - omitted for conflict with 1.1.1)
   \- commons-codec:commons-codec:jar:1.11:compile
```

### 対処法

古い版を持ち込む依存に`<exclusions>`を付けて、その推移的な依存を除きます。

```xml
<dependency>
    <groupId>commons-configuration</groupId>
    <artifactId>commons-configuration</artifactId>
    <version>1.10</version>
    <exclusions>
        <exclusion>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

もう一度`mvn dependency:tree -Dverbose`を実行すると、httpclientが持ち込む1.2が選ばれたことを確かめられます。

```
com.example:myapp:jar:1.0.0
+- commons-configuration:commons-configuration:jar:1.10:compile
|  \- commons-lang:commons-lang:jar:2.6:compile
\- org.apache.httpcomponents:httpclient:jar:4.5.14:compile
   +- org.apache.httpcomponents:httpcore:jar:4.4.16:compile
   +- commons-logging:commons-logging:jar:1.2:compile
   \- commons-codec:commons-codec:jar:1.11:compile
```

使う版を確実に決めたいときは、`<exclusions>`の代わりに、そのライブラリ（ここではcommons-logging 1.2）を`<dependencies>`に直接書く方法もあります。
直接書いた依存は、推移的な依存より優先されます。

## 古いバージョンのライブラリの使用

セキュリティ脆弱性のある古いライブラリを使っています。

### エラー例

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.9.8</version>  <!-- 脆弱性のあるバージョン -->
</dependency>
```

### 対処法

```xml
<!-- 脆弱性チェックプラグインを追加 -->
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>13.0.0</version>
    <configuration>
        <!-- CVSSの値が7以上の脆弱性があればビルドを失敗させる -->
        <failBuildOnCVSS>7</failBuildOnCVSS>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>

<!-- 脆弱性が修正された新しい版を使う（2026年11月時点の最新版） -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.23.0</version>
</dependency>
```

ここで示した版は、書籍の第22章のサンプルコード22-19・22-20に合わせた2026年11月時点のものです。
使うときは、Maven Centralで最新の安定版を確かめます。

## バージョン範囲の誤用

バージョン範囲が適切ではありません。

### エラー例

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>[3.0,)</version>  <!-- 上限なしは危険 -->
</dependency>
```

### 対処法

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.22.0</version>  <!-- 具体的なバージョンを指定 -->
</dependency>
```

## コンパイル時と実行時のライブラリのバージョンの違い

新しいバージョンで加わったメソッドを使ってコンパイルしたコードを、古いバージョンのライブラリで実行しています。
コンパイルは通りますが、実行時にメソッドが見つからずエラーになります。
推移的な依存関係で古いバージョンが選ばれたときや、実行時のクラスパスに古いJARが残っているときに起こります。

### エラー例

```java
// jackson-databind 2.15でコンパイルしたコード
// JsonNode.properties()は2.15で加わったメソッド
ObjectMapper mapper = new ObjectMapper();
JsonNode node = mapper.readTree("{\"name\":\"Alice\",\"age\":20}");
for (Map.Entry<String, JsonNode> entry : node.properties()) {
    IO.println(entry.getKey() + " = " + entry.getValue());
}
```

### エラーメッセージ

実行時に2.14のjackson-databindが使われると、次のエラーになります。

```
Exception in thread "main" java.lang.NoSuchMethodError: 'java.util.Set com.fasterxml.jackson.databind.JsonNode.properties()'
```

### 対処法

`mvn dependency:tree`で実際に使われるバージョンを確かめ、コンパイル時と同じバージョンにそろえます。
Jacksonのように複数のモジュールからなるライブラリは、BOMを読み込むとモジュールのバージョンをまとめて固定できます。

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.fasterxml.jackson</groupId>
            <artifactId>jackson-bom</artifactId>
            <version>2.23.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <!-- バージョンはBOMで決まる -->
    </dependency>
</dependencies>
```

古いバージョンでも動かす必要がある場合は、古いバージョンにもあるメソッドで書きます（`properties()`の代わりに`fields()`を使うなど）。
ただし、`fields()`は2.23では非推奨になっているので、古い版に合わせるのは移行するまでの一時的な対処にとどめます。

## 廃止予定APIの使用

廃止予定のAPIを使っています。

### エラー例

```java
@SuppressWarnings("deprecation")
Date date = new Date(2023, 1, 1);  // 非推奨のコンストラクター
IO.println(date);
```

実行結果（タイムゾーンが日本の場合）：
```
Thu Feb 01 00:00:00 JST 3923
```

`Date(int, int, int)`は非推奨（deprecated）です。
年には1900を足した値、月には0から数えた値を渡すため、この例は2023年1月1日ではなく3923年2月1日になります。
`@SuppressWarnings("deprecation")`で警告を消すと、このような誤りにも気付けなくなります。

### 対処法

```java
// 新しいAPIを使用
LocalDate date = LocalDate.of(2023, 1, 1);  // 月は1から数える
IO.println(date);

// 古いAPIにDateを渡す必要があるときだけ変換する
Date legacyDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
```

実行結果：
```
2023-01-01
```

## クラスが見つからない

必要なクラスがクラスパスに存在しません。

### エラー例

```java
import org.apache.commons.lang3.StringUtils;

public class Example {
    public static void main(String[] args) {
        StringUtils.isEmpty("test");  // NoClassDefFoundError
    }
}
```

コンパイルするときはクラスパスにcommons-lang3のJARがあったのに、実行するときにはない、という場合に起こります。
JARがなければ、そもそもコンパイルの時点で`package org.apache.commons.lang3 does not exist`というエラーになります。

### エラーメッセージ

Java 25では次のように表示されます。

```
Exception in thread "main" java.lang.NoClassDefFoundError: org/apache/commons/lang3/StringUtils
	at Example.main(Example.java:5)
Caused by: java.lang.ClassNotFoundException: org.apache.commons.lang3.StringUtils
	at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:580)
	at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:490)
	... 1 more
```

`NoClassDefFoundError`の原因（`Caused by`）に、クラスを探して見つからなかったことを表す`ClassNotFoundException`が示されます。

### 対処法

```xml
<!-- 必要な依存関係を追加 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.22.0</version>
</dependency>
```

`pom.xml`に依存を書くと、Mavenはコンパイル時にもテストの実行時にも同じJARをクラスパスに入れます。
JARを手で`javac`と`java`に指定している場合は、次の節のように実行時にも同じクラスパスを指定します。

## 実行時とコンパイル時のクラスパスの違い

コンパイルは成功しますが、実行時にエラーが発生します。

### エラー例

```bash
# コンパイル時
javac -cp "lib/*" src/Example.java

# 実行時にクラスパスを指定し忘れ
java Example  # ClassNotFoundException
```

### 対処法

```bash
# 実行時にも必要なクラスパスを指定（Windowsでは区切りを;にする）
java -cp "lib/*:src" Example
```

Mavenのプロジェクトでは、`mvn clean package`で作られる通常のJARに、依存ライブラリも、実行するクラス（`Main-Class`）の情報も入りません。
そのため、`java -jar target/myapp.jar`とするだけでは実行できません。
依存ライブラリを含めた実行可能JARを作るには、maven-shade-pluginなどの設定が必要です。
設定の例は、付録G「高度なビルドとデプロイメント技術」で扱います。

## 設定ファイルの読み込みエラー

設定ファイルが正しく読み込まれません。

### エラー例

```java
// application.propertiesが見つからない
Properties props = new Properties();
props.load(new FileInputStream("application.properties"));
```

### エラーメッセージ

```
java.io.FileNotFoundException: application.properties (No such file or directory)
```

### 対処法

```java
// クラスパスから読み込み
Properties props = new Properties();
try (InputStream input = getClass().getClassLoader()
        .getResourceAsStream("application.properties")) {
    if (input == null) {
        throw new RuntimeException("設定ファイルが見つかりません");
    }
    props.load(input);
}
```

## 設定値の型変換エラー

設定値の型が期待と異なります。

### エラー例

```properties
# application.properties
# 数値でない値が書かれている
server.port=8080abc
```

```java
int port = Integer.parseInt(props.getProperty("server.port"));
```

### エラーメッセージ

```
java.lang.NumberFormatException: For input string: "8080abc"
```

### 対処法

```java
public static int getIntProperty(Properties props, String key, int defaultValue) {
    String value = props.getProperty(key);
    if (value == null) {
        return defaultValue;
    }
    
    try {
        return Integer.parseInt(value.trim());
    } catch (NumberFormatException e) {
        System.err.println("設定値が不正です: " + key + "=" + value);
        return defaultValue;
    }
}

// 使用例
int port = getIntProperty(props, "server.port", 8080);
```

## ログフレームワークの競合

SLF4Jは、ログを書くためのAPI（slf4j-api）と、実際にログを出力する実装（プロバイダー）を分けたライブラリです。
クラスパスにプロバイダーが2つ以上あると、どちらが使われるかを選べず、意図しない設定でログが出力されます。

### エラー例

logback-classicを使うつもりで依存に加えたのに、別のライブラリがslf4j-reload4j（reload4jへ出力するプロバイダー）を推移的に持ち込んでいる場合です。
プログラムを実行すると、最初に`LoggerFactory.getLogger`でロガーを取得したときに次の警告が表示されます（SLF4J 2.0.20、logback 1.5.38で実行）。

```
SLF4J(W): Class path contains multiple SLF4J providers.
SLF4J(W): Found provider [ch.qos.logback.classic.spi.LogbackServiceProvider@341b80b2]
SLF4J(W): Found provider [org.slf4j.reload4j.Reload4jServiceProvider@55a1c291]
SLF4J(W): See https://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J(I): Actual provider is of type [ch.qos.logback.classic.spi.LogbackServiceProvider@341b80b2]
```

`@`の後ろの値は実行のたびに変わります。
SLF4J 1.7系では、同じ状況で`SLF4J: Class path contains multiple SLF4J bindings.`と表示されました。

### 対処法

`mvn dependency:tree`で、slf4j-reload4jを持ち込んでいる依存を探し、その依存に`<exclusions>`を付けて除きます。
次の`com.example:legacy-lib`は、slf4j-reload4jを持ち込んでいるライブラリの例です。

```xml
<!-- 不要なプロバイダーを除外 -->
<dependency>
    <groupId>com.example</groupId>
    <artifactId>legacy-lib</artifactId>
    <version>1.0.0</version>
    <exclusions>
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-reload4j</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<!-- 使用するログ実装を明示的に指定 -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.5.38</version>
</dependency>
```

プロバイダーがlogback-classicだけになると、警告は表示されなくなります。
古いライブラリが持ち込むのがSLF4J 1.7系のslf4j-log4j12の場合も、同じように`<exclusion>`で除きます。

## 異なるJSONライブラリの混在

JacksonとGsonが混在してAPIが混乱します。

### エラー例

```java
// ある箇所ではJackson
ObjectMapper mapper = new ObjectMapper();
String json1 = mapper.writeValueAsString(user);

// 別の箇所ではGson
Gson gson = new Gson();
String json2 = gson.toJson(user);
```

### 症状

- 2つのライブラリで、`null`のフィールドの扱いや日付の書き方などの既定が異なるため、同じオブジェクトでも出力されるJSONが変わる
- アノテーション（Jacksonの`@JsonProperty`とGsonの`@SerializedName`）が片方にしか効かず、項目名がそろわない
- 依存するJARが増え、版の管理の手間も増える

### 対処法

```java
// 使用するJSONライブラリを統一
public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();
    
    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON変換エラー", e);
        }
    }
    
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON解析エラー", e);
        }
    }
}
```

これらの問題を理解し、適切に対処することで、外部ライブラリを効果的に活用し、保守性の高いアプリケーションを開発できます。
