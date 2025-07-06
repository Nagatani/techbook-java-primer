# 第2章 Java基本文法 - Part D: クラスとオブジェクトの基礎

## クラスとオブジェクトの基本

章末の練習課題に取り組む前に、Javaプログラミングの核心であるクラスとオブジェクトの基本的な概念と構文を理解しておきましょう。

### クラスの基本構造

Javaでは、すべてのコードは**クラス**という単位で記述します。クラスは「設計図」であり、オブジェクトはその設計図から作られる「実体」です。

#### 基本的なクラスの定義

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

**重要なポイント**：
- `private`を使ってカプセル化（外部からの直接アクセスを防ぐ）
- 各オブジェクトが独自の値を持つ

#### 2. コンストラクタ

オブジェクトを作成する際に呼び出される特別なメソッドです：

```java
public Person(String name, int age) {
    this.name = name;  // パラメータの値をフィールドに設定
    this.age = age;
}
```

**重要なポイント**：
- クラス名と同じ名前
- 戻り値の型を書かない
- `this`キーワードでフィールドを明示的に指定

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

クラスの内部実装を隠し、必要なインターフェースのみを公開する原則：

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

クラスとオブジェクトの基本概念：

1. **クラス**：オブジェクトの設計図
2. **オブジェクト**：クラスから作られた実体
3. **フィールド**：オブジェクトが持つデータ
4. **メソッド**：オブジェクトの振る舞い
5. **コンストラクタ**：オブジェクト作成時の初期化処理
6. **カプセル化**：データの保護と適切なアクセス制御

これらの概念は、次章以降でより詳しく学習しますが、基本的な理解があることで、より複雑なプログラムの作成が可能になります。

---

次のパート：[Part E - 章末演習](chapter02e-exercises.md)