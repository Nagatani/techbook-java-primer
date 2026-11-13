/**
 * 口座が見つからない（課題2の解答例）
 */
public class InvalidAccountException extends BankAccountException {
    private final String accountNumber;

    public InvalidAccountException(String accountNumber) {
        super("ACCOUNT_NOT_FOUND", "指定された口座が見つかりません");
        this.accountNumber = accountNumber;
    }

    public String getDetail() {
        return "口座番号: " + accountNumber;
    }
}
