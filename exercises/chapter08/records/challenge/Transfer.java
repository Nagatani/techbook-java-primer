/**
 * 他の口座への振込
 */
public record Transfer(int day, int amount, String toAccount, Channel channel) implements Transaction {
    public Transfer {
        if (amount <= 0) {
            throw new IllegalArgumentException("振込額は1円以上にしてください");
        }
        if (toAccount == null || toAccount.isBlank()) {
            throw new IllegalArgumentException("振込先は必須です");
        }
    }
}
