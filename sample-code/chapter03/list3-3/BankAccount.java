/**
 * リスト3-3
 * BankAccountクラス
 * 
 * 元ファイル: chapter03-oop-basics.md (336行目)
 */

public class BankAccount {
    private double balance;  // ①
    
    public void deposit(double amount) {  // ②
        if (amount > 0) {
            balance += amount;
        }
    }
    
    public double getBalance() {  // ③
        return balance;
    }
}