import java.util.*;
import java.util.function.*;

public class MethodReferenceExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry");
        
        IO.println("元のリスト: " + words);

        // 静的メソッド参照の例
        Function<String, Integer> parser = Integer::parseInt;  // s -> Integer.parseInt(s)
        int number = parser.apply("123");
        IO.println("文字列から整数への変換: " + number + " + 1 = " + (number + 1));

        // 静的メソッド参照（IO.printlnは静的メソッド）
        IO.println("\n各単語を出力:");
        words.forEach(IO::println);  // s -> IO.println(s)

        // インスタンスメソッド参照（特定のインスタンス）
        String prefix = "fruit: ";
        List<String> labeled = new ArrayList<>(words);
        labeled.replaceAll(prefix::concat);  // s -> prefix.concat(s)
        IO.println("\n接頭辞を付ける:");
        labeled.forEach(IO::println);

        // インスタンスメソッド参照（不特定のインスタンス）
        List<String> upper = new ArrayList<>(words);
        upper.replaceAll(String::toUpperCase);  // s -> s.toUpperCase()
        IO.println("\n大文字変換:");
        upper.forEach(IO::println);

        // コンストラクタ参照
        Supplier<List<String>> listFactory = ArrayList::new;  // () -> new ArrayList<>()
        List<String> newList = listFactory.get();
        newList.add("Hello");
        newList.add("World");
        IO.println("\n新しいリスト: " + newList);
    }
}
