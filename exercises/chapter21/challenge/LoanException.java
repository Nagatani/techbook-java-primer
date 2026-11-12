/** 貸出できないときに投げる例外 */
public class LoanException extends Exception {
    public LoanException(String message) {
        super(message);
    }
}
