import java.util.*;
import java.time.Instant;

// ビジネス例外の基底クラス
public abstract class BusinessException extends Exception {
    private final String errorCode;
    private final Map<String, Object> details;
    
    protected BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.details = new HashMap<>();
    }
    
    // ErrorCodeからコードと既定のメッセージを受け取る
    protected BusinessException(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage());
    }
    
    protected BusinessException(String errorCode, String message, 
                              Map<String, Object> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = new HashMap<>(details);
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public Map<String, Object> getDetails() {
        return Collections.unmodifiableMap(details);
    }
    
    public void addDetail(String key, Object value) {
        details.put(key, value);
    }
}
