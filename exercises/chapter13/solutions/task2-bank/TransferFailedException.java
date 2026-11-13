/**
 * 振込の失敗（課題2の解答例）。失敗の原因になった例外を持つ
 */
public class TransferFailedException extends BankAccountException {
    public TransferFailedException(String message, Throwable cause) {
        super("TRANSFER_FAILED", message, cause);
    }
}
