package chapter09.basic;

import java.util.List;

/**
 * ジェネリックメソッドを学習するためのユーティリティクラス
 * 
 * 静的なジェネリックメソッドの実装例を提供します。
 */
public class GenericUtils {
    
    /**
     * 2つの値を入れ替える
     * TODO: 配列の指定された位置の要素を入れ替える
     * @param array 対象の配列
     * @param i 最初の位置
     * @param j 2番目の位置
     * @param <T> 配列要素の型
     */
    public static <T> void swap(T[] array, int i, int j) {
        // TODO: 実装してください
        // 境界チェックも忘れずに
    }
    
    /**
     * 配列から指定された要素を探す
     * TODO: 配列内で最初に見つかった要素のインデックスを返す
     * @param array 検索対象の配列
     * @param target 探す要素
     * @param <T> 配列要素の型
     * @return 見つかった場合はインデックス、見つからない場合は-1
     */
    public static <T> int find(T[] array, T target) {
        // TODO: 実装してください
        // ヒント: equals()メソッドを使用
        return -1;
    }
    
    /**
     * リストの要素数をカウントする
     * TODO: リスト内の非null要素の数を返す
     * @param list 対象のリスト
     * @param <T> リスト要素の型
     * @return 非null要素の数
     */
    public static <T> int countNonNull(List<T> list) {
        // TODO: 実装してください
        return 0;
    }
    
    /**
     * 2つの値の最大値を返す
     * TODO: Comparableを実装した型の2つの値を比較
     * @param a 最初の値
     * @param b 2番目の値
     * @param <T> Comparableを実装した型
     * @return より大きい値
     */
    public static <T extends Comparable<T>> T max(T a, T b) {
        // TODO: 実装してください
        // ヒント: compareTo()メソッドを使用
        return null;
    }
    
    /**
     * リストから最小値を見つける
     * TODO: リスト内の最小値を返す（リストが空の場合はnull）
     * @param list 対象のリスト
     * @param <T> Comparableを実装した型
     * @return 最小値、リストが空の場合はnull
     */
    public static <T extends Comparable<T>> T findMin(List<T> list) {
        // TODO: 実装してください
        return null;
    }
    
    /**
     * 配列を文字列として表示する
     * TODO: 配列の全要素を"[要素1, 要素2, ...]"形式で表示
     * @param array 表示する配列
     * @param <T> 配列要素の型
     */
    public static <T> void printArray(T[] array) {
        // TODO: 実装してください
        System.out.print("配列: ");
    }
    
    /**
     * 2つのPairオブジェクトを結合して新しいPairを作る
     * TODO: 最初のPairのfirstと2番目のPairのsecondで新しいPairを作成
     * @param pair1 最初のPair
     * @param pair2 2番目のPair
     * @param <A> 最初のPairのfirstの型
     * @param <B> 最初のPairのsecondの型（使用しない）
     * @param <C> 2番目のPairのfirstの型（使用しない）
     * @param <D> 2番目のPairのsecondの型
     * @return 新しいPair
     */
    public static <A, B, C, D> Pair<A, D> combinePairs(Pair<A, B> pair1, Pair<C, D> pair2) {
        // TODO: 実装してください
        return null;
    }
}