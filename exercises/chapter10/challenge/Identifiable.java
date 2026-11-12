/**
 * 番号（ID）を持つもの。番号の型は型パラメーターKで決める
 *
 * @param <K> 番号の型（会員ならString、備品ならIntegerなど）
 */
public interface Identifiable<K> {
    K getId();
}
