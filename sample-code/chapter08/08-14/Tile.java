public record Tile(double width, double height) {
    // 正方形用のコンストラクター
    public Tile(double side) {
        this(side, side);
    }
    
    // 面積を計算するメソッド
    public double area() {
        return width * height;
    }
    
    // 周囲の長さを計算するメソッド
    public double perimeter() {
        return 2 * (width + height);
    }
}
