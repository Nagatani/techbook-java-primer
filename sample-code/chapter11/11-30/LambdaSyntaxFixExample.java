import java.util.Arrays;
import java.util.List;

public class LambdaSyntaxFixExample {
    public static void main(String[] args) {
        // ✅ 正しいラムダ式の構文
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        
        System.out.println("波括弧を使用した形式:");
        names.forEach(name -> {
            System.out.println(name);  // セミコロンを追加
        });

        // または単一式の場合は波括弧を省略
        System.out.println("\n波括弧を省略した形式:");
        names.forEach(name -> System.out.println(name));
    }
}
