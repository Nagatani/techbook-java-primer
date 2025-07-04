# 第4章 ポリモーフィズムとインターフェイス

## 📋 本章の学習目標

### 前提知識
- **第3章の継承概念の完全な習得**：継承とメソッドオーバーライドの実装経験
- **メソッドオーバーライドの実装経験**：基本的な継承階層の設計経験
- **抽象クラスの設計・実装経験**：階層的なクラス設計の経験

### 到達目標

#### 知識理解目標
- ポリモーフィズムの概念と実現メカニズム
- インターフェイスと抽象クラスの違いと使い分け
- 動的束縛（動的ディスパッチ）のしくみ
- 契約による設計（Design by Contract）の基本概念
- インターフェイス分離の原則

#### 技能習得目標
- インターフェイスの定義と実装
- 複数インターフェイスの実装
- ポリモーフィズムを活用したプログラム設計
- Strategyパターン等の基本デザインパターンの実装
- 型の安全性を保ったポリモーフィックな操作

#### 到達レベルの指標
- [ ] インターフェイスを活用した柔軟な設計が実装できる
- [ ] 実行時に動作が決まるポリモーフィックなプログラムが作成できる
- [ ] 基本的なデザインパターン（Strategy、Observer、Factory）が実装できる
- [ ] 拡張性を考慮したAPIインターフェイスが設計できる

---

## 始めに：柔軟性と拡張性を実現するポリモーフィズム

前章では継承について学習しました。本章では、オブジェクト指向プログラミングの重要な概念である「ポリモーフィズム」と「インターフェイス」について詳しく学習します。

しかし、これらの概念を学ぶ前に、なぜこのようなしくみが生まれ、なぜそれが必要なのかを理解することが重要です。プログラミングの歴史を振り返ると、ソフトウェアはますます複雑化し、規模も拡大の一途をたどっています。1960年代のコンピュータプログラムは数百行程度でしたが、現代のアプリケーションは数百万行に及ぶことも珍しくありません。この規模の拡大とともに、プログラムの構造化や管理手法の重要性が高まり、その解決策として「オブジェクト指向プログラミング」という考え方が誕生しました。

### プログラミングパラダイムの進化

プログラミングの手法は、時代とともに進化してきました。初期のプログラミングは「非構造化プログラミング」と呼ばれ、goto文を多用したスパゲッティコードが問題となっていました。この問題を解決するため、1960年代後半に「構造化プログラミング」が提唱され、順次処理、分岐処理、反復処理という基本的な制御構造でプログラムを構成する手法が確立されました。皆さんが学んできたC言語は、この構造化プログラミングを基本とした「手続き型プログラミング」の代表例です。

手続き型プログラミングでは、プログラムを「データ」と「そのデータを処理する関数」に分けて考えます。この手法は小規模なプログラムには適していましたが、プログラムが大規模化するにつれて、以下のような課題が明らかになりました：

**データと処理の分離による問題**：データ構造と、それを操作する関数が別々に管理されるため、データ構造の変更時に、そのデータを使用するすべての関数を見つけ出して修正する必要があります。これは、プログラムが大きくなるほど困難になります。

**グローバル変数の濫用**：複数の関数で共有されるデータは、グローバル変数として宣言されることが多く、どの関数がいつそのデータを変更するかを追跡することが困難になります。

**コードの再利用性の低さ**：似たような処理を行う関数でも、データ構造が少し異なるだけで、まったく新しい関数を作成する必要があることが多く、コードの重複が発生しやすくなります。

**保守性の悪化**：機能追加や修正時に、変更の影響範囲を正確に把握することが困難で、予期しないバグの原因となりやすい状況が生まれます。

### オブジェクト指向の登場とその背景

これらの課題を解決するため、1970年代に「オブジェクト指向プログラミング（Object-Oriented Programming、OOP）」という新しいプログラミングパラダイムが提唱されました。オブジェクト指向の根本的な考え方は、現実世界の「もの（オブジェクト）」をソフトウェアの世界で直接的にモデリングしようというものです。

現実世界を見回すと、すべてのものは「状態（属性）」と「振る舞い（動作）」を持っています。たとえば、自動車という「オブジェクト」は、色、速度、燃料残量といった「状態」と、加速する、ブレーキをかける、方向転換するといった「振る舞い」を持っています。オブジェクト指向プログラミングでは、この現実世界の構造をそのままプログラムに反映させることを目指します。

この手法の最大の利点は、**現実世界とプログラムの構造が対応している**ことです。顧客管理システムを作る場合、現実世界の「顧客」という概念を、プログラム内でも「顧客オブジェクト」として直接表現できます。顧客は名前、住所、電話番号といった情報（状態）を持ち、注文をする、支払いをするといった行動（振る舞い）を取ります。これをそのままプログラムの構造として表現できるため、設計者、プログラマ、そして時には利用者までもが、システムの構造を直感的に理解できるのです。

### オブジェクト指向の三大原則とその意義

オブジェクト指向プログラミングは、3つの基本原則にもとづいて構築されています：

**カプセル化（Encapsulation）**：関連するデータと、そのデータを操作する機能を1つの単位（オブジェクト）にまとめ、外部からの不適切なアクセスを防ぐしくみです。これにより、オブジェクト内部の実装詳細を隠蔽し、外部からは必要最小限のインターフェイスのみを公開できます。

**継承（Inheritance）**：既存のオブジェクトの特性を受け継いで、新しいオブジェクトを作成するしくみです。共通の特性を持つオブジェクト群の階層構造を表現でき、コードの再利用性を大幅に向上させます。

**ポリモーフィズム（Polymorphism）**：同じインターフェイスで異なる動作を実現するしくみです。これにより、オブジェクトの種類に応じて適切な処理を自動的に選択でき、プログラムの柔軟性と拡張性が向上します。

これらの原則により、オブジェクト指向プログラミングは手続き型プログラミングの課題を根本的に解決します。関連するデータと処理がカプセル化されることで保守性が向上し、継承によってコードの再利用性が高まり、ポリモーフィズムによってプログラムの柔軟性が確保されます。

### 現代ソフトウェア開発におけるオブジェクト指向の重要性

今日、オブジェクト指向プログラミングは、ソフトウェア開発の標準的な手法となっています。その理由は、現代のソフトウェアが抱える課題に対する効果的な解決策を提供するからです：

**複雑性の管理**：現代のアプリケーションは、Web、データベース、ユーザーインターフェイス、セキュリティなど、多様な技術領域を統合する必要があります。オブジェクト指向により、これらの複雑な要素を適切に分割し、管理可能な単位に分解できます。

**チーム開発の効率化**：大規模なソフトウェア開発は、多くの開発者によるチーム作業です。オブジェクト指向により、システムをクラス単位で分割できるため、開発者間での作業分担や並行開発が容易になります。

**変更への対応**：ビジネス要件は常に変化し、ソフトウェアもそれに対応する必要があります。適切に設計されたオブジェクト指向システムでは、変更の影響を局所化でき、システム全体への影響を最小限に抑えながら機能追加や修正を行えます。

**再利用可能なコンポーネントの構築**：オブジェクト指向により作成されたクラスは、ほかのプロジェクトでも再利用できる場合が多く、開発効率の向上とコスト削減に貢献します。

本章では、これらのオブジェクト指向の基本概念を、Javaというプログラミング言語を通じて実践的に学習していきます。単にクラスの書き方を覚えるのではなく、なぜそのようなしくみが必要なのか、どのような利点があるのかを理解しながら進めることで、より深い理解を得ることができるでしょう。

## 4.1 手続き型からオブジェクト指向へ

皆さんが学んできたC言語は、主に**手続き型プログラミング**という考え方にもとづいています。これは、プログラムを「手順（関数）」の集まりとして捉え、データを関数に渡して処理する手法です。

一方、Javaは**オブジェクト指向プログラミング (OOP)** を中心とした言語です。OOPはプログラムを、**オブジェクト**の集まりとしてとらえます。それぞれのオブジェクトは、各々の**データ（状態）**と、そのデータを操作する**手続き（振る舞い・機能）**をまとめて扱います。

### なぜオブジェクト指向なのか

* **現実世界のモデリング**:「顧客」「商品」「注文」のように、現実世界の概念をそのままプログラムの構成要素（オブジェクト）として表現しやすいため、設計が直感的になります。
* **再利用性の向上**: 一度作成したクラス（オブジェクトの設計図）は、使い回すことができます。たとえば、「商品」クラスを作れば、在庫管理システムでも、販売システムなどのほかのシステムでも利用できます。
* **保守性の向上**: データとそれに関連する操作がクラス内にまとまっている（これを**カプセル化**と言います）ため、変更の影響範囲を特定しやすく、修正や機能追加が容易になります。バグが特定のクラス内に限定されやすくなります。
* **分業のしやすさ**: 大規模なシステム開発において、クラス単位で開発を分担しやすくなり、適切なカプセル化により、効率の良い開発を進められます。

C言語の手続き型でもたいていのアプリケーションは作成できますが、複雑で大規模なシステムを構築する場合において、オブジェクト指向のアプローチが有効になる場面が多くあります。

## 4.2 クラスとオブジェクト - お店の商品を例に

オブジェクト指向の基本的な構成要素が「クラス」と「オブジェクト」です。お店の商品管理を例に考えてみましょう。

### C言語の構造体とJavaのクラス

C言語では、商品のデータを構造体で定義し、そのデータを操作する関数を別途用意していました。

```c
#include <stdio.h>
#include <string.h>

// C言語: データと操作は別々
struct Product {
    char name[50];
    int price;
    int stock;
};

// 在庫があるか確認する関数
int C_isInStock(struct Product *p) {
    return p->stock > 0;
}

int main() {
    struct Product apple;
    strcpy(apple.name, "Apple");
    apple.price = 150;
    apple.stock = 50;

    if (C_isInStock(&apple)) { // 関数にデータを渡してチェック
        printf("%s is in stock.\n", apple.name);
    }
    return 0;
}
```

Javaでは、これらをクラスとして一体化します。

#### Javaによるオブジェクト指向実装の実践例

以下のプログラムは、手続き型プログラミングからオブジェクト指向プログラミングへの移行を理解するための重要な学習材料です。データと操作が一体化したクラス設計の利点を実践的に学ぶことができます：

ファイル名： Product.java、Shop.java

```java
// Java: データ(フィールド)と操作(メソッド)がクラス内にまとまる
public class Product {
    String name;
    int price;
    int stock;

    // 在庫があるか確認するメソッド
    boolean isInStock() {
        // this.stock は、このメソッドが呼び出されたオブジェクト自身の stock フィールドを指す
        return this.stock > 0;
    }

    // 現在の状態を表示するメソッド
    void displayInfo() {
        System.out.println("商品名: " + this.name + ", 価格: " + this.price + "円, 在庫: " + this.stock + "個");
    }

    // 商品を購入する（在庫を減らす）メソッド
    boolean purchase(int quantity) {
        if (quantity <= 0) {
            System.out.println("購入数量が無効です。");
            return false;
        }
        if (this.stock >= quantity) {
            this.stock -= quantity;
            System.out.println(this.name + "を" + quantity + "個購入しました。");
            return true;
        } else {
            System.out.println("在庫不足です。現在の在庫: " + this.stock + "個");
            return false;
        }
    }

    // 商品を入荷する（在庫を増やす）メソッド
    void restock(int quantity) {
        if (quantity > 0) {
            this.stock += quantity;
            System.out.println(this.name + "を" + quantity + "個入荷しました。");
        }
    }
}

public class Shop {
    public static void main(String[] args) {
        // Product クラスから apple オブジェクトを生成
        Product apple = new Product();
        apple.name = "りんご";
        apple.price = 150;
        apple.stock = 50;

        // 別の商品オブジェクトを生成
        Product orange = new Product();
        orange.name = "オレンジ";
        orange.price = 200;
        orange.stock = 30;

        System.out.println("=== 商品管理システム ===");
        
        // 各オブジェクトの初期状態を表示
        apple.displayInfo();
        orange.displayInfo();
        System.out.println();

        // オブジェクト自身が持つメソッドを呼び出す
        System.out.println("=== 在庫確認 ===");
        if (apple.isInStock()) {
            System.out.println(apple.name + " は在庫があります。");
        }
        if (orange.isInStock()) {
            System.out.println(orange.name + " は在庫があります。");
        }
        System.out.println();

        // 購入処理の実行
        System.out.println("=== 購入処理 ===");
        apple.purchase(10);  // りんごを10個購入
        orange.purchase(35); // オレンジを35個購入（在庫不足になる）
        System.out.println();

        // 入荷処理の実行
        System.out.println("=== 入荷処理 ===");
        orange.restock(20);  // オレンジを20個入荷
        System.out.println();

        // 最終的な状態を表示
        System.out.println("=== 最終在庫状況 ===");
        apple.displayInfo();
        orange.displayInfo();
    }
}
```

**このプログラムから学ぶ重要なオブジェクト指向の概念：**

1. **データと操作の一体化（カプセル化）**：商品の情報（name, price, stock）と、それらを操作するメソッド（isInStock, purchase, restock）が1つのクラス内にまとめられています。これにより、データの整合性を保ちながら操作が可能になります。

2. **thisキーワードの重要性**：`this.stock`は「このオブジェクト自身のstockフィールド」を指します。メソッド内でオブジェクト自身のフィールドにアクセスする際の明示的な方法です。

3. **オブジェクトの独立性**：Appleオブジェクトとorangeオブジェクトは、それぞれ独立した状態を持ち、一方への操作が他方に影響することはありません。これがオブジェクト指向の重要な特徴です。

4. **責任の明確化**：各商品オブジェクトが自分自身の在庫管理に責任を持ちます。外部から直接フィールドを変更するのではなく、適切なメソッドを通じて操作することで、ビジネスルール（例：負の在庫は許可しない）を保証できます。

5. **再利用性の向上**：同じProductクラスから何個でもオブジェクトを生成でき、それぞれが独立して機能します。新しい商品を追加する際も、既存のコードを変更する必要がありません。

**手続き型との比較から見える利点：**

- **データの安全性**：フィールドへの直接アクセスを制限し、メソッドを通じた制御された操作が可能
- **保守性の向上**：商品に関する処理がProductクラス内に集約されているため、修正時の影響範囲が明確
- **拡張性**：新しい商品タイプや機能を追加する際も、既存のコードへの影響を最小限に抑制可能
- **理解しやすさ**：現実世界の「商品」という概念がそのままコードの構造に反映されている

Javaのアプローチでは、`apple`というオブジェクト自身が`isInStock()`という「在庫確認能力」を持っている、というより管理しやすい形でプログラムを表現できます。

商品自体に在庫管理能力を持たせるべきか、商品を管理する別のクラスを作成してそちらで在庫管理を行わせるか、こういった組み合わせを考える作業を**設計**と呼びます。

設計は正解がなく、状況に応じて最適な選択を行う必要があります。オブジェクト指向でプログラミングを行う場合、最も難しいと思われる部分です。すぐに良い設計を思い浮かぶことはないと考えて、より良い設計はないかを意識しましょう。

### クラス (Class): 商品情報の「テンプレート」

クラスは、同じ種類のモノが共通して持つべき情報（状態や属性）と、できること（機能）を定義した「設計図」や「テンプレート」をイメージしてください。

例：お店に挟まざまな商品がありますが、「商品」というカテゴリで共通して「商品名」「価格」「在庫数」といった情報を持っています。また、「価格を表示する」「在庫があるか確認する」といった共通の操作が考えられます。この共通の定義をまとめたものが`Product`クラスです。

C言語との比較：C言語の構造体 (`struct`) は、関連するデータをまとめるものでした。Javaのクラスは、これらのデータ（**フィールド**）に加えて、そのデータを扱う**手続き（メソッド）**、たとえば `displayPrice()` や `isInStock()` といった関数に相当するものも一緒にクラス内で定義します。これが大きな違いです。

### オブジェクト (Object): 具体的な「商品」そのもの

オブジェクトは、クラスという設計図にもとづいて、メモリ上に実際に作成された「実体」です。インスタンス (Instance) とも呼ばれます。

例：`Product`クラス（設計図）から、「りんご（商品名： "りんご", 価格： 150, 在庫数： 50）」という具体的な商品オブジェクトや、「みかん（商品名： "みかん", 価格： 100, 在庫数： 80）」という別の商品オブジェクトを作成します。

オブジェクトの生成（インスタンス化）は`new`というキーワードを使って、クラスからオブジェクトを生成します。`Product apple = new Product();`のように書きます。

### カプセル化：データと操作をひとまとめにする利点

クラスによって、関連性の高いデータ（フィールド）とそれを操作する手続き（メソッド）が1つの単位にまとめられます。これを**カプセル化**と呼びます。

* **安全性**: 外部から直接フィールドを不まさに変更されることを防ぎ、メソッドを通じてのみ操作させるように制限できます（アクセス修飾子`private`などを使いますが、ここでは概念だけ説明します）。これにより、データの整合性を保ちやすくなります。
* **独立性**: クラス内部のしくみを変更しても、外部への影響を最小限に抑えられます。たとえば、価格の計算方法が変わっても、`displayPrice()`メソッドの呼び出し方は変わらないように作れます。

## 4.3 フィールドとメソッド - 商品の情報とできること

クラスの具体的な中身（メンバー）である「フィールド」と「メソッド」について、もう少し詳しく見ていきましょう。

### フィールド (Field): 商品の「状態」や「属性」

オブジェクトが持つべき具体的な**データ**を保持します。管理対象となるオブジェクトの「状態」や「属性」を表します。

フィールドは、クラス内で変数を宣言することで定義します。

* 例（`Product`クラスの場合）
    * `String name;` // 商品名（文字列型）
    * `int price;` // 価格（整数型）
    * `int stock;` // 在庫数（整数型）

クラスを元に生成されたオブジェクト（インスタンス）は、それぞれ独自のフィールドに各々の値を持ちます。

### メソッド (Method): 商品の「機能」や「振る舞い」

オブジェクトが行える**操作**や**処理**を定義し、オブジェクトの「機能」や「振る舞い」を表します。

クラス内に関数を定義するような形で記述します。

* 例
    * フィールドの値を参照する（例： `getPrice()`, `getName()`）。
    * フィールドの値を変更する（例： `reduceStock(int amount)`, `setPrice(int newPrice)`）。
    * 特定の計算や処理を行う（例： `calculateTaxIncludedPrice()` 税込み価格を計算する）。
    * 情報を表示する（例： `displayInfo()`）。

#### `this`キーワード

メソッド内では、`this`というキーワードを使うことで、そのメソッドを呼び出している**オブジェクト自身**を参照できます。

`this.name`は「このオブジェクトの`name`フィールド」、`this.displayInfo()`は「このオブジェクトの`displayInfo`メソッド」を意味します。

多くの場合、フィールド名がメソッド内のローカル変数と衝突しなければ`this`は省略可能ですが、明示的につけることでコードが読みやすくなることもあります（特にコンストラクタや値をフィールドに設定する目的のセッタメソッドでよく使われます）。

### `Product`クラスの例

```java
public class Product {
    String name;
    int price;
    int stock;

    // 在庫があるか確認するメソッド
    boolean isInStock() {
        return this.stock > 0;
    }

    // 現在の状態を表示するメソッド
    void displayInfo() {
        System.out.println("商品名: " + this.name + ", 価格: " + this.price + "円, 在庫: " + this.stock + "個");
    }

    // 価格をメッセージ形式で表示するメソッド
    void displayPrice() {
        System.out.println(this.name + " の価格は " + this.price + "円です。");
    }

    // 商品を売るメソッド (在庫を減らす)
    // amount: 売る数量
    void sell(int amount) {
        if (this.stock >= amount) {
            this.stock -= amount; // this.stock = this.stock - amount; と同じ
            System.out.println(this.name + " を " + amount + "個販売しました。残り在庫: " + this.stock);
        } else {
            System.out.println("エラー: " + this.name + " の在庫が足りません。(在庫: " + this.stock + ", 要求: " + amount + ")");
        }
    }

    // 在庫を補充するメソッド
    void restock(int amount) {
        this.stock += amount;
        System.out.println(this.name + " を " + amount + "個入荷しました。現在の在庫: " + this.stock);
    }
}

public class ShopInventory {
    public static void main(String[] args) {
        Product pencil = new Product();
        pencil.name = "鉛筆";
        pencil.price = 80;
        pencil.stock = 100;

        pencil.displayPrice(); // 鉛筆 の価格は 80円です。

        if (pencil.isInStock()) {
            System.out.println(pencil.name + " は在庫があります。");
        }

        pencil.sell(30); // 鉛筆 を 30個販売しました。残り在庫: 70
        pencil.sell(80); // エラー: 鉛筆 の在庫が足りません。(在庫: 70, 要求: 80)
        pencil.restock(50); // 鉛筆 を 50個入荷しました。現在の在庫: 120
        pencil.sell(80); // 鉛筆 を 80個販売しました。残り在庫: 40
    }
}
```

このように、フィールド（データ）とメソッド（操作）がいったいとなることで、「商品」オブジェクトが自身の情報にもとづいて振る舞う、より現実に近いモデルをプログラムで表現できます。

## 4.4 コンストラクタ - オブジェクトの初期化

クラスから`new`を使ってオブジェクトを生成するとき、そのオブジェクトのフィールド（たとえば、商品の名前や価格）に初期値を設定したい場合がほとんどです。

毎回`apple.name = "りんご"; apple.price = 150;`のように手動で設定するのは手間ですし、設定し忘れる可能性もあります。

そこで登場するのが**コンストラクタ (Constructor)** です。

コンストラクタは、`new`でオブジェクトが生成される**直後に自動的に呼び出される**特別なメソッドです。主な目的は、オブジェクトの**フィールドを初期化**することで、オブジェクトが利用可能な状態（適切な初期状態）になることを保証することです。

### なぜ必要か？

* **初期値の保証**: オブジェクト生成時に、フィールドが必ず意味のある値で初期化されるように強制できます（例： 価格が未設定のまま商品が作られるのを防ぐ）。
* **必須情報の指定**: オブジェクトを作るために最低限必要な情報（例： 商品名と価格）を、生成時に引数として渡すように要求できます。
* **定型的な初期化処理**: オブジェクト生成時に毎回行う必要のあるセットアップ処理（例： IDの自動採番、関連オブジェクトの生成など）を記述できます。

### 書き方のルール

1. メソッド名は、**クラス名と完全に同じ**にする。
2. **戻り値の型（`void`など）を記述しない**（戻り値を返せない）

### 特殊な**デフォルトコンストラクタ**

もしクラス内にコンストラクタを1つも定義しない場合、Javaコンパイラが「引数なし、中身が空のコンストラクタ」（デフォルトコンストラクタ）を自動的に生成します。

しかし、自分で1つでもコンストラクタを定義すると、このデフォルトコンストラクタは自動生成されなくなります。

### コンストラクタの実践例：堅牢なオブジェクト初期化

コンストラクタは、オブジェクトの信頼性を保証するための重要なしくみです。以下のプログラムは、適切なコンストラクタ設計により、データの整合性とビジネスルールの保証を実現する方法を学習するための重要な材料です：

ファイル名： Product.java、ShopSetup.java

```java
public class Product {
    String name;
    int price;
    int stock;
    String category;
    boolean isActive;

    // コンストラクタ1: 基本的な商品情報のみで初期化
    Product(String productName, int productPrice) {
        System.out.println("Productコンストラクタ(String, int) 呼び出し中...");
        
        // 入力値の妥当性チェック
        if (productName == null || productName.trim().isEmpty()) {
            System.out.println("警告: 商品名が無効です。デフォルト名を設定します。");
            this.name = "未設定商品";
        } else {
            this.name = productName.trim();
        }
        
        if (productPrice < 0) {
            System.out.println("警告: 価格が負の値です。0円に設定します。");
            this.price = 0;
        } else {
            this.price = productPrice;
        }
        
        this.stock = 0; // 在庫は初期状態で0
        this.category = "一般商品"; // デフォルトカテゴリ
        this.isActive = true; // 初期状態では販売可能
        
        System.out.println("商品「" + this.name + "」を価格" + this.price + "円で登録しました。");
    }

    // コンストラクタ2: 初期在庫数も指定
    Product(String productName, int productPrice, int initialStock) {
        System.out.println("Productコンストラクタ(String, int, int) 呼び出し中...");
        
        // 基本情報の設定（上記と同様の妥当性チェック）
        if (productName == null || productName.trim().isEmpty()) {
            this.name = "未設定商品";
        } else {
            this.name = productName.trim();
        }
        
        this.price = (productPrice < 0) ? 0 : productPrice;
        
        // 在庫数の妥当性チェック
        if (initialStock < 0) {
            System.out.println("警告: 初期在庫が負の値です。0個に設定します。");
            this.stock = 0;
        } else {
            this.stock = initialStock;
        }
        
        this.category = "一般商品";
        this.isActive = true;
        
        System.out.println("商品「" + this.name + "」を価格" + this.price + "円、初期在庫" + this.stock + "個で登録しました。");
    }

    // コンストラクタ3: 完全な商品情報を指定
    Product(String productName, int productPrice, int initialStock, String productCategory) {
        System.out.println("Productコンストラクタ(String, int, int, String) 呼び出し中...");
        
        // 基本情報の設定
        this.name = (productName == null || productName.trim().isEmpty()) ? "未設定商品" : productName.trim();
        this.price = (productPrice < 0) ? 0 : productPrice;
        this.stock = (initialStock < 0) ? 0 : initialStock;
        
        // カテゴリの設定
        if (productCategory == null || productCategory.trim().isEmpty()) {
            this.category = "一般商品";
        } else {
            this.category = productCategory.trim();
        }
        
        this.isActive = true;
        
        System.out.println("商品「" + this.name + "」(" + this.category + ")を価格" + this.price + "円、初期在庫" + this.stock + "個で登録しました。");
    }

    // デフォルトコンストラクタ（カスタムコンストラクタ定義後も利用可能にする）
    Product() {
        System.out.println("Productデフォルトコンストラクタ呼び出し中...");
        this.name = "サンプル商品";
        this.price = 100;
        this.stock = 0;
        this.category = "一般商品";
        this.isActive = true;
        System.out.println("デフォルト商品を作成しました。");
    }

    // 商品情報を表示するメソッド
    void displayInfo() {
        String status = this.isActive ? "販売中" : "販売停止";
        System.out.println("[" + this.category + "] " + this.name + " - 価格: " + this.price + "円, 在庫: " + this.stock + "個 (" + status + ")");
    }

    // 商品を非アクティブ化するメソッド
    void discontinue() {
        this.isActive = false;
        System.out.println("商品「" + this.name + "」を販売停止にしました。");
    }
}

public class ShopSetup {
    public static void main(String[] args) {
        System.out.println("=== 商品管理システム - コンストラクタ利用例 ===");
        System.out.println();
        
        // 様々なコンストラクタを使用してオブジェクトを生成
        System.out.println("1. 基本情報のみでの商品作成:");
        Product orange = new Product("オレンジ", 120);
        System.out.println();
        
        System.out.println("2. 初期在庫ありでの商品作成:");
        Product banana = new Product("バナナ", 100, 50);
        System.out.println();
        
        System.out.println("3. 完全情報での商品作成:");
        Product apple = new Product("りんご", 150, 30, "果物");
        System.out.println();
        
        System.out.println("4. デフォルトコンストラクタでの商品作成:");
        Product sample = new Product();
        System.out.println();
        
        System.out.println("5. 不正な値での商品作成テスト:");
        Product invalid = new Product("", -50, -10);
        System.out.println();
        
        // 作成された商品の情報を表示
        System.out.println("=== 登録された商品一覧 ===");
        orange.displayInfo();
        banana.displayInfo();
        apple.displayInfo();
        sample.displayInfo();
        invalid.displayInfo();
        System.out.println();
        
        // 商品の状態変更
        System.out.println("=== 商品状態の変更 ===");
        banana.discontinue();
        banana.displayInfo();
    }
}
```

**このプログラムから学ぶ重要なコンストラクタ設計原則：**

1. **入力値の妥当性検証**：コンストラクタ内で引数の妥当性をチェックし、不正な値が設定されることを防ぎます。これにより、オブジェクトの整合性を保証できます。

2. **オーバーロードによる利便性**：異なる初期化パターンに対応するため、複数のコンストラクタを提供します。利用者は必要な情報量に応じて適切なコンストラクタを選択できます。

3. **デフォルト値の適切な設定**：引数が不正な場合や、指定されなかった場合の適切なデフォルト値を設定します。システムの堅牢性を向上させます。

4. **デフォルトコンストラクタの明示的定義**：カスタムコンストラクタを定義した場合でも、デフォルトコンストラクタが必要な場合は明示的に定義します。

5. **初期化の完全性**：コンストラクタ実行後、オブジェクトが即座に使用可能な状態になることを保証します。部分的に初期化されたオブジェクトが存在することを防ぎます。

**実用的な応用場面：**

- **データベースエンティティ**：DBから取得したデータによるオブジェクト初期化
- **設定オブジェクト**：アプリケーション設定の多様な初期化パターン
- **ファクトリパターン**：異なる作成方法に対応するオブジェクト生成
- **バリデーション**：ビジネスルールにもとづいた入力値検証

**コンストラクタ設計のベストプラクティス：**

1. **引数の検証を必ず行う**：null値や範囲外の値をチェック
2. **例外的な値への適切な対応**：警告の出力やデフォルト値の設定
3. **コンストラクタチェーニング**：`this()`を使用したほかのコンストラクタの呼び出し
4. **不変オブジェクトの作成**：必要に応じて、作成後に変更できないオブジェクトの設計

### 実行結果

```
Productコンストラクタ(String, int) 呼び出し中...
商品「オレンジ」を価格120円で登録しました。
Productコンストラクタ(String, int, int) 呼び出し中...
商品「バナナ」を価格100円、初期在庫50個で登録しました。
--- 商品情報 ---
商品名: オレンジ, 価格: 120円, 在庫: 0個
商品名: バナナ, 価格: 100円, 在庫: 50個
```

### コンストラクタのオーバーロード

上記の例のように、同じクラス内に**引数の型、数、または並び順が異なる**複数のコンストラクタを定義できます。

これを**オーバーロード (Overload)** と呼びます。これにより、オブジェクトの生成方法にバリエーションを持たせることができます（例： 初期在庫を指定する場合としない場合など、どちらにも対応可能）。

オーバーロードはカプセル化の際に重要な役割を持ちます。次回以降の解説になりますが、コンストラクタのほかに、通常のメソッドもオーバーロードを実現できます。

## 4.5 mainメソッドとは

C言語の`main`関数と同様に、Javaでは「mainメソッド」がエントリポイント（クラスを実行する際の入り口）となります。

### `main`メソッドの「おまじない」を解き明らかす

```java
public static void main(String[] args) {
    // ここからプログラムが動く
}
```

この一行に含まれる各キーワードには、Javaプログラムを実行する上で重要な意味があります。

* **`public`**: アクセス修飾子で「公開されている」という意味
    - Javaプログラムを実行するJVM（Java Virtual Machine）は、OSなどプログラムの外部からこの`main`メソッドを探して呼びだす必要があります。そのため、どこからでもアクセスできるよう`public`になっている必要があります。もし`private`などほかのアクセス修飾子だと、JVMが見つけられずプログラムを実行できません。
* **`static`**: 静的で「クラスに属する」という意味
    - JVMがプログラムの実行を開始するとき、最初に`main`メソッドを呼び出します。この時点では、まだそのクラスのオブジェクトが1つも生成されていないのが普通です。オブジェクトが存在しなくても呼び出せるように、`main`メソッドは`static`である必要があります。もし`static`でないと、JVMはどのオブジェクトの`main`メソッドを呼べばよいか分からなくなってしまいます。
* **`void`**: 戻り値の型で「何も返さない」という意味
    - `main`メソッドはプログラムの起点であり、一連の処理を実行した後、通常はプログラムを終了します。呼び出し元であるJVMに対して何か特別な値を返す必要がないため、戻り値の型は`void`となります（C言語では`int main()`でOSに終了コードを返すのが一般的でしたが、Javaでは通常`System.exit(int status)`を使って明示的に終了コードを指定します）。
* **`main`**: メソッド名
    - JVMがプログラムの開始点として探すメソッドの名前が`main`である、というJavaの規約です。この名前でなければ、JVMは開始点を見つけられません。
* **`(String[] args)`**: 引数
    - プログラム実行時に外部から渡される情報（**コマンドライン引数**）を受け取るためのパラメータです。`String[]`は「`String`（文字列）の配列」であることを意味し、`args`はその配列の変数名です（慣習的に`args`が使われますが、ほかの名前でもかまいません）。たとえば、`java MyProgram arg1 arg2`のように実行した場合、`args`配列には`{"arg1", "arg2"}`という内容が格納されます。

### mainメソッドのルール

Javaのmainメソッドは、以下の条件をすべて満たしているメソッドです。

1. アクセス修飾子はpublic
2. staticメソッド
3. メソッドの戻り値はvoid
4. メソッド名は"main"である（すべて小文字）
5. メソッドの引数の型はStringの配列`String[]`またはStringの可変長引数`String...`のみ（引数名はargsでなくても良い）

```java
class Main {
    public static void main(String[] args) {
        // 何かの処理
    }
}
```

```java
class Main {
    // これでもOK
    public static void main(String... arguments) {
        // 何かの処理
    }
}
```

### mainメソッドの条件はしっかりとチェックされる

mainメソッドの条件は、Javaの実行時にきっちりチェックされます。たとえば、以下のようなものはJavaのメソッドとしては文法どおりなのでコンパイルはできますが、mainメソッドとしては認識されません。

```java
// アクセス修飾子がpublicではない
static void main(String[] args) { }
// メソッドがstaticではない
public void main(String[] args) { }
// メソッドの戻り値がvoidではない
public static int main(String[] args) { }
// メソッド名が"main"ではない
public static void Main(String[] args) { }
// 引数がStringの配列または可変長引数ではない
public static void main(String args) { }
// 引数がStringの配列だけではない(intが邪魔)
public static void main(String[] args, int intValue) { }
```

### mainメソッドを終わらせ方

mainメソッドはプログラム中で一番初めに動くものだとはいえ、普通のメソッドです。

mainメソッドの最後まで処理が進めば終わりますし、途中で`return;`すれば、そのタイミングで終わらせられます。

C言語のプログラムと違い、単純にreturnした場合は、プログラムからの戻り値を指定できません。returnで終了させてしまった場合のプログラムからの戻り値が設定される変数"ERRORLEVEL"の内容は0ですが、これはJavaの中で戻り値を指定せず、プログラム実行中にエラーが起きなかった場合に標準的な動作です。

プログラム中でエラーが起きた場合などは、戻り値をエラーの種類を表す何かの数字にしたい場合、`System.exit()`や`Runtime.exit()`を使い、引数に渡す数字でエラーを指定します。

## 4.6 nullとクラス型の変数

クラス型の変数の取り扱いについてと、nullについて理解することは、Javaでのプログラミングでは重要です。

### nullとは

**何もない**が**ある**状態を言います。

`null`は、「メモリ空間にそのオブジェクトの実体を指し示すアドレスがない状態」を指します。

Javaだけでなく、ほかの言語でもですが、バグの発生要因ダントツナンバーワンの憎めないやつです。

### クラス型のオブジェクト

- Javaの基本的な型であるプリミティブ型の変数は、初期化をしないとコンパイルエラー、もしくはクラスのフィールドの場合は勝手に初期値が代入されます。しかしクラス型のオブジェクトは、変数を宣言した状態では（つまりインスタンス化しない場合は）、初期値がnullとなります。
- クラス型の変数どうしでコピーをすれば同じものとして扱われます。
- 変数が指し示すものは、**オブジェクトが格納されているメモリアドレス**となります。

### 変数の初期値について

クラスのフィールド、staticなフィールド（クラス変数）または配列の1つ一つの要素は生成されるときに初期値で初期化されます。

初期値は変数の型ごとに異なり、標準仕様では以下のようになっています。

| 型 | 初期値 |
|---|----------|
| byte | `0` |
| short | `0` |
| int | `0` |
| long | `0L` |
| float | `0.0f` |
| double | `0.0d` |
| char | `'\u0000'` |
| boolean | `false` |
| クラス型(String型も含む) | `null` |

### nullの発生

繰り返しますが、`null`はクラス型変数で使われる特別な値です。これは、**「その変数がどのオブジェクトも参照していない（指し示していない）状態」**を表します。

* 基本データ型（`int`など）の変数には`null`を代入することはできません。
* クラス型の変数は、初期状態や、意図的にオブジェクトとの関連を切った場合`null`になることがあります。
* クラス型の配列（`String[]`など）は、要素すべてが`null`として初期化されています。

例：

```java
String message; // この時点では、message は何も参照していない（未初期化）
message = null; // message に null を代入（どのオブジェクトも参照しない状態を明示）
String greeting = "こんにちは"; // greeting は "こんにちは" という文字列オブジェクトを参照

int number = 0; // 基本データ型は null にできない
// int count = null; // これはコンパイルエラーになる

String[] strArray = new String[5]; // strArray[0]〜strArray[4]まですべてnull
```

### `NullPointerException`とそれを防ぐ

`null`の状態の変数に対して、そのオブジェクトが持つはずのメソッド（機能）を呼び出そうとしたり、フィールド（データ）にアクセスしようとすると、プログラムは「参照先がないのに操作しようとしている！」とエラーを起こして停止してしまいます。このエラーが`NullPointerException`です。

`NullPointerException`はJavaプログラミングで非常によく遭遇するエラーの1つです。これを未然に防ぐため、クラス型変数を使用する前に、その変数が`null`でないかを確認（比較）することが非常に重要になる場合が想定されます。

```java
String text = null;

// textがnullなのに、length()メソッドを呼び出そうとすると…
// System.out.println(text.length()); // ここで NullPointerException が発生！

// ↓のように、事前にnullかどうかをチェックする
if (text != null) {
    // null でない場合のみ、メソッドを呼び出す
    System.out.println(text.length());
} else {
    System.out.println("text は null です。");
}
```

### `null`の比較方法

変数が`null`かどうかを比較するには、比較演算子`==`（等しい）または`!=`（等しくない）を使います。

* `変数 == null`: 変数が`null`である場合`true`になります。
* `変数 != null`: 変数が`null`でない（何らかのオブジェクトを参照している）場合`true`になります。

```java
String name = null;

if (name == null) {
    System.out.println("変数 name は null です。");
}

name = "山田太郎";

if (name != null) {
    System.out.println("変数 name は null ではありません。値: " + name);
}
```

### 配列と`null`

クラス型の配列を作成した場合、その配列の**各要素の初期値は自動的に`null`になります**。

これは、配列が確保された段階では、各要素がまだどのオブジェクトも参照していないからです。

```java
// String型の配列で見てみましょう
String[] words = new String[3]; // 要素数が3のString配列を生成

// 配列生成直後の各要素は null になっている
System.out.println("配列生成直後:");
for (int i = 0; i < words.length; i++) {
    // 要素が null かどうかチェック
    if (words[i] == null) {
        System.out.println("words[" + i + "] は null です。");
    } else {
        // この時点ではここは実行されない
        System.out.println("words[" + i + "] は " + words[i] + " です。");
    }
}

// 配列の要素にオブジェクト（文字列）を代入
words[0] = "Java";
words[2] = "Programming"; // words[1] は null のまま

System.out.println("\n一部の要素に代入後:");
for (int i = 0; i < words.length; i++) {
    // 配列要素を使用する前に null チェック！
    if (words[i] != null) {
        // null でない要素に対してのみ操作を行う
        System.out.println("words[" + i + "] の長さ: " + words[i].length());
    } else {
        System.out.println("words[" + i + "] は null なので処理をスキップします。");
    }
}
```

この例のように、クラス型の配列を扱う際は、**各要素へアクセスする前に`null`チェックを行う**ことが`NullPointerException`を避けるための定石です。特に、ループ処理で配列の全要素を順に扱う場合には注意が必要です。

## まとめ

Javaのクラスは、「C言語の構造体に関数を内包できるようにしたもの」と覚えておくとよいでしょう。

クラス設計のポイントは、どのデータ（状態）とそれに対するメソッド（機能）をどうやってまとめて管理するか、をしっかりと考えることです。

設計から学ぶことはそれなりに難易度が高いので、プログラムの書き方をきちんと覚えつつ、どのように管理するかを継続的に考えましょう。

* **クラス型変数はオブジェクトの「参照」を保持します。**
* **`null`は、変数がどのオブジェクトも参照していない状態を表します。**
* **`null`の変数に対してメソッド呼び出しなどを行うと`NullPointerException`が発生します。**
* **`NullPointerException`を防ぐために、`== null`や`!= null`を使って、変数を使用する前に`null`チェックを行いましょう。**
* **クラス型の配列を作成すると、各要素の初期値は`null`になります。配列要素にアクセスする際も`null`チェックを忘れないようにしましょう。**

`null`チェックは、安全で堅牢なJavaプログラムを作成するための基本的ながら非常に重要なテクニックです。

## 4.7 カプセル化とアクセス制限

オブジェクト指向プログラミングの重要な原則の1つである**カプセル化 (Encapsulation)** について詳しく学習しましょう。カプセル化とは、オブジェクトのデータ（フィールド）とそのデータを操作するメソッドを1つにまとめ、オブジェクトの内部構造を外部から隠蔽することです。

### カプセル化の目的

カプセル化により以下の利点が得られます：

* **データの保護**: 外部から直接フィールドにアクセスできなくすることで、意図しない値の書き換えや、不正な状態になることを防ぎます
* **保守性の向上**: クラスの内部実装を変更しても、外部への影響を最小限に抑えることができます。公開しているメソッドの仕様が変わらなければ、内部のロジックは自由に変更できます
* **再利用性の向上**: 適切にカプセル化されたクラスは、ほかのプログラムでも利用しやすくなります

### アクセス修飾子

Javaでは、クラス、フィールド、メソッド、コンストラクタに対してアクセス修飾子を指定することで、外部からのアクセスレベルを制御します。

| 修飾子 | 同じクラス | 同じパッケージ | サブクラス (別パッケージ) | それ以外 (別パッケージ) | 説明 |
|---|---|---|---|---|---|
| `public` | ○ | ○ | ○ | ○ | どこからでもアクセス可能 |
| `protected` | ○ | ○ | ○ | × | 同じクラス、同じパッケージ、または別パッケージのサブクラスからアクセス可能 |
| (default/package-private) | ○ | ○ | × | × | 修飾子を記述しない場合。同じパッケージ内からのみアクセス可能 |
| `private` | ○ | × | × | × | 同じクラス内からのみアクセス可能 |

一般的に、フィールドは`private`にして直接アクセスできないようにし、そのフィールドを操作するための`public`なメソッド（getter/setter）を提供します。

### getter/setterパターン

カプセル化の実践的な手法として、`private`フィールドに対する**getter**（取得メソッド）と**setter**（設定メソッド）を提供する方法があります。

```java
public class Employee {
    private String name; // private: このクラス内からのみアクセス可能
    private int age;     // private: このクラス内からのみアクセス可能
    private String department;
    private double salary;

    public Employee(String name, int age, String department, double salary) {
        this.name = name;
        setAge(age); // ageの設定にはsetterメソッドを使用
        this.department = department;
        this.salary = salary;
    }

    // nameフィールドのgetterメソッド
    public String getName() {
        return name;
    }

    // nameフィールドのsetterメソッド
    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("名前は空にできません。");
        }
    }

    // ageフィールドのgetterメソッド
    public int getAge() {
        return age;
    }

    // ageフィールドのsetterメソッド（バリデーションの例）
    public void setAge(int age) {
        if (age > 0 && age < 150) {
            this.age = age;
        } else {
            throw new IllegalArgumentException("年齢は1～149の範囲で入力してください。");
        }
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        if (salary >= 0) {
            this.salary = salary;
        } else {
            throw new IllegalArgumentException("給与は0以上である必要があります。");
        }
    }

    // 従業員情報を表示するメソッド
    public void displayInfo() {
        System.out.println("名前: " + name);
        System.out.println("年齢: " + age);
        System.out.println("部署: " + department);
        System.out.println("給与: " + salary);
    }

    // 昇給メソッド（ビジネスロジックの例）
    public void giveRaise(double percentage) {
        if (percentage > 0 && percentage <= 50) { // 50%以下の昇給のみ許可
            this.salary = this.salary * (1 + percentage / 100);
            System.out.println(name + " の給与が " + percentage + "% 昇給しました。");
        } else {
            throw new IllegalArgumentException("昇給率は1～50%の範囲で指定してください。");
        }
    }
}
```

### カプセル化の実践例

以下は、適切にカプセル化されたクラスの使用例です：

```java
public class EmployeeManagement {
    public static void main(String[] args) {
        Employee emp1 = new Employee("山田太郎", 30, "開発部", 500000);

        // フィールドに直接アクセスはできない（コンパイルエラー）
        // emp1.name = "佐藤一郎"; // エラー: name は private
        // emp1.age = -5;        // エラー: age は private

        // setterメソッドを通じてアクセス（バリデーション付き）
        emp1.setName("佐藤一郎"); // OK
        System.out.println("従業員の名前: " + emp1.getName());

        try {
            emp1.setAge(35);  // OK
            emp1.setAge(-5);  // 例外が発生
        } catch (IllegalArgumentException e) {
            System.err.println("エラー: " + e.getMessage());
        }

        System.out.println("従業員の年齢: " + emp1.getAge());

        // ビジネスロジックの実行
        try {
            emp1.giveRaise(10); // 10%昇給
            emp1.displayInfo();
        } catch (IllegalArgumentException e) {
            System.err.println("エラー: " + e.getMessage());
        }
    }
}
```

### カプセル化のメリットの実例

#### 1. データの整合性保証

```java
public class BankAccount {
    private double balance;
    private final String accountNumber;

    public BankAccount(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = Math.max(0, initialBalance); // 残高は0以上
    }

    public double getBalance() {
        return balance;
    }

    // 預金メソッド
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println(amount + "円を預金しました。残高: " + balance + "円");
        } else {
            throw new IllegalArgumentException("預金額は正の値である必要があります。");
        }
    }

    // 引き出しメソッド
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println(amount + "円を引き出しました。残高: " + balance + "円");
        } else if (amount <= 0) {
            throw new IllegalArgumentException("引き出し額は正の値である必要があります。");
        } else {
            throw new IllegalArgumentException("残高不足です。残高: " + balance + "円");
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
```

#### 2. 内部実装の変更が外部に影響しない

```java
// Version 1: シンプルな実装
public class ProductCatalog {
    private java.util.List<String> products;

    public ProductCatalog() {
        products = new java.util.ArrayList<>();
    }

    public void addProduct(String product) {
        products.add(product);
    }

    public java.util.List<String> getProducts() {
        return new java.util.ArrayList<>(products); // 防御的コピー
    }
}

// Version 2: より効率的な実装に変更
// 外部からの使用方法は変わらない
public class ProductCatalog {
    private java.util.Set<String> products; // ArrayListからSetに変更

    public ProductCatalog() {
        products = new java.util.HashSet<>();
    }

    public void addProduct(String product) {
        products.add(product); // 重複は自動的に除去される
    }

    public java.util.List<String> getProducts() {
        return new java.util.ArrayList<>(products); // 戻り値は同じ型
    }
}
```

### アクセス修飾子の選択指針

適切なアクセス修飾子を選ぶための一般的な指針：

1. **フィールドは基本的に`private`** - 直接アクセスを防ぎ、getter/setterを通じて制御
2. **メソッドは必要最小限を`public`** - 外部に公開する必要のあるもののみ
3. **パッケージ内でのみ使用するものは`default`** - 明示的にアクセス修飾子を書かない
4. **継承で使用するものは`protected`** - サブクラスからアクセスできるように

```java
public class ExampleClass {
    private int privateField;        // クラス内でのみアクセス可能
    int defaultField;               // 同じパッケージ内でアクセス可能
    protected int protectedField;   // サブクラスからもアクセス可能
    public int publicField;         // どこからでもアクセス可能

    private void privateMethod() { }        // クラス内でのみ呼び出し可能
    void defaultMethod() { }               // 同じパッケージ内で呼び出し可能
    protected void protectedMethod() { }   // サブクラスからも呼び出し可能
    public void publicMethod() { }         // どこからでも呼び出し可能
}
```

カプセル化は、オブジェクト指向プログラミングの基本原則であり、保守性と再利用性の高いコードを書くために不可欠な概念です。適切なアクセス制御により、クラスの責任を明確にし、予期しない変更やエラーを防ぐことができます。