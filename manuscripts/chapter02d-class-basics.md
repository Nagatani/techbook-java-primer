# 第2章 Java基本文法 - Part D: クラスとオブジェクトの基礎

## クラスとオブジェクトの基本

章末の練習課題に取り組む前に、Javaプログラミングの核心であるクラスとオブジェクトの基本的な概念と構文を理解しておきましょう。これらの概念は、オブジェクト指向プログラミングの基盤となる重要な要素です。

### クラスの基本構造

Javaプログラミングにおいて、すべてのコードはクラスという単位で記述されます。クラスをしばしば「設計図」にたとえることがありますが、これは建築における設計図と同じように、実際の建物（オブジェクト）を作るための詳細な仕様を定義するからです。1つの設計図から同じ構造を持つ複数の建物を建ていることができるように、一つのクラスから同じ構造を持つ複数のオブジェクトを作成できます。この関係性を理解することが、オブジェクト指向プログラミングの第一歩となります。

#### 基本的なクラスの定義

以下のコード例では、人物を表現する`Person`クラスを定義しています。このクラスは、実世界の「人」という概念をプログラムで表現するための構造を提供します：

```java
public class Person {
    // フィールド（インスタンス変数）
    private String name;
    private int age;
    
    // コンストラクタ
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // メソッド（振る舞い）
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }
    
    public void introduce() {
        System.out.println("こんにちは、" + name + "です。" + age + "歳です。");
    }
}
```

### クラスの構成要素

#### 1. フィールド（インスタンス変数）

オブジェクトが持つデータを格納する変数です：

```java
private String name;    // 文字列を格納
private int age;        // 整数を格納
private double height;  // 小数を格納
```

フィールドの設計において最も重要な原則は、カプセル化です。`private`修飾子を使用することで、フィールドへの直接アクセスを防ぎ、データの整合性を保護します。これは単なる慣習ではなく、予期しない値の変更によるバグを防ぐための重要な防御機構です。また、各オブジェクトは独自のフィールド値を持つため、同じクラスから作成された複数のオブジェクトが、それぞれ異なる状態を維持できます。

#### 2. コンストラクタ

オブジェクトを作成する際に呼び出される特別なメソッドです：

```java
public Person(String name, int age) {
    this.name = name;  // パラメータの値をフィールドに設定
    this.age = age;
}
```

コンストラクタには、通常のメソッドとは異なる特別な規則があります。まず、コンストラクタの名前は必ずクラス名と完全に一致させる必要があります。これにより、Javaコンパイラはこのメソッドがコンストラクタであることを認識します。また、コンストラクタは戻り値を持たないため、戻り値の型を宣言しません（`void`も書きません）。`this`キーワードは現在のオブジェクトを参照し、パラメータ名とフィールド名が同じ場合に、どちらを指しているかを明確にするために使用されます。

#### 3. メソッド

オブジェクトの振る舞いを定義します：

```java
// 値を返すメソッド
public String getName() {
    return name;
}

// 処理を実行するメソッド
public void introduce() {
    System.out.println("こんにちは、" + name + "です。");
}
```

### オブジェクトの作成と使用

#### オブジェクトの作成

```java
public class Main {
    public static void main(String[] args) {
        // newキーワードでオブジェクトを作成
        Person person1 = new Person("田中太郎", 25);
        Person person2 = new Person("佐藤花子", 30);
        
        // メソッドを呼び出す
        person1.introduce();  // こんにちは、田中太郎です。25歳です。
        person2.introduce();  // こんにちは、佐藤花子です。30歳です。
        
        // getter メソッドで値を取得
        String name1 = person1.getName();
        System.out.println("名前: " + name1);
    }
}
```

### 実践的な例：銀行口座クラス

より実用的な例として、銀行口座を表すクラスを見てみましょう：

```java
public class BankAccount {
    // フィールド
    private String accountNumber;
    private String ownerName;
    private double balance;
    
    // コンストラクタ
    public BankAccount(String accountNumber, String ownerName) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = 0.0;  // 初期残高は0
    }
    
    // 入金メソッド
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println(amount + "円を入金しました。");
        } else {
            System.out.println("入金額が不正です。");
        }
    }
    
    // 出金メソッド
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println(amount + "円を出金しました。");
            return true;
        } else {
            System.out.println("出金できません。");
            return false;
        }
    }
    
    // 残高照会メソッド
    public double getBalance() {
        return balance;
    }
    
    // 口座情報表示メソッド
    public void displayInfo() {
        System.out.println("口座番号: " + accountNumber);
        System.out.println("名義人: " + ownerName);
        System.out.println("残高: " + balance + "円");
    }
}
```

使用例：

```java
public class BankExample {
    public static void main(String[] args) {
        // 口座を作成
        BankAccount account = new BankAccount("12345678", "山田太郎");
        
        // 操作を実行
        account.deposit(10000);     // 10000円を入金しました。
        account.withdraw(3000);     // 3000円を出金しました。
        account.displayInfo();      // 口座情報を表示
        
        // 残高: 7000円
    }
}
```

### クラス設計の重要な概念

#### 1. カプセル化

データ（フィールド）を`private`にして外部から直接アクセスできないようにし、メソッドを通じてのみアクセスを許可する設計パターン：

```java
// 悪い例（カプセル化されていない）
public class BadExample {
    public double balance;  // 外部から直接変更可能
}

// 良い例（カプセル化されている）
public class GoodExample {
    private double balance;  // 外部から直接アクセス不可
    
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        if (balance >= 0) {  // 検証ロジック
            this.balance = balance;
        }
    }
}
```

#### 2. 情報隠蔽

クラスの内部実装を隠し、必要なインターフェイスのみを公開する原則：

```java
public class Calculator {
    // 内部的な計算方法は隠蔽
    private double internalCalculation(double a, double b) {
        // 複雑な計算ロジック
        return a * b * 0.95;
    }
    
    // 公開インターフェース
    public double calculate(double a, double b) {
        return internalCalculation(a, b);
    }
}
```

### アクセス修飾子

Javaには4つのアクセスレベルがあります：

| 修飾子 | 同じクラス | 同じパッケージ | サブクラス | すべて |
|--------|-----------|---------------|-----------|---------|
| private | ○ | × | × | × |
| (なし) | ○ | ○ | × | × |
| protected | ○ | ○ | ○ | × |
| public | ○ | ○ | ○ | ○ |

### staticキーワード

`static`を付けることで、クラスレベルのメンバー（クラス変数・クラスメソッド）を定義できます：

```java
public class MathUtils {
    // クラス変数
    public static final double PI = 3.14159265359;
    
    // クラスメソッド
    public static double circleArea(double radius) {
        return PI * radius * radius;
    }
}

// 使用例（インスタンス化不要）
double area = MathUtils.circleArea(5.0);
```

### まとめ

本節で学習したクラスとオブジェクトの概念は、Javaプログラミングの基礎を形成する重要な要素です。クラスは、関連するデータ（フィールド）と操作（メソッド）を1つの単位にまとめた設計図として機能します。この設計図から作成されるオブジェクトは、それぞれが独立した状態を持つ実体となります。

フィールドはオブジェクトの状態を表現し、メソッドはその状態に対する操作を定義します。コンストラクタは、オブジェクトが作成される際に適切な初期状態を設定する重要な役割を担います。そして、カプセル化の原則により、オブジェクトの内部状態を保護し、制御された方法でのみアクセスを許可することで、プログラムの安全性と保守性を高めています。

これらの概念は、単なる文法上の規則ではなく、現実世界の複雑な問題をプログラムで表現するための強力な道具です。銀行口座の例で見たように、実世界の概念（口座、入金、出金）をクラスとして適切にモデル化することで、理解しやすく保守しやすいプログラムを作成できます。次章以降では、これらの基本概念をさらに発展させ、継承やポリモーフィズムといった高度なオブジェクト指向の概念を学習していきます。

---

次のパート：[Part E - 章末演習](chapter02e-exercises.md)