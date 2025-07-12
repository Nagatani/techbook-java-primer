# 第7章 抽象クラスとインターフェイス

## 本章の学習目標

### 前提知識

本章を学習するためには、第6章までのオブジェクト指向プログラミングの概念を完全に理解していることが必要です。特に、クラスの継承、メソッドのオーバーライド、ポリモーフィズムの基本的なしくみについての実践的な経験が不可欠です。また、複数のクラスが共通の性質を持つ場合の設計課題について、実際のプログラミングで直面した経験があることが望まれます。

さらに、より深い学習のためには、実装の詳細を隠蔽しながら共通のインターフェイスを提供する必要性を感じたことがあると、抽象化の重要性をより深く理解できるでしょう。大規模なシステムにおいて、異なる実装を統一的に扱う必要性を経験していると、抽象クラスとインターフェイスの設計思想がより鮮明になります。

### 知識理解目標

本章では、Javaの抽象化メカニズムである抽象クラスとインターフェイスの概念を深く理解することが目標です。抽象クラスは部分的な実装を持つ不完全なクラスであり、共通の基盤を提供しながら詳細な実装を子クラスに委ねるしくみです。インターフェイスは純粋な契約であり、実装すべきメソッドの仕様のみを定義します。これらの使い分け、is-a関係とcan-do関係の違い、Java 8以降のインターフェイスの進化（defaultメソッド、staticメソッド、privateメソッド）について包括的に理解します。

### 技能習得目標

技能習得の面では、抽象クラスとインターフェイスを使った実践的な設計・実装ができるようになることが目標です。抽象メソッドの定義と実装、テンプレートメソッドパターンの活用、複数インターフェイスの実装による機能の組み合わせ、defaultメソッドを使った後方互換性の維持などの技術を習得します。また、ダイヤモンド継承問題の解決方法、インターフェイス分離原則の適用、戦略パターンやMixinパターンなどの設計パターンの実装スキルも身につけます。

### 設計能力目標

設計能力の観点からは、抽象化を活用した柔軟で拡張性の高いシステム設計ができるようになることが目標です。これは、適切な抽象度の選択、責務の分離、オープン・クローズド原則の実践、プラグイン可能なアーキテクチャの構築などを含みます。実世界の問題を適切に抽象化し、保守性と拡張性を両立させた設計を行う能力を養います。

### 到達レベルの指標

最終的な到達レベルとしては、以下のことができます：
- 抽象クラスとインターフェイスの使い分けを正しく判断し、適切に設計できる
- 複数のデザインパターンを組み合わせた高度な設計ができる
- Java 8以降のインターフェイス機能を活用した現代的なコードが書ける
- SOLID原則にもとづいた拡張性の高いシステムアーキテクチャを構築できる
- 実世界の複雑な問題を適切な抽象化レベルでモデリングできる



## 7.1 抽象化の必要性

これまでの章で、継承を使うとクラスの機能を再利用できることを学びました。しかし、親クラスの段階では、具体的な処理内容を決められない場合があります。

たとえば、「図形」クラスを考えてみましょう。すべての図形（円、長方形、三角形など）には「面積を計算する」という共通の機能が考えられます。しかし、親クラスである「図形」の段階で、面積の具体的な計算方法を実装することはできません。円の面積は「半径 × 半径 × π」ですが、長方形は「幅 × 高さ」であり、計算方法がまったく異なるからです。

このように、**共通の機能（概念）は存在するが、その具体的な実装は子クラスに任せたい**、という場合に利用するのが**抽象クラス**と**インターフェイス**です。これらは、プログラムの「抽象度」を上げ、より柔軟で拡張性の高い設計を実現するための重要なしくみです。

## 7.2 抽象クラスと抽象メソッド

**抽象クラス (Abstract Class)** とは、不完全な部分を持つ、インスタンス化できないクラスのことです。その不完全な部分が**抽象メソッド**です。

**抽象メソッド (Abstract Method)** とは、**実装（メソッドの中身の処理）を持たない**メソッドのことです。`abstract`修飾子を付け、メソッドの定義（名前、引数、戻り値の型）だけを宣言します。

### 抽象クラスのルール

抽象クラスを使う際には、いくつかの重要なルールを理解しておく必要があります。まず最も基本的なルールとして、1つでも抽象メソッドを持つクラスは、必ず抽象クラスとして`abstract`修飾子を付けて宣言しなければなりません。これはJavaの言語仕様で定められた強制的なルールであり、コンパイラはこのルールに従わないコードをエラーとして扱います。

次に、抽象クラスは`new`キーワードを使ってインスタンスを生成できないという重要な制約があります。これは、抽象クラスが不完全な定義（抽象メソッド）を含んでいるからで、実装がないメソッドを持つオブジェクトを作成することは論理的に不可能だからです。

さらに、抽象クラスを継承した子クラスには、親クラスのすべての抽象メソッドをオーバーライドして具体的に実装する責任があります。もし1つでも実装しない抽象メソッドが残っている場合、その子クラスもまた抽象クラスとして宣言しなければなりません。このメカニズムにより、抽象メソッドの実装を強制し、設計上の契約を確実に守らせることができます。

### 実践例：`Shape`抽象クラス

「図形」の例を抽象クラスで実装してみましょう。

<span class="listing-number">**サンプルコード7-1**</span>

```java
// 抽象クラス Shape
public abstract class Shape {
    private String name;

    public Shape(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    // 抽象メソッド：実装は子クラスに強制される
    public abstract double getArea();

    // 通常のメソッドも持てる
    public void display() {
        System.out.println("これは " + getName() + " です。");
        System.out.println("面積は " + getArea() + " です。");
    }
}
```

`Shape`クラスは`getArea()`メソッドの具体的な計算方法を知らないため、このメソッドを抽象メソッドとして宣言します。

### 抽象クラスの継承

子クラスである`Circle`と`Rectangle`は、`Shape`クラスを継承し、それぞれの方法で`getArea()`メソッドを実装します。

<span class="listing-number">**サンプルコード7-2**</span>

```java
// Circle.java
public class Circle extends Shape {
    private double radius;

    public Circle(double radius) {
        super("円"); // 親クラスのコンストラクタを呼び出す
        this.radius = radius;
    }

    // 抽象メソッドの実装
    @Override
    public double getArea() {
        return this.radius * this.radius * Math.PI;
    }
}

// Rectangle.java
public class Rectangle extends Shape {
    private double width;
    private double height;

    public Rectangle(double width, double height) {
        super("長方形");
        this.width = width;
        this.height = height;
    }

    // 抽象メソッドの実装
    @Override
    public double getArea() {
        return this.width * this.height;
    }
}
```

### 抽象クラスの利用

抽象クラスはインスタンス化できませんが、親クラスとしてポリモーフィズムを活用することはできます。

<span class="listing-number">**サンプルコード7-3**</span>

```java
// Main.java
public class Main {
    public static void main(String[] args) {
        // Shape shape = new Shape("図形"); // コンパイルエラー！ 抽象クラスはインスタンス化できない

        Shape circle = new Circle(10.0);
        Shape rectangle = new Rectangle(5.0, 8.0);

        circle.display();    // 円のgetArea()が呼ばれる
        rectangle.display(); // 長方形のgetArea()が呼ばれる
    }
}
```

抽象クラスは、**共通の性質（フィールドや実装済みのメソッド）を持ちつつ、一部の振る舞いだけを子クラスに強制したい**場合に非常に有効です。

## 7.3 インターフェイス：振る舞いの契約

**インターフェイス (Interface)** は、クラスがどのような**振る舞い（メソッド）を持つべきか**を定めた「**契約**」です。

インターフェイスは、メソッドのシグネチャ（名前、引数、戻り値の型）と定数のみを定義でき、インスタンスフィールドを持つことはできません。実装は一切持たず、クラスが実装すべきメソッドの仕様だけを列挙します。

### インターフェイスのルール

1.  `interface`キーワードを使って宣言する。
2.  定義できるメンバーは、**抽象メソッド**、**定数 (`public static final`)**、そしてJava 8から追加された**`default`メソッド**と**`static`メソッド**のみ。
3.  インターフェイスもインスタンス化できない。
4.  クラスは`implements`キーワードを使ってインターフェイスを**実装**する。
5.  インターフェイスを実装したクラスは、そのインターフェイスが持つすべての抽象メソッドを実装しなければならない。
6.  クラスは**複数のインターフェイスを同時に実装できる**（`implements InterfaceA, InterfaceB`のようにカンマで区切る）。これはクラスの単一継承とは異なる大きな特徴です。

### 実践例：`Drawable`と`Serializable`インターフェイス

<span class="listing-number">**サンプルコード7-4**</span>

```java
// Drawable.java
public interface Drawable {
    // インターフェイス内のメソッドは自動的に public abstract になる
    void draw();
}

// Serializable.java
public interface Serializable {
    void saveToFile(String filename);
}
```

### インターフェイスの実装

`implements`キーワードを使って、クラスにインターフェイスの振る舞いを実装します。

<span class="listing-number">**サンプルコード7-5**</span>

```java
// Character.java
// DrawableとSerializableの両方のインターフェイスを実装する
public class Character implements Drawable, Serializable {
    private String name;

    public Character(String name) {
        this.name = name;
    }

    // Drawableインターフェイスのメソッドの実装
    @Override
    public void draw() {
        System.out.println(this.name + "を描画します。");
    }

    // Serializableインターフェイスのメソッドの実装
    @Override
    public void saveToFile(String filename) {
        System.out.println(this.name + "のデータをファイル " + filename + " に保存します。");
    }
}
```

インターフェイスは、**共通の機能（契約）を、継承関係にない異なるクラス間で実現したい**場合に非常に強力です。たとえば、「描画可能(`Drawable`)」という性質は、「キャラクタ」だけでなく「地図」や「グラフ」など、まったく異なるクラスも持つことができます。

## 7.4 抽象クラス vs インターフェイス

どちらも「実装を子クラスに強制する」という点で似ていますが、明確な使い分けがあります。

| 特徴 | 抽象クラス | インターフェイス |
| :--- | :--- | :--- |
| **目的** | is-a関係。共通の基盤・性質を共有する。 | can-do関係。特定の能力・振る舞いを実装する。 |
| **継承/実装** | `extends` (単一継承のみ) | `implements` (複数実装が可能) |
| **メンバー** | インスタンスフィールド、コンストラクタ、実装済みメソッド、抽象メソッド | 定数、抽象メソッド、default/staticメソッド |
| **使い分け** | **密接に関連するクラス階層**を構築したい場合。<br>共通のコードや状態を共有させたい。 | **無関係なクラスに共通の機能**を持たせたい場合。<br>APIの「契約」を定義したい。 |

**経験則:**
*   迷ったら、まずは**インターフェイス**で考える。インターフェイスの方が柔軟性が高い。
*   子クラス間で共通のコードやフィールドをどうしても共有したい場合に、**抽象クラス**を検討する。

## 7.5 Java 8以降のインターフェイス進化

Java 8以降、インターフェイスは劇的に進化し、より柔軟で強力な設計が可能になりました。この進化により、既存のAPIを破壊することなく新機能を追加でき、コードの重複を減らし、より優れた設計パターンを実現できるようになりました。

### インターフェイスの歴史的進化

#### Java 7まで：純粋な契約

Java 7まで、インターフェイスは「純粋な契約」として機能していました。この制約は、インターフェイスの役割を明確にする一方で、API の進化において大きな課題をもたらしていました。例えば、既存のインターフェイスに新しいメソッドを追加すると、そのインターフェイスを実装しているすべてのクラスがコンパイルエラーを起こしてしまうという問題がありました。

<span class="listing-number">**サンプルコード7-6**</span>

```java
// 従来のインターフェイス（Java 7まで）
public interface OldStyleInterface {
    // 定数のみ（暗黙的にpublic static final）
    int CONSTANT = 42;
    
    // 抽象メソッドのみ（暗黙的にpublic abstract）
    void abstractMethod();
    String anotherMethod(int param);
}
```

このような制約のため、ライブラリの開発者は新機能を追加する際に既存のAPIを壊してしまうか、完全に新しいインターフェイスを作成するかの難しい選択を迫られていました。

#### Java 8：defaultメソッドとstaticメソッドの導入

Java 8 では、インターフェイスの進化における最大の課題である「後方互換性」を解決するため、`default`メソッドと`static`メソッドが導入されました。これにより、既存のインターフェイスを壊すことなく新機能を追加できるようになり、Java のエコシステム全体の進化が大幅に加速されました。特に、Java 8 で導入されたStream API は、この機能なしには既存のコレクションフレームワークに統合することができませんでした。

<span class="listing-number">**サンプルコード7-7**</span>

```java
// Java 8でのインターフェイス拡張
public interface ModernInterface {
    // 従来の抽象メソッド
    void abstractMethod();
    
    // defaultメソッド：実装を持つ
    default void defaultMethod() {
        System.out.println("デフォルト実装");
    }
    
    // staticメソッド：ユーティリティ機能
    static void staticUtilityMethod() {
        System.out.println("静的ユーティリティ");
    }
}
```

`default`メソッドにより、実装クラスは新しいメソッドを実装しなくてもコンパイルエラーが発生せず、必要に応じてオーバーライドできるという柔軟性を持つようになりました。

#### Java 9：privateメソッドの追加

Java 9 では、インターフェイス内での`private`メソッドがサポートされました。これにより、複数の`default`メソッドや`static`メソッド間で共通ロジックを共有できるようになり、コードの重複を避けながらインターフェイス内部の実装を整理できるようになりました。`private`メソッドは外部から見えないため、インターフェイスの公開APIを汚すことなく、内部実装の詳細を隠蔽できます。

<span class="listing-number">**サンプルコード7-8**</span>

```java
// Java 9でのさらなる拡張
public interface CompleteInterface {
    // publicメソッド
    default void publicMethod() {
        privateMethod();
        privateStaticMethod();
    }
    
    // privateメソッド：内部実装の共有
    private void privateMethod() {
        System.out.println("内部実装の詳細");
    }
    
    // private staticメソッド
    private static void privateStaticMethod() {
        System.out.println("静的な内部処理");
    }
}
```

この機能により、インターフェイスは単なる契約の定義からより豊かな実装の共有メカニズムへと進化し、抽象クラスとの境界線がより曖昧になりました。

### defaultメソッドの設計思想

#### 後方互換性の維持

defaultメソッドの最も重要な目的は、**既存のインターフェイスに新しいメソッドを追加しても、既存の実装クラスが壊れない**ことです。

<span class="listing-number">**サンプルコード7-9**</span>

```java
// 既存のインターフェイス（多くの実装クラスが存在）
public interface Collection<E> {
    int size();
    boolean isEmpty();
    // ... 他の既存メソッド
    
    // Java 8で追加されたdefaultメソッド
    default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
    
    default Stream<E> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }
}

// 既存の実装クラスは変更不要
class MyCollection<E> implements Collection<E> {
    // streamメソッドを実装しなくても、defaultが使われる
}
```

この例は`default`メソッドの最も重要な利点を実証しています。Java 8のリリース時点で、`Collection`インターフェイスを実装する無数のクラス（`ArrayList`、`HashSet`、`TreeMap`など）が既に存在していましたが、`stream()`メソッドを`default`として追加することで、既存コードを一切変更することなく、すべてのコレクションでStream APIを利用できるようになりました。これにより、Javaエコシステム全体が一気に関数型プログラミングの恩恵を受けることができました。

#### テンプレートメソッドパターンの実現

#### テンプレートメソッドパターンの実現

`default`メソッドの強力な応用例として、**テンプレートメソッドパターン**をインターフェイスで実現できるようになりました。従来このパターンは抽象クラスでしか実装できませんでしたが、インターフェイスでも実現可能になったことで、より柔軟な設計が可能になりました。このパターンは、処理の大枠（アルゴリズムの骨格）を定義しつつ、個別のステップを実装クラスに委ねる場合に非常に有効です。

<span class="listing-number">**サンプルコード7-10**</span>

```java
// インターフェイスでテンプレートメソッドパターン
public interface DataProcessor {
    // テンプレートメソッド
    default void process() {
        var data = loadData();
        var validated = validate(data);
        if (validated) {
            var processed = transform(data);
            save(processed);
            onSuccess();
        } else {
            onError("Validation failed");
        }
    }
    
    // 抽象メソッド（サブクラスで実装）
    Object loadData();
    boolean validate(Object data);
    Object transform(Object data);
    void save(Object data);
    
    // フックメソッド（オプション）
    default void onSuccess() {
        System.out.println("Processing completed successfully");
    }
    
    default void onError(String message) {
        System.err.println("Error: " + message);
    }
}
```

このパターンにより、データ処理の共通フローを一箇所で定義し、具体的な処理内容は実装クラスに委ねることができます。フックメソッド（`onSuccess`、`onError`）は必要に応じてオーバーライドできるため、柔軟性と再利用性を両立できます。

### 多重継承問題の解決

#### ダイヤモンド問題

複数のインターフェイスが同じdefaultメソッドを持つ場合、どちらを使うかの問題が発生します。

<span class="listing-number">**サンプルコード7-11**</span>

```java
// ダイヤモンド継承の例
interface A {
    default void method() {
        System.out.println("A");
    }
}

interface B extends A {
    @Override
    default void method() {
        System.out.println("B");
    }
}

interface C extends A {
    @Override
    default void method() {
        System.out.println("C");
    }
}

// ダイヤモンド問題：BとCの両方を継承
class D implements B, C {
    // コンパイルエラー：どちらのmethod()を使うか不明
    
    // 解決策：明示的にオーバーライド
    @Override
    public void method() {
        // 特定のインターフェイスのメソッドを呼び出す
        B.super.method();  // Bのdefaultメソッドを使用
        // または
        // C.super.method();  // Cのdefaultメソッドを使用
        // または独自実装
    }
}
```

#### 継承の優先順位規則

Javaは以下の規則で優先順位を決定します：

1. **クラスが常に優先**: 実装クラスまたは親クラスのメソッドが優先
2. **より具体的なインターフェイスが優先**: サブインターフェイスのメソッドが優先
3. **それでも不明な場合はコンパイルエラー**: 明示的なオーバーライドが必要

<span class="listing-number">**サンプルコード7-12**</span>

```java
// 規則1：クラスが常に優先
class BaseClass {
    public void method() {
        System.out.println("BaseClass");
    }
}

interface BaseInterface {
    default void method() {
        System.out.println("BaseInterface");
    }
}

class Derived extends BaseClass implements BaseInterface {
    // BaseClassのmethod()が自動的に使われる（インターフェイスより優先）
}

// 規則2：より具体的なインターフェイスが優先
interface Parent {
    default void method() {
        System.out.println("Parent");
    }
}

interface Child extends Parent {
    @Override
    default void method() {
        System.out.println("Child");
    }
}

class Implementation implements Parent, Child {
    // Childのmethod()が自動的に使われる（より具体的）
}
```

### staticメソッドの活用

インターフェイスのstaticメソッドは、そのインターフェイスに関連するユーティリティ機能を提供します。

インターフェイスの`static`メソッドは、そのインターフェイスに関連するユーティリティ機能を提供する強力な手段です。これにより、関連する機能を1つのインターフェイス内にまとめ、使いやすいAPIを設計できます。特に、ファクトリメソッドや共通的なユーティリティ処理を提供する場合に威力を発揮します。

<span class="listing-number">**サンプルコード7-13**</span>

```java
public interface JsonSerializable {
    String toJson();
    
    // ファクトリメソッド
    static <T extends JsonSerializable> String serializeList(List<T> items) {
        return items.stream()
            .map(JsonSerializable::toJson)
            .collect(Collectors.joining(",", "[", "]"));
    }
    
    // ユーティリティメソッド
    static String escapeJson(String input) {
        return input.replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }
}

// 使用例
List<User> users = List.of(new User("Alice"), new User("Bob"));
String json = JsonSerializable.serializeList(users);
```

この設計により、JSONシリアライゼーション機能を実装するクラスは`toJson()`メソッドを実装するだけで、リスト全体のシリアライゼーションや文字列エスケープなどの便利な機能を自動的に利用できるようになります。

### 実践的な活用例：防御的プログラミング

### 実践的な活用例：防御的プログラミング

`default`メソッドは、**防御的プログラミング**の実装においても非常に有効です。実装クラスが不正な値や例外的な状態を返す可能性がある場合でも、インターフェイス側で安全なラッパーメソッドを提供することで、呼び出し側のコードをより安全で使いやすくできます。これは特に、外部ライブラリとの境界や、信頼性の異なる複数の実装が存在する場合に重要です。

<span class="listing-number">**サンプルコード7-14**</span>

```java
interface DefensiveInterface {
    List<String> getItems();
    
    // 防御的なdefaultメソッド
    default List<String> getSafeItems() {
        List<String> items = getItems();
        return items != null ? new ArrayList<>(items) : Collections.emptyList();
    }
    
    default Optional<String> getFirstItem() {
        List<String> items = getSafeItems();
        return items.isEmpty() ? Optional.empty() : Optional.of(items.get(0));
    }
    
    default Stream<String> streamItems() {
        return getSafeItems().stream();
    }
    
    // バリデーション付きメソッド
    default boolean addItem(String item) {
        if (item == null || item.trim().isEmpty()) {
            return false;
        }
        getItems().add(item);
        return true;
    }
}
```

このパターンにより、実装クラスが`null`を返したり、変更可能なリストを直接公開したりしても、呼び出し側は常に安全で一貫したAPIを使用できます。また、`Optional`やバリデーションを使用することで、現代的で安全なJavaコードの作法を推進できます。

## 7.6 高度な設計パターン

インターフェイスの進化により、より洗練された設計パターンを実装できるようになりました。ここでは実践的な設計パターンとその実装例を紹介します。

### Mixinパターン

**Mixin**は、クラスに機能を「混ぜ込む」設計パターンです。Java 8のdefaultメソッドにより、複数の機能を組み合わせた柔軟な設計が可能になりました。

<span class="listing-number">**サンプルコード7-15**</span>

```java
// 複数の機能を提供するMixin
interface Timestamped {
    long getTimestamp();
    
    default boolean isExpired(long expirationTime) {
        return System.currentTimeMillis() - getTimestamp() > expirationTime;
    }
    
    default Duration getAge() {
        return Duration.ofMillis(System.currentTimeMillis() - getTimestamp());
    }
}

interface Identifiable {
    String getId();
    
    default String getFullIdentifier() {
        return getClass().getSimpleName() + "#" + getId();
    }
}

interface Versioned {
    int getVersion();
    
    default boolean isNewerThan(Versioned other) {
        return this.getVersion() > other.getVersion();
    }
    
    default boolean isOlderThan(Versioned other) {
        return this.getVersion() < other.getVersion();
    }
}

// 複数のMixinを組み合わせ
class Document implements Timestamped, Identifiable, Versioned {
    private final String id;
    private final long timestamp;
    private int version;
    private String content;
    
    public Document(String id, String content) {
        this.id = id;
        this.timestamp = System.currentTimeMillis();
        this.version = 1;
        this.content = content;
    }
    
    @Override
    public String getId() { return id; }
    
    @Override
    public long getTimestamp() { return timestamp; }
    
    @Override
    public int getVersion() { return version; }
    
    public void update(String newContent) {
        this.content = newContent;
        this.version++;
        
        // デフォルトメソッドを活用
        if (isExpired(3600000)) {  // 1時間
            System.out.println("Document " + getFullIdentifier() + " has expired");
        }
    }
}
```

### トレイトパターン

**トレイト**は、状態を持たない振る舞いの集合です。インターフェイスとdefaultメソッドの組み合わせにより、Javaでもトレイトパターンを実現できます。

<span class="listing-number">**サンプルコード7-16**</span>

```java
// 比較可能オブジェクトのトレイト
interface ComparableTrait<T> extends Comparable<T> {
    
    default boolean isLessThan(T other) {
        return compareTo(other) < 0;
    }
    
    default boolean isGreaterThan(T other) {
        return compareTo(other) > 0;
    }
    
    default boolean isEqualTo(T other) {
        return compareTo(other) == 0;
    }
    
    default boolean isBetween(T lower, T upper) {
        return isGreaterThan(lower) && isLessThan(upper);
    }
    
    default T max(T other) {
        return isGreaterThan(other) ? (T) this : other;
    }
    
    default T min(T other) {
        return isLessThan(other) ? (T) this : other;
    }
}

// 使用例
class Temperature implements ComparableTrait<Temperature> {
    private final double celsius;
    
    public Temperature(double celsius) {
        this.celsius = celsius;
    }
    
    @Override
    public int compareTo(Temperature other) {
        return Double.compare(this.celsius, other.celsius);
    }
    
    public void checkRange() {
        Temperature freezing = new Temperature(0);
        Temperature boiling = new Temperature(100);
        
        if (this.isBetween(freezing, boiling)) {
            System.out.println("水は液体状態です");
        }
    }
}
```

### インターフェイス分離原則（ISP）の実践

**インターフェイス分離原則**は、クライアントが使用しないメソッドへの依存を強制してはならないという原則です。

<span class="listing-number">**サンプルコード7-17**</span>

```java
// 悪い例：肥大化したインターフェイス
interface BadUserService {
    void createUser(User user);
    void deleteUser(String id);
    void updateUser(User user);
    User findUser(String id);
    List<User> findAllUsers();
    void sendEmail(String userId, String message);
    void generateReport(String userId);
    void backupUserData(String userId);
}

// 良い例：責務ごとに分離されたインターフェイス
interface UserRepository {
    void save(User user);
    void delete(String id);
    User findById(String id);
    List<User> findAll();
}

interface UserNotificationService {
    void sendEmail(String userId, String message);
    
    default void sendWelcomeEmail(String userId) {
        sendEmail(userId, "ようこそ！サービスへの登録ありがとうございます。");
    }
    
    default void sendPasswordResetEmail(String userId, String resetLink) {
        sendEmail(userId, "パスワードリセット: " + resetLink);
    }
}

interface UserReportService {
    void generateReport(String userId);
    
    default void generateMonthlyReport(String userId) {
        System.out.println("月次レポートを生成中: " + userId);
        generateReport(userId);
    }
    
    default void generateYearlyReport(String userId) {
        System.out.println("年次レポートを生成中: " + userId);
        generateReport(userId);
    }
}

// 必要な機能だけを実装
class BasicUserService implements UserRepository, UserNotificationService {
    private Map<String, User> users = new HashMap<>();
    
    @Override
    public void save(User user) {
        users.put(user.getId(), user);
        sendWelcomeEmail(user.getId());  // defaultメソッドを活用
    }
    
    @Override
    public void delete(String id) {
        users.remove(id);
    }
    
    @Override
    public User findById(String id) {
        return users.get(id);
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    
    @Override
    public void sendEmail(String userId, String message) {
        System.out.println("メール送信 to " + userId + ": " + message);
    }
}
```

### 関数型インターフェイスとの統合

defaultメソッドを使って、関数型インターフェイスを拡張し、より豊かな機能を提供できます。

<span class="listing-number">**サンプルコード7-18**</span>

```java
@FunctionalInterface
interface EnhancedFunction<T, R> extends Function<T, R> {
    
    // Function<T, R>のapplyメソッドは抽象メソッド
    
    // 条件付き適用
    default Function<T, R> when(Predicate<T> condition, R defaultValue) {
        return t -> condition.test(t) ? this.apply(t) : defaultValue;
    }
    
    // リトライ機能
    default Function<T, R> withRetry(int maxAttempts) {
        return t -> {
            Exception lastException = null;
            for (int i = 0; i < maxAttempts; i++) {
                try {
                    return this.apply(t);
                } catch (Exception e) {
                    lastException = e;
                    try {
                        Thread.sleep(100 * (i + 1));  // 指数バックオフ
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            throw new RuntimeException("Failed after " + maxAttempts + " attempts", lastException);
        };
    }
    
    // メモ化（結果のキャッシュ）
    default Function<T, R> memoized() {
        Map<T, R> cache = new ConcurrentHashMap<>();
        return t -> cache.computeIfAbsent(t, this::apply);
    }
    
    // 実行時間計測
    default Function<T, R> timed(Consumer<Long> timeConsumer) {
        return t -> {
            long start = System.nanoTime();
            try {
                return this.apply(t);
            } finally {
                timeConsumer.accept(System.nanoTime() - start);
            }
        };
    }
}

// 使用例
public class FunctionEnhancementDemo {
    public static void main(String[] args) {
        // 高価な計算を行う関数
        EnhancedFunction<Integer, Double> expensiveComputation = n -> {
            System.out.println("計算中: " + n);
            return Math.sqrt(n) * Math.PI;
        };
        
        // 機能を組み合わせて使用
        Function<Integer, Double> optimizedFunction = expensiveComputation
            .when(n -> n > 0, -1.0)  // 正の数のみ計算
            .memoized()              // 結果をキャッシュ
            .timed(time -> System.out.println("実行時間: " + time + "ns"));
        
        // 同じ値で複数回呼び出し
        System.out.println(optimizedFunction.apply(16));  // 計算実行
        System.out.println(optimizedFunction.apply(16));  // キャッシュから取得
        System.out.println(optimizedFunction.apply(-5));  // デフォルト値を返す
    }
}
```

### バージョニング戦略

APIの進化を管理するための戦略的なアプローチです。

<span class="listing-number">**サンプルコード7-19**</span>

```java
// API進化の管理
interface ServiceV1 {
    String process(String input);
}

interface ServiceV2 extends ServiceV1 {
    // 新機能をdefaultメソッドで追加（後方互換性維持）
    default String processWithOptions(String input, Map<String, String> options) {
        // デフォルトではオプションを無視して従来の処理
        return process(input);
    }
    
    // 型安全な設定オプション
    default String processWithConfig(String input, ServiceConfig config) {
        Map<String, String> options = new HashMap<>();
        options.put("timeout", String.valueOf(config.getTimeout()));
        options.put("retries", String.valueOf(config.getRetries()));
        return processWithOptions(input, options);
    }
}

interface ServiceV3 extends ServiceV2 {
    // さらなる拡張：非同期処理
    default CompletableFuture<String> processAsync(String input) {
        return CompletableFuture.supplyAsync(() -> process(input));
    }
    
    // バッチ処理のサポート
    default List<String> processBatch(List<String> inputs) {
        return inputs.stream()
            .map(this::process)
            .collect(Collectors.toList());
    }
    
    // 非推奨メソッドの管理
    @Deprecated(since = "3.0", forRemoval = true)
    default String oldProcess(String input) {
        System.err.println("警告: oldProcessは非推奨です。processを使用してください。");
        return process(input);
    }
}

// 実装クラスは最小限の変更で新バージョンに対応
class ServiceImpl implements ServiceV3 {
    @Override
    public String process(String input) {
        return "Processed: " + input.toUpperCase();
    }
    
    // オプションで新機能をオーバーライド
    @Override
    public String processWithOptions(String input, Map<String, String> options) {
        String timeout = options.getOrDefault("timeout", "1000");
        return "Processed with timeout " + timeout + ": " + input.toUpperCase();
    }
}
```

## 7.7 章末演習

本章で学んだ抽象クラスとインターフェイスの概念を実践的な課題で確認しましょう。

### 演習課題へのアクセス

**リポジトリ**: `https://github.com/[your-repo]/java-primer-exercises`

### 第7章の課題構成

```
exercises/chapter07/
├── basic/              # 基礎課題（必須）
│   ├── Shape/          # 図形抽象化
│   └── PaymentSystem/  # 支払い戦略
├── advanced/           # 発展課題（推奨）
├── challenge/          # チャレンジ課題（任意）
└── solutions/          # 解答例
```

### 学習目標
- 抽象クラスとインターフェイスの使い分け
- 抽象メソッドの実装
- インターフェイスの多重実装
- defaultメソッドの活用

### 基礎課題の概要

**Shape（図形）課題**
- Shapeを抽象クラスで定義
- CircleとRectangleを具象クラスで実装
- DrawableインターフェイスでSVG出力機能を追加

**PaymentSystem（支払い）課題**
- PaymentMethodインターフェイスで戦略パターンを実装
- CreditCard、BankTransfer、DigitalWalletを実装
- 支払い手数料の計算をdefaultメソッドで共通化

詳細はGitHubリポジトリのREADMEを参照してください。

**次のステップ**: 第8章「列挙型(Enums)」へ進みましょう。
