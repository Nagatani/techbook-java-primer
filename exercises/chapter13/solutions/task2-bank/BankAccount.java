/**
 * 銀行口座（課題2の解答例）
 */
public class BankAccount {
    private final String number;
    private int balance;

    public BankAccount(String number, int balance) {
        this.number = number;
        this.balance = balance;
    }

    // 残高不足は、呼び出す側が対処すべき「起こりうる失敗」なので、検査例外で知らせる
    public void withdraw(int amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException(amount, balance);
        }
        balance -= amount;
    }

    // 0以下の入金は、呼び出す側のプログラムの誤りなので、非検査例外で知らせる
    public void deposit(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("入金額は1円以上にしてください: " + amount);
        }
        balance += amount;
    }

    public String getNumber() {
        return number;
    }

    public int getBalance() {
        return balance;
    }
}
