import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class MethodReferenceExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry");
        
        IO.println("元のリスト: " + words);

        // 静的メソッド参照の例
        List<String> numbers = Arrays.asList("123", "456", "789");
        List<Integer> integers = numbers.stream()
            .map(Integer::parseInt)  // s -> Integer.parseInt(s)
            .collect(Collectors.toList());
        IO.println("文字列から整数への変換: " + integers);

        // 静的メソッド参照（IO.printlnは静的メソッド）
        IO.println("\n各単語を出力:");
        words.forEach(IO::println);  // s -> IO.println(s)

        // インスタンスメソッド参照（特定のインスタンス）
        String prefix = "fruit: ";
        IO.println("\n接頭辞を付ける:");
        words.stream()
             .map(prefix::concat)  // s -> prefix.concat(s)
             .forEach(IO::println);

        // インスタンスメソッド参照（不特定のインスタンス）
        IO.println("\n大文字変換:");
        words.stream()
             .map(String::toUpperCase)  // s -> s.toUpperCase()
             .forEach(IO::println);

        // コンストラクタ参照
        Supplier<List<String>> listFactory = ArrayList::new;  // () -> new ArrayList<>()
        List<String> newList = listFactory.get();
        newList.add("Hello");
        newList.add("World");
        IO.println("\n新しいリスト: " + newList);
    }
}
