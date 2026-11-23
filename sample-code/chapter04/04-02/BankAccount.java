public class BankAccount {
    private double balance;      // 外部から直接変更不可
    private int transactionCount;  // 取引の回数（内部でだけ数える）
    
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;   // 同じクラス内からは可視
            recordTransaction(); // プライベートメソッドの呼び出し
        }
    }
    
    public double getBalance() {
        return balance;
    }
    
    // 内部の処理を分けるためのメソッド（外からは呼ばせない）
    private void recordTransaction() {
        transactionCount++;
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
        IO.println("残高: " + account.getBalance());
        
        // privateメソッドは呼び出せない
        // account.recordTransaction(); // コンパイルエラー
    }
}
