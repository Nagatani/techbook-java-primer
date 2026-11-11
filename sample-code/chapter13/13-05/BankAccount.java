// 独自の検査例外クラス
class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}

// 使用例
public class BankAccount {
    private int balance;
    public void withdraw(int amount) throws InsufficientBalanceException {
        if (amount > balance) {
            // 意図的に例外をスローする
            throw new InsufficientBalanceException("残高が不足しています。");
        }
        this.balance -= amount;
    }

    public void deposit(int amount) {
        this.balance += amount;
    }

    public int getBalance() {
        return balance;
    }
}
