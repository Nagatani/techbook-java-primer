import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * List.ofなどの変更できないコレクションの実装クラスと、nullの扱い、ビューとコピーの違いを見る。
 *
 *   javac Unmodifiable.java
 *   java Unmodifiable
 */
public class Unmodifiable {

    public static void main(String[] args) {
        classes();
        nulls();
        duplicates();
        viewAndCopy();
        others();
    }

    /** 要素の数によって、実装クラスが変わる */
    static void classes() {
        IO.println("== 実装クラス ==");
        show("List.of()", List.of());
        show("List.of(1)", List.of(1));
        show("List.of(1, 2)", List.of(1, 2));
        show("List.of(1, 2, 3)", List.of(1, 2, 3));
        show("Set.of(1, 2)", Set.of(1, 2));
        show("Set.of(1, 2, 3)", Set.of(1, 2, 3));
        show("Map.of(1, 2)", Map.of(1, 2));
        show("Map.of(1, 2, 3, 4)", Map.of(1, 2, 3, 4));
        show("Stream.toList()", Stream.of(1, 2, 3).toList());
        show("Arrays.asList(1, 2, 3)", Arrays.asList(1, 2, 3));
        show("Collections.unmodifiableList", Collections.unmodifiableList(new ArrayList<>()));
        fields(List.of(1, 2));
        fields(List.of(1, 2, 3));
    }

    /** 実装クラスのインスタンスフィールドの名前と型 */
    static void fields(Object o) {
        StringBuilder sb = new StringBuilder();
        for (java.lang.reflect.Field f : o.getClass().getDeclaredFields()) {
            if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                sb.append(f.getType().getSimpleName()).append(" ").append(f.getName()).append("; ");
            }
        }
        IO.println(o.getClass().getSimpleName() + "のフィールド: " + sb.toString().trim());
    }

    static void show(String label, Object o) {
        IO.println(label + " -> " + o.getClass().getName());
    }

    /** nullを入れる・nullを探す */
    static void nulls() {
        IO.println("== null ==");
        attempt("List.of(1, null)", () -> List.of(1, null));
        attempt("List.of(1, 2).contains(null)", () -> List.of(1, 2).contains(null));
        attempt("Stream.of(1, null).toList()", () -> Stream.of(1, null).toList());
        attempt("new ArrayList<>(List.of(1)).contains(null)",
                () -> new ArrayList<>(List.of(1)).contains(null));
        attempt("ConcurrentHashMapにnullの値をput",
                () -> new ConcurrentHashMap<String, Integer>().put("a", null));
    }

    /** Set.ofとMap.ofは、重複した要素・キーを受け付けない */
    static void duplicates() {
        IO.println("== 重複 ==");
        attempt("Set.of(1, 1)", () -> Set.of(1, 1));
        attempt("Set.copyOf(List.of(1, 1))", () -> Set.copyOf(List.of(1, 1)));
    }

    /** unmodifiableListは元のリストのビュー、List.copyOfはコピー */
    static void viewAndCopy() {
        IO.println("== ビューとコピー ==");
        List<String> source = new ArrayList<>(List.of("A"));
        List<String> view = Collections.unmodifiableList(source);
        List<String> copy = List.copyOf(source);
        source.add("B");
        IO.println("元にBを足したあと: unmodifiableList=" + view + " List.copyOf=" + copy);
        List<String> fixed = List.of("A");
        IO.println("変更できないリストのList.copyOfは同じオブジェクト: " + (List.copyOf(fixed) == fixed));
    }

    /** Arrays.asListは要素の置き換えだけができる */
    static void others() {
        IO.println("== Arrays.asList ==");
        String[] array = {"A", "B"};
        List<String> list = Arrays.asList(array);
        list.set(0, "Z");
        IO.println("asList(配列).set(0, \"Z\")のあとの配列: " + Arrays.toString(array));
        attempt("asList(配列).add(\"C\")", () -> list.add("C"));
        attempt("List.of(\"A\").set(0, \"Z\")", () -> List.of("A").set(0, "Z"));
    }

    static void attempt(String label, java.util.function.Supplier<Object> action) {
        try {
            IO.println(label + " -> " + action.get());
        } catch (RuntimeException e) {
            IO.println(label + " -> " + e.getClass().getSimpleName());
        }
    }
}
