<!-- 
校正チャンク情報
================
元ファイル: chapter03-oop-basics.md
チャンク: 4/15
行範囲: 550 - 746
作成日時: 2025-08-03 01:46:20

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

#### フィールドのデフォルト値

フィールドにプリミティブ型を指定する場合は、それぞれの型に合わせた初期値が代入されています。
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

#### 実行結果

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

#### 使用例と実行結果

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

#### 実行結果

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

#### メソッドの宣言構文

<span class="listing-number">**サンプルコード3-28**</span>

```java
アクセス修飾子 戻り値の型 メソッド名(引数リスト) {
    // メソッドの処理
    return 戻り値;  // 戻り値がある場合
}
```

<span class="listing-number">**サンプルコード3-9**</span>

```java
public class Rectangle {
    // フィールド
    private double width;
    private double height;
    
    // コンストラクタ（特別なメソッド）
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    // 戻り値があるメソッド
    public double calculateArea() {
        return width * height;
    }
    
    // 戻り値があるメソッド
    public double calculatePerimeter() {
        return 2 * (width + height);
    }
    
    // 戻り値がないメソッド（void）
    public void displayInfo() {
        System.out.println("幅: " + width);
        System.out.println("高さ: " + height);
        System.out.println("面積: " + calculateArea());
        System.out.println("周囲: " + calculatePerimeter());
    }
    
    // 引数を受け取るメソッド
    public void resize(double factor) {
        if (factor > 0) {
            width *= factor;
            height *= factor;
            System.out.println(factor + "倍にリサイズしました");
        }
    }
}



<!-- 
================
チャンク 4/15 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
