import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 「元に戻す」「やり直す」ができる値の履歴
 *
 * 文字列でも数値でも同じしくみで扱えるよう、値の型を型パラメーターTにする。
 * 元に戻すための値と、やり直すための値を、それぞれArrayListに積んでおく（末尾が最新）。
 *
 * @param <T> 履歴を取る値の型
 */
public class History<T> {
    private final List<T> undoValues = new ArrayList<>();
    private final List<T> redoValues = new ArrayList<>();
    private T current;

    public History(T initial) {
        this.current = initial;
    }

    public T current() {
        return current;
    }

    // 値を変える。新しく変えたら、やり直しの履歴は消える
    public void change(T value) {
        undoValues.add(current);
        redoValues.clear();
        current = value;
    }

    // 変更をまとめて適用する。Tの子クラスのリストも渡せるよう、? extends Tにする
    public void changeAll(List<? extends T> values) {
        for (T value : values) {
            change(value);
        }
    }

    public boolean canUndo() {
        return !undoValues.isEmpty();
    }

    public boolean canRedo() {
        return !redoValues.isEmpty();
    }

    // 1つ前の値に戻す。戻せなければfalse
    public boolean undo() {
        if (!canUndo()) {
            return false;
        }
        redoValues.add(current);
        current = undoValues.remove(undoValues.size() - 1);
        return true;
    }

    // 元に戻した変更をやり直す。やり直せなければfalse
    public boolean redo() {
        if (!canRedo()) {
            return false;
        }
        undoValues.add(current);
        current = redoValues.remove(redoValues.size() - 1);
        return true;
    }

    // これまでの値（古い順）と今の値を、渡されたコレクションに加える
    // Tを入れられるコレクションならよいので、? super Tにする（History<Integer>からList<Number>へも書き出せる）
    public void exportTo(Collection<? super T> dest) {
        dest.addAll(undoValues);
        dest.add(current);
    }

    // 2つの履歴の今の値を組にする（ジェネリックメソッド。型パラメーターは呼び出しごとに推論される）
    public static <A, B> String describe(History<A> first, History<B> second) {
        return "(" + first.current() + ", " + second.current() + ")";
    }
}
