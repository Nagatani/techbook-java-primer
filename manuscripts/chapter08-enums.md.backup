# <b>8章</b> <span>列挙型(Enums)</span> <small>型安全な定数と状態管理の技術</small>

## 本章の学習目標

### この章で学ぶこと

1. Enumの基本理解
    - 型安全な定数定義、標準メソッド（values、valueOf等）の活用
2. 高度なEnum機能
    - フィールドとメソッドを持つEnum、コンストラクタ、インターフェイス実装
3. デザインパターンへの応用
    - Singletonパターン、Strategyパターン、Stateパターンでの活用
4. 実践的な活用
    - switch文との組み合わせ、EnumSetとEnumMap、エラーハンドリング

### この章を始める前に

第6章のfinalキーワードとstatic final定数を理解していれば準備完了です。

enumがなぜ役立つのか、従来の定数定義の何が問題なのかを実感を持って理解できます。
オブジェクト指向の基本概念、とくにクラスとインスタンスの関係、静的メンバーと非静的メンバーの違いについても理解していることが重要です。

設計経験の面では、実際に定数管理で苦労した経験があると本章の内容がより深く理解できます。
たとえば、整数定数を使った状態管理で、無効な値が設定されてバグが発生した経験や、定数の意味がわかりにくくてコードの可読性が低下した経験などがあれば、enumの価値を実感できるでしょう。
また、switch文やif文を使った分岐処理の基本的な経験があることで、enumとの組み合わせによる改善効果を理解しやすくなります。

### 本章の学習目標

本章では、Java特有の強力な機能であるenum（列挙型）について学習します。
知識理解の面では、まずenumの設計思想と、従来の`static final`定数との根本的な違いを理解します。
enumは単なる定数の集合ではなく、特殊な形式のクラスであり、型安全性を保証するしくみです。
この設計により、コンパイル時に不正な値の使用を防ぎ、実行時エラーを着実に削減できます。

enumのインスタンス性とsingleton性の理解も重要なポイントです。
各enum定数は、そのenum型の唯一のインスタンスとして存在し、JVMによって管理されます。
この特性により、`==`演算子での比較が可能になり、switch文でのO(1)時間での分岐処理が実現されています。
また、values()やvalueOf()といったenum特有のメソッドがどのように動作し、どのような場面で活用できるのかを理解します。

技能習得の観点では、基本的なenumの定義からはじめ、フィールドやメソッドを持つ高度なenum設計まで段階的に習得します。
単純な定数の列挙だけでなく、各定数に関連するデータや振る舞いを持たせることで、より表現力豊かな設計が可能になります。
たとえば、曜日のenumに営業日かどうかを判定するメソッドを追加したり、HTTPステータスコードのenumに説明文を持たせたりといった実践的な設計パターンを学びます。

設計能力の面では、各状況に応じた抽象化レベルでenumを活用する判断力を養います。
すべての定数をenumにすればよいわけではなく、値の集合が明確に定義でき、将来的にも変更が少ない場合に適用するとよいでしょう。
また、Strategy PatternやState Patternといったデザインパターンとenumを組み合わせることで、より拡張可能で保守性の高い設計を実現する方法も学びます。
ドメイン駆動設計（DDD）におけるValue Objectとしてのenum活用など、実務で求められる高度な設計技法も習得します。

最終的な到達レベルとして、型安全性を最大限に活用したenum設計ができるようになることを目指します。
複雑なビジネスロジックをenumで表現し、コードの意図を明確に伝えられます。
また、既存のレガシーコードに含まれる整数定数や文字列定数を、適切にenumへリファクタリングする能力も身につけます。
これにより、保守性と可読性が明確に向上したコードベースを構築できます。

## enumとは？

`enum`（列挙型）は、決まったいくつかの値だけを取りうる型を定義するための、特殊なクラスです。たとえば、「曜日（月、火、水、木、金、土、日）」や「信号の色（赤、青、黄）」のように、値の範囲が限定されているものを扱うのに最適です。

### なぜenumが必要なのか？

`enum`がない場合、このような定数は`public static final int`フィールドとして定義することが一般的でした。しかし、この方法には以下のような問題点があります。
- `int`型ですので、`setSignal(100)`のような無効な値も代入できてしまう
- `int`という型からは、それが「信号の色」を表すという情報がわからない
- 変数の値を表示しても`0`としか出力されず、それが「赤」を意味するのかわからない

`enum`は、これらの問題をすべて解決します。`enum`で定義された各要素（列挙子）は、その`enum`型のユニークなインスタンスとして扱われ、型安全性を保証します。

### 基本的な構文と使い方

<span class="listing-number">**サンプルコード8-1**</span>

```java
public enum DayOfWeek {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
}
```
これだけで、`DayOfWeek`という新しい型が作られ、その型が取りうる値は定義された7つの曜日に限定されます。

<span class="listing-number">**サンプルコード8-2**</span>

```java
public class EnumExample {
    public static void main(String[] args) {
        DayOfWeek today = DayOfWeek.WEDNESDAY;

        System.out.println("今日は " + today + " です。"); // toString()がオーバーライドされており、"WEDNESDAY"と表示される

        // 型安全なので、不正な値はコンパイルエラーになる
        // DayOfWeek tomorrow = "Thursday"; // エラー
    }
}
```

## enumの便利な機能

Javaの`enum`は、暗黙的に`java.lang.Enum`クラスを継承しており、便利なメソッドが標準で提供されています。

- `values()`: すべての列挙子を定義順に格納した配列を返す
- `valueOf(String name)`: 指定された名前の列挙子を返す
- `name()`: 列挙子の名前（定義した通りの文字列）を返す
- `ordinal()`: 列挙子の定義順序（ゼロから始まる）を返す

<span class="listing-number">**サンプルコード8-3**</span>

```java
// DayOfWeek.values() を使って全曜日をループ処理
for (DayOfWeek day : DayOfWeek.values()) {
    System.out.println(day.name() + " は " + day.ordinal() + "番目");
}

// 文字列からenumインスタンスを生成
DayOfWeek friday = DayOfWeek.valueOf("FRIDAY");
System.out.println(friday); // FRIDAY
```

### `switch`文との連携

`enum`は`switch`文と非常に相性がよく、安全で読みやすいコードを書くことができます。

<span class="listing-number">**サンプルコード8-4**</span>

```java
public class TrafficLight {
    public static void checkSignal(DayOfWeek day) {
        // Java 14以降のswitch式を使うと、より簡潔に書ける
        String typeOfDay = switch (day) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "平日";
            case SATURDAY, SUNDAY -> "週末";
        };
        System.out.println(day + " は " + typeOfDay + " です。");
    }
}
```
`case`句では、`DayOfWeek.MONDAY`のように書く必要はなく、`MONDAY`とだけ書ける点に注意してください。

## フィールドとメソッドを持つenum

`enum`は単なる定数の集まりではなく、クラスのようにフィールドやコンストラクタ、メソッドを持たせることができます。これにより、定数に関連するデータや振る舞いをカプセル化できます。

たとえば、太陽系の惑星を表現する場合を考えてみましょう。単純に`MERCURY`、`VENUS`、`EARTH`といった定数を定義するだけでなく、各惑星の質量や半径といった物理的な属性、さらにはそれらを使った計算（表面重力など）も含めることができます。
このようなリッチなenumは、科学計算や物理シミュレーションなどの分野でとくに有効です。

#### 使用場面
- 固定された選択肢のそれぞれに複数の属性が関連付けられている場合
- 定数に対応する計算処理やアルゴリズムが必要な場合
- オブジェクト指向的な設計でストラテジーパターンを簡潔に実現したい場合

<span class="listing-number">**サンプルコード8-5**</span>

```java
public enum Planet {
    // 各列挙子を定義する際に、コンストラクタの引数を渡す
    MERCURY (3.303e+23, 2.4397e6),
    VENUS   (4.869e+24, 6.0518e6),
    EARTH   (5.976e+24, 6.37814e6);
    // ... 他の惑星

    private final double mass;   // 質量 (kg)
    private final double radius; // 半径 (m)

    // コンストラクタは暗黙的にprivate
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }

    // 通常のメソッド
    public double surfaceGravity() {
        final double G = 6.67300E-11;
        return G * mass / (radius * radius);
    }
}

public class PlanetTest {
    public static void main(String[] args) {
        System.out.println("地球の表面重力: " + Planet.EARTH.surfaceGravity());
    }
}
```

#### 重要なポイント

1. コンストラクタの役割： enumのコンストラクタは暗黙的に`private`になる。これにより、外部からインスタンスを作成することはできず、定義された列挙子のみが存在することが保証される。

2. データの不変性： `final`修飾子を使ったフィールドにより、各惑星の質量や半径は変更不可能な値として保持される。これにより、データの整合性が保たれる。

3. 計算ロジックの一元化： 表面重力の計算など、各惑星に共通する処理をenumのメソッドとして定義することで、計算ロジックを一箇所にまとめられる。

4. タイプセーフティ： 従来の定数クラスと比較して、コンパイル時に型の安全性が保証される。存在しない惑星を指定してしまうようなミスを防げる。

このような設計パターンは、ゲーム開発におけるキャラクタークラスの定義、Webアプリケーションにおけるステータス管理、設定管理システムなど、さまざまな実用的な場面で活用できます。

## さらに高度な使い方

### 列挙子ごとのメソッド実装

各列挙子でメソッドをオーバーライドすることで、列挙子ごとに異なる振る舞いをさせることができます。これはストラテジーパターンを簡潔に実現する方法の1つです。

<span class="listing-number">**サンプルコード8-6**</span>

```java
public enum Operation {
    PLUS("+") {
        public double apply(double x, double y) { return x + y; }
    },
    MINUS("-") {
        public double apply(double x, double y) { return x - y; }
    };

    private final String symbol;
    Operation(String symbol) { this.symbol = symbol; }
    public abstract double apply(double x, double y); // 抽象メソッド
}
```
`op.apply(x, y)`という同じ呼び出し方で、`PLUS`なら足し算、`MINUS`なら引き算が実行されます。

### インターフェイスの実装

`enum`はクラスを継承できませんが、インターフェイスを実装することは可能です。

<span class="listing-number">**サンプルコード8-7**</span>

```java
interface Loggable {
    String getLogMessage();
}

public enum Status implements Loggable {
    SUCCESS {
        public String getLogMessage() { return "処理が成功しました。"; }
    },
    ERROR {
        public String getLogMessage() { return "エラーが発生しました。"; }
    };
}
```

※ 本章の高度な内容については、付録B.11「Enumの高度な活用」を参照してください。
（`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b11-enum-patterns/`）

## まとめ

本章では、型安全な定数を扱うための`enum`について学びました。

- `enum`は、取りうる値が限定された、独自の型を定義する機能である
- `int`定数などと比べて、型安全で、コードが読みやすくなる
- `switch`文と組み合わせることで、網羅的で安全な条件分岐を記述できる
- クラスのように、フィールドやメソッドを持たせることができ、より高機能な定数を定義できる
- 列挙子ごとにメソッドを実装したり、インターフェイスを実装したりすることで、さらに柔軟な設計が可能である

プログラムのなかで「種類」「状態」「ステータス」などを表す値が出てきたら、まず`enum`が使えないか検討することは、高品質なコードを書くためのよい習慣です。

## enumを使ったSingletonパターン

### Singletonパターンとは

Singletonパターンは、クラスのインスタンスがシステム全体で1つだけ存在することを保証するデザインパターンです。データベース接続、設定管理、ログ管理など、システム全体で共有されるリソースの管理によく使用されます。

### 従来のSingleton実装の問題点

第3章で触れたstaticを使った従来のSingleton実装には、いくつかの問題があります。

<span class="listing-number">**サンプルコード8-8**</span>

```java
// 従来のSingleton実装（問題がある）
public class DatabaseConnection {
    private static DatabaseConnection instance;
    
    private DatabaseConnection() {
        // プライベートコンストラクタ
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();  // スレッドセーフではない
        }
        return instance;
    }
}
```

#### この実装の問題点
- スレッドセーフティ： 複数のスレッドが同時にgetInstance()を呼ぶと、複数のインスタンスが作られる可能性がある
- シリアライゼーション： デシリアライズ時に新しいインスタンスが作られてしまう
- リフレクション攻撃： リフレクションAPIを使って複数のインスタンスを作成できる

### enumを使った最適なSingleton実装

Javaでは、enumを使うことでもっともシンプルかつ安全なSingletonを実装できます。

<span class="listing-number">**サンプルコード8-9**</span>

```java
import java.sql.Connection;

public enum DatabaseConnectionManager {
    INSTANCE;
    
    private Connection connection;
    
    // enumのコンストラクタは暗黙的にprivate
    DatabaseConnectionManager() {
        // 初期化処理
        initializeConnection();
    }
    
    private void initializeConnection() {
        // データベース接続の初期化
        System.out.println("データベース接続を初期化しています...");
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    public void executeQuery(String sql) {
        System.out.println("クエリを実行: " + sql);
    }
}

// 使用例
public class Application {
    public static void main(String[] args) {
        // どこからアクセスしても同じインスタンス
        DatabaseConnectionManager.INSTANCE.executeQuery("SELECT * FROM users");
        
        // 別の場所から
        DatabaseConnectionManager dbManager = DatabaseConnectionManager.INSTANCE;
        dbManager.executeQuery("UPDATE users SET active = true");
    }
}
```

### enumによるSingletonの利点

1. スレッドセーフ： JVMがenum定数の初期化を保証するため、自動的にスレッドセーフ
2. シリアライゼーション対応： enumは自動的に適切にシリアライズ・デシリアライズされる
3. リフレクション攻撃への耐性： enumのインスタンス化はJVMレベルで制限される
4. シンプルな実装： 複雑な同期処理やダブルチェックロッキングが不要

### 実践的な例：アプリケーション設定管理

<span class="listing-number">**サンプルコード8-10**</span>

```java
import java.util.Properties;
import java.io.IOException;

public enum ConfigurationManager {
    INSTANCE;
    
    private final Properties properties = new Properties();
    
    ConfigurationManager() {
        loadConfiguration();
    }
    
    private void loadConfiguration() {
        try {
            properties.load(getClass().getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            throw new RuntimeException("設定ファイルの読み込みに失敗しました", e);
        }
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }
}

// 使用例
public class App {
    public static void main(String[] args) {
        // アプリケーションのどこからでも同じ設定にアクセス
        String dbUrl = ConfigurationManager.INSTANCE.getProperty("database.url");
        int maxConnections = ConfigurationManager.INSTANCE.getIntProperty("database.maxConnections", 10);
    }
}
```

このように、enumを使ったSingletonパターンは、Javaにおけるもっとも推奨される実装方法です。

## よくあるエラーと対処法

enumを学習する際に遭遇する典型的なエラーとその対処法を示します。

### enum定数の不正な使用

#### エラー例1: enum定数の大文字小文字を間違える

<span class="listing-number">**サンプルコード8-11**</span>

```java
public enum Status {
    ACTIVE, INACTIVE, PENDING
}

// 間違った使用
if (status == Status.active) {  // コンパイルエラー
    // 処理
}
```

##### エラーメッセージ
```
error: cannot find symbol
  symbol:   variable active
  location: class Status
```

##### 対処法

<span class="listing-number">**サンプルコード8-12**</span>

```java
// 正しい使用
if (status == Status.ACTIVE) {
    // 処理
}
```

#### エラー例2: enum定数をnewで作成しようとする

<span class="listing-number">**サンプルコード8-13**</span>

```java
Status status = new Status();  // コンパイルエラー
```

##### エラーメッセージ
```
error: enum types may not be instantiated
```

##### 対処法

<span class="listing-number">**サンプルコード8-14**</span>

```java
// 正しい使用
Status status = Status.ACTIVE;
```

### switchステートメントでのenum取り扱い

#### エラー例3: switch文でenum名を含めてしまう

<span class="listing-number">**サンプルコード8-15**</span>

```java
switch (status) {
    case Status.ACTIVE:  // コンパイルエラー
        break;
    case Status.INACTIVE:
        break;
}
```

##### エラーメッセージ
```
error: an enum switch case label must be the unqualified name of an enumeration constant
```

##### 対処法

<span class="listing-number">**サンプルコード8-16**</span>

```java
// 正しい使用
switch (status) {
    case ACTIVE:
        break;
    case INACTIVE:
        break;
}
```

#### エラー例4: switch文でのdefault句の省略

<span class="listing-number">**サンプルコード8-17**</span>

```java
public enum Priority {
    HIGH, MEDIUM, LOW
}

// 全てのcase句を書いていない場合
switch (priority) {
    case HIGH:
        // 処理
        break;
    case MEDIUM:
        // 処理
        break;
    // LOWのcase句がない
}
```

##### 問題点
新しいenum値が追加されたときに、コンパイル時に検出されない可能性があります。

##### 対処法

<span class="listing-number">**サンプルコード8-18**</span>

```java
// 推奨：全てのcase句を書く、またはdefaultを使用
switch (priority) {
    case HIGH:
        // 処理
        break;
    case MEDIUM:
        // 処理
        break;
    case LOW:
        // 処理
        break;
    default:
        throw new IllegalArgumentException("Unknown priority: " + priority);
}
```

### enumのコンストラクタ呼び出し

#### エラー例5: enumのコンストラクタを直接呼び出す

<span class="listing-number">**サンプルコード8-19**</span>

```java
public enum Color {
    RED(255, 0, 0),
    GREEN(0, 255, 0),
    BLUE(0, 0, 255);
    
    private final int r, g, b;
    
    Color(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }
}

// 間違った使用
Color color = new Color(128, 128, 128);  // コンパイルエラー
```

##### エラーメッセージ
```
error: enum types may not be instantiated
```

##### 対処法

<span class="listing-number">**サンプルコード8-20**</span>

```java
// 正しい使用：定義済みの定数のみ使用可能
Color color = Color.RED;

// 新しい色が必要な場合は、enum定義に追加
public enum Color {
    RED(255, 0, 0),
    GREEN(0, 255, 0),
    BLUE(0, 0, 255),
    GRAY(128, 128, 128);  // 新しい定数を追加
    
    // ... コンストラクタとメソッド
}
```

### Comparableインターフェイスとの関係

#### エラー例6: enumの比較で間違った方法を使用

<span class="listing-number">**サンプルコード8-21**</span>

```java
public enum Size {
    SMALL, MEDIUM, LARGE
}

// 間違った使用
if (size1 > size2) {  // コンパイルエラー
    // 処理
}
```

##### エラーメッセージ
```
error: bad operand types for binary operator '>'
```

##### 対処法

<span class="listing-number">**サンプルコード8-22**</span>

```java
// 正しい使用：ordinal()の比較またはcompareTo()を使用
if (size1.ordinal() > size2.ordinal()) {
    // 処理
}

// より良い方法：compareTo()を使用
if (size1.compareTo(size2) > 0) {
    // 処理
}
```

### enumの拡張に関する誤解

#### エラー例7: enumを継承しようとする

<span class="listing-number">**サンプルコード8-23**</span>

```java
public enum BasicColor {
    RED, GREEN, BLUE
}

// 間違った使用
public enum ExtendedColor extends BasicColor {  // コンパイルエラー
    YELLOW, CYAN, MAGENTA
}
```

##### エラーメッセージ
```
error: enum cannot inherit from classes
```

##### 対処法1: インターフェイスを使用

<span class="listing-number">**サンプルコード8-24**</span>

```java
public interface ColorInterface {
    String getHexValue();
}

public enum BasicColor implements ColorInterface {
    RED("#FF0000"),
    GREEN("#00FF00"),
    BLUE("#0000FF");
    
    private final String hexValue;
    
    BasicColor(String hexValue) {
        this.hexValue = hexValue;
    }
    
    @Override
    public String getHexValue() {
        return hexValue;
    }
}

public enum ExtendedColor implements ColorInterface {
    YELLOW("#FFFF00"),
    CYAN("#00FFFF"),
    MAGENTA("#FF00FF");
    
    private final String hexValue;
    
    ExtendedColor(String hexValue) {
        this.hexValue = hexValue;
    }
    
    @Override
    public String getHexValue() {
        return hexValue;
    }
}
```

##### 対処法2: 抽象メソッドを使用して多態性を実現

<span class="listing-number">**サンプルコード8-25**</span>

```java
public enum Operation {
    PLUS {
        @Override
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS {
        @Override
        public double apply(double x, double y) {
            return x - y;
        }
    };
    
    public abstract double apply(double x, double y);
}
```

### 実践的なデバッグのヒント

#### デバッグ時の確認ポイント

1. null値の確認

<span class="listing-number">**サンプルコード8-26**</span>

```java
// enumはnullになる可能性がある
if (status != null && status == Status.ACTIVE) {
    // 処理
}
```

2. 文字列からenumへの変換

<span class="listing-number">**サンプルコード8-27**</span>

```java
// 安全な変換
public static Status fromString(String value) {
    try {
        return Status.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
        return null;  // またはデフォルト値
    }
}
```

3. enumの順序に依存しない設計

<span class="listing-number">**サンプルコード8-28**</span>

```java
// 悪い例：ordinal()に依存
int level = priority.ordinal();  // 順序が変わると破綻

// 良い例：明示的な値を持つ
public enum Priority {
    HIGH(1), MEDIUM(2), LOW(3);
    
    private final int level;
    
    Priority(int level) {
        this.level = level;
    }
    
    public int getLevel() {
        return level;
    }
}
```

これらのエラーパターンを理解することで、enumをより安全かつ効果的に使用できるようになります。