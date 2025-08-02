<!-- 
校正チャンク情報
================
元ファイル: chapter04-classes-and-instances.md
チャンク: 4/11
行範囲: 554 - 745
作成日時: 2025-08-02 21:08:55

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

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

使用例と実行結果：
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

実行結果：
```
口座番号: 123456
初期残高: 10000.0

5000円入金後の残高: 15000.0
3000円出金成功後の残高: 12000.0
20000円出金失敗（残高不足）

取引履歴:
  口座開設: 初期残高 10000.0円
  入金: 5000.0円
  出金: 3000.0円
  出金失敗（残高不足）: 20000.0円
```

#### 完成した設計の特徴
- 不変性
-    + 口座番号はfinalで変更不可
- 完全な検証
-    + すべての入力を検証
- 履歴管理
-    + すべての取引を記録
- 防御的コピー: 内部リストの参照を外部に漏らさない

### 銀行口座クラスのさらなる発展

BankAccountV3の設計を基に、本章ではさらに高度な設計パターンを適用した例を見てみましょう。これは、実際のエンタープライズアプリケーションで使用されるレベルの設計です。

<span class="listing-number">**サンプルコード4-11**</span>

```java
import java.time.LocalDateTime;

// BankAccountV3をさらに発展させた設計例
public class EnhancedBankAccount extends BankAccountV3 {
    // 追加のフィールド：口座の状態管理
    private String status;
    private LocalDateTime lastActivityDate;
    private int failedTransactionCount;
    
    // 口座状態を表す定数
    public static final String STATUS_ACTIVE = "ACTIVE";      // アクティブ
    public static final String STATUS_SUSPENDED = "SUSPENDED"; // 一時停止
    public static final String STATUS_CLOSED = "CLOSED";       // 閉鎖
    
    // 拡張されたコンストラクタ
    public EnhancedBankAccount(String accountNumber, String accountHolder, double initialBalance) {
        super(accountNumber, initialBalance);  // 親クラスのコンストラクタを呼び出し
        this.status = STATUS_ACTIVE;
        this.lastActivityDate = LocalDateTime.now();
        this.failedTransactionCount = 0;
    }
    
    // オーバーライドされた入金メソッド：状態チェックを追加
    @Override
    public void deposit(double amount) {
        validateAccountStatus();
        super.deposit(amount);
        updateLastActivity();
    }
    
    // オーバーライドされた出金メソッド：失敗回数の追跡
    @Override
    public boolean withdraw(double amount) {
        validateAccountStatus();
        boolean success = super.withdraw(amount);
        if (!success) {
            failedTransactionCount++;
            if (failedTransactionCount >= 3) {
                status = STATUS_SUSPENDED;
                System.out.println("連続した取引失敗により、口座が一時停止されました。");
            }
        } else {
            failedTransactionCount = 0;  // 成功したらリセット
        }
        updateLastActivity();
        return success;
    }
    
    // プライベートメソッド：内部ロジック
    private void validateAccountStatus() {
        if (!STATUS_ACTIVE.equals(status)) {
            throw new IllegalStateException("口座がアクティブではありません: " + status);
        }
    }
    
    private void updateLastActivity() {
        this.lastActivityDate = LocalDateTime.now();
    }
    


<!-- 
================
チャンク 4/11 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
