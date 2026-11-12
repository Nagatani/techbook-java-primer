import java.util.ArrayList;
import java.util.List;

/**
 * 上位の決まった件数だけを、大きい順に残すランキング
 *
 * 要素どうしを比べるため、型パラメーターTに「Comparable<T>を実装している」という境界を付ける。
 * これでTの値に対してcompareToを呼べる。
 *
 * @param <T> 順位を付ける要素の型
 */
public class Ranking<T extends Comparable<T>> {
    private final List<T> entries = new ArrayList<>();
    private final int capacity;

    public Ranking(int capacity) {
        this.capacity = capacity;
    }

    // 要素を加え、入った順位（1から）を返す。ランキングに入らなければ0
    public int add(T item) {
        int position = 0;
        // 同じ値なら先に入った方を上にするので、itemより小さい要素の手前に入れる
        while (position < entries.size() && entries.get(position).compareTo(item) >= 0) {
            position++;
        }
        if (position >= capacity) {
            return 0;
        }
        entries.add(position, item);
        if (entries.size() > capacity) {
            entries.remove(entries.size() - 1);  // 件数を超えたら最下位を外す
        }
        return position + 1;
    }

    // 順位（1から）の要素
    public T get(int rank) {
        return entries.get(rank - 1);
    }

    public int size() {
        return entries.size();
    }

    // 最下位の要素。空ならnull
    public T lowest() {
        if (entries.isEmpty()) {
            return null;
        }
        return entries.get(entries.size() - 1);
    }

    // ランキングの中身のコピー（呼び出し側が書き換えてもランキングは変わらない）
    public List<T> toList() {
        return new ArrayList<>(entries);
    }

    // リストの要素をすべて加えたランキングを作る（ジェネリックメソッド）
    // Tのリストに限らず、Tの子クラスのリストも受け取れるよう、? extends Tにする
    public static <T extends Comparable<T>> Ranking<T> of(int capacity, List<? extends T> items) {
        Ranking<T> ranking = new Ranking<>(capacity);
        for (T item : items) {
            ranking.add(item);
        }
        return ranking;
    }
}
