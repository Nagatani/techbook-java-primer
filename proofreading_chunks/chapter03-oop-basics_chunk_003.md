<!-- 
校正チャンク情報
================
元ファイル: chapter03-oop-basics.md
チャンク: 3/12
行範囲: 493 - 693
作成日時: 2025-08-02 14:34:01

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

##### ポリモーフィズムの仕組み

- ①　統一インターフェイス
        + すべての決済方法が実装すべき共通のメソッド仕様を定義
- ②　多様な実装
        + 同じインターフェイスに対して、具体的な決済手段ごとに異なる実装を提供する
- ③　クレジットカード固有処理
        + カード決済に特化した具体的な処理を実装
- ④　銀行振込の固有処理
        + 銀行振込に特化した具体的な処理を実装

この設計により、`PaymentMethod`型の変数で異なる決済方法を統一的に扱えます。
新しい決済方法（カード会社、決済サービス、仮想通貨決済など）を追加しても既存のコードに影響を与えません。




## クラスの作成

オブジェクト指向プログラミングでは、クラスという設計図を作成し、その設計図からオブジェクト（インスタンス）を生成します。

### クラスの基本構造

クラスは主に以下の要素で構成されます。

1. フィールド（属性）- オブジェクトが持つデータ
2. メソッド（操作）- オブジェクトができる処理
3. コンストラクタ - オブジェクトを初期化する特別なメソッド

これらの要素について、順に詳しく見ていきましょう。

### フィールド（属性）の基礎

フィールドは、オブジェクトが持つデータを表現します。各オブジェクトは自分専用のフィールドの値を持ちます。

<span class="listing-number">**サンプルコード3-6**</span>

```java
public class Student {
    // フィールドの宣言
    String name;           // 名前
    int age;              // 年齢
    double height;        // 身長（cm）
    boolean isActive;     // 在籍中かどうか
    
    // 初期値を持つフィールド
    String school = "東京大学";    // 学校名（初期値付き）
    int grade = 1;                // 学年（初期値: 1年生）
}
```

これはクラス構造の例です。

#### フィールドの重要なポイント
- フィールドは通常、クラスの最初に宣言する
- 各フィールドには型（String、int、doubleなど）が必要
- 初期値を設定しない場合、デフォルト値が自動的に設定される
- フィールド名は意味がわかりやすい名前にする

#### フィールドのデフォルト値

フィールドにプリミティブ型を指定する場合は、それぞれの型に合わせた初期値が代入されます。
それに対して、`String`をはじめとしたクラス型のフィールドの場合、初期値として`null`が代入されます。

これらは、メソッド内部で通常の変数として定義した際の「未初期化」の状態とは異なります。

<span class="listing-number">**サンプルコード3-7**</span>

```java
public class DefaultValueDemo {
    // 数値型のデフォルト値
    int intValue;         // 0
    double doubleValue;   // 0.0
    
    // その他の型のデフォルト値
    boolean boolValue;    // false
    char charValue;       // '\u0000' (空文字)
    String stringValue;   // null
    
    public void showDefaults() {
        System.out.println("int: " + intValue);        // 0
        System.out.println("double: " + doubleValue);  // 0.0
        System.out.println("boolean: " + boolValue);   // false
        System.out.println("String: " + stringValue);  // null
    }
    
    public static void main(String[] args) {
        DefaultValueDemo demo = new DefaultValueDemo();
        demo.showDefaults();
    }
}
```

実行結果：
```
int: 0
double: 0.0
boolean: false
String: null
```

### フィールド（インスタンス変数）の詳細

フィールドは、オブジェクトの状態を表現する変数です。クラス内で宣言され、そのクラスから生成される各オブジェクトが独自の値を保持します。

#### フィールドの宣言構文

<span class="listing-number">**サンプルコード3-27**</span>

```java
アクセス修飾子 データ型 フィールド名;
アクセス修飾子 データ型 フィールド名 = 初期値;
```

これは構文の説明です。

#### 実際の使用例

<span class="listing-number">**サンプルコード3-8**</span>

```java
public class Product {
    // フィールドの宣言
    private String productId;  // 商品ID
    private String name;       // 商品名  
    private double price;      // 価格
    private int stock;         // 在庫数
    
    // コンストラクタでフィールドを初期化
    public Product(String productId, String name, double price) {
        this.productId = productId;  // thisは現在のオブジェクトを指す
        this.name = name;
        this.price = price;
        this.stock = 0;              // 初期在庫は0
    }
    
    // フィールドを使った処理
    public void addStock(int quantity) {
        if (quantity > 0) {
            stock += quantity;  // 在庫数を更新
            System.out.println(quantity + "個入荷しました");
        }
    }
    
    // フィールドの値を表示
    public void showInfo() {
        System.out.println("商品ID: " + productId);
        System.out.println("商品名: " + name);
        System.out.println("価格: ¥" + price);
        System.out.println("在庫数: " + stock + "個");
    }
}
```

使用例と実行結果：
```java
// ProductTest.java
public class ProductTest {
    public static void main(String[] args) {
        Product product = new Product("P001", "ノートPC", 98000);
        
        product.showInfo();
        System.out.println("---");
        
        product.addStock(10);
        product.showInfo();
    }
}
```

実行結果：
```
商品ID: P001
商品名: ノートPC
価格: ¥98000.0
在庫数: 0個
---
10個入荷しました
商品ID: P001
商品名: ノートPC
価格: ¥98000.0
在庫数: 10個
```

#### フィールドのデフォルト値

フィールドに初期値を指定しない場合、以下のデフォルト値が自動的に設定されます。

| データ型 | デフォルト値 |
|---------|------------|
| byte, short, int, long | 0 |
| float, double | 0.0 |
| char | '\u0000' |
| boolean | false |
| 参照型（クラス、配列など） | null |

### メソッド（操作）の基礎

メソッドは、オブジェクトが「できること」を定義します。
C言語の関数と似ていますが、関数とデータ（フィールド）がそれぞれオブジェクトに属している点が異なります。
これにより、データとそれに対してできることをまとめて管理できます。



<!-- 
================
チャンク 3/12 の終了
校正ステータス: [ ] 未完了 / [x] 完了
================
-->
