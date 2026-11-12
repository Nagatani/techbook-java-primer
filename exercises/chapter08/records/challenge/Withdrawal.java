/**
 * 出金
 */
public record Withdrawal(int day, int amount, Channel channel) implements Transaction {
    public Withdrawal {
        if (amount <= 0) {
            throw new IllegalArgumentException("出金額は1円以上にしてください");
        }
    }
}
