import java.util.*;
import java.time.Instant;

// システム例外の基底クラス
public abstract class SystemException extends RuntimeException {
    private final String errorId;
    private final Instant occurredAt;
    
    protected SystemException(String message, Throwable cause) {
        super(message, cause);
        this.errorId = generateErrorId();
        this.occurredAt = Instant.now();
    }
    
    private String generateErrorId() {
        return String.format("SYS-%s-%s", 
            getClass().getSimpleName(), 
            java.util.UUID.randomUUID().toString().substring(0, 8));
    }
    
    public String getErrorId() {
        return errorId;
    }
    
    public Instant getOccurredAt() {
        return occurredAt;
    }
}
