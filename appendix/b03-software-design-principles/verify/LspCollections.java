import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * リスコフの置換原則：List型として受け取ったものに、要素を足せるとは限らない。
 * Listの変更メソッドは「optional operation」で、対応しない実装は
 * UnsupportedOperationExceptionを投げる。
 * 実行: javac LspCollections.java && java LspCollections
 */
public class LspCollections {
    // 呼ぶ側は「List型なら足せる」と思っている
    static void addDefault(List<String> list) {
        list.add("default");
    }

    static void tryOp(String label, List<String> list, Consumer<List<String>> op) {
        try {
            op.accept(list);
            IO.println(label + ": できた " + list);
        } catch (UnsupportedOperationException e) {
            IO.println(label + ": UnsupportedOperationException");
        }
    }

    public static void main(String[] args) {
        List<String> base = new ArrayList<>(List.of("a", "b"));
        tryOp("ArrayList.add", new ArrayList<>(base), LspCollections::addDefault);
        tryOp("List.of(...).add", List.of("a", "b"), LspCollections::addDefault);
        tryOp("Collections.unmodifiableList(...).add",
                Collections.unmodifiableList(base), LspCollections::addDefault);
        tryOp("Arrays.asList(...).add", Arrays.asList("a", "b"), LspCollections::addDefault);
        // Arrays.asListは大きさが変えられないだけで、setはできる
        tryOp("Arrays.asList(...).set", Arrays.asList("a", "b"), l -> l.set(0, "z"));
    }
}
