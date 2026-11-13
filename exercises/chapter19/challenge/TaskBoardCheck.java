import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * TaskBoardの動作確認
 *
 * 画面の代わりに、通知を記録するだけのPropertyChangeListenerを登録して確かめます。
 */
public class TaskBoardCheck {
    private static int total = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        IO.println("=== TaskBoardの動作確認 ===");
        TaskBoard board = new TaskBoard();
        List<Object> notices = new ArrayList<>();
        board.addPropertyChangeListener(e -> notices.add(e.getNewValue()));

        check("追加できる", true, board.add("A"));
        board.add("B");
        board.add("C");
        check("前後の空白を除いて追加する", true, board.add("  D  "));
        check("同じ名前は追加しない", false, board.add("A"));
        check("空の名前は追加しない", false, board.add("   "));
        check("追加は未着手の列の末尾", List.of("A", "B", "C", "D"), board.getTasks(Column.TODO));
        check("追加のたびに通知する（失敗したときは通知しない）", 4, notices.size());

        board.move("B", Column.DOING, -1);
        check("別の列へ移す", Column.DOING, board.find("B"));
        check("移すと通知する。新しい値は列ごとの数",
                Map.of(Column.TODO, 3, Column.DOING, 1, Column.DONE, 0), notices.get(notices.size() - 1));

        board.move("A", Column.TODO, 3);
        check("同じ列で下へ動かす（Dの後ろへ）", List.of("C", "D", "A"), board.getTasks(Column.TODO));
        board.move("A", Column.TODO, 0);
        check("同じ列で上へ動かす（先頭へ）", List.of("A", "C", "D"), board.getTasks(Column.TODO));
        check("並べ替えでも通知する", 7, notices.size());

        check("ないタスクは移せない", false, board.move("Z", Column.DONE, 0));
        check("削除できる", true, board.remove("C"));
        check("削除したタスクはどの列にもない", null, board.find("C"));
        check("ないタスクは削除できない", false, board.remove("C"));
        IO.println();
        IO.println(total + "件中" + (total - failed) + "件OK");
    }

    private static void check(String label, Object expected, Object actual) {
        total++;
        if (Objects.equals(expected, actual)) {
            IO.println("OK  " + label);
        } else {
            failed++;
            IO.println("NG  " + label + "（期待: " + expected + "、実際: " + actual + "）");
        }
    }
}
