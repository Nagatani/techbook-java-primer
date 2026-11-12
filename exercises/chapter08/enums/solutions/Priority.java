/**
 * 優先度（課題1の解答例）
 *
 * 列挙子は書いた順に並ぶので、compareToは「先に書いたほうが小さい」になる。
 */
public enum Priority {
    URGENT(1, "即座に対応が必要です"),
    HIGH(2, "今日中に対応してください"),
    MEDIUM(3, "今週中に対応してください"),
    LOW(4, "時間があるときに対応してください");

    private final int level;
    private final String instruction;

    Priority(int level, String instruction) {
        this.level = level;
        this.instruction = instruction;
    }

    public int getLevel() {
        return level;
    }

    public String getInstruction() {
        return instruction;
    }

    @Override
    public String toString() {
        return name() + "(" + level + ")";
    }
}
