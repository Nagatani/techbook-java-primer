/** 注文データの書式や内容が正しくないときの例外 */
public class InvalidOrderException extends OrderException {
    public InvalidOrderException(String message, int lineNumber) {
        super(message, lineNumber);
    }

    public InvalidOrderException(String message, int lineNumber, Throwable cause) {
        super(message, lineNumber, cause);
    }
}
