import java.math.BigDecimal;

// 本文では省略している補助の型
record Money(BigDecimal amount) {
    static final Money ZERO = new Money(BigDecimal.ZERO);
    Money add(Money other) { return new Money(amount.add(other.amount)); }
    Money subtract(Money other) { return new Money(amount.subtract(other.amount)); }
}

record AccountState(String id, String owner, Money balance) {
    static AccountState empty() { return new AccountState(null, null, Money.ZERO); }
    AccountState withBalance(Money newBalance) { return new AccountState(id, owner, newBalance); }
}
