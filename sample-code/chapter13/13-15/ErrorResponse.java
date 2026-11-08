import java.util.Map;
import java.time.Instant;

// エラーレスポンスの統一フォーマット
public class ErrorResponse {
    private final String errorCode;
    private final String message;
    private final Map<String, Object> details;
    private final String timestamp;
    private final String path;
    
    public ErrorResponse(BusinessException e, String path) {
        this.errorCode = e.getErrorCode();
        this.message = e.getMessage();
        this.details = e.getDetails();
        this.timestamp = Instant.now().toString();
        this.path = path;
    }
    
    public ErrorResponse(SystemException e, String path) {
        this.errorCode = "SYS-999";
        this.message = "システムエラーが発生しました";
        this.details = Map.of("errorId", e.getErrorId());
        this.timestamp = e.getOccurredAt().toString();
        this.path = path;
    }
    
    // ゲッターメソッド省略
}
