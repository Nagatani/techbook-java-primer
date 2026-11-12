package chapter10.solutions;

import java.util.List;

/**
 * ジェネリックメソッドを集めたクラス（課題2の解答例）
 */
public class GenericUtils {

    private GenericUtils() {
    }

    /**
     * 配列のi番目とj番目を入れ替える。範囲の外を指定したら例外を投げる
     */
    public static <T> void swap(T[] array, int i, int j) {
        if (i < 0 || i >= array.length || j < 0 || j >= array.length) {
            throw new IndexOutOfBoundsException("範囲外の位置です: " + i + ", " + j);
        }
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /**
     * 最初に見つかった位置を返す。見つからなければ-1を返す
     */
    public static <T> int find(T[] array, T target) {
        for (int i = 0; i < array.length; i++) {
            // nullどうしも等しいとみなす
            if (array[i] == null ? target == null : array[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }

    public static <T> int countNonNull(List<T> list) {
        int count = 0;
        for (T element : list) {
            if (element != null) {
                count++;
            }
        }
        // 第12章のStream APIを使うと、次のように書ける
        // return (int) list.stream().filter(Objects::nonNull).count();
        return count;
    }

    /**
     * 大きいほうを返す。TがComparable<T>なので、compareToで比べられる
     */
    public static <T extends Comparable<T>> T max(T a, T b) {
        if (a.compareTo(b) >= 0) {
            return a;
        }
        return b;
    }

    /**
     * 最小の要素を返す。空のリストならnullを返す
     */
    public static <T extends Comparable<T>> T findMin(List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        T min = list.get(0);
        for (T element : list) {
            if (element.compareTo(min) < 0) {
                min = element;
            }
        }
        // 第12章のStream APIを使うと、次のように書ける（空ならnullを返す）
        // return list.stream().min(Comparator.naturalOrder()).orElse(null);
        return min;
    }

    /**
     * 配列を「[要素1, 要素2, ...]」の形で表示する
     */
    public static <T> void printArray(T[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(array[i]);
        }
        sb.append("]");
        IO.println(sb.toString());
        // java.util.ArraysのtoStringを使うと、IO.println(Arrays.toString(array))の1行で同じ形になる
    }

    /**
     * 1つ目の組のfirstと、2つ目の組のsecondから新しい組を作る
     */
    public static <A, B, C, D> Pair<A, D> combinePairs(Pair<A, B> pair1, Pair<C, D> pair2) {
        return new Pair<>(pair1.getFirst(), pair2.getSecond());
    }
}
