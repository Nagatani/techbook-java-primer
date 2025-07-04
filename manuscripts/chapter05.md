# 第5章 継承：コードの再利用と拡張

## 🎯総合演習プロジェクトへのステップ

本章で学ぶ「継承」は、将来的に**総合演習プロジェクト「ToDoリストアプリケーション」**の機能を拡張する際に役立ちます。

- **タスクの多様化**: 現在は1つの`Task`クラスですが、継承を使えば「優先度の高いタスク `PriorityTask`」や「繰り返しタスク `RecurringTask`」のように、基本機能を引き継ぎつつ、それぞれが固有の機能を持つ新しいタスク種別を簡単に追加できます。
- **コードの再利用**: すべてのタスクに共通する処理（例：完了状態の管理）を基本となる`Task`クラスに記述することで、コードの重複を防ぎ、保守性を高めます。

本章を学ぶことで、既存のクラスを基盤として、効率的に新しい機能を追加していくオブジェクト指向の強力なテクニックが身につきます。

## 📋 本章の学習目標

### 前提知識
- **第3章、第4章のクラス設計**: クラス、フィールド、メソッド、コ��ストラクタ、アクセス修飾子の役割を理解している。

### 到達目標

#### 知識理解目標
- 継承の基本概念と、「is-a」関係を説明できる。
- スーパークラスとサブクラスの関係を理解する。
- `extends`キーワードと`super`キーワードの役割と使い方を説明できる。
- メソッドのオーバーライドの目的と`@Override`アノテーションの重要性を理解する。
- サブクラスのコンストラクタからスーパークラスのコンストラクタが呼び出される連鎖のしくみを理解する。

#### 技能習得目標
- `extends`を使ってクラスを継承できる。
- サブクラスのコンストラクタから`super()`を使ってスーパークラスのコンストラクタを呼び出せる。
- スーパークラスのメソッドをオーバーライドして、サブクラス固有の振る舞いを実装できる。
- `super`キーワードを使って、オーバーライドしたメソッド内からスーパークラスのメソッドを呼び出せる。

---

## 5.1 継承とは何か？

**継承（Inheritance）**とは、既存のクラス（**親クラス**または**スーパークラス**）の特性（フィールドやメソッド）を受け継いで、新しいクラス（**子クラス**または**サブクラス**）を作成するしくみです。

### なぜ継承が必要か？

ソフトウェア開発では、似たような機能を持つクラスを複数作成する場面がよくあります。たとえば、ECサイトで「書籍」「家電」「食品」などを扱う場合、これらはすべて「商品」として、以下のような共通の特性を持っています。

- **共通のデータ**: 商品ID, 商品名、 価格
- **共通の操作**: 価格を表示する、 在庫を確認する

継承を使わない場合、これらの共通機能を各クラスで個別に実装する必要があり、コードの重複や修正漏れの原因となります。

継承を使うと、まず共通機能をまとめた`Product`（商品）クラスを作成し、`Book`（書籍）や`Electronics`（家電）クラスがそれを継承することで、共通機能を再利用しつつ、それぞれ固有の機能（書籍なら著者名、家電ならメーカー名など）を追加できます。

### is-a関係

継承は、クラス間に「**is-a**」（〜は〜の一種である）という関係が成り立つ場合に適用するのが原則です。

- 「`Book` is-a `Product`」（本は商品の一種である）-> **OK**
- 「`Electronics` is-a `Product`」（家電は商品の一種である）-> **OK**
- 「`Car` is-a `Tire`」（車はタイヤの一種である）-> **NG** (車はタイヤを「持っている（has-a）」関係）

## 5.2 Javaにおける継承の実装

Javaでクラスを継承するには、`extends`キーワードを使用します。

```java
// スーパークラス
class Product {
    // ...
}

// サブクラス
class Book extends Product {
    // ...
}
```

### 継承の実践例：ECサイトの商品階層システム

`Product`クラスをスーパークラスとし、それを継承する`Book`と`Electronics`サブクラスを作成する例を見てみましょう。

```java
// Product.java (スーパークラス)
public class Product {
    protected String productId;
    protected String name;
    protected int price;

    public Product(String productId, String name, int price) {
        this.productId = productId;
        this.name = name;
        this.price = price;
    }

    public void displayDetails() {
        System.out.println("商品ID: " + this.productId);
        System.out.println("商品名: " + this.name);
        System.out.println("価格（税抜）: " + this.price + "円");
    }
}

// Book.java (サブクラス)
public class Book extends Product {
    private String author; // Book固有のフィールド

    public Book(String productId, String name, int price, String author) {
        // super()でスーパークラスのコンストラクタを呼び出す
        super(productId, name, price);
        this.author = author;
    }
}

// Electronics.java (サブクラス)
public class Electronics extends Product {
    private String manufacturer; // Electronics固有のフィールド

    public Electronics(String productId, String name, int price, String manufacturer) {
        // super()でスーパークラスのコンストラクタを呼び出す
        super(productId, name, price);
        this.manufacturer = manufacturer;
    }
}
```

### 継承におけるコンストラクタと`super`

サブクラスのオブジェクトが生成されるとき、そのサブクラスのコンストラクタが実行される**前**に、必ずスーパークラスのコンストラクタが**先に**呼び出されます。

サブクラスのコンストラクタの**先頭**で`super()`を呼びだすことで、スーパークラスのどのコンストラクタを呼びだすかを明示的に指定できます。`super()`を省略した場合、スーパークラスの引数なしのコンストラクタ（デフォルトコンストラクタ）が自動的に呼び出されます。

### メソッドのオーバーライド

**オーバーライド (Override)** とは、サブクラスでスーパークラスから継承したメソッドを、サブクラス固有の処理内容で**再定義（上書き）**することです。

`Book`クラスと`Electronics`クラスで、`displayDetails`メソッドをオーバーライドして、それぞれ固有の情報を追加で表示するようにしてみましょう。

```java
// Book.java
public class Book extends Product {
    // ... (コンストラクタなどは省略) ...

    // @Overrideアノテーションを付けることを推奨
    @Override
    public void displayDetails() {
        super.displayDetails(); // superでスーパークラスの元のメソッドを呼び出す
        System.out.println("著者: " + this.author); // 固有の情報を追加
    }
}

// Electronics.java
public class Electronics extends Product {
    // ... (コンストラクタなどは省略) ...

    @Override
    public void displayDetails() {
        super.displayDetails(); // スーパークラスの処理を再利用
        System.out.println("メーカー: " + this.manufacturer); // 固有の情報を追加
    }
}
```

-   **`@Override`アノテーション**: メソッドが正しくオーバーライドされているかをコンパイラにチェックさせるための目印です。タイプミスな��を防ぐため、付けることが強く推奨されます。
-   **`super.メソッド名()`**: オーバーライドしたメソッド内から、スーパークラスの元のメソッドを呼びだすことができます。これにより、共通の処理を再利用しつつ、機能を追加できます。

## まとめ

本章では、オブジェクト指向の重要な柱である「継承」について学びました。

-   **継承**は、`extends`キーワードを使い、既存クラスの機能を引き継いで新しいクラスを作成するしくみです。
-   継承は「**is-a**」関係を表し、コードの**再利用性**を高めます。
-   サブクラスのコンストラクタでは、`super()`を使って親クラスのコンストラクタを呼び出し、初期化処理を連鎖させます。
-   **メソッドのオーバーライド**により、サブクラスで親クラスの振る舞いをカスタマイズできます。

次章では、この継承を基盤とした、オブジェクト指向のもう1つの柱である「ポリモーフィズム」について学びます。