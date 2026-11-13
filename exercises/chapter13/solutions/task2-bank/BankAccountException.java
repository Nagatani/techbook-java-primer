/**
 * 銀行口座の例外の親クラス（課題2の解答例）
 *
 * Exceptionを継承するので検査例外になる。呼び出す側は、catchするかthrowsで宣言しなければならない。
 */
public class BankAccountException extends Exception {
    private final String errorCode;

    public BankAccountException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    // 原因になった例外（cause）を受け取るコンストラクター。例外の連鎖に使う
    public BankAccountException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
