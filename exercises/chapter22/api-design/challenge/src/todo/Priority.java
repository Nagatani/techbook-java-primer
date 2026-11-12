package todo;

/**
 * タスクの優先度です。
 *
 * <p>定数の宣言順（{@code HIGH}、{@code MEDIUM}、{@code LOW}）が、優先度の高い順です。</p>
 *
 * @since 1.0
 */
public enum Priority {
    /** 高い */
    HIGH("高"),
    /** 普通 */
    MEDIUM("中"),
    /** 低い */
    LOW("低");

    private final String label;

    Priority(String label) {
        this.label = label;
    }

    /**
     * 画面に表示するための短い名前を返します。
     *
     * @return 「高」「中」「低」のいずれか
     */
    public String label() {
        return label;
    }
}
