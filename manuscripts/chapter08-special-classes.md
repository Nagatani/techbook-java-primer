# <b>8章</b> <span>Javaの特殊なクラス形式</span> <small>列挙型・レコード・内部クラスの使いどころ</small>

## 本章の学習目標

### この章で学ぶこと

本章では、Javaが提供する特殊なクラス形式について学習します。
具体的には以下の内容を扱います。

1. 列挙型（Enum）の理解と活用
    - 型安全な定数定義の実現方法
    - 高度なEnum機能とデザインパターンへの応用
2. レコード（Record）の理解と活用
    - 不変データクラスの簡潔な定義方法
    - パターンマッチングとの統合による高度な活用
3. 両者の使い分けと実践的な設計
    - それぞれの特徴を活かした設計上の選択
    - 実際のアプリケーションでの組み合わせ方
4. 内部クラスの基本
    - メンバー内部クラス、静的内部クラス、ローカル内部クラス、匿名内部クラスの違い

### この章を始める前に

第6章のfinalキーワードとstatic final定数、第7章のインターフェイスを理解していれば準備完了です。
これらの知識により、従来の定数定義やデータクラスの問題点と、EnumやRecordがもたらす改善を実感できます。

## 列挙型（Enum）

### enumとは？

`enum`（列挙型）は、決まったいくつかの値だけを取りうる型を定義するための、特殊なクラスです<span class="footnote">enumはJava 5（2004年）で導入されました。C言語のenumは整数に名前を付けるだけで`int`の値と混ぜて使えますが、Javaのenumはクラスなので、ほかの型の値と取り違えることはありません。</span>。たとえば、「曜日（月、火、水、木、金、土、日）」や「信号の色（赤、青、黄）」のように、値の範囲が限定されているものを扱うのに最適です。

### なぜenumが必要なのか？

`enum`がない場合、このような定数は`public static final int`フィールドとして定義することが一般的でした。しかし、この方法には問題があります。
まず、値の型はただの`int`なので、`setSignal(100)`のような無効な値も代入できてしまいます。
また、`int`という型からは、その値が「信号の色」を表すことが読み取れません。
さらに、変数の値を表示しても`0`としか出力されず、それが「赤」を意味するのかわかりません。

`enum`は、これらの問題をすべて解決します。`enum`で定義された各要素（列挙子）は、その`enum`型のユニークなインスタンスとして扱われ、型安全性を保証します。

### 基本的な構文と使い方

以下のコードは、曜日を表す列挙型の最も基本的な定義例です。enumキーワードを使って7つの曜日を定義することで、DayOfWeek型はこれら7つの値のみを取ることができる型安全な定数となります。各列挙子（SUNDAY、MONDAYなど）は自動的にDayOfWeek型の唯一のインスタンスとして生成されます。

<span class="listing-number">**サンプルコード8-1**　DayOfWeek.java</span>

```java
public enum DayOfWeek {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
}
```

これだけで、`DayOfWeek`という新しい型が作られ、その型が取りうる値は定義された7つの曜日に限定されます。

<span class="listing-number">**サンプルコード8-2**　EnumExample.java</span>

```java
public class EnumExample {
    public static void main(String[] args) {
        DayOfWeek today = DayOfWeek.WEDNESDAY;
        
        IO.println("今日は " + today + " です。");
        // toString()がオーバーライドされており、"WEDNESDAY"と表示される
        
        // 型安全なので、不正な値はコンパイルエラーになる
        // DayOfWeek tomorrow = "Thursday"; // エラー
    }
}
```

実行結果：

```
今日は WEDNESDAY です。
```

### enumの便利な機能

Javaの`enum`は、暗黙的に`java.lang.Enum`クラスを継承しており、便利なメソッドが標準で提供されています<span class="footnote">このうち`values()`と`valueOf(String)`は`Enum`クラスにはなく、コンパイラーがenumごとに自動で追加する静的メソッドです。</span>。

- `values()`
    + すべての列挙子を定義順に格納した配列を返す
- `valueOf(String name)`
    + 指定された名前の列挙子を返す
- `name()`
    + 列挙子の名前（定義した通りの文字列）を返す
- `ordinal()`
    + 列挙子の定義順序（ゼロから始まる）を返す

<span class="listing-number">**サンプルコード8-3**　EnumMethodsExample.java</span>

```java
public class EnumMethodsExample {
    public static void main(String[] args) {
        // すべての曜日を出力
        for (DayOfWeek day : DayOfWeek.values()) {
            IO.println(day.ordinal() + ": " + day.name());
        }
        
        // 文字列からenumへの変換
        DayOfWeek friday = DayOfWeek.valueOf("FRIDAY");
        IO.println("金曜日: " + friday);
    }
}
```

実行結果：

```
0: SUNDAY
1: MONDAY
2: TUESDAY
3: WEDNESDAY
4: THURSDAY
5: FRIDAY
6: SATURDAY
金曜日: FRIDAY
```

### フィールドとメソッドを持つenum

`enum`は単なる定数の集合ではなく、フィールドやメソッドを持つことができます。これにより、各列挙子に関連するデータや振る舞いを定義できます。

<div class="figure">

![列挙子はそれぞれ1つだけ作られるインスタンス（サンプルコード8-4）](images/diagrams/chapter08-enum-instances.svg)

</div>

<span class="listing-number">**サンプルコード8-4**　Planet.java</span>

```java
public enum Planet {
    MERCURY(3.303e+23, 2.4397e6),
    VENUS(4.869e+24, 6.0518e6),
    EARTH(5.976e+24, 6.37814e6),
    MARS(6.421e+23, 3.3972e6);
    
    private final double mass;   // 質量（kg）
    private final double radius; // 半径（m）
    
    // コンストラクタ（暗黙的にprivate）
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }
    
    public double getMass() {
        return mass;
    }
    
    public double getRadius() {
        return radius;
    }
    
    // 表面重力を計算
    public double surfaceGravity() {
        double G = 6.67300E-11; // 万有引力定数
        return G * mass / (radius * radius);
    }
}
```

### switch文とenumの組み合わせ

`enum`と`switch`文は非常に相性がよく、読みやすいコードを書くことができます。

<span class="listing-number">**サンプルコード8-5**　TrafficLight.java</span>

```java
public class TrafficLight {
    public enum Color {
        RED, YELLOW, GREEN
    }
    
    public void handleSignal(Color color) {
        switch (color) {
            case RED:
                IO.println("停止してください");
                break;
            case YELLOW:
                IO.println("注意して進んでください");
                break;
            case GREEN:
                IO.println("進んでください");
                break;
        }
    }
}
```

実行結果：

```
信号のテスト:
停止してください
注意して進んでください
進んでください
```

この実行結果は、GitHubの全文に付けた`TrafficLightDemo.java`<span class="footnote">サンプルコード8-5の全文は`https://github.com/Nagatani/techbook-java-primer/tree/main/sample-code/chapter08/08-05/`にあります。</span>で、すべての色について`handleSignal`を呼んだときのものです。

### 列挙子ごとのメソッド実装

各列挙子でメソッドをオーバーライドすることで、列挙子ごとに異なる振る舞いをさせることができます。

<span class="listing-number">**サンプルコード8-6**　Operation.java</span>

```java
public enum Operation {
    PLUS("+") {
        public double apply(double x, double y) { return x + y; }
    },
    MINUS("-") {
        public double apply(double x, double y) { return x - y; }
    },
    TIMES("*") {
        public double apply(double x, double y) { return x * y; }
    },
    DIVIDE("/") {
        public double apply(double x, double y) { return x / y; }
    };

    private final String symbol;
    
    Operation(String symbol) {
        this.symbol = symbol;
    }
    
    public abstract double apply(double x, double y);
    
    @Override
    public String toString() {
        return symbol;
    }
}
```

実行結果：

```
x = 6.0, y = 2.0
6.000000 + 2.000000 = 8.000000
6.000000 - 2.000000 = 4.000000
6.000000 * 2.000000 = 12.000000
6.000000 / 2.000000 = 3.000000
```

この実行結果は、GitHubの全文に付けた`OperationDemo.java`<span class="footnote">サンプルコード8-6の全文は`https://github.com/Nagatani/techbook-java-primer/tree/main/sample-code/chapter08/08-06/`にあります。</span>で、6.0と2.0に4つの演算を適用したときのものです。

### enumを使ったデザインパターン

#### Singletonパターン

`enum`を使うと、最も簡潔で安全なシングルトンを実装できます<span class="footnote">enumによるシングルトンは、ジョシュア・ブロックの『Effective Java』（第2版、2008年）で勧められたことで広く知られるようになりました。</span>。
次のサンプルの`Connection`は、データベースとの接続を表す`java.sql`パッケージのインターフェイスです。
ここでは接続の処理そのものは扱わず、列挙子`INSTANCE`がただ1つのインスタンスとして接続を持ち続ける形に注目してください。

<span class="listing-number">**サンプルコード8-7**　DatabaseConnection.java</span>

```java
import java.sql.Connection;

public enum DatabaseConnection {
    INSTANCE;
    
    private Connection connection;
    
    DatabaseConnection() {
        // データベース接続の初期化
        this.connection = initializeConnection();
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    private Connection initializeConnection() {
        // 接続の初期化ロジック
        return null; // 実際の実装では適切な接続を返す
    }
}
```

#### Strategyパターン

列挙子ごとに異なる処理（戦略）を持たせると、Strategyパターンになります。

<span class="listing-number">**サンプルコード8-8**　PaymentStrategy.java</span>

```java
public enum PaymentStrategy {
    CREDIT_CARD {
        @Override
        public void pay(double amount) {
            IO.println("クレジットカードで" + amount + "円を支払いました");
        }
    },
    BANK_TRANSFER {
        @Override
        public void pay(double amount) {
            IO.println("銀行振込で" + amount + "円を支払いました");
        }
    },
    CASH {
        @Override
        public void pay(double amount) {
            IO.println("現金で" + amount + "円を支払いました");
        }
    };
    
    public abstract void pay(double amount);
}
```

実行結果：

```
支払い方法のテスト:
クレジットカードで1500.0円を支払いました
銀行振込で1500.0円を支払いました
現金で1500.0円を支払いました
```

この実行結果は、GitHubの全文に付けた`PaymentStrategyDemo.java`<span class="footnote">サンプルコード8-8の全文は`https://github.com/Nagatani/techbook-java-primer/tree/main/sample-code/chapter08/08-08/`にあります。</span>で、すべての支払い方法で1500円を支払ったときのものです。

### インターフェイスの実装

`enum`はインターフェイスを実装することもできます。

<span class="listing-number">**サンプルコード8-9**　Printable.java、DocumentType.java</span>

```java
public interface Printable {
    void print();
}

public enum DocumentType implements Printable {
    PDF {
        @Override
        public void print() {
            IO.println("PDFドキュメントを印刷します");
        }
    },
    WORD {
        @Override
        public void print() {
            IO.println("Wordドキュメントを印刷します");
        }
    },
    TEXT {
        @Override
        public void print() {
            IO.println("テキストファイルを印刷します");
        }
    }
}
```

### EnumSetとEnumMap

Java標準ライブラリには、`enum`専用の高速なコレクションクラスが用意されています<span class="footnote">`EnumSet`は内部で、各列挙子を1ビットに対応させたビットの並びとして集合を表します。C言語で`READ | WRITE`のようにビットフラグを組み合わせる書き方を、型安全にしたものと考えられます。</span>。
コレクション（`Set`や`Map`）は第9章で学びます。
ここでは、`EnumSet`は列挙子の集まり、`EnumMap`は列挙子ごとに値を対応づける表、と読んでください。
`EnumSet<Permission>`の`<Permission>`は、入れる要素の型を表します。

<span class="listing-number">**サンプルコード8-10**　EnumCollectionExample.java</span>

```java
import java.util.EnumSet;
import java.util.EnumMap;

public class EnumCollectionExample {
    public enum Permission {
        READ, WRITE, EXECUTE, DELETE
    }
    
    public static void main(String[] args) {
        // EnumSet: enum専用の高速なSet実装
        EnumSet<Permission> adminPermissions = EnumSet.allOf(Permission.class);
        EnumSet<Permission> userPermissions = EnumSet.of(Permission.READ, Permission.WRITE);
        
        IO.println("管理者権限: " + adminPermissions);
        IO.println("ユーザー権限: " + userPermissions);
        
        // EnumMap: enumをキーとする高速なMap実装
        EnumMap<Permission, String> permissionDescriptions = new EnumMap<>(Permission.class);
        permissionDescriptions.put(Permission.READ, "読み取り権限");
        permissionDescriptions.put(Permission.WRITE, "書き込み権限");
        permissionDescriptions.put(Permission.EXECUTE, "実行権限");
        permissionDescriptions.put(Permission.DELETE, "削除権限");
        
        for (Permission p : Permission.values()) {
            IO.println(p + ": " + permissionDescriptions.get(p));
        }
    }
}
```

実行結果：

```
管理者権限: [READ, WRITE, EXECUTE, DELETE]
ユーザー権限: [READ, WRITE]
READ: 読み取り権限
WRITE: 書き込み権限
EXECUTE: 実行権限
DELETE: 削除権限
```

### enumのベストプラクティス

名前の付け方には、次の慣習があります。

- enum型名
    + 単数形の名詞（`Status`、`Color`など）
- 列挙子名
    + すべて大文字でアンダースコア区切り（`PENDING`、`IN_PROGRESS`など）

enumが向いているのは、取りうる値の集合を明確に定義でき、その集合がめったに変わらない場合です。
無効な値をコンパイル時に防ぎたいときのように、型安全性が重要な場面でも効果を発揮します。

反対に、プログラムの実行中に値の集合が変化する場合は、enumを避けます。
列挙子はソースコードに書いて定義するため、あとから値を増やしたり減らしたりできないからです。
値の数が数百を超えるような場合も、enum以外の方法を検討します。
データベースや外部のシステムから受け取る値で、相手の側で値が増えることがある場合も同じです。
enumは定義していない値を表せないので、新しい値を受け取ったときに処理できなくなります。

### よくあるエラーと対処法

列挙型では、定数名の大文字・小文字の間違いや、`switch`文の`case`に型名を付けて書いてしまう誤りがよく起きます。
`switch`文の`case`では、Java 20以前は`Status.ACTIVE`ではなく`ACTIVE`と定数名だけを書く必要がありました。

エラーの例と修正したコードは、GitHubリポジトリにまとめています。<br>
`https://github.com/Nagatani/techbook-java-primer/blob/main/appendix/h-common-errors/chapters/chapter08.md`

## レコード（Record）

### Recordとは？

Recordは、Java 16で正式に導入された<span class="footnote">Java 14と15では、プレビュー機能（試験的に提供する機能）として先に公開されていました。</span>、不変（immutable）なデータを保持するための、簡潔なクラスを定義するための機能です。

これまで、データを保持するためだけのクラス（データクラスやデータキャリアと呼ばれる）を作るには、多くの定型的なコード（ボイラープレートコード）が必要でした。

- `private final`なフィールド
- 全フィールドを初期化するコンストラクター
- 全フィールドのゲッターメソッド（アクセサー）
- `equals()`, `hashCode()`, `toString()` メソッドのオーバーライド

`Record`は、これらの定型コードをコンパイラーが自動的に生成してくれます。

フィールドを`private final`にしてsetterを持たせないという不変クラスのルールは、第6章「不変性とfinalキーワード」で学びました。
`equals()`・`hashCode()`・`toString()`をオーバーライドする理由と書き方は、第5章の節「`Object`クラスのメソッド（toString・equals・hashCode）」で学んでいます。
Recordは、これらを手で書く代わりにコンパイラーに任せるしくみです。

### 基本的な構文

以下のコードは、Recordの最も簡潔な定義例です。recordキーワードを使って、名前と年齢を持つPersonクラスを1行で定義しています。この短い定義から、コンパイラーは自動的にコンストラクター、アクセサーメソッド、equals()、hashCode()、toString()などの必要なメソッドをすべて生成します。

<span class="listing-number">**サンプルコード8-11**　Person.java</span>

```java
public record Person(String name, int age) {}
```

これだけで、コンパイラーが次のクラスを定義したのとほぼ同じ意味になります（メソッドの中身は省略しています）。

<span class="listing-number">**サンプルコード8-12**</span>

```java
public final class Person {
    private final String name;  // ①
    private final int age;      // ①
    
    public Person(String name, int age) {  // ②
        this.name = name;
        this.age = age;
    }
    
    public String name() { return this.name; }  // ③
    public int age() { return this.age; }       // ③
    
    @Override
    public boolean equals(Object o) { /* 全フィールドを比較する実装 */ }  // ④
    
    @Override
    public int hashCode() { /* 全フィールドから計算する実装 */ }  // ⑤
    
    @Override
    public String toString() { /* 全フィールドを表示する実装 */ }  // ⑥
}
```

### 自動生成される要素の詳細

- ① 不変フィールド
    + 全フィールドがprivate finalとして宣言される
- ② 正準コンストラクター
    + 全フィールドを初期化する標準的なコンストラクター
- ③ アクセサーメソッド
    + フィールド名と同じ名前のメソッド（従来のgetXxx()形式ではない）
- ④ equals()メソッド
    + 全フィールドの値を比較する実装
- ⑤ hashCode()メソッド
    + 全フィールドから一貫性のあるハッシュ値を計算
- ⑥ toString()メソッド
    + フィールド名と値を読みやすい形式で表示

### Recordのメリット・デメリット

Recordを使う最大のメリットは簡潔さです。
フィールドやコンストラクター、アクセサーなどのボイラープレートコードを自分で書かずに済みます。
また、フィールドはすべて`final`になるので、作成したあとで値が書き換わる心配がなく、プログラムの安全性が高まります。
`record`と宣言するだけで、このクラスが「データを保持するためのもの」だという意図も読み手に伝わります。

一方で、`Record`は暗黙的に`final`なので、ほかのクラスから継承できません<span class="footnote">また、Recordは暗黙的に`java.lang.Record`クラスを継承するため、ほかのクラスを継承することもできません。インターフェイスは実装できます。</span>。
また、フィールドを変更できないため、状態が変わっていくオブジェクトを表すのには使えません。

### Recordの使い方

<span class="listing-number">**サンプルコード8-13**　RecordExample.java</span>

```java
public class RecordExample {
    public static void main(String[] args) {
        Person person1 = new Person("山田太郎", 25);
        Person person2 = new Person("山田太郎", 25);
        Person person3 = new Person("鈴木花子", 30);
        
        // アクセサメソッドの使用
        IO.println("名前: " + person1.name());
        IO.println("年齢: " + person1.age());
        
        // toString()の自動実装
        IO.println(person1);  // Person[name=山田太郎, age=25]
        
        // equals()の自動実装
        IO.println(person1.equals(person2));  // true
        IO.println(person1.equals(person3));  // false
        
        // hashCode()の自動実装
        IO.println(person1.hashCode() == person2.hashCode());  // true
    }
}
```

実行結果：

```
名前: 山田太郎
年齢: 25
Person[name=山田太郎, age=25]
true
false
true
```

### カスタムコンストラクターとバリデーション

Recordでは、コンパクトコンストラクターを使用してバリデーションを追加できます。

<span class="listing-number">**サンプルコード8-14**　BankAccount.java</span>

```java
public record BankAccount(String accountNumber, double balance) {
    // コンパクトコンストラクタ
    public BankAccount {
        // 暗黙的にフィールドへの代入が行われる前に実行される
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("口座番号は必須です");
        }
        if (balance < 0) {
            throw new IllegalArgumentException("残高は0以上でなければなりません");
        }
        // フィールドへの代入は自動的に行われる
    }
}
```

### 追加のコンストラクター

標準のコンストラクター以外にも、追加のコンストラクターを定義できます。
次の`Tile`は、幅と高さを持つ長方形のタイルを表すRecordで、1辺の長さだけを渡して正方形のタイルを作るコンストラクターを足しています。

<span class="listing-number">**サンプルコード8-15**　Tile.java</span>

```java
public record Tile(double width, double height) {
    // 正方形用のコンストラクタ
    public Tile(double side) {
        this(side, side);
    }
    
    // 面積を計算するメソッド
    public double area() {
        return width * height;
    }
    
    // 周囲の長さを計算するメソッド
    public double perimeter() {
        return 2 * (width + height);
    }
}
```

### 静的ファクトリメソッド

Recordでも静的ファクトリメソッドパターンを使用できます。

<span class="listing-number">**サンプルコード8-16**　Temperature.java</span>

```java
public record Temperature(double value, String unit) {
    // 静的ファクトリメソッド
    public static Temperature ofCelsius(double celsius) {
        return new Temperature(celsius, "C");
    }
    
    public static Temperature ofFahrenheit(double fahrenheit) {
        return new Temperature(fahrenheit, "F");
    }
    
    public static Temperature ofKelvin(double kelvin) {
        return new Temperature(kelvin, "K");
    }
    
    // 摂氏に変換
    public double toCelsius() {
        return switch (unit) {
            case "C" -> value;
            case "F" -> (value - 32) * 5 / 9;
            case "K" -> value - 273.15;
            default -> throw new IllegalStateException("未知の単位: " + unit);
        };
    }
}
```

### withメソッドパターン

不変性を保ちながら部分的な更新を行うwithメソッドパターンは、Recordで頻繁に使用されます。

<span class="listing-number">**サンプルコード8-17**　Configuration.java</span>

```java
import java.time.Duration;

public record Configuration(
    String host,
    int port,
    boolean useSsl,
    Duration timeout
) {
    // withメソッド群
    public Configuration withHost(String newHost) {
        return new Configuration(newHost, port, useSsl, timeout);
    }
    
    public Configuration withPort(int newPort) {
        return new Configuration(host, newPort, useSsl, timeout);
    }
    
    public Configuration withSsl(boolean newUseSsl) {
        return new Configuration(host, port, newUseSsl, timeout);
    }
    
    public Configuration withTimeout(Duration newTimeout) {
        return new Configuration(host, port, useSsl, newTimeout);
    }
}
```

### パターンマッチング（Java 21以降）

Java 21以降では、Recordとパターンマッチングを組み合わせて使用できます<span class="footnote">Recordを分解するパターン（レコードパターン）と、switchで型を判定するパターンは、プレビュー機能の期間を経てJava 21で正式な機能になりました。</span>。

<span class="listing-number">**サンプルコード8-18**　Point.java、PatternMatchingExample.java</span>

```java
public record Point(int x, int y) {}

public class PatternMatchingExample {
    public static String describe(Object obj) {
        return switch (obj) {
            case Point(int x, int y) -> 
                String.format("座標(%d, %d)の点", x, y);
            case String s -> 
                String.format("文字列: %s", s);
            case null -> 
                "null値";
            default -> 
                "未知のオブジェクト";
        };
    }
    
    public static void main(String[] args) {
        Point p = new Point(10, 20);
        IO.println(describe(p));        // 座標(10, 20)の点
        IO.println(describe("Hello"));  // 文字列: Hello
        IO.println(describe(null));     // null値
        IO.println(describe(42));       // 未知のオブジェクト
    }
}
```

実行結果：

```
座標(10, 20)の点
文字列: Hello
null値
未知のオブジェクト
```

### ローカルRecord

メソッド内でのみ使用する小さなデータ構造として、ローカルRecordを定義できます。

次のサンプルでは、配列から求めた件数・平均・最大・最小を、メソッド内で宣言したRecordにまとめています。

<span class="listing-number">**サンプルコード8-19**　LocalRecordExample.java</span>

```java
public class LocalRecordExample {
    public static void analyzeData(int[] numbers) {
        // ローカルRecord
        record Statistics(int count, double average, int max, int min) {}
        
        if (numbers.length == 0) {
            IO.println("データがありません");
            return;
        }
        
        int sum = 0;
        int max = numbers[0];
        int min = numbers[0];
        for (int n : numbers) {
            sum += n;
            if (n > max) {
                max = n;
            }
            if (n < min) {
                min = n;
            }
        }
        double average = (double) sum / numbers.length;
        
        Statistics stats = new Statistics(numbers.length, average, max, min);
        IO.println("統計情報: " + stats);
    }
}
```

### ネストされたRecord

Recordは他のRecordを含むことができ、複雑なデータ構造を表現できます。

<span class="listing-number">**サンプルコード8-20**　Address.java、Company.java、Employee.java</span>

```java
public record Address(String street, String city, String country) {}

public record Company(String name, Address headquarters) {}

public record Employee(String name, String id, Company company) {
    // 従業員の完全な住所を取得
    public String getFullAddress() {
        return String.format("%s works at %s, located at %s, %s %s",
            name,
            company.name(),
            company.headquarters().street(),
            company.headquarters().city(),
            company.headquarters().country()
        );
    }
}
```

### Recordの実践的な使用例

#### DTOパターン

DTO（Data Transfer Object）は、処理の間やシステムの間でデータをまとめて受け渡すためのオブジェクトです。
次の`ApiResponse`は、処理が成功したかどうか、結果のデータ、メッセージ、作った時刻をまとめて返します。

<span class="listing-number">**サンプルコード8-21**</span>

```java
// APIレスポンス用のDTO
public record ApiResponse(
    boolean success,
    String data,
    String message,
    long timestamp
) {
    public ApiResponse(boolean success, String data, String message) {
        this(success, data, message, System.currentTimeMillis());
    }
    
    public static ApiResponse success(String data) {
        return new ApiResponse(true, data, "Success");
    }
    
    public static ApiResponse error(String message) {
        return new ApiResponse(false, null, message);
    }
}
```

#### Value Objectパターン

<span class="listing-number">**サンプルコード8-22**　Money.java</span>

```java
import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal amount, String currency) {
    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("金額はnullにできません");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("通貨は必須です");
        }
        // 金額を小数点以下2桁に丸める
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }
    
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("異なる通貨は加算できません");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
    
    public Money multiply(int quantity) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)), this.currency);
    }
}
```

### sealed interfaceとRecordの組み合わせ

sealed interfaceは、実装可能なクラスを明示的に制限する機能で<span class="footnote">sealedは、Java 15と16のプレビュー機能を経て、Java 17で正式な機能になりました。</span>、Recordと組み合わせることで型安全な設計を実現できます。

<div class="figure">

![sealed interfaceとRecordの組み合わせ（サンプルコード8-23）](images/diagrams/chapter08-sealed-record.svg)

</div>

<span class="listing-number">**サンプルコード8-23**　Shape.java、Circle.java、Rectangle.java、Triangle.java、ShapeCalculator.java</span>

```java
// 図形を表現する代数的データ型
public sealed interface Shape permits Circle, Rectangle, Triangle {}

public record Circle(double radius) implements Shape {}

public record Rectangle(double width, double height) implements Shape {}

public record Triangle(double base, double height) implements Shape {}

// パターンマッチングによる処理
public class ShapeCalculator {
    public static double calculateArea(Shape shape) {
        return switch (shape) {
            case Circle(double radius) -> Math.PI * radius * radius;
            case Rectangle(double width, double height) -> width * height;
            case Triangle(double base, double height) -> 0.5 * base * height;
        };
    }
}
```

### データ指向プログラミング（DOP）

データ指向プログラミングは、データ構造とビジネスロジックを明確に分離する設計パラダイムです<span class="footnote">Javaの言語設計者であるブライアン・ゲッツが、2022年の記事「Data Oriented Programming in Java」で、Record・sealed・パターンマッチングを組み合わせた書き方として紹介しました。</span>。

次のサンプルの`List<OrderItem>`は、`OrderItem`を順に並べて持つリストです（第9章で学びます）。
`OrderProcessor`では、拡張for文でリストの要素を1つずつ取り出して計算や判定を行います。
本文では、各ファイルの先頭にあるimport文を省いています<span class="footnote">省いたimport文は`java.math.BigDecimal`、`java.time.LocalDateTime`、`java.util.List`です。全文は`https://github.com/Nagatani/techbook-java-primer/tree/main/sample-code/chapter08/08-24/`にあります。</span>。

<span class="listing-number">**サンプルコード8-24**　Order.java（4〜5行目）、Customer.java、OrderItem.java（3行目）、OrderProcessor.java（3〜21行目）</span>

```java
// データの定義（Records）
public record Order(String orderId, Customer customer, List<OrderItem> items, LocalDateTime orderDate) {}
public record Customer(String id, String name, String email) {}
public record OrderItem(String productName, int quantity, BigDecimal price) {}

// ロジックの分離（純粋関数）
public class OrderProcessor {
    public static BigDecimal calculateTotal(Order order) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : order.items()) {
            total = total.add(item.price().multiply(BigDecimal.valueOf(item.quantity())));
        }
        return total;
    }
    
    public static boolean hasProduct(Order order, String productName) {
        for (OrderItem item : order.items()) {
            if (item.productName().equals(productName)) {
                return true;
            }
        }
        return false;
    }
}
```

## EnumとRecordの使い分けと組み合わせ

### 使い分けのガイドライン

#### Enumを使うべき場合
- 値の集合が固定的で明確に定義できる
- 各値に特定の振る舞いを持たせたい
- switch文での分岐処理が必要
- Singletonパターンが必要

#### Recordを使うべき場合
- 不変のデータ構造が必要
- equals()、hashCode()、toString()の標準的な実装で十分
- DTOやValue Objectを実装したい
- 簡潔なコードでデータクラスを定義したい

### EnumとRecordの組み合わせ

次のサンプルは、注文の状態を表すEnumの`OrderStatus`と、その状態を持つRecordの`Order`を組み合わせたものです。
この`Order`は、サンプルコード8-24の`Order`とは別に、状態の移り変わりを表すために定義し直したものです。
`updateStatus`は自分を書き換えず、新しい状態を持つ`Order`を作って返します。
<span class="listing-number">**サンプルコード8-25**　OrderStatus.java（1〜6、20〜23行目）、Order.java（4〜26行目）</span>

```java
public enum OrderStatus {
    PENDING("注文受付", false),
    PROCESSING("処理中", false),
    SHIPPED("発送済み", false),
    DELIVERED("配達完了", true),
    CANCELLED("キャンセル", true);
    // ...
    public boolean isFinal() {
        return isFinal;
    }
}

public record Order(
    String orderId,
    String customerId,
    LocalDateTime orderDate,
    OrderStatus status,
    BigDecimal totalAmount
) {
    public Order {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("注文IDは必須です");
        }
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("合計金額は0以上でなければなりません");
        }
    }
    
    public Order updateStatus(OrderStatus newStatus) {
        if (this.status.isFinal()) {
            throw new IllegalStateException("最終状態の注文は更新できません");
        }
        return new Order(orderId, customerId, orderDate, newStatus, totalAmount);
    }
}
```

### 実践的なアプリケーション例

<span class="listing-number">**サンプルコード8-26**　CharacterClass.java（1〜5、24行目）、CharacterStats.java、GameCharacter.java</span>

```java
// ゲームのキャラクタークラス
public enum CharacterClass {
    WARRIOR("戦士", 100, 20, 10),
    MAGE("魔法使い", 60, 10, 30),
    ARCHER("弓使い", 80, 15, 15);
    // ...
}

public record CharacterStats(int level, int health, int attack, int magic) {
    public CharacterStats levelUp() {
        return new CharacterStats(
            level + 1,
            health + 10,
            attack + 2,
            magic + 2
        );
    }
}

public record GameCharacter(
    String playerName,
    CharacterClass characterClass,
    CharacterStats stats
) {
    public static GameCharacter create(String playerName, CharacterClass characterClass) {
        return new GameCharacter(
            playerName,
            characterClass,
            new CharacterStats(
                1,
                characterClass.getBaseHealth(),
                characterClass.getBaseAttack(),
                characterClass.getBaseMagic()
            )
        );
    }
    
    public GameCharacter levelUp() {
        return new GameCharacter(playerName, characterClass, stats.levelUp());
    }
}
```

## 内部クラス（Inner Classes）

### 内部クラスとは？

内部クラスは、他のクラスの内部に定義されるクラスです<span class="footnote">Javaの言語仕様では、クラスの内側に宣言したクラスをまとめて「ネストしたクラス（nested class）」と呼び、`static`を付けないものだけを「内部クラス（inner class）」と呼びます。本書では広く使われている呼び方に合わせ、4種類とも内部クラスと呼びます。</span>。Javaでは4種類の内部クラスがあり、それぞれ異なる特性と用途を持ちます。

<div class="figure">

![内部クラスの種類と関係性](images/diagrams/chapter08-inner-class-hierarchy.svg)

</div>

### 内部クラスの種類

#### 1. メンバー内部クラス（Non-static Inner Class）

外部クラスのインスタンスに関連付けられた内部クラスです。

<span class="listing-number">**サンプルコード8-27**　OuterClass.java</span>

```java
public class OuterClass {
    private String outerField = "外部フィールド";
    
    // メンバー内部クラス
    public class InnerClass {
        public void printOuterField() {
            // 外部クラスのprivateメンバーにアクセス可能
            IO.println("外部クラスのフィールド: " + outerField);
        }
    }
    
    public static void main(String[] args) {
        OuterClass outer = new OuterClass();
        // 内部クラスのインスタンス化には外部クラスのインスタンスが必要
        OuterClass.InnerClass inner = outer.new InnerClass();
        inner.printOuterField();
    }
}
```

実行結果：

```
外部クラスのフィールド: 外部フィールド
```

#### 2. 静的内部クラス（Static Nested Class）

外部クラスのインスタンスに依存しない内部クラスです。

<span class="listing-number">**サンプルコード8-28**　MathOperations.java</span>

```java
public class MathOperations {
    // 静的内部クラス
    public static class Vector2D {
        private final double x, y;
        
        public Vector2D(double x, double y) {
            this.x = x;
            this.y = y;
        }
        
        public double magnitude() {
            return Math.sqrt(x * x + y * y);
        }
    }
    
    public static void main(String[] args) {
        // 外部クラスのインスタンスなしで生成可能
        Vector2D vector = new Vector2D(3, 4);
        IO.println("大きさ: " + vector.magnitude()); // 5.0
    }
}
```

実行結果：

```
大きさ: 5.0
```

第6章のビルダーパターン（サンプルコード6-11）で使った`Book.Builder`も、`Book`の内側に宣言した静的内部クラスです。
外部クラスのインスタンスがなくても`new Book.Builder(...)`で作れるのは、このためです。

#### 3. ローカル内部クラス（Local Inner Class）

メソッド内で定義される内部クラスです。

<span class="listing-number">**サンプルコード8-29**　LocalClassExample.java</span>

```java
public class LocalClassExample {
    public void processData(final String prefix) {
        // ローカル内部クラス
        class DataProcessor {
            public String process(String data) {
                // メソッドのfinalまたは実質的にfinalな変数にアクセス可能
                return prefix + ": " + data.toUpperCase();
            }
        }
        
        DataProcessor processor = new DataProcessor();
        IO.println(processor.process("hello")); // 例: prefixが"Log"なら「Log: HELLO」
    }
}
```

#### 4. 匿名内部クラス（Anonymous Inner Class）

名前を持たない内部クラスで<span class="footnote">名前がなくてもクラスファイルは作られ、コンパイラーは`AnonymousClassExample$1.class`のように、外部クラスの名前に番号を付けた名前にします。メンバー内部クラスなら`OuterClass$InnerClass.class`です。</span>、インターフェイスの実装や抽象クラスの継承に使用されます。

次のサンプルでは、配列を並べ替える`Arrays.sort()`に、並べ方を決める`Comparator`インターフェイスを実装した匿名クラスのオブジェクトを渡しています。
`Comparator<String>`の`<String>`は、比べる要素が文字列であることを表します（型の指定のしかたは第10章「ジェネリクス」で学びます）。
`compare()`は、1つめの引数を先に並べるなら負の数、後に並べるなら正の数を返します。
ここでは`s2.compareTo(s1)`を返すことで、辞書順の逆に並べています。

<span class="listing-number">**サンプルコード8-30**　AnonymousClassExample.java</span>

```java
import java.util.Arrays;
import java.util.Comparator;

public class AnonymousClassExample {
    public static void main(String[] args) {
        String[] names = {"Alice", "Bob", "Charlie"};
        
        // 匿名内部クラスでComparatorを実装
        Arrays.sort(names, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s2.compareTo(s1); // 逆順ソート
            }
        });
        
        IO.println(Arrays.toString(names));
    }
}
```

実行結果：

```
[Charlie, Bob, Alice]
```

このように、メソッドを1つだけ持つインターフェイスを匿名クラスで実装する書き方は、ラムダ式を使うともっと短く書けます。
第11章「ラムダ式と関数型インターフェイス」の冒頭で、匿名クラスとラムダ式を比べながら、ラムダ式の書き方を学びます。
`Comparator`をラムダ式で書いて並べ替える方法も、第11章で扱います。

### 実践的な使用例：イテレーターパターン

イテレーターは、入れ物にある要素を1つずつ順に取り出すためのオブジェクトです。
次のサンプルでは、文字列を持つ独自のリスト`StringList`が、匿名内部クラスでイテレーターを作って返します。
匿名内部クラスが、外側のクラスのフィールド（`elements`と`size`）を直接参照している点が、ここでの要点です。

<span class="listing-number">**サンプルコード8-31**　StringList.java</span>

```java
// 要素を1つずつ順に取り出すためのインターフェイス
interface StringIterator {
    boolean hasNext();
    String next();
}

public class StringList {
    private final String[] elements;
    private int size = 0;
    
    public StringList(int capacity) {
        elements = new String[capacity];
    }
    
    public void add(String element) {
        if (size < elements.length) {
            elements[size++] = element;
        }
    }
    
    public StringIterator iterator() {
        // 匿名内部クラスでStringIteratorを実装
        return new StringIterator() {
            private int index = 0;
            
            @Override
            public boolean hasNext() {
                return index < size;  // 外側のクラスのフィールドを直接参照
            }
            
            @Override
            public String next() {
                return elements[index++];
            }
        };
    }
    
    public static void main(String[] args) {
        StringList list = new StringList(5);
        list.add("りんご");
        list.add("みかん");
        list.add("ぶどう");
        
        StringIterator it = list.iterator();
        while (it.hasNext()) {
            IO.println(it.next());
        }
    }
}
```

実行結果：

```
りんご
みかん
ぶどう
```

Javaの標準ライブラリにも、同じ考え方の`Iterator`インターフェイスがあります。
第9章で学ぶコレクションは、`Iterator`を返すことで、拡張for文で要素を順に取り出せるようにしています。

### 内部クラスのベストプラクティス

内部クラスは、用途に合わせて種類を選びます。
外部クラスのインスタンスのフィールドやメソッドを使うならメンバー内部クラス、外部クラスのインスタンスから独立して使うなら静的内部クラスにします。
1つのメソッド内で一時的に使うだけなら、ローカル内部クラスか匿名内部クラスで十分です<span class="footnote">Java 8以降では、匿名内部クラスの多くをラムダ式で置き換えて短く書けます。ラムダ式は第11章「ラムダ式と関数型インターフェイス」で学びます。</span>。

メンバー内部クラスのオブジェクトは、外部クラスのインスタンスに関連付けられています。
長い期間使い続ける内部クラスは静的内部クラスにし、不要になった外部クラスへの参照は切って、メモリリークを防ぎます。

## まとめ

本章では、Javaの特殊なクラス形式であるEnum、Record、内部クラスについて学習しました。

### Enumの重要ポイント
- 型安全な定数定義を実現
- フィールドとメソッドを持つことができる
- switch文との相性がよい
- デザインパターンの実装に活用できる

### Recordの重要ポイント
- 不変データクラスを簡潔に定義
- ボイラープレートコードを自動生成
- パターンマッチングとの統合
- DTOやValue Objectの実装に使用される

### 内部クラスの重要ポイント
- 4種類の内部クラスそれぞれの特性と用途
- 外部クラスとの関係性とアクセス制御
- メンバー内部クラスが外部クラスへの参照を持つことと、メモリリークを防ぐための選び方
- Java 8以降のラムダ式による代替

### 次のステップ
これらの特殊なクラス形式を理解することで、より簡潔で保守性の高いコードを書けるようになります。次章では、コレクションフレームワークについて学習し、多数のデータをまとめて扱う方法を習得します。

## 章末演習

### 演習課題へのアクセス

本章の演習課題は、GitHubリポジトリで提供されています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter08/`

### 課題構成

演習は題材ごとに、レコード（`records/`）と列挙型（`enums/`）の2つのディレクトリに分かれています。
それぞれに、難易度別の3つのレベルの課題があります。

- 基礎課題（basic/）
    + EnumとRecordの基本的な使い方
- 発展課題（advanced/）
    + 実践的なアプリケーションでの活用
- チャレンジ課題（challenge/）
    + パターンマッチングや高度な設計パターン

各課題には、詳細な要件と実装のヒントが含まれています。
