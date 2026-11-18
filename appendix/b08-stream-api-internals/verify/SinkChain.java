import java.util.ArrayList;
import java.util.List;

/**
 * 終端操作を呼んだとき、各段の処理がSink（受け取り口）の連鎖になって
 * 1要素ずつ流れることを、mapのラムダ式の中から呼び出し履歴（スタック）を見て確かめる。
 *   javac SinkChain.java && java SinkChain
 */
public class SinkChain {
    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3));
        long count = numbers.stream()
                .filter(n -> n > 1)
                .map(n -> {
                    if (n == 2) {
                        printStack();
                    }
                    return n * 10;
                })
                .count();
        IO.println("count = " + count);
    }

    /** java.utilのフレームだけを、呼ばれた側（上）から順に表示する */
    static void printStack() {
        IO.println("mapのラムダ式を呼んでいるメソッド（上が内側）");
        StackWalker.getInstance().walk(frames -> {
            frames.filter(f -> f.getClassName().startsWith("java.util"))
                  .forEach(f -> IO.println("  " + f.getClassName() + "." + f.getMethodName()));
            return null;
        });
    }
}
