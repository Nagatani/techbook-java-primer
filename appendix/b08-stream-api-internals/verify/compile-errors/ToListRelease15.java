import java.util.List;
import java.util.stream.Stream;

/** Stream.toList()はJava 16で加わったので、--release 15ではコンパイルできない */
public class ToListRelease15 {
    public static void main(String[] args) {
        List<Integer> list = Stream.of(1, 2, 3).toList();
        System.out.println(list);
    }
}
