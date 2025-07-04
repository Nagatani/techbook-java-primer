/**
 * 第3章 演習問題2: Rectangleクラスの解答例
 * 
 * 【学習ポイント】
 * - メソッドオーバーロードの実装
 * - 正方形と長方形の関係性
 * - 計算メソッドの設計
 * - コンストラクタの使い分け
 * 
 * 【よくある間違いと対策】
 * 1. オーバーロードの条件を理解していない（引数の型・数・順序）
 * 2. 正方形のコンストラクタで重複したコードを書く
 * 3. 計算メソッドの精度を考慮しない
 * 4. 負の値の処理を忘れる
 */
public class Rectangle {
    // インスタンス変数
    private double width;
    private double height;
    
    // 【基本解答】正方形用コンストラクタ（一つの値）
    public Rectangle(double size) {
        // thisキーワードで長方形コンストラクタを呼び出し
        this(size, size);
    }
    
    // 【基本解答】長方形用コンストラクタ（幅と高さ）
    public Rectangle(double width, double height) {
        // 負の値の場合は絶対値を取る
        this.width = Math.abs(width);
        this.height = Math.abs(height);
    }
    
    // 【応用解答】デフォルトコンストラクタ（1x1の正方形）
    public Rectangle() {
        this(1.0);
    }
    
    // 【応用解答】コピーコンストラクタ
    public Rectangle(Rectangle other) {
        this(other.width, other.height);
    }
    
    // 【基本解答】setSizeメソッドのオーバーロード（正方形用）
    public void setSize(double size) {
        this.width = Math.abs(size);
        this.height = Math.abs(size);
    }
    
    // 【基本解答】setSizeメソッドのオーバーロード（長方形用）
    public void setSize(double width, double height) {
        this.width = Math.abs(width);
        this.height = Math.abs(height);
    }
    
    // 【応用解答】setSizeメソッドのオーバーロード（Rectangleオブジェクト）
    public void setSize(Rectangle other) {
        this.width = other.width;
        this.height = other.height;
    }
    
    // 【応用解答】setWidthメソッド
    public void setWidth(double width) {
        this.width = Math.abs(width);
    }
    
    // 【応用解答】setHeightメソッド
    public void setHeight(double height) {
        this.height = Math.abs(height);
    }
    
    // 面積計算メソッド
    public double getArea() {
        return width * height;
    }
    
    // 周囲計算メソッド
    public double getPerimeter() {
        return 2 * (width + height);
    }
    
    // 【発展解答】対角線の長さ計算
    public double getDiagonal() {
        return Math.sqrt(width * width + height * height);
    }
    
    // 【発展解答】正方形かどうかの判定
    public boolean isSquare() {
        return Math.abs(width - height) < 1e-10;  // 浮動小数点の誤差を考慮
    }
    
    // 【発展解答】縦長か横長かの判定
    public boolean isPortrait() {
        return height > width;
    }
    
    public boolean isLandscape() {
        return width > height;
    }
    
    // 【発展解答】拡大・縮小メソッド
    public void scale(double factor) {
        if (factor > 0) {
            width *= factor;
            height *= factor;
        }
    }
    
    // 【発展解答】回転メソッド（90度回転）
    public void rotate() {
        double temp = width;
        width = height;
        height = temp;
    }
    
    // 情報表示メソッド
    public void displayInfo() {
        System.out.println("幅: " + width + ", 高さ: " + height);
        System.out.println("面積: " + getArea() + ", 周囲: " + getPerimeter());
    }
    
    // 【発展解答】詳細情報表示メソッド
    public void displayDetailedInfo() {
        System.out.printf("長方形情報:%n");
        System.out.printf("  幅: %.2f%n", width);
        System.out.printf("  高さ: %.2f%n", height);
        System.out.printf("  面積: %.2f%n", getArea());
        System.out.printf("  周囲: %.2f%n", getPerimeter());
        System.out.printf("  対角線: %.2f%n", getDiagonal());
        System.out.printf("  形状: %s%n", isSquare() ? "正方形" : 
                         isPortrait() ? "縦長" : "横長");
    }
    
    // getter メソッド
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }
    
    // 【発展解答】アスペクト比の取得
    public double getAspectRatio() {
        return height != 0 ? width / height : Double.POSITIVE_INFINITY;
    }
    
    // 【発展解答】別の長方形との比較メソッド
    public boolean isSameSize(Rectangle other) {
        return Math.abs(this.width - other.width) < 1e-10 &&
               Math.abs(this.height - other.height) < 1e-10;
    }
    
    public boolean isSameArea(Rectangle other) {
        return Math.abs(this.getArea() - other.getArea()) < 1e-10;
    }
    
    public boolean isLargerThan(Rectangle other) {
        return this.getArea() > other.getArea();
    }
    
    // 【発展解答】toString()メソッドのオーバーライド
    @Override
    public String toString() {
        return String.format("Rectangle{width=%.2f, height=%.2f, area=%.2f}", 
                           width, height, getArea());
    }
    
    // 【発展解答】equals()メソッドのオーバーライド
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Rectangle rectangle = (Rectangle) obj;
        return Double.compare(rectangle.width, width) == 0 &&
               Double.compare(rectangle.height, height) == 0;
    }
    
    // 【発展解答】hashCode()メソッドのオーバーライド
    @Override
    public int hashCode() {
        return Double.hashCode(width) + Double.hashCode(height);
    }
    
    // 【発展解答】静的メソッド - 二つの長方形の合成面積
    public static double getTotalArea(Rectangle r1, Rectangle r2) {
        return r1.getArea() + r2.getArea();
    }
    
    // 【発展解答】静的メソッド - 最大の長方形を返す
    public static Rectangle getLarger(Rectangle r1, Rectangle r2) {
        return r1.getArea() >= r2.getArea() ? r1 : r2;
    }
}