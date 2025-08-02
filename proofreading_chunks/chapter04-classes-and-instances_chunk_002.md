<!-- 
校正チャンク情報
================
元ファイル: chapter04-classes-and-instances.md
チャンク: 2/11
行範囲: 169 - 355
作成日時: 2025-08-02 21:08:55

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

##### パッケージプライベート（デフォルト）の使用例

<span class="listing-number">**サンプルコード4-3**</span>

```java
package com.example.internal;

public class DataProcessor {
    String processId;     // 同じパッケージ内からアクセス可能
    
    void processInternal() {  // パッケージ内協調用メソッド
        // 内部処理
    }
}

// 同じパッケージ内の別クラス
class ProcessorHelper {
    void assist(DataProcessor processor) {
        processor.processId = "PROC-001";     // OK: 同じパッケージ
        processor.processInternal();           // OK: 同じパッケージ
    }
}
```

これはパッケージプライベート（デフォルトアクセス）の例です。

`protected`の使用例。
<span class="listing-number">**サンプルコード4-4**</span>

```java
package com.example.base;

public class Vehicle {
    protected String engineType;     // サブクラスからアクセス可能
    protected int maxSpeed;
    
    protected void startEngine() {   // サブクラスで利用可能
        System.out.println("Engine started: " + engineType);
    }
}

// 別パッケージのサブクラス
package com.example.cars;
import com.example.base.Vehicle;

public class Car extends Vehicle {
    public void initialize() {
        engineType = "V6";          // OK: protected継承
        maxSpeed = 200;             // OK: protected継承
        startEngine();              // OK: protectedメソッド
    }
}
```

使用例と実行結果：
```java
// VehicleTest.java
public class VehicleTest {
    public static void main(String[] args) {
        Car car = new Car();
        car.initialize();
        
        // protectedメンバーは別パッケージから直接アクセスできない
        // System.out.println(car.engineType); // コンパイルエラー
    }
}
```

実行結果：
```
Engine started: V6
```

##### `public`の使用例

<span class="listing-number">**サンプルコード4-5**</span>

```java
public class MathUtils {
    public static final double PI = 3.14159;  // 公開定数
    
    public static int add(int a, int b) {     // 公開ユーティリティメソッド
        return a + b;
    }
    
    public boolean isPositive(int number) {   // 公開インスタンスメソッド
        return number > 0;
    }
}

class MathUtilsTest {
    public static void main(String[] args) {
        // publicな定数へのアクセス
        System.out.println("PI = " + MathUtils.PI);
        
        // publicなstaticメソッドの呼び出し
        int sum = MathUtils.add(10, 20);
        System.out.println("10 + 20 = " + sum);
        
        // publicなインスタンスメソッドの呼び出し
        MathUtils utils = new MathUtils();
        System.out.println("5は正の数？ " + utils.isPositive(5));
        System.out.println("-3は正の数？ " + utils.isPositive(-3));
    }
}
```

実行結果：
```
PI = 3.14159
10 + 20 = 30
5は正の数？ true
-3は正の数？ false
```

### getter/setterメソッドのベストプラクティス

getter/setterメソッド（アクセサメソッドとも呼ばれます）は、カプセル化の実装で中心的な役割を果たします。
フィールドの値を取得・設定するだけでなく、データの整合性を保証し、将来の変更に対する柔軍性を提供します。
以下の例では、プライベートフィールドへの安全なアクセスを提供する標準パターンを示します。

<span class="listing-number">**サンプルコード4-6**</span>

```java
public class Product {
    private String name;
    private double price;
    
    // getter：値を取得
    public String getName() {
        return name;
    }
    
    public double getPrice() {
        return price;
    }
    
    // setter：データ検証付きで値を設定
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("商品名は必須です");
        }
        this.name = name;
    }
    
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("価格は負の値にできません");
        }
        this.price = price;
    }
}

class ProductTest {
    public static void main(String[] args) {
        Product product = new Product();
        
        try {
            // 正常なデータ設定
            product.setName("ノートPC");
            product.setPrice(98000);
            System.out.println("商品名: " + product.getName());
            System.out.println("価格: " + product.getPrice() + "円");
            
            // 不正なデータの検証
            product.setName("");  // 例外が発生
        } catch (IllegalArgumentException e) {
            System.out.println("エラー: " + e.getMessage());
        }
        
        try {
            product.setPrice(-1000);  // 例外が発生
        } catch (IllegalArgumentException e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }
}
```

実行結果：
```
商品名: ノートPC
価格: 98000.0円
エラー: 商品名は必須です
エラー: 価格は負の値にできません
```



<!-- 
================
チャンク 2/11 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
