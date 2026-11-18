import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeMap;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;

/**
 * JOL（Java Object Layout）で、コレクションの1要素ごとのオブジェクトの大きさと、
 * 1000件を入れたコレクション全体の大きさを測る。
 * JOLのJAR（org.openjdk.jol:jol-core:0.17）をクラスパスに加えて実行する。
 *
 *   javac -cp jol-core-0.17.jar EntrySizes.java
 *   java -Djdk.attach.allowAttachSelf=true -cp jol-core-0.17.jar:. EntrySizes
 *   java -XX:+UseCompactObjectHeaders -Djdk.attach.allowAttachSelf=true -cp jol-core-0.17.jar:. EntrySizes
 */
public class EntrySizes {

    public static void main(String[] args) throws Exception {
        IO.println("== HashMap$Nodeの配置 ==");
        IO.println(ClassLayout.parseClass(Class.forName("java.util.HashMap$Node")).toPrintable());

        IO.println("== 1要素ごとのオブジェクト ==");
        String[] names = {
            "java.util.HashMap$Node", "java.util.HashMap$TreeNode",
            "java.util.LinkedHashMap$Entry", "java.util.TreeMap$Entry",
            "java.util.LinkedList$Node",
        };
        for (String name : names) {
            ClassLayout layout = ClassLayout.parseClass(Class.forName(name));
            IO.println(name.replace("java.util.", "") + ": " + layout.instanceSize()
                    + "バイト（ヘッダー" + layout.headerSize() + "バイト）");
        }

        IO.println("== 1要素ごとのオブジェクトのフィールド（スーパークラスの分を含む） ==");
        for (String name : names) {
            IO.println(name.replace("java.util.", "") + ": " + fields(Class.forName(name)));
        }

        IO.println("== 1000件を入れたコレクション全体（キーと値のIntegerを除く） ==");
        int n = 1000;
        Integer[] keys = new Integer[n];
        for (int i = 0; i < n; i++) {
            keys[i] = 1000 + i;   // -128〜127のキャッシュを避け、すべて別のオブジェクトにする
        }
        // 配列の要素を1つずつ根として渡すので、1000個のIntegerだけの大きさになる
        long integers = GraphLayout.parseInstance((Object[]) keys).totalSize();

        HashMap<Integer, Integer> hash = new HashMap<>();
        LinkedHashMap<Integer, Integer> linked = new LinkedHashMap<>();
        TreeMap<Integer, Integer> tree = new TreeMap<>();
        ArrayList<Integer> array = new ArrayList<>();
        LinkedList<Integer> list = new LinkedList<>();
        for (Integer k : keys) {
            hash.put(k, k);
            linked.put(k, k);
            tree.put(k, k);
            array.add(k);
            list.add(k);
        }
        report("HashMap", hash, integers, n);
        report("LinkedHashMap", linked, integers, n);
        report("TreeMap", tree, integers, n);
        report("ArrayList", array, integers, n);
        report("LinkedList", list, integers, n);
    }

    /** インスタンスフィールドの名前を、スーパークラスのものから順に並べる */
    static String fields(Class<?> c) {
        java.util.List<String> names = new java.util.ArrayList<>();
        for (Class<?> k = c; k != Object.class; k = k.getSuperclass()) {
            java.util.List<String> own = new java.util.ArrayList<>();
            for (java.lang.reflect.Field f : k.getDeclaredFields()) {
                if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                    own.add(f.getName());
                }
            }
            names.addAll(0, own);
        }
        return String.join(" ", names);
    }

    static void report(String label, Object collection, long integers, int n) {
        long total = GraphLayout.parseInstance(collection).totalSize() - integers;
        IO.println(label + ": " + total + "バイト（1件あたり約" + Math.round((double) total / n) + "バイト）");
    }
}
