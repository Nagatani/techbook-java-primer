/**
 * 入金
 */
public record Deposit(int day, int amount, Channel channel) implements Transaction {
    public Deposit {
        if (amount <= 0) {
            throw new IllegalArgumentException("入金額は1円以上にしてください");
        }
    }
}
