import java.util.Arrays;
import java.util.List;

public class ToArrayExample {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
        
        // toArrayの正しい使い方：型安全な配列を取得
        // String[]::newを指定することで、正しい型の配列が生成される
        String[] nameArray = names.stream()
            .filter(name -> name.length() > 3)  // 3文字より長い名前のみ
            .map(String::toUpperCase)           // 大文字に変換
            .toArray(String[]::new);            // String配列として取得
        
        System.out.println("結果配列: " + Arrays.toString(nameArray));
        // 出力: 結果配列: [ALICE, CHARLIE, DAVID]
        
        // サイズを指定した配列生成も可能
        String[] sizedArray = names.stream()
            .toArray(size -> new String[size]);
        System.out.println("サイズ指定配列: " + Arrays.toString(sizedArray));
    }
}
