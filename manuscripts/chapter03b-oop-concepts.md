# 第3章 オブジェクト指向の考え方 - Part B: オブジェクト指向の基本概念

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
public class Animal {
    protected String name;
    
    public void eat() {
        System.out.println(name + "が食事をしています");
    }
}

public class Dog extends Animal {
    public void bark() {
        System.out.println(name + "が吠えています");
    }
}
```

#### 3. ポリモーフィズム
```java
public interface Shape {
    double calculateArea();
}

public class Circle implements Shape {
    private double radius;
    
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
}

public class Rectangle implements Shape {
    private double width, height;
    
    public double calculateArea() {
        return width * height;
    }
}
```

## 2.4 クラスの作成

オブジェクト指向プログラミングでは、**クラス**という設計図を作成し、その設計図から**オブジェクト（インスタンス）**を生成します。

### クラスの基本構造

```java
public class Car {
    // フィールド（状態）
    private String model;
    private String color;
    private int speed;
    
    // コンストラクタ
    public Car(String model, String color) {
        this.model = model;
        this.color = color;
        this.speed = 0;
    }
    
    // メソッド（振る舞い）
    public void accelerate(int increment) {
        speed += increment;
    }
    
    public void brake(int decrement) {
        speed = Math.max(0, speed - decrement);
    }
    
    public void displayInfo() {
        System.out.println(model + " (" + color + ") - 速度: " + speed + "km/h");
    }
}
```

### mainメソッドとプログラムの実行

```java
public class CarDemo {
    public static void main(String[] args) {
        // オブジェクトの生成
        Car myCar = new Car("プリウス", "シルバー");
        
        // メソッドの呼び出し
        myCar.accelerate(50);
        myCar.displayInfo();
        
        myCar.accelerate(30);
        myCar.displayInfo();
        
        myCar.brake(20);
        myCar.displayInfo();
    }
}
```

実行結果：
```
プリウス (シルバー) - 速度: 50km/h
プリウス (シルバー) - 速度: 80km/h
プリウス (シルバー) - 速度: 60km/h
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

---

次のパート：[Part C - クラスとオブジェクト](chapter03c-classes-and-objects.md)