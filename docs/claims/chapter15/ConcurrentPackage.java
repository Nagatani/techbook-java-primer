// 第15章：java.util.concurrentパッケージ（Java 5で追加）
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentPackage {
    static Map<String, Integer> make() {
        return new ConcurrentHashMap<String, Integer>();
    }
}
