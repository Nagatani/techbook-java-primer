/**
 * リスト9-22
 * コードスニペット
 * 
 * 元ファイル: chapter09-records.md (841行目)
 */

public record Range(int start, int end) {
    public Range {
        if (start > end) {
            throw new IllegalArgumentException("Start cannot be greater than end");
        }
    }
    
    // 範囲の長さ
    public int length() {
        return end - start;
    }
    
    // 値が範囲内かチェック
    public boolean contains(int value) {
        return value >= start && value <= end;
    }
    
    // 範囲の重複チェック
    public boolean overlaps(Range other) {
        return this.start <= other.end && other.start <= this.end;
    }
    
    // 範囲の結合
    public Range union(Range other) {
        if (!this.overlaps(other) && !this.isAdjacent(other)) {
            throw new IllegalArgumentException("Ranges must overlap or be adjacent");
        }
        return new Range(
            Math.min(this.start, other.start),
            Math.max(this.end, other.end)
        );
    }
    
    // 隣接チェック
    public boolean isAdjacent(Range other) {
        return this.end + 1 == other.start || other.end + 1 == this.start;
    }
    
    // Streamとの統合
    public IntStream stream() {
        return IntStream.rangeClosed(start, end);
    }
    
    // Iterableの実装
    public Iterable<Integer> asIterable() {
        return () -> stream().iterator();
    }
}