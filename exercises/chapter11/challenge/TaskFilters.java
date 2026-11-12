import java.util.function.Predicate;

/**
 * タスクの条件を作る高階関数。どのメソッドも、引数から条件（Predicate）を作って返す
 */
public class TaskFilters {
    // 完了していない
    public static Predicate<Task> notDone() {
        return task -> !task.done();
    }

    // 期限がday日以前
    public static Predicate<Task> dueBy(int day) {
        return task -> task.dueDay() <= day;
    }

    // 指定した優先度
    public static Predicate<Task> priorityIs(Priority priority) {
        return task -> task.priority() == priority;
    }

    // 題名にwordを含む
    public static Predicate<Task> titleContains(String word) {
        return task -> task.title().contains(word);
    }
}
