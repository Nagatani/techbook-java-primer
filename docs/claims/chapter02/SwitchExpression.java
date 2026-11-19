// 第2章：switch式（Java 14で正式）
public class SwitchExpression {
    static String name(int day) {
        return switch (day) {
            case 1 -> "月";
            default -> "他";
        };
    }
}
