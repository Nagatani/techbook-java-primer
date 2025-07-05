/**
 * 矩形を表すクラス（Shapeの具象実装）
 * 
 * 【学習ポイント】
 * 1. 抽象クラスの継承と抽象メソッドの実装
 * 2. 矩形特有の計算ロジック
 * 3. 正方形の特殊ケース処理
 * 4. 座標系での矩形操作
 * 5. 図形の衝突判定
 */
public class Rectangle extends Shape {
    
    private final double width;
    private final double height;
    
    /**
     * 矩形のコンストラクタ
     * @param name 図形の名前
     * @param color 図形の色
     * @param x 左上のX座標
     * @param y 左上のY座標
     * @param width 幅
     * @param height 高さ
     */
    public Rectangle(String name, String color, double x, double y, double width, double height) {
        super(name, color, x, y);
        if (width <= 0) {
            throw new IllegalArgumentException("幅は正の値でなければなりません: " + width);
        }
        if (height <= 0) {
            throw new IllegalArgumentException("高さは正の値でなければなりません: " + height);
        }
        this.width = width;
        this.height = height;
    }
    
    /**
     * 簡単なコンストラクタ（デフォルト名・色）
     */
    public Rectangle(double x, double y, double width, double height) {
        this("Rectangle", "Green", x, y, width, height);
    }
    
    /**
     * 正方形を作成するコンストラクタ
     */
    public Rectangle(String name, String color, double x, double y, double size) {
        this(name, color, x, y, size, size);
    }
    
    /**
     * 幅を取得
     */
    public double getWidth() {
        return width;
    }
    
    /**
     * 高さを取得
     */
    public double getHeight() {
        return height;
    }
    
    /**
     * 正方形かどうかを判定
     */
    public boolean isSquare() {
        return Double.compare(width, height) == 0;
    }
    
    /**
     * 対角線の長さを計算
     */
    public double getDiagonal() {
        return Math.sqrt(width * width + height * height);
    }
    
    // === 抽象メソッドの実装 ===
    
    /**
     * 矩形の面積を計算
     * 面積 = 幅 × 高さ
     */
    @Override
    public double getArea() {
        return width * height;
    }
    
    /**
     * 矩形の周囲の長さを計算
     * 周囲 = 2 × (幅 + 高さ)
     */
    @Override
    public double getPerimeter() {
        return 2 * (width + height);
    }
    
    /**
     * 矩形を描画
     */
    @Override
    public void draw() {
        System.out.println("矩形を描画中...");
        System.out.println("  左上: (" + getX() + ", " + getY() + ")");
        System.out.println("  幅: " + width + ", 高さ: " + height);
        System.out.println("  色: " + getColor());
        
        if (isSquare()) {
            System.out.println("  (正方形)");
        }
        
        // ASCII アートで矩形を表現
        drawAsciiRectangle();
    }
    
    /**
     * ASCII アートで矩形を描画
     */
    private void drawAsciiRectangle() {
        System.out.println("ASCII 矩形:");
        int w = Math.min(15, (int)width);
        int h = Math.min(10, (int)height);
        
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (i == 0 || i == h - 1 || j == 0 || j == w - 1) {
                    System.out.print("*");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
    
    /**
     * 矩形のコピーを作成
     */
    @Override
    public Shape copy() {
        return new Rectangle(getName(), getColor(), getX(), getY(), width, height);
    }
    
    /**
     * 矩形をスケーリング
     * @param factor スケーリング係数
     * @return スケーリングされた新しい矩形
     */
    @Override
    public Shape scale(double factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("スケーリング係数は正の値でなければなりません: " + factor);
        }
        return new Rectangle(getName(), getColor(), getX(), getY(), 
                           width * factor, height * factor);
    }
    
    /**
     * 指定された点が矩形内に含まれるかどうかを判定
     * @param pointX X座標
     * @param pointY Y座標
     * @return 矩形内に含まれる場合true
     */
    @Override
    public boolean contains(double pointX, double pointY) {
        return pointX >= getX() && pointX <= getX() + width &&
               pointY >= getY() && pointY <= getY() + height;
    }
    
    /**
     * 矩形のバウンディングボックスを取得
     * @return 矩形自身
     */
    @Override
    public Shape.Rectangle getBoundingBox() {
        return new Shape.Rectangle(getX(), getY(), width, height);
    }
    
    /**
     * 矩形の中心点を取得
     * @return 中心点
     */
    @Override
    public Point getCenter() {
        return new Point(getX() + width / 2, getY() + height / 2);
    }
    
    // === 矩形固有のメソッド ===
    
    /**
     * 右上の座標を取得
     */
    public Point getTopRight() {
        return new Point(getX() + width, getY());
    }
    
    /**
     * 左下の座標を取得
     */
    public Point getBottomLeft() {
        return new Point(getX(), getY() + height);
    }
    
    /**
     * 右下の座標を取得
     */
    public Point getBottomRight() {
        return new Point(getX() + width, getY() + height);
    }
    
    /**
     * 矩形の4つの頂点を取得
     */
    public Point[] getVertices() {
        return new Point[]{
            new Point(getX(), getY()),              // 左上
            getTopRight(),                          // 右上
            getBottomRight(),                       // 右下
            getBottomLeft()                         // 左下
        };
    }
    
    /**
     * 矩形の縦横比を取得
     */
    public double getAspectRatio() {
        return width / height;
    }
    
    /**
     * 他の矩形と交差するかどうかを判定
     * @param other 他の矩形
     * @return 交差する場合true
     */
    public boolean intersects(Rectangle other) {
        return !(this.getX() + this.width < other.getX() ||
                 other.getX() + other.width < this.getX() ||
                 this.getY() + this.height < other.getY() ||
                 other.getY() + other.height < this.getY());
    }
    
    /**
     * 他の矩形を完全に含むかどうかを判定
     * @param other 他の矩形
     * @return 完全に含む場合true
     */
    public boolean contains(Rectangle other) {
        return this.getX() <= other.getX() &&
               this.getY() <= other.getY() &&
               this.getX() + this.width >= other.getX() + other.width &&
               this.getY() + this.height >= other.getY() + other.height;
    }
    
    /**
     * 他の矩形との交差領域を取得
     * @param other 他の矩形
     * @return 交差領域（交差しない場合はnull）
     */
    public Rectangle getIntersection(Rectangle other) {
        if (!intersects(other)) {
            return null;
        }
        
        double x1 = Math.max(this.getX(), other.getX());
        double y1 = Math.max(this.getY(), other.getY());
        double x2 = Math.min(this.getX() + this.width, other.getX() + other.width);
        double y2 = Math.min(this.getY() + this.height, other.getY() + other.height);
        
        return new Rectangle("Intersection", "Gray", x1, y1, x2 - x1, y2 - y1);
    }
    
    /**
     * 他の矩形との結合領域を取得
     * @param other 他の矩形
     * @return 結合領域
     */
    public Rectangle getUnion(Rectangle other) {
        double x1 = Math.min(this.getX(), other.getX());
        double y1 = Math.min(this.getY(), other.getY());
        double x2 = Math.max(this.getX() + this.width, other.getX() + other.width);
        double y2 = Math.max(this.getY() + this.height, other.getY() + other.height);
        
        return new Rectangle("Union", "Yellow", x1, y1, x2 - x1, y2 - y1);
    }
    
    /**
     * 矩形を回転させる（90度単位）
     * @param times 90度回転の回数
     * @return 回転後の矩形
     */
    public Rectangle rotate90(int times) {
        times = times % 4;
        if (times < 0) times += 4;
        
        switch (times) {
            case 0:
                return new Rectangle(getName(), getColor(), getX(), getY(), width, height);
            case 1:
            case 3:
                return new Rectangle(getName(), getColor(), getX(), getY(), height, width);
            case 2:
                return new Rectangle(getName(), getColor(), getX(), getY(), width, height);
            default:
                throw new IllegalStateException("予期しない回転回数: " + times);
        }
    }
    
    /**
     * 矩形を拡張する（全方向に指定した値だけ拡張）
     * @param margin 拡張する値
     * @return 拡張された矩形
     */
    public Rectangle expand(double margin) {
        return new Rectangle(getName(), getColor(), 
                           getX() - margin, getY() - margin,
                           width + 2 * margin, height + 2 * margin);
    }
    
    /**
     * 矩形を収縮する（全方向に指定した値だけ収縮）
     * @param margin 収縮する値
     * @return 収縮された矩形
     */
    public Rectangle shrink(double margin) {
        double newWidth = width - 2 * margin;
        double newHeight = height - 2 * margin;
        
        if (newWidth <= 0 || newHeight <= 0) {
            throw new IllegalArgumentException("収縮しすぎて矩形が消失します");
        }
        
        return new Rectangle(getName(), getColor(), 
                           getX() + margin, getY() + margin,
                           newWidth, newHeight);
    }
    
    // === スーパークラスのメソッドのオーバーライド ===
    
    /**
     * 矩形固有の情報を表示
     */
    @Override
    protected void displaySpecificInfo() {
        System.out.println("幅: " + width);
        System.out.println("高さ: " + height);
        System.out.println("対角線: " + String.format("%.2f", getDiagonal()));
        System.out.println("縦横比: " + String.format("%.2f", getAspectRatio()));
        if (isSquare()) {
            System.out.println("形状: 正方形");
        } else {
            System.out.println("形状: 矩形");
        }
    }
    
    /**
     * 矩形の移動（オーバーライド）
     * @param newX 新しいX座標
     * @param newY 新しいY座標
     * @return 移動した矩形
     */
    @Override
    public Shape move(double newX, double newY) {
        return new Rectangle(getName(), getColor(), newX, newY, width, height);
    }
    
    /**
     * 描画前処理（矩形固有）
     */
    @Override
    protected void beforeDraw() {
        super.beforeDraw();
        System.out.println("矩形描画の準備: " + width + "×" + height + " の" + 
                         (isSquare() ? "正方形" : "矩形"));
    }
    
    /**
     * 描画後処理（矩形固有）
     */
    @Override
    protected void afterDraw() {
        super.afterDraw();
        System.out.println("矩形描画完了: 面積 " + String.format("%.2f", getArea()) + 
                         ", 周囲 " + String.format("%.2f", getPerimeter()));
    }
    
    // === 静的ユーティリティメソッド ===
    
    /**
     * 正方形を作成
     * @param x 左上のX座標
     * @param y 左上のY座標
     * @param size 一辺の長さ
     * @return 正方形
     */
    public static Rectangle createSquare(double x, double y, double size) {
        return new Rectangle("Square", "Blue", x, y, size, size);
    }
    
    /**
     * 単位正方形を作成
     * @param x 左上のX座標
     * @param y 左上のY座標
     * @return 一辺の長さが1の正方形
     */
    public static Rectangle createUnitSquare(double x, double y) {
        return createSquare(x, y, 1.0);
    }
    
    /**
     * 黄金比の矩形を作成
     * @param x 左上のX座標
     * @param y 左上のY座標
     * @param width 幅
     * @return 黄金比の矩形
     */
    public static Rectangle createGoldenRectangle(double x, double y, double width) {
        double goldenRatio = (1 + Math.sqrt(5)) / 2;
        return new Rectangle("GoldenRectangle", "Gold", x, y, width, width / goldenRatio);
    }
    
    /**
     * 複数の矩形を包含する最小の矩形を作成
     * @param rectangles 矩形の配列
     * @return 最小包含矩形
     */
    public static Rectangle getBoundingRectangle(Rectangle[] rectangles) {
        if (rectangles == null || rectangles.length == 0) {
            throw new IllegalArgumentException("矩形の配列が空です");
        }
        
        double minX = rectangles[0].getX();
        double minY = rectangles[0].getY();
        double maxX = rectangles[0].getX() + rectangles[0].width;
        double maxY = rectangles[0].getY() + rectangles[0].height;
        
        for (int i = 1; i < rectangles.length; i++) {
            Rectangle rect = rectangles[i];
            minX = Math.min(minX, rect.getX());
            minY = Math.min(minY, rect.getY());
            maxX = Math.max(maxX, rect.getX() + rect.width);
            maxY = Math.max(maxY, rect.getY() + rect.height);
        }
        
        return new Rectangle("BoundingRectangle", "Red", minX, minY, maxX - minX, maxY - minY);
    }
    
    /**
     * 与えられた点を通る最小の矩形を作成
     * @param points 点の配列
     * @return 最小包含矩形
     */
    public static Rectangle getMinimumBoundingRectangle(Point[] points) {
        if (points == null || points.length == 0) {
            throw new IllegalArgumentException("点の配列が空です");
        }
        
        double minX = points[0].getX();
        double minY = points[0].getY();
        double maxX = points[0].getX();
        double maxY = points[0].getY();
        
        for (int i = 1; i < points.length; i++) {
            Point point = points[i];
            minX = Math.min(minX, point.getX());
            minY = Math.min(minY, point.getY());
            maxX = Math.max(maxX, point.getX());
            maxY = Math.max(maxY, point.getY());
        }
        
        return new Rectangle("MinBoundingRectangle", "Orange", minX, minY, maxX - minX, maxY - minY);
    }
    
    @Override
    public String toString() {
        return String.format("Rectangle{name='%s', color='%s', position=(%.1f,%.1f), size=%.1f×%.1f, area=%.2f}",
                           getName(), getColor(), getX(), getY(), width, height, getArea());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        Rectangle rectangle = (Rectangle) obj;
        return Double.compare(rectangle.width, width) == 0 &&
               Double.compare(rectangle.height, height) == 0;
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), width, height);
    }
}