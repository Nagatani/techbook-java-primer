// 本文では省略しているmainメソッド（引き出しを試す）
public class BankAccountDemo {
    public static void main(String[] args) {
        BankAccount account = new BankAccount();
        account.deposit(1000);
        IO.println("初期残高: " + account.getBalance() + "円");

        withdraw(account, 500);
        withdraw(account, 600);
    }

    private static void withdraw(BankAccount account, int amount) {
        IO.println(amount + "円を引き出します...");
        try {
            account.withdraw(amount);
            IO.println("残高: " + account.getBalance() + "円");
        } catch (InsufficientBalanceException e) {
            IO.println("エラー: " + e.getMessage());
            IO.println("現在の残高: " + account.getBalance() + "円");
        }
    }
}
