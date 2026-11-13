import java.time.LocalDate;

/**
 * 1件の支出（レコード、第8章）
 */
public record Expense(LocalDate date, String category, String item, int amount) {
    public Expense {
        if (amount <= 0) {
            throw new IllegalArgumentException("金額は1円以上にしてください: " + amount);
        }
    }
}
