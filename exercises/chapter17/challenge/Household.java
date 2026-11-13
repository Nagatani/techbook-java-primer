import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 家計簿のデータと集計（画面に依存しないロジック）
 */
public class Household {
    private final List<String> categories;
    private final List<Expense> expenses = new ArrayList<>();

    public Household(List<String> categories) {
        this.categories = List.copyOf(categories);
    }

    public void add(Expense expense) {
        if (!categories.contains(expense.category())) {
            throw new IllegalArgumentException("登録されていない分類です: " + expense.category());
        }
        expenses.add(expense);
    }

    public List<String> getCategories() {
        return categories;
    }

    public List<Expense> getExpenses() {
        return List.copyOf(expenses);
    }

    public int getTotal() {
        int total = 0;
        for (Expense expense : expenses) {
            total += expense.amount();
        }
        return total;
    }

    /**
     * 分類ごとの合計。分類は登録した順に並べ、支出のない分類は0にする
     */
    public Map<String, Integer> totalsByCategory() {
        Map<String, Integer> totals = new LinkedHashMap<>();
        for (String category : categories) {
            totals.put(category, 0);
        }
        for (Expense expense : expenses) {
            totals.put(expense.category(), totals.get(expense.category()) + expense.amount());
        }
        return totals;
    }

    /**
     * 合計に対する割合を、円グラフの角度（度）に直す
     */
    public static int toDegrees(int amount, int total) {
        if (total <= 0) {
            return 0;
        }
        return (int) Math.round(amount * 360.0 / total);
    }
}
