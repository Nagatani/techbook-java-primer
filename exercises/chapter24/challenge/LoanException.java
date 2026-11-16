/**
 * 貸出・返却の決まりに合わないときの例外（貸出中の本、冊数の上限など）
 *
 * データベースの障害を表すSQLExceptionとは分けて、利用者に伝える理由を持たせる。
 */
public class LoanException extends Exception {
    public LoanException(String message) {
        super(message);
    }
}
