import java.util.Collection;

/**
 * 一覧の表示
 */
public class Report {
    // 番号を持つものなら何でも表示できる。番号の型は問わないのでIdentifiable<?>にする
    public static void printAll(String title, Collection<? extends Identifiable<?>> items) {
        IO.println(title + "（" + items.size() + "件）");
        for (Identifiable<?> item : items) {
            IO.println("  " + item);
        }
    }
}
