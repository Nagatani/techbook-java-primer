import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LinkedHashMapの2つの並び順（入れた順・使った順）と、removeEldestEntryによるLRUキャッシュを見る。
 *
 *   javac LinkedOrder.java
 *   java LinkedOrder
 */
public class LinkedOrder {

    /** いちばん長く使われていない組を、上限を超えたときに捨てるマップ */
    static final class LruCache<K, V> extends LinkedHashMap<K, V> {
        private static final long serialVersionUID = 1L;
        private final int limit;

        LruCache(int limit) {
            super(16, 0.75f, true);   // 3つめの引数trueで、並び順を使った順にする
            this.limit = limit;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > limit;
        }
    }

    public static void main(String[] args) {
        Map<String, Integer> insertion = new LinkedHashMap<>();
        insertion.put("A", 1);
        insertion.put("B", 2);
        insertion.put("C", 3);
        insertion.get("A");
        insertion.put("B", 20);            // すでにあるキーに入れ直す
        IO.println("入れた順（既定）: " + insertion.keySet());

        Map<String, Integer> access = new LinkedHashMap<>(16, 0.75f, true);
        access.put("A", 1);
        access.put("B", 2);
        access.put("C", 3);
        access.get("A");
        IO.println("使った順（get(\"A\")のあと）: " + access.keySet());

        LruCache<String, Integer> cache = new LruCache<>(3);
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);
        cache.get("A");
        cache.put("D", 4);
        IO.println("LRUキャッシュ（上限3、Aを使ってからDを入れる）: " + cache.keySet());
    }
}
