# 第7章 抽象クラスとインターフェイス

## 章末演習

本章で学んだ抽象クラスとインターフェイスの概念を活用して、実践的な練習課題に取り組みましょう。

### 演習の目標
- 抽象クラス（abstract class）の概念と実装
- インターフェイス（interface）の設計と活用
- 抽象メソッドと具象メソッドの使い分け
- 多重継承の代替としてのインターフェイス
- デフォルトメソッドと静的メソッドの活用
- SOLID原則にもとづく抽象化設計の理解と実践

### 📁 課題の場所
演習課題は `exercises/chapter07/` ディレクトリに用意されています：

```
exercises/chapter07/
├── basic/          # 基本課題（必須）
│   ├── README.md   # 課題の詳細説明
│   ├── Shape.java  # 課題1: 図形の抽象クラス設計
│   ├── Circle.java
│   ├── Rectangle.java
│   ├── ShapeTest.java
│   ├── Flyable.java # 課題2: 動物の行動インターフェイス
│   ├── Swimmable.java
│   ├── Walkable.java
│   ├── Bird.java
│   ├── Fish.java
│   ├── Duck.java
│   ├── AnimalInterfaceTest.java
│   ├── PaymentMethod.java # 課題3: 支払い方法の戦略パターン
│   ├── CreditCard.java
│   ├── DebitCard.java
│   ├── Cash.java
│   ├── PaymentProcessor.java
│   ├── PaymentTest.java
│   ├── Notifiable.java # 課題4: 通知システムの複合インターフェイス
│   ├── Loggable.java
│   ├── EmailNotifier.java
│   ├── SMSNotifier.java
│   ├── SystemLogger.java
│   └── NotificationTest.java
├── advanced/       # 発展課題（推奨）
└── challenge/      # 挑戦課題（上級者向け）
```

### 推奨する学習の進め方

1. **基本課題**から順番に取り組む
2. 各課題のREADME.mdで詳細を確認
3. ToDoコメントを参考に実装
4. 抽象クラスとインターフェイスの使い分けを理解する
5. デフォルトメソッドと多重実装を活用する

基本課題が完了したら、`advanced/`の発展課題でより複雑な抽象化設計に挑戦してみましょう！

## 本章の学習目標

### 前提知識
**必須前提**：
- 第6章までのオブジェクト指向概念の習得
- クラスとインスタンスの基本的な理解
- 継承とポリモーフィズムの基本概念

**設計経験前提**：
- 基本的なクラス設計と実装の経験
- オブジェクトの状態変更に関する課題の理解

### 学習目標
**知識理解目標**：
- 不変性（Immutability）の概念と利点
- finalキーワードのさまざまな用途と効果
- 不変オブジェクトとミュータブルオブジェクトの違い
- スレッドセーフティにおける不変性の重要性

**技能習得目標**：
- finalフィールドを使った不変オブジェクトの設計・実装
- final変数、final引数の適切な使用
- finalメソッドとfinalクラスの効果的な活用
- 不変コレクションの作成と使用

**設計能力目標**：
- 安全で予測可能なオブジェクト設計
- 副作用を最小化したメソッド設計
- 不変性を活用した堅牢なAPIの設計

**到達レベルの指標**：
- finalキーワードを適切に使い分けた安全なクラスが設計できる
- 不変オブジェクトパターンを使った堅牢な設計ができる
- ミュータブル性と不変性のトレードオフを理解して適切に選択できる
- スレッドセーフな不変オブジェクトが実装できる

---

## 7.1 抽象化の必要性

これまでの章で、継承を使うとクラスの機能を再利用できることを学びました。しかし、親クラスの段階では、具体的な処理内容を決められない場合があります。

たとえば、「図形」クラスを考えてみましょう。すべての図形（円、長方形、三角形など）には「面積を計算する」という共通の機能が考えられます。しかし、親クラスである「図形」の段階で、面積の具体的な計算方法を実装することはできません。円の面積は「半径 × 半径 × π」ですが、長方形は「幅 × 高さ」であり、計算方法がまったく異なるからです。

このように、**共通の機能（概念）は存在するが、その具体的な実装は子クラスに任せたい**、という場合に利用するのが**抽象クラス**と**インターフェイス**です。これらは、プログラムの「抽象度」を上げ、より柔軟で拡張性の高い設計を実現するための重要なしくみです。

## 7.2 抽象クラスと抽象メソッド

**抽象クラス (Abstract Class)** とは、不完全な部分を持つ、インスタンス化できないクラスのことです。その不完全な部分が**抽象メソッド**です。

**抽象メソッド (Abstract Method)** とは、**実装（メソッドの中身の処理）を持たない**メソッドのことです。`abstract`修飾子を付け、メソッドの定義（名前、引数、戻り値の型）だけを宣言します。

### 抽象クラスのルール

1.  1つでも抽象メソッドを持つクラスは、必ず**抽象クラス**として`abstract`修飾子を付けて宣言しなければならない。
2.  抽象クラスは、**`new`を使ってインスタンスを生成できない**。
3.  抽象クラスを継承した子クラスは、親クラスの**すべての抽象メソッドをオーバーライドして実装しなければならない**。もし1つでも実装しない抽象メソッドがある場合、その子クラスもまた抽象クラスでなければならない。

### 実践例：`Shape`抽象クラス

「図形」の例を抽象クラスで実装してみましょう。

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

## 7.5 `default`メソッドと`static`メソッド

Java 8から、インターフェイスに実装を持つメソッド（`default`メソッドと`static`メソッド）を追加できるようになり、より柔軟性が増しました。

### `default`メソッド

`default`メソッドは、インターフェイス内に**デフォルトの実装**を持つことができるメソッドです。
このメソッドの主な目的は、**既存のインターフェイスに新しいメソッドを追加しつつ、すでにある実装クラスがコンパイルエラーになるのを防ぐ**ことです。

```java
public interface Loggable {
    void log(String message);

    // defaultメソッド
    default void logError(String errorMessage) {
        System.err.println("【ERROR】" + errorMessage);
    }
}

public class MyLogger implements Loggable {
    @Override
    public void log(String message) {
        System.out.println(message);
    }
    // logErrorは実装しなくても、デフォルト実装が使われる
}
```

### `static`メソッド

インターフェイスに`static`メソッドを定義することもできます。これは、そのインターフェイスに関連するユーティリティメソッドなどを提供するのに便利です。

```java
public interface Calculable {
    int calculate(int x);

    // staticメソッド
    static int triple(int num) {
        return num * 3;
    }
}

// 呼び出し方
int result = Calculable.triple(5); // 15
```

## 7.6 章末演習

### 演習：データリポジトリの設計

**目的:** 抽象クラスとインターフェイスを組み合わせ、柔軟なデータアクセス層を設計する。

**シナリオ:**
さまざまな種類のデータを保存・読み込みするシステムを考えます。データには必ずIDがあり、保存・読み込みの操作は共通ですが、保存形式（ファイル、データベースなど）は異なります。

**手順:**

1.  **`Storable`インターフェイスの作成**:
    *   `Storable.java`というインターフェイスを作成します。
    *   `void save()` と `void load()` という2つの抽象メソッドを定義します。

2.  **`BaseRepository`抽象クラスの作成**:
    *   `BaseRepository.java`という**抽象クラス**を作成し、`Storable`インターフェイスを**実装**します。
    *   `protected String id;` というフィールドを持ちます。
    *   IDを初期化するコンストラクタを作成します。
    *   `displayId()`という、IDを表示する具象メソッドを実装します。
    *   `save()`と`load()`は、この段階では実装できないので、**抽象メソッド**のままにしておきます。

3.  **`FileRepository`具象クラスの作成**:
    *   `BaseRepository`を**継承**した`FileRepository.java`を作成します。
    *   コンストラクタで親のコンストラクタを呼び出します。
    *   `save()`メソッドをオーバーライドし、「(id)をファイルに保存しました」と表示する処理を実装します。
    *   `load()`メソッドをオーバーライドし、「(id)をファイルから読み込みました」と表示する処理を実装します。

4.  **`DatabaseRepository`具象クラスの作成**:
    *   同様に、`DatabaseRepository.java`を作成します。
    *   `save()`では「データベースに保存」、`load()`では「データベースから読み込み」と表示するように実装します。

5.  **`Main`実行クラスの作成**:
    *   `main`メソッドを持つ`Main.java`を作成します。
    *   `Storable`型の配列を作成し、`FileRepository`と`DatabaseRepository`のインスタンスを格納します。
    *   ループ処理で、各要素の`save()`と`load()`メソッドを呼び出し、ポリモーフィズムによってそれぞれの実装が呼び出されることを確認してください。
