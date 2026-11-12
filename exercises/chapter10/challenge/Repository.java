import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

/**
 * 番号でデータを登録・検索する入れ物
 *
 * 会員（番号はString）にも備品（番号はInteger）にも使えるよう、番号の型Kと、データの型Vを型パラメーターにする。
 * - K extends Comparable<K>: 番号の順に並べ、範囲で取り出すため、番号どうしを比べられる型に限る
 * - V extends Identifiable<K>: データから番号を取り出すため、K型の番号を持つ型に限る
 *
 * @param <K> 番号の型
 * @param <V> データの型
 */
public class Repository<K extends Comparable<K>, V extends Identifiable<K>> {
    private final TreeMap<K, V> items = new TreeMap<>();  // 番号の順に並ぶ

    // 登録する。同じ番号がすでにあればfalse
    public boolean add(V item) {
        if (items.containsKey(item.getId())) {
            return false;
        }
        items.put(item.getId(), item);
        return true;
    }

    // まとめて登録し、登録できた数を返す。Vの子クラスのリストも渡せるよう、? extends Vにする
    public int addAll(Collection<? extends V> newItems) {
        int count = 0;
        for (V item : newItems) {
            if (add(item)) {
                count++;
            }
        }
        return count;
    }

    // 番号で探す。なければnull
    public V find(K id) {
        return items.get(id);
    }

    public boolean remove(K id) {
        return items.remove(id) != null;
    }

    // 番号がfrom以上to以下のデータ（番号の順）
    public List<V> findRange(K from, K to) {
        return new ArrayList<>(items.subMap(from, true, to, true).values());
    }

    // すべてのデータ（番号の順）のコピー
    public List<V> all() {
        return new ArrayList<>(items.values());
    }

    public int size() {
        return items.size();
    }

    // すべてのデータを渡されたコレクションに加える。Vを入れられるコレクションならよいので、? super Vにする
    public void exportTo(Collection<? super V> dest) {
        dest.addAll(items.values());
    }
}
