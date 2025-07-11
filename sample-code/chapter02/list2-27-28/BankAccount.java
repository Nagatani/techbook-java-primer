/**
 * リスト2-27
 * BankAccountクラス
 * 
 * 元ファイル: chapter02-getting-started.md (937行目)
 */

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