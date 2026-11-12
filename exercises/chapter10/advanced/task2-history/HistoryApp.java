import java.util.ArrayList;
import java.util.List;

/**
 * 第10章 発展課題2: 元に戻す・やり直すの履歴（解答例）
 *
 * 同じHistoryクラスを、メモの文章（String）と文字の大きさ（Integer）に使います。
 *
 * 学習ポイント:
 * - ジェネリッククラスと型パラメーターT
 * - 上限境界ワイルドカード ? extends T（値を取り出して使う引数）
 * - 下限境界ワイルドカード ? super T（値を入れる先の引数）
 * - ジェネリックメソッドと、右辺から型がわかる場面でのvar
 */
public class HistoryApp {
    public static void main(String[] args) {
        IO.println("=== メモの文章 ===");
        var memo = new History<String>("買い物");
        memo.change("買い物: 牛乳");
        memo.change("買い物: 牛乳、卵");
        IO.println("今の文章: " + memo.current());
        memo.undo();
        IO.println("元に戻す: " + memo.current());
        memo.undo();
        IO.println("元に戻す: " + memo.current());
        memo.redo();
        IO.println("やり直す: " + memo.current());
        memo.change("買い物: 牛乳、パン");
        IO.println("書き換え: " + memo.current());
        IO.println("やり直せるか: " + memo.canRedo());

        IO.println();
        IO.println("=== 文字の大きさ ===");
        var fontSize = new History<Integer>(12);
        fontSize.changeAll(List.of(14, 16, 18));
        IO.println("今の大きさ: " + fontSize.current());
        int count = 0;
        while (fontSize.undo()) {
            count++;
        }
        IO.println(count + "回元に戻した: " + fontSize.current());
        IO.println("さらに元に戻せるか: " + fontSize.undo());
        fontSize.redo();
        fontSize.redo();

        IO.println();
        IO.println("=== 履歴の書き出し ===");
        List<Number> numbers = new ArrayList<>();
        fontSize.exportTo(numbers);  // History<Integer>からList<Number>へ
        IO.println("文字の大きさ: " + numbers);
        List<Object> all = new ArrayList<>();
        memo.exportTo(all);           // History<String>からList<Object>へ
        IO.println("メモ: " + all);

        IO.println();
        IO.println("今の状態: " + History.describe(memo, fontSize));
    }
}
