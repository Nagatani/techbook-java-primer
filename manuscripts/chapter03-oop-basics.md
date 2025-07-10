# 第3章 オブジェクト指向の考え方

## 本章の学習目標

### 前提知識
**必須前提**：
- 第1章までの内容の完全な習得
- Java基本文法での簡単なプログラム作成経験
- メソッドと変数の概念の理解

**概念的前提**：
- ソフトウェア開発における問題（コードの複雑化、保守の困難さ）の実感
- 大規模なシステム開発の課題への関心

### 学習目標
**知識理解目標**：
- オブジェクト指向パラダイムの歴史的必然性
- カプセル化、継承、ポリモーフィズムの基本概念
- クラスとオブジェクトの関係性
- アクセス修飾子の役割と重要性

**技能習得目標**：
- 基本的なクラスの設計と実装
- コンストラクタの適切な定義と使用
- インスタンス変数とメソッドの実装
- private/publicアクセス修飾子の適切な使用
- オブジェクトの生成と操作

**設計思考目標**：
- 現実世界の概念をクラスとして抽象化する能力
- データと処理をまとめる設計思考
- カプセル化によるデータ保護の設計

**到達レベルの指標**：
- 実世界の概念（商品、人物、車など）をクラスとして設計・実装できる
- 複数のクラスを組み合わせた簡単なプログラムが作成できる
- 手続き型とオブジェクト指向の違いを具体例で説明できる
- クラス図の基本的な読み書きができる



## 章の構成

本章は、オブジェクト指向プログラミングの基礎から実践まで、段階的に学習できるよう構成されています：

### [Part A: 導入](chapter03a-oop-introduction.md)
- なぜオブジェクト指向が必要なのか
- ソフトウェア開発の課題と解決策
- オブジェクト指向の基本的な考え方

### [Part B: オブジェクト指向の基本概念](chapter03b-oop-concepts.md)
- 手続き型とオブジェクト指向の違い
- オブジェクト指向の3つの基本原則
- クラスの作成と基本構造
- 実用的なクラス設計例

### [Part C: クラスとオブジェクトの詳細](chapter03c-classes-and-objects.md)
- オブジェクト指向の重要な用語と概念
- クラス、インスタンス、オブジェクトの関係
- カプセル化の実践
- アクセス修飾子の段階的理解

### [Part D: Java言語の基礎](chapter03d-java-basics.md)
- 型とリテラル
- プリミティブ型と参照型
- 型変換とラッパクラス

### [Part E: 演算子と式](chapter03e-operators.md)
- 代入演算子と算術演算子
- 比較演算子と論理演算子
- インクリメント・デクリメント
- 文字列との連結

### [Part F: 制御構造](chapter03f-control-structures.md)
- if文による条件分岐
- switch文による複数の選択肢の比較
- while、do-while、forによる繰り返し
- break、continueによる制御

### [Part G: 配列と文字列](chapter03g-arrays-strings.md)
- 文字列の作成と操作（作成予定）
- 文字列の比較と検索（作成予定）

### [Part H: static修飾子](chapter03h-static-modifier.md)
- インスタンスメンバーとクラスメンバー
- staticフィールドとstaticメソッド
- staticの実用的な使用例

### [Part I: 配列](chapter03i-arrays.md)
- 配列の宣言と領域確保
- 多次元配列
- 配列の実践的な使用例

### [Part J: 章末演習](chapter03j-exercises.md)
- メソッド作成練習
- テストの点数管理クラス
- 車の燃料消費シミュレータ

## 学習の進め方

1. Part A-Cでオブジェクト指向の基本概念を理解
2. Part D-FでJava言語の基本文法を習得
3. Part G-Iで実践的なプログラミング技術を学習
4. Part Jの演習課題で理解を深める

各パートは独立して読むことも可能ですが、順番に学習することで、オブジェクト指向プログラミングの全体像を体系的に理解できるよう設計されています。

<!-- Merged from chapter03a-oop-introduction.md -->


## 始めに：なぜオブジェクト指向が必要なのか

前章では、Javaの歴史的背景と特徴について学習しました。本章では、Javaの核心となる「オブジェクト指向プログラミング」について学習します。

### ソフトウェア開発の課題

1960年代後半、ソフトウェアの規模が急速に拡大し、「ソフトウェアクライシス」と呼ばれる問題が発生しました。プロジェクトの遅延、品質の低下、保守の困難さなど、従来の開発手法では対処できない課題が山積していました。

**ソフトウェアクライシスの具体例：**

1. **IBMのOS/360プロジェクト（1960年代）**
   - 予算：5千万ドル → 実際：5億ドル（10倍の超過）
   - 開発期間：大幅な遅延
   - フレデリック・ブルックスが「人月の神話」で詳述
   - 教訓：人員を増やしても開発速度は比例しない

2. **アポロ計画のソフトウェア（1960年代）**
   - 40万行のコードに1000人以上のプログラマ
   - 手作業でのコード管理による混乱
   - マーガレット・ハミルトンによる「ソフトウェア工学」の提唱

3. **SABRE航空予約システム（1960年代）**
   - 史上初の大規模リアルタイムシステム
   - 従来の手法では複雑性を管理できず
   - 新しい開発手法の必要性を実証

構造化プログラミングがこれらの問題への最初の回答でしたが、データと処理が分離されているため、大規模システムでは変更の影響範囲の把握が困難で、コードの再利用性も低いという限界がありました。

**構造化プログラミングの限界を示す例：**
```c
// 銀行システムの例（C言語）
struct Account {
    int id;
    double balance;
    char name[50];
};

void deposit(struct Account* acc, double amount) {
    acc->balance += amount;
}

void withdraw(struct Account* acc, double amount) {
    if (acc->balance >= amount) {
        acc->balance -= amount;
    }
}

// 問題点：
// 1. データ構造変更時、全関数の修正が必要
// 2. 誤った操作（直接balance変更）を防げない
// 3. 新しい口座タイプ（定期預金など）の追加が困難
```

### オブジェクト指向という解決策

オブジェクト指向は、これらの問題を解決するために生まれた革新的なパラダイムです。その最も重要な特徴は、**現実世界のモデリング**というアプローチです。プログラムを「オブジェクト」の集合として構成し、各オブジェクトが状態（データ）と振る舞い（メソッド）を一体化して持つことで、現実の問題領域を直接的にコードで表現できるようになりました。

この基本概念を支えるのが**カプセル化**です。オブジェクトの内部実装を隠蔽し、明確なインターフェイスのみを公開することで、実装の詳細を知らなくてもオブジェクトを安全に利用できます。これにより、変更の影響範囲を局所化し、システム全体の保守性を大幅に向上させることができました。

さらに、**継承**メカニズムにより、共通の特性を持つオブジェクトを階層的に組織化し、コードの再利用性を高めることが可能になりました。そして**ポリモーフィズム**は、同じインターフェイスを通じて異なる実装を扱える柔軟性を提供し、システムの拡張性を大きく向上させました。

これらの概念が統合されることで、大規模で複雑なソフトウェアであっても、理解しやすく保守しやすい構造を維持できるようになりました。

## オブジェクト指向パラダイムの歴史的意義

オブジェクト指向の革新性は、データと処理を一体化することで根本的な解決を提供したことにあります。構造化プログラミングではデータ構造の変更がすべての関数に影響しましたが、オブジェクト指向では変更の影響を局所化できるようになりました。

このパラダイムシフトは、現代のデザインパターン、フレームワーク、マイクロサービス、ドメイン駆動設計などの基盤となっています。

**オブジェクト指向の詳細な歴史と実装パターンについては、付録B.03「プログラミングパラダイムの進化」を参照してください。**

## 2.1 オブジェクト指向とは

オブジェクト指向という言葉は、実は多様な意味を持っています。ソフトウェア開発のさまざまな工程で、オブジェクト指向の考え方が適用されているからです。

まず、システム設計の段階では、**オブジェクト指向設計（OOD: Object-Oriented Design）**が用いられます。これは、システムをオブジェクトの集合として設計し、それらの関係性を定義する手法です。同様に、要求分析の段階では**オブジェクト指向分析（OOA: Object-Oriented Analysis）**が、問題領域をオブジェクトの観点から分析します。これらは総称して**オブジェクト指向分析設計（OOAD: Object-Oriented Analysis and Design）**と呼ばれます。

しかし、本書が焦点を当てるのは**オブジェクト指向プログラミング（OOP: Object-Oriented Programming）**です。これは、設計されたオブジェクトを実際にプログラミング言語で実装する技術です。Javaは、OOPを実践するために設計された言語であり、クラス、継承、ポリモーフィズムなどのOOPの主要概念を言語レベルでサポートしています。

さらに幅広い視点では、開発方法論やプロジェクト管理手法としてのオブジェクト指向、そしてプログラミング言語仕様としてのオブジェクト指向も存在しますが、これらは本書の範囲を超える内容です。

## 2.2 オブジェクト指向を学ぶ必要性

現代において「専門的にオブジェクト指向について学ぶこと」自体は、実はそれほど重要でありません。では本書で取り扱うものは何なのか、ということになります。

皆さんは一年次にC言語を講義で学びました。
C言語でのプログラミングでも、構造的なプログラミングや、関数ライブラリを使った柔軟な開発は可能です。
極論を言ってしまえば、新たな道具としてオブジェクト指向を学ばなくても、たいていのプログラムは作れます。

### より良く継続的な開発を行うために

C言語でも十分なプログラミングは可能ですが、現実のソフトウェア開発では、プログラムの作成は全体の一部にすぎません。作成されたプログラムは、その後長期にわたる「保守・運用」のフェーズに入り、この期間が実は開発期間よりもはるかに長く、コストも大きくなることが一般的です。

このような現実を踏まえると、プログラミング言語には単に動くプログラムを作ること以上の要求が突きつけられます。まず、**開発の効率化**が重要です。同じ機能を何度も実装するのではなく、既存のコードを再利用し、新たな機能をすばやく追加できる必要があります。

次に、**保守性の向上**が不可欠です。要求の変化やバグ修正の際に、変更箇所が明確で、変更の影響範囲が限定されていることが理想です。そして、**品質の向上**も欠かせません。バグが少なく、意図が明確で、拡張性の高いコードを書くことが、長期的な成功につながります。

これらの要求を満たすための強力なアプローチが、オブジェクト指向プログラミングなのです。

### 大規模で長期的に維持可能なプログラムを作る

C言語でも不可能ではありませんが「もっと効率良くしたい」という要望のために、オブジェクト指向という考え方が登場します。
より効率良くプログラミングを行うことを目的として、保守運用を見据えた概念であることをよく理解してオブジェクト指向を学んでください。

ただ、現代におけるプログラミングに求められるプログラミングの考え方には、オブジェクト指向以外も必要としています。
オブジェクト指向のその先もある、ということです。



次のパート：[Part B - オブジェクト指向の基本概念](chapter03b-oop-concepts.md)




<!-- Merged from chapter03b-oop-concepts.md -->


## 2.3 オブジェクト指向の基本概念を実際のコードで理解する

理論的な説明の前に、実際のJavaコードを見てオブジェクト指向の基本概念を理解しましょう。

### 手続き型とオブジェクト指向の違い

#### 手続き型の例（C言語風）
```java
// データと処理が分離している
public class ProceduralExample {
    public static void main(String[] args) {
        // データは単なる変数
        String studentName = "田中太郎";
        int studentAge = 20;
        double studentGpa = 3.5;
        
        // 処理は独立した関数
        printStudent(studentName, studentAge, studentGpa);
    }
    
    public static void printStudent(String name, int age, double gpa) {
        System.out.println("名前: " + name + ", 年齢: " + age + ", GPA: " + gpa);
    }
}
```

#### オブジェクト指向の例
```java
// データと処理が一体化している
public class Student {
    // データ（状態）
    private String name;
    private int age;
    private double gpa;
    
    // コンストラクタ
    public Student(String name, int age, double gpa) {
        this.name = name;
        this.age = age;
        this.gpa = gpa;
    }
    
    // 処理（振る舞い）
    public void printInfo() {
        System.out.println("名前: " + name + ", 年齢: " + age + ", GPA: " + gpa);
    }
}

// 使用例
public class ObjectOrientedExample {
    public static void main(String[] args) {
        Student student = new Student("田中太郎", 20, 3.5);
        student.printInfo();  // オブジェクトに処理を依頼
    }
}
```

### オブジェクト指向の3つの基本原則

#### 1. カプセル化
```java
public class BankAccount {
    private double balance;  // privateで隠蔽
    
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;  // 適切な検証後に変更
        }
    }
    
    public double getBalance() {
        return balance;  // 読み取り専用のアクセス
    }
}
```

#### 2. 継承
```java
public class Product {
    protected String productId;
    protected String name;
    protected int price;
    
    public void displayInfo() {
        System.out.println("商品名: " + name + ", 価格: " + price + "円");
    }
}

public class Book extends Product {
    private String author;
    private String isbn;
    
    public void displayBookInfo() {
        displayInfo();  // 親クラスのメソッドを呼び出し
        System.out.println("著者: " + author + ", ISBN: " + isbn);
    }
}
```

#### 3. ポリモーフィズム
```java
public interface PaymentMethod {
    void processPayment(double amount);
}

public class CreditCardPayment implements PaymentMethod {
    private String cardNumber;
    
    public void processPayment(double amount) {
        System.out.println("クレジットカードで" + amount + "円を決済しました");
        // 実際のクレジットカード決済処理
    }
}

public class BankTransferPayment implements PaymentMethod {
    private String accountNumber;
    
    public void processPayment(double amount) {
        System.out.println("銀行振込で" + amount + "円を送金しました");
        // 実際の銀行振込処理
    }
}
```

## 2.4 クラスの作成

オブジェクト指向プログラミングでは、**クラス**という設計図を作成し、その設計図から**オブジェクト（インスタンス）**を生成します。

### クラスの基本構造

```java
public class Order {
    // フィールド（状態）
    private String orderId;
    private String customerName;
    private String status;  // "受付中", "処理中", "配送中", "完了"
    private double totalAmount;
    
    // コンストラクタ
    public Order(String orderId, String customerName, double totalAmount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.status = "受付中";
    }
    
    // メソッド（振る舞い）
    public void processOrder() {
        if (status.equals("受付中")) {
            status = "処理中";
            System.out.println("注文 " + orderId + " の処理を開始しました");
        }
    }
    
    public void ship() {
        if (status.equals("処理中")) {
            status = "配送中";
            System.out.println("注文 " + orderId + " を発送しました");
        }
    }
    
    public void complete() {
        if (status.equals("配送中")) {
            status = "完了";
            System.out.println("注文 " + orderId + " が完了しました");
        }
    }
    
    public void displayInfo() {
        System.out.printf("注文ID: %s, 顧客: %s, 金額: %.2f円, 状態: %s%n",
                orderId, customerName, totalAmount, status);
    }
}
```

### mainメソッドとプログラムの実行

```java
public class OrderDemo {
    public static void main(String[] args) {
        // オブジェクトの生成
        Order order1 = new Order("ORD-001", "田中太郎", 15800);
        
        // 注文のライフサイクルをシミュレート
        order1.displayInfo();
        
        order1.processOrder();
        order1.displayInfo();
        
        order1.ship();
        order1.displayInfo();
        
        order1.complete();
        order1.displayInfo();
    }
}
```

実行結果：
```
注文ID: ORD-001, 顧客: 田中太郎, 金額: 15800.00円, 状態: 受付中
注文 ORD-001 の処理を開始しました
注文ID: ORD-001, 顧客: 田中太郎, 金額: 15800.00円, 状態: 処理中
注文 ORD-001 を発送しました
注文ID: ORD-001, 顧客: 田中太郎, 金額: 15800.00円, 状態: 配送中
注文 ORD-001 が完了しました
注文ID: ORD-001, 顧客: 田中太郎, 金額: 15800.00円, 状態: 完了
```

## 2.5 実用的なクラス設計例

### 例1：図書管理システム

```java
public class Book {
    private String isbn;
    private String title;
    private String author;
    private boolean isAvailable;
    
    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.isAvailable = true;
    }
    
    public boolean borrow() {
        if (isAvailable) {
            isAvailable = false;
            return true;
        }
        return false;
    }
    
    public void returnBook() {
        isAvailable = true;
    }
    
    public String getInfo() {
        String status = isAvailable ? "貸出可能" : "貸出中";
        return String.format("%s - %s (%s) [%s]", isbn, title, author, status);
    }
}
```

### 例2：ショッピングカート

```java
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<Item> items;
    private double taxRate;
    
    public ShoppingCart(double taxRate) {
        this.items = new ArrayList<>();
        this.taxRate = taxRate;
    }
    
    public void addItem(String name, double price, int quantity) {
        items.add(new Item(name, price, quantity));
    }
    
    public double calculateTotal() {
        double subtotal = 0;
        for (Item item : items) {
            subtotal += item.getSubtotal();
        }
        return subtotal * (1 + taxRate);
    }
    
    public void displayCart() {
        System.out.println("=== ショッピングカート ===");
        for (Item item : items) {
            System.out.println(item);
        }
        System.out.printf("合計（税込）: %.2f円%n", calculateTotal());
    }
    
    // 内部クラス
    private class Item {
        private String name;
        private double price;
        private int quantity;
        
        public Item(String name, double price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }
        
        public double getSubtotal() {
            return price * quantity;
        }
        
        @Override
        public String toString() {
            return String.format("%s - %.2f円 × %d = %.2f円", 
                name, price, quantity, getSubtotal());
        }
    }
}
```

## まとめ

このパートでは、オブジェクト指向の基本概念を実際のコードを通じて学習しました。重要なポイントは：

1. **データと処理の一体化**：クラスは関連するデータと処理をまとめる
2. **カプセル化**：内部実装を隠蔽し、必要なインターフェイスのみを公開
3. **オブジェクトの生成と操作**：newキーワードでインスタンスを生成し、メソッドを通じて操作
4. **現実世界のモデリング**：実世界の概念をクラスとして表現

次のパートでは、より詳細なクラスの機能について学習します。



次のパート：[Part C - クラスとオブジェクト](chapter03c-classes-and-objects.md)




<!-- Merged from chapter03c-classes-and-objects.md -->


## 2.4 オブジェクト指向の重要な用語と概念

ここからは、オブジェクト指向プログラミングで使用される重要な用語と概念を、実践的な例を交えて説明します。

### オブジェクトとは

オブジェクトは、コンピュータのメモリ上に展開された、プログラム内の「状態」と「振る舞い」を統合した存在です。「状態」とは、オブジェクトが保持するデータを指し、変数やフィールドとして実装されます。一方、「振る舞い」は、オブジェクトが実行できる処理内容であり、メソッド（関数）として実装されます。

重要なのは、これらの状態と振る舞いが別々に存在するのではなく、密接な関連を持って一体化されている点です。たとえば、銀行口座オブジェクトであれば、残高（状態）と入金・出金操作（振る舞い）が1つのオブジェクトとして管理されることで、データの整合性を保ちやすくなります。

### クラスとは

オブジェクトはメモリ上に展開されて使用されます。
そのオブジェクトは、どんな状態を持って、どんな振る舞いをするのかを定義するのがクラスです。

よくある表現として、オブジェクトの設計図をクラスという言い方がされます。

### インスタンス（実体）とは
オブジェクトは、メモリ上に展開されて使用されます。
その状態をインスタンスと呼び、メモリ上に展開して使用できるようにすることをインスタンス化（実体化）と言います。

### クラス、インスタンス、オブジェクトの関係

オブジェクト指向プログラミングでは、「クラス」「インスタンス」「オブジェクト」という用語が頻繁に登場し、初学者にとって混乱の原因となることがあります。これらの関係を明確に理解することが重要です。

**クラス**は、オブジェクトの設計図です。建築にたとえれば、家の設計図にあたります。クラスは、どのようなデータ（フィールド）を持ち、どのような操作（メソッド）ができるかを定義します。

**インスタンス**は、クラスをもとに実際に作成された具体的な実体です。設計図から実際に建てられた家に相当します。1つのクラスから複数のインスタンスを作成でき、それぞれが独立したデータを保持します。

**オブジェクト**という用語は、文脈によって意味が変わることに注意が必要です。多くの場合、オブジェクトはインスタンスを指しますが、より幅広い概念を表す場合や、クラス自体を指す場合もあります。「オブジェクト指向」という場合のオブジェクトは、より抽象的な概念を表しています。

※厳密には、インスタンス化をしなくても内部的に使用できる状態や振る舞いというのも存在します。それらは「**静的（static）な**○○」と呼ばれ、インスタンス化を明示的に行わなくても、プログラム実行時に自動的にメモリへ展開されており使用できるようになっています。対義語として「動的（dynamic）な○○」という言い方もあり、そちらはインスタンス化しないと使用できません。

### クラスはどのように書くか

何らかのデータと、それに対する処理をまとめて書けると良いです。

「クラスは、C言語における構造体（struct）に、関数をつけられるようにしたもの」のようにイメージすると理解しやすいです。

### 役割ごとに分割する必要性

その方が大規模なプログラムを作る際には管理しやすくなります。
管理しやすいとは、複雑でなく、バグの発見・修正が容易なことを指します。

### 手続き型 vs オブジェクト指向

ここでは、手続き型プログラミングとオブジェクト指向プログラミングの根本的な違いを、学生情報を管理するプログラムの例を通じて理解します。この違いは、データの管理方法と責任の所在に関する重要な設計思想の違いを表しています。

#### 手続き型プログラミングの特徴と課題

手続き型プログラミングでは、データ構造と処理が分離されており、グローバルな関数がデータを操作します。この方式は単純で理解しやすい反面、大規模なシステムでは以下のような問題が生じます：

**手続き型プログラミング（C言語）**
```c
// データと処理が分離している
struct Student {
    char name[50];
    int age;
    double gpa;
};

void printStudent(struct Student s) {
    printf("名前: %s, 年齢: %d, GPA: %.2f\n", s.name, s.age, s.gpa);
}
```

**手続き型の課題：**
- **データの一貫性管理の困難さ**：Studentデータを変更する関数が複数存在する場合、どの関数がどのタイミングでデータを変更するかを追跡することが困難になります。
- **変更の影響範囲の拡大**：Student構造体にフィールドを追加した場合、そのデータを使用するすべての関数を見つけ出して修正する必要があります。
- **責任の所在のあいまいさ**：データの検証、初期化、出力など、学生に関する処理がシステム全体に散らばってしまいます。

#### オブジェクト指向プログラミングによる解決

オブジェクト指向では、関連するデータとそれを操作する処理を1つのクラスにまとめることで、上記の問題を解決します：

**オブジェクト指向プログラミング（Java）**
```java
// データと処理が一体化している
public class Student {
    private String name;
    private int age;
    private double gpa;
    
    // データを操作するメソッドも同じクラス内に定義
    public void printInfo() {
        System.out.println("名前: " + name + ", 年齢: " + age + ", GPA: " + gpa);
    }
}
```

**オブジェクト指向の利点：**
- **責任の明確化**：学生に関するすべての操作がStudentクラス内に集約され、データの管理責任が明確になります。
- **カプセル化による保護**：privateキーワードにより、外部からの不正なデータアクセスを防ぎ、データの整合性を保護します。
- **変更の局所化**：学生に関する処理の変更は、Studentクラス内のみで完結し、システム全体への影響を最小限に抑えられます。
- **現実世界のモデリング**：「学生」という概念を直接的にプログラムの構造に反映させることで、直感的で理解しやすいコードになります。

### main関数に処理を突っ込むのではなく

自分の作っているプログラムをよく分析して、どんな登場人物（データ）がいるか、それぞれどんな役割があるかで考え、クラスに分割しましょう。

- イメージとして、子どもに対して着替えの指示を出すことを考えましょう。
    + （上着を着ているか確認）「上着のボタンを外して」「右腕から袖を通して次に左腕通して脱いで」（シャツを着ているか確認）「シャツも右腕から……」（靴下を履いているか確認）「屈んで」「靴下脱いで」（ズボンを履いているか確認）「ズボン脱いで」（タンスの場所を子どもが知っているか）「新しい服をタンスから出して」（新しい服はそろっているか）「シャツを首に通して……」……………

毎回、全部指示出すのは面倒でありませんか？
子どもには、**「子どもは、着替えてください（服を）」** というような命令で指示を出せて、結果だけ受け取ることができたらよいですよね？

### 全部自分のプログラムでやろうとしない

自分が作ったクラスから、役割を持つほかのオブジェクトに対して指示を出すことで、関心の分離が可能となります。
大事なポイントとして、「処理をほかのオブジェクトに**お任せ**する」ということが挙げられます。

### 処理をお任せすることと、お任せされる側の配慮

オブジェクトに処理をお任せする以上、そのオブジェクトの状態や振る舞いに外から干渉したくない（されたくもない）

- オブジェクトが持つ状態や、状態に対する変更処理を外部から直接触られると、予期せぬ不具合や、副作用が発生する。
    + 意図しない操作をされたくない部分を、外部から操作できないようにする

→ これを**カプセル化（隠ぺい）** とよいます。
適切なカプセル化を施すことで、オブジェクトの使いやすさ向上につながります。

### 設計時に振る舞いの名前や入出力情報だけ決める
昨今のオブジェクト指向言語には、**インターフェイス**という概念があります。
インターフェイスにはクラスの振る舞いの名前や渡されるべきデータ型、振る舞いによる結果のデータ型だけを定義しておけます。

- インターフェイスは、それ自体をデータ型として使える便利なもの
- オブジェクト指向は、プログラミングだけでなく設計としての側面も強い

### オブジェクト指向言語でつまずやすい継承

難しく考える必要はありません。
同じ状態、同じ振る舞いを持つクラスをそれぞれコピー&ペーストで作ると、管理が煩雑になってしまいます。

「似たデータ、似た処理をもつオブジェクトを、クラスの時点でまとめよう」として使用するのが継承という考え方になります。
単にまとめるための手段だと知っておきましょう。

（「従業員クラス」「マネージャークラス」がそれぞれ「人事管理可能」インターフェイスを実装することで、どちらも「人事評価機能」を持つといった設計もよくあります。教育的な例としては有効ですが、実際のシステムでは権限管理やロールベースのアクセス制御など、より洗練されたアプローチが必要になることが多いです）

### システム開発に「銀の弾丸などない」

フレッド・ブルックスが1975年に著した「人月の神話」で述べたように、ソフトウェア開発の複雑性を一挙に解決する「銀の弾丸」は存在しません。オブジェクト指向プログラミングも例外ではなく、万能の解決策ではありません。

オブジェクト指向を不適切に適用した場合、返って問題を悪化させることがあります。たとえば、ごく小規模なスクリプトやユーティリティプログラムにオブジェクト指向を無理に取り入れると、シンプルな処理が複雑なクラス構造に埋もれてしまい、コードの記述量が膨大になることがあります。

また、バッチ処理やパイプライン処理のように、単機能の処理が連続して呼び出されるシステムでは、オブジェクト指向のオーバーヘッドが大きくなり、手続き型のアプローチの方が適切な場合があります。さらに、過度な抽象化や継承の乱用は、コードの理解を困難にし、管理しきれない副作用を発生させる可能性があります。

重要なのは、オブジェクト指向が適切な場面を見極め、適度に使用することです。問題の性質、システムの規模、開発チームのスキルセットなどを考慮して、最適なアプローチを選択することが、真のプロフェッショナルに求められるスキルです。

### オブジェクト指向は効率良く開発を行うための考え方

歴史をたどると、システム開発の効率を求めた結果、オブジェクト指向という体系ができたに過ぎません。
これは通過点であり、オブジェクト指向の考え方を学ぶことですべてのことが説明できる気になったりましますが、これですべてを賄えることはありません。
本書では、オブジェクト指向という道具を学びますが、これはただのプログラミングテクニックの1つとして、使いたいときに使えるよう、適切な使い方を身に付けましょう。

## 2.5 クラスとオブジェクト

### クラスの定義

クラスの定義は、オブジェクト指向プログラミングの核心部分です。ここでは、「本」という現実世界の概念をプログラムで表現する方法を学びます。クラス設計では、そのオブジェクトが「何を知っているか（属性）」と「何ができるか（操作）」を明確に定義することが重要です。

以下のBookクラスは、本という概念の本質的な属性と操作を表現した例です：

```java
public class Book {
    // フィールド（属性）
    private String title;
    private String author;
    private int pages;
    private double price;
    
    // コンストラクタ
    public Book(String title, String author, int pages, double price) {
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.price = price;
    }
    
    // メソッド（操作）
    public void displayInfo() {
        System.out.println("タイトル: " + title);
        System.out.println("著者: " + author);
        System.out.println("ページ数: " + pages);
        System.out.println("価格: " + price + "円");
    }
    
    // ゲッターメソッド
    public String getTitle() {
        return title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    // セッターメソッド
    public void setPrice(double price) {
        if (price >= 0) {
            this.price = price;
        }
    }
}
```

**このクラス定義から学ぶ重要な概念：**

1. **カプセル化の実践**：すべてのフィールドをprivateで宣言することで、外部からの直接アクセスを禁止し、データの整合性を保護しています。

2. **コンストラクタによる初期化**：オブジェクト作成時に必要な情報をすべて受け取ることで、不完全なオブジェクトの生成を防いでいます。

3. **責任の明確化**：本に関する表示処理（displayInfo）を本クラス自身が担当することで、「本は自分の情報を表示できる」という自然な責任分担を実現しています。

4. **制御されたアクセス**：setPrice()メソッドで価格の妥当性をチェックすることで、ビジネスルール（価格は0以上）をクラス内で守っています。

5. **情報隠蔽の原則**：読み取り専用の情報（title, author）はgetterのみを提供し、変更可能な情報（price）には検証付きのsetterを提供するという、アクセス制御の使い分けを示しています。

### オブジェクトの作成と使用

クラスを定義しただけでは、まだ実際にデータを保存したり処理を実行したりすることはできません。クラスは「設計図」であり、実際に動作するプログラムを作るには、その設計図からオブジェクト（インスタンス）を作成する必要があります。

以下のプログラムは、Bookクラスから実際のオブジェクトを作成し、それらを操作する方法を示しています：

```java
public class BookTest {
    public static void main(String[] args) {
        // オブジェクトの作成（インスタンス化）
        Book book1 = new Book("Java入門", "田中太郎", 300, 2800.0);
        Book book2 = new Book("データ構造", "佐藤花子", 250, 3200.0);
        
        // メソッドの呼び出し
        book1.displayInfo();
        System.out.println("---");
        book2.displayInfo();
        
        // 価格の変更
        book1.setPrice(2500.0);
        System.out.println("変更後の価格: " + book1.getTitle() + " - " + book1.getPrice() + "円");
    }
}
```

**このプログラムから学ぶオブジェクト指向の重要な概念：**

1. **オブジェクトの独立性**：book1とbook2は同じBookクラスから作られていますが、それぞれ独立したデータを持っています。一方のオブジェクトの状態を変更しても、他方には影響しません。

2. **new演算子の役割**：`new Book(...)`は、メモリ上にBookオブジェクト用の領域を確保し、コンストラクタを呼び出してオブジェクトを初期化します。これにより、クラスという「型」から具体的な「実体」が作られます。

3. **メッセージパッシング**：`book1.displayInfo()`のような記法は、book1オブジェクトに「自分の情報を表示して」というメッセージを送っていると解釈できます。オブジェクトは自分の責任範囲で処理を実行します。

4. **状態の変更と永続性**：`book1.setPrice(2500.0)`により、book1オブジェクトの内部状態が変更されます。この変更は、そのオブジェクトが存在する限り維持されます。

5. **型安全性**：BookTestクラスでは、Bookオブジェクトに対してBookクラスで定義されたメソッドのみ呼びだすことができ、不正な操作を防いでいます。

**実行時の動作理解：**
このプログラムを実行すると、メモリ上に2つの独立したBookオブジェクトが作成され、それぞれが異なる本の情報を保持します。price変更後のbook1は新しい価格情報を保持し続け、これによりオブジェクトの「状態を持つ」という特性を実感できます。

## 2.3 カプセル化

カプセル化は、オブジェクト指向プログラミングの最も重要な原則の1つです。この概念は、関連するデータと処理を1つの単位にまとめ、外部からの不適切なアクセスを制限することで、システムの安全性と保守性を大幅に向上させます。

カプセル化の本質は「情報隠蔽」と「責任の局所化」であり、これにより以下の利益を得られます：
- データの整合性保護（不正な状態への変更を防ぐ）
- 変更の影響範囲の局所化（内部実装を変更しても外部への影響を最小限に抑える）
- 再利用性の向上（明確なインターフェイスを通じた利用）

### アクセス修飾子とカプセル化の段階的理解

カプセル化の重要性を理解するために、段階的に設計を改善していく例を見てみましょう。

#### 段階1: カプセル化なし（問題のある設計）

まず、カプセル化されていない設計から始めます：

```java
// 悪い例：カプセル化されていない銀行口座
public class BankAccountV1 {
    public String accountNumber;  // public: 誰でも変更可能
    public double balance;        // public: 残高を直接操作可能
    
    public BankAccountV1(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }
}

// 使用例（問題のある使い方）
public class ProblemExample {
    public static void main(String[] args) {
        BankAccountV1 account = new BankAccountV1("12345", 1000.0);
        
        // 問題：残高を直接操作できてしまう
        account.balance = -500.0;  // 負の残高！
        account.accountNumber = "";  // 口座番号を空に！
        
        // 問題：取引履歴が残らない
        account.balance += 1000.0;  // 誰がいつ入金したか不明
    }
}
```

**この設計の問題点：**
- データの整合性が保証されない（負の残高、空の口座番号）
- ビジネスルールを強制できない
- 変更履歴が追跡できない
- 誤った使用方法を防げない

#### 段階2: 基本的なカプセル化

privateキーワードとメソッドを使って基本的なカプセル化を実装：

```java
// 改善例：基本的なカプセル化
public class BankAccountV2 {
    private String accountNumber;
    private double balance;
    
    public BankAccountV2(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }
    
    // 入金メソッド
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    // 出金メソッド
    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
    
    // 残高照会
    public double getBalance() {
        return balance;
    }
}
```

**改善された点：**
- フィールドがprivateで保護されている
- メソッドを通じてのみ操作可能
- 基本的な検証ロジックを実装

#### 段階3: 完全なカプセル化（エンタープライズレベル）

実際の業務システムで求められるレベルのカプセル化：

```java
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BankAccountV3 {
    private final String accountNumber;  // finalで不変性を保証
    private double balance;
    private final List<Transaction> transactions;
    private boolean isActive;
    
    public BankAccountV3(String accountNumber, double initialBalance) {
        // コンストラクタでも検証
        if (!isValidAccountNumber(accountNumber)) {
            throw new IllegalArgumentException("無効な口座番号です");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("初期残高は0以上である必要があります");
        }
        
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
        this.isActive = true;
        
        // 初期取引を記録
        transactions.add(new Transaction(
            TransactionType.INITIAL_DEPOSIT, 
            initialBalance, 
            LocalDateTime.now()
        ));
    }
    
    public synchronized void deposit(double amount) {
        validateAccountActive();
        if (amount <= 0) {
            throw new IllegalArgumentException("入金額は正の値である必要があります");
        }
        
        balance += amount;
        transactions.add(new Transaction(
            TransactionType.DEPOSIT, 
            amount, 
            LocalDateTime.now()
        ));
    }
    
    public synchronized boolean withdraw(double amount) {
        validateAccountActive();
        if (amount <= 0) {
            throw new IllegalArgumentException("出金額は正の値である必要があります");
        }
        
        if (balance < amount) {
            return false;  // 残高不足
        }
        
        balance -= amount;
        transactions.add(new Transaction(
            TransactionType.WITHDRAWAL, 
            amount, 
            LocalDateTime.now()
        ));
        return true;
    }
    
    // イミュータブルな取引履歴を返す
    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactions);  // 防御的コピー
    }
    
    // アカウントの凍結
    public void freeze() {
        this.isActive = false;
    }
    
    private void validateAccountActive() {
        if (!isActive) {
            throw new IllegalStateException("この口座は凍結されています");
        }
    }
    
    private static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && accountNumber.matches("\\d{10}");
    }
    
    // 内部クラスで取引を表現
    public static class Transaction {
        private final TransactionType type;
        private final double amount;
        private final LocalDateTime timestamp;
        
        private Transaction(TransactionType type, double amount, LocalDateTime timestamp) {
            this.type = type;
            this.amount = amount;
            this.timestamp = timestamp;
        }
        
        // getterのみ提供（イミュータブル）
        public TransactionType getType() { return type; }
        public double getAmount() { return amount; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    public enum TransactionType {
        INITIAL_DEPOSIT, DEPOSIT, WITHDRAWAL
    }
}
```

**エンタープライズレベルのカプセル化の特徴：**
1. **完全な検証**：すべての入力を検証
2. **イミュータビリティ**：変更不可能なフィールドはfinalで宣言
3. **防御的コピー**：内部状態を返す際はコピーを返す
4. **スレッドセーフティ**：synchronizedで同時アクセスを制御
5. **監査証跡**：すべての操作を記録
6. **状態管理**：アカウントの有効/無効状態を管理

このように、カプセル化は単にprivateを使うだけでなく、システムの要求に応じて適切なレベルで実装する必要があります。



次のパート：[Part D - Java言語の基礎](chapter03d-java-basics.md)




<!-- Merged from chapter03d-java-basics.md -->


ここから、Javaを使ったオブジェクト指向プログラミングを学びますが、そもそもそれ以前に、Javaというプログラミング言語の基本構文について学びましょう。

## 3.1 型とリテラル

### 型

型、またはデータ型とは、プログラミング言語における「データを保持する形式」という認識で（いまのところは）良いでしょう。  
Javaにおける基本的な型の種類は以下の表を参照してください。

| 型名 | サイズ | 用途 | 想定される値 | ラッパクラス |
|-----|-----------------|-----|------------|--------------|
| `boolean` | -  | 真偽値 | true または false | Boolean
| `char` | 2byte | 文字(一文字分) | \u0000 〜 \uffff | Character
| `byte` | 1byte | 整数 | -128 〜 127 | Byte
| `short` | 2byte | 整数 | -32768 〜 32767 | Short
| `int` | 4byte | 整数 | -2147483648 〜 2147483647 | Integer
| `long` | 8byte | 整数 | -9223372036854775808 〜 9223372036854775807 | Long
| `float` | 4byte | 小数 | 1.4e-45 〜 3.4e+38 | Float
| `double` | 8byte | 小数 | 4.9e-324 〜 1.7e+308 | Double

これらの型は、**基本データ型**(primitive data type)または**プリミティブ型**と呼ばれます。

なお、int型では、おおよそ正負21億の整数が格納可能です。
ちなみに、`String`（文字列）は基本データ型ではなく、クラスです。
文字列に関しても、クラスという概念を学んだ後に、より詳しく学びます。

プログラミング初心者の方は、「（いまのところは）boolean、int、double、そしてString」で大丈夫です、と言っておきます。

### プリミティブ型

Javaには8つのプリミティブ型が存在します。先述の表を参照してください。
これらはオブジェクトではなく、単純な値を表現します。

メモリ効率を重視する場合や、高速な計算処理が必要な場合に使用されます。

### 参照型

プリミティブ型以外のすべての型は参照型です。これには以下が含まれます：

- クラス（String、Integer、自作のクラスなど）
- インターフェイス
- 配列
- 列挙型（enum）

```java
// プリミティブ型
int number = 42;
double price = 99.99;
boolean isValid = true;

// 参照型
String text = "Hello Java";
Integer boxedNumber = 100;  // Integerクラス（Wrapper）
int[] numbers = {1, 2, 3, 4, 5};  // 配列も参照型
```

### リテラル

リテラルとは、プログラムのソースコード内で直接表現される値のことです。

#### 整数リテラル

```java
int decimal = 100;      // 10進数
int binary = 0b1100100; // 2進数（0bプレフィックス）
int octal = 0144;       // 8進数（0プレフィックス）
int hex = 0x64;         // 16進数（0xプレフィックス）

// 数値の区切り（Java 7以降）
long largeNumber = 1_000_000_000L;  // 読みやすくするためのアンダースコア
```

#### 浮動小数点リテラル

```java
double normalNotation = 3.14159;
double scientificNotation = 3.14159e0;  // 科学記法
float floatValue = 3.14f;  // float型はfまたはFサフィックスが必要
```

#### 文字・文字列リテラル

```java
char singleChar = 'A';          // 文字リテラル（シングルクォート）
char unicodeChar = '\u0041';    // Unicode表現（'A'と同じ）
String text = "Hello World";    // 文字列リテラル（ダブルクォート）

// エスケープシーケンス
String escaped = "Line 1\nLine 2\t\"Quoted\"";
// \n: 改行、\t: タブ、\": ダブルクォート
```

#### 真偽値リテラル

```java
boolean isTrue = true;
boolean isFalse = false;
```

### 型変換

#### 暗黙的な型変換（拡大変換）

小さい型から大きい型への変換は自動的に行われます：

```java
int intValue = 100;
long longValue = intValue;    // int → long（自動変換）
double doubleValue = intValue; // int → double（自動変換）
```

#### 明示的な型変換（キャスト・縮小変換）

大きい型から小さい型への変換は明示的なキャストが必要です：

```java
double doubleValue = 3.14159;
int intValue = (int) doubleValue;  // 3になる（小数部分は切り捨て）

long longValue = 1000L;
int intValue2 = (int) longValue;   // 明示的なキャストが必要
```

**注意**: 縮小変換では情報の損失が発生する可能性があります。

### ラッパクラス

各プリミティブ型には対応するラッパクラスが存在します：

```java
// オートボクシング（Java 5以降）
Integer boxedInt = 100;  // int → Integer（自動変換）
Double boxedDouble = 3.14;  // double → Double（自動変換）

// アンボクシング
int primitiveInt = boxedInt;  // Integer → int（自動変換）
double primitiveDouble = boxedDouble;  // Double → double（自動変換）

// ラッパークラスの便利なメソッド
String numberStr = "123";
int parsedInt = Integer.parseInt(numberStr);  // 文字列から数値への変換
String binaryStr = Integer.toBinaryString(42); // "101010"
```

ラッパクラスは、プリミティブ型をオブジェクトとして扱う必要がある場合（コレクションフレームワークなど）に使用されます。



次のパート：[Part E - 演算子と式](chapter03e-operators.md)




<!-- Merged from chapter03e-operators.md -->


## 3.2 演算子

数値の計算や、文字列の連結など、さまざまな場所で演算子を使った処理を行います。  
すべての演算子が今すぐ使うものではありません。
いつ使うのか分からない演算子も、こういった書き方もできるということを覚えておきましょう。

### 代入演算子

変数へ値を代入するには、 `=` を使用します。

例：変数に代入を行う場合（aという変数に5を代入する）

```java
a = 5;
```

左辺に対して、右辺を代入します。逆はできません。

次は文法上可能ですが、あまり好ましくない例です。

```java
a = b = 5;
```

とすると、aには「b=5」が代入されるのですが、このとき「b=5」の処理結果は、5を返すため、a,bどちらにも5が代入されます。  
この書き方をたまにすることがありますが、本来ならコードを見た時に認識しづらい書き方だと思われるので極力使わないようにしましょう。

### 算術演算子

算術演算子には、以下のものがあります。

| 演算子記号 | 役割 | 算出時の優先順位
|----------|------|----|
| `+` | 足し算(加法演算子) | 後
| `-` | 引き算(加法演算子) | 後
| `*` | 掛け算(乗法演算子) | 先
| `/` | 割り算(乗法演算子) | 先
| `%` | 余り(乗法演算子) | 先

#### 算術演算子サンプルプログラム

算術演算子は、プログラミングの基本中の基本であり、数学的な計算、データ解析、アルゴリズムの実装など、あらゆるプログラムで使用されます。以下のプログラムは、Javaの基本的な算術演算子の動作を確認し、整数型の演算がどのように実行されるかを理解するための重要な学習材料です：

ファイル名： ArithmeticSample.java
```java
class ArithmeticSample {
  public static void main(String[] args) {
    int num1 = 10;
    int num2 = 5;

    System.out.println("num1とnum2にいろいろな演算を行います。");
    System.out.println("num1 + num2は" + (num1 + num2) + "です。");
    System.out.println("num1 - num2は" + (num1 - num2) + "です。");
    System.out.println("num1 * num2は" + (num1 * num2) + "です。");
    System.out.println("num1 / num2は" + (num1 / num2) + "です。");
    System.out.println("num1 % num2は" + (num1 % num2) + "です。");
  }
}
```

**このプログラムから学ぶ重要な数学的概念と演算特性：**

1. **整数除算の特性**：`num1 / num2`（10 / 5）の結果は2となりますが、これは整数どうしの除算では小数点以下が切り捨てられるためです。たとえば、num1を3、num2を2に変更すると、3 / 2 = 1（数学的には1.5）となります。

2. **剰余演算の重要性**：`%`演算子（モジュロ演算）は、プログラミングで非常に有用で、偶数・奇数の判定、循環処理の制御、ハッシュテーブルのインデックス計算などで頻繁に使用されます。

3. **演算子優先度の実践**：コード内の`(num1 + num2)`のような括弧は、文字列連結よりも先に算術演算を実行させるために必要です。括弧がないと、文字列連結が優先されて意図しない結果になります。

4. **型安全性とオーバーフロー**：int型の範囲内での演算である限り、このプログラムは安全に実行されます。しかし、很大きな数値を使用する場合は、オーバーフローの可能性を考慮する必要があります。

5. **コードの可読性とデバッグ**：各演算を個別のSytem.out.println文で出力することで、各演算の結果を個別に確認でき、デバッグや動作確認が容易になっています。

**学習のための発展的な実験：**

このプログラムを実行して基本動作を確認した後、以下のような値で実験してみることをお勧めします：

- **整数除算の特性理解**：num1=7, num2=3（7/3=2, 7%3=1）
- **負の数での演算**：num1=-10, num2=3（負の数の剰余演算の理解）
- **ゼロ除算のエラー**：num2=0に設定して、ArithmeticExceptionの発生を確認
- **大きな数値でのオーバーフロー**：num1=Integer.MAX_VALUE, num2=1での加算

これらの実験を通じて、Javaの数値演算の特性と限界を深く理解し、実際のプログラム開発で注意すべきポイントを学ぶことができます。

### 算術演算を伴う代入演算子（再帰代入演算子）

左辺に対して、右辺との算術演算を行った後に、左辺に代入する処理を省略して書くことができます。

| 演算子記号 | 役割 | 同じ処理で違う書き方
|----------|------|----|
| `+=` | 左辺に右辺を足し算した後に左辺へ代入 | `x += 5;`, `x = x + 5;`
| `-=` | 左辺に右辺を引き算した後に左辺へ代入 | `x -= 5;`, `x = x - 5;`
| `*=` | 左辺に右辺を掛け算した後に左辺へ代入 | `x *= 5;`, `x = x * 5;`
| `/=` | 左辺に右辺を割り算した後に左辺へ代入 | `x /= 5;`, `x = x / 5;`
| `%=` | 左辺に右辺の値で余りを算出した後に左辺へ代入 | `x %= 5;`, `x = x % 5;`

### 比較演算子

※両辺を比較する場合に使用し、演算子の結果はboolean型で戻ってきます。

| 演算子 | 例 | 説明
|----------|------|------|
| `==` | `a == b` | aとbが等しいときtrue
| `!=` | `a != b` | aとbが等しくないときtrue
| `>` | `a > b` | aがbより大きいときtrue
| `<` | `a < b` | aがbより小さいきとtrue
| `>=` | `a >= b` | aがb以上のときtrue
| `<=` | `a <= b` | aがb以下のときtrue

### 論理演算子(ショートサーキット演算子)

※両辺ともbooleanの場合に使用します。

| 演算子 | 例 | 説明
|----------|------|------|
| `&&` | `a && b` | aとbの両方がtrueのときtrue
| `||` | `a || b` | aとbのどちらかがtrueのときtrue
| `!` | `!a` | aがfalseのときtrue。逆になる

### インクリメント・デクリメント

インクリメント・デクリメントの演算子は、それぞれ2種類ずつあります。
この演算子を式中で使う場合、この違いがとても大きく、間違えやすい点ですので、注意が必要です。

#### インクリメント

| 演算子(位置に注意) | 式中で行われるものと同義のステートメント | 式中での評価、更新順序
|------|------|------|
| `y++` | `y = y + 1;` | y を評価してから式を演算し、 y + 1 に更新
| `++y` | `y = y + 1;` | y + 1 に更新してから式の評価、演算

#### デクリメント

| 演算子(位置に注意) | 式中で行われるものと同義のステートメント | 式中での評価、更新順序
|------|------|------|
| `y--` | `y = y - 1;` | y を評価してから式を演算し、 y - 1 に更新
| `--y` | `y = y - 1;` | y - 1 に更新してから式の評価、演算

### データ型による演算の制約

演算時の注意点として、データ型による丸めの発生があります。

- 整数型どうしの除算は、小数点以下切り捨て
    + 10 / 4 = 2
- どちらかに浮動小数点型の数値がある場合は、小数点以下も計算されます。
    + 10.0 / 4 = 2.5
    + この場合、計算結果は浮動小数点型となります。

### キャスト演算子

整数型の変数に、浮動小数点型の数値を代入しようとするとエラーとなります。

浮動小数点数を整数に***強制的に変換***し、代入をすればエラーとなりません。  
ただし、整数型に型の強制変換を行ったタイミングで、小数点以下の値は切り捨てられてしまうので注意が必要です。

```java
float s = 1234.567f;
int num = (int)s; // ← numには、1234 が代入される
```

### 文字列との連結

`+` を使います。

```
"Hello, " + "World!!" → "Hello, World!!"
```

次のプログラムはどのような結果を出力するでしょう？

```java
int a = 3;
int b = 5;
System.out.println("文字列と数値を結合すると..." + a + b);
```

出力結果：

```bash
文字列と数値を結合すると...35
```

基本的には、式中に文字列がある場合、その式が返す値はすべて文字列になります（ただし、括弧などにより、数値の演算のみを優先的に行うことは可能です）。
数値は暗黙的に文字列へ変換されてしまいます。
表示結果をコントロールしたい場合は、この暗黙的な型変換に注意してください。



次のパート：[Part F - 制御構造](chapter03f-control-structures.md)




<!-- Merged from chapter03f-control-structures.md -->


## 3.3 制御構造

プログラミングにおいて、最も重要で最もバグを出しやすい部分です。

### if文による条件分岐

#### if – 基本的な書き方

括弧内の条件（*boolean型*）に合致する（値が`true`）の場合に、波括弧`{ }`内のブロックの処理を行います。

```java
if ( 条件 ) {
    ここは条件に合致した場合にのみ実行される
}
```

波括弧のブロックは、実行する処理が1行の場合のみ省略が可能ですが、波括弧は省略しない方が良いです。

もし、条件に合致した場合実行する処理が1行だけのとき、次の書き方のように改行を挟まず1行で書くようにすると分かりやすいです。

```java
if ( 条件 ) ここは条件に合致した場合にのみ実行される
```

波括弧を省略してほしくない理由は、次のようなパターンが考えられるからです。

```java
if ( 条件 )
    最初にif文を書いた人が書いた条件内のコード
```

これは正常に動きます。  
次に、このコードを改修する際、以下のようなコードの修正を行ってしまいやすい点が波括弧を省略した際に起こりやすいです。

```java
if ( 条件 )
    最初にif文を書いた人が書いた条件内のコード
    追加された条件内コードのつもりで書かれたコード
```

このとき、追加されたコードは、ifの条件にまったく関係なく処理が実行されてしまい、これが意図しない処理としてバグにつながります。
すぐに気付けるようだったら良いのですが、追加されたコードの影響が、かなり後の方（たとえばリリース後など）で気付く状況も少なからずあります。
そもそも波括弧を省略していなければ防げているバグを発生させないためにも、必ず波括弧を書きましょう。

少し逸れますがバグの混入を防ぐ話として、インデントをそろえることも重要です。  
インデントはタブでもスペースでもかまいませんが、コードブロックが分かりやすくなるように適切な字下げをしましょう。  
また、インデントに関していえば、IDEに搭載されているコードフォーマッタを使うのもよいです。自動的にコードの整形を行ってくれるため、先ほどの追加コードのような不具合もコードの整形によって気付きやすくなります。

#### if-else – どちらかを実行

ifの条件に合致しなかった場合のみ実行される処理を書ける

```java
if ( 条件 ) {
    ここは条件に合致した場合にのみ実行される
} else {
    条件に合致しなかった場合にのみ実行される
}
```

#### if-elseif-else – 複数の条件

条件に合致しなかった場合、再度評価を行うこともできる

```java
if ( 条件1 ) {
    条件1に合致した場合にのみ実行される
} else if ( 条件2 ) {
    条件1に合致せず、条件2に合致した場合
} else {
    上記すべての条件に合致しなかった場合
}
```

#### 比較演算子と論理演算子を組み合わせた複数条件指定

if文の条件は、条件1かつ条件2と言ったように、1つのif文で複数の条件を入れることが可能です。

###### AND（〜かつ〜）
###### AND（〜かつ〜）

```java
if ( 条件１ && 条件２ ) {
    条件１と条件２どちらにも合致した場合にのみ実行される
}
```

###### OR（〜または〜）
###### OR（〜または〜）

```java
if ( 条件１ || 条件２ ) {
    条件１か条件２のどちらかに合致した場合にのみ実行される
}
```

#### 条件分岐の実践例

論理演算子を使った複雑な条件分岐は、実際のビジネスロジックを実装する際に頻繁に使用されます。以下のプログラムは、会員システムの割り引き判定という実用的な例を通じて、論理演算子の効果的な使用方法を学習するための材料です：

ファイル名： MembershipDiscount.java

```java
public class MembershipDiscount {
    public static void main(String[] args) {
        // 顧客情報の設定
        int age = 25;
        boolean isPremiumMember = true;
        int purchaseAmount = 5000;
        int membershipYears = 3;
        
        System.out.println("=== 顧客情報 ===");
        System.out.println("年齢: " + age + "歳");
        System.out.println("プレミアム会員: " + (isPremiumMember ? "はい" : "いいえ"));
        System.out.println("購入金額: " + purchaseAmount + "円");
        System.out.println("会員歴: " + membershipYears + "年");
        System.out.println();
        
        // 複数の割引条件を論理演算子で組み合わせた判定
        System.out.println("=== 割引判定結果 ===");
        
        // 条件1: シニア割引（65歳以上）
        if (age >= 65) {
            System.out.println("シニア割引が適用されます（20%オフ）");
        }
        
        // 条件2: プレミアム会員かつ高額購入
        if (isPremiumMember && purchaseAmount >= 3000) {
            System.out.println("プレミアム会員高額購入割引が適用されます（15%オフ）");
        }
        
        // 条件3: 若年層または長期会員
        if (age <= 25 || membershipYears >= 5) {
            System.out.println("若年層・長期会員割引が適用されます（10%オフ）");
        }
        
        // 条件4: 複雑な組み合わせ条件
        if ((isPremiumMember && purchaseAmount >= 5000) || 
            (age >= 60 && membershipYears >= 3)) {
            System.out.println("特別VIP割引が適用されます（25%オフ）");
        }
        
        // 条件5: 除外条件を含む複雑な判定
        if (purchaseAmount >= 10000 && age >= 20 && age <= 60 && !isPremiumMember) {
            System.out.println("一般会員高額購入割引が適用されます（5%オフ）");
        }
    }
}
```

**このプログラムから学ぶ重要な概念：**

1. **論理AND演算子（&&）の実用性**：「プレミアム会員かつ高額購入」のように、複数の条件を同時に満たす場合の判定に使用します。ビジネスルールでは「すべての条件を満たす」ケースが頻繁にあります。

2. **論理OR演算子（||）の柔軟性**：「若年層または長期会員」のように、いずれかの条件を満たせば良い場合に使用します。顧客の多様なニーズに対応するための重要なしくみです。

3. **複雑な条件の構築**：括弧を使用することで、より複雑なビジネスロジックを表現できます。例：「（プレミアム会員で高額購入）または（シニアで長期会員）」

4. **論理NOT演算子（!）の活用**：`!isPremiumMember`のように、特定の条件を除外する際に使用します。「プレミアム会員ではない一般会員に対する特別オファー」などの実装に有用です。

5. **短絡評価（Short-circuit evaluation）の重要性**：`&&`演算子では、左の条件がfalseの場合、右の条件は評価されません。`||`演算子では、左の条件がtrueの場合、右の条件は評価されません。これにより、効率的で安全な条件判定が可能になります。

**実用的な応用場面：**

- **ECサイトの価格計算システム**：顧客属性にもとづいた動的割り引き計算
- **アクセス制御システム**：ユーザーの権限や属性にもとづいた機能制限
- **ゲームの進行条件**：プレイヤーのレベル、アイテム所持状況にもとづいた分岐
- **在庫管理システム**：商品の種類、在庫量、季節性を考慮した発注判定

**論理演算子使用時の注意点：**

1. **可読性の確保**：複雑な条件は適切に括弧でグループ化し、意図を明確にする
2. **ド・モルガンの法則**：`!(A && B)` = `!A || !B`、`!(A || B)` = `!A && !B`の理解
3. **null安全性**：オブジェクトの参照がnullでないことを最初に確認する習慣

#### 三項演算子

三項演算子は、`if...else...`を式として扱えます。

```java
System.out.println(条件 ? "true" : "false");
//条件に合致していればtrueと表示されます。合致していなければfalseと表示されます。

//↑の処理と同じように書く場合↓
if ( 条件 ) {
    System.out.println("true");
} else {
    System.out.println("false");
}
```

### switch文による複数の選択肢の比較

「`==`」を条件としたif文を複数書くような場合は、switch文を使って記述できます。

switchの括弧内の変数に入っている値がcaseの値に該当した時、処理を実行します。

```java
switch ( 検査対象の値 ) {
    case 値1:
        検査対象の値が値１だった場合の処理
        break;
    case 値2:
        検査対象の値が値２だった場合の処理
        break;
    default:
        いずれのcaseに合致しない場合
        break;
}
```

breakが書かれていない場合は、その下にあるcaseの処理も実行されます。  
これは多くのプログラマにとって期待していない処理ですので、必ずcaseにはbreakを入れましょう。

#### 拡張switch文（switch式）：モダンJavaの新機能

Java 14以降、switch文が大幅に強化され、より安全で簡潔な記述が可能になりました。

```java
// 従来のswitch文
String dayType;
switch (day) {
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
        dayType = "平日";
        break;
    case 6:
    case 7:
        dayType = "休日";
        break;
    default:
        dayType = "無効な曜日";
}

// 新しいswitch式（Java 14以降）
int day = 3;
String dayType = switch (day) {
    case 1, 2, 3, 4, 5 -> "平日";
    case 6, 7 -> "休日";
    default -> "無効な曜日";
};
```

**switch式のポイント:**

*   **`->`（アロー）ラベル:** `case`の後にコロン`:`の代わりにアロー`->`を使います。
*   **`break`不要:** アローの右側が単一の式かブロックの場合、`break`は不要です。これにより、`break`の書き忘れによるバグ（フォールスルー）を完全に防ぐことができます。
*   **値を返す:** `switch`式全体が値を返すため、結果を直接変数に代入できます。
*   **網羅性のチェック:** `default`節がない場合など、すべての可能性を網羅していないとコンパイルエラーになります（`enum`型を`switch`する場合など）。これにより、条件漏れを防ぐことができます。
*   **複数行の処理と`yield`**: `case`の処理が複数行にわたる場合は、ブロック`{}`で囲み、`yield`キーワードを使って値を返します。

```java
// yieldを使った例
String message = switch (day) {
    case 1, 2, 3, 4, 5 -> {
        System.out.println("今日は頑張る日！");
        yield "平日"; // ブロックから値を返す
    }
    case 6, 7 -> "休日";
    default -> "無効な曜日";
};
```

**パターンマッチング for switch (Java 17以降):**
`switch`で変数の型をチェックし、そのままその型の変数として利用できます。

```java
Object obj = "Hello";
String formatted = switch (obj) {
    case Integer i -> String.format("Integer: %d", i);
    case String s -> String.format("String: %s", s);
    case null -> "It's null"; // nullもcaseとして扱える
    default -> "Unknown type";
};
System.out.println(formatted); // "String: Hello"
```
この機能により、冗長な`if-else`の`instanceof`チェインを、安全で読みやすい`switch`式に置き換えることができます。

#### switch文の実践例

switch文は、特定の値にもとづいた分岐処理に優れており、特に列挙型のような限定された値セットを扱う場合に威力を発揮します。以下のプログラムは、成績管理システムという実用的な例を通じて、switch文の効果的な使用方法を学習するための材料です：

ファイル名： GradeCalculator.java

```java
public class GradeCalculator {
    public static void main(String[] args) {
        // テストケースとして複数の成績を評価
        char[] grades = {'A', 'B', 'C', 'D', 'F', 'B'};
        int totalGradePoints = 0;
        int courseCount = grades.length;
        
        System.out.println("=== 成績評価システム ===");
        System.out.println("履修科目数: " + courseCount + "科目");
        System.out.println();
        
        for (int i = 0; i < grades.length; i++) {
            char grade = grades[i];
            int gradePoint;
            String evaluation;
            
            // switch文による成績の詳細評価
            switch (grade) {
                case 'A':
                    gradePoint = 4;
                    evaluation = "優秀（Excellent）";
                    break;
                case 'B':
                    gradePoint = 3;
                    evaluation = "良好（Good）";
                    break;
                case 'C':
                    gradePoint = 2;
                    evaluation = "普通（Average）";
                    break;
                case 'D':
                    gradePoint = 1;
                    evaluation = "要努力（Below Average）";
                    break;
                case 'F':
                    gradePoint = 0;
                    evaluation = "不合格（Fail）";
                    break;
                default:
                    gradePoint = 0;
                    evaluation = "無効な成績";
                    System.out.println("警告: 無効な成績 '" + grade + "' が検出されました");
                    break;
            }
            
            totalGradePoints += gradePoint;
            System.out.println("科目" + (i + 1) + ": " + grade + "(" + gradePoint + "点) - " + evaluation);
        }
        
        System.out.println();
        
        // GPA計算と総合評価
        double gpa = (double) totalGradePoints / courseCount;
        System.out.println("総ポイント: " + totalGradePoints + "点");
        System.out.printf("GPA: %.2f\n", gpa);
        
        // GPAにもとづく総合評価（switch文の応用）
        int gpaCategory = (int) gpa; // 小数点以下切り捨て
        
        System.out.print("総合評価: ");
        switch (gpaCategory) {
            case 4:
                System.out.println("最優秀（Summa Cum Laude）- 学長表彰対象");
                break;
            case 3:
                System.out.println("優秀（Magna Cum Laude）- 学部長表彰対象");
                break;
            case 2:
                System.out.println("良好（Cum Laude）- 追加指導不要");
                break;
            case 1:
                System.out.println("要注意（Academic Warning）- 学習指導が必要");
                break;
            case 0:
                System.out.println("要改善（Academic Probation）- 緊急学習支援が必要");
                break;
            default:
                System.out.println("計算エラー");
                break;
        }
        
        // 複数caseラベルの活用例（奨学金適用条件）
        System.out.print("奨学金適用: ");
        switch (gpaCategory) {
            case 4:
            case 3:
                System.out.println("特待生奨学金適用対象");
                break;
            case 2:
                System.out.println("一般奨学金適用対象");
                break;
            default:
                System.out.println("奨学金適用対象外");
                break;
        }
    }
}
```

**このプログラムから学ぶ重要な概念：**

1. **switch文の適用場面**：文字や整数などの離散的な値による分岐において、if-else文よりも読みやすく、効率的な処理が可能です。特に「成績」「曜日」「月」「状態」などの列挙可能な値に最適です。

2. **break文の重要性**：各caseの最後にbreak文を配置することで、意図しないfall-through動作を防ぎます。break文を忘れると、次のcaseも実行されてしまう重大なバグの原因となります。

3. **default句の活用**：予期しない値に対する安全な処理を提供します。入力検証やエラーハンドリングの重要な要素として機能します。

4. **複数caseラベルの実用性**：同じ処理を複数の値で実行したい場合、case文を連続して記述することで、コードの重複を避けられます。

5. **型安全性とパフォーマンス**：switch文は、コンパイル時に値のチェックが行われ、実行時は効率的なジャンプテーブルとして最適化される場合があります。

**実用的な応用場面：**

- **状態管理システム**：注文状態（待機中、処理中、発送済み等）の処理分岐
- **ゲーム開発**：プレイヤーの行動（移動、攻撃、防御等）による処理分岐
- **ユーザーインターフェイス**：メニュー選択やボタンクリックにもとづいた機能実行
- **データ変換処理**：ファイル形式やデータ型による変換処理の分岐

**switch文使用時のベストプラクティス：**

1. **必ずdefault句を含める**：予期しない値に対する適切な処理を提供
2. **各caseにbreak文を配置**：意図しないfall-throughを防ぐ
3. **複雑な処理はメソッドに分割**：各caseの処理が長くなる場合は、別メソッドに委譲
4. **列挙型（enum）との組み合わせ**：型安全性をさらに向上させるため、可能な限りenum型を使用

### String型の内容評価について

JavaのString型は、プログラミング初心者が遇遇する最も細かい罠の1つであり、同時にJavaのメモリ管理とオブジェクト指向の本質を理解するための絶好の教材でもあります。以下のプログラムは、String型の特別な性質と、参照比較と値比較の違いを具体的に示しています。

このサンプルを通じて、Javaの文字列プール機能、オブジェクトの同一性、メモリ管理のしくみを理解しましょう：

```java
public class StringEval {
  public static void main(String[] args) {

    String a = "Hello";
    String b = "Hello";

    if (a == b) {
      System.out.println("同じだよ！");  // こっち
    } else {
      System.out.println("違うよ！");
    }

    // a,b両方に処理を加えて値を変化させる
    a += 1;
    b += 1;
    System.out.println("a:" + System.identityHashCode(a));
    System.out.println("b:" + System.identityHashCode(b));

    if (a == b) {
      System.out.println("同じだよ！");
    } else {
      System.out.println("違うよ！");  // こっち
    }

    if (a.equals(b)) {
      System.out.println("equalsなら同じだよ！");  // こっち
    } else {
      System.out.println("equalsでも違うよ！");
    }
  }
}
```

**このプログラムが示す重要な概念：**

1. **文字列プール（String Pool）の存在**：最初の`a == b`がtrueになるのは、Javaが同じ文字列リテラル"Hello"を自動的に共有するためです。これは文字列プールというしくみにより、メモリ効率を向上させています。

2. **参照の同一性 vs 値の同等性**：
   - `==`演算子：2つの変数が同じオブジェクトを参照しているかをチェック（参照の同一性）
   - `equals()`メソッド：2つのオブジェクトの内容が同じかをチェック（値の同等性）

3. **Stringの不変性（Immutability）**：`a += 1`のような操作は、既存の文字列を変更するのではなく、新しい文字列オブジェクトを生成します。これがSystem.identityHashCode()で異なる値を示す理由です。

4. **メモリ効率の考慮**：文字列プールの存在により、同じ内容の文字列リテラルを複数ヵ所で使用してもメモリを節約できます。ただし、動的に生成された文字列（演算結果など）は自動的にプールには入りません。

5. **プログラミングのベストプラクティス**：文字列の比較には常に`equals()`メソッドを使用すべきです。`==`での比較は、意図しない結果を引き起こす可能性があります。

**実用的な注意点：**

```java
// 良い例
if (userInput.equals("yes")) {
    // 処理
}

// 悪い例（バグの原因）
if (userInput == "yes") {
    // userInputが動的に生成された場合、falseになる可能性大
}

// null安全な比較
if ("yes".equals(userInput)) {
    // userInputがnullでもNullPointerExceptionを回避
}
```

このような文字列の特性を理解することは、Javaプログラマとして必須の知識であり、多くのバグを未然に防ぐことができます。

### 単純な繰り返し

#### while

while文の括弧内で指定された条件に、合致している間は処理を繰り返します。

```java
while ( 条件 ) {
    条件が真値(true)の間、繰り返す処理
}
```

###### 10回繰り返す
###### 10回繰り返す

while文では、繰り返しの条件となるものが必要となります。

```java
int count = 1;
while ( count <= 10 ) {
    繰り返す処理をここに書きます。
    count += 1;
}
```

#### do…while

while文なんだけど、条件の検査が波括弧を閉じるタイミングで行われるため、波括弧内の処理は、条件にかかわらず一度は実行され繰り返し処理です。

```java
do {
    処理を実行後、条件が真値の場合には何度も繰り返す
} while ( 条件 ); //セミコロンを忘れずに付けましょう！
```

存在を忘れがちですので、覚えておきましょう。

###### 10回繰り返す
###### 10回繰り返す

```java
int count = 1;
do {
    繰り返す処理 (countの初期値が11だった場合でも処理は実行されます)
    count += 1;
} while ( count <= 10 );
```

#### for

whileループの繰り返し条件用の数値型変数を簡略化して書けるループ処理です。

初期化された変数が、波括弧内の処理を繰り返す都度、指定された変化ルールに従って変化します。  
変化した値が繰り返し条件に合致しなくなった場合、繰り返しから抜けられます。

```java
for (変数の初期化; 繰り返し条件; 繰り返し時の変化) {
    繰り返す処理
}
```

###### 10回繰り返す
###### 10回繰り返す

```java
for (int count = 1; count <= 10; count++) {
    繰り返す処理
}
```

### 繰り返し処理中に例外的な処理を行う

#### 繰り返し中に別の条件でループを抜けたい - break

ループ処理に限らず、ブロックから抜け出すことができる命令としてbreakがあります。

for文を例にした場合：

```java
for (変数の初期化; 繰り返し条件; 繰り返し時の変化) {
    繰り返す処理
    if ( 別の条件 ) break;
}
```

#### 一度だけ処理を飛ばして、次の繰り返しを行う - continue

ループ処理中に、continueが実行されると、それ以降の処理は1回の繰り返し処理時のみ飛ばして次の繰り返し処理が実行されます。

```java
for (変数の初期化; 繰り返し条件; 繰り返し時の変化) {
    繰り返す処理
    if ( 別の条件 ) continue;
    別の条件に合致する場合、実行されない処理
}
```



次のパート：[Part G - 配列と文字列](chapter03g-arrays-strings.md)




<!-- Merged from chapter03h-static-modifier.md -->


## 3.5 static修飾子：インスタンス不要の共有メンバー

これまでに見てきたクラスのフィールドやメソッドは、すべて`new`キーワードによってオブジェクト（インスタンス）を生成してからでないと利用できませんでした。これらを「**インスタンスメンバー**」と呼びます。

しかし、Javaにはインスタンスを生成しなくても利用できる特別なメンバーが存在します。それが`static`修飾子を付けた「**クラスメンバー**」または「**静的メンバー**」です。

### インスタンスメンバー vs クラスメンバー

この2つの違いを理解することは、オブジェクト指向プログラミングにおいて非常に重要です。

*   **インスタンスメンバー（`static`なし）**
    *   **所有者**: 個々のインスタンス
    *   **メモリ**: インスタンスが生成されるたびに、そのインスタンス専用の領域が確保される。
    *   **アクセス**: `インスタンス変数名.メンバー名`
    *   **比喩**:「**住宅の各戸が持つ家具**」。Aさんの家のテーブルとBさんの家のテーブルは別物です。

*   **クラスメンバー（`static`あり）**
    *   **所有者**: クラスそのもの
    *   **メモリ**: クラスがロードされる時に一度だけ、クラス共有の領域が確保される。
    *   **アクセス**: `クラス名.メンバー名`
    *   **比喩**:「**マンションの共有掲示板**」。どの部屋の住人が見ても、掲示板は1つであり、そこに書かれた情報は全住人で共有されます。

### メモリ上のイメージ

```
【メモリ空間】
+-----------------------------------------------------------------+
| **クラス領域（共有）**                                          |
| +-------------------------------------------------------------+ |
| | **Toolクラス**                                              | |
| |   `static String sharedInfo = "共有情報";`  <-- 1つだけ存在 | |
| |   `static void showSharedInfo()`                           | |
| +-------------------------------------------------------------+ |
+-----------------------------------------------------------------+
| **ヒープ領域（インスタンスごと）**                              |
| +---------------------------+ +---------------------------+     |
| | **tool1インスタンス**     | | **tool2インスタンス**     |     |
| | `String instanceName;`    | | `String instanceName;`    |     |
| | `void showInstanceName()` | | `void showInstanceName()` |     |
| +---------------------------+ +---------------------------+     |
+-----------------------------------------------------------------+
```

### `static`メンバーの実践例

`static`フィールドと`static`メソッドの具体的な使い方を見てみましょう。

```java
// StaticMemberExample.java
class Tool {
    // インスタンスメンバー（各Toolインスタンスが個別に持つ）
    String name;

    // クラスメンバー（Toolクラス全体で共有される）
    static int toolCount = 0;

    // コンストラクタ
    public Tool(String name) {
        this.name = name;
        toolCount++; // インスタンスが作られるたびに、共有カウンタを増やす
        System.out.println(this.name + " が作成されました。現在のツール総数: " + toolCount);
    }

    // インスタンスメソッド
    public void showName() {
        System.out.println("このツールの名前は " + this.name + " です。");
    }

    // クラスメソッド
    public static void showToolCount() {
        // System.out.println(this.name); // エラー！ staticメソッド内ではインスタンスメンバー(name)は使えない
        System.out.println("作成されたツールの総数は " + toolCount + " です。");
    }
}

public class StaticMemberExample {
    public static void main(String[] args) {
        System.out.println("--- ツール作成前 ---");
        // インスタンスがなくてもクラスメソッドは呼び出せる
        Tool.showToolCount();

        System.out.println("\n--- ツール作成 ---");
        Tool hammer = new Tool("ハンマー");
        Tool wrench = new Tool("レンチ");

        System.out.println("\n--- 各ツールの情報表示 ---");
        hammer.showName(); // インスタンスメソッドの呼び出し
        wrench.showName();

        System.out.println("\n--- ツール総数の再確認 ---");
        // クラス名経由でのアクセスが推奨
        Tool.showToolCount();
    }
}
```

**実行結果:**
```
--- ツール作成前 ---
作成されたツールの総数は 0 です。

--- ツール作成 ---
ハンマー が作成されました。現在のツール総数: 1
レンチ が作成されました。現在のツール総数: 2

--- 各ツールの情報表示 ---
このツールの名前は ハンマー です。
このツールの名前は レンチ です。

--- ツール総数の再確認 ---
作成されたツールの総数は 2 です。
```

### `static`の重要な制約

`static`メソッド内では、以下の制約があります：

1. **インスタンスメンバーにアクセスできない**: `this`キーワードを使うことができず、インスタンスフィールドやインスタンスメソッドを直接呼び出せません。
2. **staticメンバーのみアクセス可能**: staticメソッドやstaticフィールドのみ使用できます。

```java
class Example {
    String instanceField = "インスタンス";
    static String staticField = "スタティック";

    void instanceMethod() {
        System.out.println(instanceField);  // OK
        System.out.println(staticField);    // OK
    }

    static void staticMethod() {
        // System.out.println(instanceField);  // エラー！
        System.out.println(staticField);        // OK
        // instanceMethod();                    // エラー！
    }
}
```

### `static`の実用的な使用例

#### 1. ユーティリティクラス

数学関数や文字列処理など、状態を持たない汎用的な処理を提供するクラス：

```java
public class MathUtils {
    // privateコンストラクタでインスタンス化を防ぐ
    private MathUtils() {}

    public static double calculateCircleArea(double radius) {
        return Math.PI * radius * radius;
    }

    public static int factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }
}

// 使用例
double area = MathUtils.calculateCircleArea(5.0);
```

#### 2. 定数の定義

プログラム全体で共有される定数値：

```java
public class Constants {
    public static final double TAX_RATE = 0.10;  // 消費税率
    public static final int MAX_RETRY_COUNT = 3;  // 最大リトライ回数
    public static final String DEFAULT_ENCODING = "UTF-8";
}
```

#### 3. Singletonパターン

アプリケーション全体で1つしか存在しないインスタンスを保証する設計パターン：

```java
public class DatabaseConnection {
    private static DatabaseConnection instance;

    private DatabaseConnection() {
        // プライベートコンストラクタ
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
}
```

### `static`使用時のベストプラクティス

1. **過度な使用を避ける**: `static`を多用するとオブジェクト指向の利点が失われます
2. **状態を持たない処理に使用**: ユーティリティメソッドなど、インスタンスの状態に依存しない処理に適しています
3. **定数にはfinalと併用**: 変更不可能な定数には`static final`を使用します
4. **スレッドセーフティに注意**: staticフィールドは全スレッドで共有されるため、同期処理が必要な場合があります

### まとめ

`static`修飾子は、クラスレベルでデータや機能を共有する強力な機能です。しかし、オブジェクト指向プログラミングの本質は「データと振る舞いをカプセル化したオブジェクトの相互作用」にあるため、`static`の使用は慎重に行うべきです。適切に使用することで、より効率的で保守性の高いプログラムを作成できます。



次のパート：[Part I - 配列](chapter03i-arrays.md)




<!-- Merged from chapter03i-arrays.md -->


## 3.6 配列：要素を効率的に管理する

### 配列とは？

配列とは、***変数をまとめて管理する***しくみです。
その存在と利用法については、すでにC言語で学習しているものとして、Javaでの書き方について確認しましょう。

#### 配列を使う場面

複数の同じような値を同じような計算などで処理する場合に、配列にまとめて管理すると処理を行いやすいです。

前述のとおり、Javaはオブジェクト指向言語ではありますが、配列もサポートしています。
(C++と同じスタンス）

今でこそ、後の章で紹介されるコレクションAPIを使って配列よりも楽にデータ管理ができます。しかし、処理速度を優先したい場合や、プリミティブ型での要素管理をしたい場合などでは、配列を使うケースがあります。

配列の短所としては、サイズが決まっていること（宣言時またはインスタンス化時に決まる）など、管理がたいへんなケースがあるということです。また、配列内の要素の数が変わるような処理を行う場合、配列は向いていません。

### 配列の宣言

#### 書き方パターン1（推奨）

```java
型[] 配列名;
```

#### 書き方パターン2（C言語と同じ）

```java
型 配列名[];
```

配列の場合、型、配列変数名（識別子）に加え、`[]`を付けます。Javaの場合、配列であることが分かりやすいため、型の後に`[]`をつけることが推奨されています。

```java
// intの配列を宣言する
int[] scores;
```

### 配列の領域確保

宣言しただけでは、配列の要素を格納する領域（どの程度の要素数が必要か）がまだ決まっていません。
以下の書き方で、どの程度の要素数を格納する領域を確保するかを決定します。

```java
配列名 = new 型[要素数];
```

例：
```java
scores = new int[5];
```

配列の宣言と領域の確保を同時に行う場合は以下のように書けます。

```java
int[] scores;		      // 宣言
scores = new int[5];	// 領域を5つ確保する
// ↓ 宣言と同時に領域確保
int[] scores = new int[5];
```

配列の宣言と、領域の確保、値の代入をまとめて行う場合は、`{ }`を使い、リテラルをカンマ区切りで必要数分記入します。

```java
int[] scores = {74, 88, 98, 53, 25};
```

###### 配列の領域数（要素数）を取得
###### 配列の領域数（要素数）を取得

確保された領域の数を取得するには、配列の変数の`length`を参照します。

```java
int[] scores = {74, 88, 98, 53, 25};
System.out.println(scores.length);    // 5と表示
```

for文の条件や、配列の要素の最後にアクセスする場合などに使用できます。

```java
int[] scores = {74, 88, 98, 53, 25};

// 要素の最後にアクセス
System.out.println(scores[scores.length - 1]); //25と表示される

// 各要素を順に表示
for (int i = 0; i < scores.length; i++) {
  System.out.print(scores[i] + ", "); // 74, 88, 98, 53, 25と表示される。
}
```

#### 複数次元の配列(多次元配列)

先ほどまでのは、1次元配列と呼ばれるものです。要素は単一のインデックス（整数値による連番：ゼロからスタート）でアクセスできます。
それに対して、複数の次元を持たせた配列を使うこともできます。

```java
// 教科ごとの点数(1次元)を生徒ごと(2次元)で管理したい
int[][] scoresOfStudents = new int[3][5];
```

必要な次元の数だけ`[]`をつなげます。

###### 多次元配列における各要素へのアクセス
###### 多次元配列における各要素へのアクセス

基本は1次元配列と同じです。

```java
// 一人目の点数を代入
scoresOfStudents[0][0] = 100;
scoresOfStudents[0][1] = 70;
scoresOfStudents[0][2] = 50;
scoresOfStudents[0][3] = 98;
scoresOfStudents[0][4] = 45;
// 二人目
scoresOfStudents[1][0] = 70;
scoresOfStudents[1][1] = 70;
scoresOfStudents[1][2] = 45;
scoresOfStudents[1][3] = 68;
scoresOfStudents[1][4] = 70;

// ...以下省略
```

###### 多次元配列もまとめて初期化可能
###### 多次元配列もまとめて初期化可能

配列内部の`{}`をカンマで区切ることで、多次元の配列を同時に宣言、初期化、代入ができます。

```java
int[][] scoresOfStudents = {
    {70, 60, 80, 90, 50},
    {81, 45, 32, 78, 100},
    {32, 44, 34, 55, 70},
};
```

###### 多次元配列の領域数を取得
###### 多次元配列の領域数を取得

先ほどの多次元配列にて

```java
System.out.println(scoresOfStudents.length);    //3と表示
```

次元ごとの領域数を取得するには、

```java
System.out.println(scoresOfStudents[0].length);    //5と表示
```

### 配列の実践的な使用例

#### 拡張for文（for-each文）

Java 5から導入された拡張for文を使うと、配列の要素を簡潔に反復処理できます：

```java
int[] scores = {74, 88, 98, 53, 25};

// 従来のfor文
for (int i = 0; i < scores.length; i++) {
    System.out.println("点数: " + scores[i]);
}

// 拡張for文（推奨）
for (int score : scores) {
    System.out.println("点数: " + score);
}
```

#### 配列のコピー

配列をコピーする場合は、`System.arraycopy()`メソッドや`Arrays.copyOf()`メソッドを使用します：

```java
int[] original = {1, 2, 3, 4, 5};
int[] copy1 = new int[original.length];

// System.arraycopy()を使用
System.arraycopy(original, 0, copy1, 0, original.length);

// Arrays.copyOf()を使用（より簡潔）
int[] copy2 = Arrays.copyOf(original, original.length);
```

#### 配列のソート

`Arrays.sort()`メソッドを使って配列を簡単にソートできます：

```java
int[] numbers = {5, 2, 8, 1, 9};
Arrays.sort(numbers);
// numbers は {1, 2, 5, 8, 9} になる
```

### 配列使用時の注意点

1. **配列の境界チェック**: 配列の範囲外アクセスは`ArrayIndexOutOfBoundsException`を引き起こします
2. **参照の共有**: 配列は参照型ですので、代入は参照のコピーになります
3. **サイズの固定**: 一度作成した配列のサイズは変更できません

```java
// 範囲外アクセスの例（エラーになる）
int[] arr = new int[5];
// arr[5] = 10;  // エラー！インデックスは0-4まで

// 参照の共有の例
int[] arr1 = {1, 2, 3};
int[] arr2 = arr1;  // 参照のコピー
arr2[0] = 99;
System.out.println(arr1[0]);  // 99と表示される
```

### まとめ

配列は、同じ型の複数の値を効率的に管理するための基本的なデータ構造です。固定サイズという制約はありますが、メモリ効率が良く、高速なアクセスが可能です。より柔軟なデータ構造が必要な場合は、後の章で学習するコレクションフレームワーク（`ArrayList`など）の使用を検討してください。



次のパート：[Part J - 章末演習](chapter03j-exercises.md)




<!-- Merged from chapter03j-exercises.md -->


## 3.7 章末演習

本章で学んだクラス、メソッド、配列、制御構造の知識を総動員して、以下の演習問題に取り組んでください。これらの課題は、単に文法を理解するだけでなく、実際にプログラムを設計し、問題を解決する能力を養うことを目的としています。

### 演習1：メソッド作成練習

この演習は、メソッドの定義、引数、戻り値の基本的な使い方に慣れるためのものです。プログラムとして直接的な意味は持ちませんが、メソッド設計の基礎を固める上で重要です。

**技術的背景：メソッド設計の重要性**

実際のソフトウェア開発では、1つのプログラムが数千、数万行のコードから構成されることは珍しくありません。このような大規模なプログラムを1つの巨大なmainメソッドに書くことは不可能です。そこで、プログラムを適切な単位（メソッド）に分割することが不可欠となります。

メソッドを適切に設計することで以下のメリットが得られます：
- **コードの再利用性**：同じ処理を何度も書かずに済む
- **保守性の向上**：バグ修正や機能変更が局所的に行える
- **可読性の向上**：処理の意味が明確になり、ほかの開発者が理解しやすい
- **テストの容易性**：単位テストにより品質を保証できる

この演習では、引数なし・戻り値なし、引数あり・戻り値あり、さらに特殊な処理（遅延実行）を含むメソッドなど、さまざまなパターンを実装することで、メソッド設計の基本的な型を身につけます。

**1. クラスの準備**

`MethodsPractice.java`というファイル名で、`MethodsPractice`クラスを作成してください。

**2. メソッドの実装**

`MethodsPractice`クラス内に、以下の仕様に従って4つのメソッドを実装してください。

**2-1. 金額表示メソッド**

-   **メソッド名:** `printCurrency`
-   **戻り値:** `void`
-   **引数:** `double`型の金額
-   **処理内容:** 受け取った金額を、`System.out.printf("%.2f\n", ...)` を使って小数点以下2桁の形式で画面に出力します。

**2-2. 整数加算メソッド**

-   **メソッド名:** `add`
-   **戻り値:** `int`
-   **引数:** `int`型の数値2つ
-   **処理内容:** 2つの引数を加算した結果を返します。

**2-3. 整数除算メソッド**

-   **メソッド名:** `division`
-   **戻り値:** `double`
-   **引数:** `int`型の数値2つ
-   **処理内容:** 1つ目の引数を2つ目の引数で除算した結果を、`double`型で返します。整数除算で結果が切り捨てられないように注意してください（例： `1 / 2` が `0.5` となるようにする）。

**2-4. 「生命、宇宙、そして万物についての究極の疑問の答え」を返すメソッド**

-   **メソッド名:** `getTheAnswerToTheUltimateQuestionOfLife_TheUniverse_And_Everything`
-   **戻り値:** `String`
-   **引数:** なし
-   **処理内容:** 750ミリ秒待機した後に、文字列 `"42"` を返します。
    -   **ヒント:** 750ミリ秒待機するには、以下のコードを使用します。例外処理については後の章で詳しく学びますが、今は「おまじない」として利用してください。
        ```java
        try {
            Thread.sleep(750);
        } catch (InterruptedException e) {
            // 今は空でOK
        }
        ```

**3. 動作確認**

`main`メソッドを作成し、実装した各メソッドを呼び出して、その動作が正しいことを確認してください。`static`ではないメソッドを呼びだすには、`MethodsPractice`クラスのインスタンスを生成する必要がある点に注意してください。

```java
// mainメソッド内での呼び出し例
MethodsPractice app = new MethodsPractice();
app.printCurrency(1234.567);
int sum = app.add(10, 20);
System.out.println("加算結果: " + sum);
// ... 他のメソッドも同様にテストする
```

### 演習2：テストの点数管理クラス

生徒一人一人のテストの点数を管理し、合計点や平均点などを計算するクラスを設計します。この課題を通じて、データ（フィールド）と振る舞い（メソッド）をまとめたクラスの設計方法を学びます。

**技術的背景：データとロジックの一体化**

従来の手続き型プログラミング（C言語など）では、データ構造（構造体）と、そのデータを操作する関数を別々に定義していました。しかし、この方法では以下の問題が発生します：

- **データの不整合**：どの関数からでもデータを変更できるため、予期しない変更が起こりやすい
- **関連性の不明瞭さ**：どの関数がどのデータに対して使用されるべきかが不明確
- **保守の困難さ**：データ構造を変更すると、関連するすべての関数を修正する必要がある

オブジェクト指向では、データ（生徒の点数）とそれを操作するロジック（合計計算、平均計算など）を1つのクラスにまとめることで、これらの問題を解決します。この設計により、データの整合性が保たれ、関連する処理が明確になり、変更の影響範囲を限定できます。

この演習では、教育現場でよく見られる成績管理という実務的な例を通じて、オブジェクト指向設計の基本を実践的に学習します。

**1. クラスの設計**

以下の2つのクラスを作成します。

-   `StudentScores.java`: 生徒一人の情報を保持するクラス。
-   `ScoresRegistry.java`: `main`メソッドを持ち、プログラム全体を管理するクラス。

**2. `StudentScores`クラスの実装**

**フィールド:**

| フィールド名 | 型 | 説明 |
| :--- | :--- | :--- |
| `id` | `int` | 出席番号 |
| `name` | `String` | 氏名 |
| `scores` | `int[5]` | 5教科の点数配列<br>([0]:国語, [1]:数学, [2]:理科, [3]:社会, [4]:英語) |

**メソッド:**

| メソッド名 | 戻り値 | 引数 | 説明 |
| :--- | :--- | :--- | :--- |
| `(各教科のゲッター)` | `int` | なし | 各教科の点数を返す (`getJapaneseScore`, `getMathematicsScore`など) |
| `(各教科のセッター)` | `void` | `int value` | 各教科の点数を設定する (`setJapaneseScore`, `setMathematicsScore`など) |
| `getTotal` | `int` | なし | 5教科の合計点を計算して返す |
| `getAverage` | `double` | なし | 5教科の平均点を計算して返す |
| `getHighScore` | `int` | なし | 5教科のうち最高得点を返す |
| `getHighScoreSubject` | `String` | なし | 最高得点の科目名を返す（複数ある場合はカンマ区切り。例: "国語,英語"） |
| `getLowScore` | `int` | なし | 5教科のうち最低得点を返す |
| `getLowScoreSubject` | `String` | なし | 最低得点の科目名を返す（複数ある場合はカンマ区切り） |
| `getInfo` | `String` | なし | 生徒の全情報を整形した文字列を返す（例: "出席番号: 1 氏名: 田中太郎 国語:80点 ... 合計:400点 平均:80.0点"） |

**3. `ScoresRegistry`クラスの実装**

-   `main`メソッドを実装します。
-   `Scanner`クラスなどを使って、ユーザーから複数人分の生徒情報（出席番号、氏名、各教科の点数）をコンソールから入力させます。
-   入力された情報を使って`StudentScores`のインスタンスを生成します。
-   すべての入力が終わったら、各生徒の`getInfo`メソッドを呼び出し、全員分の情報を画面に表示します。

### 演習3：車の燃料消費シミュレータ

オブジェクトの状態がメソッド呼び出しによって変化していく様子をシミュレートします。

**技術的背景：オブジェクトの状態管理とシミュレーション**

実世界の多くのシステムは、時間とともに状態が変化します。たとえば：
- **銀行口座**：入出金により残高が変化
- **在庫管理**：商品の入荷・出荷により在庫数が変化
- **ゲームキャラクタ**：行動によりHP、経験値などが変化

このような状態変化を適切にモデル化することは、ソフトウェア開発の重要な要素です。オブジェクト指向プログラミングでは、オブジェクトが内部状態（フィールド）を持ち、メソッドを通じてその状態を変更することで、実世界のシステムを自然に表現できます。

この演習では、車の走行による燃料消費というシンプルだが実用的な例を通じて、以下の概念を学習します：
- **状態の累積**：走行距離と燃料消費の累積管理
- **計算ロジックのカプセル化**：燃費計算をクラス内部に隠蔽
- **イミュータブルな操作**：runメソッドは副作用を持つが、計算結果も返す設計

これらは、より複雑なビジネスロジックやゲーム開発などで必要となる基本的な設計パターンです。

**1. クラスの設計**

-   `Car.java`: 車の状態と振る舞いを定義するクラス。
-   `FuelExpenseCalculator.java`: `main`メソッドを持ち、シミュレーションを実行するクラス。

**2. `Car`クラスの実装**

**フィールド:**

| フィールド名 | 型 | 説明 |
| :--- | :--- | :--- |
| `fuelEfficiency` | `double` | 燃費 (km/L) |
| `totalMileage` | `double` | 累積走行距離 (km) |
| `expendedFuel` | `double` | 累積消費燃料 (L) |

**メソッド:**

| メソッド名 | 戻り値 | 引数 | 説明 |
| :--- | :--- | :--- | :--- |
| `run` | `double` | `double mileage` | 引数で渡された距離を走行し、消費燃料を計算してフィールドを更新する。今回消費した燃料を返す。 |
| `getInfo` | `String` | なし | 現在の累積走行距離と累積消費燃料を整形した文字列を返す。 |

**3. `FuelExpenseCalculator`クラスの実装**

-   `main`メソッドを実装します。
-   最初に車の燃費を設定して`Car`インスタンスを生成します。
-   ユーザーに走行距離を繰り返し入力させます。「end」と入力されたらループを終了します。
-   入力があるたびに`Car`の`run`メソッドを呼び出し、`getInfo`メソッドで現在の状況を表示します。

### 演習のポイント

これらの演習では、以下の重要な概念を実践的に学習できます：

1. **カプセル化**: データ（フィールド）と処理（メソッド）を1つのクラスにまとめる
2. **情報隠蔽**: 必要な情報のみを公開し、内部実装を隠す
3. **オブジェクトの状態管理**: メソッド呼び出しによるオブジェクトの状態変化
4. **クラス間の連携**: 複数のクラスを組み合わせたプログラム設計

各演習の解答例は、`source/chapter03/`ディレクトリに用意されています。まずは自分で実装してみて、その後で解答例と比較することで、より深い理解が得られるでしょう。



