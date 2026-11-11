import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NullSafeProcessingExample {
    public static void main(String[] args) {
        // nullが混入する可能性があるデータ
        List<String> namesWithNull = Arrays.asList("Alice", null, "Bob", "", "Charlie");
        
        // nullや空文字列を取り除いてから変換する
        List<String> validNames = namesWithNull.stream()
            .filter(Objects::nonNull)           // nullを除外
            .filter(name -> !name.trim().isEmpty()) // 空文字列を除外
            .map(String::toUpperCase)           // 大文字に変換
            .collect(Collectors.toList());
        
        IO.println("有効な名前: " + validNames);
        // 出力: 有効な名前: [ALICE, BOB, CHARLIE]
    }
}
