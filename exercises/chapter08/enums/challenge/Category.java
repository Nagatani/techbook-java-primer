/**
 * 科目の区分。今学期に取りたい単位数をフィールドに持つ
 */
public enum Category {
    REQUIRED("必修", 6),
    ELECTIVE_REQUIRED("選択必修", 4),
    ELECTIVE("選択", 2);

    private final String label;
    private final int targetCredits;

    Category(String label, int targetCredits) {
        this.label = label;
        this.targetCredits = targetCredits;
    }

    public String getLabel() {
        return label;
    }

    public int getTargetCredits() {
        return targetCredits;
    }
}
