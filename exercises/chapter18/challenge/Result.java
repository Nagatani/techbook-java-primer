/**
 * 1問ごとの結果。
 */
public enum Result {
    CORRECT("正解"),
    WRONG("不正解"),
    TIME_UP("時間切れ");

    private final String label;

    Result(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
