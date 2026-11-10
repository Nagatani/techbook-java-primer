import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class VarBestPractices {
    
    // GOOD: 変数名で型が明確
    public static void goodPractices() {
        var studentList = new ArrayList<String>();
        var scoreMap = new HashMap<String, Integer>();
        var messageBuilder = new StringBuilder();
        
        // ○ 長いジェネリック型の簡略化
        var complexStructure = new HashMap<String, List<Map<String, Object>>>();
        
        // ○ ファクトリーメソッドでの使用
        var immutableList = List.of("a", "b", "c");
        var emptySet = Set.of();
        
        // ○ Stream API処理結果
        var evenNumbers = List.of(1, 2, 3, 4, 5)
            .stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList());
    }
    
    // POOR: 型が不明確
    public static void poorPractices() {
        // × 変数名だけでは型が不明
        var data = processData();
        var result = calculate();
        var value = getValue();
        
        // × プリミティブ型での誤用
        var x = 10;          // int? long?
        var y = 3.14;        // float? double?
        
        // × nullリテラル（コンパイルエラー）
        // var nullValue = null;
        
        // × 配列初期化子（コンパイルエラー）
        // var array = {1, 2, 3};
    }
    
    // GOOD: 型注釈を活用した明確化
    public static void typeAnnotationPattern() {
        // コンテキストに応じて型注釈で明確化
        List<String> stringList = new ArrayList<>();  // 明示的
        var numberList = new ArrayList<Integer>();     // 推論、型は明確
        
        // メソッド参照やラムダ式は、代入先の型がないと型を決められない
        // var upperCaseConverter = String::toUpperCase;  // コンパイルエラー
        Function<String, String> upperCaseConverter = String::toUpperCase;
        
        // var predicate = (String s) -> s.length() > 3;  // コンパイルエラー
        Predicate<String> predicate = s -> s.length() > 3;
    }
    
    private static Object processData() { return new Object(); }
    private static int calculate() { return 42; }
    private static String getValue() { return "value"; }
}
