# 付録G: 外部ライブラリの使用例<small>Jackson・Apache Commons・OkHttp・Lombok・Guava・HikariCP</small>

書籍の第22章「ドキュメントと外部ライブラリ」と第24章「データベースプログラミング」から移した、よく使われる外部ライブラリの使用例です。
依存関係の書き方とライブラリの選び方は第22章で、ライブラリを選ぶ判断の進め方は第26章で学びます。
ここでは、本文で表や要点だけを紹介した6つのライブラリについて、依存関係の書き方とコードの例を示します。

- OkHttp・Lombok・Guava（第22章の「そのほかのよく使われるライブラリ」）
- Jackson・Apache Commons（第22章）
- HikariCP（第24章）

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

## Jackson - 多機能なJSONライブラリ

Jacksonは、Gsonと並んで人気の高いJSONライブラリで、Spring Frameworkなどの業務アプリケーションで広く使われています。
大きなJSONを少しずつ読むストリーミングAPIと、アノテーションでJSONの名前とフィールドの対応を細かく決められる点が特徴です。
本書の第16章と演習では、依存が1つで設定なしに使えるGsonを使います（Gsonの使い方は第22章の「外部ライブラリの利用例（Gson）」で学びます）。
Jacksonは、Springなどを使う開発で出会うことが多いので、ここではGsonとの書き方の違いを見ておきます。

2025年10月に公開されたJackson 3では、groupIdが`tools.jackson.core`に、パッケージ名が`tools.jackson`に変わりました。
この付録のコードは、いまも更新が続いているJackson 2の系列（`com.fasterxml.jackson.core`）で書いています。
第22章の「ライブラリの組み合わせ例」の表でJackson 3の座標を示しているのは、Spring Boot 4がJackson 3を使うためです。

### Mavenの依存関係

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.23.0</version>
</dependency>
```

`jackson-databind`は、オブジェクトとJSONを変換する部分です。
JSONを読み書きする`jackson-core`と、アノテーションの`jackson-annotations`に依存しているので、Mavenを使えばこの2つも推移的な依存として自動で入ります。
JARを手で集めてクラスパスに加える場合は、3つのJARが要ります。

### Jacksonの使用例

以下の例では、`@JsonProperty`によるフィールド名のマッピング、`@JsonIgnore`による特定フィールドの除外、`TypeReference`を使ったジェネリック型のデシリアライズなど、実践的な機能を紹介します。

コードの抜粋（コンパイルできる全文：[code/JacksonExample/](code/JacksonExample/)のJacksonExample.java）

```java
public class JacksonExample {
    // データモデルクラス
    static class Product {
        @JsonProperty("product_id")
        private int id;
        
        private String name;
        private double price;
        
        @JsonIgnore  // JSONに含めない
        private String internalCode;
        
        private Map<String, String> attributes;
        // ...
    }
    // ...
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        
        // 1. オブジェクトからJSONへ
        Product product = new Product(1, "ノートPC", 98000.0);
        String json = mapper.writeValueAsString(product);
        IO.println("JSON: " + json);
        
        // 2. 整形されたJSON出力
        String prettyJson = mapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(product);
        IO.println("\nPretty JSON:\n" + prettyJson);
        // ...
        Product loaded = mapper.readValue(jsonInput, Product.class);
        IO.println("\nLoaded: " + loaded.getName());
        // ...
        String productsJson = mapper.writeValueAsString(products);
        
        // TypeReferenceを使った複雑な型のデシリアライズ
        List<Product> loadedProducts = mapper.readValue(productsJson, 
            new TypeReference<List<Product>>() {});
        
        IO.println("\nLoaded products count: " + loadedProducts.size());
    }
}
```

実行結果：

```
JSON: {"name":"ノートPC","price":98000.0,"attributes":{},"product_id":1}

Pretty JSON:
{
  "name" : "ノートPC",
  "price" : 98000.0,
  "attributes" : { },
  "product_id" : 1
}

Loaded: マウス

Loaded products count: 2
```

Gsonとの違いを、第22章のGsonの例と対応させて見ます。

- 変換の入り口
    + Gsonは`Gson`（整形するときは`GsonBuilder`で作る）、Jacksonは`ObjectMapper`を使う
    + オブジェクトからJSONへは、Gsonの`toJson`がJacksonの`writeValueAsString`に、JSONからオブジェクトへは、Gsonの`fromJson`がJacksonの`readValue`に当たる
- JSONの名前の指定とフィールドの除外
    + Gsonの`@SerializedName`がJacksonの`@JsonProperty`に当たる
    + Gsonは`transient`を付けたフィールドを除くが、Jacksonは`@JsonIgnore`を付けて除く
- 整形した出力
    + Gsonは作るときに`setPrettyPrinting()`を指定し、Jacksonは書き出すときに`writerWithDefaultPrettyPrinter()`を使う
- `List<Product>`のような型引数を持つ型への変換
    + 型引数は実行時に消えるので、Jacksonでは`TypeReference`の匿名クラスで型を伝える。Gsonでは`TypeToken`を使う
- 例外
    + Jacksonの`writeValueAsString`や`readValue`は検査例外（`JsonProcessingException`など）をスローするので、`throws`で宣言するかtry-catchで受け取る。Gsonの`toJson`や`fromJson`は非検査例外をスローする

実行結果の1行目のように、JSONの項目の順は、クラスのフィールドを書いた順になるとは限りません。
順を決めたいときは、クラスに`@JsonPropertyOrder`を付けて指定します。
`internalCode`は`@JsonIgnore`を付けたので、JSONに含まれていません。

## Apache Commons - ユーティリティライブラリ集

Apache Commonsは、Javaプログラミングでよく使われる機能を提供するライブラリ群です。
Apache Software Foundationのプロジェクトで、文字列の操作のCommons Lang、ファイルとストリームのCommons I/O、CSVの読み書きのCommons CSV（第26章で使います）など、目的ごとに別のライブラリに分かれています。
必要なものだけを依存関係に加えます。

### Commons Langの例

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.22.0</version>
</dependency>
```

Apache Commons LangはJava標準APIを補完する便利なユーティリティメソッドを提供します。
以下の例では、文字列操作、null安全な処理、配列操作、ランダムな文字列の生成、日付の書式化、`toString`の自動生成など、日常的に必要になる機能を示します。

コードの抜粋（コンパイルできる全文：[code/CommonsLangExample/](code/CommonsLangExample/)のCommonsLangExample.java）

```java
public class CommonsLangExample {
    static class Person {
        // ...
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this,
                    ToStringStyle.SHORT_PREFIX_STYLE);
        }
        // ...
    }
    // ...
    public static void main(String[] args) {
        // 1. StringUtils - 文字列操作
        String text = "  Hello World  ";
        IO.println("Original: '" + text + "'");
        IO.println("Trimmed: '" + StringUtils.trim(text) + "'");
        IO.println("Is blank?: " + StringUtils.isBlank("   "));
        IO.println("Reverse: " + StringUtils.reverse("Hello"));
        IO.println("Capitalize: " + StringUtils.capitalize("java"));
        
        // nullや空文字列の安全な処理
        String nullStr = null;
        IO.println("Default: "
                + StringUtils.defaultIfEmpty(nullStr, "デフォルト値"));
        
        // 2. ArrayUtils - 配列操作
        int[] numbers = {1, 2, 3, 4, 5};
        IO.println("\nArray contains 3?: "
                + ArrayUtils.contains(numbers, 3));
        int[] reversed = ArrayUtils.clone(numbers);
        ArrayUtils.reverse(reversed);
        IO.println("Reversed: " + ArrayUtils.toString(reversed));
        // ...
    }
}
```

次は実行結果の一例です。ランダムな文字列と日時は実行するたびに変わります。

```
Original: '  Hello World  '
Trimmed: 'Hello World'
Is blank?: true
Reverse: olleH
Capitalize: Java
Default: デフォルト値

Array contains 3?: true
Reversed: {5,4,3,2,1}

Random alphabetic: KJqMGBribR
Random numeric: 654594
Random alphanumeric: qUPX37y9

Date formats:
ISO: 2026-11-08T19:58:50
Custom: 2026年11月08日 19:58:50

Person: CommonsLangExample.Person[age=30,name=田中太郎]
```

`StringUtils`のメソッドは、引数に`null`を渡しても`NullPointerException`をスローせず、`null`や決まった値を返します。
たとえば`StringUtils.trim(null)`は`null`を返し、`isBlank`は`null`・空文字列・空白だけの文字列のどれにも`true`を返します。
標準APIにも、Java 11からは`String`の`isBlank`や`strip`があります。
ただし`null`に対して呼ぶと`NullPointerException`になるので、`null`が来うる値にはCommons Langのほうが短く書けます。

`ToStringBuilder.reflectionToString`は、リフレクション（実行時にクラスのフィールドを調べるしくみ）でフィールドの名前と値を並べた文字列を作ります。
フィールドを足しても`toString`を書き直さずに済みます。
データを運ぶだけのクラスなら、第8章で学んだRecordを使えば、`toString`が自動で用意されます。

### Commons I/Oの例

```xml
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.22.0</version>
</dependency>
```

Apache Commons I/Oは、ファイルやストリームの操作を簡潔なメソッドで実現するライブラリです。
ファイル全体の読み書きなら、第14章「ファイルI/O」で学んだ`Files`クラスの`readAllLines`や`writeString`でも1行で書けます。
Commons I/Oは、ディレクトリのサイズの計算、ファイル名からの拡張子の取り出し、ストリームのコピーなど、標準APIでは何行も必要な処理も1行で書ける点が便利です。
以下の例では、ファイルの読み書き、コピー、ディレクトリ操作、パス操作など、実務で頻繁に使用される機能を紹介します。

コードの抜粋（コンパイルできる全文：[code/CommonsIOExample/](code/CommonsIOExample/)のCommonsIOExample.java）

```java
public class CommonsIOExample {
    public static void main(String[] args) throws IOException {
        // 1. FileUtils - ファイル操作の簡略化
        File file = new File("example.txt");
        
        // ファイルへの書き込み（1行で完了）
        FileUtils.writeStringToFile(file, "Hello, Commons IO!\n", 
            StandardCharsets.UTF_8);
        
        // ファイルへの追記
        FileUtils.writeStringToFile(file, "追加のテキスト\n", 
            StandardCharsets.UTF_8, true);
        
        // ファイルの読み込み（1行で完了）
        String content = FileUtils.readFileToString(file,
                StandardCharsets.UTF_8);
        IO.println("File content:\n" + content);
        
        // 行単位での読み込み
        List<String> lines =
                FileUtils.readLines(file, StandardCharsets.UTF_8);
        IO.println("Lines count: " + lines.size());
        // ...
        // 4. FilenameUtils - パス操作
        String filePath = "/home/user/documents/report.pdf";
        IO.println("\nFile path analysis:");
        IO.println("Base name: " + FilenameUtils.getBaseName(filePath));
        IO.println("Extension: " + FilenameUtils.getExtension(filePath));
        IO.println("Full path: " + FilenameUtils.getFullPath(filePath));
        // ...
    }
}
```

実行結果：

```
File content:
Hello, Commons IO!
追加のテキスト

Lines count: 2
temp_dir size: 41 bytes

File path analysis:
Base name: report
Extension: pdf
Full path: /home/user/documents/

Stream copy result: Stream processing example
```

全文では、抜粋で省いた部分でファイルのコピー（`copyFile`）、ディレクトリの作成とディレクトリへのコピー（`forceMkdir`・`copyFileToDirectory`）、ストリームのコピー（`IOUtils.copy`）を行い、最後に`deleteQuietly`で作ったファイルとディレクトリを消しています。
`deleteQuietly`は、消せなかったときも例外をスローしません。
`FileUtils`のメソッドは`java.io.File`を受け取ります。
第14章で学んだ`Path`と組み合わせるときは、`path.toFile()`で`File`に変換して渡します。

## HikariCP - コネクションプール

第24章「データベースプログラミング」では、`DriverManager.getConnection`でデータベースに接続し、使い終わったらtry-with-resourcesで接続を閉じました。
データベースへの接続を作る処理には、時間がかかります。
とくにサーバー型のデータベースでは、ネットワークでの通信や認証が伴います。
処理のたびに接続を作り直さず、作っておいた接続を使い回すしくみを、コネクションプールと呼びます。
多くの利用者の要求を同時に受け付けるWebアプリケーションのサーバーでは、コネクションプールを使うのがふつうです。

Javaでよく使われるコネクションプールのライブラリに、HikariCPがあります（Mavenの座標は`com.zaxxer:HikariCP:7.1.0`、2026年11月時点の最新版）。

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>7.1.0</version>
</dependency>
```

HikariCPはログの出力にSLF4J（`org.slf4j:slf4j-api`）を使うので、JARを手で集める場合はそれもクラスパスに加えます（Mavenなら推移的な依存として自動で入ります）。
SLF4Jはログを出す窓口だけを決めるライブラリで、実際にどこへ出力するかは別のJARで選びます（第26章で扱います）。
次の例は第24章と同じH2のメモリ上のデータベースを使うので、H2のJAR（`com.h2database:h2:2.5.252`）も加えます。

以下のコードは、HikariCPのプールを作り、そこから接続を借りて表を作り、3行を追加して数える例です。

コードの抜粋（コンパイルできる全文：[code/PoolExample/](code/PoolExample/)のPoolExample.java）

```java
        HikariConfig config = new HikariConfig();
        // DB_CLOSE_DELAY=-1: 接続がすべてプールに戻っても、メモリ上の
        // データベースを消さない
        config.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(5); // 用意しておく接続の数

        // HikariDataSourceはAutoCloseable。閉じるとプールの接続をすべて切る
        try (HikariDataSource pool = new HikariDataSource(config)) {
            // プールから借りる
            try (Connection conn = pool.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE visits (page VARCHAR(50))");
                // ブロックを出るときのcloseは、切断ではなくプールへ返す
            }
            for (String page : new String[] {"top", "news", "top"}) {
                try (Connection conn = pool.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "INSERT INTO visits VALUES (?)")) {
                    ps.setString(1, page);
                    ps.executeUpdate();
                }
            }
            try (Connection conn = pool.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                         "SELECT COUNT(*) FROM visits")) {
                rs.next();
                IO.println("記録した訪問: " + rs.getInt(1) + "件");
            }
        }
```

実行結果：

```
SLF4J(W): No SLF4J providers were found.
SLF4J(W): Defaulting to no-operation (NOP) logger implementation
SLF4J(W): See https://www.slf4j.org/codes.html#noProviders for further details.
記録した訪問: 3件
```

`HikariConfig`に、接続URL・ユーザー名・パスワードと、用意しておく接続の数の上限（`setMaximumPoolSize`）を設定し、`HikariDataSource`を作ります。
`HikariDataSource`は、標準APIの`javax.sql.DataSource`を実装したクラスです。
接続は`DriverManager.getConnection`の代わりに、プールの`getConnection`で借ります。

プールから借りた接続の`close`は、接続を切らずにプールへ返す動作になります。
そのため、使う側のコードは、第24章と同じようにtry-with-resourcesで接続を閉じればよいのです。
閉じ忘れると、接続がプールに戻らず、上限の数まで借りたところでほかの処理が接続を借りられなくなります。

接続URLの`DB_CLOSE_DELAY=-1`は、H2の設定です。
H2のメモリ上のデータベースは、最後の接続を閉じると消えます。
プールが接続を切り替えるときなどに開いた接続がなくなっても表が消えないように、この設定を付けています。
`HikariDataSource`は`AutoCloseable`を実装しているので、try-with-resourcesで閉じると、プールが持っている接続をすべて切ります。

最初の3行は、SLF4Jのログの出力先（`slf4j-simple`など）をクラスパスに加えていないときに、標準エラー出力へ出る警告です。
ログを出さないだけで、プールの動作には影響しません。

第24章の`JdbcUserDao`は、接続（`Connection`）をコンストラクターで受け取ります。
DAOを作る側がプールから借りた接続を渡せば、DAOのコードを変えずにコネクションプールを使えます。
