// 使用例
public class ProblemDemo {
    public static void main(String[] args) {
        BankAccountV0 account = new BankAccountV0("123456", 10000);
        
        // 問題1: 直接残高を変更できてしまう
        account.balance = -1000;  // 不正な値の設定が可能
        
        // 問題2: 口座番号を後から変更できてしまう
        account.accountNumber = "999999";  // 口座番号の改ざん
        
        // 問題3: 取引履歴が残らない
        account.balance += 5000;  // 入金の記録がない
    }
}
