// 第7章：ラムダ式とStream API（Java 8で導入）
import java.util.Arrays;
import java.util.List;

public class LambdaStream {
    static long count(List<String> words) {
        return words.stream().filter(w -> w.length() > 3).count();
    }

    static long sample() {
        return count(Arrays.asList("Java", "C", "Kotlin"));
    }
}
