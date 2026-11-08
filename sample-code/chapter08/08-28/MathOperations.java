public class MathOperations {
    // 静的内部クラス
    public static class Vector2D {
        private final double x, y;
        
        public Vector2D(double x, double y) {
            this.x = x;
            this.y = y;
        }
        
        public double magnitude() {
            return Math.sqrt(x * x + y * y);
        }
    }
    
    public static void main(String[] args) {
        // 外部クラスのインスタンスなしで生成可能
        Vector2D vector = new Vector2D(3, 4);
        System.out.println("大きさ: " + vector.magnitude()); // 5.0
    }
}
