/**
 * 2次元の点（課題1の解答例）
 *
 * recordを宣言すると、private finalのフィールド・コンストラクター・アクセサー（x()・y()）・
 * equals・hashCode・toStringが自動で作られる。
 */
public record Point(double x, double y) {

    // コンパクトコンストラクター。引数の並びを書かずに、検査だけを書く
    public Point {
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException("座標は有限の値にします: " + x + ", " + y);
        }
    }

    public double distanceFromOrigin() {
        return Math.sqrt(x * x + y * y);
    }

    public double distanceTo(Point other) {
        double dx = x - other.x;
        double dy = y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // レコードのフィールドは変えられないので、移動した点は新しく作って返す
    public Point translate(double dx, double dy) {
        return new Point(x + dx, y + dy);
    }

    public static Point origin() {
        return new Point(0, 0);
    }
}
