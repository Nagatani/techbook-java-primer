/**
 * リスト3-14
 * BankAccountV2クラス
 * 
 * 元ファイル: chapter03-oop-basics.md (971行目)
 */

// 改善例：基本的なカプセル化
public class BankAccountV2 {
    private String accountNumber;
    private double balance;
    
    public BankAccountV2(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }
    
    // 入金メソッド
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    // 出金メソッド
    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
    
    // 残高照会
    public double getBalance() {
        return balance;
    }
}