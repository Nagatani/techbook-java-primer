/**
 * リスト7-16
 * コードスニペット
 * 
 * 元ファイル: chapter07-abstract-classes-and-interfaces.md (595行目)
 */

// 比較可能オブジェクトのトレイト
interface ComparableTrait<T> extends Comparable<T> {
    
    default boolean isLessThan(T other) {
        return compareTo(other) < 0;
    }
    
    default boolean isGreaterThan(T other) {
        return compareTo(other) > 0;
    }
    
    default boolean isEqualTo(T other) {
        return compareTo(other) == 0;
    }
    
    default boolean isBetween(T lower, T upper) {
        return isGreaterThan(lower) && isLessThan(upper);
    }
    
    default T max(T other) {
        return isGreaterThan(other) ? (T) this : other;
    }
    
    default T min(T other) {
        return isLessThan(other) ? (T) this : other;
    }
}

// 使用例
class Temperature implements ComparableTrait<Temperature> {
    private final double celsius;
    
    public Temperature(double celsius) {
        this.celsius = celsius;
    }
    
    @Override
    public int compareTo(Temperature other) {
        return Double.compare(this.celsius, other.celsius);
    }
    
    public void checkRange() {
        Temperature freezing = new Temperature(0);
        Temperature boiling = new Temperature(100);
        
        if (this.isBetween(freezing, boiling)) {
            System.out.println("水は液体状態です");
        }
    }
}