/**
 * 残高不足（課題2の解答例）
 */
public class InsufficientFundsException extends BankAccountException {
    private final int requested;
    private final int balance;

    public InsufficientFundsException(int requested, int balance) {
        super("INSUFFICIENT_FUNDS", "残高が不足しています");
        this.requested = requested;
        this.balance = balance;
    }

    public String getDetail() {
        return "要求額: %,d円, 現在残高: %,d円, 不足額: %,d円".formatted(requested, balance, requested - balance);
    }
}
