// 第4章：静的インポート（Java 5で追加）
import static java.lang.Math.max;

public class StaticImport {
    static int f(int a, int b) {
        return max(a, b);
    }
}
