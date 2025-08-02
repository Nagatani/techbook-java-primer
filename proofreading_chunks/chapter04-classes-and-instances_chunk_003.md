<!-- 
校正チャンク情報
================
元ファイル: chapter04-classes-and-instances.md
チャンク: 3/11
行範囲: 356 - 553
作成日時: 2025-08-02 23:30:11

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

### データ検証の重要性

プログラミングで、データの状態を常に有効に保つことはきわめて重要です。オブジェクト指向では、これをカプセル化とsetterメソッドによる検証で実現します。setterメソッドは単なる値の代入ではなく、オブジェクトの不変条件（invariant）を守るゲートキーパーとしての役割を担います。範囲チェック、nullチェック、ビジネスルールの検証を実装すると、バグの早期発見と予防が可能になり、システム全体の信頼性が向上します。以下の例では、実務でよく使用される検証パターンを示します。

<span class="listing-number">**サンプルコード4-7**</span>

```java
public class Employee {
    private String name;
    private int age;
    private double salary;
    
    public void setAge(int age) {
        if (age < 18 || age > 100) {
            throw new IllegalArgumentException("年齢は18歳以上100歳以下で入力してください");
        }
        this.age = age;
    }
    
    public void setSalary(double salary) {
        if (salary < 0) {
            throw new IllegalArgumentException("給与は負の値にできません");
        }
        this.salary = salary;
    }
}
```

これはデータ検証パターンの例です。

## 設計原則とソフトウェアアーキテクチャ

ソフトウェア設計では、SOLID原則と呼ばれる5つの基本原則があります。これらはオブジェクト指向設計の文脈で生まれましたが、単一責任原則（モジュールは1つの責任のみを持つべき）や開放閉鎖原則（拡張に開かれ、修正に閉じているべき）などは、他のプログラミングパラダイムでも応用可能な普遍的な原則です。

カプセル化は単にデータを隠す技術ではなく、変更の影響を局所化し、再利用性とテスト容易性を高める重要な設計技術です。

## 実践的なクラス設計例

### 銀行口座クラスの段階的な改善

先ほどの問題のあるBankAccountV0を、カプセル化の原則にしたがって段階的に改善していきましょう。

#### ステップ1: 基本的なカプセル化（BankAccountV1）

まず、フィールドをprivateにして、publicメソッドを通じてのみアクセスできるようにします。

<span class="listing-number">**サンプルコード4-8**</span>

```java
public class BankAccountV1 {
    private String accountNumber;
    private double balance;
    
    public BankAccountV1(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }
    
    public void deposit(double amount) {
        balance += amount;
    }
    
    public void withdraw(double amount) {
        balance -= amount;
    }
    
    public double getBalance() {
        return balance;
    }
}
```

使用例と実行結果：
```java
// BankAccountV1Test.java
public class BankAccountV1Test {
    public static void main(String[] args) {
        BankAccountV1 account = new BankAccountV1("123456", 10000);
        
        System.out.println("初期残高: " + account.getBalance());
        
        account.deposit(5000);
        System.out.println("5000円入金後: " + account.getBalance());
        
        account.withdraw(3000);
        System.out.println("3000円出金後: " + account.getBalance());
        
        // 問題：負の金額でも処理される
        account.deposit(-1000);
        System.out.println("負の金額入金後: " + account.getBalance());
    }
}
```

実行結果：
```
初期残高: 10000.0
5000円入金後: 15000.0
3000円出金後: 12000.0
負の金額入金後: 11000.0
```

#### 改善点
- フィールドがprivateになり、直接アクセスできない
- メソッドを通じてのみ操作可能

#### 残る問題
- 負の金額でも入金・出金できてしまう
- 残高不足でも出金できてしまう
- 初期残高が負の値でも設定できる

#### ステップ2: 基本的な検証を追加（BankAccountV2）

<span class="listing-number">**サンプルコード4-9**</span>

```java
public class BankAccountV2 {
    private String accountNumber;
    private double balance;
    
    public BankAccountV2(String accountNumber, double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("初期残高は0以上である必要です");
        }
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }
    
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("入金額は正の値である必要です");
        }
        balance += amount;
    }
    
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
    
    public double getBalance() {
        return balance;
    }
}
```

使用例と実行結果：
```java
// BankAccountV2Test.java
public class BankAccountV2Test {
    public static void main(String[] args) {
        try {
            BankAccountV2 account = new BankAccountV2("123456", 10000);
            System.out.println("初期残高: " + account.getBalance());
            
            // 正常な入金
            account.deposit(5000);
            System.out.println("5000円入金後: " + account.getBalance());
            
            // 出金の成功
            if (account.withdraw(3000)) {
                System.out.println("3000円出金成功: " + account.getBalance());
            }
            
            // 残高不足の出金
            if (!account.withdraw(20000)) {
                System.out.println("20000円出金失敗（残高不足）");
            }
            
            // 不正な入金額
            account.deposit(-1000);
        } catch (IllegalArgumentException e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }
}
```

実行結果：
```
初期残高: 10000.0
5000円入金後: 15000.0
3000円出金成功: 12000.0
20000円出金失敗（残高不足）
エラー: 入金額は正の値である必要です
```

#### 改善点
- コンストラクタで初期値を検証
- 入金時に金額を検証
- 出金時に残高と金額を確認



<!-- 
================
チャンク 3/11 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
