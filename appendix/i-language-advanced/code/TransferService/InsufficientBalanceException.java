import java.math.BigDecimal;

// 具体的なビジネス例外
public class InsufficientBalanceException extends BusinessException {
    public InsufficientBalanceException(BigDecimal currentBalance, 
                                      BigDecimal requestedAmount) {
        super("ACC-001", "残高が不足しています");
        addDetail("currentBalance", currentBalance);
        addDetail("requestedAmount", requestedAmount);
        addDetail("shortage", requestedAmount.subtract(currentBalance));
    }
}
