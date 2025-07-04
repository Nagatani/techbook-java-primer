/**
 * 第4章 演習問題2: SecureBankAccountTestクラスの解答例
 */
public class SecureBankAccountTest {
    public static void main(String[] args) {
        System.out.println("=== SecureBankAccount テスト ===");
        
        SecureBankAccount account = new SecureBankAccount("123456", "1234", 100000.0);
        
        // 正常な取引のテスト
        System.out.println("--- 正常な取引 ---");
        account.deposit("1234", 10000.0);
        account.withdraw("1234", 5000.0);
        
        // 認証失敗のテスト
        System.out.println("\n--- 認証失敗テスト ---");
        account.deposit("0000", 1000.0);  // 間違ったPIN
        account.deposit("1111", 1000.0);  // 間違ったPIN
        account.deposit("2222", 1000.0);  // 間違ったPIN（口座ロック）
        
        // ロック後の操作試行
        System.out.println("\n--- ロック後の操作 ---");
        account.deposit("1234", 1000.0);  // 正しいPINでもロックされている
        
        // 管理者によるロック解除
        System.out.println("\n--- ロック解除 ---");
        account.unlockAccount("ADMIN123");
        
        // ロック解除後の操作
        System.out.println("\n--- ロック解除後の操作 ---");
        account.deposit("1234", 5000.0);
        
        // 取引履歴の表示
        System.out.println("\n--- 取引履歴 ---");
        account.showTransactionHistory("1234");
    }
}