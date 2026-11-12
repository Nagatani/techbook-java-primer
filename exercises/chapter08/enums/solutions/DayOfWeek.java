/**
 * 曜日（課題1の解答例）
 *
 * 列挙子ごとに日本語の名前を持たせる。週末かどうかは、フィールドではなくメソッドで求める。
 */
public enum DayOfWeek {
    MONDAY("月曜日"),
    TUESDAY("火曜日"),
    WEDNESDAY("水曜日"),
    THURSDAY("木曜日"),
    FRIDAY("金曜日"),
    SATURDAY("土曜日"),
    SUNDAY("日曜日");

    private final String japaneseName;

    // enumのコンストラクターは、外から呼べない（privateになる）
    DayOfWeek(String japaneseName) {
        this.japaneseName = japaneseName;
    }

    public String getJapaneseName() {
        return japaneseName;
    }

    public boolean isWeekend() {
        return this == SATURDAY || this == SUNDAY;
    }

    // 次の曜日。日曜日の次は月曜日に戻る
    public DayOfWeek next() {
        DayOfWeek[] days = values();
        return days[(ordinal() + 1) % days.length];
    }
}
