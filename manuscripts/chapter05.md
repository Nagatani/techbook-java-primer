# 第5章 継承とポリモーフィズム

## はじめに：コードの再利用とソフトウェアの進化を支える継承の概念

前章では、オブジェクト指向プログラミングの基礎であるクラスとオブジェクトについて学習しました。この章では、オブジェクト指向プログラミングの真の威力を発揮する「継承（Inheritance）」と「ポリモーフィズム（Polymorphism）」という概念について詳しく学習します。

これらの概念は、単なるプログラミング技法ではありません。ソフトウェア開発における根本的な課題である「コードの再利用性」「保守性」「拡張性」を解決するための、長年にわたる研究と実践の成果として生まれた重要な仕組みです。

### ソフトウェア開発における継承の必要性

ソフトウェア開発の歴史において、最も重要な課題の一つは「車輪の再発明」を避けることでした。これは、既に存在する機能や仕組みを一から作り直すのではなく、既存のものを活用してより効率的に開発を進めるという考え方です。

1960年代から1970年代にかけて、プログラムの規模が拡大するにつれて、開発者たちは似たような処理を何度も書く必要性に直面しました。例えば、図形描画プログラムを考えてみましょう。円、四角形、三角形など、異なる図形を扱う場合でも、「位置を持つ」「色を持つ」「面積を計算する」「描画する」といった共通の特性があります。従来の手続き型プログラミングでは、これらの共通部分であっても、図形の種類ごとに別々のコードを書く必要がありました。

この問題は、単なる開発効率の低下だけでなく、保守性の著しい悪化をもたらしました。共通の処理に修正が必要になった場合、すべての類似コードを個別に修正する必要があり、修正漏れやバグの温床となっていました。さらに、新しい図形を追加する際も、既存のコードを参考にしながら、ほぼ全体を一から作り直す必要がありました。

### 生物学的継承からソフトウェア継承へのアナロジー

継承という概念は、生物学の世界からインスピレーションを得て命名されました。生物の世界では、親から子へと遺伝的特性が受け継がれ、子は親の特性を基本として持ちながら、環境に適応するための新しい特性を獲得していきます。この自然界の仕組みをソフトウェア開発に応用したのが、オブジェクト指向プログラミングにおける継承の概念です。

ソフトウェアの継承では、「親クラス（スーパークラス）」が持つ特性を「子クラス（サブクラス）」が自動的に受け継ぎます。そして、子クラスは受け継いだ特性をそのまま使用することも、必要に応じて変更（オーバーライド）することも、新しい特性を追加することも可能です。この仕組みにより、共通部分の重複を排除し、差分のみを実装することで効率的な開発が実現されます。

### 継承がもたらす革命的な変化

継承の導入は、ソフトウェア開発に革命的な変化をもたらしました。その主な効果は以下の通りです：

**コードの劇的な削減**：共通する機能を一箇所にまとめることで、コード量を大幅に削減できます。これは、開発時間の短縮だけでなく、テスト工数の削減、バグの減少にも直結します。

**階層的なソフトウェア設計**：現実世界の分類体系をそのままソフトウェアの構造として表現できるようになりました。例えば、「動物」→「哺乳類」→「犬」という階層構造を、そのままクラスの継承関係として実装できます。

**段階的な機能拡張**：既存のクラスを継承して新しいクラスを作ることで、既存の機能を損なうことなく段階的にシステムを拡張できます。これにより、大規模システムの段階的開発が可能になりました。

**プラットフォームの構築**：基本的な機能を提供する基底クラス群を「プラットフォーム」として整備し、その上で様々なアプリケーションを開発するという手法が確立されました。これは現代のフレームワーク開発の基礎となっています。

### ポリモーフィズムの概念とその重要性

継承と密接に関連する概念として、「ポリモーフィズム（多態性）」があります。ポリモーフィズムは、ギリシャ語の「多くの」（poly）と「形」（morph）を組み合わせた言葉で、「同じインターフェイスで異なる動作を実現する」仕組みを指します。

ポリモーフィズムの威力は、「統一されたインターフェイスによる多様な実装」にあります。例えば、「描画する」という同じ命令でも、円なら円として、四角形なら四角形として、それぞれ適切に描画されます。プログラムを書く側は、個々の図形の種類を意識することなく、統一された方法ですべての図形を扱えるのです。

この仕組みにより、新しい図形クラスを追加する際も、既存のコードを変更することなく、システムを拡張できます。これは「開放・閉鎖の原則」として知られ、オブジェクト指向設計の重要な指針となっています。システムは新しい機能に対して開放的でありながら、既存のコードに対しては閉鎖的（変更不要）であるべきという考え方です。

### 現代的なソフトウェア開発における継承の位置づけ

現代のソフトウェア開発では、継承は適切に使用されることで大きな威力を発揮しますが、同時に「継承よりも合成を選べ」という設計原則も重視されています。これは、継承を乱用すると、クラス間の結合度が高くなり、かえって保守性が悪化する場合があるためです。

適切な継承の使用には、以下の点が重要です：

**「is-a」関係の厳密な適用**：継承は、真の「〜は〜の一種である」関係にのみ使用すべきです。単にコードを再利用したいだけの場合は、合成やインターフェイスの実装を検討すべきです。

**リスコフの置換原則の遵守**：子クラスのオブジェクトは、親クラスのオブジェクトと置き換え可能でなければなりません。これにより、ポリモーフィズムが正しく機能します。

**適切な抽象化レベルの選択**：継承階層は、適切な抽象化レベルで構築されるべきです。過度に細かい階層は複雑性を増し、過度に粗い階層は再利用性を損ないます。

この章では、これらの重要な概念を、Javaの具体的な構文と実践的な例を通じて学習していきます。単に継承の書き方を覚えるのではなく、なぜ継承が必要なのか、どのような場面で効果的なのか、どのような注意点があるのかを理解することで、優れたオブジェクト指向設計の基礎を身につけていきましょう。

## 5.1 継承とは何か？

### 継承の基本概念

継承とは、「受け継ぐこと」という意味通り、オブジェクトが持つフィールドやメソッドなどを受け継ぐことができる仕組みです。受け継いだ機能は、そのまま利用することもできますし、上書きして違う機能を提供することもできます。

継承は、オブジェクト指向プログラミングにおける基本的なメカニズムの1つです。これは、既存のクラス（親クラス、スーパークラス）を基盤として、新しいクラス（子クラス、サブクラス）を作成することを可能にします。新しいクラスは、基盤となるクラスの非privateなメンバー（フィールドとメソッド）を自動的に受け継ぎます。

### is-a関係

継承の本質は、「**is-a**」（〜は〜の一種である）関係にあります。サブクラスは、スーパークラスの**特殊化された**バージョンと考えることができます。

たとえば、ECサイトにおいて「書籍 (Book)」は多種多様な「商品 (Product)」の中の一種です。したがって、`Book`クラスは`Product`クラスを継承することで、商品が共通して持つ特性（商品ID、名称、価格、税込み価格計算など）を受け継ぎつつ、書籍固有の特性（著者名、出版社、ISBNなど）を追加できます。

### 理解のためのアナロジー

継承を理解するために、ECサイトの商品カテゴリの階層構造を考えてみましょう。「書籍 (Book)」は「商品 (Product)」の一種であり、「家電製品 (Electronics)」も「商品 (Product)」の一種です。どちらも商品としての共通の特性（価格を持つ、詳細情報を表示するなど）を継承しますが、それぞれ固有の特性（書籍なら著者名や出版社、家電製品ならメーカー名や保証期間など）を追加します。

### 主要な用語

継承関係において、基盤となるクラス、つまり継承される側のクラスを**スーパークラス (Superclass)** または親クラス、基底クラスと呼びます。

一方、スーパークラスから特性を受け継ぐクラスを**サブクラス (Subclass)** または子クラス、派生クラスと呼びます。

サブクラスはスーパークラスを**拡張 (extends)** すると表現され、スーパークラスの特性を継承し、さらに独自の特性を追加したり、継承した振る舞いを変更したりできます。

## 5.2 Javaにおける継承の実装

### extendsキーワード

Javaでクラス間の継承関係を確立するには、`extends`キーワードを使用します。基本的な構文は以下の通りです。

```java
// スーパークラス（親）
class SuperclassName {
    // フィールドとメソッド
}

// サブクラス（子）
class SubclassName extends SuperclassName {
    // 追加のフィールドやメソッド、またはメソッドのオーバーライド
}
```

この宣言により、`SubclassName`は`SuperclassName`の非privateなメンバーへアクセスできるようになります。

### 具体的なコード例：ProductとBook

具体的な例を見てみましょう。ECサイトの商品を表す`Product`クラスをスーパークラスとし、その一種である`Book`クラスをサブクラスとして定義します。

```java
// スーパークラス（商品全般）
class Product {
    protected String productId;  // 商品ID（サブクラスからもアクセスできるようprotected）
    protected String name;       // 商品名
    protected int price;         // 価格

    // コンストラクタ
    public Product(String productId, String name, int price) {
        this.productId = productId;
        this.name = name;
        this.price = price;
    }

    // 価格に消費税を加算するメソッド（共通の振る舞い）
    public int getPriceWithTax() {
        return (int) (this.price * 1.10); // 消費税10%として計算
    }

    // 商品情報を表示するメソッド（基本的な表示）
    public void displayDetails() {
        System.out.println("商品ID: " + this.productId);
        System.out.println("商品名: " + this.name);
        System.out.println("価格（税抜）: " + this.price + "円");
        System.out.println("価格（税込）: " + getPriceWithTax() + "円");
    }

    // getter メソッド
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public int getPrice() { return price; }
}

// サブクラス（書籍）
class Book extends Product {
    private String author;     // 著者名（Book固有）
    private String publisher;  // 出版社（Book固有）
    private String isbn;       // ISBN（Book固有）

    // コンストラクタ
    public Book(String productId, String name, int price, String author, String publisher, String isbn) {
        // スーパークラスのコンストラクタを呼び出し、共通のフィールドを初期化
        super(productId, name, price);
        this.author = author;
        this.publisher = publisher;
        this.isbn = isbn;
    }

    // Book固有の情報を追加して表示するように、スーパークラスのメソッドをオーバーライド
    @Override
    public void displayDetails() {
        super.displayDetails(); // まずスーパークラスの表示処理を呼び出す
        System.out.println("著者: " + this.author);
        System.out.println("出版社: " + this.publisher);
        System.out.println("ISBN: " + this.isbn);
    }

    // Book固有のメソッド
    public String getAuthor() { return this.author; }
    public String getPublisher() { return this.publisher; }
    public String getIsbn() { return this.isbn; }

    // 書籍の詳細情報を文字列として取得
    public String getBookInfo() {
        return String.format("『%s』 著者: %s, 出版社: %s", name, author, publisher);
    }
}
```

### 継承の実行例

```java
public class InheritanceExample {
    public static void main(String[] args) {
        // Bookオブジェクトを作成
        Book myBook = new Book("B001", "Javaプログラミング実践", 3200, 
                               "佐々木一郎", "技術書典", "978-4-123456-78-9");

        // スーパークラスProductから継承したメソッドを使用
        System.out.println("商品名: " + myBook.getName());
        System.out.println("税込価格: " + myBook.getPriceWithTax() + "円");
        
        System.out.println("--- 詳細情報 ---");
        // オーバーライドされたdisplayDetailsメソッドを呼び出し
        myBook.displayDetails();
        
        System.out.println("--- 書籍固有情報 ---");
        // サブクラスBook固有のメソッドを呼び出し
        System.out.println(myBook.getBookInfo());
        System.out.println("この本の著者は " + myBook.getAuthor() + " です。");
    }
}
```

**実行結果：**
```
商品名: Javaプログラミング実践
税込価格: 3520円
--- 詳細情報 ---
商品ID: B001
商品名: Javaプログラミング実践
価格（税抜）: 3200円
価格（税込）: 3520円
著者: 佐々木一郎
出版社: 技術書典
ISBN: 978-4-123456-78-9
--- 書籍固有情報 ---
『Javaプログラミング実践』 著者: 佐々木一郎, 出版社: 技術書典
この本の著者は 佐々木一郎 です。
```

## 5.3 何が継承されるか？

### 継承されるメンバー

* スーパークラスの`public`および`protected`なメンバー（フィールドとメソッド）はサブクラスに継承され、サブクラスから直接アクセスできます
* スーパークラスの`private`なメンバーは、サブクラスのオブジェクト内に存在はしますが、サブクラスから直接アクセスすることは**できません**。これは、カプセル化の原則を維持するためです
* アクセス修飾子なし（デフォルト、パッケージプライベート）のメンバーは、同じパッケージ内のサブクラスにのみ継承されます
* 重要な点として、**コンストラクタは継承されません**。コンストラクタはクラス固有の初期化処理であり、そのまま引き継がれるものではありません

### アクセス修飾子と継承

```java
class Parent {
    public String publicField = "public";        // 継承される、どこからでもアクセス可能
    protected String protectedField = "protected"; // 継承される、サブクラスからアクセス可能
    String packageField = "package";            // 同じパッケージなら継承される
    private String privateField = "private";    // 継承されるが、サブクラスからアクセス不可

    public void publicMethod() { }        // 継承される
    protected void protectedMethod() { }  // 継承される
    void packageMethod() { }             // 同じパッケージなら継承される
    private void privateMethod() { }     // 継承されるが、サブクラスからアクセス不可
}

class Child extends Parent {
    public void testAccess() {
        System.out.println(publicField);    // OK
        System.out.println(protectedField); // OK
        System.out.println(packageField);   // 同じパッケージならOK
        // System.out.println(privateField); // コンパイルエラー

        publicMethod();    // OK
        protectedMethod(); // OK
        packageMethod();   // 同じパッケージならOK
        // privateMethod(); // コンパイルエラー
    }
}
```

## 5.4 継承におけるコンストラクタ

### コンストラクタの連鎖呼び出し

サブクラスのオブジェクトが生成されるとき、サブクラス自身のコンストラクタが実行される**前に**、必ずそのスーパークラスのコンストラクタが**先に**呼び出されます。

この呼び出しは継承階層を遡って行われます。たとえば、クラスCがクラスBを継承し、クラスBがクラスAを継承している場合、`new C()`でオブジェクトを生成すると、A→B→Cの順でコンストラクタが呼び出されます。

```java
class A {
    A() {
        System.out.println("Aのコンストラクタが呼び出されました。");
    }
}

class B extends A {
    B() {
        // ここで暗黙的に super(); が呼び出される
        System.out.println("Bのコンストラクタが呼び出されました。");
    }
}

class C extends B {
    C() {
        // ここで暗黙的に super(); が呼び出される
        System.out.println("Cのコンストラクタが呼び出されました。");
    }

    public static void main(String[] args) {
        new C(); // Cのオブジェクトを生成
    }
}
```

**実行結果：**
```
Aのコンストラクタが呼び出されました。
Bのコンストラクタが呼び出されました。
Cのコンストラクタが呼び出されました。
```

### superキーワード

`super`キーワードを使用して、スーパークラスのコンストラクタやメソッドを明示的に呼び出すことができます。

```java
class Animal {
    protected String name;
    protected int age;

    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
        System.out.println("動物「" + name + "」が作成されました。");
    }

    public void makeSound() {
        System.out.println(name + " が音を出しています。");
    }
}

class Dog extends Animal {
    private String breed;

    public Dog(String name, int age, String breed) {
        super(name, age); // スーパークラスのコンストラクタを明示的に呼び出し
        this.breed = breed;
        System.out.println("犬種: " + breed);
    }

    @Override
    public void makeSound() {
        super.makeSound(); // スーパークラスのメソッドを呼び出し
        System.out.println(name + " がワンワンと吠えています。");
    }

    public void fetch() {
        System.out.println(name + " がボールを取ってきます。");
    }
}
```

### 引数付きコンストラクタと継承

スーパークラスに引数なしのコンストラクタが存在しない場合、サブクラスのコンストラクタで明示的に`super()`を呼び出す必要があります。

```java
class Vehicle {
    protected String brand;
    protected int year;

    // 引数なしのコンストラクタが存在しない
    public Vehicle(String brand, int year) {
        this.brand = brand;
        this.year = year;
    }
}

class Car extends Vehicle {
    private int doors;

    public Car(String brand, int year, int doors) {
        super(brand, year); // 必須：スーパークラスのコンストラクタを明示的に呼び出し
        this.doors = doors;
    }

    // 以下はコンパイルエラーになる
    /*
    public Car(int doors) {
        // super(); が暗黙的に呼ばれるが、Vehicle()は存在しないためエラー
        this.doors = doors;
    }
    */
}
```

## 5.5 メソッドのオーバーライド

### オーバーライドの概念

**オーバーライド (Override)** とは、サブクラスでスーパークラスのメソッドを再定義することです。これにより、サブクラス固有の振る舞いを実装できます。

### @Overrideアノテーション

メソッドをオーバーライドする際は、`@Override`アノテーションを付けることが強く推奨されます。これにより、コンパイラがオーバーライドが正しく行われているかをチェックできます。

```java
class Shape {
    protected String color;

    public Shape(String color) {
        this.color = color;
    }

    public void draw() {
        System.out.println(color + "の図形を描画します。");
    }

    public double getArea() {
        return 0.0; // 基本実装
    }
}

class Circle extends Shape {
    private double radius;

    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }

    @Override
    public void draw() {
        System.out.println(color + "の半径" + radius + "の円を描画します。");
    }

    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }

    // Circle固有のメソッド
    public double getCircumference() {
        return 2 * Math.PI * radius;
    }
}

class Rectangle extends Shape {
    private double width;
    private double height;

    public Rectangle(String color, double width, double height) {
        super(color);
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw() {
        System.out.println(color + "の" + width + "×" + height + "の長方形を描画します。");
    }

    @Override
    public double getArea() {
        return width * height;
    }

    // Rectangle固有のメソッド
    public double getPerimeter() {
        return 2 * (width + height);
    }
}
```

### オーバーライドの実行例

```java
public class OverrideExample {
    public static void main(String[] args) {
        Shape[] shapes = {
            new Circle("赤", 5.0),
            new Rectangle("青", 4.0, 6.0),
            new Circle("緑", 3.0)
        };

        for (Shape shape : shapes) {
            shape.draw();  // それぞれのクラスでオーバーライドされたメソッドが呼ばれる
            System.out.println("面積: " + shape.getArea());
            System.out.println("---");
        }
    }
}
```

**実行結果：**
```
赤の半径5.0の円を描画します。
面積: 78.53981633974483
---
青の4.0×6.0の長方形を描画します。
面積: 24.0
---
緑の半径3.0の円を描画します。
面積: 28.274333882308138
---
```

## 5.6 ポリモーフィズム

### ポリモーフィズムの概念

**ポリモーフィズム (Polymorphism)** は「多態性」とも呼ばれ、同じインターフェイス（メソッド呼び出し）で異なる振る舞いを実現する機能です。継承とメソッドのオーバーライドにより実現されます。

### アップキャストとダウンキャスト

```java
public class PolymorphismExample {
    public static void main(String[] args) {
        // アップキャスト：サブクラスのオブジェクトをスーパークラス型の変数で参照
        Shape shape1 = new Circle("赤", 5.0);  // CircleをShape型として扱う
        Shape shape2 = new Rectangle("青", 4.0, 6.0);  // RectangleをShape型として扱う

        // ポリモーフィズム：同じメソッド呼び出しで異なる振る舞い
        shape1.draw();  // Circle.draw()が呼ばれる
        shape2.draw();  // Rectangle.draw()が呼ばれる

        // ダウンキャスト：スーパークラス型からサブクラス型への変換
        if (shape1 instanceof Circle) {
            Circle circle = (Circle) shape1;
            System.out.println("円周: " + circle.getCircumference());
        }

        if (shape2 instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) shape2;
            System.out.println("周囲: " + rectangle.getPerimeter());
        }
    }
}
```

### 実践的なポリモーフィズムの例

```java
// 図形を管理するクラス
class DrawingApp {
    public void processShapes(Shape[] shapes) {
        double totalArea = 0;
        
        for (Shape shape : shapes) {
            shape.draw();  // ポリモーフィズム：それぞれの図形の描画方法が呼ばれる
            totalArea += shape.getArea();  // ポリモーフィズム：それぞれの面積計算が行われる
        }
        
        System.out.println("総面積: " + totalArea);
    }
    
    public static void main(String[] args) {
        DrawingApp app = new DrawingApp();
        
        Shape[] shapes = {
            new Circle("赤", 3.0),
            new Rectangle("青", 4.0, 5.0),
            new Circle("緑", 2.0),
            new Rectangle("黄", 3.0, 3.0)
        };
        
        app.processShapes(shapes);
    }
}
```

## 5.7 パッケージとimport

### パッケージの基礎

パッケージは、関連するクラスやインターフェイスをグループ化する仕組みです。名前空間を提供し、クラス名の衝突を防ぎます。

#### パッケージ宣言

```java
// com/example/utils/StringUtil.java
package com.example.utils;

public class StringUtil {
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
    
    public static String reverse(String str) {
        if (isEmpty(str)) return str;
        return new StringBuilder(str).reverse().toString();
    }
}
```

#### パッケージのインポート

```java
// com/example/app/Main.java
package com.example.app;

import com.example.utils.StringUtil;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (StringUtil.isEmpty("")) {
            System.out.println("文字列は空です");
        }
        
        String original = "Hello";
        String reversed = StringUtil.reverse(original);
        System.out.println(original + " -> " + reversed);
        
        List<String> list = new ArrayList<>();  // java.util.ArrayListを使用
        list.add("Java");
        list.add("Programming");
    }
}
```

### パッケージ構造と命名規則

```
src/
├── com/
│   └── example/
│       ├── model/
│       │   ├── Product.java
│       │   ├── Book.java
│       │   └── Customer.java
│       ├── service/
│       │   ├── ProductService.java
│       │   └── OrderService.java
│       └── util/
│           ├── StringUtil.java
│           └── DateUtil.java
└── Main.java
```

## 5.8 JAR ファイルの作成とデプロイメント

### JARファイルとは

JAR (Java Archive) ファイルは、複数のJavaクラスファイルやリソースファイルを1つのファイルにまとめたアーカイブファイルです。アプリケーションの配布や実行を簡単にします。

### IntelliJ IDEAでのJAR作成

1. **File** > **Project Structure** を選択
2. **Artifacts** タブを選択
3. **+** ボタンをクリックして **JAR** > **From modules with dependencies** を選択
4. **Main Class** を指定（mainメソッドを持つクラス）
5. **OK** をクリック
6. **Build** > **Build Artifacts** からJARファイルを生成

### コマンドラインでのJAR作成

```bash
# クラスファイルをコンパイル
javac -d build src/com/example/**/*.java

# JARファイルを作成
jar cf myapp.jar -C build .

# 実行可能JARファイルを作成（マニフェストファイル付き）
echo "Main-Class: com.example.Main" > manifest.txt
jar cfm myapp.jar manifest.txt -C build .

# JARファイルを実行
java -jar myapp.jar
```

### マニフェストファイル

```
Manifest-Version: 1.0
Main-Class: com.example.Main
Class-Path: lib/commons-lang3-3.12.0.jar lib/gson-2.8.9.jar
```

## 5.9 JavaDocによるドキュメンテーション

### JavaDocの基本

JavaDocは、Javaソースコードからドキュメントを自動生成するツールです。特別なコメント形式を使用してAPIドキュメントを作成できます。

### JavaDocコメントの書き方

```java
/**
 * 商品を表すクラスです。
 * ECサイトで販売される商品の基本情報を管理します。
 * 
 * @author 山田太郎
 * @version 1.0
 * @since 2024-01-01
 */
public class Product {
    
    /**
     * 商品ID
     */
    private String productId;
    
    /**
     * 商品名
     */
    private String name;
    
    /**
     * 商品価格（税抜き）
     */
    private int price;
    
    /**
     * 商品を作成します。
     * 
     * @param productId 商品ID（null不可）
     * @param name 商品名（null不可、空文字不可）
     * @param price 商品価格（0以上）
     * @throws IllegalArgumentException 引数が無効な場合
     */
    public Product(String productId, String name, int price) {
        if (productId == null || name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("商品IDと商品名は必須です");
        }
        if (price < 0) {
            throw new IllegalArgumentException("価格は0以上である必要があります");
        }
        
        this.productId = productId;
        this.name = name;
        this.price = price;
    }
    
    /**
     * 税込み価格を計算します。
     * 消費税率は10%で計算されます。
     * 
     * @return 税込み価格
     */
    public int getPriceWithTax() {
        return (int) (this.price * 1.10);
    }
    
    /**
     * 商品名を取得します。
     * 
     * @return 商品名
     */
    public String getName() {
        return name;
    }
    
    /**
     * 商品価格（税抜き）を取得します。
     * 
     * @return 商品価格
     */
    public int getPrice() {
        return price;
    }
}
```

### 主要なJavaDocタグ

| タグ | 説明 | 使用場所 |
|------|------|----------|
| `@param` | パラメータの説明 | メソッド、コンストラクタ |
| `@return` | 戻り値の説明 | メソッド |
| `@throws` `@exception` | 例外の説明 | メソッド、コンストラクタ |
| `@author` | 作成者 | クラス、インターフェイス |
| `@version` | バージョン | クラス、インターフェイス |
| `@since` | 追加されたバージョン | すべて |
| `@deprecated` | 非推奨のマーク | すべて |
| `@see` | 関連項目への参照 | すべて |

### JavaDocの生成

#### IntelliJ IDEAでの生成

1. **Tools** > **Generate JavaDoc** を選択
2. 出力ディレクトリを指定
3. **OK** をクリック

#### コマンドラインでの生成

```bash
# JavaDocを生成
javadoc -d docs -sourcepath src -subpackages com.example

# プライベートメンバーも含めて生成
javadoc -d docs -private -sourcepath src -subpackages com.example

# 文字エンコーディングを指定
javadoc -d docs -encoding UTF-8 -charset UTF-8 -sourcepath src -subpackages com.example
```

## まとめ

この章では、オブジェクト指向プログラミングの重要な概念である継承とポリモーフィズムについて学習しました。

### 重要なポイント

1. **継承は「is-a」関係を表現する**: サブクラスはスーパークラスの特殊化版
2. **適切なアクセス修飾子の使用**: `private`、`protected`、`public`を正しく使い分ける
3. **コンストラクタの連鎖**: サブクラスのオブジェクト生成時にスーパークラスのコンストラクタが先に呼ばれる
4. **メソッドのオーバーライド**: `@Override`アノテーションを使用して安全にオーバーライドする
5. **ポリモーフィズムの活用**: 同じインターフェイスで異なる振る舞いを実現する
6. **パッケージによる名前空間管理**: 関連するクラスをグループ化し、名前の衝突を防ぐ
7. **JAR ファイルによる配布**: アプリケーションを単一ファイルとして配布する
8. **JavaDocによるドキュメンテーション**: ソースコードから自動的にドキュメントを生成する

継承は強力な機能ですが、過度に使用すると複雑で保守が困難なコードになる場合があります。「継承よりも合成を選ぶ」という設計原則も重要です。次の章では、インターフェイスについて学習し、より柔軟な設計手法を身につけましょう。