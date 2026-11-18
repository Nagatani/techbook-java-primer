import java.util.stream.Stream;

/** Stream.mapMultiはJava 16で加わったので、--release 15ではコンパイルできない */
public class MapMultiRelease15 {
    public static void main(String[] args) {
        Stream<Integer> s = Stream.of(1, 2, 3).<Integer>mapMulti((n, sink) -> sink.accept(n));
        System.out.println(s.count());
    }
}
