import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LambdaVsMethodReference {
    public static void main(String[] args) {
        // ラムダ式 vs メソッド参照の比較
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        
        System.out.println("元のリスト: " + names);
        
        // ラムダ式：やや冗長
        System.out.println("\nラムダ式を使用:");
        names.stream()
             .map(name -> name.toUpperCase())
             .forEach(name -> System.out.println(name));

        // メソッド参照：簡潔で意図が明確
        System.out.println("\nメソッド参照を使用:");
        names.stream()
             .map(String::toUpperCase)
             .forEach(System.out::println);

        // コンストラクタ参照の活用
        List<Person> people = names.stream()
            .map(Person::new)  // name -> new Person(name) と同じ
            .collect(Collectors.toList());
        
        System.out.println("\nPersonオブジェクトのリスト:");
        people.forEach(System.out::println);

        // ラムダ: () -> new ArrayList<>()
        // メソッド参照: ArrayList::new
        Supplier<List<String>> listFactory = ArrayList::new;
        List<String> newList = listFactory.get();
        newList.add("Hello");
        newList.add("Method Reference");
        System.out.println("\n新しいリスト: " + newList);
    }
}
