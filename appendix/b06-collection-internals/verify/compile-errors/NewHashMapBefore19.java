import java.util.HashMap;
import java.util.Map;

/**
 * HashMap.newHashMapはJava 19で加わったメソッドなので、--release 18ではコンパイルできない。
 *
 *   javac --release 18 NewHashMapBefore19.java   （エラーになる）
 *   javac --release 19 NewHashMapBefore19.java   （コンパイルできる）
 */
public class NewHashMapBefore19 {
    static Map<String, Integer> create() {
        return HashMap.newHashMap(100);
    }
}
