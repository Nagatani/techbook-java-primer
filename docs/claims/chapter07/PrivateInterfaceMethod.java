// 第7章：インターフェイスのprivateメソッド（Java 9から）
public interface PrivateInterfaceMethod {
    default String a() {
        return prefix() + "a";
    }

    private String prefix() {
        return ">";
    }
}
