/**
 * 銀行口座の例外を試すプログラム（課題2の解答例）
 */
public class BankDemo {
    public static void main(String[] args) {
        Bank bank = new Bank();
        bank.open(new BankAccount("12345", 100000));
        bank.open(new BankAccount("67890", 50000));
        IO.println("--- 通常の出金 ---");
        try {
            BankAccount a = bank.find("12345");
            a.withdraw(30000);
            IO.println("口座12345から30,000円出金: 成功（残高: %,d円）".formatted(a.getBalance()));
        } catch (BankAccountException e) {
            IO.println("想定外: " + e.getMessage());
        }
        IO.println("\n--- 残高不足 ---");
        try {
            bank.find("67890").withdraw(80000);
        } catch (InsufficientFundsException e) {
            IO.println("例外: " + e.getClass().getSimpleName());
            IO.println("エラーコード: " + e.getErrorCode());
            IO.println("メッセージ: " + e.getMessage());
            IO.println("詳細: " + e.getDetail());
        } catch (BankAccountException e) {
            IO.println("想定外: " + e.getMessage());
        }
        IO.println("\n--- 存在しない口座 ---");
        try {
            bank.transfer("12345", "99999", 1000);
        } catch (InvalidAccountException e) {
            IO.println("例外: " + e.getClass().getSimpleName());
            IO.println("エラーコード: " + e.getErrorCode());
            IO.println("詳細: " + e.getDetail());
        } catch (BankAccountException e) {
            IO.println("想定外: " + e.getMessage());
        }
        IO.println("\n--- 振込の失敗と例外の連鎖 ---");
        try {
            bank.transfer("12345", "67890", 80000);
        } catch (BankAccountException e) {
            IO.println("例外: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            IO.println("原因: " + e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage());
        }
        try {
            IO.println("口座12345の残高: %,d円（変わらない）".formatted(bank.find("12345").getBalance()));
            IO.println("口座67890の残高: %,d円（変わらない）".formatted(bank.find("67890").getBalance()));
        } catch (InvalidAccountException e) {
            IO.println("想定外: " + e.getMessage());
        }
        IO.println("\n--- 非検査例外 ---");
        try {
            new BankAccount("11111", 0).deposit(-500);
        } catch (IllegalArgumentException e) {
            IO.println("例外: " + e.getMessage());
        }
    }
}
