// 第2章：StringBuilder（Java 5で追加）
public class StringBuilderProbe {
    static String f() {
        return new StringBuilder().append("a").append(1).toString();
    }
}
