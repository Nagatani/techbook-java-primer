public class PatternMatchingExample {
    public static String describe(Object obj) {
        return switch (obj) {
            case Point(int x, int y) when x == y -> 
                String.format("座標(%d, %d)の点（直線y = xの上）", x, y);
            case Point(int x, int y) -> 
                String.format("座標(%d, %d)の点", x, y);
            case String s when s.isEmpty() -> 
                "空の文字列";
            case String s -> 
                String.format("文字列: %s", s);
            case null -> 
                "null値";
            default -> 
                "未知のオブジェクト";
        };
    }
    
    public static void main(String[] args) {
        IO.println(describe(new Point(10, 20)));  // 座標(10, 20)の点
        IO.println(describe(new Point(5, 5)));    // 座標(5, 5)の点（直線y = xの上）
        IO.println(describe("Hello"));            // 文字列: Hello
        IO.println(describe(""));                 // 空の文字列
        IO.println(describe(null));               // null値
        IO.println(describe(42));                 // 未知のオブジェクト
    }
}
