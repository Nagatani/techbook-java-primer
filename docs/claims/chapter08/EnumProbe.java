// 第8章：enum（Java 5で導入）
public class EnumProbe {
    enum Signal { RED, GREEN, YELLOW }

    static Signal first() {
        return Signal.values()[0];
    }
}
