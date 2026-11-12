/**
 * 優先度。並べ替えると高・中・低の順になる。
 */
public enum Priority {
    HIGH("高"),
    MEDIUM("中"),
    LOW("低");

    private final String label;

    Priority(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
