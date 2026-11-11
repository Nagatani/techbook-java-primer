import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal amount, String currency) {
    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("金額はnullにできません");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("通貨は必須です");
        }
        // 金額を小数点以下2桁に丸める
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }
    
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("異なる通貨は加算できません");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
    
    public Money multiply(int quantity) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)), this.currency);
    }
}
