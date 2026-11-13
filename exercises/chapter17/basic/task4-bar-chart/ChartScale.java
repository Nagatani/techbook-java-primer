/**
 * 棒グラフの棒の高さを計算するクラス（画面に依存しないロジック）
 */
public class ChartScale {

    private ChartScale() {
    }

    /**
     * 配列の中の最大値を返す。空の配列なら0を返す
     */
    public static int max(int[] values) {
        int max = 0;
        for (int value : values) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    /**
     * 最大値の棒がareaHeightの高さになるように、値を棒の高さ（ピクセル）に直す
     * 最大値が0以下なら、どの棒も高さ0にする
     */
    public static int barHeight(int value, int max, int areaHeight) {
        if (max <= 0 || value <= 0) {
            return 0;
        }
        return (int) Math.round((double) value * areaHeight / max);
    }
}
