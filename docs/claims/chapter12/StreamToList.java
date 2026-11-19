// 第12章：Stream.toList（Java 16で追加）
import java.util.List;
import java.util.stream.Stream;

public class StreamToList {
    static List<String> names = Stream.of("a", "b").toList();
}
