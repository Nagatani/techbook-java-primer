# 第21章 ドキュメントと外部ライブラリ

## 章末演習

本章で学んだドキュメントと外部ライブラリの活用概念を実践的な練習課題に取り組みましょう。

### 演習の目標
ドキュメント作成と外部ライブラリの活用技術を習得します。

### 技術的背景：現代的なJava開発エコシステム

**ドキュメントの重要性：**

優れたドキュメントは、ソフトウェア開発において以下の重要な役割を果たします：

1. **開発効率の向上**
   - APIの使用方法が明確
   - 試行錯誤の時間削減
   - チーム内の知識共有促進

2. **保守性の向上**
   - 将来の自分への説明
   - 新メンバーのオンボーディング短縮
   - 設計意図の記録

3. **品質保証**
   - 仕様の明文化
   - 期待動作の明確化
   - エッジケースの文書化

**Javadocの実践的活用：**

```java
/**
 * 銀行口座を表すクラス。
 * 
 * <p>このクラスはスレッドセーフではありません。
 * 同期が必要な場合は{@link SynchronizedAccount}を使用してください。</p>
 * 
 * @author 田中太郎
 * @version 1.0
 * @since 2024-01-01
 * @see SynchronizedAccount
 */
public class BankAccount {
    /**
     * 口座から指定金額を引き出します。
     * 
     * @param amount 引き出し金額（正の値）
     * @return 引き出し後の残高
     * @throws IllegalArgumentException 金額が0以下の場合
     * @throws InsufficientFundsException 残高不足の場合
     */
    public double withdraw(double amount) 
        throws InsufficientFundsException {
        // 実装
    }
}
```

**外部ライブラリのエコシステム：**

1. **Maven Central Repository**
   - 40万以上のライブラリ
   - 自動依存関係解決
   - セキュリティ脆弱性チェック

2. **主要ライブラリカテゴリ**
   - **ロギング**: SLF4J, Logback
   - **JSON処理**: Jackson, Gson
   - **HTTPクライアント**: Apache HttpClient, OkHttp
   - **テスト**: JUnit, Mockito, AssertJ
   - **ユーティリティ**: Apache Commons, Guava

3. **依存関係管理の課題**
   ```
   プロジェクト
   └── LibraryA v1.0
       └── LibraryB v2.0
   └── LibraryC v1.0
       └── LibraryB v3.0  ← バージョン競合！
   ```

**ビルドツールの活用：**

Maven/Gradleは単なるビルドツール以上の価値を提供：

```xml
<!-- Maven POM.xml の例 -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.2</version>
    <scope>compile</scope>
</dependency>
```

```gradle
// Gradle build.gradle の例
dependencies {
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.15.2'
    testImplementation 'org.junit.jupiter:junit-jupiter:5.9.3'
}
```

**実務でのベストプラクティス：**

1. **ライブラリ選定基準**
   - ライセンス互換性の確認
   - コミュニティの活発さ
   - 最終更新日とメンテナンス状況
   - ドキュメントの充実度

2. **セキュリティ考慮事項**
   - 定期的な依存関係更新
   - 脆弱性スキャンの自動化
   - ライセンスコンプライアンス

この演習では、実際のプロジェクトで必要となるこれらの技術を実践的に学びます。

### 課題の場所
演習課題は `exercises/chapter21/` ディレクトリに用意されています：

```
exercises/chapter21/
├── basic/          # 基本課題（必須）
│   ├── README.md   # 課題の詳細説明
│   ├── APIDocumentation.java
│   └── SimpleLibraryDoc.java
├── advanced/       # 発展課題（推奨）
└── challenge/      # 挑戦課題（上級者向け）
```


## 本章の学習目標

### 前提知識

本章を学習するための必須前提として、第20章までに習得した総合的なJava開発能力が必要です。これまでの章で学んだすべての技術要素を組み合わせて、実用的なアプリケーションを開発できるレベルに達していることが重要です。また、基本的なソフトウェア工学の知識も必要で、ソフトウェア開発ライフサイクル、バージョン管理、継続的インテグレーションなどの概念を理解していることが求められます。プロジェクト管理の基本的な理解も重要で、依存関係の管理、リリース計画、技術的負債の概念などを理解していることで、本章で学ぶ内容の実務的な価値をより深く理解できます。

開発経験の前提として、中規模なJavaアプリケーションの開発経験があることが望ましいです。特に、標準ライブラリだけでは実現が困難な機能に直面し、外部ライブラリの必要性を感じた経験があれば、本章の内容をより実践的に理解できます。また、外部APIやライブラリの使用に対する関心と、それらを適切に選択・活用したいという意欲があることも重要です。

### 学習目標

本章では、現代のJava開発に不可欠な外部ライブラリの活用と、それを支えるビルドツールについて体系的に学習します。知識理解の面では、まず依存関係管理の重要性と、それがもたらす課題について深く理解します。手動でJARファイルを管理することの限界、推移的依存関係の複雑さ、バージョン競合の問題など、実際の開発で直面する課題を理解し、それらを解決するための体系的なアプローチを学びます。

Maven/Gradleといった現代的なビルドシステムの理解も重要な学習目標です。これらのツールがどのように依存関係を解決し、ビルドプロセスを自動化し、プロジェクトの構造を標準化するのかを理解します。MavenのPOM（Project Object Model）やGradleのDSL（Domain Specific Language）の概念を学び、宣言的な依存関係管理の利点を理解します。また、ライブラリ選択の判断基準についても学習します。ライセンス、コミュニティの活発さ、ドキュメントの充実度、セキュリティアップデートの頻度など、技術的な側面だけでなく、長期的な保守性を考慮した選択基準を身につけます。

技能習得の観点では、Maven/Gradleプロジェクトの作成と管理の実践的なスキルを習得します。プロジェクトの初期化、依存関係の追加、カスタムタスクの作成、マルチモジュールプロジェクトの構築など、実務で必要となる技術を段階的に学びます。主要ライブラリの活用では、JSONパーサーとして広く使われるJackson、ユーティリティライブラリのApache Commons、HTTPクライアントライブラリなど、実務でよく使用されるライブラリの使い方を習得します。これらのライブラリを通じて、外部ライブラリの一般的な使用パターンと、APIドキュメントの効果的な読み方を学びます。

ソフトウェア工学能力の面では、効率的な開発フローの構築方法を学びます。ビルドツールを活用した自動化、継続的インテグレーションとの統合、開発環境の標準化など、チーム開発において重要な要素を理解します。品質の高いソフトウェアの継続的な開発では、依存関係の定期的な更新、セキュリティ脆弱性のチェック、廃止予定のAPIへの対応など、長期的な保守性を保つための実践的な手法を学びます。

最終的な到達レベルとして、外部ライブラリを効果的に活用した実用的なアプリケーションが開発できるようになることを目指します。これには、適切なライブラリの選択眼、効率的な統合方法、そしてトラブルシューティング能力が含まれます。ビルドツールを使った効率的な開発環境の構築により、手動作業を最小限に抑え、開発の生産性を大幅に向上させることができます。また、ライブラリのバージョンアップグレードを適切に実施できる能力により、セキュリティと機能性を維持しながら、技術的負債を最小限に抑えた持続可能な開発が可能になります。

---

## 21.1 Javadocによるドキュメント

Javaには、ソースコード内に記述した特定の形式のコメントから、API仕様書（HTML形式）を自動生成する**Javadoc**というツールが標準で備わっています。Oracleが提供するJavaの公式APIドキュメントも、このJavadocによって生成されています。

### Javadocコメントの書き方

Javadocコメントは `/**` で始まり、`*/` で終わります。クラス、フィールド、コンストラクタ、メソッドなどの宣言の直前に記述します。

```java
/**
 * 2つの整数の和を計算して返します。
 * <p>
 * このメソッドは、nullを許容せず、オーバーフローも考慮しません。
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

コメント内では`@`で始まる**Javadocタグ**を使い、構造化された情報を記述します。

| タグ | 説明 |
| :--- | :--- |
| `@param` | メソッドの引数を説明します。`@param 引数名 説明`の形式で記述します。 |
| `@return` | メソッドの戻り値を説明します。 |
| `@throws` | メソッドがスローする可能性のある例外を説明します。`@throws 例外クラス名 説明`の形式で記述します。 |
| `@author` | クラスやインターフェイスの作成者を記述します。 |
| `@version` | バージョン情報を記述します。 |
| `@since` | この機能が追加されたバージョンを記述します。 |
| `@see` | 関連するクラスやメソッドへのリンクを生成します。 |
| `@deprecated` | 非推奨のAPIであることを示し、代替手段を案内します。 |

### 実践的なJavadocの例

```java
/**
 * タスク管理システムのコアクラスです。
 * <p>
 * このクラスは、ToDoリストアプリケーションのタスクを表現し、
 * タスクの作成、更新、完了状態の管理を行います。
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
     * 指定されたタイトルとデフォルトの優先度で新しいタスクを作成します。
     * 
     * @param title タスクのタイトル（null不可、空文字不可）
     * @throws IllegalArgumentException titleがnullまたは空文字の場合
     */
    public Task(String title) {
        this(title, Priority.MEDIUM);
    }
    
    /**
     * 指定されたタイトルと優先度で新しいタスクを作成します。
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
     * タスクのタイトルを取得します。
     * 
     * @return タスクのタイトル（nullではない）
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * タスクのタイトルを設定します。
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
     * タスクを完了状態にします。
     * <p>
     * このメソッドは {@code setCompleted(true)} と同等です。
     * </p>
     * 
     * @see #setCompleted(boolean)
     */
    public void complete() {
        this.completed = true;
    }
    
    /**
     * タスクの優先度を1段階上げます。
     * <p>
     * 既にHIGHの場合は変更されません。
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
     * タスクを文字列表現に変換します。
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
     * タスクの詳細な文字列表現を返します。
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
- 必要に応じて `-author`、`-version` オプションを追加

## 21.2 外部ライブラリの利用

現代のソフトウェア開発では、車輪の再発明を避け、高品質な**外部ライブラリ**を活用するのが一般的です。Javaの世界には、さまざまな機能を提供する膨大な数のオープンソースライブラリが存在します。

### クラスパスとは

Java仮想マシン（JVM）がプログラムの実行時に必要なクラスファイル（`.class`ファイルや`.jar`ファイル）を探すための場所（ディレクトリやファイルのパス）を**クラスパス**と呼びます。外部ライブラリを利用するには、そのライブラリのJARファイルをクラスパスに含める必要があります。

### 依存関係管理ツール：Maven

手動でJARファイルをダウンロードし、クラスパスを設定するのは非常に手間がかかり、管理も煩雑です。そこで、**Maven**や**Gradle**といった**依存関係管理ツール（ビルドツール）**を使うのが現代の標準的な開発スタイルです。

**Maven**は、`pom.xml`という設定ファイルに、利用したいライブラリの名前とバージョンを記述するだけで、必要なJARファイルをインターネット上のリポジトリ（Maven Central Repositoryなど）から自動的にダウンロードし、クラスパスの設定まで行ってくれます。

#### `pom.xml`での依存関係の記述

たとえば、Googleが開発したJSONライブラリである**Gson**を利用したい場合、`pom.xml`の`<dependencies>`セクションに以下のように記述します。

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

```java
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;
import java.util.Date;
import java.util.List;
import java.util.Arrays;

// Gsonの高度な使用例
public class Task {
    @SerializedName("task_title")  // JSONのフィールド名を指定
    private String title;
    
    private boolean completed;
    
    @Expose(serialize = false)  // シリアライズ時に除外
    private String internalId;
    
    private Date createdAt;
    private List<String> tags;
    
    public Task(String title, boolean completed) {
        this.title = title;
        this.completed = completed;
        this.createdAt = new Date();
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

Jacksonは、Gsonと並んで人気の高いJSONライブラリです。高速な処理と豊富な機能が特徴です。

#### Mavenの依存関係
```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.2</version>
</dependency>
```

#### Jacksonの使用例
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
```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

```java
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.util.Date;

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
```xml
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>
```

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

OkHttpは、効率的なHTTP通信を行うためのライブラリです。

```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.11.0</version>
</dependency>
```

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

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.28</version>
    <scope>provided</scope>
</dependency>
```

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

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>32.1.1-jre</version>
</dependency>
```

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

適切なライブラリを選ぶことは、プロジェクトの成功に大きく影響します。以下の観点から評価しましょう。

#### 1. ライブラリの成熟度と信頼性
- **リリース履歴**: 安定版がリリースされているか（1.0以上）
- **更新頻度**: 定期的にメンテナンスされているか
- **コミュニティ**: 活発なコミュニティが存在するか
- **採用実績**: 大規模プロジェクトでの採用実績

#### 2. ライセンスの確認
```java
// pom.xmlでライセンスを確認
<licenses>
    <license>
        <name>Apache License, Version 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
    </license>
</licenses>
```

主要なオープンソースライセンス：
- **Apache License 2.0**: 商用利用可、特許条項あり
- **MIT License**: 最も制限が少ない
- **GPL**: ソースコード公開義務あり（商用利用注意）
- **LGPL**: リンクのみなら公開義務なし

#### 3. 依存関係の管理

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

#### 4. セキュリティの考慮事項

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

#### 5. ライブラリの組み合わせ例

実際のプロジェクトでよく使われる組み合わせ：

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

よくある問題と解決方法：

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

## より深い理解のために

本章で学んだドキュメント化とライブラリ活用をさらに深く理解したい方は、付録B.22「オープンソースエコシステムとライブラリ設計」を参照してください。この付録では以下の高度なトピックを扱います：

- **オープンソース設計原則**: SOLID原則のOSS実装、Interface Segregationの実践
- **API設計のベストプラクティス**: Fluent Interface、型安全なDSL設計
- **バージョニング戦略**: セマンティックバージョニング、後方互換性保証
- **プラグインアーキテクチャ**: 拡張可能なプラグインシステム、型安全な拡張ポイント
- **包括的なAPI文書化**: OpenAPI/Swagger統合、パフォーマンス最適化

これらの技術は、長期間愛用されるライブラリやフレームワークの開発において重要な役割を果たします。

## まとめ

-   **Javadoc**は、ソースコード内のコメントからAPI仕様書を自動生成する標準ツールです。適切なコメントは、コードの可読性と保守性を高めます。
-   **外部ライブラリ**は、既存の優れた機能を活用し、開発を効率化するために不可欠です。
-   **Maven**のような依存関係管理ツールを使うことで、外部ライブラリの導入と管理が劇的に簡単になります。
-   `pom.xml`に必要なライブラリの情報を記述するだけで、ライブラリのダウンロードからクラスパスの設定までが自動で行われます。