/**
 * リスト22-9
 * GuavaExampleクラス
 * 
 * 元ファイル: chapter22-documentation-and-libraries.md (867行目)
 */

import com.google.common.collect.*;
import com.google.common.base.*;
import com.google.common.cache.*;
import java.util.concurrent.TimeUnit;
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
        
        System.out.println("Immutable list: " + immutableList);
        System.out.println("Immutable map: " + immutableMap);
        
        // 2. Multimap - 1つのキーに複数の値
        Multimap<String, String> multimap = ArrayListMultimap.create();
        multimap.put("fruits", "apple");
        multimap.put("fruits", "banana");
        multimap.put("fruits", "orange");
        multimap.put("vegetables", "carrot");
        
        System.out.println("\nMultimap:");
        System.out.println("Fruits: " + multimap.get("fruits"));
        
        // 3. BiMap - 双方向マップ
        BiMap<String, Integer> biMap = HashBiMap.create();
        biMap.put("one", 1);
        biMap.put("two", 2);
        
        System.out.println("\nBiMap:");
        System.out.println("Key for 2: " + biMap.inverse().get(2));
        
        // 4. Table - 2次元のマップ
        Table<String, String, Integer> table = HashBasedTable.create();
        table.put("Tokyo", "2023", 14000000);
        table.put("Tokyo", "2024", 14100000);
        table.put("Osaka", "2023", 2700000);
        
        System.out.println("\nTable:");
        System.out.println("Tokyo 2024: " + table.get("Tokyo", "2024"));
        System.out.println("All Tokyo data: " + table.row("Tokyo"));
        
        // 5. 文字列処理
        String input = "  hello,  world,  java  ";
        List<String> parts = Splitter.on(',')
            .trimResults()
            .omitEmptyStrings()
            .splitToList(input);
        System.out.println("\nSplit result: " + parts);
        
        String joined = Joiner.on(" | ")
            .skipNulls()
            .join("A", null, "B", "C");
        System.out.println("Joined: " + joined);
        
        // 6. キャッシュ
        LoadingCache<String, String> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) {
                    // 実際のデータ取得処理
                    return "Value for " + key;
                }
            });
        
        System.out.println("\nCache:");
        System.out.println("First call: " + cache.get("key1"));
        System.out.println("Second call (cached): " + cache.get("key1"));
        
        // 7. Optional（Java 8より前から利用可能）
        Optional<String> optional = Optional.of("Hello");
        System.out.println("\nOptional:");
        System.out.println("Is present: " + optional.isPresent());
        System.out.println("Value: " + optional.or("Default"));
    }
}