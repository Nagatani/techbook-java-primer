# 第13章 ライブラリの活用

この章では、外部ライブラリの使用方法を学習します。C言語では手動でライブラリをリンクしていましたが、Javaではより簡単にライブラリを活用できます。

## 13.1 ライブラリとは

### C言語でのライブラリ使用

```c
// C言語では手動でヘッダファイルをincludeし、リンクする
#include <math.h>
#include <sqlite3.h>

// コンパイル時にライブラリをリンク
// gcc program.c -lm -lsqlite3
```

### Javaでのライブラリ使用

Javaでは、以下の方法でライブラリを管理します：
- **Maven**: XMLベースの依存関係管理
- **Gradle**: Groovy/Kotlinベースのビルドツール
- **手動**: JARファイルを直接追加

## 13.2 Maven入門

### プロジェクト構造

```
my-java-project/
├── pom.xml
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── example/
    │               └── App.java
    └── test/
        └── java/
            └── com/
                └── example/
                    └── AppTest.java
```

### pom.xml の基本構成

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.example</groupId>
    <artifactId>my-java-project</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <!-- JSON処理ライブラリ -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.15.2</version>
        </dependency>
        
        <!-- HTTP クライアント -->
        <dependency>
            <groupId>org.apache.httpcomponents.client5</groupId>
            <artifactId>httpclient5</artifactId>
            <version>5.2.1</version>
        </dependency>
        
        <!-- テストフレームワーク -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.9.3</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### 基本的なMavenコマンド

```bash
# プロジェクトのコンパイル
mvn compile

# テストの実行
mvn test

# パッケージの作成
mvn package

# 依存関係のダウンロード
mvn dependency:resolve

# クリーンビルド
mvn clean package

# プロジェクトの実行
mvn exec:java -Dexec.mainClass="com.example.App"
```

## 13.3 人気ライブラリの活用

### Jackson - JSON処理

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.*;

class Person {
    private String name;
    private int age;
    private List<String> hobbies;
    
    // コンストラクタ
    public Person() {}
    
    public Person(String name, int age, List<String> hobbies) {
        this.name = name;
        this.age = age;
        this.hobbies = hobbies;
    }
    
    // getter/setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public List<String> getHobbies() { return hobbies; }
    public void setHobbies(List<String> hobbies) { this.hobbies = hobbies; }
    
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + ", hobbies=" + hobbies + "}";
    }
}

public class JacksonExample {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        
        // Javaオブジェクト → JSON
        Person person = new Person("田中太郎", 25, Arrays.asList("読書", "映画鑑賞"));
        String json = mapper.writeValueAsString(person);
        System.out.println("JSON: " + json);
        
        // JSON → Javaオブジェクト
        String jsonInput = """
            {
                "name": "佐藤花子",
                "age": 30,
                "hobbies": ["料理", "旅行", "音楽"]
            }
            """;
        Person parsedPerson = mapper.readValue(jsonInput, Person.class);
        System.out.println("パースした人: " + parsedPerson);
        
        // JSON → JsonNode（動的解析）
        JsonNode node = mapper.readTree(jsonInput);
        String name = node.get("name").asText();
        int age = node.get("age").asInt();
        System.out.println("名前: " + name + ", 年齢: " + age);
        
        // 配列の処理
        String jsonArray = """
            [
                {"name": "太郎", "age": 20},
                {"name": "花子", "age": 25},
                {"name": "次郎", "age": 30}
            ]
            """;
        
        List<Person> people = mapper.readValue(jsonArray, 
            mapper.getTypeFactory().constructCollectionType(List.class, Person.class));
        
        people.forEach(System.out::println);
    }
}
```

### Apache Commons Lang - ユーティリティ

```java
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import java.util.Date;

public class CommonsLangExample {
    public static void main(String[] args) {
        // 文字列ユーティリティ
        String text = "  Hello World  ";
        
        System.out.println("空文字チェック: " + StringUtils.isEmpty(""));
        System.out.println("空白文字チェック: " + StringUtils.isBlank("   "));
        System.out.println("左パディング: " + StringUtils.leftPad("123", 5, "0"));
        System.out.println("キャピタライズ: " + StringUtils.capitalize("hello world"));
        System.out.println("逆順: " + StringUtils.reverse("hello"));
        
        // 配列を文字列に結合
        String[] array = {"apple", "banana", "cherry"};
        String joined = StringUtils.join(array, ", ");
        System.out.println("結合: " + joined);
        
        // ランダム文字列生成
        String randomString = RandomStringUtils.randomAlphabetic(10);
        String randomNumber = RandomStringUtils.randomNumeric(6);
        System.out.println("ランダム文字列: " + randomString);
        System.out.println("ランダム数字: " + randomNumber);
        
        // 日付ユーティリティ
        Date now = new Date();
        Date tomorrow = DateUtils.addDays(now, 1);
        Date nextWeek = DateUtils.addWeeks(now, 1);
        
        System.out.println("今日: " + now);
        System.out.println("明日: " + tomorrow);
        System.out.println("来週: " + nextWeek);
    }
}
```

### Guava - Googleのユーティリティライブラリ

```java
import com.google.common.collect.*;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import java.util.*;

public class GuavaExample {
    public static void main(String[] args) {
        // 便利なコレクション作成
        List<String> list = Lists.newArrayList("a", "b", "c");
        Set<String> set = Sets.newHashSet("x", "y", "z");
        Map<String, Integer> map = Maps.newHashMap();
        map.put("apple", 100);
        map.put("banana", 80);
        
        // ImmutableList（不変リスト）
        List<String> immutableList = ImmutableList.of("red", "green", "blue");
        // immutableList.add("yellow"); // エラー！
        
        // Multiset（要素の出現回数を管理）
        Multiset<String> multiset = HashMultiset.create();
        multiset.add("apple");
        multiset.add("banana");
        multiset.add("apple");
        multiset.add("apple");
        
        System.out.println("appleの数: " + multiset.count("apple"));  // 3
        
        // Multimap（1つのキーに複数の値）
        Multimap<String, String> multimap = ArrayListMultimap.create();
        multimap.put("fruits", "apple");
        multimap.put("fruits", "banana");
        multimap.put("vegetables", "carrot");
        multimap.put("vegetables", "spinach");
        
        System.out.println("果物: " + multimap.get("fruits"));
        
        // 文字列操作
        List<String> words = Arrays.asList("Java", "Python", "C++");
        String joined = Joiner.on(", ").join(words);
        System.out.println("結合: " + joined);
        
        String text = "apple,banana,cherry";
        Iterable<String> split = Splitter.on(',').split(text);
        System.out.println("分割: " + split);
        
        // フィルタリング
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Iterable<Integer> evenNumbers = Iterables.filter(numbers, n -> n % 2 == 0);
        System.out.println("偶数: " + evenNumbers);
        
        // 変換
        Iterable<String> stringNumbers = Iterables.transform(numbers, n -> "Number: " + n);
        System.out.println("変換: " + stringNumbers);
    }
}
```

## 13.4 ログライブラリ

### SLF4J + Logback

```xml
<!-- pom.xmlに追加 -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.7</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.8</version>
</dependency>
```

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingExample {
    private static final Logger logger = LoggerFactory.getLogger(LoggingExample.class);
    
    public static void main(String[] args) {
        logger.trace("トレースメッセージ");
        logger.debug("デバッグメッセージ");
        logger.info("情報メッセージ");
        logger.warn("警告メッセージ");
        logger.error("エラーメッセージ");
        
        // パラメータ付きログ
        String user = "田中太郎";
        int attempts = 3;
        logger.info("ユーザー {} が {} 回ログインを試行しました", user, attempts);
        
        // 例外のログ
        try {
            int result = 10 / 0;
        } catch (Exception e) {
            logger.error("計算エラーが発生しました", e);
        }
    }
}
```

### logback.xml設定ファイル

```xml
<!-- src/main/resources/logback.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- コンソール出力 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- ファイル出力 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- ルートロガー -->
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>
    
    <!-- 特定パッケージのログレベル -->
    <logger name="com.example" level="DEBUG" />
</configuration>
```

## 13.5 データベースアクセス

### H2データベース + JDBC

```xml
<!-- pom.xmlに追加 -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.2.220</version>
</dependency>
```

```java
import java.sql.*;
import java.util.*;

class User {
    private int id;
    private String name;
    private String email;
    
    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    // getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    
    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}

public class DatabaseExample {
    private static final String DB_URL = "jdbc:h2:mem:testdb";
    private static final String USER = "sa";
    private static final String PASS = "";
    
    public static void main(String[] args) {
        try {
            // データベース接続
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            // テーブル作成
            createTable(conn);
            
            // データ挿入
            insertUsers(conn);
            
            // データ取得
            List<User> users = selectAllUsers(conn);
            users.forEach(System.out::println);
            
            // データ更新
            updateUser(conn, 1, "田中太郎（更新）");
            
            // 特定ユーザー検索
            User user = selectUserById(conn, 1);
            System.out.println("更新後: " + user);
            
            conn.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void createTable(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE users (
                id INT PRIMARY KEY,
                name VARCHAR(100),
                email VARCHAR(100)
            )
            """;
        
        Statement stmt = conn.createStatement();
        stmt.execute(sql);
        stmt.close();
    }
    
    private static void insertUsers(Connection conn) throws SQLException {
        String sql = "INSERT INTO users (id, name, email) VALUES (?, ?, ?)";
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        // 複数ユーザーの挿入
        String[][] userData = {
            {"1", "田中太郎", "tanaka@example.com"},
            {"2", "佐藤花子", "sato@example.com"},
            {"3", "鈴木次郎", "suzuki@example.com"}
        };
        
        for (String[] user : userData) {
            pstmt.setInt(1, Integer.parseInt(user[0]));
            pstmt.setString(2, user[1]);
            pstmt.setString(3, user[2]);
            pstmt.executeUpdate();
        }
        
        pstmt.close();
    }
    
    private static List<User> selectAllUsers(Connection conn) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email FROM users";
        
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        
        while (rs.next()) {
            users.add(new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email")
            ));
        }
        
        rs.close();
        stmt.close();
        return users;
    }
    
    private static User selectUserById(Connection conn, int id) throws SQLException {
        String sql = "SELECT id, name, email FROM users WHERE id = ?";
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        
        User user = null;
        if (rs.next()) {
            user = new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email")
            );
        }
        
        rs.close();
        pstmt.close();
        return user;
    }
    
    private static void updateUser(Connection conn, int id, String newName) throws SQLException {
        String sql = "UPDATE users SET name = ? WHERE id = ?";
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, newName);
        pstmt.setInt(2, id);
        pstmt.executeUpdate();
        
        pstmt.close();
    }
}
```

## 13.6 HTTP通信

### OkHttp ライブラリ

```xml
<!-- pom.xmlに追加 -->
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.11.0</version>
</dependency>
```

```java
import okhttp3.*;
import java.io.IOException;

public class HttpClientExample {
    private static final OkHttpClient client = new OkHttpClient();
    
    public static void main(String[] args) throws IOException {
        // GET リクエスト
        String getResult = performGetRequest("https://httpbin.org/get");
        System.out.println("GET結果: " + getResult);
        
        // POST リクエスト
        String postResult = performPostRequest("https://httpbin.org/post", 
            "{\"name\":\"田中太郎\",\"age\":25}");
        System.out.println("POST結果: " + postResult);
        
        // ヘッダー付きリクエスト
        String headerResult = performRequestWithHeaders("https://httpbin.org/headers");
        System.out.println("ヘッダー付き結果: " + headerResult);
    }
    
    private static String performGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
            .url(url)
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
    
    private static String performPostRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
    
    private static String performRequestWithHeaders(String url) throws IOException {
        Request request = new Request.Builder()
            .url(url)
            .addHeader("User-Agent", "Java-App/1.0")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer token123")
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
```

## 13.7 実践的なプロジェクト例

### 天気情報取得アプリ

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

class WeatherInfo {
    private String city;
    private double temperature;
    private String description;
    private int humidity;
    
    public WeatherInfo(String city, double temperature, String description, int humidity) {
        this.city = city;
        this.temperature = temperature;
        this.description = description;
        this.humidity = humidity;
    }
    
    @Override
    public String toString() {
        return String.format("%s: %.1f℃, %s, 湿度%d%%", 
                           city, temperature, description, humidity);
    }
}

public class WeatherApp {
    private static final Logger logger = LoggerFactory.getLogger(WeatherApp.class);
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();
    
    // 実際のAPIキーが必要
    private static final String API_KEY = "your-api-key";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    
    public static void main(String[] args) {
        WeatherApp app = new WeatherApp();
        
        try {
            WeatherInfo tokyo = app.getWeatherInfo("Tokyo");
            WeatherInfo osaka = app.getWeatherInfo("Osaka");
            
            System.out.println(tokyo);
            System.out.println(osaka);
            
        } catch (Exception e) {
            logger.error("天気情報の取得に失敗しました", e);
        }
    }
    
    public WeatherInfo getWeatherInfo(String city) throws IOException {
        String url = String.format("%s?q=%s&appid=%s&units=metric&lang=ja", 
                                 BASE_URL, city, API_KEY);
        
        Request request = new Request.Builder()
            .url(url)
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("APIエラー: " + response.code());
            }
            
            String jsonResponse = response.body().string();
            return parseWeatherInfo(city, jsonResponse);
        }
    }
    
    private WeatherInfo parseWeatherInfo(String city, String json) throws IOException {
        JsonNode root = mapper.readTree(json);
        
        double temperature = root.path("main").path("temp").asDouble();
        String description = root.path("weather").get(0).path("description").asText();
        int humidity = root.path("main").path("humidity").asInt();
        
        return new WeatherInfo(city, temperature, description, humidity);
    }
}
```

## 13.8 練習問題

1. JSONファイルを読み込んで、Javaオブジェクトに変換し、フィルタリング・ソート機能を実装してください。

2. 簡単なREST APIクライアントを作成し、外部サービスからデータを取得してください。

3. データベースを使用した簡単なCRUDアプリケーションを作成してください。

## まとめ

この章では、外部ライブラリの活用方法を学習しました。Maven による依存関係管理、人気ライブラリの使用方法、データベースアクセス、HTTP通信など、実用的なJavaアプリケーション開発に必要な知識を習得することができました。ライブラリを適切に活用することで、開発効率を大幅に向上させることができます。