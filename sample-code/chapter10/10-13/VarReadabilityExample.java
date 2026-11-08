import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class VarReadabilityExample {
    public static void demonstrateGoodUsage() {
        // 良い使用例：型が明らかで、冗長性を減らす
        var students = List.of("Alice", "Bob", "Charlie");
        var scoreMap = new HashMap<String, Integer>();
        var builder = new StringBuilder();
        
        // 複雑なジェネリック型でとくに有効
        var complexMap = new HashMap<String, List<Map<String, Object>>>();
        
        // Stream APIとの組み合わせ
        var upperCaseNames = students.stream()
            .map(String::toUpperCase)
            .collect(Collectors.toList());
    }
    
    public static void demonstratePoorUsage() {
        // 悪い使用例：型が不明確
        var data = getData();           // 戻り値の型が不明
        var result = process(data);     // メソッドの結果の型が不明
        var flag = checkCondition();    // boolean? String? 不明
        
        // 数値型の場合は明示的な型の方が良い場合も
        var count = 0;      // int? long? 
        var rate = 0.5;     // float? double?
        
        // より明確な書き方
        int itemCount = 0;
        double successRate = 0.5;
    }
    
    private static Object getData() { return null; }
    private static Object process(Object data) { return null; }
    private static boolean checkCondition() { return true; }
}
