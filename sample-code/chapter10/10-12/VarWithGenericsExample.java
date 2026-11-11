import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VarWithGenericsExample {
    public static void main(String[] args) {
        // 長いジェネリック型の宣言を簡潔にする
        var userPreferences = new HashMap<String, List<String>>();
        var nestedData = new ArrayList<Map<String, Set<Integer>>>();

        // HashMap<String, List<String>>と推論されるので、getの戻り値はList<String>になる
        userPreferences.put("Alice", List.of("Java", "Python"));
        List<String> languages = userPreferences.get("Alice");
        IO.println("Alice: " + languages);

        // ジェネリックメソッドの戻り値（Pair<String, Integer>と推論される）
        var pair = createPair("Hello", 42);
        String first = pair.getFirst();
        int second = pair.getSecond();
        IO.println("First: " + first);
        IO.println("Second: " + second);
    }

    // 2つの型パラメータT、Uを持つジェネリックメソッド
    public static <T, U> Pair<T, U> createPair(T first, U second) {
        return new Pair<>(first, second);
    }

    // 2つの型パラメータを持つジェネリッククラス
    static class Pair<T, U> {
        private final T first;
        private final U second;

        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }

        public T getFirst() { return first; }
        public U getSecond() { return second; }
    }
}
