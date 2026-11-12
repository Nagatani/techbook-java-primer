import java.util.Objects;

/**
 * 不変の座標（課題1の解答例）
 */
public final class ImmutablePoint {
    // finalフィールドを宣言する
    private final double x;
    private final double y;
    
    // コンストラクタ
    public ImmutablePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    // xのgetterメソッド
    public double getX() {
        return x;
    }
    
    // yのgetterメソッド
    public double getY() {
        return y;
    }
    
    // 座標間の距離を計算するメソッド
    public double distanceTo(ImmutablePoint other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    // 座標を移動した新しい座標を返すメソッド
    public ImmutablePoint move(double deltaX, double deltaY) {
        return new ImmutablePoint(this.x + deltaX, this.y + deltaY);
    }
    
    // 原点を中心に回転した新しい座標を返すメソッド（度数指定）
    public ImmutablePoint rotate(double degrees) {
        double radians = Math.toRadians(degrees);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        
        double newX = this.x * cos - this.y * sin;
        double newY = this.x * sin + this.y * cos;
        
        return new ImmutablePoint(newX, newY);
    }
    
    // 原点からの距離を計算するメソッド
    public double distanceFromOrigin() {
        return Math.sqrt(x * x + y * y);
    }
    
    // 文字列表現を返すメソッド
    @Override
    public String toString() {
        return "(%.1f, %.1f)".formatted(x, y);
    }
    
    // equalsメソッド（不変クラスでは重要）
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ImmutablePoint that = (ImmutablePoint) obj;
        return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0;
    }
    
    // hashCodeメソッド
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}