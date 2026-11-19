// 第9章：List.of・Set.of・Map.of（Java 9で追加）
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ImmutableFactory {
    static List<String> list = List.of("A", "B", "C");
    static Set<String> set = Set.of("X", "Y", "Z");
    static Map<String, Integer> map = Map.of("key1", 1, "key2", 2);
}
