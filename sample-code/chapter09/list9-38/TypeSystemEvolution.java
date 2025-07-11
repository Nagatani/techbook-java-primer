/**
 * リスト9-38
 * TypeSystemEvolutionクラス
 * 
 * 元ファイル: chapter09-records.md (1824行目)
 */

// 将来の型システム拡張
public class TypeSystemEvolution {
    
    // Union Typesとの統合（概念的）
    public sealed interface Result<T, E> permits Success, Error {}
    public record Success<T, E>(T value) implements Result<T, E> {}
    public record Error<T, E>(E error) implements Result<T, E> {}
    
    // Generic Records の拡張
    public record Pair<T, U>(T first, U second) {
        // より高度な制約
        public <R> Pair<R, U> mapFirst(Function<T, R> mapper) {
            return new Pair<>(mapper.apply(first), second);
        }
    }
    
    // Dependent Typesとの統合（非常に将来的）
    public record Matrix<N extends Number>(N rows, N cols, double[][] data) {
        public Matrix {
            if (data.length != rows.intValue() || 
                data[0].length != cols.intValue()) {
                throw new IllegalArgumentException("Dimension mismatch");
            }
        }
    }
}