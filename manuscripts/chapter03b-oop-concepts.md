## 第3章 オブジェクト指向の考え方 - Part B: オブジェクト指向の基本概念

### 2.3 オブジェクト指向の基本概念を実際のコードで理解する

理論的な説明の前に、実際のJavaコードを見てオブジェクト指向の基本概念を理解しましょう。

#### 手続き型とオブジェクト指向の違い

##### 手続き型の例（C言語風）
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

##### オブジェクト指向の例
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

#### オブジェクト指向の3つの基本原則

##### 1. カプセル化
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

##### 2. 継承
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

##### 3. ポリモーフィズム
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

### 2.4 クラスの作成

オブジェクト指向プログラミングでは、**クラス**という設計図を作成し、その設計図から**オブジェクト（インスタンス）**を生成します。

#### クラスの基本構造

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

#### mainメソッドとプログラムの実行

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

### 2.5 実用的なクラス設計例

#### 例1：図書管理システム

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

#### 例2：ショッピングカート

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

### まとめ

このパートでは、オブジェクト指向の基本概念を実際のコードを通じて学習しました。重要なポイントは：

1. **データと処理の一体化**：クラスは関連するデータと処理をまとめる
2. **カプセル化**：内部実装を隠蔽し、必要なインターフェイスのみを公開
3. **オブジェクトの生成と操作**：newキーワードでインスタンスを生成し、メソッドを通じて操作
4. **現実世界のモデリング**：実世界の概念をクラスとして表現

次のパートでは、より詳細なクラスの機能について学習します。

---

次のパート：[Part C - クラスとオブジェクト](chapter03c-classes-and-objects.md)
