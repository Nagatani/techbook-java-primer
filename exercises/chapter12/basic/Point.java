/**
 * 第12章 レコード - 基本演習
 * 基本的なRecordクラス
 */
// TODO: Pointをrecordとして定義
// フィールド: double x, double y
public record Point(/* TODO: フィールドを定義 */) {
    
    // TODO: コンパクトコンストラクタで入力検証を追加
    // ヒント: xとyは有限の値であることを確認
    
    /**
     * 原点からの距離を計算
     */
    public double distanceFromOrigin() {
        // TODO: Math.sqrt(x*x + y*y)を使用して実装
        return 0.0;
    }
    
    /**
     * 別の点までの距離を計算
     */
    public double distanceTo(Point other) {
        // TODO: 2点間の距離を計算
        return 0.0;
    }
    
    /**
     * 点を移動した新しいPointを返す
     */
    public Point translate(double dx, double dy) {
        // TODO: 新しいPointを返す
        return null;
    }
    
    /**
     * スタティックファクトリーメソッド: 原点を返す
     */
    public static Point origin() {
        // TODO: (0, 0)のPointを返す
        return null;
    }
}