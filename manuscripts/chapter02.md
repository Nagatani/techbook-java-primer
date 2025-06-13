# 第2章 オブジェクト指向の基礎

この章では、C言語の手続き型プログラミングとは異なる、オブジェクト指向プログラミングの基本概念を学びます。

## 2.1 オブジェクト指向とは

### 手続き型 vs オブジェクト指向

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

## 2.2 クラスとオブジェクト

### クラスの定義

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

### オブジェクトの作成と使用

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

## 2.3 カプセル化

カプセル化は、データと処理を1つにまとめ、外部からの不正なアクセスを防ぐ概念です。

### アクセス修飾子

```java
public class BankAccount {
    private String accountNumber;  // private: クラス内からのみアクセス可能
    private double balance;        // private: 残高を直接変更されないよう保護
    
    public BankAccount(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }
    
    // public: 外部から利用可能なメソッド
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println(amount + "円を入金しました。");
        }
    }
    
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println(amount + "円を出金しました。");
            return true;
        }
        System.out.println("出金できません。");
        return false;
    }
    
    public double getBalance() {
        return balance;
    }
    
    // 口座番号は読み取り専用
    public String getAccountNumber() {
        return accountNumber;
    }
}
```

## 2.4 コンストラクタ

### 基本的なコンストラクタ

```java
public class Rectangle {
    private double width;
    private double height;
    
    // デフォルトコンストラクタ
    public Rectangle() {
        this.width = 1.0;
        this.height = 1.0;
    }
    
    // パラメータ付きコンストラクタ
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    // 正方形用コンストラクタ
    public Rectangle(double side) {
        this.width = side;
        this.height = side;
    }
    
    public double getArea() {
        return width * height;
    }
    
    public double getPerimeter() {
        return 2 * (width + height);
    }
}
```

### コンストラクタの使用例

```java
public class RectangleTest {
    public static void main(String[] args) {
        Rectangle rect1 = new Rectangle();           // デフォルト
        Rectangle rect2 = new Rectangle(5.0, 3.0);  // 長方形
        Rectangle rect3 = new Rectangle(4.0);       // 正方形
        
        System.out.println("rect1の面積: " + rect1.getArea());
        System.out.println("rect2の面積: " + rect2.getArea());
        System.out.println("rect3の面積: " + rect3.getArea());
    }
}
```

## 2.5 thisキーワード

```java
public class Person {
    private String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;  // thisでフィールドを明示
        this.age = age;
    }
    
    public void setName(String name) {
        this.name = name;  // パラメータ名とフィールド名が同じ場合に必要
    }
    
    public Person createCopy() {
        return new Person(this.name, this.age);  // 現在のオブジェクトのコピーを作成
    }
}
```

## 2.6 静的メンバー（static）

```java
public class MathUtil {
    // 静的定数
    public static final double PI = 3.14159265359;
    
    // 静的変数（クラス変数）
    private static int instanceCount = 0;
    
    // インスタンス変数
    private int value;
    
    public MathUtil(int value) {
        this.value = value;
        instanceCount++;  // インスタンス作成時にカウントアップ
    }
    
    // 静的メソッド
    public static double circleArea(double radius) {
        return PI * radius * radius;
    }
    
    public static int getInstanceCount() {
        return instanceCount;
    }
    
    // インスタンスメソッド
    public int getValue() {
        return value;
    }
}
```

### 静的メンバーの使用

```java
public class MathUtilTest {
    public static void main(String[] args) {
        // 静的メソッドの呼び出し（インスタンス作成不要）
        double area = MathUtil.circleArea(5.0);
        System.out.println("円の面積: " + area);
        
        // インスタンス作成
        MathUtil util1 = new MathUtil(10);
        MathUtil util2 = new MathUtil(20);
        
        // 静的変数の確認
        System.out.println("作成されたインスタンス数: " + MathUtil.getInstanceCount());
    }
}
```

## 2.7 練習問題

1. `Car`クラスを作成し、以下の要素を持たせてください：
   - フィールド：メーカー、モデル、年式、走行距離
   - メソッド：情報表示、走行距離追加

2. `Calculator`クラスを作成し、四則演算の静的メソッドを実装してください。

3. `BankAccount`クラスを拡張し、取引履歴を記録する機能を追加してください。

## まとめ

この章では、オブジェクト指向プログラミングの基本概念を学習しました。クラス、オブジェクト、カプセル化、コンストラクタなど、Javaプログラミングの核となる概念を理解することができました。次章では、さらに詳しくクラスとオブジェクトについて学習します。