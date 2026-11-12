import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * ToDoリスト。絞り込み・並べ替え・更新・表示の方法は、引数の関数で受け取る
 */
public class TodoList {
    private final List<Task> tasks = new ArrayList<>();
    private int nextNumber = 1;
    // タスク番号を作る。ラムダ式の中からフィールドは書き換えられる（ローカル変数は書き換えられない）
    private final Supplier<String> idGenerator = () -> "T" + (nextNumber++);

    public Task add(String title, Priority priority, int dueDay) {
        Task task = new Task(idGenerator.get(), title, priority, dueDay, false);
        tasks.add(task);
        return task;
    }

    // 条件に合うタスクを、登録した順に返す
    public List<Task> select(Predicate<Task> condition) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            if (condition.test(task)) {
                result.add(task);
            }
        }
        return result;
    }

    // 条件に合うタスクを、指定した順に並べて返す
    public List<Task> select(Predicate<Task> condition, Comparator<Task> order) {
        List<Task> result = select(condition);
        result.sort(order);
        return result;
    }

    // 条件に合うタスクを変換して置き換え、置き換えた数を返す
    public int update(Predicate<Task> condition, UnaryOperator<Task> change) {
        int[] count = {0};  // ラムダ式の中で数えるため、配列の要素を使う
        tasks.replaceAll(task -> {
            if (condition.test(task)) {
                count[0]++;
                return change.apply(task);
            }
            return task;
        });
        return count[0];
    }

    // 条件に合うタスクを消し、消した数を返す
    public int remove(Predicate<Task> condition) {
        int before = tasks.size();
        tasks.removeIf(condition);
        return before - tasks.size();
    }

    // タスクの一覧を、formatterで文字列にして表示する
    public static void print(List<Task> list, Function<Task, String> formatter) {
        if (list.isEmpty()) {
            IO.println("  （なし）");
            return;
        }
        for (Task task : list) {
            IO.println(formatter.apply(task));
        }
    }
}
