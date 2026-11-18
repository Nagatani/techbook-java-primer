import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @SafeVarargsを付けた可変長引数のメソッドは、呼び出し側にも宣言にも未検査の警告が出ない。
 * 付けてよいのは、可変長引数の配列を書き換えず、外へ渡さないメソッドだけである。
 *
 *   javac -Xlint:unchecked SafeVarargsUse.java && java SafeVarargsUse
 */
public class SafeVarargsUse {
    @SafeVarargs
    static <T> List<T> concat(List<? extends T>... lists) {
        List<T> result = new ArrayList<>();
        for (List<? extends T> list : lists) {   // 配列は読むだけ
            result.addAll(list);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        List<String> all = concat(List.of("a", "b"), List.of("c"));
        IO.println(all);

        // 標準ライブラリの例
        boolean annotated = Collections.class
                .getMethod("addAll", Collection.class, Object[].class)
                .isAnnotationPresent(SafeVarargs.class);
        IO.println("Collections.addAllに@SafeVarargs: " + annotated);
    }
}
