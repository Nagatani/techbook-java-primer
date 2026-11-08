import java.util.*;
import java.util.stream.Collectors;

public class VarWithGenericsExample {
    public static void main(String[] args) {
        // 複雑なジェネリック型の簡略化
        var userPreferences = new HashMap<String, List<String>>();
        var nestedData = new ArrayList<Map<String, Set<Integer>>>();
        
        // メソッドチェーンでの型推論
        var processedData = Arrays.asList("apple", "banana", "cherry")
            .stream()
            .filter(s -> s.length() > 5)
            .map(String::toUpperCase)
            .collect(Collectors.toList());
        
        // ワイルドカードとの組み合わせ
        List<? extends Number> numbers = Arrays.asList(1, 2.5, 3L);
        var sum = calculateSum(numbers);
        
        // ジェネリックメソッドの戻り値
        var pair = createPair("Hello", 42);
        System.out.println("First: " + pair.getFirst());
        System.out.println("Second: " + pair.getSecond());
    }
    
    public static double calculateSum(List<? extends Number> numbers) {
        return numbers.stream()
            .mapToDouble(Number::doubleValue)
            .sum();
    }
    
    public static <T, U> Pair<T, U> createPair(T first, U second) {
        return new Pair<>(first, second);
    }
    
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
