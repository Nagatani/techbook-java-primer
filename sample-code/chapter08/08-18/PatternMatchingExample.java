public class PatternMatchingExample {
    public static String describe(Object obj) {
        return switch (obj) {
            case Point(int x, int y) -> 
                String.format("座標(%d, %d)の点", x, y);
            case String s -> 
                String.format("文字列: %s", s);
            case null -> 
                "null値";
            default -> 
                "未知のオブジェクト";
        };
    }
    
    public static void main(String[] args) {
        Point p = new Point(10, 20);
        IO.println(describe(p));        // 座標(10, 20)の点
        IO.println(describe("Hello"));  // 文字列: Hello
        IO.println(describe(null));     // null値
        IO.println(describe(42));       // 未知のオブジェクト
    }
}
