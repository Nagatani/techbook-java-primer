// 第7章：Java 8でdefaultメソッドとして加わったremoveIf・forEach・sort・stream
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class CollectionDefaults {
    static long f(List<String> list, Collection<String> c, Iterable<String> it) {
        c.removeIf(s -> s.isEmpty());
        it.forEach(s -> { });
        list.sort(Comparator.naturalOrder());
        return new ArrayList<>(c).stream().count();
    }
}
