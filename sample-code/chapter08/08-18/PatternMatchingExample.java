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
        System.out.println(describe(p));  // 座標(10, 20)の点
    }
}
