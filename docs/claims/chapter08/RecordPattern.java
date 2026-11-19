// 第8章：レコードパターン（Java 21で正式）
public class RecordPattern {
    record Point(int x, int y) {
    }

    static int f(Object o) {
        if (o instanceof Point(int x, int y)) {
            return x + y;
        }
        return 0;
    }
}
