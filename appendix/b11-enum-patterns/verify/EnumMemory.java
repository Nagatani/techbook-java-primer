import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;
import org.openjdk.jol.vm.VM;

/**
 * EnumSet・EnumMapとHashSet・HashMapの大きさを、JOLで測って比べる。
 * どの集合も同じenumの定数と値を指すので、その分は除いて、集合そのものの大きさを出す。
 * EnumSet・EnumMapが指すenumのClassと「全定数の配列」は、enumごとに1つを共有するので数えない。
 *   javac -cp jol-core-0.17.jar EnumMemory.java
 *   java -Djdk.attach.allowAttachSelf=true --add-opens java.base/java.util=ALL-UNNAMED \
 *        -cp jol-core-0.17.jar:. EnumMemory
 */
public class EnumMemory {
    enum Day { MON, TUE, WED, THU, FRI, SAT, SUN }

    public static void main(String[] args) throws ReflectiveOperationException {
        Set<Day> enumSet = EnumSet.allOf(Day.class);
        Set<Day> hashSet = new HashSet<>(enumSet);
        Map<Day, Integer> enumMap = new EnumMap<>(Day.class);
        Map<Day, Integer> hashMap = new HashMap<>();
        for (Day d : Day.values()) {
            enumMap.put(d, d.ordinal());
            hashMap.put(d, d.ordinal());
        }
        // 共有している定数（名前の文字列を含む）と、値のIntegerの分
        GraphLayout shared = GraphLayout.parseInstance((Object) Day.values())
            .add(GraphLayout.parseInstance(hashMap.values().toArray()));

        IO.println("要素はDayの%d個".formatted(Day.values().length));
        IO.println(ClassLayout.parseInstance(enumSet).toPrintable());

        long enumSetSize = VM.current().sizeOf(enumSet);
        long hashSetSize = GraphLayout.parseInstance(hashSet).subtract(shared).totalSize();
        Object vals = field(enumMap, "vals");
        long enumMapSize = VM.current().sizeOf(enumMap) + VM.current().sizeOf(vals);
        long hashMapSize = GraphLayout.parseInstance(hashMap).subtract(shared).totalSize();

        IO.println("EnumSet: %dバイト".formatted(enumSetSize));
        IO.println("HashSet: %dバイト".formatted(hashSetSize));
        IO.println("EnumMap: %dバイト（本体%d＋値の配列%d）".formatted(
            enumMapSize, VM.current().sizeOf(enumMap), VM.current().sizeOf(vals)));
        IO.println("HashMap: %dバイト".formatted(hashMapSize));
        IO.println("EnumSetのほうが小さい: " + (enumSetSize < hashSetSize));
        IO.println("EnumMapのほうが小さい: " + (enumMapSize < hashMapSize));
    }

    static Object field(Object target, String name) throws ReflectiveOperationException {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(target);
    }
}
