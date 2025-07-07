# 第7章 抽象クラスとインターフェイス

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

抽象クラスを使う際には、いくつかの重要なルールを理解しておく必要があります。まず最も基本的なルールとして、1つでも抽象メソッドを持つクラスは、必ず抽象クラスとして`abstract`修飾子を付けて宣言しなければなりません。これはJavaの言語仕様で定められた強制的なルールであり、コンパイラはこのルールに従わないコードをエラーとして扱います。

次に、抽象クラスは`new`キーワードを使ってインスタンスを生成できないという重要な制約があります。これは、抽象クラスが不完全な定義（抽象メソッド）を含んでいるからで、実装がないメソッドを持つオブジェクトを作成することは論理的に不可能だからです。

さらに、抽象クラスを継承した子クラスには、親クラスのすべての抽象メソッドをオーバーライドして具体的に実装する責任があります。もし1つでも実装しない抽象メソッドが残っている場合、その子クラスもまた抽象クラスとして宣言しなければなりません。このメカニズムにより、抽象メソッドの実装を強制し、設計上の契約を確実に守らせることができます。

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

本章で学んだ抽象クラスとインターフェイスの概念を実践的に活用する演習課題に取り組みましょう。

### 演習の目標
- 抽象クラスとインターフェイスの基本概念の習得
- デザインパターンの実践的な適用
- SOLID原則にもとづく設計

### 演習課題

#### 基礎レベル

**課題1: 図形の抽象化設計**

図形システムを抽象クラスとインターフェイスを使って実装してください。

**技術的背景：抽象化による拡張性の確保**

図形処理システムは、CAD、ゲーム開発、画像処理など多くの分野で必要とされる基本的なシステムです。その設計において抽象化が重要な理由：

- **新しい図形の追加が容易**：三角形、多角形、楕円など、新しい図形を追加する際に既存コードの変更が不要
- **共通処理の一元化**：色の管理、描画の基本処理などを抽象クラスに集約
- **ポリモーフィズムの活用**：異なる図形を統一的に扱うことで、処理の簡潔化

実際のシステムでの応用例：
- **グラフィックスライブラリ**：JavaのGraphics2DやSwingコンポーネントも同様の設計
- **ゲームエンジン**：すべてのゲームオブジェクトが共通の基底クラスから派生
- **CADシステム**：図形の面積計算、衝突判定などを統一的に処理

この演習では、オープン・クローズド原則（拡張に対して開いており、修正に対して閉じている）の実践的な適用方法を学びます。

**要求仕様：**

### 実装のスケルトンコード

演習を始めるための基本構造を以下に示します。このコードを出発点として、要求仕様を満たすように実装を完成させてください：

```java
// Shape.java (抽象クラス)
public abstract class Shape {
    // TODO: colorフィールドを定義（private String color）
    
    // TODO: コンストラクタを実装
    // - colorを初期化
    
    // TODO: getColor()メソッドを実装
    
    // TODO: 抽象メソッドを宣言
    // - public abstract double calculateArea();
    // - public abstract double calculatePerimeter();
}

// Drawable.java (インターフェイス)
public interface Drawable {
    // TODO: draw()メソッドを宣言
    // void draw();
}

// Circle.java (具象クラス)
public class Circle extends Shape implements Drawable {
    // TODO: radiusフィールドを定義（private double radius）
    
    // TODO: コンストラクタを実装
    // - super()でcolorを初期化
    // - radiusを初期化
    
    // TODO: calculateArea()をオーバーライド
    // - Math.PI * radius * radius
    
    // TODO: calculatePerimeter()をオーバーライド
    // - 2 * Math.PI * radius
    
    // TODO: draw()を実装
    // - System.out.println(getColor() + "い円を描画");
}

// Rectangle.java (具象クラス)
public class Rectangle extends Shape implements Drawable {
    // TODO: width, heightフィールドを定義
    
    // TODO: コンストラクタを実装
    
    // TODO: 必要なメソッドをすべて実装
}

// ShapeTest.java (テストクラス)
public class ShapeTest {
    public static void main(String[] args) {
        // TODO: Shapeの配列を作成
        Shape[] shapes = new Shape[2];
        
        // TODO: CircleとRectangleのインスタンスを作成
        // shapes[0] = new Circle("赤", 5.0);
        // shapes[1] = new Rectangle("青", 8.0, 3.0);
        
        // TODO: 各図形の情報を表示
        // - 色、面積、周囲を表示
        // - Drawableにキャストしてdraw()を呼び出し
    }
}
```

**期待される出力例：**
```
赤い円: 面積=78.54, 周囲=31.42
青い長方形: 面積=24.0, 周囲=20.0
```

**ヒント：** 本章で学んだ抽象クラスの共通実装とインターフェイスの多重実装を活用してください。

3. Rectangleクラス（Shapeを継承）:
   - privateフィールド： `width`、`height`（double）
   - 面積計算： 幅 × 高さ
   - 周囲計算： 2 ×（幅 + 高さ）

**実行例：**
```
=== 図形抽象クラステスト ===
円:
色: 赤
半径: 5.0
面積: 78.54
周囲: 31.42

長方形:
色: 青
幅: 8.0, 高さ: 3.0
面積: 24.0
周囲: 22.0

全図形の合計面積: 102.54
```

**実装ヒント：**
```java
public abstract class Shape {
    private String color;
    
    public Shape(String color) {
        this.color = color;
    }
    
    // 抽象メソッド（サブクラスで実装必須）
    public abstract double calculateArea();
    public abstract double calculatePerimeter();
    
    // 具象メソッド（共通実装）
    public String getColor() {
        return color;
    }
    
    public void displayInfo() {
        System.out.println("色: " + color);
        System.out.println("面積: " + String.format("%.2f", calculateArea()));
        System.out.println("周囲: " + String.format("%.2f", calculatePerimeter()));
    }
}

public class Circle extends Shape {
    private double radius;
    
    public Circle(String color, double radius) {
        super(color);  // 親クラスのコンストラクタを呼び出す
        this.radius = radius;
    }
    
    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
    
    @Override
    public double calculatePerimeter() {
        return 2 * Math.PI * radius;
    }
}
```

### 課題2: 動物の行動インターフェイス設計

**学習目標：** インターフェイスの定義、多重実装、デフォルトメソッド

**問題説明：**
動物の行動を表すインターフェイス（Flyable、Swimmable、Walkable）を作成し、さまざまな動物クラスで実装してください。一部の動物は複数の行動が可能です。

**技術的背景：インターフェイスによる多重継承の実現**

Javaがクラスの多重継承を禁止している理由は、ダイヤモンド継承問題を回避するためです。しかし、現実世界では一つのオブジェクトが複数の「能力」を持つことは一般的です：

- **アヒル**：飛べる、泳げる、歩ける
- **ペンギン**：泳げる、歩けるが、飛べない
- **飛行機**：飛べるが、歩けない（動物ではないが同じ「飛ぶ」能力を持つ）

インターフェイスを使った設計の利点：
- **柔軟な能力の組み合わせ**：必要な能力だけを選択的に実装
- **異なる種類のオブジェクトの統一的な扱い**：「飛べるもの」として鳥も飛行機も扱える
- **将来の拡張性**：新しい能力（Climbable、Diveable等）の追加が容易

実際のシステムでの応用：
- **ゲーム開発**：キャラクターの能力システム（攻撃可能、防御可能、魔法使用可能等）
- **ロボット制御**：ロボットの機能モジュール（移動可能、把持可能、認識可能等）
- **プラグインシステム**：プラグインが提供する機能の定義

この演習では、インターフェイス分離原則（必要な機能だけを公開する）の実践的な適用を学びます。

**要求仕様：**
1. インターフェイス定義：
   - Flyable: `fly()`メソッド、デフォルトメソッド`getAltitude()`
   - Swimmable: `swim()`メソッド、デフォルトメソッド`getDiveDepth()`
   - Walkable: `walk()`メソッド

2. 動物クラス：
   - Bird: Flyable、Walkableを実装
   - Fish: Swimmableを実装
   - Duck: Flyable、Swimmable、Walkableを実装（すべて実装）

**実行例：**
```
=== 動物行動インターフェイステスト ===
鳥の行動:
空を飛んでいます（高度: 100m）
地面を歩いています

魚の行動:
水中を泳いでいます（深度: 10m）

アヒルの行動:
空を飛んでいます（高度: 50m）
水中を泳いでいます（深度: 2m）
地面を歩いています

移動可能な動物の総数: 3
```

**実装ヒント：**
```java
public interface Flyable {
    void fly();
    
    // デフォルトメソッド
    default int getAltitude() {
        return 100;  // デフォルトの高度
    }
}

public interface Swimmable {
    void swim();
    
    default int getDiveDepth() {
        return 10;  // デフォルトの深度
    }
}

public class Duck implements Flyable, Swimmable, Walkable {
    @Override
    public void fly() {
        System.out.println("空を飛んでいます（高度: " + getAltitude() + "m）");
    }
    
    @Override
    public void swim() {
        System.out.println("水中を泳いでいます（深度: " + getDiveDepth() + "m）");
    }
    
    @Override
    public void walk() {
        System.out.println("地面を歩いています");
    }
    
    // 必要に応じてデフォルトメソッドをオーバーライド
    @Override
    public int getAltitude() {
        return 50;  // アヒルは低く飛ぶ
    }
}
```

### 課題3: 支払い方法の戦略パターン（PaymentMethod.java）

**学習目標：** インターフェイスによる戦略パターン、統一的な処理

**問題説明：**
支払い方法を表すインターフェイスを作成し、異なる支払い方法（クレジットカード、デビットカード、現金）を実装してください。各支払い方法には手数料計算があります。

**技術的背景：戦略パターンによる処理の切り替え**

現代の電子商取引システムでは、多様な支払い方法への対応が必須です。しかし、支払い方法ごとに異なる処理を条件分岐で実装すると：

```java
// アンチパターン：条件分岐の肥大化
if (paymentType.equals("credit")) {
    // クレジットカード処理
} else if (paymentType.equals("debit")) {
    // デビットカード処理
} else if (paymentType.equals("cash")) {
    // 現金処理
} else if (paymentType.equals("qr")) {  // 新しい支払い方法追加時
    // QRコード決済処理（既存コードの修正が必要）
}
```

戦略パターンを使うことで：
- **開放閉鎖原則の実現**：新しい支払い方法の追加時に既存コードの修正が不要
- **処理の一元化**：支払い処理の流れを統一的に管理
- **テストの容易性**：各支払い方法を独立してテスト可能

実際のシステムでの応用：
- **ECサイト**：クレジット、コンビニ決済、電子マネー、仮想通貨など
- **POSシステム**：現金、クレジット、QRコード、ポイント決済など
- **サブスクリプションサービス**：月額、年額、従量課金などの料金体系

この演習では、実際の決済システムで使われる設計パターンの基本を学びます。

**要求仕様：**
1. PaymentMethodインターフェイス：
   - `processPayment(double amount)`
   - `calculateFee(double amount)`
   - デフォルトメソッド`getPaymentType()`

2. 実装クラス：
   - CreditCard: 手数料3%
   - DebitCard: 手数料2%
   - Cash: 手数料0%

3. PaymentProcessorクラス：
   - 支払い方法を受け取り、統一的に処理

**実行例：**
```
=== 支払い方法戦略パターンテスト ===
クレジットカード支払い:
支払い金額: 10,000円
手数料: 300円
合計: 10,300円
支払い完了

デビットカード支払い:
支払い金額: 5,000円
手数料: 100円
合計: 5,100円
支払い完了

現金支払い:
支払い金額: 3,000円
手数料: 0円
合計: 3,000円
支払い完了

今日の総売上: 18,400円
```

### 課題4: 通知システムの複合インターフェイス（Notifiable.java）

**学習目標：** 複数インターフェイスの実装、機能の組み合わせ

**問題説明：**
通知とログ機能を持つシステムを作成してください。一部のクラスは両方の機能を持ち、一部は片方のみを実装します。

**技術的背景：横断的関心事の分離**

エンタープライズシステムでは、「通知」や「ログ記録」といった機能は、多くのコンポーネントで必要とされる横断的関心事（Cross-cutting Concerns）です：

- **通知機能**：ユーザーへの情報伝達（メール、SMS、プッシュ通知など）
- **ログ機能**：システムの動作記録、監査証跡、デバッグ情報

これらを適切に設計することの重要性：
- **関心事の分離**：通知とログを独立したインターフェイスとして定義
- **選択的な実装**：必要な機能のみを実装（すべてのクラスがログを必要とするわけではない）
- **将来の拡張性**：新しい通知手段（Slack、Teams等）の追加が容易

実際のシステムでの応用：
- **マイクロサービス**：各サービスが必要な機能（通知、ログ、メトリクス等）を選択的に実装
- **監視システム**：アラート通知とログ収集を組み合わせた統合監視
- **ワークフローシステム**：処理の各段階で通知とログを適切に記録

この演習では、アスペクト指向プログラミングの基礎となる概念を学びます。

**要求仕様：**
1. インターフェイス：
   - Notifiable: `sendNotification(String message)`
   - Loggable: `log(String message)`、デフォルトメソッド`getTimestamp()`

2. 実装クラス：
   - EmailNotifier: Notifiable、Loggableを実装
   - SMSNotifier: Notifiable、Loggableを実装
   - SystemLogger: Loggableのみ実装

**実行例：**
```
=== 通知システム複合インターフェイステスト ===
Emailによる通知:
[EMAIL] メッセージを送信しました: システム開始
[LOG] 2024-07-04 10:30:00 - Email送信: システム開始

SMSによる通知:
[SMS] メッセージを送信しました: エラー発生
[LOG] 2024-07-04 10:30:01 - SMS送信: エラー発生

システムログ:
[LOG] 2024-07-04 10:30:02 - システム情報: 処理完了

全通知件数: 3件
```

---

## 応用レベル課題（推奨）

### 課題1: ゲームキャラクタシステム

**学習目標：** 抽象クラスとインターフェイスの組み合わせ、テンプレートメソッドパターン

**問題説明：**
ゲームキャラクタシステムを設計してください。全キャラクタは基本的な属性を持ち、種類によって異なる行動や能力を持ちます。

**要求仕様：**
1. GameCharacter抽象クラス：
   - フィールド： name、health、attackPower
   - 抽象メソッド： attack()、defend()
   - テンプレートメソッド： performTurn()

2. Drawableインターフェイス：
   - draw()メソッド
   - getPosition()メソッド

3. 具象クラス：
   - Player: GameCharacterを継承、Drawableを実装
   - Enemy: GameCharacterを継承、Drawableを実装
   - 特殊能力の実装

### 課題2: 支払いシステムの拡張

**学習目標：** インターフェイス分離原則、実世界のビジネスロジック

**問題説明：**
給与計算システムを作成してください。従業員と請求書の両方を統一的に処理できるようにします。

**要求仕様：**
1. Payableインターフェイス：
   - getPaymentAmount()
   - getPaymentName()
   - デフォルトメソッドgetPaymentDescription()

2. 実装クラス：
   - Employee: 月給制、時給制の従業員
   - Invoice: 商品の請求書
   - Contractor: 契約社員（特別な計算ロジック）

### 課題3: ソート可能オブジェクト

**学翕目標：** 複数基準でのソート、列挙型の活用

**問題説明：**
学生情報を管理し、複数の基準でソートできるシステムを作成してください。

**要求仕様：**
1. Sorテーブルインターフェイス：
   - compareTo()メソッド
   - setSortCriteria()メソッド

2. SortCriteria列挙型：
   - NAME、SCORE、STUDENT_ID

3. Studentクラス：
   - SorテーブルとComparableの両方を実装
   - 名前、点数、学籍番号の管理

---

## 発展レベル課題（挑戦）

### 課題1: プラグインシステムの設計

**学習目標：** 拡張可能なアーキテクチャ、高度な抽象化

**問題説明：**
プラグイン可能なアプリケーションフレームワークを設計してください。新しい機能を既存コードを変更せずに追加できるようにします。

**要求仕様：**
1. Pluginインターフェイス階層：
   - Plugin: 基本インターフェイス
   - Configurable: 設定可能なプラグイン
   - Lifecycle: ライフサイクル管理

2. PluginManager:
   - プラグインの登録、実行、管理
   - 依存関係の解決
   - イベントシステム

### 課題2: データアクセス層の抽象化

**学習目標：** リポジトリパターン、データベース抽象化

**問題説明：**
異なるデータソース（ファイル、データベース、メモリ）に対して統一的なアクセスを提供するシステムを作成してください。

**要求仕様：**
1. Repository階層：
   - Repository<T>: ジェネリックインターフェイス
   - CrudRepository<T>: CRUD操作
   - QueryableRepository<T>: クエリ機能

2. 実装：
   - FileRepository
   - DatabaseRepository
   - InMemoryRepository

---

## 挑戦レベル課題（上級者向け）

### 統合プロジェクト: ゲームエンジンフレームワーク

**学習目標：** 総合的な設計力、実用的なフレームワーク構築

**問題説明：**
2Dゲームを作成するための基本的なゲームエンジンフレームワークを設計してください。

**要求仕様：**

1. **コアシステム:**
   - GameObject抽象クラス
   - Componentシステム（コンポーネント指向設計）
   - Scene管理
   - GameLoop（テンプレートメソッド）

2. **インターフェイス階層:**
   - Renderable: 描画可能
   - Updateable: 更新可能
   - Collidable: 衝突判定可能
   - Interactive: インタラクション可能

3. **具体的な実装:**
   - Sprite: 画像表示
   - AnimatedSprite: アニメーション
   - PhysicsBody: 物理演算
   - InputHandler: 入力処理

4. **高度な機能:**
   - イベントシステム
   - リソース管理
   - ステート管理
   - 拡張可能なアーキテクチャ

**評価ポイント：**
- SOLID原則の適用
- デザインパターンの活用
- 拡張性と保守性
- パフォーマンスの考慮
- ドキュメントとテスト

---

## 実装環境

演習課題の詳細な実装テンプレート、テストコード、解答例は以下のディレクトリを参照してください：

```
exercises/chapter07/
├── basic/          # 基礎レベル課題
│   ├── README.md   # 詳細な課題説明
│   ├── Shape.java
│   ├── Circle.java
│   ├── Rectangle.java
│   └── ShapeTest.java
├── advanced/       # 応用・発展レベル課題
│   └── README.md
├── challenge/      # 挑戦レベル課題
└── solutions/      # 解答例（実装完了後に参照）
```

---

## より深い理解のために

本章で学んだインターフェイスの基本概念をさらに深く理解したい方は、付録B.12「Java 8以降のインターフェイス進化と設計パターン」を参照してください。この付録では以下の高度なトピックを扱います：

- **インターフェイスの歴史的進化**: Java 7からJava 9への機能拡張
- **defaultメソッドの設計思想**: 後方互換性とテンプレートメソッドパターン
- **多重継承問題の解決**: ダイヤモンド問題と優先順位規則
- **高度な設計パターン**: Mixinパターン、トレイトパターン、ISPの実践
- **関数型インターフェイスとの統合**: defaultメソッドによる関数合成

これらの知識は、モダンなJavaアプリケーションの設計において重要な役割を果たします。

---

## 完了確認チェックリスト

### 基礎レベル
- [ ] Shape抽象クラスと図形クラスが正しく実装されている
- [ ] 動物行動インターフェイスで多重実装ができている
- [ ] 支払い方法で戦略パターンが実装されている
- [ ] 通知システムで複合インターフェイスが活用されている

### 技術要素
- [ ] 抽象クラスとインターフェイスの使い分けができている
- [ ] ポリモーフィズムを活用した統一的な処理ができている
- [ ] デフォルトメソッドの意味と使い方を理解している
- [ ] SOLID原則を意識した設計ができている

### 応用・発展レベル
- [ ] 複数のデザインパターンを適切に適用できている
- [ ] 実世界の問題を抽象化して設計できている
- [ ] 拡張性と保守性を考慮した設計ができている
- [ ] 大規模なシステムアーキテクチャを構築できている
