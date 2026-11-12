import java.util.List;
import java.util.Map;

/**
 * 第9章 発展課題2: タグ付きのToDoリスト（解答例）
 *
 * 学習ポイント:
 * - Listのadd(位置, 要素)・removeで並び順を変える
 * - Iteratorのremoveで、走査しながら安全に要素を取り除く
 * - TreeSet・TreeMapで、重複を除いて辞書順に並べる
 * - 値がListのMap（Map<String, List<String>>）を組み立てる
 */
public class TodoApp {
    public static void main(String[] args) {
        TodoList todo = new TodoList();
        todo.add("レポートを書く", "大学", "締切");
        todo.add("牛乳を買う", "買い物");
        todo.add("サークルの会計をまとめる", "サークル", "締切");
        todo.add("図書館に本を返す", "大学", "大学");  // 同じタグは1つになる
        todo.add("洗剤を買う", "買い物");

        IO.println("=== ToDoリスト ===");
        printTasks(todo.getTasks());

        IO.println();
        IO.println("=== 3番を先頭に移し、2番と4番を完了にする ===");
        todo.moveToTop(3);
        todo.complete(2);
        todo.complete(4);
        IO.println("9番を完了にする: " + (todo.complete(9) ? "できた" : "その番号はない"));
        printTasks(todo.getTasks());

        IO.println();
        IO.println("=== タグの一覧 ===");
        IO.println("  " + todo.allTags());

        IO.println();
        IO.println("=== タグごとのタスク ===");
        printByTag(todo.tasksByTag());

        IO.println();
        IO.println("=== 完了したタスクを片付ける ===");
        IO.println("  " + todo.removeCompleted() + "件を取り除きました");
        printTasks(todo.getTasks());
        IO.println("  タグの一覧: " + todo.allTags());
    }

    private static void printTasks(List<Task> tasks) {
        for (Task task : tasks) {
            IO.println("  " + task);
        }
    }

    private static void printByTag(Map<String, List<String>> byTag) {
        for (Map.Entry<String, List<String>> entry : byTag.entrySet()) {
            IO.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }
}
