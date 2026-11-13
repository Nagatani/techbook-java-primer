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
        
        IO.println("結果配列: " + Arrays.toString(nameArray));
        // 出力: 結果配列: [ALICE, CHARLIE, DAVID]
        
        // String[]::newは、配列の大きさを受け取って配列を作るラムダ式と同じ意味
        String[] sizedArray = names.stream()
            .toArray(size -> new String[size]);
        IO.println("サイズ指定配列: " + Arrays.toString(sizedArray));
    }
}
