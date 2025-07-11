/**
 * リストAC-9
 * GoodBankAccountクラス
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (237行目)
 */

public class GoodBankAccount {
    private List<Transaction> transactions; // 実装詳細を隠蔽
    private double balance;
    
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        transactions.add(new Transaction("DEPOSIT", amount));
        balance += amount;
        // 不変条件: balance = sum of all transactions
    }
    
    public double getBalance() {
        return balance; // 計算結果のコピーを返す
    }
    
    public List<Transaction> getTransactionHistory() {
        return List.copyOf(transactions); // 不変ビューを返す
    }
}