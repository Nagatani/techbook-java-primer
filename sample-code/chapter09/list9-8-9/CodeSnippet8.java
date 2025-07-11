/**
 * リスト9-8
 * コードスニペット
 * 
 * 元ファイル: chapter09-records.md (343行目)
 */

// ドメインモデルの明確な表現
public record Product(
    String id,
    String name,
    ProductCategory category,
    Money price,
    Inventory inventory
) {}

public record Money(BigDecimal amount, Currency currency) {
    public Money {
        if (amount.scale() > currency.getDefaultFractionDigits()) {
            amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
        }
    }
    
    public Money add(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        return new Money(amount.add(other.amount), currency);
    }
}