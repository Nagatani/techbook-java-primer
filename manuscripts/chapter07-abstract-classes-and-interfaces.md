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

本章で学んだ抽象クラスとインターフェイスの概念を実践的に活用する演習課題に取り組みましょう。

### 🎯 演習の目標
- 抽象クラス（abstract class）の概念と実装
- インターフェイス（interface）の設計と活用
- 抽象メソッドと具象メソッドの使い分け
- 多重継承の代替としてのインターフェイス
- デフォルトメソッドと静的メソッドの活用
- SOLID原則にもとづく抽象化設計の理解と実践

### 演習課題の難易度レベル

#### 🟢 基礎レベル（Basic）
- **目的**: 抽象クラスとインターフェイスの基本概念の確実な理解
- **所要時間**: 30-45分/課題
- **前提**: 本章の内容を理解していること
- **評価基準**: 
  - 抽象クラスの適切な設計と継承
  - インターフェイスの実装と多重実装の理解
  - ポリモーフィズムの活用
  - デフォルトメソッドの理解

#### 🟡 応用レベル（Applied）
- **目的**: デザインパターンと実践的な設計
- **所要時間**: 45-60分/課題
- **前提**: 基礎レベルを完了していること
- **評価基準**:
  - SOLID原則の適用
  - 戦略パターンやテンプレートメソッドパターンの実装
  - 実世界の問題への適用
  - コードの再利用性と拡張性

#### 🔴 発展レベル（Advanced）
- **目的**: 複雑なシステム設計と高度な抽象化
- **所要時間**: 60-90分/課題
- **前提**: 応用レベルを完了していること
- **評価基準**:
  - 複数のデザインパターンの組み合わせ
  - 大規模システムの設計
  - パフォーマンスと保守性の考慮
  - 実用的なフレームワーク設計

#### ⚫ 挑戦レベル（Challenge）
- **目的**: 総合的な設計力と実装力の実証
- **所要時間**: 90分以上
- **前提**: 発展レベル完了と高度な設計への意欲
- **評価基準**:
  - 革新的な設計アプローチ
  - 複雑な要求への対応
  - プロダクションレベルの品質
  - 拡張性と保守性の両立

---

## 🟢 基礎レベル課題（必須）

### 課題1: 図形の抽象クラス設計（Shape.java）

**学習目標：** 抽象クラスの定義、継承、ポリモーフィズム

**問題説明：**
図形を表す抽象クラスShapeを作成し、具体的な図形クラス（Circle、Rectangle）で実装してください。すべての図形は色を持ち、面積と周囲の長さを計算できます。

**要求仕様：**
1. 抽象クラスShape:
   - privateフィールド： `color`（String）
   - コンストラクタで色を初期化
   - 抽象メソッド： `calculateArea()`、`calculatePerimeter()`
   - 具象メソッド： `getColor()`、`displayInfo()`

2. Circleクラス（Shapeを継承）:
   - privateフィールド： `radius`（double）
   - 面積計算： π × 半径²
   - 周囲計算： 2 × π × 半径

3. Rectangleクラス（Shapeを継承）:
   - privateフィールド： `width`、`height`（double）
   - 面積計算： 幅 × 高さ
   - 周囲計算： 2 × （幅 + 高さ）

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

## 🟡 応用レベル課題（推奨）

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

## 🔴 発展レベル課題（挑戦）

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

## ⚫ 挑戦レベル課題（上級者向け）

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

## 🔗 実装環境

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

## ✅ 完了確認チェックリスト

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
