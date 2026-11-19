// 第21章：assert文（Java 1.4から）
public class AssertStatement {
    static int half(int x) {
        assert x % 2 == 0 : "偶数を渡す";
        return x / 2;
    }
}
