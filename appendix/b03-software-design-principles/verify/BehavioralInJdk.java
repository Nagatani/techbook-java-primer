import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;

/**
 * 標準ライブラリの中の振る舞いに関するパターン。
 * 実行: javac BehavioralInJdk.java && java BehavioralInJdk
 */
public class BehavioralInJdk {
    // テンプレートメソッド：getとsizeだけを書けば、残りのメソッドはAbstractListが用意する
    static class Squares extends AbstractList<Integer> {
        private final int n;

        Squares(int n) {
            this.n = n;
        }

        @Override
        public Integer get(int index) {
            return index * index;
        }

        @Override
        public int size() {
            return n;
        }
    }

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        // ストラテジー：並べ方（Comparator）を差し替える。sortのコードは同じ
        List<String> words = new ArrayList<>(List.of("pear", "fig", "banana", "kiwi"));
        words.sort(Comparator.naturalOrder());
        IO.println("辞書順: " + words);
        words.sort(Comparator.comparing(String::length).thenComparing(Comparator.reverseOrder()));
        IO.println("長さ順（同じ長さは逆の辞書順）: " + words);

        // イテレーター：中身の並び方を知らずに順にたどる
        IO.println("ArrayListのイテレーターのクラス: "
                + new ArrayList<>(words).iterator().getClass().getName());

        Squares squares = new Squares(5);
        IO.println("AbstractListから受け継いだtoString: " + squares);
        IO.println("AbstractListから受け継いだindexOf(9): " + squares.indexOf(9));
        IO.println("AbstractListから受け継いだcontains(16): " + squares.contains(16));

        // オブザーバー：java.util.Observableは非推奨
        Deprecated d = Observable.class.getAnnotation(Deprecated.class);
        IO.println("Observableの@Deprecated: since=" + d.since() + " forRemoval=" + d.forRemoval());
    }
}
