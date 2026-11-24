import java.util.*;
import java.util.function.Function;

public class LambdaVsMethodReference {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        IO.println("元のリスト: " + names);
        
        // ラムダ式：やや冗長
        IO.println("\nラムダ式を使用:");
        List<String> upper1 = new ArrayList<>(names);
        upper1.replaceAll(name -> name.toUpperCase());
        upper1.forEach(name -> IO.println(name));

        // メソッド参照：簡潔で意図が明確
        IO.println("\nメソッド参照を使用:");
        List<String> upper2 = new ArrayList<>(names);
        upper2.replaceAll(String::toUpperCase);
        upper2.forEach(IO::println);

        // 引数を受け取るコンストラクターの参照
        // name -> new Person(name) と同じ
        Function<String, Person> toPerson = Person::new;
        List<Person> people = new ArrayList<>();
        for (String name : names) {
            people.add(toPerson.apply(name));
        }
        
        IO.println("\nPersonオブジェクトのリスト:");
        people.forEach(IO::println);
    }
}
