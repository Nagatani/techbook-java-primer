import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * java.util.TreeMapの内部の木（root、left・right、color）をリフレクションで直接見て、
 * 赤黒木の性質が保たれていること、木の高さが抑えられていることを確かめる。
 * 非公開のフィールドを読むので、次のオプションを付けて実行する。
 *
 *   javac TreeMapShape.java
 *   java --add-opens java.base/java.util=ALL-UNNAMED TreeMapShape
 */
public class TreeMapShape {
    private static final Field ROOT = field(TreeMap.class, "root");
    private static final Class<?> ENTRY = entryClass();
    private static final Field LEFT = field(ENTRY, "left");
    private static final Field RIGHT = field(ENTRY, "right");
    private static final Field COLOR = field(ENTRY, "color");   // trueが黒
    private static final Field KEY = field(ENTRY, "key");

    public static void main(String[] args) throws Exception {
        small();
        heights();
        comparison();
        view();
    }

    /** 1〜10を昇順に入れたときの木の形 */
    static void small() throws Exception {
        IO.println("== 1〜10を昇順に入れたTreeMapの木 ==");
        TreeMap<Integer, String> map = new TreeMap<>();
        for (int i = 1; i <= 10; i++) {
            map.put(i, "v");
        }
        print(ROOT.get(map), "", "");
    }

    /** 昇順に入れても、木の高さは要素数の対数の2倍以内に収まる */
    static void heights() throws Exception {
        IO.println("== 昇順に入れたときの木の高さ ==");
        for (int n : new int[] {1_000, 1_000_000}) {
            TreeMap<Integer, Integer> map = new TreeMap<>();
            for (int i = 0; i < n; i++) {
                map.put(i, i);
            }
            Object root = ROOT.get(map);
            double log2 = Math.log(n + 1) / Math.log(2);
            IO.println("n=" + n + ": 高さ=" + height(root)
                    + "（log2(n+1)=" + "%.1f".formatted(log2)
                    + "、2×log2(n+1)=" + "%.1f".formatted(2 * log2) + "）"
                    + " 根は黒=" + COLOR.getBoolean(root)
                    + " 赤が2つ続く箇所=" + redRed(root)
                    + " 黒の高さがそろう=" + (blackHeight(root) > 0));
        }
    }

    /** TreeSetはequalsではなくcompareTo（またはComparator）で同じ要素かを決める */
    static void comparison() {
        IO.println("== 同じ要素かを決める方法 ==");
        List<BigDecimal> numbers = List.of(new BigDecimal("1.0"), new BigDecimal("1.00"));
        Set<BigDecimal> hash = new HashSet<>(numbers);
        Set<BigDecimal> tree = new TreeSet<>(numbers);
        IO.println("1.0と1.00: equals=" + numbers.get(0).equals(numbers.get(1))
                + " compareTo=" + numbers.get(0).compareTo(numbers.get(1))
                + " HashSetの要素数=" + hash.size() + " TreeSetの要素数=" + tree.size());

        Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        names.add("java");
        names.add("Java");
        names.add("JAVA");
        IO.println("CASE_INSENSITIVE_ORDERのTreeSet: " + names);
    }

    /** subMapは元のマップの見え方（ビュー）で、コピーではない */
    static void view() {
        IO.println("== subMapはビュー ==");
        TreeMap<Integer, String> map = new TreeMap<>();
        for (int i = 10; i <= 50; i += 10) {
            map.put(i, "v" + i);
        }
        NavigableMap<Integer, String> range = map.subMap(20, true, 40, true);
        IO.println("subMap(20〜40): " + range.keySet());
        map.put(25, "v25");
        range.remove(30);
        IO.println("元に25を足し、subMapから30を消したあと: 元=" + map.keySet()
                + " subMap=" + range.keySet());
        try {
            range.put(45, "v45");
        } catch (IllegalArgumentException e) {
            IO.println("subMapの範囲外のput(45): IllegalArgumentException");
        }
    }

    // ---- 木を調べる道具

    /**
     * 木を罫線で表示する。左の子（L:）を先に、右の子（R:）を後に並べる。
     * 縦線のない列は「·」で埋める（行頭の空白が詰められても形が崩れないように）
     */
    static void print(Object node, String prefix, String label) throws Exception {
        String color = COLOR.getBoolean(node) ? "黒" : "赤";
        IO.println(prefix + label + KEY.get(node) + "(" + color + ")");
        String childPrefix = prefix.isEmpty() ? "" : prefix.substring(0, prefix.length() - 2)
                + (prefix.endsWith("└─") ? "· " : "│ ");
        Object left = LEFT.get(node);
        Object right = RIGHT.get(node);
        if (left != null) {
            print(left, childPrefix + (right == null ? "└─" : "├─"), "L:");
        }
        if (right != null) {
            print(right, childPrefix + "└─", "R:");
        }
    }

    static int height(Object node) throws Exception {
        return node == null ? 0 : 1 + Math.max(height(LEFT.get(node)), height(RIGHT.get(node)));
    }

    /** 赤のノードの子に赤があれば、その数を返す */
    static int redRed(Object node) throws Exception {
        if (node == null) {
            return 0;
        }
        Object l = LEFT.get(node);
        Object r = RIGHT.get(node);
        int here = 0;
        if (!COLOR.getBoolean(node)) {
            if (l != null && !COLOR.getBoolean(l)) {
                here++;
            }
            if (r != null && !COLOR.getBoolean(r)) {
                here++;
            }
        }
        return here + redRed(l) + redRed(r);
    }

    /** どの経路でも黒の数が同じならその数を、違えば-1を返す */
    static int blackHeight(Object node) throws Exception {
        if (node == null) {
            return 1;
        }
        int l = blackHeight(LEFT.get(node));
        int r = blackHeight(RIGHT.get(node));
        if (l < 0 || r < 0 || l != r) {
            return -1;
        }
        return l + (COLOR.getBoolean(node) ? 1 : 0);
    }

    static Class<?> entryClass() {
        try {
            return Class.forName("java.util.TreeMap$Entry");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    static Field field(Class<?> c, String name) {
        try {
            Field f = c.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
