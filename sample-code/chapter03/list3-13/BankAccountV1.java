/**
 * リスト3-13
 * BankAccountV1クラス
 * 
 * 元ファイル: chapter03-oop-basics.md (933行目)
 */

// 悪い例：カプセル化されていない銀行口座
public class BankAccountV1 {
    public String accountNumber;  // public: 誰でも変更可能
    public double balance;        // public: 残高を直接操作可能
    
    public BankAccountV1(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }
}

// 使用例（問題のある使い方）
public class ProblemExample {
    public static void main(String[] args) {
        BankAccountV1 account = new BankAccountV1("12345", 1000.0);
        
        // 問題：残高を直接操作できてしまう
        account.balance = -500.0;  // 負の残高！
        account.accountNumber = "";  // 口座番号を空に！
        
        // 問題：取引履歴が残らない
        account.balance += 1000.0;  // 誰がいつ入金したか不明
    }
}