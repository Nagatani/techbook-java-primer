/**
 * タスクボードの列（列挙型、第8章）
 */
public enum Column {
    TODO("未着手"),
    DOING("作業中"),
    DONE("完了");

    private final String label;

    Column(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
