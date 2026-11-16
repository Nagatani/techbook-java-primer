// RepositoryException.java - 保存先で起きた異常（利用者には直せない）
public class RepositoryException extends RuntimeException {
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
