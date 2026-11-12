import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 第11章 チャレンジ課題: ToDoリスト（解答例）
 *
 * 絞り込みの条件をPredicateの組み合わせ（and・or・negate）で、並べ方をComparatorで、
 * 表示の形をFunctionの合成（andThen）で表し、TodoListのメソッドに渡します。
 * 日付は「1日」「12日」のような日の番号（整数）で扱い、今日は5日とします。
 */
public class TodoApp {
    public static void main(String[] args) {
        int today = 5;
        TodoList todo = new TodoList();
        todo.add("レポートを書く", Priority.HIGH, 7);
        todo.add("図書館に本を返す", Priority.MEDIUM, 4);
        todo.add("サークルの会費を払う", Priority.LOW, 10);
        todo.add("数学の課題を解く", Priority.HIGH, 5);
        todo.add("英語の単語テストの勉強", Priority.MEDIUM, 6);
        todo.add("自転車を修理に出す", Priority.LOW, 3);

        // 表示の形。Functionをつなげて組み立てる
        Function<Task, String> basic = task -> task.id() + " [" + task.priority().getLabel() + "] "
                + task.title() + "（" + task.dueDay() + "日まで）";
        Function<Task, String> withMark = basic.andThen(text -> "  " + text);
        Function<Task, String> withState = task -> withMark.apply(task) + (task.done() ? " 完了" : "");

        // 並べ方。優先度の高い順、同じ優先度なら期限の早い順
        Comparator<Task> byPriority = Comparator.comparing(Task::priority)
                .thenComparingInt(Task::dueDay);
        Comparator<Task> byDue = Comparator.comparingInt(Task::dueDay);

        IO.println("=== すべてのタスク（優先度順） ===");
        TodoList.print(todo.select(task -> true, byPriority), withMark);

        IO.println();
        IO.println("=== 今日（" + today + "日）までにやること（期限順） ===");
        Predicate<Task> dueToday = TaskFilters.notDone().and(TaskFilters.dueBy(today));
        TodoList.print(todo.select(dueToday, byDue), withMark);

        IO.println();
        IO.println("=== 完了にする ===");
        int completed = todo.update(TaskFilters.titleContains("数学")
                .or(TaskFilters.titleContains("図書館")), Task::complete);
        IO.println("  " + completed + "件を完了にしました");
        TodoList.print(todo.select(task -> true, byDue), withState);

        IO.println();
        IO.println("=== 期限切れで残っているタスク ===");
        Predicate<Task> overdue = TaskFilters.notDone().and(TaskFilters.dueBy(today - 1));
        TodoList.print(todo.select(overdue), withMark);

        IO.println();
        IO.println("=== 期限切れのタスクを3日延ばす ===");
        int postponed = todo.update(overdue, task -> task.postpone(3));
        IO.println("  " + postponed + "件の期限を延ばしました");

        IO.println();
        IO.println("=== 優先度が低くないもの（未完了、期限順） ===");
        Predicate<Task> notLow = TaskFilters.priorityIs(Priority.LOW).negate();
        TodoList.print(todo.select(notLow.and(TaskFilters.notDone()), byDue), withMark);

        IO.println();
        IO.println("=== 完了したタスクを片付ける ===");
        int removed = todo.remove(Task::done);
        IO.println("  " + removed + "件を削除しました");

        IO.println();
        IO.println("=== 残りのタスク（期限順） ===");
        TodoList.print(todo.select(task -> true, byDue), withMark);
    }
}
