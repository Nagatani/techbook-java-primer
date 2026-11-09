import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * 第7章 課題1: 図形クラスの継承と面積計算 - 解答例
 *
 * 抽象クラスShapeの実装（Circle・Rectangleの共通の親クラス）
 *
 * 学習ポイント:
 * - 抽象クラスの定義と使用方法
 * - 抽象メソッドによる実装の強制
 * - 共通処理（名前・色・位置の管理、描画の手順）と個別処理（面積・描画内容）の分離
 * - テンプレートメソッド（draw()の前後処理をフックメソッドで差し替える）
 */
public abstract class Shape {
    private final String name;
    private final String color;
    private final double x;
    private final double y;

    /**
     * @param name 図形の名前
     * @param color 図形の色
     * @param x 基準点のX座標
     * @param y 基準点のY座標
     */
    protected Shape(String name, String color, double x, double y) {
        this.name = Objects.requireNonNull(name, "name");
        this.color = Objects.requireNonNull(color, "color");
        this.x = x;
        this.y = y;
    }

    // === サブクラスが実装する抽象メソッド ===

    /** 面積 */
    public abstract double getArea();

    /** 周囲の長さ */
    public abstract double getPerimeter();

    /** 図形を文字で描画する */
    public abstract void draw();

    /** 同じ内容の新しい図形を作る */
    public abstract Shape copy();

    /** factor倍に拡大・縮小した新しい図形を作る */
    public abstract Shape scale(double factor);

    /** 基準点を(newX, newY)に移動した新しい図形を作る */
    public abstract Shape move(double newX, double newY);

    /** 点(pointX, pointY)が図形に含まれるか */
    public abstract boolean contains(double pointX, double pointY);

    /** 図形を囲む矩形 */
    public abstract BoundingBox getBoundingBox();

    /** 図形の中心 */
    public abstract Point getCenter();

    // === 共通処理 ===

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    /**
     * 図形の情報を表示する（固有の情報はdisplaySpecificInfo()で表示）
     */
    public void displayInfo() {
        System.out.println("図形: " + name + "（" + getShapeType(this) + "）");
        System.out.println("色: " + color);
        System.out.printf("位置: (%.1f, %.1f)%n", x, y);
        System.out.printf("面積: %.2f%n", getArea());
        displaySpecificInfo();
    }

    /** 図形固有の情報を表示する（必要なサブクラスがオーバーライドする） */
    protected void displaySpecificInfo() {
    }

    /** 描画の前処理（フックメソッド） */
    protected void beforeDraw() {
        System.out.println("--- " + name + " の描画開始 ---");
    }

    /** 描画の後処理（フックメソッド） */
    protected void afterDraw() {
        System.out.println("--- " + name + " の描画終了 ---");
    }

    /**
     * 描画する（テンプレートメソッド）
     * 手順は親クラスで固定し、前後処理と描画内容をサブクラスが差し替える
     */
    public final void render() {
        beforeDraw();
        draw();
        afterDraw();
    }

    /**
     * 面積を比較する
     * @return 自分のほうが大きければ正、小さければ負、等しければ0
     */
    public int compareAreaWith(Shape other) {
        return Double.compare(getArea(), other.getArea());
    }

    /** 面積が指定した値より大きいか */
    public boolean isLargerThan(double area) {
        return getArea() > area;
    }

    /** 名前・色・位置の1行の説明 */
    public String getInfo() {
        return String.format("%s（色: %s、位置: (%.1f, %.1f)）", name, color, x, y);
    }

    /** 種類・名前・色・面積・周囲の長さを含む説明 */
    public String getDetailedDescription() {
        return String.format("%s「%s」 色: %s、面積: %.2f、周囲の長さ: %.2f",
                getShapeType(this), name, color, getArea(), getPerimeter());
    }

    @Override
    public String toString() {
        return String.format("%s{name='%s', color='%s', position=(%.1f,%.1f)}",
                getShapeType(this), name, color, x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Shape other = (Shape) obj;
        return Double.compare(other.x, x) == 0
                && Double.compare(other.y, y) == 0
                && name.equals(other.name)
                && color.equals(other.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, x, y);
    }

    // === 静的ユーティリティメソッド ===

    /** 面積の小さい順に並べた新しい配列を返す */
    public static Shape[] sortByArea(Shape[] shapes) {
        Shape[] sorted = shapes.clone();
        Arrays.sort(sorted, Comparator.comparingDouble(Shape::getArea));
        return sorted;
    }

    /** 面積が最大の図形（空の配列ならnull） */
    public static Shape findLargestByArea(Shape[] shapes) {
        if (shapes == null || shapes.length == 0) {
            return null;
        }
        return Arrays.stream(shapes).max(Comparator.comparingDouble(Shape::getArea)).orElse(null);
    }

    /** 面積の合計（nullや空の配列なら0） */
    public static double calculateTotalArea(Shape[] shapes) {
        if (shapes == null) {
            return 0;
        }
        return Arrays.stream(shapes).mapToDouble(Shape::getArea).sum();
    }

    /** 図形の種類（クラス名） */
    public static String getShapeType(Shape shape) {
        return shape.getClass().getSimpleName();
    }

    // === 図形で使う値のクラス ===

    /**
     * 点
     */
    public static class Point {
        private final double x;
        private final double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        /** 別の点までの距離 */
        public double distanceTo(Point other) {
            return Math.hypot(x - other.x, y - other.y);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Point other)) return false;
            return Double.compare(x, other.x) == 0 && Double.compare(y, other.y) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return String.format("(%.1f, %.1f)", x, y);
        }
    }

    /**
     * 図形を囲む矩形（左上の点と幅・高さ）
     * Rectangleクラス（図形）と区別するため、名前をBoundingBoxにしている
     */
    public static class BoundingBox {
        private final double x;
        private final double y;
        private final double width;
        private final double height;

        public BoundingBox(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }

        @Override
        public String toString() {
            return String.format("BoundingBox[(%.1f, %.1f), %.1f x %.1f]", x, y, width, height);
        }
    }
}
