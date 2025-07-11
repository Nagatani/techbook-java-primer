/**
 * リスト9-18
 * コードスニペット
 * 
 * 元ファイル: chapter09-records.md (646行目)
 */

// ソースコード
public record Point(int x, int y) {}

// コンパイラが生成する実際のコード（概念的表現）
public final class Point extends Record {
    private final int x;
    private final int y;
    
    // カノニカルコンストラクタ
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    // アクセサメソッド
    public int x() { return x; }
    public int y() { return y; }
    
    // equals メソッド
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Point other &&
               x == other.x && y == other.y;
    }
    
    // hashCode メソッド
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    
    // toString メソッド
    @Override
    public String toString() {
        return "Point[x=" + x + ", y=" + y + "]";
    }
    
    // Record特有のメソッド
    @Override
    public final Object[] componentArray() {
        return new Object[] { x, y };
    }
}