/**
 * ChartScaleの動作確認
 *
 * 画面を開かずに、棒の高さの計算だけを確かめます。
 */
public class ChartScaleCheck {
    private static int total = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        IO.println("=== ChartScaleの動作確認 ===");
        check("最大値", 210, ChartScale.max(new int[] {120, 95, 140, 180, 210, 160}));
        check("空の配列の最大値は0", 0, ChartScale.max(new int[0]));
        check("最大値の棒は領域の高さ", 230, ChartScale.barHeight(210, 210, 230));
        check("半分の値は半分の高さ", 115, ChartScale.barHeight(105, 210, 230));
        check("四捨五入する（120 / 210 × 230 = 131.4...）", 131, ChartScale.barHeight(120, 210, 230));
        check("0の値は高さ0", 0, ChartScale.barHeight(0, 210, 230));
        check("最大値が0なら高さ0", 0, ChartScale.barHeight(0, 0, 230));
        IO.println();
        IO.println(total + "件中" + (total - failed) + "件OK");
    }

    private static void check(String label, int expected, int actual) {
        total++;
        if (expected == actual) {
            IO.println("OK  " + label);
        } else {
            failed++;
            IO.println("NG  " + label + "（期待: " + expected + "、実際: " + actual + "）");
        }
    }
}
