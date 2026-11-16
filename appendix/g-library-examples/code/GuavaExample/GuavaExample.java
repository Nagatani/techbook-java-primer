import com.google.common.collect.*;
import com.google.common.base.*;
import com.google.common.cache.*;
import java.time.Duration;
import java.util.List;

public class GuavaExample {
    public static void main(String[] args) throws Exception {
        // 1. 不変コレクション
        ImmutableList<String> immutableList = ImmutableList.of("A", "B", "C");
        ImmutableMap<String, Integer> immutableMap = ImmutableMap.of(
            "one", 1,
            "two", 2,
            "three", 3
        );
        
        IO.println("Immutable list: " + immutableList);
        IO.println("Immutable map: " + immutableMap);
        
        // 2. Multimap - 1つのキーに複数の値
        Multimap<String, String> multimap = ArrayListMultimap.create();
        multimap.put("fruits", "apple");
        multimap.put("fruits", "banana");
        multimap.put("fruits", "orange");
        multimap.put("vegetables", "carrot");
        
        IO.println("\nMultimap:");
        IO.println("Fruits: " + multimap.get("fruits"));
        
        // 3. BiMap - 双方向マップ
        BiMap<String, Integer> biMap = HashBiMap.create();
        biMap.put("one", 1);
        biMap.put("two", 2);
        
        IO.println("\nBiMap:");
        IO.println("Key for 2: " + biMap.inverse().get(2));
        
        // 4. Table - 2次元のマップ
        Table<String, String, Integer> table = HashBasedTable.create();
        table.put("Tokyo", "2023", 14000000);
        table.put("Tokyo", "2024", 14100000);
        table.put("Osaka", "2023", 2700000);
        
        IO.println("\nTable:");
        IO.println("Tokyo 2024: " + table.get("Tokyo", "2024"));
        IO.println("All Tokyo data: " + table.row("Tokyo"));
        
        // 5. 文字列処理
        String input = "  hello,  world,  java  ";
        List<String> parts = Splitter.on(',')
            .trimResults()
            .omitEmptyStrings()
            .splitToList(input);
        IO.println("\nSplit result: " + parts);
        
        String joined = Joiner.on(" | ")
            .skipNulls()
            .join("A", null, "B", "C");
        IO.println("Joined: " + joined);
        
        // 6. キャッシュ
        LoadingCache<String, String> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) {
                    // 実際のデータ取得処理
                    return "Value for " + key;
                }
            });
        
        IO.println("\nCache:");
        IO.println("First call: " + cache.get("key1"));
        IO.println("Second call (cached): " + cache.get("key1"));
    }
}
