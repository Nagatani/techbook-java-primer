import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * hashCodeが同じキーを集めたとき、HashMapのバケットが連結リストから赤黒木に変わる様子を見る。
 * 非公開のフィールドを読むので、次のオプションを付けて実行する。
 *
 *   javac Treeify.java
 *   java --add-opens java.base/java.util=ALL-UNNAMED Treeify
 */
public class Treeify {
    private static final Field TABLE = field(HashMap.class, "table");
    private static final Field NEXT = field(nodeClass(), "next");

    /** hashCodeがすべて同じで、Comparableを実装しないキー */
    static final class Key {
        static long equalsCalls;
        final int hash;
        final int id;

        Key(int hash, int id) {
            this.hash = hash;
            this.id = id;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object o) {
            equalsCalls++;
            return o instanceof Key k && k.hash == hash && k.id == id;
        }
    }

    /** hashCodeがすべて同じで、Comparableを実装するキー */
    static final class ComparableKey implements Comparable<ComparableKey> {
        static long equalsCalls;
        final int id;

        ComparableKey(int id) {
            this.id = id;
        }

        @Override
        public int hashCode() {
            return 42;
        }

        @Override
        public boolean equals(Object o) {
            equalsCalls++;
            return o instanceof ComparableKey k && k.id == id;
        }

        @Override
        public int compareTo(ComparableKey o) {
            return Integer.compare(id, o.id);
        }
    }

    public static void main(String[] args) throws Exception {
        growWithSmallTable();
        growWithLargeTable();
        removeFromTree();
        splitOnResize();
        lookupCost();
    }

    /** 既定の大きさ（16）から、同じhashCodeのキーを1つずつ足す */
    static void growWithSmallTable() throws Exception {
        IO.println("== 既定の大きさから同じhashCodeのキーを足す ==");
        HashMap<Key, Integer> map = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            map.put(new Key(42, i), i);
            IO.println(i + "個目: " + state(map));
        }
    }

    /** 表の長さが64以上なら、9個目で木になる */
    static void growWithLargeTable() throws Exception {
        IO.println("== 表の長さ64から同じhashCodeのキーを足す ==");
        HashMap<Key, Integer> map = new HashMap<>(64);
        for (int i = 1; i <= 10; i++) {
            map.put(new Key(42, i), i);
            if (i >= 7) {
                IO.println(i + "個目: " + state(map));
            }
        }
    }

    /** 木になったバケットから1つずつ削除する（木の形が毎回同じになるよう、Comparableなキーを使う） */
    static void removeFromTree() throws Exception {
        IO.println("== 木から削除する ==");
        removeInOrder("小さいキーから", false);
        removeInOrder("大きいキーから", true);
    }

    static void removeInOrder(String label, boolean fromLargest) throws Exception {
        HashMap<ComparableKey, Integer> map = new HashMap<>(64);
        for (int i = 0; i < 12; i++) {
            map.put(new ComparableKey(i), i);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 11; i++) {
            int id = fromLargest ? 11 - i : i;
            map.remove(new ComparableKey(id));
            sb.append(map.size()).append(":").append(isTree(map) ? "木" : "リスト").append(" ");
        }
        IO.println(label + "削除（残りの個数:形） " + sb.toString().trim());
    }

    static boolean isTree(HashMap<?, ?> map) throws Exception {
        for (Object head : (Object[]) TABLE.get(map)) {
            if (head != null) {
                return head.getClass().getSimpleName().equals("TreeNode");
            }
        }
        return false;
    }

    /** 表を広げたとき、木が2つに分かれ、6個以下になった側は連結リストに戻る */
    static void splitOnResize() throws Exception {
        IO.println("== 表を広げたときの分割 ==");
        splitCase(5, 6);
        splitCase(4, 7);
    }

    static void splitCase(int low, int high) throws Exception {
        HashMap<Key, Integer> map = new HashMap<>(64);
        // hashCodeが0と64のキーは、長さ64の表では同じバケット0に入り、長さ128の表では0と64に分かれる
        for (int i = 0; i < low; i++) {
            map.put(new Key(0, i), i);
        }
        for (int i = 0; i < high; i++) {
            map.put(new Key(64, i), i);
        }
        IO.println("hashCode 0が" + low + "個・64が" + high + "個: 表の長さ" + capacity(map)
                + " バケット0=" + bucket(map, 0));
        // 閾値（48）を超えるまで、別のバケットに入るキーを足して表を広げる
        for (int i = 1; map.size() <= 48; i++) {
            map.put(new Key(i, 1000 + i), 0);
        }
        IO.println("  表を広げたあと: 表の長さ" + capacity(map)
                + " バケット0=" + bucket(map, 0) + " バケット64=" + bucket(map, 64));
    }

    /** 1000個のキーが同じバケットにあるとき、getで呼ばれるequalsの回数 */
    static void lookupCost() {
        IO.println("== 同じバケットにある1000個のキーを探す ==");
        int n = 1000;
        HashMap<Key, Integer> plain = new HashMap<>();
        HashMap<ComparableKey, Integer> comparable = new HashMap<>();
        for (int i = 0; i < n; i++) {
            plain.put(new Key(42, i), i);
            comparable.put(new ComparableKey(i), i);
        }
        Key.equalsCalls = 0;
        ComparableKey.equalsCalls = 0;
        for (int i = 0; i < n; i++) {
            plain.get(new Key(42, i));
            comparable.get(new ComparableKey(i));
        }
        IO.println("Comparableでないキー: getあたりのequalsの回数 平均 "
                + "%.1f".formatted((double) Key.equalsCalls / n));
        IO.println("Comparableなキー: getあたりのequalsの回数 平均 "
                + "%.1f".formatted((double) ComparableKey.equalsCalls / n));
    }

    // ---- 内部を見るための道具

    static String state(HashMap<?, ?> map) throws Exception {
        Object[] table = (Object[]) TABLE.get(map);
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) {
                return "表の長さ" + table.length + " バケット" + i + "=" + bucket(map, i);
            }
        }
        return "表の長さ" + table.length + " (空)";
    }

    /** バケットの先頭のノードのクラス名と、つながっているノードの数 */
    static String bucket(HashMap<?, ?> map, int index) throws Exception {
        Object[] table = (Object[]) TABLE.get(map);
        Object head = table[index];
        if (head == null) {
            return "空";
        }
        int n = 0;
        for (Object node = head; node != null; node = NEXT.get(node)) {
            n++;
        }
        String name = head.getClass().getName().replace("java.util.", "");
        return name + "×" + n;
    }

    static int capacity(HashMap<?, ?> map) throws Exception {
        return ((Object[]) TABLE.get(map)).length;
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
