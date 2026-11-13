import java.math.BigDecimal;

// ErrorCodeを使って定義し直したビジネス例外
public class InsufficientBalanceException extends BusinessException {
    public InsufficientBalanceException(BigDecimal currentBalance, 
                                      BigDecimal requestedAmount) {
        super(ErrorCode.ACC_001);
        addDetail("currentBalance", currentBalance);
        addDetail("requestedAmount", requestedAmount);
        addDetail("shortage", requestedAmount.subtract(currentBalance));
    }
}
