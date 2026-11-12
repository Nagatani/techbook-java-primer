/**
 * 時限。開始と終了の時刻を持つ
 */
public enum Period {
    FIRST("9:00", "10:30"),
    SECOND("10:40", "12:10"),
    THIRD("13:00", "14:30"),
    FOURTH("14:40", "16:10"),
    FIFTH("16:20", "17:50");

    private final String start;
    private final String end;

    Period(String start, String end) {
        this.start = start;
        this.end = end;
    }

    // 「1限」のような表示。ordinal()は0から数える
    public String getLabel() {
        return (ordinal() + 1) + "限";
    }

    public String getTimeRange() {
        return start + "-" + end;
    }

    // 午前の時限か（昼休みより前）
    public boolean isMorning() {
        return this == FIRST || this == SECOND;
    }

    // n個あとの時限。範囲を超えるならnull
    public Period after(int n) {
        int index = ordinal() + n;
        Period[] all = values();
        return index < all.length ? all[index] : null;
    }
}
