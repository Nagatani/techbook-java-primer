import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VarReadabilityExample {
    public static void demonstrateGoodUsage() {
        // 良い使用例：右辺から型が明らかで、長い型名を繰り返さずに済む
        var students = List.of("Alice", "Bob", "Charlie");
        var scoreMap = new HashMap<String, Integer>();
        var builder = new StringBuilder();

        // 長いジェネリック型でとくに有効
        var complexMap = new HashMap<String, List<Map<String, Object>>>();
    }

    public static void demonstratePoorUsage() {
        // 悪い使用例：右辺を見ても型がわからない
        var data = getData();           // 戻り値の型が不明
        var result = process(data);     // メソッドの結果の型が不明
        var flag = checkCondition();    // boolean? String? 不明

        // 数値リテラルは、どの型になるか読み手が迷いやすい
        var count = 0;      // int? long?
        var rate = 0.5;     // float? double?

        // 型を明示したほうが意図が伝わる
        int itemCount = 0;
        double successRate = 0.5;
    }

    private static Object getData() { return null; }
    private static Object process(Object data) { return null; }
    private static boolean checkCondition() { return true; }
}
