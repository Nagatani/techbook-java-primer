/**
 * リスト2-29
 * BadExampleクラス
 * 
 * 元ファイル: chapter02-getting-started.md (1013行目)
 */

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