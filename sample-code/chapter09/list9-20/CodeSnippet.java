/**
 * リスト9-20
 * コードスニペット
 * 
 * 元ファイル: chapter09-records.md (719行目)
 */

// 複雑なバリデーションの例
public record Money(BigDecimal amount, Currency currency) {
    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        
        // 通貨の小数点桁数に合わせて正規化
        if (amount.scale() > currency.getDefaultFractionDigits()) {
            amount = amount.setScale(
                currency.getDefaultFractionDigits(), 
                RoundingMode.HALF_UP
            );
        }
    }
    
    // ビジネスロジックメソッド
    public Money add(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        return new Money(amount.add(other.amount), currency);
    }
    
    public Money multiply(BigDecimal multiplier) {
        return new Money(amount.multiply(multiplier), currency);
    }
    
    public boolean isZero() {
        return amount.equals(BigDecimal.ZERO);
    }
}