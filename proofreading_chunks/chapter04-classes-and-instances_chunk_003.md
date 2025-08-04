<!-- 
校正チャンク情報
================
元ファイル: chapter04-classes-and-instances.md
チャンク: 3/10
行範囲: 427 - 653
作成日時: 2025-08-03 02:32:41

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->


#### 使用例と実行結果

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

#### 実行結果

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

#### 使用例と実行結果

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

#### 実行結果

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

#### 残る問題
- 口座番号が後から変更可能（getterがない）
- 取引履歴が残らない
- 口座番号の検証がない

#### ステップ3: 完全なカプセル化（BankAccountV3）

<span class="listing-number">**サンプルコード4-10**</span>

```java
import java.util.*;

public class BankAccountV3 {
    private final String accountNumber;  // finalで不変にする
    private double balance;
    private List<String> transactionHistory;
    
    public BankAccountV3(String accountNumber, double initialBalance) {
        // 口座番号の検証
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("口座番号は必須です");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("初期残高は0以上である必要です");
        }
        
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        this.transactionHistory.add("口座開設: 初期残高 " + initialBalance + "円");
    }
    
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("入金額は正の値である必要です");
        }
        balance += amount;
        transactionHistory.add("入金: " + amount + "円");
    }
    
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }
        if (balance >= amount) {
            balance -= amount;
            transactionHistory.add("出金: " + amount + "円");
            return true;
        }
        transactionHistory.add("出金失敗（残高不足）: " + amount + "円");
        return false;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    // 防御的コピーで履歴を返す
    public List<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }
}
```

#### 使用例と実行結果

```java
// BankAccountV3Test.java
public class BankAccountV3Test {
    public static void main(String[] args) {
        BankAccountV3 account = new BankAccountV3("123456", 10000);
        
        System.out.println("口座番号: " + account.getAccountNumber());
        System.out.println("初期残高: " + account.getBalance());
        
        // 入金
        account.deposit(5000);
        System.out.println("\n5000円入金後の残高: " + account.getBalance());
        
        // 出金成功
        if (account.withdraw(3000)) {
            System.out.println("3000円出金成功後の残高: " + account.getBalance());
        }
        
        // 出金失敗
        if (!account.withdraw(20000)) {
            System.out.println("20000円出金失敗（残高不足）");
        }
        
        // 取引履歴の表示
        System.out.println("\n取引履歴:");
        for (String transaction : account.getTransactionHistory()) {
            System.out.println("  " + transaction);
        }
    }
}
```


<!-- 
================
チャンク 3/10 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
