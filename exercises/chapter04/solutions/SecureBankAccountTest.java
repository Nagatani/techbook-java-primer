/**
 * SecureBankAccountを試すプログラム（課題2の解答例）
 */
public class SecureBankAccountTest {
    public static void main(String[] args) {
        IO.println("=== セキュア銀行口座システム ===");
        
        // SecureBankAccountオブジェクトを作成する
        SecureBankAccount account = new SecureBankAccount("123456", 100000, "1234");
        IO.println();
        
        // 正しいPINで入金を行ってください
        account.deposit(10000, "1234");
        IO.println();
        
        // 間違ったPINで出金を試行する
        account.withdraw(5000, "9999");
        IO.println();
        
        // 正しいPINで出金を行ってください
        account.withdraw(50000, "1234");
        IO.println();
        
        // 口座情報を表示する
        account.showAccountInfo("1234");
        IO.println();
        
        // 残高照会を行ってください
        double balance = account.getBalance("1234");
        if (balance >= 0) {
            IO.println("照会結果: " + "%,.0f".formatted(balance) + "円");
        }
        
        // 間違ったPINで残高照会を試行する
        double failedBalance = account.getBalance("0000");
        if (failedBalance < 0) {
            IO.println("残高照会に失敗しました。");
        }
    }
}