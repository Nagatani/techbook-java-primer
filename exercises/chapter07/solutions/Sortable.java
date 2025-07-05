package chapter07.solutions;

/**
 * ソート可能なオブジェクトを表すインターフェース
 * 
 * このインターフェースは、オブジェクトの比較とソートを
 * 統一的に扱うためのものです。
 */
public interface Sortable {
    
    /**
     * 他のオブジェクトとの比較を行う
     * 
     * @param other 比較対象のオブジェクト
     * @return 負の値: このオブジェクトが小さい
     *         0: 等しい
     *         正の値: このオブジェクトが大きい
     */
    int compareTo(Sortable other);
    
    /**
     * ソート用のキーを取得する
     * 
     * @return ソート用のキー
     */
    Comparable<?> getSortKey();
    
    /**
     * ソートの方向を指定する
     * 
     * @return true: 昇順, false: 降順
     */
    default boolean isAscending() {
        return true;
    }
    
    /**
     * ソートの優先度を取得する
     * 数値が小さいほど優先度が高い
     * 
     * @return ソート優先度
     */
    default int getSortPriority() {
        return 0;
    }
}