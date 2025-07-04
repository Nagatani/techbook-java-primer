# 第5章 継承とポリモーフィズム

## 始めに：コードの再利用とソフトウェアの進化を支える継承の概念

前章では、オブジェクト指向プログラミングの基礎であるクラスとオブジェクトについて学習しました。本章では、オブジェクト指向プログラミングの真の威力を発揮する「継承（Inheritance）」と「ポリモーフィズム（Polymorphism）」という概念について詳しく学習します。

これらの概念は単なるプログラミング技法ではありません。ソフトウェア開発における根本的な課題である「コードの再利用性」「保守性」「拡張性」を解決するための、長年にわたる研究と実践の成果として生まれた重要なしくみです。

### ソフトウェア開発における継承の必要性

ソフトウェア開発の歴史において、最も重要な課題の1つは「車輪の再発明」を避けることでした。これは、すでに存在する機能やしくみをゼロから作り直すのではなく、既存のものを活用してより効率的に開発を進めるという考え方です。

1960年代から1970年代にかけて、プログラムの規模が拡大するにつれて、開発者たちは似たような処理を何度も書く必要性に直面しました。たとえば、図形描画プログラムを考えてみましょう。円、四角形、三角形など、異なる図形を扱う場合でも、「位置を持つ」「色を持つ」「面積を計算する」「描画する」といった共通の特性があります。従来の手続き型プログラミングでは、これらの共通部分であっても、図形の種類ごとに別々のコードを書く必要がありました。

この問題は、単なる開発効率の低下だけでなく、保守性の著しい悪化をもたらしました。共通の処理に修正が必要になった場合、すべての類似コードを個別に修正する必要があり、修正漏れやバグの温床となっていました。さらに、新しい図形を追加する際も、既存のコードを参考にしながら、ほぼ全体をゼロから作り直す必要がありました。

### 生物学的継承からソフトウェア継承へのアナロジー

継承という概念は、生物学の世界からインスピレーションを得て命名されました。生物の世界では、親から子へと遺伝的特性が受け継がれ、子は親の特性を基本として持ちながら、環境に適応するための新しい特性を獲得していきます。この自然界のしくみをソフトウェア開発に応用したのが、オブジェクト指向プログラミングにおける継承の概念です。

ソフトウェアの継承では、「親クラス（スーパークラス）」が持つ特性を「子クラス（サブクラス）」が自動的に受け継ぎます。そして、子クラスは受け継いだ特性をそのまま使用することも、必要に応じて変更（オーバーライド）することも、新しい特性を追加することも可能です。このしくみにより、共通部分の重複を排除し、差分のみを実装することで効率的な開発が実現されます。

### 継承がもたらす革命的な変化

継承の導入は、ソフトウェア開発に革命的な変化をもたらしました。その主な効果は以下のとおりです：

**コードの劇的な削減**：共通する機能を一ヵ所にまとめることで、コード量を大幅に削減できます。これは、開発時間の短縮だけでなく、テスト工数の削減、バグの減少にも直結します。

**階層的なソフトウェア設計**：現実世界の分類体系をそのままソフトウェアの構造として表現できるようになりました。たとえば、「動物」→「哺乳類」→「犬」という階層構造を、そのままクラスの継承関係として実装できます。

**段階的な機能拡張**：既存のクラスを継承して新しいクラスを作ることで、既存の機能を損なうことなく段階的にシステムを拡張できます。これにより、大規模システムの段階的開発が可能になりました。

**プラットフォームの構築**：基本的な機能を提供する基底クラス群を「プラットフォーム」として整備し、そのうえでさまざまなアプリケーションを開発するという手法が確立されました。これは現代のフレームワーク開発の基礎となっています。

### ポリモーフィズムの概念とその重要性

継承と密接に関連する概念として、「ポリモーフィズム（多態性）」があります。ポリモーフィズムは、ギリシャ語の「多くの」（poly）と「形」（morph）を組み合わせた言葉で、「同じインターフェイスで異なる動作を実現する」しくみを指します。

ポリモーフィズムの威力は、「統一されたインターフェイスによる多様な実装」にあります。たとえば、「描画する」という同じ命令でも、円なら円として、四角形なら四角形として、それぞれ適切に描画されます。プログラムを書く側は、個々の図形の種類を意識することなく、統一された方法ですべての図形を扱えるのです。

このしくみにより、新しい図形クラスを追加する際も、既存のコードを変更することなく、システムを拡張できます。これは「開放・閉鎖の原則」として知られ、オブジェクト指向設計の重要な指針となっています。システムは新しい機能に対して開放的でありながら、既存のコードに対しては閉鎖的（変更不要）であるべきという考え方です。

### 現代的なソフトウェア開発における継承の位置付け

現代のソフトウェア開発では、継承は適切に使用されることで大きな威力を発揮しますが、同時に「継承よりも合成を選べ」という設計原則も重視されています。これは、継承を乱用すると、クラス間の結合度が高くなり、返って保守性が悪化する場合があるためです。

適切な継承の使用には、以下の点が重要です：

**「is-a」関係の厳密な適用**：継承は、真の「〜は〜の一種である」関係にのみ使用すべきです。単にコードを再利用したいだけの場合は、合成やインターフェイスの実装を検討すべきです。

**リスコフの置換原則の遵守**：子クラスのオブジェクトは、親クラスのオブジェクトと置き換え可能でなければなりません。これにより、ポリモーフィズムが正しく機能します。

**適切な抽象化レベルの選択**：継承階層は、適切な抽象化レベルで構築されるべきです。過度に細かい階層は複雑性を増し、過度に粗い階層は再利用性を損ないます。

本章では、これらの重要な概念を、Javaの具体的な構文と実践的な例を通じて学習していきます。単に継承の書き方を覚えるのではなく、なぜ継承が必要なのか、どのような場面で効果的なのか、どのような注意点があるのかを理解することで、優れたオブジェクト指向設計の基礎を身につけていきましょう。

## 5.1 継承とは何か？

### 継承の基本概念

継承とは、「受け継ぐこと」という意味通り、オブジェクトが持つフィールドやメソッドなどを受け継ぐことができるしくみです。受け継いだ機能は、そのまま利用することもできますし、上書きして違う機能を提供することもできます。

継承は、オブジェクト指向プログラミングにおける基本的なメカニズムの1つです。これは、既存のクラス（親クラス、スーパークラス）を基盤として、新しいクラス（子クラス、サブクラス）を作成することを可能にします。新しいクラスは、基盤となるクラスの非privateなメンバー（フィールドとメソッド）を自動的に受け継ぎます。

### is-a関係

継承の本質は、「**is-a**」（〜は〜の一種である）関係にあります。サブクラスは、スーパークラスの**特殊化された**バージョンと考えることができます。

たとえば、ECサイトにおいて「書籍（Book）」は多種多様な「商品 (Product)」の中の一種です。したがって、`Book`クラスは`Product`クラスを継承することで、商品が共通して持つ特性（商品ID、名称、価格、税込み価格計算など）を受け継ぎつつ、書籍固有の特性（著者名、出版社、ISBNなど）を追加できます。

### 理解のためのアナロジー

継承を理解するために、ECサイトの商品カテゴリの階層構造を考えてみましょう。「書籍（Book）」は「商品（Product）」の一種であり、「家電製品（Electronics）」も「商品 (Product)」の一種です。どちらも商品としての共通の特性（価格を持つ、詳細情報を表示するなど）を継承しますが、それぞれ固有の特性（書籍なら著者名や出版社、家電製品ならメーカー名や保証期間など）を追加します。

### 主要な用語

継承関係において、基盤となるクラス、つまり継承される側のクラスを**スーパークラス (Superclass)** または親クラス、基底クラスと呼びます。

一方、スーパークラスから特性を受け継ぐクラスを**サブクラス (Subclass)** または子クラス、派生クラスと呼びます。

サブクラスはスーパークラスを**拡張 (extends)** すると表現され、スーパークラスの特性を継承し、さらに独自の特性を追加したり、継承した振る舞いを変更したりできます。

## 5.2 Javaにおける継承の実装

### extendsキーワード

Javaでクラス間の継承関係を確立するには、`extends`キーワードを使用します。基本的な構文は以下のとおりです。

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

この宣言により、`SubclassName`は`SuperclassName`の非privateなメンバーへアクセスできます。

### 継承の実践例：ECサイトの商品階層システム

継承は、現実世界の分類体系をプログラムの構造として直接表現する強力なしくみです。以下のプログラムは、ECサイトの商品管理システムという実用的な例を通じて、継承の利点と適切な設計方法を学習するための重要な材料です：

ファイル名： Product.java、Book.java、Electronics.java、InheritanceExample.java

```java
// スーパークラス（商品全般の共通属性と振る舞いを定義）
class Product {
    protected String productId;    // 商品ID（サブクラスからもアクセスできるようprotected）
    protected String name;         // 商品名
    protected int price;           // 価格
    protected String category;     // カテゴリ
    protected boolean inStock;     // 在庫有無

    // コンストラクタ：すべてのProductに必要な基本情報を初期化
    public Product(String productId, String name, int price, String category) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.inStock = true; // デフォルトで在庫あり
        
        System.out.println("Product: " + this.name + " を登録しました（カテゴリ: " + this.category + "）");
    }

    // 価格に消費税を加算するメソッド（すべての商品共通の振る舞い）
    public int getPriceWithTax() {
        return (int) (this.price * 1.10); // 消費税10%として計算
    }

    // 商品情報を表示するメソッド（基本的な表示、サブクラスでオーバーライド可能）
    public void displayDetails() {
        System.out.println("=== 商品情報 ===");
        System.out.println("商品ID: " + this.productId);
        System.out.println("商品名: " + this.name);
        System.out.println("カテゴリ: " + this.category);
        System.out.println("価格（税抜）: " + this.price + "円");
        System.out.println("価格（税込）: " + getPriceWithTax() + "円");
        System.out.println("在庫状況: " + (this.inStock ? "在庫あり" : "在庫なし"));
    }

    // 在庫状態を変更するメソッド
    public void setStockStatus(boolean inStock) {
        this.inStock = inStock;
        System.out.println(this.name + " の在庫状況を「" + (inStock ? "在庫あり" : "在庫なし") + "」に変更しました。");
    }

    // 割引価格を計算するメソッド（サブクラスでオーバーライド可能）
    public int getDiscountedPrice(double discountRate) {
        if (discountRate < 0 || discountRate > 1.0) {
            System.out.println("無効な割引率です。元の価格を返します。");
            return this.price;
        }
        return (int) (this.price * (1.0 - discountRate));
    }

    // getter メソッド
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    public String getCategory() { return category; }
    public boolean isInStock() { return inStock; }
}

// サブクラス1：書籍（Product の特殊化）
class Book extends Product {
    private String author;     // 著者名（Book固有の属性）
    private String publisher;  // 出版社（Book固有の属性）
    private String isbn;       // ISBN（Book固有の属性）
    private int pageCount;     // ページ数（Book固有の属性）

    // コンストラクタ：書籍特有の情報も含めて初期化
    public Book(String productId, String name, int price, String author, String publisher, String isbn, int pageCount) {
        // スーパークラスのコンストラクタを呼び出し、共通のフィールドを初期化
        super(productId, name, price, "書籍");
        this.author = author;
        this.publisher = publisher;
        this.isbn = isbn;
        this.pageCount = pageCount;
        
        System.out.println("Book: 書籍固有情報（著者: " + this.author + ", 出版社: " + this.publisher + "）を設定しました。");
    }

    // スーパークラスのメソッドをオーバーライド：書籍固有の情報を追加表示
    @Override
    public void displayDetails() {
        super.displayDetails(); // まずスーパークラスの基本表示を実行
        System.out.println("=== 書籍詳細情報 ===");
        System.out.println("著者: " + this.author);
        System.out.println("出版社: " + this.publisher);
        System.out.println("ISBN: " + this.isbn);
        System.out.println("ページ数: " + this.pageCount + "ページ");
    }

    // 書籍の特別割引計算（専門書は割引率を制限）
    @Override
    public int getDiscountedPrice(double discountRate) {
        // 専門書の場合は最大20%まで割引
        if (discountRate > 0.2) {
            System.out.println("書籍の割引は最大20%までです。20%割引を適用します。");
            discountRate = 0.2;
        }
        return super.getDiscountedPrice(discountRate);
    }

    // Book固有のメソッド
    public String getAuthor() { return this.author; }
    public String getPublisher() { return this.publisher; }
    public String getIsbn() { return this.isbn; }
    public int getPageCount() { return this.pageCount; }

    // 書籍の詳細情報を簡潔な文字列で取得
    public String getBookInfo() {
        return String.format("『%s』 著者: %s, 出版社: %s, %dページ", name, author, publisher, pageCount);
    }

    // 1ページあたりの価格を計算（書籍特有の指標）
    public double getPricePerPage() {
        return (double) this.price / this.pageCount;
    }
}

// サブクラス2：電子機器（Product の別の特殊化）
class Electronics extends Product {
    private String manufacturer;  // メーカー（Electronics固有）
    private String model;         // 型番（Electronics固有）
    private int warrantyPeriod;   // 保証期間（月単位）（Electronics固有）
    private double powerConsumption; // 消費電力（W）（Electronics固有）

    // コンストラクタ
    public Electronics(String productId, String name, int price, String manufacturer, 
                      String model, int warrantyPeriod, double powerConsumption) {
        super(productId, name, price, "電子機器");
        this.manufacturer = manufacturer;
        this.model = model;
        this.warrantyPeriod = warrantyPeriod;
        this.powerConsumption = powerConsumption;
        
        System.out.println("Electronics: 電子機器固有情報（メーカー: " + this.manufacturer + ", 型番: " + this.model + "）を設定しました。");
    }

    // オーバーライド：電子機器固有の情報を追加表示
    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("=== 電子機器詳細情報 ===");
        System.out.println("メーカー: " + this.manufacturer);
        System.out.println("型番: " + this.model);
        System.out.println("保証期間: " + this.warrantyPeriod + "ヶ月");
        System.out.println("消費電力: " + this.powerConsumption + "W");
    }

    // 電子機器の特別割引計算（高額商品は割引率を制限）
    @Override
    public int getDiscountedPrice(double discountRate) {
        // 価格が10万円以上の場合は最大10%まで割引
        if (this.price >= 100000 && discountRate > 0.1) {
            System.out.println("高額電子機器の割引は最大10%までです。10%割引を適用します。");
            discountRate = 0.1;
        }
        return super.getDiscountedPrice(discountRate);
    }

    // Electronics固有のメソッド
    public String getManufacturer() { return this.manufacturer; }
    public String getModel() { return this.model; }
    public int getWarrantyPeriod() { return this.warrantyPeriod; }
    public double getPowerConsumption() { return this.powerConsumption; }

    // 年間電気代を概算計算（電子機器特有の指標）
    public int getAnnualElectricityCost() {
        // 1日8時間使用、電気代30円/kWhと仮定
        double dailyCost = (this.powerConsumption / 1000.0) * 8 * 30;
        return (int) (dailyCost * 365);
    }
}

public class InheritanceExample {
    public static void main(String[] args) {
        System.out.println("=== ECサイト商品管理システム - 継承の実践例 ===");
        System.out.println();
        
        // 書籍オブジェクトを作成
        System.out.println("1. 書籍商品の作成:");
        Book javaBook = new Book("B001", "Java完全マスター", 3500, 
                                "田中太郎", "技術書出版", "978-4-123456-78-9", 580);
        System.out.println();
        
        // 電子機器オブジェクトを作成
        System.out.println("2. 電子機器商品の作成:");
        Electronics laptop = new Electronics("E001", "ノートパソコン ProBook", 120000,
                                           "TechCorp", "PB-2024", 24, 65.5);
        System.out.println();
        
        // 継承されたメソッドの使用
        System.out.println("3. 共通メソッドの使用（継承の恩恵）:");
        System.out.println("Java本の税込価格: " + javaBook.getPriceWithTax() + "円");
        System.out.println("ノートPCの税込価格: " + laptop.getPriceWithTax() + "円");
        System.out.println();
        
        // オーバーライドされたメソッドの使用
        System.out.println("4. 書籍の詳細情報表示:");
        javaBook.displayDetails();
        System.out.println();
        
        System.out.println("5. 電子機器の詳細情報表示:");
        laptop.displayDetails();
        System.out.println();
        
        // サブクラス固有のメソッドの使用
        System.out.println("6. サブクラス固有メソッドの使用:");
        System.out.println("書籍情報: " + javaBook.getBookInfo());
        System.out.printf("1ページあたりの価格: %.2f円\n", javaBook.getPricePerPage());
        System.out.println("ノートPCの年間電気代: " + laptop.getAnnualElectricityCost() + "円");
        System.out.println();
        
        // オーバーライドされた割引計算の動作確認
        System.out.println("7. 商品カテゴリ別の割引ルール:");
        System.out.println("Java本の30%割引価格: " + javaBook.getDiscountedPrice(0.3) + "円");
        System.out.println("ノートPCの30%割引価格: " + laptop.getDiscountedPrice(0.3) + "円");
        System.out.println();
        
        // 在庫状態の変更
        System.out.println("8. 在庫状態の管理:");
        javaBook.setStockStatus(false);
        laptop.setStockStatus(true);
    }
}
```

**このプログラムから学ぶ重要な継承の概念：**

1. **コードの再利用性**：Productクラスの基本機能（価格計算、在庫管理等）をBookとElectronicsが重複なく利用しています。

2. **is-a関係の実現**：「BookはProductの一種」「ElectronicsはProductの一種」という自然な関係がコードに反映されています。

3. **super キーワードの活用**：サブクラスのコンストラクタとメソッドで、スーパークラスの機能を適切に呼び出しています。

4. **メソッドオーバーライドの威力**：displayDetails() メソッドは各サブクラスで拡張され、適切な情報表示を実現しています。

5. **protected アクセス修飾子の意義**：スーパークラスのフィールドをサブクラスからアクセス可能にしつつ、外部からの直接アクセスは防いでいます。

6. **ビジネスルールの継承**：割り引き計算のロジックを商品タイプごとにカスタマイズしながら、基本的な処理は共通化しています。

**実用的な応用場面：**

- **企業システム**：従業員（正社員、契約社員、アルバイト）の階層管理
- **ゲーム開発**：キャラクタ（プレイヤー、敵、NPC）の共通機能と特殊化
- **ファイル管理**：ファイル（テキスト、画像、動画）の統一的な操作と特殊処理
- **UI コンポーネント**：ボタン、入力フィールド、選択リストの共通基盤と個別機能

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

この呼び出しは継承階層をさかのぼって行われます。たとえば、クラスCがクラスBを継承し、クラスBがクラスAを継承している場合、`new C()`でオブジェクトを生成すると、A→B→Cの順でコンストラクタが呼び出されます。

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

`super`キーワードを使用して、スーパークラスのコンストラクタやメソッドを明示的に呼びだすことができます。

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

スーパークラスに引数なしのコンストラクタが存在しない場合、サブクラスのコンストラクタで明示的に`super()`を呼びだす必要があります。

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

### ポリモーフィズムの実践例：動物園管理システム

ポリモーフィズムは、オブジェクト指向プログラミングの最も強力な特徴の1つです。以下のプログラムは、動物園の管理システムという実用的な例を通じて、ポリモーフィズムがどのようにコードの柔軟性と拡張性を向上させるかを学習するための重要な材料です：

ファイル名： Animal.java、Zoo.java、PolymorphismExample.java

```java
// スーパークラス：すべての動物の基本的な振る舞いを定義
abstract class Animal {
    protected String name;
    protected int age;
    protected String species;
    protected boolean isHealthy;

    public Animal(String name, int age, String species) {
        this.name = name;
        this.age = age;
        this.species = species;
        this.isHealthy = true;
        System.out.println("動物「" + name + "」(" + species + ")が動物園に登録されました。");
    }

    // 抽象メソッド：サブクラスで必ず実装される
    public abstract void makeSound();
    public abstract void eat();
    public abstract void move();

    // 共通のメソッド：すべての動物で同じ処理
    public void sleep() {
        System.out.println(name + " はぐっすり眠っています。");
    }

    public void showInfo() {
        System.out.println("=== " + name + " の情報 ===");
        System.out.println("種類: " + species);
        System.out.println("年齢: " + age + "歳");
        System.out.println("健康状態: " + (isHealthy ? "良好" : "要治療"));
    }

    // オーバーライド可能なメソッド：基本的な振る舞いを提供
    public void performTrick() {
        System.out.println(name + " は基本的な芸を披露しています。");
    }

    // getter メソッド
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getSpecies() { return species; }
    public boolean isHealthy() { return isHealthy; }
    
    public void setHealthy(boolean healthy) { 
        this.isHealthy = healthy;
        System.out.println(name + " の健康状態が「" + (healthy ? "良好" : "要治療") + "」に変更されました。");
    }
}

// サブクラス1：ライオン
class Lion extends Animal {
    private int maneLength; // たてがみの長さ（ライオン固有の属性）

    public Lion(String name, int age, int maneLength) {
        super(name, age, "ライオン");
        this.maneLength = maneLength;
    }

    @Override
    public void makeSound() {
        System.out.println(name + " が力強く「ガオーーー！」と吠えています。");
    }

    @Override
    public void eat() {
        System.out.println(name + " が肉を豪快に食べています。狩りの本能を感じます。");
    }

    @Override
    public void move() {
        System.out.println(name + " が威厳を持ってゆっくりと歩いています。百獣の王の風格です。");
    }

    @Override
    public void performTrick() {
        System.out.println(name + " が威厳のある咆哮で観客を魅了しています！");
    }

    public void hunt() {
        System.out.println(name + " が狩りの体勢を取りました。さすが肉食動物の王です。");
    }
}

// サブクラス2：ゾウ
class Elephant extends Animal {
    private double trunkLength; // 鼻の長さ（ゾウ固有の属性）

    public Elephant(String name, int age, double trunkLength) {
        super(name, age, "ゾウ");
        this.trunkLength = trunkLength;
    }

    @Override
    public void makeSound() {
        System.out.println(name + " が「パオーーン！」と雄大な鳴き声を響かせています。");
    }

    @Override
    public void eat() {
        System.out.println(name + " が器用に鼻を使って草や果物を食べています。");
    }

    @Override
    public void move() {
        System.out.println(name + " がどっしりとした足取りで歩いています。地面が揺れています。");
    }

    @Override
    public void performTrick() {
        System.out.println(name + " が鼻を使って器用にボールを投げています！観客から拍手喝采です！");
    }

    public void spray() {
        System.out.println(name + " が鼻で水をかけて涼んでいます。");
    }
}

// サブクラス3：サル
class Monkey extends Animal {
    private boolean canSwing; // 木にぶら下がれるか（サル固有の属性）

    public Monkey(String name, int age, boolean canSwing) {
        super(name, age, "サル");
        this.canSwing = canSwing;
    }

    @Override
    public void makeSound() {
        System.out.println(name + " が「ウキキー！」と元気に鳴いています。");
    }

    @Override
    public void eat() {
        System.out.println(name + " がバナナを器用に剥いて美味しそうに食べています。");
    }

    @Override
    public void move() {
        if (canSwing) {
            System.out.println(name + " が木から木へと軽やかにぶら下がって移動しています。");
        } else {
            System.out.println(name + " が地面を跳ね回って移動しています。");
        }
    }

    @Override
    public void performTrick() {
        System.out.println(name + " がアクロバティックに逆立ちをして観客を楽しませています！");
    }

    public void climbTree() {
        System.out.println(name + " が素早く木に登っています。");
    }
}

// 動物園管理クラス：ポリモーフィズムを活用
class Zoo {
    private Animal[] animals;
    private int count;
    private static final int MAX_ANIMALS = 50;

    public Zoo() {
        animals = new Animal[MAX_ANIMALS];
        count = 0;
        System.out.println("新しい動物園が開園しました！");
    }

    public void addAnimal(Animal animal) {
        if (count < MAX_ANIMALS) {
            animals[count] = animal;
            count++;
            System.out.println(animal.getName() + " が動物園に追加されました。現在の動物数: " + count);
        } else {
            System.out.println("動物園が満員です。これ以上動物を追加できません。");
        }
    }

    // ポリモーフィズムの威力：すべての動物に統一的な操作
    public void feedAllAnimals() {
        System.out.println("\n=== 全動物の餌やり時間 ===");
        for (int i = 0; i < count; i++) {
            System.out.print((i + 1) + ". ");
            animals[i].eat(); // 各動物の固有のeat()メソッドが呼び出される
        }
    }

    public void makeAllAnimalsSound() {
        System.out.println("\n=== 全動物の鳴き声タイム ===");
        for (int i = 0; i < count; i++) {
            System.out.print((i + 1) + ". ");
            animals[i].makeSound(); // 各動物の固有のmakeSound()メソッドが呼び出される
        }
    }

    public void showAllAnimalsInfo() {
        System.out.println("\n=== 動物園の全動物情報 ===");
        for (int i = 0; i < count; i++) {
            animals[i].showInfo();
            System.out.println();
        }
    }

    public void performAllTricks() {
        System.out.println("\n=== 動物ショータイム ===");
        for (int i = 0; i < count; i++) {
            System.out.print((i + 1) + ". ");
            animals[i].performTrick(); // 各動物の固有のperformTrick()メソッドが呼び出される
        }
    }

    public void moveAllAnimals() {
        System.out.println("\n=== 動物たちの移動時間 ===");
        for (int i = 0; i < count; i++) {
            System.out.print((i + 1) + ". ");
            animals[i].move(); // 各動物の固有のmove()メソッドが呼び出される
        }
    }

    public int getAnimalCount() {
        return count;
    }

    // 特定の種類の動物の数を数える
    public int countSpecies(String species) {
        int speciesCount = 0;
        for (int i = 0; i < count; i++) {
            if (animals[i].getSpecies().equals(species)) {
                speciesCount++;
            }
        }
        return speciesCount;
    }
}

public class PolymorphismExample {
    public static void main(String[] args) {
        System.out.println("=== 動物園管理システム - ポリモーフィズムの実践 ===");
        System.out.println();
        
        // 動物園を作成
        Zoo myZoo = new Zoo();
        System.out.println();
        
        // 様々な種類の動物を作成して動物園に追加
        System.out.println("--- 動物の追加 ---");
        Lion simba = new Lion("シンバ", 5, 20);
        Elephant dumbo = new Elephant("ダンボ", 10, 1.5);
        Monkey george = new Monkey("ジョージ", 3, true);
        Lion nala = new Lion("ナラ", 4, 15);
        Monkey chimp = new Monkey("チンプ", 2, false);
        
        myZoo.addAnimal(simba);
        myZoo.addAnimal(dumbo);
        myZoo.addAnimal(george);
        myZoo.addAnimal(nala);
        myZoo.addAnimal(chimp);
        System.out.println();
        
        // ポリモーフィズムの実演：同じメソッド呼び出しで異なる振る舞い
        myZoo.showAllAnimalsInfo();
        myZoo.makeAllAnimalsSound();
        myZoo.feedAllAnimals();
        myZoo.moveAllAnimals();
        myZoo.performAllTricks();
        
        // 統計情報の表示
        System.out.println("\n=== 動物園統計 ===");
        System.out.println("総動物数: " + myZoo.getAnimalCount());
        System.out.println("ライオンの数: " + myZoo.countSpecies("ライオン"));
        System.out.println("ゾウの数: " + myZoo.countSpecies("ゾウ"));
        System.out.println("サルの数: " + myZoo.countSpecies("サル"));
        
        // アップキャストとダウンキャストの実演
        System.out.println("\n=== キャストの実演 ===");
        Animal animal = new Lion("レオ", 6, 25); // アップキャスト：自動的に行われる
        
        animal.makeSound(); // Lion の makeSound() が呼び出される（ポリモーフィズム）
        animal.performTrick(); // Lion の performTrick() が呼び出される
        
        // ダウンキャスト：サブクラス固有のメソッドを使用したい場合
        if (animal instanceof Lion) {
            Lion lion = (Lion) animal; // ダウンキャスト
            lion.hunt(); // Lion固有のメソッドを呼び出し
        }
    }
}
```

**このプログラムから学ぶ重要なポリモーフィズムの概念：**

1. **統一的なインターフェイス**：Zooクラスの各メソッド（feedAllAnimals(), makeAllAnimalsSound()等）は、Animal型の配列に対して統一的な操作を行えます。新しい動物の種類を追加しても、これらのメソッドを変更する必要がありません。

2. **実行時の動的決定**：animal.makeSound（）の呼び出し時に、実際にどのクラスのmakeSound()メソッドが実行されるかは、実行時に決定されます。これが「動的ディスパッチ」と呼ばれるしくみです。

3. **開放・閉鎖の原則**：新しい動物クラス（例：Tiger, Penguin等）を追加する際、既存のコード（Zooクラス）を変更することなく、システムを拡張できます。

4. **型安全性の保証**：すべての動物オブジェクトはAnimal型として扱われるため、コンパイル時に型の整合性がチェックされます。

5. **コードの簡潔性**：ポリモーフィズムにより、if-else文やswitch文による条件分岐を避け、よりエレガントで保守しやすいコードが実現されます。

**実用的な応用場面：**

- **図形描画システム**：Circle, Rectangle, Triangle等の図形を統一的に描画
- **支払いシステム**：CreditCard, BankTransfer, DigitalWallet等の決済方法を統一的に処理
- **メディアプレイヤー**：MP3, MP4, WAV等のファイル形式を統一的に再生
- **データベースアクセス**：MySQL, PostgreSQL, Oracle等のDBを統一的に操作

### アップキャストとダウンキャスト

```java
public class CastingExample {
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

## まとめ

本章では、オブジェクト指向プログラミングの重要な概念である継承とポリモーフィズムについて学習しました。

### 重要なポイント

1. **継承は「is-a」関係を表現する**: サブクラスはスーパークラスの特殊化版
2. **適切なアクセス修飾子の使用**: `private`、`protected`、`public`を正しく使い分ける
3. **コンストラクタの連鎖**: サブクラスのオブジェクト生成時にスーパークラスのコンストラクタが先に呼ばれる
4. **メソッドのオーバーライド**: `@Override`アノテーションを使用して安全にオーバーライドする
5. **ポリモーフィズムの活用**: 同じインターフェイスで異なる振る舞いを実現する

### 次章に向けて

第6章からは、より実用的なプログラミング技法について学習していきます。例外処理により堅牢なプログラムの作成方法を習得し、今回実装した図書館管理システムをより実用的なものに発展させていきましょう。

継承は強力な機能ですが、過度に使用すると複雑で保守が困難なコードになる場合があります。「継承よりも合成を選ぶ」という設計原則も重要です。次の章では、インターフェイスについて学習し、より柔軟な設計手法を身につけましょう。