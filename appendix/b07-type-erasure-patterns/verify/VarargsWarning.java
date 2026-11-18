import java.util.ArrayList;
import java.util.List;

/**
 * SafeVarargsUse.javaと同じメソッドから@SafeVarargsを外したもの。
 * 宣言と呼び出し側の両方に未検査の警告が出る（わざと警告を出している）。
 *
 *   javac -Xlint:unchecked VarargsWarning.java
 */
public class VarargsWarning {
    static <T> List<T> concat(List<? extends T>... lists) {
        List<T> result = new ArrayList<>();
        for (List<? extends T> list : lists) {
            result.addAll(list);
        }
        return result;
    }

    public static void main(String[] args) {
        IO.println(concat(List.of("a", "b"), List.of("c")));
    }
}
