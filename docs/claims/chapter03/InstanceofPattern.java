// 第3章：instanceofのパターンマッチング（Java 16で正式）
public class InstanceofPattern {
    static int f(Object o) {
        if (o instanceof String s) {
            return s.length();
        }
        return 0;
    }
}
