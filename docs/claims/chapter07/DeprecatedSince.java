// 第7章：@Deprecatedのsinceとforremoval（Java 9から）
public class DeprecatedSince {
    @Deprecated(since = "1.0", forRemoval = true)
    void old() {
    }
}
