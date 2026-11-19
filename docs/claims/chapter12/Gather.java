// 第12章：Stream.gatherとGatherers（Java 24で正式、JEP 485）
import java.util.List;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public class Gather {
    static List<List<Integer>> windows() {
        return Stream.of(1, 2, 3, 4).gather(Gatherers.windowFixed(2)).toList();
    }
}
