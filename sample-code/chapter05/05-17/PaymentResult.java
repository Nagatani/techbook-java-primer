import java.math.BigDecimal;

// 決済結果を保持するクラス
public class PaymentResult {
    private boolean success;
    private String authorizationCode;
    private String message;
    
    public PaymentResult(boolean success, String authorizationCode, String message) {
        this.success = success;
        this.authorizationCode = authorizationCode;
        this.message = message;
    }
    
    public boolean isSuccess() { return success; }
    public String getAuthorizationCode() { return authorizationCode; }
    public String getMessage() { return message; }
}
