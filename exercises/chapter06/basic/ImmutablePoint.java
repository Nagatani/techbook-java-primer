/**
 * 第6章 基本課題1: 不変の座標クラス設計
 * 
 * 課題: 不変の2D座標を表すクラスを作成し、不変性の概念を実装してください。
 * 
 * 要求仕様:
 * - finalフィールド: x（double）, y（double）
 * - コンストラクタでの初期化のみ
 * - getterメソッドのみ提供（setterなし）
 * - 座標間の距離計算メソッド
 * - 新しい座標を返すメソッド（移動、回転など）
 * 
 * 評価ポイント:
 * - finalフィールドの適切な使用
 * - 不変性の維持
 * - メソッドからの新しいインスタンス返却
 */
public final class ImmutablePoint {
    // TODO: finalフィールドを宣言してください
    // private final double x;
    // private final double y;
    
    // TODO: コンストラクタを実装してください
    // public ImmutablePoint(double x, double y) {
    //     this.x = x;
    //     this.y = y;
    // }
    
    // TODO: xのgetterメソッドを実装してください
    // public double getX() {
    //     return x;
    // }
    
    // TODO: yのgetterメソッドを実装してください
    // public double getY() {
    //     return y;
    // }
    
    // TODO: 座標間の距離を計算するメソッドを実装してください
    // public double distanceTo(ImmutablePoint other) {
    //     double dx = this.x - other.x;
    //     double dy = this.y - other.y;
    //     return Math.sqrt(dx * dx + dy * dy);
    // }
    
    // TODO: 座標を移動した新しい座標を返すメソッドを実装してください
    // public ImmutablePoint move(double deltaX, double deltaY) {
    //     return new ImmutablePoint(this.x + deltaX, this.y + deltaY);
    // }
    
    // TODO: 原点を中心に回転した新しい座標を返すメソッドを実装してください（度数指定）
    // public ImmutablePoint rotate(double degrees) {
    //     double radians = Math.toRadians(degrees);
    //     double cos = Math.cos(radians);
    //     double sin = Math.sin(radians);
    //     
    //     double newX = this.x * cos - this.y * sin;
    //     double newY = this.x * sin + this.y * cos;
    //     
    //     return new ImmutablePoint(newX, newY);
    // }
    
    // TODO: 原点からの距離を計算するメソッドを実装してください
    // public double distanceFromOrigin() {
    //     return Math.sqrt(x * x + y * y);
    // }
    
    // TODO: 文字列表現を返すメソッドを実装してください
    // @Override
    // public String toString() {
    //     return String.format("(%.1f, %.1f)", x, y);
    // }
    
    // TODO: equalsメソッドを実装してください（不変クラスでは重要）
    // @Override
    // public boolean equals(Object obj) {
    //     if (this == obj) return true;
    //     if (obj == null || getClass() != obj.getClass()) return false;
    //     ImmutablePoint that = (ImmutablePoint) obj;
    //     return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0;
    // }
    
    // TODO: hashCodeメソッドを実装してください
    // @Override
    // public int hashCode() {
    //     return Objects.hash(x, y);
    // }
}