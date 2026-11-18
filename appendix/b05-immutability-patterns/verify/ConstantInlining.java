import java.util.List;

/**
 * static finalのフィールドのうち、定数変数（プリミティブ型かStringで、
 * 定数式で初期化したもの）だけがコンパイル時に値へ置き換わることを観察する。
 *   javac ConstantInlining.java && javap -c -p ConstantInlining
 */
public class ConstantInlining {
    static final int MAX = 100;                    // 定数変数
    static final String NAME = "limit";            // 定数変数
    static final Integer BOXED = 100;              // 定数変数ではない（Integer型）
    static final int COMPUTED = Integer.parseInt("100"); // 定数変数ではない（定数式でない）
    static final List<String> NAMES = List.of("a"); // 定数変数ではない

    static String readAll() {
        return NAME + MAX + BOXED + COMPUTED + NAMES;
    }

    public static void main(String[] args) {
        IO.println(readAll());
    }
}
