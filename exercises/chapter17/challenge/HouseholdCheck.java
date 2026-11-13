import java.util.Map;
import java.util.Objects;

/**
 * Householdの動作確認
 *
 * 画面を開かずに、集計の結果だけを確かめます。
 */
public class HouseholdCheck {
    private static int total = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        IO.println("=== Householdの動作確認 ===");
        Household household = HouseholdApp.sampleData();
        check("支出の件数", 8, household.getExpenses().size());
        check("合計", 24500, household.getTotal());
        check("分類ごとの合計（登録した順、支出のない分類は0）",
                Map.of("食費", 9700, "交通費", 9800, "日用品", 1450, "娯楽", 3550, "その他", 0),
                household.totalsByCategory());
        check("分類の並び", "[食費, 交通費, 日用品, 娯楽, その他]",
                household.totalsByCategory().keySet().toString());
        check("合計の半分は180度", 180, Household.toDegrees(50, 100));
        check("角度は四捨五入する（9700 / 24500 × 360 = 142.5...）", 143, Household.toDegrees(9700, 24500));
        check("合計が0なら0度", 0, Household.toDegrees(0, 0));
        IO.println();
        IO.println(total + "件中" + (total - failed) + "件OK");
    }

    private static void check(String label, Object expected, Object actual) {
        total++;
        if (Objects.equals(expected, actual)) {
            IO.println("OK  " + label);
        } else {
            failed++;
            IO.println("NG  " + label + "（期待: " + expected + "、実際: " + actual + "）");
        }
    }
}
