public class BankAccount {
    private double balance;      // 外部から直接変更不可
    private String accountId;    // 内部管理用ID
    
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;   // 同じクラス内からは可視
            logTransaction();    // プライベートメソッドの呼び出し
        }
    }
    
    private void logTransaction() {  // 内部処理専用メソッド
        // トランザクションログの記録
    }
}

// テスト用のクラス
class BankAccountTest {
    public static void main(String[] args) {
        BankAccount account = new BankAccount();
        
        // フィールドは直接アクセスできない
        // account.balance = 1000; // コンパイルエラー
        
        // publicメソッドを通じて操作
        account.deposit(5000);
        // getBalanceメソッドは未実装のため、残高は表示できません
        IO.println("入金処理が完了しました");
        
        // privateメソッドは呼び出せない
        // account.logTransaction(); // コンパイルエラー
    }
}
