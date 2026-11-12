/**
 * 授業のある曜日
 */
public enum Weekday {
    MONDAY("月"),
    TUESDAY("火"),
    WEDNESDAY("水"),
    THURSDAY("木"),
    FRIDAY("金");

    private final String label;

    Weekday(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
