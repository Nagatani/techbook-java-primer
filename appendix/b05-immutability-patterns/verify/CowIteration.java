import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * CopyOnWriteArrayListのイテレーターが、作った時点の配列（スナップショット）を
 * たどることを、ArrayListと比べて観察する。
 *   javac CowIteration.java && java CowIteration
 */
public class CowIteration {
    public static void main(String[] args) {
        List<String> plain = new ArrayList<>(List.of("a", "b", "c"));
        try {
            for (String s : plain) {
                if (s.equals("a")) {
                    plain.add("d");             // たどっている途中で変更する
                }
            }
        } catch (RuntimeException e) {
            IO.println("ArrayList: " + e.getClass().getSimpleName());
        }

        List<String> cow = new CopyOnWriteArrayList<>(List.of("a", "b", "c"));
        StringBuilder seen = new StringBuilder();
        for (String s : cow) {
            if (s.equals("a")) {
                cow.add("d");
            }
            seen.append(s);
        }
        IO.println("CopyOnWriteArrayList: たどった要素=" + seen + " 終了後のリスト=" + cow);

        Iterator<String> it = cow.iterator();
        it.next();
        try {
            it.remove();
        } catch (RuntimeException e) {
            IO.println("イテレーターのremove: " + e.getClass().getSimpleName());
        }
    }
}
