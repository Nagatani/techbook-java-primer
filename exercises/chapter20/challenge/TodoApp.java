import java.time.LocalDate;
import javax.swing.SwingUtilities;

/**
 * ToDoリストを起動する。Model・View・Controllerを作ってつなぐ。
 */
public class TodoApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LocalDate today = LocalDate.now();
            TaskManager manager = createSampleData(today);
            CategoryListModel categoryModel = new CategoryListModel(manager);
            TaskTableModel tableModel = new TaskTableModel(manager);
            TodoFrame view = new TodoFrame(categoryModel, tableModel, manager, today);
            new TodoController(manager, categoryModel, tableModel, view, today);
            view.setVisible(true);
        });
    }

    /** 期限は今日を基準に決める（いつ起動しても同じ見え方になる） */
    static TaskManager createSampleData(LocalDate today) {
        TaskManager manager = new TaskManager();
        manager.addTask(new Task("線形代数のレポート", "学校", Priority.HIGH, today.minusDays(1)));
        manager.addTask(new Task("Javaの演習課題", "学校", Priority.HIGH, today));
        manager.addTask(new Task("図書館に本を返す", "学校", Priority.MEDIUM, today.plusDays(3)));
        manager.addTask(new Task("シフトの希望を出す", "アルバイト", Priority.MEDIUM, today.plusDays(1)));
        manager.addTask(new Task("制服をクリーニングに出す", "アルバイト", Priority.LOW, today.plusDays(5)));
        manager.addTask(new Task("電気代を払う", "生活", Priority.HIGH, today.plusDays(2)));
        manager.addTask(new Task("洗剤を買う", "生活", Priority.LOW, today.minusDays(2)));
        Task done = new Task("履修登録", "学校", Priority.HIGH, today.minusDays(7));
        manager.addTask(done);
        manager.setDone(done, true);
        return manager;
    }
}
