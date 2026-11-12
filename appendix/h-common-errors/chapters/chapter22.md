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
 * @param name ユーザー名  // クラスレベルでは@paramは使用不可
 * @return なし          // クラスレベルでは@returnは使用不可
 */
public class UserService {
    // ...
}
```

### エラーメッセージ

```
warning: @param has no meaning in class documentation
warning: @return has no meaning in class documentation
```

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

### エラー例

```bash
javadoc -d docs src/*.java
# 日本語が文字化けして表示される
```

### 対処法

```bash
# 文字エンコーディングを明示的に指定
javadoc -d docs -encoding UTF-8 -charset UTF-8 -docencoding UTF-8 src/*.java

# Mavenを使用する場合
mvn javadoc:javadoc -Dfile.encoding=UTF-8
```

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
 * <li>ユーザー更新  // 閉じタグなし
 * </ul>
 */
public class UserService {
    // ...
}
```

### エラーメッセージ

```
warning: unclosed tag: <strong>
warning: unclosed tag: <li>
```

### 対処法

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
    <jackson.version>2.15.0</jackson.version>
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

間接的に依存するライブラリが競合します。

### エラー例

```bash
mvn dependency:tree
# 出力例
[INFO] com.example:myapp:jar:1.0.0
[INFO] +- org.springframework:spring-core:jar:5.3.0:compile
[INFO] |  \- commons-logging:commons-logging:jar:1.2:compile
[INFO] \- org.slf4j:slf4j-api:jar:1.7.30:compile
[INFO]    \- commons-logging:commons-logging:jar:1.1.3:compile
```

### 対処法

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>5.3.0</version>
    <exclusions>
        <exclusion>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<!-- 明示的に使用するバージョンを指定 -->
<dependency>
    <groupId>commons-logging</groupId>
    <artifactId>commons-logging</artifactId>
    <version>1.2</version>
</dependency>
```

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
    <version>8.3.1</version>
    <configuration>
        <failBuildOnCVSS>7</failBuildOnCVSS>
    </configuration>
</plugin>

<!-- 最新の安全なバージョンを使用 -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.2</version>
</dependency>
```

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
    <version>3.12.0</version>  <!-- 具体的なバージョンを指定 -->
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
            <version>2.15.2</version>
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

## 廃止予定APIの使用

廃止予定のAPIを使っています。

### エラー例

```java
@SuppressWarnings("deprecation")
Date date = new Date(2023, 1, 1);  // 廃止予定のコンストラクタ
```

### 対処法

```java
// 新しいAPIを使用
LocalDate date = LocalDate.of(2023, 1, 1);
Date legacyDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
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

実行結果：
```
=== NoClassDefFoundError デモンストレーション ===

実際にはApache Commons Langライブラリが必要ですが、
クラスパスに存在しないため、以下のようなエラーが発生します：

エラーの再現:
Exception in thread "main" java.lang.NoClassDefFoundError: org/apache/commons/lang3/StringUtils
    at Example.main(Example.java:4)
Caused by: java.lang.ClassNotFoundException: org.apache.commons.lang3.StringUtils
    at java.net.URLClassLoader.findClass(URLClassLoader.java:382)
    at java.lang.ClassLoader.loadClass(ClassLoader.java:418)
    at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:352)
    at java.lang.ClassLoader.loadClass(ClassLoader.java:351)
    ... 1 more

解決方法:
1. pom.xmlに依存関係を追加
2. Mavenでライブラリをダウンロード（mvn compile）
3. クラスパスが正しく設定されていることを確認

依存関係管理の重要性:
- 手動でJARファイルを管理すると、このようなエラーが発生しやすい
- MavenやGradleを使用することで、依存関係が自動解決される
- 推移的依存関係も自動的に含まれる
```

### エラーメッセージ

```
java.lang.NoClassDefFoundError: org/apache/commons/lang3/StringUtils
```

### 対処法

```xml
<!-- 必要な依存関係を追加 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

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
# 実行時にも必要なクラスパスを指定
java -cp "lib/*:src" Example

# または実行可能JARを作成
mvn clean package
java -jar target/myapp.jar
```

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
server.port=8080abc  # 数値でない
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

複数のログフレームワークが競合します。

### エラー例

```
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:.../slf4j-log4j12-1.7.30.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:.../logback-classic-1.2.3.jar!/org/slf4j/impl/StaticLoggerBinder.class]
```

### 対処法

```xml
<!-- 不要なログ実装を除外 -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>5.3.0</version>
    <exclusions>
        <exclusion>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<!-- 使用するログ実装を明示的に指定 -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.12</version>
</dependency>
```

## 異なるJSONライブラリの混在

JacksonとGsonが混在してAPIが混乱します。

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
