import java.util.ArrayList;
import java.util.List;

/**
 * raw型と可変長引数によるヒープ汚染を観察する。
 * わざと未検査の警告が出る書き方をしている。
 *
 *   javac -Xlint:unchecked HeapPollution.java     警告を見る
 *   java HeapPollution                            例外が起きる場所を見る
 */
public class HeapPollution {
    // 1. raw型を通した代入
    static void rawType() {
        List<String> names = new ArrayList<>();
        names.add("Alice");
        List raw = names;               // raw型への代入は許される
        raw.add(42);                    // 未検査の警告だけで、コンパイルも実行も通る
        IO.println("追加できた。要素数: " + names.size());
        try {
            String s = names.get(1);    // 取り出すときに初めて失敗する
            IO.println(s);
        } catch (ClassCastException e) {
            IO.println("1. 取り出すときにClassCastException");
        }
    }

    // 2. 型パラメーターの可変長引数をそのまま返す（安全でない）
    static <T> T[] toArray(T... args) {
        return args;
    }

    static <T> T[] pickTwo(T a, T b) {
        return toArray(a, b);           // ここで作られる配列は Object[]
    }

    static void varargs() {
        try {
            String[] pair = pickTwo("x", "y");
            IO.println(pair.length);
        } catch (ClassCastException e) {
            IO.println("2. " + e.getMessage().replaceAll(" \\(.*", ""));
        }
    }

    public static void main(String[] args) {
        rawType();
        varargs();
    }
}
