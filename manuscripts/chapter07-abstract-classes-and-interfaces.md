# <b>7章</b> <span>抽象クラスとインターフェイス</span> <small>柔軟なシステム設計の基礎</small>

## 本章の学習目標

### 前提知識

必須
- 第6章までのオブジェクト指向プログラミングの概念
- クラスの継承、メソッドのオーバーライド、ポリモーフィズム
- 複数のクラスが共通の性質を持つ場合の設計課題

推奨
- 実装の詳細を隠蔽した共通インターフェイスの必要性
- 大規模システムでの異なる実装の統一的な扱い

### 知識理解目標

本章では、Javaの抽象化メカニズムである抽象クラスとインターフェイスの概念を深く理解することが目標です。抽象クラスは部分的な実装を持つ不完全なクラスであり、共通の基盤を提供しながら詳細な実装を子クラスに委ねるしくみです。インターフェイスは純粋な契約であり、実装することが大切なメソッドの仕様のみを定義します。これらの使い分け、is-a関係とcan-do関係の違い、Java 8以降のインターフェイスの進化（defaultメソッド、staticメソッド、privateメソッド）について包括的に理解します。

### 技能習得目標

技能習得の面では、抽象クラスとインターフェイスを使った実践的な設計・実装ができるようになることが目標です。抽象メソッドの定義と実装、テンプレートメソッドパターンの活用、複数インターフェイスの実装による機能の組み合わせ、defaultメソッドを使った後方互換性の維持などの技術を習得します。また、ダイヤモンド継承問題の解決方法、インターフェイス分離原則の適用、戦略パターンやMixinパターンなどの設計パターンの実装スキルも身につけます。

### 設計能力目標

設計能力の観点からは、抽象化を活用した柔軟で拡張性の高いシステム設計ができるようになることが目標です。これは、適切な抽象度の選択、責務の分離、オープン・クローズド原則の実践、プラグイン可能なアーキテクチャの構築などを含みます。実世界の問題を適切に抽象化し、保守性と拡張性を両立させた設計を行う能力を養います。

### 到達レベルの指標

最終的な到達レベルとしては、以下のことができます。
- 抽象クラスとインターフェイスの使い分けを正しく判断し、適切に設計できる
- 複数のデザインパターンを組み合わせた高度な設計ができる
- Java 8以降のインターフェイス機能を活用した現代的なコードが書ける
- SOLID原則にもとづいた拡張性の高いシステムアーキテクチャを構築できる
- 実世界の複雑な問題を適切な抽象化レベルでモデリングできる



## 抽象化の必要性

これまでの章で、継承を使うとクラスの機能を再利用できることを学びました。しかし、親クラスの段階では、具体的な処理内容を決められない場合があります。

たとえば、「図形」クラスを考えてみましょう。すべての図形（円、長方形、三角形など）には「面積を計算する」という共通の機能が考えられます。しかし、親クラスである「図形」の段階で、面積の具体的な計算方法を実装することはできません。円の面積は「半径 × 半径 × π」ですが、長方形は「幅 × 高さ」であり、計算方法がまったく異なるからです。

このように、共通の機能（概念）は存在するが、その具体的な実装は子クラスに任せたい、という場合に利用するのが抽象クラスとインターフェイスです。これらは、プログラムの「抽象度」を上げ、より柔軟で拡張性の高い設計を実現するための機能です。ただし、これらはオブジェクト指向特有の手法であり、関数型プログラミングでは高階関数や型クラスを使って同様の抽象化を実現します。

## 抽象クラスと抽象メソッド

抽象クラス (Abstract Class) とは、不完全な部分を持つ、インスタンス化できないクラスのことです。その不完全な部分が抽象メソッドです

抽象メソッド (Abstract Method) とは、実装（メソッドの中身の処理）を持たないメソッドのことです。`abstract`修飾子を付け、メソッドの定義（名前、引数、戻り値の型）だけを宣言します

### 抽象クラスのルール

抽象クラスを使う際には、いくつかの大切なルールを理解しておくことがポイントです。まず最も基本的なルールとして、1つでも抽象メソッドを持つクラスは、抽象クラスとして`abstract`修飾子を付けて宣言することが大切です。これはJavaの言語仕様で定められた強制的なルールであり、コンパイラはこのルールに従わないコードをエラーとして扱います。

次に、抽象クラスは`new`キーワードを使ってインスタンスを生成できないという大切な制約があります。これは、抽象クラスが不完全な定義（抽象メソッド）を含んでいるからで、実装がないメソッドを持つオブジェクトを作成することは論理的に不可能だからです。

さらに、抽象クラスを継承した子クラスには、親クラスのすべての抽象メソッドをオーバーライドして具体的に実装する責任があります。もし1つでも実装しない抽象メソッドが残っている場合、その子クラスもまた抽象クラスとして宣言することが大切です。このメカニズムにより、抽象メソッドの実装を強制し、設計上の契約を確実に守らせることができます。

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

抽象クラスは、共通の性質（フィールドや実装済みのメソッド）を持ちつつ、一部の振る舞いだけを子クラスに強制したい場合に非常に有効です。

## インターフェイス：振る舞いの契約

インターフェイス (Interface) は、クラスがどのような振る舞い（メソッド）を持つことが大切かを定めた「契約」です

インターフェイスは、メソッドのシグネチャ（名前、引数、戻り値の型）と定数のみを定義でき、インスタンスフィールドを持つことはできません。実装は一切持たず、クラスが実装することが大切なメソッドの仕様だけを列挙します。

### インターフェイスのルール

1.  `interface`キーワードを使って宣言する。
2.  定義できるメンバーは、抽象メソッド、定数 (`public static final`)、そしてJava 8から追加された`default`メソッドと`static`メソッドのみ。
3.  インターフェイスもインスタンス化できない。
4.  クラスは`implements`キーワードを使ってインターフェイスを実装する。
5.  インターフェイスを実装したクラスは、そのインターフェイスが持つすべての抽象メソッドを実装しなければならない。
6.  クラスは複数のインターフェイスを同時に実装できる（`implements InterfaceA, InterfaceB`のようにカンマで区切る）。これはクラスの単一継承とは異なる大きな特徴です。

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

インターフェイスは、共通の機能（契約）を、継承関係にない異なるクラス間で実現したい場合に非常に強力です。たとえば、「描画可能(`Drawable`)」という性質は、「キャラクタ」だけでなく「地図」や「グラフ」など、まったく異なるクラスも持つことができます。

## 抽象クラス vs インターフェイス

どちらも「実装を子クラスに強制する」という点で似ていますが、明確な使い分けがあります。

| 特徴 | 抽象クラス | インターフェイス |
| :--- | :--- | :--- |
| 目的 | is-a関係。共通の基盤・性質を共有する。 | can-do関係。特定の能力・振る舞いを実装する。 |
| 継承/実装 | `extends` (単一継承のみ) | `implements` (複数実装が可能) |
| メンバー | インスタンスフィールド、コンストラクタ、実装済みメソッド、抽象メソッド | 定数、抽象メソッド、default/staticメソッド |
| 使い分け | 密接に関連するクラス階層を構築したい場合。<br>共通のコードや状態を共有させたい。 | 無関係なクラスに共通の機能を持たせたい場合。<br>APIの「契約」を定義したい。 |

#### 経験則
* 迷ったら、まずはインターフェイスで考える。インターフェイスの方が柔軟性が高い
* 子クラス間で共通のコードやフィールドをどうしても共有したい場合に、抽象クラスを検討する

## Java 8以降のインターフェイス進化

Java 8以降、インターフェイスは大きく進化し、より柔軟な設計が可能になりました。この進化により、既存のAPIを破壊することなく新機能を追加でき、コードの重複を減らし、様々な設計パターンを実現できるようになりました。これは関数型プログラミングの影響を受けたJavaの進化の一例です。

### インターフェイスの歴史的進化

#### Java 7まで：純粋な契約

Java 7まで、インターフェイスは「純粋な契約」として機能していました。この制約は、インターフェイスの役割を明確にする一方で、APIの進化において大きな課題をもたらしていました。例えば、既存のインターフェイスに新しいメソッドを追加すると、そのインターフェイスを実装しているすべてのクラスがコンパイルエラーを起こしてしまうという問題がありました。

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

Java 8では、インターフェイスの進化における最大の課題である「後方互換性」を解決するため、`default`メソッドと`static`メソッドが導入されました。これにより、既存のインターフェイスを壊すことなく新機能を追加できるようになり、Javaのエコシステム全体の進化が大幅に加速されました。この機能により、既存のコレクションフレームワークに新しい機能を追加することが可能になりました（Stream APIなどの高度な機能はChapter 12で学習します）。

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

Java 9では、インターフェイス内での`private`メソッドがサポートされました。これにより、複数の`default`メソッドや`static`メソッド間で共通ロジックを共有できるようになり、コードの重複を避けながらインターフェイス内部の実装を整理できるようになりました。`private`メソッドは外部から見えないため、インターフェイスの公開APIを汚すことなく、内部実装の詳細を隠蔽できます。

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

defaultメソッドの最も大切な目的は、既存のインターフェイスに新しいメソッドを追加しても、既存の実装クラスが壊れないことです。

<span class="listing-number">**サンプルコード7-9**</span>

```java
// 既存のインターフェイス（多くの実装クラスが存在）
public interface Collection<E> {
    int size();
    boolean isEmpty();
    // ... 他の既存メソッド
    
    // Java 8で追加されたdefaultメソッドの例
    // 注：Stream APIはChapter 12で詳しく学習します
    // ここでは別のdefaultメソッドの例を示します
    default boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (!contains(e)) {
                return false;
            }
        }
        return true;
    }
    
    default boolean removeIf(Predicate<? super E> filter) {
        boolean removed = false;
        Iterator<E> each = iterator();
        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
                removed = true;
            }
        }
        return removed;
    }
}

// 既存の実装クラスは変更不要
class MyCollection<E> implements Collection<E> {
    // defaultメソッドを実装しなくても、デフォルト実装が使われる
}
```

この例は`default`メソッドの最も大切な利点を実証しています。Java 8のリリース時点で、`Collection`インターフェイスを実装する無数のクラス（`ArrayList`、`HashSet`、`TreeMap`など）が既に存在していましたが、新しいメソッドを`default`として追加することで、既存コードを一切変更することなく、すべてのコレクションで新機能を利用できるようになりました。これにより、APIの後方互換性を保ちながら機能を拡張できるようになりました。

#### テンプレートメソッドパターンの実現

#### テンプレートメソッドパターンの実現

`default`メソッドの強力な応用例として、テンプレートメソッドパターンをインターフェイスで実現できるようになりました。従来このパターンは抽象クラスでしか実装できませんでしたが、インターフェイスでも実現可能になったことで、より柔軟な設計が可能になりました。このパターンは、処理の大枠（アルゴリズムの骨格）を定義しつつ、個別のステップを実装クラスに委ねる場合に非常に有効です。

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

Javaは以下の規則で優先順位を決定します。

1. クラスが常に優先： 実装クラスまたは親クラスのメソッドが優先
2. より具体的なインターフェイスが優先： サブインターフェイスのメソッドが優先
3. それでも不明な場合はコンパイルエラー: 明示的なオーバーライドが必要

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
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(items.get(i).toJson());
        }
        sb.append("]");
        return sb.toString();
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

`default`メソッドは、防御的プログラミングの実装においても非常に有効です。実装クラスが不正な値や例外的な状態を返す可能性がある場合でも、インターフェイス側で安全なラッパーメソッドを提供することで、呼び出し側のコードをより安全で使いやすくできます。これは特に、外部ライブラリとの境界や、信頼性の異なる複数の実装が存在する場合に大切です。

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
    
    default Iterator<String> iterateItems() {
        return getSafeItems().iterator();
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

## 高度な設計パターン

インターフェイスの進化により、より洗練された設計パターンを実装できるようになりました。ここでは実践的な設計パターンとその実装例を紹介します。

### Mixinパターン

Mixinは、クラスに機能を「混ぜ込む」設計パターンです。Java 8のdefaultメソッドにより、複数の機能を組み合わせた柔軟な設計が可能になりました

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

トレイトは、状態を持たない振る舞いの集合です。インターフェイスとdefaultメソッドの組み合わせにより、Javaでもトレイトパターンを実現できます

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

インターフェイス分離原則は、クライアントが使用しないメソッドへの依存を強制してはならないという原則です

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
        List<String> results = new ArrayList<>();
        for (String input : inputs) {
            results.add(process(input));
        }
        return results;
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

## Factoryパターン：オブジェクト生成の抽象化

### Factoryパターンとは

Factoryパターンは、オブジェクトの生成を専門に行うクラス（ファクトリー）を用意することで、オブジェクト生成のロジックをカプセル化するデザインパターンです。これにより、クライアントコードは具体的なクラスに依存することなく、インターフェイスや抽象クラスを通じてオブジェクトを利用できます。

### Simple Factory パターン

最も基本的なFactoryパターンの実装例を見てみましょう。

<span class="listing-number">**サンプルコード7-20**</span>

```java
// 共通のインターフェイス
interface Shape {
    void draw();
    double getArea();
}

// 具体的な実装クラス
class Circle implements Shape {
    private double radius;
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    @Override
    public void draw() {
        System.out.println("○ 半径 " + radius + " の円を描画");
    }
    
    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }
}

class Rectangle implements Shape {
    private double width;
    private double height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void draw() {
        System.out.println("□ " + width + " x " + height + " の長方形を描画");
    }
    
    @Override
    public double getArea() {
        return width * height;
    }
}

class Triangle implements Shape {
    private double base;
    private double height;
    
    public Triangle(double base, double height) {
        this.base = base;
        this.height = height;
    }
    
    @Override
    public void draw() {
        System.out.println("△ 底辺 " + base + ", 高さ " + height + " の三角形を描画");
    }
    
    @Override
    public double getArea() {
        return base * height / 2;
    }
}

// ファクトリークラス
public class ShapeFactory {
    public static Shape createShape(String type, double... params) {
        switch (type.toLowerCase()) {
            case "circle":
                if (params.length != 1) {
                    throw new IllegalArgumentException("円には半径が必要です");
                }
                return new Circle(params[0]);
                
            case "rectangle":
                if (params.length != 2) {
                    throw new IllegalArgumentException("長方形には幅と高さが必要です");
                }
                return new Rectangle(params[0], params[1]);
                
            case "triangle":
                if (params.length != 2) {
                    throw new IllegalArgumentException("三角形には底辺と高さが必要です");
                }
                return new Triangle(params[0], params[1]);
                
            default:
                throw new IllegalArgumentException("不明な図形タイプ: " + type);
        }
    }
}

// 使用例
public class FactoryDemo {
    public static void main(String[] args) {
        // ファクトリーを使って図形を生成
        Shape circle = ShapeFactory.createShape("circle", 5.0);
        Shape rectangle = ShapeFactory.createShape("rectangle", 4.0, 6.0);
        Shape triangle = ShapeFactory.createShape("triangle", 3.0, 4.0);
        
        // 具体的なクラスを知らなくても使える
        List<Shape> shapes = List.of(circle, rectangle, triangle);
        
        for (Shape shape : shapes) {
            shape.draw();
            System.out.println("面積: " + shape.getArea());
            System.out.println();
        }
    }
}
```

### Factory Method パターン

より高度なFactoryパターンとして、Factory Methodパターンがあります。これは、オブジェクト生成のインターフェイスを定義し、サブクラスがどのクラスをインスタンス化するかを決定します。

<span class="listing-number">**サンプルコード7-21**</span>

```java
// 抽象ファクトリークラス
abstract class DocumentFactory {
    // Factory Method - サブクラスで実装
    public abstract Document createDocument();
    
    // テンプレートメソッド
    public Document newDocument() {
        Document doc = createDocument();
        doc.open();
        return doc;
    }
}

// 具体的なファクトリー
class WordDocumentFactory extends DocumentFactory {
    @Override
    public Document createDocument() {
        return new WordDocument();
    }
}

class PdfDocumentFactory extends DocumentFactory {
    @Override
    public Document createDocument() {
        return new PdfDocument();
    }
}

// ドキュメントインターフェイス
interface Document {
    void open();
    void save();
    void close();
}

// 具体的なドキュメント実装
class WordDocument implements Document {
    @Override
    public void open() {
        System.out.println("Word文書を開いています...");
    }
    
    @Override
    public void save() {
        System.out.println("Word文書を保存しています...");
    }
    
    @Override
    public void close() {
        System.out.println("Word文書を閉じています...");
    }
}

class PdfDocument implements Document {
    @Override
    public void open() {
        System.out.println("PDF文書を開いています...");
    }
    
    @Override
    public void save() {
        System.out.println("PDF文書を保存しています...");
    }
    
    @Override
    public void close() {
        System.out.println("PDF文書を閉じています...");
    }
}
```

### Factoryパターンの利点

1. 結合度の低減： クライアントコードは具体的なクラスに依存しない
2. 拡張性： 新しい型を追加しても既存のコードを変更する必要がない
3. 複雑な生成ロジックの隠蔽： オブジェクト生成の詳細をファクトリー内に隠蔽
4. 一貫性： オブジェクト生成の方法を統一できる

### 実践的な使用例：データベース接続

<span class="listing-number">**サンプルコード7-22**</span>

```java
// データベース接続インターフェイス
interface DatabaseConnection {
    void connect();
    void executeQuery(String sql);
    void close();
}

// 具体的な実装
class MySQLConnection implements DatabaseConnection {
    private String connectionString;
    
    public MySQLConnection(String host, int port, String database) {
        this.connectionString = String.format("mysql://%s:%d/%s", host, port, database);
    }
    
    @Override
    public void connect() {
        System.out.println("MySQLに接続: " + connectionString);
    }
    
    @Override
    public void executeQuery(String sql) {
        System.out.println("MySQLでクエリ実行: " + sql);
    }
    
    @Override
    public void close() {
        System.out.println("MySQL接続を閉じる");
    }
}

class PostgreSQLConnection implements DatabaseConnection {
    private String connectionString;
    
    public PostgreSQLConnection(String host, int port, String database) {
        this.connectionString = String.format("postgresql://%s:%d/%s", host, port, database);
    }
    
    @Override
    public void connect() {
        System.out.println("PostgreSQLに接続: " + connectionString);
    }
    
    @Override
    public void executeQuery(String sql) {
        System.out.println("PostgreSQLでクエリ実行: " + sql);
    }
    
    @Override
    public void close() {
        System.out.println("PostgreSQL接続を閉じる");
    }
}

// データベース接続ファクトリー
public class DatabaseConnectionFactory {
    public static DatabaseConnection createConnection(String type, String host, int port, String database) {
        switch (type.toUpperCase()) {
            case "MYSQL":
                return new MySQLConnection(host, port, database);
            case "POSTGRESQL":
                return new PostgreSQLConnection(host, port, database);
            default:
                throw new IllegalArgumentException("サポートされていないデータベース: " + type);
        }
    }
    
    // 設定ファイルから接続を作成
    public static DatabaseConnection createConnectionFromConfig(Properties config) {
        String type = config.getProperty("db.type");
        String host = config.getProperty("db.host", "localhost");
        int port = Integer.parseInt(config.getProperty("db.port", "3306"));
        String database = config.getProperty("db.database");
        
        return createConnection(type, host, port, database);
    }
}
```

Factoryパターンは、オブジェクト指向設計において非常に重要なパターンの1つです。適切に使用することで、保守性と拡張性の高いシステムを構築できます。

## よくあるエラーと対処法

抽象クラスとインターフェイスの学習過程で遭遇する典型的なエラーと、その対処法を解説します。

### 1. 抽象クラスのインスタンス化エラー

抽象クラスは直接インスタンス化できません。

エラー例
```java
abstract class Animal {
    public abstract void makeSound();
}

// コンパイルエラーが発生
Animal animal = new Animal();  // Cannot instantiate the type Animal
```

エラーメッセージ
```
Cannot instantiate the type Animal
```

対処法
```java
// 抽象クラスを継承した具象クラスを作成
class Dog extends Animal {
    @Override
    public void makeSound() {
        System.out.println("ワンワン");
    }
}

// 具象クラスをインスタンス化
Animal animal = new Dog();  // OK
```

### 2. 抽象メソッドの実装忘れ

抽象メソッドを実装しないと、継承先のクラスも抽象クラスになってしまいます。

エラー例
```java
abstract class Shape {
    public abstract double getArea();
    public abstract void draw();
}

// コンパイルエラー：抽象メソッドが未実装
class Rectangle extends Shape {
    private double width, height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public double getArea() {
        return width * height;
    }
    
    // draw()メソッドの実装が不足
}
```

エラーメッセージ
```
The type Rectangle must implement the inherited abstract method Shape.draw()
```

対処法
```java
class Rectangle extends Shape {
    private double width, height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public double getArea() {
        return width * height;
    }
    
    @Override
    public void draw() {
        System.out.println("長方形を描画");
    }
}
```

### 3. インターフェイスの実装不備

インターフェイスのすべての抽象メソッドを実装する必要があります。

エラー例
```java
interface Drawable {
    void draw();
    void setColor(String color);
}

// コンパイルエラー：すべてのメソッドが実装されていない
class Circle implements Drawable {
    private double radius;
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    @Override
    public void draw() {
        System.out.println("円を描画");
    }
    
    // setColor()メソッドの実装が不足
}
```

エラーメッセージ
```
The type Circle must implement the inherited abstract method Drawable.setColor(String)
```

対処法
```java
class Circle implements Drawable {
    private double radius;
    private String color = "black";
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    @Override
    public void draw() {
        System.out.println(color + "の円を描画");
    }
    
    @Override
    public void setColor(String color) {
        this.color = color;
    }
}
```

### 4. 多重継承とダイヤモンド問題

Javaでは複数の抽象クラスを継承できませんが、インターフェイスの場合は実装時に名前衝突が発生する可能性があります。

エラー例
```java
interface Flyable {
    default void move() {
        System.out.println("飛んで移動");
    }
}

interface Swimmable {
    default void move() {
        System.out.println("泳いで移動");
    }
}

// コンパイルエラー：同名のdefaultメソッドが衝突
class Duck implements Flyable, Swimmable {
    // どちらのmove()メソッドを使用するか不明
}
```

エラーメッセージ
```
Duplicate default methods named move with the parameters () and () are inherited from the types Swimmable and Flyable
```

対処法
```java
class Duck implements Flyable, Swimmable {
    @Override
    public void move() {
        // 明示的にどちらのメソッドを使用するか指定
        System.out.println("アヒルは");
        Flyable.super.move();  // 飛行を選択
        System.out.println("または");
        Swimmable.super.move();  // 泳ぎを選択
    }
    
    // または独自の実装を提供
    public void moveOnLand() {
        System.out.println("歩いて移動");
    }
}
```

### 5. defaultメソッドとstaticメソッドの混乱

インターフェイスのstaticメソッドは継承されず、直接インターフェイス名で呼び出す必要があります。

エラー例
```java
interface MathUtils {
    static double PI = 3.14159;
    
    static double calculateCircleArea(double radius) {
        return PI * radius * radius;
    }
    
    default void printInfo() {
        // staticメソッドの呼び出し方が間違っている
        System.out.println("面積: " + calculateCircleArea(5.0));  // コンパイルエラー
    }
}

class Calculator implements MathUtils {
    public void doCalculation() {
        // staticメソッドの呼び出し方が間違っている
        double area = calculateCircleArea(3.0);  // コンパイルエラー
    }
}
```

エラーメッセージ
```
Cannot make a static reference to the non-static method calculateCircleArea(double) from the type MathUtils
```

対処法
```java
interface MathUtils {
    static double PI = 3.14159;
    
    static double calculateCircleArea(double radius) {
        return PI * radius * radius;
    }
    
    default void printInfo() {
        // インターフェイス名を明示して呼び出し
        System.out.println("面積: " + MathUtils.calculateCircleArea(5.0));
    }
}

class Calculator implements MathUtils {
    public void doCalculation() {
        // インターフェイス名を明示して呼び出し
        double area = MathUtils.calculateCircleArea(3.0);
    }
}
```

### 6. 抽象クラスvsインターフェイスの使い分け間違い

どちらを使用するべきか迷うケースがあります。

間違った選択例
```java
// 状態を持つのに無理にインターフェイスを使用
interface Vehicle {
    // インターフェイスでは実装できない
    // private String brand;  // コンパイルエラー
    
    void start();
    void stop();
    
    default String getBrand() {
        // 状態を持てないため、この実装は不完全
        return "unknown";
    }
}
```

正しい選択
```java
// 共通の状態と実装を持つ場合は抽象クラス
abstract class Vehicle {
    protected String brand;
    protected boolean isRunning;
    
    public Vehicle(String brand) {
        this.brand = brand;
        this.isRunning = false;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public abstract void start();
    public abstract void stop();
    
    public final boolean isRunning() {
        return isRunning;
    }
}

// 行動の契約のみを定義する場合はインターフェイス
interface Maintainable {
    void performMaintenance();
    
    default void scheduleMaintenanceReminder() {
        System.out.println("メンテナンスをスケジュール");
    }
}

class Car extends Vehicle implements Maintainable {
    public Car(String brand) {
        super(brand);
    }
    
    @Override
    public void start() {
        isRunning = true;
        System.out.println(brand + "の車を始動");
    }
    
    @Override
    public void stop() {
        isRunning = false;
        System.out.println(brand + "の車を停止");
    }
    
    @Override
    public void performMaintenance() {
        System.out.println(brand + "の車のメンテナンス実行");
    }
}
```

### 7. オーバーライドアノテーションの省略

`@Override`アノテーションを省略すると、メソッドの実装ミスに気づきにくくなります。

問題のあるコード
```java
abstract class Animal {
    public abstract void makeSound();
}

class Cat extends Animal {
    // @Overrideアノテーションがない
    public void makesound() {  // メソッド名のタイプミス
        System.out.println("ニャー");
    }
}
```

この場合、`makesound()`は新しいメソッドとして認識され、`makeSound()`の実装が不足しているエラーが発生します。

正しい実装
```java
class Cat extends Animal {
    @Override
    public void makeSound() {  // 正しいメソッド名
        System.out.println("ニャー");
    }
}
```

### トラブルシューティングのベストプラクティス

1. IDEの活用： 現代のIDEはコンパイルエラーを即座に表示し、修正提案を提供します
2. @Overrideアノテーション： 必ず付与して、実装ミスを防ぎましょう
3. 段階的な実装： 複雑な継承階層は一度に実装せず、段階的に構築しましょう
4. テストの活用： 抽象クラスやインターフェイスの実装は、単体テストで動作確認しましょう
5. コードレビュー: 設計の妥当性を同僚と確認しましょう

これらの知識を身につけることで、抽象クラスとインターフェイスを効果的に活用した、保守性の高いJavaプログラムを作成できるようになります。

## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています。
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter07/`

### 課題構成
- 本章の基本概念の理解確認
- 応用的な実装練習
- 実践的な総合問題

詳細な課題内容と実装のヒントは、各課題フォルダ内のREADME.mdを参照してください。
- DrawableインターフェイスでSVG出力機能を追加

PaymentSystem（支払い）課題
- PaymentMethodインターフェイスで戦略パターンを実装
- CreditCard、BankTransfer、DigitalWalletを実装
- 支払い手数料の計算をdefaultメソッドで共通化

詳細はGitHubリポジトリのREADMEを参照してください。

次のステップ： 第8章「列挙型(Enums)」へ進みましょう。
