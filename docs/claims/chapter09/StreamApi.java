// 第9章：Stream API（Java 8で導入）
import java.util.List;
import java.util.stream.Stream;

public class StreamApi {
    static long count(List<String> names) {
        Stream<String> s = names.stream();
        return s.filter(n -> n.length() > 3).count();
    }
}
