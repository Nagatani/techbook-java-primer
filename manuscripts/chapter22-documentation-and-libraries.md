# <b>22章</b> <span>ドキュメントと外部ライブラリ</span> <small>チーム開発を支える技術</small>

## 本章の学習目標

### 前提知識

必須
- 第4章のクラスとインスタンス（基本的なOOPの概念）
- 第14章の例外処理（try-catch、例外の種類）
- 第15章のファイル入出力（基本的なI/O操作）
- HTMLの基礎知識

推奨
- 第22章のユニットテスト（テストコードの作成）
- ビルドツール（Maven、Gradle）の基本概念
- JSONデータ形式の理解
- ソフトウェア開発プロセスの基本理解

### 学習目標

#### 知識理解目標
- Javadocによるドキュメント生成の仕組みと重要性
- 外部ライブラリの選択基準とライセンスの考慮事項
- Maven、Gradleなどのビルドツールの役割と概念
- 依存関係管理の原則と版数管理の重要性

#### 技能習得目標
- クラス、メソッド、フィールドを説明するJavadocコメントの記述とAPI仕様書の生成
- GsonによるJSON処理の実装
- Apache Commons Langなどのユーティリティライブラリの活用
- ビルドツールを使用したプロジェクト管理と依存関係解決

#### 実践的な活用目標
- プロダクションレベルのAPI仕様書の作成
- 外部APIとの連携とJSON処理の実装
- チーム開発における依存関係管理の運用
- ライブラリのアップデートとセキュリティ対応

#### 到達レベルの指標
- 保守性の高いドキュメント付きコードが作成できる
- ライセンス、信頼性、パフォーマンスを考慮して外部ライブラリを選択し、コードの再利用性を高める
- ビルドツールを使用したプロフェッショナルなプロジェクト管理ができる
- オープンソースライブラリのコントリビューションができる基礎素養を持つ

## Javadocによるドキュメント

Javaには、ソースコード内に記述した特定の形式のコメントから、API仕様書（HTML形式）を自動生成する
Javadocというツールが標準で備わっています。
Oracleが提供するJavaの公式APIドキュメントも、このJavadocによって生成されています。

### Javadocコメントの書き方

Javadocコメントは `/**` で始まり、`*/` で終わります。クラス、フィールド、コンストラクタ、メソッドなどの宣言の直前に記述します。

以下の例では、メソッドに対する完全なJavadocコメントの記述方法を示します。最初の行は概要説明、HTMLタグを使った詳細説明、そして様々なJavadocタグによる構造化された情報が含まれています。

<span class="listing-number">**サンプルコード22-2**</span>

```java
/**
 * 2つの整数の和を計算して返す
 * <p>
 * このメソッドは、nullを許容せず、オーバーフローも考慮しません
 * </p>
 *
 * @param a 加算する最初の整数
 * @param b 加算する2番目の整数
 * @return 2つの整数の和
 * @throws ArithmeticException 計算結果がオーバーフローした場合（この例では発生しないが記述例として）
 * @see Math#addExact(int, int)
 * @since 1.0
 * @author Taro Yamada
 */
public int add(int a, int b) {
    return a + b;
}
```

### 主要なJavadocタグ

コメント内では`@`で始まるJavadocタグを使い、構造化された情報を記述します。

| タグ | 説明 |
| :--- | :--- |
| `@param` | メソッドの引数を説明します。`@param 引数名 説明`の形式で記述します |
| `@return` | メソッドの戻り値を説明します |
| `@throws` | メソッドがスローする可能性のある例外を説明します。`@throws 例外クラス名 説明`の形式で記述します |
| `@author` | クラスやインターフェイスの作成者を記述します |
| `@version` | バージョン情報を記述します |
| `@since` | この機能が追加されたバージョンを記述します |
| `@see` | 関連するクラスやメソッドへのリンクを生成します |
| `@deprecated` | 非推奨のAPIであることを示し、代替手段を案内します |

### 実践的なJavadocの例

以下は、実際のプロジェクトで使用されるような、詳細で実用的なJavadocコメントの例です。クラスレベルのコメント、各フィールド、コンストラクタ、メソッドに至るまで、包括的なドキュメントを提供しています。

<span class="listing-number">**サンプルコード22-4**</span>

```java
/**
 * タスク管理システムのコアクラスである
 * <p>
 * このクラスは、ToDoリストアプリケーションのタスクを表現し、
 * タスクの作成、更新、完了状態の管理を行う
 * </p>
 * 
 * <h2>使用例</h2>
 * <pre>{@code
 * TaskManager manager = new TaskManager();
 * Task task = manager.createTask("買い物", Priority.HIGH);
 * task.setCompleted(true);
 * }</pre>
 * 
 * @author Taro Yamada
 * @version 2.0
 * @since 1.0
 * @see TaskManager
 * @see Priority
 */
public class Task {
    /**
     * タスクの優先度を表す列挙型
     */
    public enum Priority {
        /** 低優先度 */
        LOW,
        /** 中優先度 */
        MEDIUM,
        /** 高優先度 */
        HIGH
    }
    
    /** タスクの一意識別子 */
    private final long id;
    
    /** タスクのタイトル（必須、最大100文字） */
    private String title;
    
    /** タスクの詳細説明（オプション） */
    private String description;
    
    /** タスクの完了状態 */
    private boolean completed;
    
    /** タスクの優先度 */
    private Priority priority;
    
    /**
     * 指定されたタイトルとデフォルトの優先度で新しいタスクを作成する
     * 
     * @param title タスクのタイトル（null不可、空文字不可）
     * @throws IllegalArgumentException titleがnullまたは空文字の場合
     */
    public Task(String title) {
        this(title, Priority.MEDIUM);
    }
    
    /**
     * 指定されたタイトルと優先度で新しいタスクを作成する
     * 
     * @param title タスクのタイトル（null不可、空文字不可）
     * @param priority タスクの優先度（null不可）
     * @throws IllegalArgumentException titleがnullまたは空文字、
     *         またはpriorityがnullの場合
     */
    public Task(String title, Priority priority) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("タイトルは必須です");
        }
        if (priority == null) {
            throw new IllegalArgumentException("優先度は必須です");
        }
        
        this.id = System.currentTimeMillis();
        this.title = title;
        this.priority = priority;
        this.completed = false;
    }
    
    /**
     * タスクのタイトルを取得する
     * 
     * @return タスクのタイトル（nullではない）
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * タスクのタイトルを設定する
     * 
     * @param title 新しいタイトル（null不可、空文字不可）
     * @throws IllegalArgumentException titleがnullまたは空文字の場合
     */
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("タイトルは必須です");
        }
        this.title = title;
    }
    
    /**
     * タスクを完了状態にする
     * <p>
     * このメソッドは {@code setCompleted(true)} と同等である
     * </p>
     * 
     * @see #setCompleted(boolean)
     */
    public void complete() {
        this.completed = true;
    }
    
    /**
     * タスクの優先度を1段階上げる
     * <p>
     * 既にHIGHの場合は変更されません
     * </p>
     * 
     * @return 優先度が変更された場合はtrue、そうでない場合はfalse
     */
    public boolean increasePriority() {
        switch (priority) {
            case LOW:
                priority = Priority.MEDIUM;
                return true;
            case MEDIUM:
                priority = Priority.HIGH;
                return true;
            default:
                return false;
        }
    }
    
    /**
     * タスクを文字列表現に変換する
     * 
     * @return タスクの文字列表現
     * @deprecated {@link #toDetailedString()} を使用してください
     */
    @Deprecated(since = "2.0", forRemoval = true)
    @Override
    public String toString() {
        return String.format("Task[id=%d, title='%s']", id, title);
    }
    
    /**
     * タスクの詳細な文字列表現を返す
     * 
     * @return タスクの詳細情報を含む文字列
     * @since 2.0
     */
    public String toDetailedString() {
        return String.format(
            "Task[id=%d, title='%s', priority=%s, completed=%s]",
            id, title, priority, completed
        );
    }
}
```

### Javadocの生成方法

#### コマンドライン (javac)

<span class="listing-number">**サンプルコード22-5**</span>

```bash
# 基本的な使用方法
javadoc -d doc -author -version -private MyClass.java

# 複数ファイル、パッケージ指定
javadoc -d doc -sourcepath src -subpackages com.example -author -version

# 文字エンコーディング指定（日本語対応）
javadoc -d doc -encoding UTF-8 -charset UTF-8 -author -version *.java

# カスタムタイトルとウィンドウタイトル
javadoc -d doc -windowtitle "タスク管理API" -doctitle "タスク管理システム API仕様書" *.java
```

#### IntelliJ IDEA
メニューの `Tools` -> `Generate JavaDoc...` から生成できます。
- Output directory: `doc` または `javadoc`
- Other command LINE arguments: `-encoding UTF-8 -charset UTF-8`
- 著者情報やバージョン情報を含める場合は `-author`、`-version` オプションを追加

## 外部ライブラリの利用

現代のソフトウェア開発では、車輪の再発明を避け、高品質な外部ライブラリを活用するのが一般的です。Javaの世界には、さまざまな機能を提供する膨大な数のオープンソースライブラリが存在します。

### クラスパスとは

Java仮想マシン（JVM）がプログラムの実行時に必要なクラスファイル（`.class`ファイルや`.jar`ファイル）を
探すための場所（ディレクトリやファイルのパス）をクラスパスと呼びます。
外部ライブラリを利用するには、そのライブラリのJARファイルをクラスパスに含める必要があります。

### 依存関係管理ツール：Maven

手動でJARファイルをダウンロードし、クラスパスを設定するのは非常に手間がかかり、管理も煩雑です。そこで、MavenやGradleといった依存関係管理ツール（ビルドツール）を使うのが現代の標準的な開発スタイルです。

Mavenは、`pom.xml`という設定ファイルに利用したいライブラリの名前とバージョンを記述するだけで、
ライブラリの管理が完結します。
必要なJARファイルはMaven Central Repositoryなどのリポジトリから自動的にダウンロードされ、
クラスパスの設定も自動で行ってくれます。

#### `pom.xml`での依存関係の記述

たとえば、Googleが開発したJSONライブラリであるGsonを利用したい場合、`pom.xml`の`<dependencies>`セクションに以下のように記述します。

<span class="listing-number">**サンプルコード22-6**</span>

```xml
<dependencies>
    <!-- 他の依存関係... -->

    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>
</dependencies>
```
`groupId`, `artifactId`, `version`は、ライブラリを一意に識別するための座標（coordinates）です。これらの情報は、Maven Centralなどのリポジトリサイトで検索できます。

### 外部ライブラリの利用例 (Gson)

`pom.xml`に上記の設定を追加すると、IntelliJ IDEAは自動的にGsonライブラリをダウンロードします。その後は、自分のコードから`import`してライブラリの機能を利用できます。

以下の例では、Gsonの高度な機能を示します。
@SerializedNameアノテーションによるJSONフィールド名のカスタマイズ、
@Exposeによるシリアライズ対象の制御、GsonBuilderを使った詳細な設定など、
実務でよく使われるパターンを網羅しています。

<span class="listing-number">**サンプルコード22-8**</span>

```java
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

// Gsonの高度な使用例
public class Task {
    @SerializedName("task_title")  // JSONのフィールド名を指定
    private String title;
    
    private boolean completed;
    
    @Expose(serialize = false)  // シリアライズ時に除外
    private String internalId;
    
    private LocalDateTime createdAt;
    private List<String> tags;
    
    public Task(String title, boolean completed) {
        this.title = title;
        this.completed = completed;
        this.createdAt = LocalDateTime.now();
        this.tags = Arrays.asList("work", "important");
    }
    
    // ゲッター・セッター省略
    public String getTitle() { return title; }
    public boolean isCompleted() { return completed; }
}

public class GsonAdvancedExample {
    public static void main(String[] args) {
        // 1. カスタマイズされたGsonインスタンスの作成
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()  // 整形された出力
            .setDateFormat("yyyy-MM-dd HH:mm:ss")  // 日付フォーマット
            .excludeFieldsWithoutExposeAnnotation()  // @Exposeのないフィールドを除外
            .create();
        
        // 2. オブジェクトのシリアライズ
        Task task = new Task("買い物", false);
        String json = gson.toJson(task);
        System.out.println("Pretty JSON:\n" + json);
        
        // 3. コレクションの処理
        List<Task> tasks = Arrays.asList(
            new Task("買い物", false),
            new Task("勉強", true),
            new Task("運動", false)
        );
        
        String tasksJson = gson.toJson(tasks);
        System.out.println("\nTasks JSON:\n" + tasksJson);
        
        // 4. 複雑なデシリアライズ
        String complexJson = """
            {
                "task_title": "プログラミング",
                "completed": false,
                "createdAt": "2024-01-20 10:30:00",
                "tags": ["study", "java", "important"]
            }
            """;
        
        Task loadedTask = gson.fromJson(complexJson, Task.class);
        System.out.println("\nLoaded Task: " + loadedTask.getTitle());
    }
}
```

### Jackson - 高性能JSONライブラリ

Jacksonは、Gsonと並んで人気の高いJSONライブラリです。ストリーミングAPIによるメモリ使用量を最小限に抑えた処理とアノテーションベースの柔軟なマッピングが特徴です。

#### Mavenの依存関係

<span class="listing-number">**サンプルコード22-9**</span>

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.2</version>
</dependency>
```

#### Jacksonの使用例

Jacksonはエンタープライズアプリケーションで広く使われている高性能JSONライブラリです。
以下の例では、@JsonPropertyによるフィールド名のマッピング、@JsonIgnoreによる特定フィールドの除外、
TypeReferenceを使ったジェネリック型のデシリアライズなど、実践的な機能を紹介します。

<span class="listing-number">**サンプルコード22-11**</span>

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.*;

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
        
        // コンストラクタ、ゲッター、セッター
        public Product() {}
        
        public Product(int id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.attributes = new HashMap<>();
        }
        
        // ゲッター省略
        public String getName() { return name; }
        public double getPrice() { return price; }
    }
    
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        
        // 1. オブジェクトからJSONへ
        Product product = new Product(1, "ノートPC", 98000.0);
        String json = mapper.writeValueAsString(product);
        System.out.println("JSON: " + json);
        
        // 2. 整形されたJSON出力
        String prettyJson = mapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(product);
        System.out.println("\nPretty JSON:\n" + prettyJson);
        
        // 3. JSONからオブジェクトへ
        String jsonInput = """
            {
                "product_id": 2,
                "name": "マウス",
                "price": 2980.0,
                "attributes": {
                    "color": "black",
                    "wireless": "true"
                }
            }
            """;
        
        Product loaded = mapper.readValue(jsonInput, Product.class);
        System.out.println("\nLoaded: " + loaded.getName());
        
        // 4. コレクションの処理
        List<Product> products = Arrays.asList(
            new Product(1, "キーボード", 5980.0),
            new Product(2, "モニター", 25800.0)
        );
        
        String productsJson = mapper.writeValueAsString(products);
        
        // TypeReferenceを使った複雑な型のデシリアライズ
        List<Product> loadedProducts = mapper.readValue(productsJson, 
            new TypeReference<List<Product>>() {});
        
        System.out.println("\nLoaded products count: " + loadedProducts.size());
    }
}
```

### Apache Commons - ユーティリティライブラリ集

Apache Commonsは、Javaプログラミングでよく使われる機能を提供するライブラリ群です。

#### Commons Langの例

<span class="listing-number">**サンプルコード22-12**</span>

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

Apache Commons LangはJava標準APIを補完する便利なユーティリティメソッドを提供します。以下の例では、文字列操作、null安全な処理、数値ユーティリティなど、日常的に必要になる機能を示します。

<span class="listing-number">**サンプルコード22-14**</span>

```java
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.time.LocalDateTime;

public class CommonsLangExample {
    
    static class Person {
        private String name;
        private int age;
        
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
        
        @Override
        public boolean equals(Object obj) {
            return EqualsBuilder.reflectionEquals(this, obj);
        }
        
        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }
    
    public static void main(String[] args) {
        // 1. StringUtils - 文字列操作
        String text = "  Hello World  ";
        System.out.println("Original: '" + text + "'");
        System.out.println("Trimmed: '" + StringUtils.trim(text) + "'");
        System.out.println("Is blank?: " + StringUtils.isBlank("   "));
        System.out.println("Reverse: " + StringUtils.reverse("Hello"));
        System.out.println("Capitalize: " + StringUtils.capitalize("java"));
        
        // 空文字列の安全な処理
        String nullStr = null;
        System.out.println("Default: " + StringUtils.defaultString(nullStr, "デフォルト値"));
        
        // 2. ArrayUtils - 配列操作
        int[] numbers = {1, 2, 3, 4, 5};
        System.out.println("\nArray contains 3?: " + ArrayUtils.contains(numbers, 3));
        int[] reversed = ArrayUtils.clone(numbers);
        ArrayUtils.reverse(reversed);
        System.out.println("Reversed: " + ArrayUtils.toString(reversed));
        
        // 3. RandomStringUtils - ランダム文字列生成
        System.out.println("\nRandom alphabetic: " + 
            RandomStringUtils.randomAlphabetic(10));
        System.out.println("Random numeric: " + 
            RandomStringUtils.randomNumeric(6));
        System.out.println("Random alphanumeric: " + 
            RandomStringUtils.randomAlphanumeric(8));
        
        // 4. DateFormatUtils - 日付フォーマット
        Date now = new Date();
        System.out.println("\nDate formats:");
        System.out.println("ISO: " + DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(now));
        System.out.println("Custom: " + DateFormatUtils.format(now, "yyyy年MM月dd日 HH:mm:ss"));
        
        // 5. Builder utilities
        Person person = new Person("田中太郎", 30);
        System.out.println("\nPerson: " + person);
    }
}
```

#### Commons I/Oの例

<span class="listing-number">**サンプルコード22-15**</span>

```xml
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>
```

Apache Commons I/Oは、ファイルやストリームの操作を簡潔なメソッドで実現するライブラリです。
Java標準APIでは何行も必要なファイル操作が、1行で実装できます。
以下の例では、ファイルの読み書き、コピー、ディレクトリ操作、パス操作など、
実務で頻繁に使用される機能を紹介します。

<span class="listing-number">**サンプルコード22-17**</span>

```java
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.FileDeleteStrategy;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
        String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        System.out.println("File content:\n" + content);
        
        // 行単位での読み込み
        List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
        System.out.println("Lines count: " + lines.size());
        
        // 2. ファイルのコピー
        File copyFile = new File("example_copy.txt");
        FileUtils.copyFile(file, copyFile);
        
        // 3. ディレクトリ操作
        File dir = new File("temp_dir");
        FileUtils.forceMkdir(dir);  // 親ディレクトリも含めて作成
        
        // ディレクトリのサイズ取得
        long dirSize = FileUtils.sizeOfDirectory(new File("."));
        System.out.println("Current directory size: " + 
            FileUtils.byteCountToDisplaySize(dirSize));
        
        // 4. FilenameUtils - パス操作
        String filePath = "/home/user/documents/report.pdf";
        System.out.println("\nFile path analysis:");
        System.out.println("Base name: " + FilenameUtils.getBaseName(filePath));
        System.out.println("Extension: " + FilenameUtils.getExtension(filePath));
        System.out.println("Full path: " + FilenameUtils.getFullPath(filePath));
        
        // 5. IOUtils - ストリーム操作
        String text = "Stream processing example";
        try (InputStream is = IOUtils.toInputStream(text, StandardCharsets.UTF_8);
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            
            IOUtils.copy(is, os);
            String result = os.toString(StandardCharsets.UTF_8);
            System.out.println("\nStream copy result: " + result);
        }
        
        // クリーンアップ
        FileUtils.deleteQuietly(file);
        FileUtils.deleteQuietly(copyFile);
        FileUtils.deleteQuietly(dir);
    }
}
```

### OkHttp - モダンなHTTPクライアント

OkHttpは、接続プーリング、HTTP/2サポート、リトライ機能を備えたHTTP通信ライブラリです。

<span class="listing-number">**サンプルコード22-18**</span>

```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.11.0</version>
</dependency>
```

OkHttpはSquare社が開発した高性能なHTTPクライアントライブラリで、
コネクションプーリング、レスポンスキャッシュ、HTTP/2サポートなどの機能を提供します。
以下の例では、同期・GET/POSTリクエスト、非同期リクエストの実装方法を示します。

<span class="listing-number">**サンプルコード22-20**</span>

```java
import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
            
            System.out.println("GET Response:");
            System.out.println("Status: " + response.code());
            System.out.println("Body: " + response.body().string());
        }
        
        // 2. POSTリクエスト（JSONデータ送信）
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String json = """
            {
                "name": "Test User",
                "email": "test@example.com"
            }
            """;
        
        RequestBody body = RequestBody.create(json, JSON);
        Request postRequest = new Request.Builder()
            .url("https://httpbin.org/post")
            .post(body)
            .build();
        
        try (Response response = client.newCall(postRequest).execute()) {
            System.out.println("\nPOST Response:");
            System.out.println("Status: " + response.code());
            System.out.println("Body: " + response.body().string());
        }
        
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
                    System.out.println("\nAsync Response received!");
                    System.out.println("Thread: " + Thread.currentThread().getName());
                }
            }
        });
        
        // 非同期処理の完了を待つ
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

### Lombok - ボイラープレートコードの削減

Lombokは、アノテーションを使ってゲッタ、セッタ、コンストラクタなどを自動生成します。

<span class="listing-number">**サンプルコード22-21**</span>

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.28</version>
    <scope>provided</scope>
</dependency>
```

Lombokはコンパイル時にアノテーションを処理し、
ボイラープレートコードを自動生成する革新的なライブラリです。
@Dataアノテーション1つでゲッタ、セッタ、equals、hashCode、toStringメソッドが自動生成されます。
以下の例では、主要なアノテーションの使用方法を示します。

<span class="listing-number">**サンプルコード22-23**</span>

```java
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import lombok.NonNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

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

// ログ機能付きクラス
@Slf4j
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
        System.out.println("User: " + user);  // toStringが自動生成
        System.out.println("Username: " + user.getUsername());  // getterが自動生成
        
        user.setAge(26);  // setterが自動生成
        
        // 3. equalsの動作確認
        User sameUser = User.builder()
            .username("tanaka")
            .email("tanaka@example.com")
            .age(26)
            .password("different")
            .build();
        
        System.out.println("Equals: " + user.equals(sameUser));
        
        // 4. ログ出力（SLF4J）
        log.info("User created: {}", user.getUsername());
        log.debug("Debug information: {}", user);
        
        // 5. @NonNullの動作確認
        try {
            User invalidUser = new User(null, "email@example.com", 20, "pass");
        } catch (NullPointerException e) {
            log.error("Null username not allowed", e);
        }
    }
}

// 個別のアノテーション使用例
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

### Guava - Google製のコアライブラリ

GoogleのGuavaライブラリは、コレクション、キャッシュ、文字列処理などの高度な機能を提供します。

<span class="listing-number">**サンプルコード22-24**</span>

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>32.1.1-jre</version>
</dependency>
```

GuavaはGoogleが開発した、Javaの標準ライブラリを強化する多機能なライブラリです。
不変コレクション、Multimap、BiMap、Table、強力な文字列処理、キャッシュ機能など、
実用的な機能が豊富に含まれています。
以下の例では、これらの主要機能の使用方法を示します。

<span class="listing-number">**サンプルコード22-26**</span>

```java
import com.google.common.collect.*;
import com.google.common.base.*;
import com.google.common.cache.*;
import java.util.concurrent.TimeUnit;
import java.util.List;

public class GuavaExample {
    public static void main(String[] args) throws Exception {
        // 1. 不変コレクション
        ImmutableList<String> immutableList = ImmutableList.of("A", "B", "C");
        ImmutableMap<String, Integer> immutableMap = ImmutableMap.of(
            "one", 1,
            "two", 2,
            "three", 3
        );
        
        System.out.println("Immutable list: " + immutableList);
        System.out.println("Immutable map: " + immutableMap);
        
        // 2. Multimap - 1つのキーに複数の値
        Multimap<String, String> multimap = ArrayListMultimap.create();
        multimap.put("fruits", "apple");
        multimap.put("fruits", "banana");
        multimap.put("fruits", "orange");
        multimap.put("vegetables", "carrot");
        
        System.out.println("\nMultimap:");
        System.out.println("Fruits: " + multimap.get("fruits"));
        
        // 3. BiMap - 双方向マップ
        BiMap<String, Integer> biMap = HashBiMap.create();
        biMap.put("one", 1);
        biMap.put("two", 2);
        
        System.out.println("\nBiMap:");
        System.out.println("Key for 2: " + biMap.inverse().get(2));
        
        // 4. Table - 2次元のマップ
        Table<String, String, Integer> table = HashBasedTable.create();
        table.put("Tokyo", "2023", 14000000);
        table.put("Tokyo", "2024", 14100000);
        table.put("Osaka", "2023", 2700000);
        
        System.out.println("\nTable:");
        System.out.println("Tokyo 2024: " + table.get("Tokyo", "2024"));
        System.out.println("All Tokyo data: " + table.row("Tokyo"));
        
        // 5. 文字列処理
        String input = "  hello,  world,  java  ";
        List<String> parts = Splitter.on(',')
            .trimResults()
            .omitEmptyStrings()
            .splitToList(input);
        System.out.println("\nSplit result: " + parts);
        
        String joined = Joiner.on(" | ")
            .skipNulls()
            .join("A", null, "B", "C");
        System.out.println("Joined: " + joined);
        
        // 6. キャッシュ
        LoadingCache<String, String> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) {
                    // 実際のデータ取得処理
                    return "Value for " + key;
                }
            });
        
        System.out.println("\nCache:");
        System.out.println("First call: " + cache.get("key1"));
        System.out.println("Second call (cached): " + cache.get("key1"));
        
        // 7. Optional（Java 8より前から利用可能）
        Optional<String> optional = Optional.of("Hello");
        System.out.println("\nOptional:");
        System.out.println("Is present: " + optional.isPresent());
        System.out.println("Value: " + optional.or("Default"));
    }
}
```

### 外部ライブラリ選定の指針

ライブラリの選定では、機能性、信頼性、保守性、ライセンスの4つの観点から評価します。

#### ライブラリの成熟度と信頼性
- リリース履歴安定版がリリースされているか（1.0以上）
- 更新頻度：定期的にメンテナンスされているか
- コミュニティ活発なコミュニティが存在するか
- 採用実績：大規模プロジェクトでの採用実績

#### ライセンスの確認
<span class="listing-number">**サンプルコード22-28**</span>

```java
// pom.xmlでライセンスを確認
<licenses>
    <license>
        <name>Apache License, Version 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
    </license>
</licenses>
```

主要なオープンソースライセンス。
- Apache License 2.0: 商用利用可、特許条項あり
- MIT License: 最も制限が少ない
- GPL: ソースコード公開義務あり（商用利用注意）
- LGPL: リンクのみなら公開義務なし

#### 依存関係の管理

<span class="listing-number">**サンプルコード22-29**</span>

```xml
<!-- バージョン管理のベストプラクティス -->
<properties>
    <!-- バージョンを一元管理 -->
    <jackson.version>2.15.2</jackson.version>
    <commons.lang.version>3.12.0</commons.lang.version>
    <junit.version>5.9.3</junit.version>
</properties>

<dependencies>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>${jackson.version}</version>
    </dependency>
    
    <!-- テスト用ライブラリはscopeを指定 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>${junit.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

#### セキュリティの考慮事項

<span class="listing-number">**サンプルコード22-30**</span>

```xml
<!-- 依存関係の脆弱性チェックプラグイン -->
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>8.3.1</version>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### ライブラリの組み合わせ例

実際のプロジェクトでよく使われる組み合わせ。

<span class="listing-number">**サンプルコード22-31**</span>

```xml
<!-- Webアプリケーション開発 -->
<dependencies>
    <!-- Webフレームワーク -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>3.1.0</version>
    </dependency>
    
    <!-- JSON処理 -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.15.2</version>
    </dependency>
    
    <!-- データベースアクセス -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
        <version>3.1.0</version>
    </dependency>
    
    <!-- ユーティリティ -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.12.0</version>
    </dependency>
    
    <!-- ログ -->
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.4.8</version>
    </dependency>
    
    <!-- テスト -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.9.3</version>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.4.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 実践的な開発フロー

<span class="listing-number">**サンプルコード22-33**</span>

```java
// 1. プロジェクトの初期化（Maven）
mvn archetype:generate -DgroupId=com.example -DartifactId=myapp

// 2. 依存関係の追加（pom.xmlを編集後）
mvn clean install

// 3. 依存関係ツリーの確認
mvn dependency:tree

// 4. 不要な依存関係の検出
mvn dependency:analyze

// 5. 最新バージョンの確認
mvn versions:display-dependency-updates
```

### トラブルシューティング

よくある問題と解決方法。

<span class="listing-number">**サンプルコード22-35**</span>

```java
// 1. 依存関係の競合
// 解決策: 明示的にバージョンを指定
<dependency>
    <groupId>commons-logging</groupId>
    <artifactId>commons-logging</artifactId>
    <version>1.2</version>
</dependency>

// 2. 推移的依存関係の除外
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>5.3.28</version>
    <exclusions>
        <exclusion>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>

// 3. ローカルリポジトリのクリア（キャッシュ問題）
mvn dependency:purge-local-repository
```

## まとめ

- Javadocは、ソースコード内のコメントからAPI仕様書を自動生成する標準ツールである。パラメータ、戻り値、例外を記述したコメントは、APIの理解を容易にし、チーム間のコミュニケーションを円滑にする
- 外部ライブラリは、既存の優れた機能を活用し、開発を効率化するために必要である
- Mavenのような依存関係管理ツールを使うことで、外部ライブラリの導入と管理が劇的に簡単になる
- `pom.xml`に必要なライブラリの情報を記述するだけで、ライブラリのダウンロードからクラスパスの設定までが自動で行われる

## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter22/`

### 課題構成
- 本章の基本概念の理解確認
- 応用的な実装練習
- 実践的な総合問題

詳細な課題内容と実装のヒントは、各課題フォルダ内のREADME.mdを参照してください。

1. 基礎課題： Javadocコメントの記述とHTML文書の生成
2. 発展課題： Mavenプロジェクトの作成と外部ライブラリの統合
3. チャレンジ課題： 複数のライブラリを組み合わせた実用的なアプリケーション開発

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

次のステップ： 基礎課題が完了したら、第23章「ビルドとデプロイ」に進みましょう。

## よくあるエラーと対処法

ドキュメント作成と外部ライブラリの使用において、よく遭遇するエラーとその対処法を説明します。

### Javadocの生成エラー

#### 1. Javadocコメントの構文エラー

##### 問題
Javadocコメントの構文が正しくありません。

##### エラー例

<span class="listing-number">**サンプルコード22-36**</span>

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

##### エラーメッセージ
```
warning: @param has no meaning in class documentation
warning: @return has no meaning in class documentation
```

##### 解決策

<span class="listing-number">**サンプルコード22-37**</span>

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

#### 2. 文字エンコーディングの問題

##### 問題
日本語のJavadocが文字化けします。

##### エラー例

<span class="listing-number">**サンプルコード22-38**</span>

```bash
javadoc -d docs src/*.java
# 日本語が文字化けして表示される
```

##### 解決策

<span class="listing-number">**サンプルコード22-39**</span>

```bash
# 文字エンコーディングを明示的に指定
javadoc -d docs -encoding UTF-8 -charset UTF-8 -docencoding UTF-8 src/*.java

# Mavenを使用する場合
mvn javadoc:javadoc -Dfile.encoding=UTF-8
```

#### 3. HTMLタグの誤用

##### 問題
JavadocでHTMLタグを間違って使用。

##### エラー例

<span class="listing-number">**サンプルコード22-40**</span>

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

##### エラーメッセージ
```
warning: unclosed tag: <strong>
warning: unclosed tag: <li>
```

##### 解決策

<span class="listing-number">**サンプルコード22-41**</span>

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

### 依存関係の競合

#### 1. バージョン競合

##### 問題
異なるバージョンのライブラリが競合します。

##### エラー例

<span class="listing-number">**サンプルコード22-42**</span>

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

##### エラーメッセージ
```
java.lang.NoSuchMethodError: com.fasterxml.jackson.core.JsonFactory.createGenerator
```

##### 解決策

<span class="listing-number">**サンプルコード22-43**</span>

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

#### 2. 推移的な依存関係の問題

##### 問題
間接的に依存するライブラリが競合します。

##### エラー例

<span class="listing-number">**サンプルコード22-44**</span>

```bash
mvn dependency:tree
# 出力例
[INFO] com.example:myapp:jar:1.0.0
[INFO] +- org.springframework:spring-core:jar:5.3.0:compile
[INFO] |  \- commons-logging:commons-logging:jar:1.2:compile
[INFO] \- org.slf4j:slf4j-api:jar:1.7.30:compile
[INFO]    \- commons-logging:commons-logging:jar:1.1.3:compile
```

##### 解決策

<span class="listing-number">**サンプルコード22-45**</span>

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

### ライブラリのバージョン管理

#### 1. 古いバージョンの使用

##### 問題
セキュリティ脆弱性のある古いライブラリを使用。

##### エラー例

<span class="listing-number">**サンプルコード22-46**</span>

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.9.8</version>  <!-- 脆弱性のあるバージョン -->
</dependency>
```

##### 解決策

<span class="listing-number">**サンプルコード22-47**</span>

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

#### 2. バージョン範囲の誤用

##### 問題
バージョン範囲が適切でありません。

##### エラー例

<span class="listing-number">**サンプルコード22-48**</span>

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>[3.0,)</version>  <!-- 上限なしは危険 -->
</dependency>
```

##### 解決策

<span class="listing-number">**サンプルコード22-49**</span>

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>  <!-- 具体的なバージョンを指定 -->
</dependency>
```

### APIの非互換性

#### 1. メソッドシグネチャの変更

##### 問題
ライブラリのバージョンアップでAPIが変更される。

##### エラー例

<span class="listing-number">**サンプルコード22-50**</span>

```java
// 古いバージョン（1.x）
ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(object);

// 新しいバージョン（2.x）でコンパイルエラー
```

##### エラーメッセージ
```
java.lang.NoSuchMethodError: com.fasterxml.jackson.databind.ObjectMapper.writeValueAsString
```

##### 解決策

<span class="listing-number">**サンプルコード22-51**</span>

```java
// 新しいAPIに対応
ObjectMapper mapper = new ObjectMapper();
try {
    String json = mapper.writeValueAsString(object);
} catch (JsonProcessingException e) {
    // 新しいバージョンでは例外処理が必要
    throw new RuntimeException("JSON変換エラー", e);
}
```

#### 2. 廃止予定APIの使用

##### 問題
廃止予定のAPIを使用している。

##### エラー例

<span class="listing-number">**サンプルコード22-52**</span>

```java
@SuppressWarnings("deprecation")
Date date = new Date(2023, 1, 1);  // 廃止予定のコンストラクタ
```

##### 解決策

<span class="listing-number">**サンプルコード22-53**</span>

```java
// 新しいAPIを使用
LocalDate date = LocalDate.of(2023, 1, 1);
Date legacyDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
```

### クラスパスの問題

#### 1. クラスが見つからない

##### 問題
必要なクラスがクラスパスに存在しありません。

##### エラー例

<span class="listing-number">**サンプルコード22-54**</span>

```java
import org.apache.commons.lang3.StringUtils;

public class Example {
    public static void main(String[] args) {
        StringUtils.isEmpty("test");  // NoClassDefFoundError
    }
}
```

##### エラーメッセージ
```
java.lang.NoClassDefFoundError: org/apache/commons/lang3/StringUtils
```

##### 解決策

<span class="listing-number">**サンプルコード22-55**</span>

```xml
<!-- 必要な依存関係を追加 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

#### 2. 実行時とコンパイル時のクラスパスの違い

##### 問題
コンパイルは成功するが実行時にエラーが発生。

##### エラー例

<span class="listing-number">**サンプルコード22-56**</span>

```bash
# コンパイル時
javac -cp "lib/*" src/Example.java

# 実行時にクラスパスを指定し忘れ
java Example  # ClassNotFoundException
```

##### 解決策

<span class="listing-number">**サンプルコード22-57**</span>

```bash
# 実行時にも必要なクラスパスを指定
java -cp "lib/*:src" Example

# または実行可能JARを作成
mvn clean package
java -jar target/myapp.jar
```

### 設定ファイルの問題

#### 1. 設定ファイルの読み込みエラー

##### 問題
設定ファイルが正しく読み込まれありません。

##### エラー例

<span class="listing-number">**サンプルコード22-58**</span>

```java
// application.propertiesが見つからない
Properties props = new Properties();
props.load(new FileInputStream("application.properties"));
```

##### エラーメッセージ
```
java.io.FileNotFoundException: application.properties (No such file or directory)
```

##### 解決策

<span class="listing-number">**サンプルコード22-59**</span>

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

#### 2. 設定値の型変換エラー

##### 問題
設定値の型が期待と異なる。

##### エラー例

<span class="listing-number">**サンプルコード22-60**</span>

```properties
# application.properties
server.port=8080abc  # 数値でない
```

<span class="listing-number">**サンプルコード22-61**</span>

```java
int port = Integer.parseInt(props.getProperty("server.port"));
```

##### エラーメッセージ
```
java.lang.NumberFormatException: For input string: "8080abc"
```

##### 解決策

<span class="listing-number">**サンプルコード22-62**</span>

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

### ライブラリの組み合わせ問題

#### 1. ログフレームワークの競合

##### 問題
複数のログフレームワークが競合します。

##### エラー例
```
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:.../slf4j-log4j12-1.7.30.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:.../logback-classic-1.2.3.jar!/org/slf4j/impl/StaticLoggerBinder.class]
```

##### 解決策

<span class="listing-number">**サンプルコード22-63**</span>

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

#### 2. 異なるJSONライブラリの混在

##### 問題
JacksonとGsonが混在してAPIが混乱します。

##### 解決策

<span class="listing-number">**サンプルコード22-64**</span>

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