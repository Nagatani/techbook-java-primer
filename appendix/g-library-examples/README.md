# 付録G: 外部ライブラリの使用例<small>OkHttp・Lombok・Guava</small>

書籍の第22章「ドキュメントと外部ライブラリ」から移した、よく使われる外部ライブラリの使用例です。
依存関係の書き方とライブラリの選び方は第22章で、ライブラリを選ぶ判断の進め方は第26章で学びます。
ここでは、本文で表だけで紹介した3つのライブラリについて、依存関係の書き方とコードの例を示します。

各節のJavaのコードは抜粋です。コンパイルして実行できる全文は`code/`の下にあります。
実行には、各節の依存関係に書いたライブラリのJARをクラスパスに加えます（Mavenを使えば自動で入ります）。

## OkHttp - モダンなHTTPクライアント

OkHttpはSquare社が開発したHTTP通信ライブラリで、接続プーリング、レスポンスキャッシュ、HTTP/2サポート、リトライ機能を備えています（Java 11からは、第16章「ネットワークプログラミング」で学んだ標準APIの`HttpClient`もHTTP/2に対応しています）。


```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp-jvm</artifactId>
    <version>5.5.1</version>
</dependency>
```

OkHttpの5.x系では、Mavenから使うときの成果物の名前が`okhttp-jvm`です（5.x系の`okhttp`は、Gradleが読む付加情報で`okhttp-jvm`を指す中身のないJARです。Mavenはその情報を読まないので、`okhttp`と書くとクラスが見つからずコンパイルできません。Gradleでは`com.squareup.okhttp3:okhttp:5.5.1`と書けます）。

以下の例では、同期のGET・POSTリクエストと、非同期リクエストの実装方法を示します。

コードの抜粋（コンパイルできる全文：[code/OkHttpExample/](code/OkHttpExample/)のOkHttpExample.java）

```java
public class OkHttpExample {
    public static void main(String[] args) throws IOException {
        // OkHttpClientの設定
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
        
        // 1. GETリクエスト
        Request getRequest = new Request.Builder()
            .url("https://api.github.com/users/github")
            .header("User-Agent", "OkHttp Example")
            .build();
        
        try (Response response = client.newCall(getRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            
            IO.println("GET Response:");
            IO.println("Status: " + response.code());
            IO.println("Body: " + response.body().string());
        }
        // ...
        // 3. 非同期リクエスト
        Request asyncRequest = new Request.Builder()
            .url("https://httpbin.org/delay/2")
            .build();
        
        client.newCall(asyncRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }
                    IO.println("\nAsync Response received!");
                    IO.println("Thread: " + Thread.currentThread().getName());
                }
            }
        });
        // ...
    }
}
```

次は実行結果の一例です。インターネットに接続できる環境で実行してください。レスポンスの本文は長いため一部を省略しています。

```
GET Response:
Status: 200
Body: {"login":"github","id":9919,"type":"Organization", ...（以下省略）

POST Response:
Status: 200
Body: {
  "args": {},
  ...（以下省略）

Async Response received!
Thread: OkHttp https://httpbin.org/...
```

## Lombok - ボイラープレートコードの削減

Lombok（Lombokという名前は、ジャワ島（Java）の近くにあるインドネシアのロンボク島に由来します）は、アノテーションを使ってゲッター、セッター、コンストラクターなどを自動生成するライブラリです。
Lombokは、コンパイル時にアノテーションを処理するプログラム（アノテーションプロセッサー）として働きます。
JDK 23からは、使うアノテーションプロセッサーを指定しない限り、javacはアノテーションを処理しません。
そのため、依存関係に加えるだけでなく、`maven-compiler-plugin`の`<annotationProcessorPaths>`にもLombokを指定します。


```xml
<dependencies>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.48</version>
        <scope>provided</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.14.1</version>
            <configuration>
                <!-- JDK 23以降は、アノテーションプロセッサーの指定が必要 -->
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>1.18.48</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

`provided`スコープは、コンパイル時だけ使い、実行時のクラスパスやJARには含めないライブラリに指定します。
`javac`コマンドで直接コンパイルするときは、`-proc:full`オプションを付けると、クラスパスにあるLombokが働きます（JDK 25でLombokを使うと、コンパイル時に`sun.misc.Unsafe`の非推奨のメソッドを呼び出したという警告（WARNING）が表示されます。Lombokの内部の処理によるもので、コンパイルの結果には影響しません）。
`@Data`アノテーション1つでゲッター、セッター、equals、hashCode、toStringメソッドが自動生成されます（値を変えないデータのクラスなら、Java 16で正式に加わったRecord（第8章「Javaの特殊なクラス形式」で学びました）でも、equals・hashCode・toStringを自動で用意できます）。
以下の例では、主要なアノテーションの使用方法を示します。

コードの抜粋（コンパイルできる全文：[code/LombokExample/](code/LombokExample/)のLombokExample.java、User.java）

```java
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

// @Data = @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @NonNull
    private String username;
    
    private String email;
    
    private int age;
    
    @ToString.Exclude  // toStringから除外
    private String password;
}

public class LombokExample {
    public static void main(String[] args) {
        // 1. Builderパターンでインスタンス作成
        User user = User.builder()
            .username("tanaka")
            .email("tanaka@example.com")
            .age(25)
            .password("secret123")
            .build();
        
        // 2. 自動生成されたメソッドの使用
        IO.println("User: " + user);  // toStringが自動生成
        IO.println("Username: " + user.getUsername());  // getterが自動生成
        
        user.setAge(26);  // setterが自動生成
        IO.println("Updated age: " + user.getAge());
        
        // 3. equalsの動作確認（passwordもequalsの比較に含まれる）
        User otherPassword = User.builder()
            .username("tanaka")
            .email("tanaka@example.com")
            .age(26)
            .password("different")
            .build();
        IO.println("Equals (different password): " + user.equals(otherPassword));
        
        otherPassword.setPassword("secret123");
        IO.println("Equals (same password): " + user.equals(otherPassword));
        
        // 4. @NonNullの動作確認
        try {
            new User(null, "email@example.com", 20, "pass");
        } catch (NullPointerException e) {
            IO.println("NullPointerException: " + e.getMessage());
        }
    }
}
```

実行結果：

```
User: User(username=tanaka, email=tanaka@example.com, age=25)
Username: tanaka
Updated age: 26
Equals (different password): false
Equals (same password): true
NullPointerException: username is marked non-null but is null
```

`@ToString.Exclude`を付けた`password`は`toString`の結果に出ませんが、`equals`の比較には含まれます。
`@NonNull`を付けたフィールドには、生成されたコンストラクターやセッターが`null`をチェックする処理が加わります。

`@Data`を使わず、必要なメソッドだけをアノテーションで個別に生成することもできます。
次の例では、`@Getter`と`@Setter`をフィールドごとに付け、`@ToString.Include`で`toString`に表示する名前を変えています。

コードの抜粋（コンパイルできる全文：[code/ProductExample/](code/ProductExample/)のProduct.java）

```java
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 個別のアノテーション使用例
@ToString
class Product {
    @Getter @Setter
    private String name;
    
    @Getter
    private final double price;  // finalフィールドはsetterなし
    
    @ToString.Include(name = "id")  // toStringでの名前を指定
    private long productId;
    
    public Product(double price) {
        this.price = price;
    }
}
```

実行結果：

```
Product: Product(name=ノートPC, price=15000.0, id=0)
Price: 15000.0
```

実行結果は、全文の`ProductExample`クラスで`name`に「ノートPC」を設定し、`toString`と`getPrice`を呼び出したものです。
`productId`フィールドは、`toString`の結果に`id`という名前で表示されます。

## Guava - Google製のコアライブラリ

GuavaはGoogleが開発した、Javaの標準ライブラリを強化する多機能なライブラリです。


```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>33.8.0-jre</version>
</dependency>
```

不変コレクション、Multimap、BiMap、Table、強力な文字列処理、キャッシュ機能など、実用的な機能が豊富に含まれています。
以下の例では、これらの主要機能の使用方法を示します。
不変のリストやマップは、第9章「コレクションフレームワーク」で学んだ`List.of`や`Map.of`でも作れます。
Guavaの`ImmutableList`などは、標準のメソッドがなかったJava 8以前から使われてきたものです。

コードの抜粋（コンパイルできる全文：[code/GuavaExample/](code/GuavaExample/)のGuavaExample.java）

```java
public class GuavaExample {
    public static void main(String[] args) throws Exception {
        // 1. 不変コレクション
        ImmutableList<String> immutableList = ImmutableList.of("A", "B", "C");
        ImmutableMap<String, Integer> immutableMap = ImmutableMap.of(
            "one", 1,
            "two", 2,
            "three", 3
        );
        
        IO.println("Immutable list: " + immutableList);
        IO.println("Immutable map: " + immutableMap);
        
        // 2. Multimap - 1つのキーに複数の値
        Multimap<String, String> multimap = ArrayListMultimap.create();
        multimap.put("fruits", "apple");
        multimap.put("fruits", "banana");
        multimap.put("fruits", "orange");
        multimap.put("vegetables", "carrot");
        
        IO.println("\nMultimap:");
        IO.println("Fruits: " + multimap.get("fruits"));
        // ...
        // 5. 文字列処理
        String input = "  hello,  world,  java  ";
        List<String> parts = Splitter.on(',')
            .trimResults()
            .omitEmptyStrings()
            .splitToList(input);
        IO.println("\nSplit result: " + parts);
        
        String joined = Joiner.on(" | ")
            .skipNulls()
            .join("A", null, "B", "C");
        IO.println("Joined: " + joined);
        // ...
        // 6. キャッシュ
        LoadingCache<String, String> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) {
                    // 実際のデータ取得処理
                    return "Value for " + key;
                }
            });
            // ...
    }
}
```

実行結果：

```
Immutable list: [A, B, C]
Immutable map: {one=1, two=2, three=3}

Multimap:
Fruits: [apple, banana, orange]

BiMap:
Key for 2: two

Table:
Tokyo 2024: 14100000
All Tokyo data: {2023=14000000, 2024=14100000}

Split result: [hello, world, java]
Joined: A | B | C

Cache:
First call: Value for key1
Second call (cached): Value for key1
```
