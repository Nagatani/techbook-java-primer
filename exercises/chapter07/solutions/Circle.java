/**
 * 円を表すクラス（Shapeの具象実装）
 * 
 * 【学習ポイント】
 * 1. 抽象クラスの継承と抽象メソッドの実装
 * 2. 具象クラスでの具体的なロジック実装
 * 3. スーパークラスのメソッドの活用
 * 4. 数学的計算の実装
 * 5. 図形固有の処理の実装
 */
public class Circle extends Shape {
    
    private final double radius;
    
    /**
     * 円のコンストラクタ
     * @param name 図形の名前
     * @param color 図形の色
     * @param x 中心のX座標
     * @param y 中心のY座標
     * @param radius 半径
     */
    public Circle(String name, String color, double x, double y, double radius) {
        super(name, color, x, y);
        if (radius <= 0) {
            throw new IllegalArgumentException("半径は正の値でなければなりません: " + radius);
        }
        this.radius = radius;
    }
    
    /**
     * 簡単なコンストラクタ（デフォルト名・色）
     */
    public Circle(double x, double y, double radius) {
        this("Circle", "Blue", x, y, radius);
    }
    
    /**
     * 半径を取得
     */
    public double getRadius() {
        return radius;
    }
    
    /**
     * 直径を取得
     */
    public double getDiameter() {
        return radius * 2;
    }
    
    // === 抽象メソッドの実装 ===
    
    /**
     * 円の面積を計算
     * 面積 = π × r²
     */
    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }
    
    /**
     * 円の周囲の長さ（円周）を計算
     * 円周 = 2 × π × r
     */
    @Override
    public double getPerimeter() {
        return 2 * Math.PI * radius;
    }
    
    /**
     * 円を描画
     */
    @Override
    public void draw() {
        System.out.println("円を描画中...");
        System.out.println("  中心: (" + getX() + ", " + getY() + ")");
        System.out.println("  半径: " + radius);
        System.out.println("  色: " + getColor());
        
        // ASCII アートで円を表現（簡単な例）
        drawAsciiCircle();
    }
    
    /**
     * ASCII アートで円を描画
     */
    private void drawAsciiCircle() {
        System.out.println("ASCII 円:");
        int size = Math.min(10, (int)(radius * 2));
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double dx = j - size / 2.0;
                double dy = i - size / 2.0;
                double distance = Math.sqrt(dx * dx + dy * dy);
                
                if (Math.abs(distance - size / 2.0) < 0.5) {
                    System.out.print("* ");
                } else {
                    System.out.print("  ");
                }
            }
            System.out.println();
        }
    }
    
    /**
     * 円のコピーを作成
     */
    @Override
    public Shape copy() {
        return new Circle(getName(), getColor(), getX(), getY(), radius);
    }
    
    /**
     * 円をスケーリング
     * @param factor スケーリング係数
     * @return スケーリングされた新しい円
     */
    @Override
    public Shape scale(double factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("スケーリング係数は正の値でなければなりません: " + factor);
        }
        return new Circle(getName(), getColor(), getX(), getY(), radius * factor);
    }
    
    /**
     * 指定された点が円内に含まれるかどうかを判定
     * @param pointX X座標
     * @param pointY Y座標
     * @return 円内に含まれる場合true
     */
    @Override
    public boolean contains(double pointX, double pointY) {
        double dx = pointX - getX();
        double dy = pointY - getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= radius;
    }
    
    /**
     * 円のバウンディングボックスを取得
     * @return 円を囲む矩形
     */
    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle(
            getX() - radius,  // 左上のX座標
            getY() - radius,  // 左上のY座標
            radius * 2,       // 幅
            radius * 2        // 高さ
        );
    }
    
    /**
     * 円の中心点を取得
     * @return 中心点
     */
    @Override
    public Point getCenter() {
        return new Point(getX(), getY());
    }
    
    // === 円固有のメソッド ===
    
    /**
     * 円の内接正多角形の面積を計算
     * @param sides 多角形の辺数
     * @return 内接正多角形の面積
     */
    public double getInscribedPolygonArea(int sides) {
        if (sides < 3) {
            throw new IllegalArgumentException("多角形の辺数は3以上でなければなりません: " + sides);
        }
        
        double centralAngle = 2 * Math.PI / sides;
        double triangleArea = 0.5 * radius * radius * Math.sin(centralAngle);
        return triangleArea * sides;
    }
    
    /**
     * 円の外接正多角形の面積を計算
     * @param sides 多角形の辺数
     * @return 外接正多角形の面積
     */
    public double getCircumscribedPolygonArea(int sides) {
        if (sides < 3) {
            throw new IllegalArgumentException("多角形の辺数は3以上でなければなりません: " + sides);
        }
        
        double centralAngle = 2 * Math.PI / sides;
        double triangleArea = radius * radius * Math.tan(centralAngle / 2);
        return triangleArea * sides;
    }
    
    /**
     * 円弧の長さを計算
     * @param angle 中心角（ラジアン）
     * @return 円弧の長さ
     */
    public double getArcLength(double angle) {
        return radius * angle;
    }
    
    /**
     * 扇形の面積を計算
     * @param angle 中心角（ラジアン）
     * @return 扇形の面積
     */
    public double getSectorArea(double angle) {
        return 0.5 * radius * radius * angle;
    }
    
    /**
     * 弦の長さを計算
     * @param angle 中心角（ラジアン）
     * @return 弦の長さ
     */
    public double getChordLength(double angle) {
        return 2 * radius * Math.sin(angle / 2);
    }
    
    /**
     * 他の円との距離を計算
     * @param other 他の円
     * @return 中心間の距離
     */
    public double getDistanceFrom(Circle other) {
        double dx = this.getX() - other.getX();
        double dy = this.getY() - other.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * 他の円と交差するかどうかを判定
     * @param other 他の円
     * @return 交差する場合true
     */
    public boolean intersects(Circle other) {
        double distance = getDistanceFrom(other);
        return distance < (this.radius + other.radius);
    }
    
    /**
     * 他の円を完全に含むかどうかを判定
     * @param other 他の円
     * @return 完全に含む場合true
     */
    public boolean contains(Circle other) {
        double distance = getDistanceFrom(other);
        return distance + other.radius <= this.radius;
    }
    
    // === スーパークラスのメソッドのオーバーライド ===
    
    /**
     * 円固有の情報を表示
     */
    @Override
    protected void displaySpecificInfo() {
        System.out.println("半径: " + radius);
        System.out.println("直径: " + getDiameter());
        System.out.println("円周: " + String.format("%.2f", getPerimeter()));
    }
    
    /**
     * 円の移動（オーバーライド）
     * @param newX 新しいX座標
     * @param newY 新しいY座標
     * @return 移動した円
     */
    @Override
    public Shape move(double newX, double newY) {
        return new Circle(getName(), getColor(), newX, newY, radius);
    }
    
    /**
     * 描画前処理（円固有）
     */
    @Override
    protected void beforeDraw() {
        super.beforeDraw();
        System.out.println("円描画の準備: 半径 " + radius + " の円");
    }
    
    /**
     * 描画後処理（円固有）
     */
    @Override
    protected void afterDraw() {
        super.afterDraw();
        System.out.println("円描画完了: 面積 " + String.format("%.2f", getArea()));
    }
    
    // === 静的ユーティリティメソッド ===
    
    /**
     * 2つの円の交点を計算
     * @param circle1 円1
     * @param circle2 円2
     * @return 交点の配列（0個、1個、または2個）
     */
    public static Point[] getIntersectionPoints(Circle circle1, Circle circle2) {
        double d = circle1.getDistanceFrom(circle2);
        double r1 = circle1.radius;
        double r2 = circle2.radius;
        
        // 交点が存在しない場合
        if (d > r1 + r2 || d < Math.abs(r1 - r2) || d == 0) {
            return new Point[0];
        }
        
        // 1つの交点の場合（外接または内接）
        if (d == r1 + r2 || d == Math.abs(r1 - r2)) {
            double x = circle1.getX() + (circle2.getX() - circle1.getX()) * r1 / d;
            double y = circle1.getY() + (circle2.getY() - circle1.getY()) * r1 / d;
            return new Point[]{new Point(x, y)};
        }
        
        // 2つの交点の場合
        double a = (r1 * r1 - r2 * r2 + d * d) / (2 * d);
        double h = Math.sqrt(r1 * r1 - a * a);
        
        double px = circle1.getX() + a * (circle2.getX() - circle1.getX()) / d;
        double py = circle1.getY() + a * (circle2.getY() - circle1.getY()) / d;
        
        double x1 = px + h * (circle2.getY() - circle1.getY()) / d;
        double y1 = py - h * (circle2.getX() - circle1.getX()) / d;
        
        double x2 = px - h * (circle2.getY() - circle1.getY()) / d;
        double y2 = py + h * (circle2.getX() - circle1.getX()) / d;
        
        return new Point[]{new Point(x1, y1), new Point(x2, y2)};
    }
    
    /**
     * 与えられた点を通る最小の円を作成
     * @param points 点の配列
     * @return 最小包含円
     */
    public static Circle getMinimumEnclosingCircle(Point[] points) {
        if (points == null || points.length == 0) {
            throw new IllegalArgumentException("点の配列が空です");
        }
        
        // 簡単な実装：すべての点の重心を中心とし、最も遠い点までの距離を半径とする
        double centerX = 0, centerY = 0;
        for (Point point : points) {
            centerX += point.getX();
            centerY += point.getY();
        }
        centerX /= points.length;
        centerY /= points.length;
        
        double maxDistance = 0;
        for (Point point : points) {
            double distance = Math.sqrt(Math.pow(point.getX() - centerX, 2) + 
                                      Math.pow(point.getY() - centerY, 2));
            maxDistance = Math.max(maxDistance, distance);
        }
        
        return new Circle("MinEnclosingCircle", "Red", centerX, centerY, maxDistance);
    }
    
    /**
     * 単位円を作成
     * @param x 中心のX座標
     * @param y 中心のY座標
     * @return 半径1の円
     */
    public static Circle createUnitCircle(double x, double y) {
        return new Circle("UnitCircle", "Black", x, y, 1.0);
    }
    
    @Override
    public String toString() {
        return String.format("Circle{name='%s', color='%s', center=(%.1f,%.1f), radius=%.1f, area=%.2f}",
                           getName(), getColor(), getX(), getY(), radius, getArea());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        Circle circle = (Circle) obj;
        return Double.compare(circle.radius, radius) == 0;
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), radius);
    }
}