// 第6章：List.copyOf（Java 10で追加）
import java.util.List;

public class ListCopyOf {
    static List<String> copy(List<String> src) {
        return List.copyOf(src);
    }
}
