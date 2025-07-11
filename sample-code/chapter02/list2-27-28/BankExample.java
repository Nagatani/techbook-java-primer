/**
 * リスト2-28
 * BankExampleクラス
 * 
 * 元ファイル: chapter02-getting-started.md (990行目)
 */

public class BankExample {
    public static void main(String[] args) {
        // 口座を作成
        BankAccount account = new BankAccount("12345678", "山田太郎");
        
        // 操作を実行
        account.deposit(10000);     // 10000円を入金しました。
        account.withdraw(3000);     // 3000円を出金しました。
        account.displayInfo();      // 口座情報を表示
        
        // 残高: 7000円
    }
}