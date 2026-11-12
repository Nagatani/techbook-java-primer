/** 注文を処理できなかったときの例外の親クラス（検査例外） */
public class OrderException extends Exception {
    private final int lineNumber;

    public OrderException(String message, int lineNumber) {
        super(message);
        this.lineNumber = lineNumber;
    }

    public OrderException(String message, int lineNumber, Throwable cause) {
        super(message, cause);
        this.lineNumber = lineNumber;
    }

    /** 注文データの何行目か */
    public int getLineNumber() {
        return lineNumber;
    }
}
