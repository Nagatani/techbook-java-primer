import java.util.HashMap;
import java.util.Map;

/**
 * 銀行（課題2の解答例）
 */
public class Bank {
    private final Map<String, BankAccount> accounts = new HashMap<>();

    public void open(BankAccount account) {
        accounts.put(account.getNumber(), account);
    }

    public BankAccount find(String number) throws InvalidAccountException {
        BankAccount account = accounts.get(number);
        if (account == null) {
            throw new InvalidAccountException(number);
        }
        return account;
    }

    // 口座を2つとも見つけてから出金し、出金に成功したときだけ入金する。
    // 途中で失敗したら、どちらの残高も変わらない
    public void transfer(String from, String to, int amount) throws BankAccountException {
        BankAccount source = find(from);
        BankAccount dest = find(to);
        try {
            source.withdraw(amount);
        } catch (InsufficientFundsException e) {
            // 残高不足を「振込の失敗」として包み直す。原因の例外はgetCause()で取り出せる
            throw new TransferFailedException("振込に失敗しました", e);
        }
        dest.deposit(amount);
    }
}
