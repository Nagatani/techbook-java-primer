/**
 * 科目。科目コード・科目名・曜日時限（例: "月1"）・単位数を持つ
 */
public class Course {
    private final String code;
    private final String name;
    private final String slot;
    private final int credits;

    public Course(String code, String name, String slot, int credits) {
        this.code = code;
        this.name = name;
        this.slot = slot;
        this.credits = credits;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSlot() {
        return slot;
    }

    public int getCredits() {
        return credits;
    }

    @Override
    public String toString() {
        return code + " " + name + "（" + slot + "、" + credits + "単位）";
    }
}
