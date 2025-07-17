# <b>8章</b> <span>列挙型(Enums)</span> <small>型安全な定数と状態管理の技術</small>

## 本章の学習目標

### 前提知識

#### ポイントとなる前提
- 第4章のクラスとインスタンス（コンストラクタ、メソッド、フィールド）
- 第7章の抽象クラスとインターフェイス
- 第5章のequals、hashCode、toString メソッド

#### あるとよい前提
- 第9章のRecordの概念（不変データ構造）
- オブジェクト指向設計におけるカプセル化の原則
- デザインパターンの基礎知識（Strategy、State パターン）
- switch文の基本的な使い方

### 学習目標

#### 知識理解目標
- Enumの設計思想と型安全性の保証方法
- 定数とEnumの違いと適切な使い分け
- Enumの内部実装とコンパイラによる自動生成機能
- EnumSet、EnumMapの利点と性能特性

#### 技能習得目標
- 基本的なEnum定義と標準メソッドの活用
- コンストラクタ、フィールド、メソッドを持つEnumの実装
- switch文とEnum の組み合わせによる条件分岐
- EnumによるStateパターンやStrategyパターンの実装

#### 実践的な活用目標
- ビジネスロジックにおける状態管理の設計
- 設定値や分類コードの型安全な表現
- EnumSetやEnumMapを活用した高性能なデータ処理
- enumベースのAPIとDSLの設計

#### 到達レベルの指標
- 適切な場面でEnum を選択し効果的に活用できる
- 複雑な状態遷移をEnum で表現できる
- Enum の性能特性を理解し最適な設計ができる
- 保守性の高いEnum ベースのアーキテクチャを構築できる

## 章末演習

本章で学んだ列挙型(Enums)を実践的な課題で確認しましょう。

### 演習課題へのアクセス

本書の演習課題は、以下のGitHubリポジトリで提供されています：

リポジトリ: `https://github.com/Nagatani/techbook-java-primer/tree/main/exercises`

### 第8章の課題構成

```
exercises/chapter08/
├── basic/              # 基礎課題（推奨）
│   ├── README.md       # 詳細な課題説明
│   └── DayOfWeek.java
├── advanced/           # 発展課題（推奨）
├── challenge/          # チャレンジ課題（任意）
└── solutions/          # 解答例（実装後に参照）
```

### 学習の目標

本章の演習を通じて以下のスキルを習得します：
- Enumによる型安全な定数の定義と活用
- Enumへのメソッドとフィールドの追加
- 高度なEnumパターンの実装

### 課題の概要

1. **基礎課題**: 基本的なEnum定義と標準メソッドの活用
2. **発展課題**: メソッドを持つEnumとステートマシンの実装
3. **チャレンジ課題**: EnumSetやEnumMapを活用した高度な設計

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

次のステップ: 基礎課題が完了したら、第14章「例外処理の基礎と応用」に進みましょう。





## 完了確認チェックリスト

### 基礎レベル
- 基本的なEnumの定義と使用ができている
- 状態管理パターンが実装できている
- 関数型インターフェイスとの組み合わせができている
- 複雑なEnum設計システムが構築できている

### 技術要素
- Enumの利点と制限を理解している
- 適切な設計パターンでEnumを活用できている
- Switch文での網羅性チェックを活用できている
- ビジネスロジックをEnum内に適切にカプセル化できている

### 応用レベル
- EnumSet、EnumMapを効果的に活用できている
- 抽象メソッドとポリモーフィズムを活用できている
- パフォーマンスを考慮したEnum設計ができている
- レガシーコードからEnumへのリファクタリングができている

## 本章の学習目標

### 前提知識

本章を学習するためには、第6章で学んだfinalキーワードの理解とクラス設計の経験がポイントとなります。特に、不変性の概念と、なぜ値を変更できないことが設計上有益なのかを理解していることが大切です。また、定数の概念と`static final`フィールドを使った基本的な定数定義の経験があることで、enumがなぜ役立つのか、従来の定数定義の何が問題なのかを実感を持って理解できます。オブジェクト指向の基本概念、特にクラスとインスタンスの関係、静的メンバーと非静的メンバーの違いについても理解していることがよいでしょう。

設計経験の面では、実際に定数管理で苦労した経験があると本章の内容がより深く理解できます。たとえば、整数定数を使った状態管理で、無効な値が設定されてバグが発生した経験や、定数の意味が分かりにくくてコードの可読性が低下した経験などがあれば、enumの価値を実感できるでしょう。また、switch文やif文を使った分岐処理の基本的な経験があることで、enumとの組み合わせによる改善効果を理解しやすくなります。

### 学習目標

本章では、Java特有の強力な機能であるenum（列挙型）について学習します。知識理解の面では、まずenumの設計思想と、従来の`static final`定数との根本的な違いを理解します。enumは単なる定数の集合ではなく、特殊な形式のクラスであり、型安全性を保証するしくみです。この設計により、コンパイル時に不正な値の使用を防ぎ、実行時エラーを大幅に削減できます。

enumのインスタンス性とsingleton性の理解も大切なポイントです。各enum定数は、そのenum型の唯一のインスタンスとして存在し、JVMによって管理されます。この特性により、`==`演算子での比較が可能になり、switch文での効率的な分岐処理が実現されています。また、values()やvalueOf()といったenum特有のメソッドがどのように動作し、どのような場面で活用できるのかを理解します。

技能習得の観点では、基本的なenumの定義から始め、フィールドやメソッドを持つ高度なenum設計まで段階的に習得します。単純な定数の列挙だけでなく、各定数に関連するデータや振る舞いを持たせることで、より表現力豊かな設計が可能になります。たとえば、曜日のenumに営業日かどうかを判定するメソッドを追加したり、HTTPステータスコードのenumに説明文を持たせたりといった実践的な設計パターンを学びます。

設計能力の面では、適切な抽象化レベルでenumを活用する判断力を養います。すべての定数をenumにすれば良いわけではなく、値の集合が明確に定義でき、将来的にも変更が少ない場合に適用するとよいでしょう。また、Strategy PatternやState Patternといったデザインパターンとenumを組み合わせることで、より拡張可能で保守性の高い設計を実現する方法も学びます。ドメイン駆動設計（DDD）におけるValue Objectとしてのenum活用など、実務で求められる高度な設計技法も習得します。

最終的な到達レベルとして、型安全性を最大限に活用したenum設計ができるようになることを目指します。複雑なビジネスロジックをenumで表現し、コードの意図を明確に伝えられます。また、既存のレガシーコードに含まれる整数定数や文字列定数を、適切にenumへリファクタリングする能力も身につけます。これにより、保守性と可読性が大幅に向上したコードベースを構築できます。



## 8.1 enumとは？

`enum`（列挙型）は、**決まったいくつかの値だけを取りうる型**を定義するための、特殊なクラスです。たとえば、「曜日（月火水木金土日）」や「信号の色（赤青黄）」のように、値の範囲が限定されているものを扱うのに最適です。

### なぜenumが必要なのか？

`enum`がない場合、このような定数は`public static final int`フィールドとして定義することが一般的でした。しかし、この方法には以下のような問題点があります。
-   **型安全でない**: `int`型ですので、`setSignal(100)`のような無効な値も代入できてしまう。
-   **意味が不明確**: `int`という型からは、それが「信号の色」を表すという情報がわからない。
-   **デバッグしにくい**: 変数の値を表示しても`0`としか出力されず、それが「赤」を意味するのかわからない。

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

## 8.2 enumの便利な機能

Javaの`enum`は、暗黙的に`java.lang.Enum`クラスを継承しており、便利なメソッドが標準で提供されています。

-   `values()`: すべての列挙子を定義順に格納した配列を返します。
-   `valueOf(String name)`: 指定された名前の列挙子を返します。
-   `name()`: 列挙子の名前（定義した通りの文字列）を返します。
-   `ordinal()`: 列挙子の定義順序（ゼロから始まる）を返します。

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

`enum`は`switch`文と非常に相性が良く、安全で読みやすいコードを書くことができます。

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

## 8.3 フィールドとメソッドを持つenum

`enum`は単なる定数の集まりではなく、クラスのようにフィールドやコンストラクタ、メソッドを持たせることができます。これにより、定数に関連するデータや振る舞いをカプセル化できます。

例えば、太陽系の惑星を表現する場合を考えてみましょう。単純に`MERCURY`、`VENUS`、`EARTH`といった定数を定義するだけでなく、各惑星の質量や半径といった物理的な属性、さらにはそれらを使った計算（表面重力など）も含めることができます。このようなリッチなenumは、科学計算や物理シミュレーションなどの分野で特に有効です。

**使用場面:**
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

**大切なポイント:**

1. **コンストラクタの役割**: enumのコンストラクタは暗黙的に`private`になります。これにより、外部からインスタンスを作成することはできず、定義された列挙子のみが存在することが保証されます。

2. **データの不変性**: `final`修飾子を使ったフィールドにより、各惑星の質量や半径は変更不可能な値として保持されます。これにより、データの整合性が保たれます。

3. **計算ロジックの一元化**: 表面重力の計算など、各惑星に共通する処理をenumのメソッドとして定義することで、計算ロジックを一箇所にまとめられます。

4. **タイプセーフティ**: 従来の定数クラスと比較して、コンパイル時に型の安全性が保証されます。存在しない惑星を指定してしまうようなミスを防げます。

このような設計パターンは、ゲーム開発におけるキャラクタークラスの定義、Webアプリケーションにおけるステータス管理、設定管理システムなど、様々な実用的な場面で活用できます。

## 8.4 さらに高度な使い方

### 列挙子ごとのメソッド実装

各列挙子でメソッドをオーバーライドすることで、列挙子ごとに異なる振る舞いをさせることができます。これは**ストラテジーパターン**を簡潔に実現する方法の1つです。

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

## より深い理解のために

本章で学んだEnumsをさらに深く理解したい方は、GitHubリポジトリの付録資料を参照してください：

付録リソース: `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b13-enum-patterns/`

この付録では以下の高度なトピックを扱います：

- 状態機械の実装: 複雑なワークフローエンジンやタスク管理システムの実装
- 戦略パターンとEnum: 計算戦略やプロトコル実装での活用
- EnumSetとEnumMapの活用: 権限管理システムや設定管理での高速処理
- パフォーマンス最適化: EnumSetの内部実装とビット演算による高速化
- 実践的な応用例: イベント駆動システムでの型安全なイベント定義

これらの技術は、ドメイン駆動設計や複雑なビジネスロジックの実装において大切な役割を果たします。

## まとめ

本章では、型安全な定数を扱うための`enum`について学びました。

-   **`enum`**は、取りうる値が限定された、独自の型を定義する機能です。
-   `int`定数などと比べて、**型安全**で、コードが**読みやすく**なります。
-   `switch`文と組み合わせることで、網羅的で安全な条件分岐を記述できます。
-   クラスのように、**フィールドやメソッドを持たせる**ことができ、より高機能な定数を定義できます。
-   列挙子ごとにメソッドを実装したり、インターフェイスを実装したりすることで、さらに柔軟な設計が可能です。

プログラムの中で「種類」「状態」「ステータス」などを表す値が出てきたら、まず`enum`が使えないか検討することは、高品質なコードを書くための良い習慣です。

## 8.5 enumを使ったSingletonパターン

### Singletonパターンとは

Singletonパターンは、クラスのインスタンスがシステム全体で1つだけ存在することを保証するデザインパターンです。データベース接続、設定管理、ログ管理など、システム全体で共有されるリソースの管理によく使用されます。

### 従来のSingleton実装の問題点

第3章で触れたstaticを使った従来のSingleton実装には、いくつかの問題があります：

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

この実装の問題点：
- スレッドセーフティ: 複数のスレッドが同時にgetInstance()を呼ぶと、複数のインスタンスが作られる可能性がある
- シリアライゼーション: デシリアライズ時に新しいインスタンスが作られてしまう
- リフレクション攻撃: リフレクションAPIを使って複数のインスタンスを作成できる

### enumを使った最適なSingleton実装

Javaでは、enumを使うことで最もシンプルかつ安全なSingletonを実装できます：

<span class="listing-number">**サンプルコード8-9**</span>

```java
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

1. **スレッドセーフ**: JVMがenum定数の初期化を保証するため、自動的にスレッドセーフ
2. **シリアライゼーション対応**: enumは自動的に適切にシリアライズ・デシリアライズされる
3. **リフレクション攻撃への耐性**: enumのインスタンス化はJVMレベルで制限される
4. **シンプルな実装**: 複雑な同期処理やダブルチェックロッキングが不要

### 実践的な例：アプリケーション設定管理

<span class="listing-number">**サンプルコード8-10**</span>

```java
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

このように、enumを使ったSingletonパターンは、Javaにおける最も推奨される実装方法です。