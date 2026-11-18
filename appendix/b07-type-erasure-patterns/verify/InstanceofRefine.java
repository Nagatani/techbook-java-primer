import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 静的な型がCollection<String>なら、実行時にListかどうかを調べるだけで
 * List<String>であることまで保証できる。Java 16からは、このようなinstanceofを書ける。
 * Objectの変数に対するinstanceof List<String>はコンパイルエラーのまま
 * （compile-errors/InstanceofUnsafe.java）。
 *
 *   javac InstanceofRefine.java && java InstanceofRefine
 *   javac --release 15 InstanceofRefine.java      Java 15まではコンパイルエラー
 */
public class InstanceofRefine {
    static String describe(Collection<String> c) {
        if (c instanceof List<String>) {
            return "List<String>: 先頭は" + ((List<String>) c).get(0);
        }
        return "Listではない";
    }

    public static void main(String[] args) {
        IO.println(describe(new ArrayList<>(List.of("a", "b"))));
    }
}
