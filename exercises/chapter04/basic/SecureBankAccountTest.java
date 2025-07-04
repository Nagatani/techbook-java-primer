/**
 * 第4章 基本課題2: SecureBankAccountTest クラス
 * 
 * SecureBankAccountクラスをテストするためのクラスです。
 * PINコードによる認証システムとセキュリティを確認してください。
 * 
 * 実行例:
 * === セキュア銀行口座システム ===
 * 口座作成: 口座番号 123456
 * 残高: 100,000円
 * 
 * PIN認証成功
 * 10,000円を入金しました。
 * 現在の残高: 110,000円
 * 
 * PIN認証失敗
 * 取引が拒否されました。
 * 
 * PIN認証成功
 * 50,000円を出金しました。
 * 現在の残高: 60,000円
 */
public class SecureBankAccountTest {
    public static void main(String[] args) {
        System.out.println("=== セキュア銀行口座システム ===");
        
        // TODO: SecureBankAccountオブジェクトを作成してください
        // SecureBankAccount account = new SecureBankAccount("123456", 100000, "1234");
        // System.out.println();
        
        // TODO: 正しいPINで入金を行ってください
        // account.deposit(10000, "1234");
        // System.out.println();
        
        // TODO: 間違ったPINで出金を試行してください
        // account.withdraw(5000, "9999");
        // System.out.println();
        
        // TODO: 正しいPINで出金を行ってください
        // account.withdraw(50000, "1234");
        // System.out.println();
        
        // TODO: 口座情報を表示してください
        // account.showAccountInfo("1234");
        // System.out.println();
        
        // TODO: 残高照会を行ってください
        // double balance = account.getBalance("1234");
        // if (balance >= 0) {
        //     System.out.println("照会結果: " + String.format("%.0f", balance) + "円");
        // }
        
        // TODO: 間違ったPINで残高照会を試行してください
        // double failedBalance = account.getBalance("0000");
        // if (failedBalance < 0) {
        //     System.out.println("残高照会に失敗しました。");
        // }
    }
}