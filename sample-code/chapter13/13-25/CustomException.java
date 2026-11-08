// 悪い例
public class CustomException extends Exception {
    public CustomException(String message) {
        super(message);
    }
    // 原因となった例外を保持できない
}
