// 第7章：インターフェイスのdefaultメソッドとstaticメソッド（Java 8から）
public interface DefaultStaticMethods {
    String name();

    default String greet() {
        return "Hello, " + name();
    }

    static String version() {
        return "8";
    }
}
