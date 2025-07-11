/**
 * リスト6-1
 * BankAccountクラス
 * 
 * 元ファイル: chapter06-immutability-and-final.md (47行目)
 */

// カプセル化されていない危険なコード
public class BankAccount {
    public String ownerName;
    public double balance;      // publicなので誰でも直接変更できてしまう
    public double creditLimit;  // クレジット限度額
    public double loanAmount;   // ローン残高
}

public class BankingSystemProblem {
    public static void main(String[] args) {
        BankAccount account = new BankAccount();
        account.ownerName = "山田太郎";
        account.balance = 100000;
        account.creditLimit = 500000;
        account.loanAmount = 200000;

        // 問題1: 不正な値の設定による論理的矛盾
        account.balance = -50000;  // 負の残高（物理的にありえない）
        
        // 問題2: ビジネスルールの破壊
        account.loanAmount = 1000000;  // クレジット限度額を超えるローン
        
        // 問題3: 悪意のある操作やバグによるデータ破壊
        account.balance = Double.MAX_VALUE;  // 突然、無限のお金持ちに！
        
        // 問題4: 並行処理での競合状態
        // スレッドAとスレッドBが同時にbalanceを変更すると...
        // データの整合性が保証されない
    }
}