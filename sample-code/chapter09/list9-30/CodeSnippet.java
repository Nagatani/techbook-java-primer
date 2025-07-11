/**
 * リスト9-30
 * コードスニペット
 * 
 * 元ファイル: chapter09-records.md (1400行目)
 */

// インライン化されやすいRecord
@jdk.internal.ValueBased  // 将来のValue Typeの候補
public record Point2D(double x, double y) {
    // 小さく、不変で、equalsとhashCodeが値ベース
    
    public double distanceFrom(Point2D other) {
        double dx = x - other.x;
        double dy = y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    public Point2D translate(double deltaX, double deltaY) {
        return new Point2D(x + deltaX, y + deltaY);
    }
}

// メモリレイアウトの最適化
public record OptimizedData(
    // プリミティブ型を前に配置（メモリ効率向上）
    long id,
    int count,
    boolean active,
    byte flags,
    // 参照型は後に配置
    String name,
    List<String> tags
) {}