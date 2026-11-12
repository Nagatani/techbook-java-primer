package todo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * タスクの一覧です。
 *
 * <p>タスクは登録した順に保持し、1から順に番号を割り当てます。
 * 削除したタスクの番号は再利用しません。</p>
 *
 * <pre>{@code
 * TodoList list = new TodoList();
 * list.add("買い物", Priority.LOW, LocalDate.of(2026, 6, 1));
 * list.add("レポート", Priority.HIGH, LocalDate.of(2026, 6, 3));
 * for (Task t : list.sortedByDue()) {
 *     IO.println(t.due() + " " + t.title());
 * }
 * }</pre>
 *
 * <p>このクラスはスレッドセーフではありません。</p>
 *
 * @see Task
 * @since 1.0
 */
public class TodoList {
    private final List<Task> tasks = new ArrayList<>();
    private int nextId = 1;

    /**
     * 空のToDoリストを作ります。
     */
    public TodoList() {
    }

    /**
     * タスクを登録します。登録したタスクは未完了です。
     *
     * @param title    タスクの名前（空でない文字列）
     * @param priority 優先度
     * @param due      期限の日
     * @return 番号を割り当てた、登録したタスク
     * @throws NullPointerException     いずれかの引数が{@code null}のとき
     * @throws IllegalArgumentException 名前が空白だけのとき
     */
    public Task add(String title, Priority priority, LocalDate due) {
        Task task = new Task(nextId, title, priority, due, false);
        tasks.add(task);
        nextId++;
        return task;
    }

    /**
     * 番号でタスクを探します。
     *
     * @param id タスクの番号
     * @return 見つかったタスク。なければ空の{@link Optional}
     */
    public Optional<Task> find(int id) {
        for (Task t : tasks) {
            if (t.id() == id) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    /**
     * タスクを完了にします。すでに完了しているタスクは、そのままです。
     *
     * @param id タスクの番号
     * @return 完了にしたタスク
     * @throws TaskNotFoundException その番号のタスクがないとき
     */
    public Task complete(int id) {
        int index = indexOf(id);
        Task done = tasks.get(index).completed();
        tasks.set(index, done);
        return done;
    }

    /**
     * タスクを削除します。
     *
     * @param id タスクの番号
     * @return 削除したタスク
     * @throws TaskNotFoundException その番号のタスクがないとき
     */
    public Task remove(int id) {
        return tasks.remove(indexOf(id));
    }

    /**
     * 登録した順に並んだ、すべてのタスクを返します。
     *
     * @return すべてのタスク（変更できないリスト）
     */
    public List<Task> all() {
        return List.copyOf(tasks);
    }

    /**
     * 未完了のタスクを、登録した順に返します。
     *
     * @return 未完了のタスク（変更できないリスト）
     */
    public List<Task> pending() {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (!t.done()) {
                result.add(t);
            }
        }
        return List.copyOf(result);
    }

    /**
     * 指定した日の時点で期限を過ぎている未完了のタスクを、登録した順に返します。
     *
     * @param today 基準にする日。ふつうは{@code LocalDate.now()}を渡す
     * @return 期限を過ぎたタスク（変更できないリスト）
     * @throws NullPointerException {@code today}が{@code null}のとき
     * @see Task#isOverdue(LocalDate)
     */
    public List<Task> overdue(LocalDate today) {
        Objects.requireNonNull(today, "today");
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t.isOverdue(today)) {
                result.add(t);
            }
        }
        return List.copyOf(result);
    }

    /**
     * 未完了のタスクを、期限の早い順に返します。
     * 期限が同じタスクは優先度の高い順に、それも同じなら番号の順に並べます。
     *
     * @return 並べ替えた未完了のタスク（変更できないリスト）
     */
    public List<Task> sortedByDue() {
        List<Task> result = new ArrayList<>(pending());
        result.sort(Comparator.comparing(Task::due)
            .thenComparing(Task::priority)
            .thenComparingInt(Task::id));
        return List.copyOf(result);
    }

    private int indexOf(int id) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).id() == id) {
                return i;
            }
        }
        throw new TaskNotFoundException(id);
    }
}
