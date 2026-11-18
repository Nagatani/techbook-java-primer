import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * java.util.HashMapの内部の表（table）と閾値（threshold）を、リフレクションで直接見る。
 * 非公開のフィールドを読むので、次のオプションを付けて実行する。
 *
 *   javac HashMapTable.java
 *   java --add-opens java.base/java.util=ALL-UNNAMED HashMapTable
 */
public class HashMapTable {
    private static final Field TABLE = field(HashMap.class, "table");
    private static final Field THRESHOLD = field(HashMap.class, "threshold");
    private static final Field NEXT = field(nodeClass(), "next");

    public static void main(String[] args) throws Exception {
        defaultCapacity();
        growth();
        presized();
        spreading();
        iterationOrder();
        sameHashCode();
        nullKey();
    }

    /** 既定のコンストラクターで作ったときの表の長さと閾値 */
    static void defaultCapacity() throws Exception {
        IO.println("== 既定のコンストラクター ==");
        HashMap<Integer, String> map = new HashMap<>();
        IO.println("作った直後: 表=" + describe(map));
        map.put(1, "a");
        IO.println("1件目を入れたあと: 表の長さ=" + capacity(map) + " 閾値=" + threshold(map));
    }

    /** 要素を足していき、表の長さが変わった時点を表示する */
    static void growth() throws Exception {
        IO.println("== 表が広がる時点 ==");
        HashMap<Integer, String> map = new HashMap<>();
        int last = 0;
        for (int i = 1; i <= 1000; i++) {
            map.put(i, "v");
            int cap = capacity(map);
            if (cap != last) {
                IO.println(i + "件目: 表の長さ " + last + " -> " + cap
                        + "（閾値 " + threshold(map) + "）");
                last = cap;
            }
        }
    }

    /** 初期容量を指定したときと、HashMap.newHashMapで作ったとき */
    static void presized() throws Exception {
        IO.println("== 初期容量の指定 ==");
        HashMap<Integer, String> byCapacity = new HashMap<>(100);
        HashMap<Integer, String> byMappings = HashMap.newHashMap(100);
        report("new HashMap<>(100)", byCapacity);
        report("HashMap.newHashMap(100)", byMappings);
    }

    static void report(String label, HashMap<Integer, String> map) throws Exception {
        int resizes = 0;
        Object before = null;
        for (int i = 1; i <= 100; i++) {
            map.put(i, "v");
            Object table = TABLE.get(map);
            if (i == 1) {
                IO.println(label + ": 1件目のあと 表の長さ=" + capacity(map)
                        + " 閾値=" + threshold(map));
            } else if (table != before) {
                resizes++;
            }
            before = table;
        }
        IO.println(label + ": 100件入れたあと 表の長さ=" + capacity(map)
                + " 作り直した回数=" + resizes);
    }

    /** hashCodeの上位16ビットだけが違うキーが、どのバケットに入るか */
    static void spreading() throws Exception {
        IO.println("== 上位ビットのかく乱 ==");
        HashMap<Integer, String> map = new HashMap<>();
        for (int i = 1; i <= 8; i++) {
            map.put(i << 16, "v");
        }
        for (int i = 1; i <= 8; i++) {
            int key = i << 16;
            IO.println("キー " + key + " (0x" + Integer.toHexString(key) + "): バケット "
                    + bucketOf(map, key) + "（hashCode & 15 は " + (key & 15) + "）");
        }
    }

    /** Integerのキーを入れた順と、取り出される順 */
    static void iterationOrder() throws Exception {
        IO.println("== 反復の順序 ==");
        HashMap<Integer, String> map = new HashMap<>();
        int[] keys = {100, 3, 17, 50};
        StringBuilder in = new StringBuilder();
        for (int k : keys) {
            map.put(k, "v");
            in.append(k).append(" ");
        }
        StringBuilder out = new StringBuilder();
        for (Integer k : map.keySet()) {
            out.append(k).append("(バケット").append(bucketOf(map, k)).append(") ");
        }
        IO.println("入れた順: " + in.toString().trim());
        IO.println("取り出した順: " + out.toString().trim());
    }

    /** hashCodeが等しい2つの文字列は、同じバケットにつながる */
    static void sameHashCode() throws Exception {
        IO.println("== hashCodeの衝突 ==");
        IO.println("\"Aa\".hashCode()=" + "Aa".hashCode() + " \"BB\".hashCode()=" + "BB".hashCode());
        HashMap<String, Integer> map = new HashMap<>();
        map.put("Aa", 1);
        map.put("BB", 2);
        int b = bucketOf(map, "Aa");
        IO.println("Aa: バケット " + b + " / BB: バケット " + bucketOf(map, "BB")
                + " / バケット" + b + "のノード数 " + chainLength(map, b));
        IO.println("get(\"BB\")=" + map.get("BB"));
    }

    /** nullのキー */
    static void nullKey() throws Exception {
        IO.println("== nullのキー ==");
        HashMap<String, Integer> map = new HashMap<>();
        map.put("x", 1);
        map.put(null, 0);
        IO.println("nullのキー: バケット " + bucketOf(map, null) + " get(null)=" + map.get(null));
    }

    // ---- 内部を見るための道具

    static int capacity(HashMap<?, ?> map) throws Exception {
        Object[] table = (Object[]) TABLE.get(map);
        return table == null ? 0 : table.length;
    }

    static int threshold(HashMap<?, ?> map) throws Exception {
        return THRESHOLD.getInt(map);
    }

    static String describe(HashMap<?, ?> map) throws Exception {
        return TABLE.get(map) == null ? "null（まだ作っていない）" : "長さ" + capacity(map);
    }

    /** 表を先頭から調べ、keyのノードがあるバケットの番号を返す */
    static int bucketOf(HashMap<?, ?> map, Object key) throws Exception {
        Object[] table = (Object[]) TABLE.get(map);
        for (int i = 0; i < table.length; i++) {
            for (Object node = table[i]; node != null; node = NEXT.get(node)) {
                if (java.util.Objects.equals(((Map.Entry<?, ?>) node).getKey(), key)) {
                    return i;
                }
            }
        }
        return -1;
    }

    static int chainLength(HashMap<?, ?> map, int index) throws Exception {
        Object[] table = (Object[]) TABLE.get(map);
        int n = 0;
        for (Object node = table[index]; node != null; node = NEXT.get(node)) {
            n++;
        }
        return n;
    }

    static Class<?> nodeClass() {
        try {
            return Class.forName("java.util.HashMap$Node");
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
