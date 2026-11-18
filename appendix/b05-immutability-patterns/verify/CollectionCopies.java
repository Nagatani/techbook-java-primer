import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 変更できないリストの作り方ごとに、ビューかコピーか、
 * nullを入れられるか、copyOfがコピーを作るかを比べる。
 *   javac CollectionCopies.java && java CollectionCopies
 */
public class CollectionCopies {
    public static void main(String[] args) {
        List<String> source = new ArrayList<>(List.of("a", "b"));
        List<String> view = Collections.unmodifiableList(source);
        List<String> copy = List.copyOf(source);
        source.add("c");
        IO.println("元のリストにcを足した後");
        IO.println("  unmodifiableList: " + view);
        IO.println("  List.copyOf:      " + copy);

        List<String> fixed = List.of("x", "y");
        IO.println("List.copyOf(List.of(...))は同じオブジェクト: "
                + (List.copyOf(fixed) == fixed));
        IO.println("List.copyOf(unmodifiableList)は同じオブジェクト: "
                + (List.copyOf(view) == view));
        IO.println("unmodifiableList(unmodifiableList)は同じオブジェクト: "
                + (Collections.unmodifiableList(view) == view));

        List<String> withNull = new ArrayList<>(Arrays.asList("p", null));
        IO.println("unmodifiableListはnullを持てる: "
                + Collections.unmodifiableList(withNull));
        tryRun("List.copyOfにnullを含むリスト", () -> List.copyOf(withNull));
        tryRun("List.ofにnull", () -> List.of("p", null));

        List<String> asList = Arrays.asList("s", "t");
        asList.set(0, "S");
        IO.println("Arrays.asListはsetできる: " + asList);
        tryRun("Arrays.asListにadd", () -> asList.add("u"));

        List<String> streamed = Stream.of("m", "n").toList();
        tryRun("Stream.toList()の結果にadd", () -> streamed.add("o"));
        List<String> collected = Stream.of("m", "n").collect(Collectors.toList());
        collected.add("o");
        IO.println("Collectors.toList()の結果にadd: " + collected);

        IO.println("List.of(2要素)のクラス: " + fixed.getClass().getName());
        IO.println("List.of(3要素)のクラス: "
                + List.of(1, 2, 3).getClass().getName());
        IO.println("  List12のフィールド: " + fieldsOf(fixed.getClass()));
        IO.println("  ListNのフィールド: " + fieldsOf(List.of(1, 2, 3).getClass()));
    }

    // staticでないフィールドの「型 名前」の一覧（中身は読まないので--add-opensは要らない）
    static String fieldsOf(Class<?> c) {
        StringBuilder sb = new StringBuilder();
        for (Field f : c.getDeclaredFields()) {
            if (!Modifier.isStatic(f.getModifiers())) {
                sb.append(f.getType().getSimpleName()).append(' ').append(f.getName()).append(' ');
            }
        }
        return sb.toString().strip();
    }

    static void tryRun(String label, Runnable r) {
        try {
            r.run();
            IO.println(label + ": 例外なし");
        } catch (RuntimeException e) {
            IO.println(label + ": " + e.getClass().getSimpleName());
        }
    }
}
