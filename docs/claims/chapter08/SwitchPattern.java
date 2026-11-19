// 第8章：switchで型を判定するパターン（Java 21で正式）
public class SwitchPattern {
    static int f(Object o) {
        return switch (o) {
            case String s -> s.length();
            default -> 0;
        };
    }
}
