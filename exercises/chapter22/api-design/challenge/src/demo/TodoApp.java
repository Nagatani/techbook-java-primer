package demo;

import java.time.LocalDate;
import java.util.List;

import todo.Priority;
import todo.Task;
import todo.TaskNotFoundException;
import todo.TodoList;

/**
 * todoライブラリを使う側のプログラム
 */
public class TodoApp {
    private TodoApp() {
    }

    /**
     * タスクを登録し、完了・削除・一覧の表示を実演する。
     *
     * @param args 使わない
     */
    public static void main(String[] args) {
        TodoList list = new TodoList();
        Task book = list.add("図書館に本を返す", Priority.MEDIUM, LocalDate.of(2026, 6, 2));
        list.add("情報数学のレポート", Priority.HIGH, LocalDate.of(2026, 6, 5));
        list.add("サークルの会計報告", Priority.LOW, LocalDate.of(2026, 6, 5));
        list.add("歯医者の予約", Priority.LOW, LocalDate.of(2026, 5, 30));
        list.add("プログラミング演習の課題", Priority.HIGH, LocalDate.of(2026, 6, 3));

        print("すべてのタスク", list.all());

        list.complete(book.id());
        Task removed = list.remove(4);
        IO.println("削除: " + removed.title());

        print("期限の早い順（未完了）", list.sortedByDue());

        LocalDate today = LocalDate.of(2026, 6, 4);
        print(today + "の時点で期限を過ぎたタスク", list.overdue(today));

        try {
            list.complete(4);
        } catch (TaskNotFoundException e) {
            IO.println("完了にできません: " + e.getMessage());
        }
        IO.println("番号9のタスク: " + list.find(9).map(Task::title).orElse("なし"));
    }

    private static void print(String heading, List<Task> tasks) {
        IO.println("=== " + heading + " ===");
        for (Task t : tasks) {
            IO.println("  %d [%s] %s（期限 %s）%s".formatted(
                t.id(), t.priority().label(), t.title(), t.due(), t.done() ? " 完了" : ""));
        }
    }
}
