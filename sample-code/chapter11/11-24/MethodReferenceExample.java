import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class MethodReferenceExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry");
        
        System.out.println("元のリスト: " + words);

        // 静的メソッド参照の例
        List<String> numbers = Arrays.asList("123", "456", "789");
        List<Integer> integers = numbers.stream()
            .map(Integer::parseInt)  // s -> Integer.parseInt(s)
            .collect(Collectors.toList());
        System.out.println("文字列から整数への変換: " + integers);

        // インスタンスメソッド参照（特定のインスタンス）
        System.out.println("\n各単語を出力:");
        words.forEach(System.out::println);  // s -> System.out.println(s)

        // インスタンスメソッド参照（不特定のインスタンス）
        System.out.println("\n大文字変換:");
        words.stream()
             .map(String::toUpperCase)  // s -> s.toUpperCase()
             .forEach(System.out::println);

        // コンストラクタ参照
        Supplier<List<String>> listFactory = ArrayList::new;  // () -> new ArrayList<>()
        List<String> newList = listFactory.get();
        newList.add("Hello");
        newList.add("World");
        System.out.println("\n新しいリスト: " + newList);
    }
}
