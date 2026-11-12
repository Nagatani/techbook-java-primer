/**
 * タスクの優先度。宣言した順（HIGH → LOW）が自然な順序になる
 */
public enum Priority {
    HIGH("高"), MEDIUM("中"), LOW("低");

    private final String label;

    Priority(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
