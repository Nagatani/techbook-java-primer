// 第8章：外部クラスのインスタンスを使わない内部クラスは、Java 18以降のjavacで参照（this$0）を持たない
public class OuterReference {
    int count;

    class UsesOuter {
        int get() {
            return count;
        }
    }

    class NoOuter {
        int get() {
            return 1;
        }
    }
}
