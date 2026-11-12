import java.util.ArrayList;
import java.util.List;

/**
 * ToDoリストのモデル（GUIに依存しない）。
 * 内容を変えるメソッドは、最後に登録されたリスナーへ知らせる。
 */
public class TaskManager {
    private final List<Task> tasks = new ArrayList<>();
    private final List<String> categories = new ArrayList<>();
    private final List<TaskManagerListener> listeners = new ArrayList<>();

    public void addListener(TaskManagerListener listener) {
        listeners.add(listener);
    }

    private void fireChanged() {
        for (TaskManagerListener l : listeners) {
            l.tasksChanged();
        }
    }

    /** 分類を追加する。すでにあれば何もしない */
    public void addCategory(String category) {
        if (!categories.contains(category)) {
            categories.add(category);
            fireChanged();
        }
    }

    /** タスクを追加する。分類がまだなければ分類も追加する */
    public void addTask(Task task) {
        if (!categories.contains(task.getCategory())) {
            categories.add(task.getCategory());
        }
        tasks.add(task);
        fireChanged();
    }

    public void removeTask(Task task) {
        if (tasks.remove(task)) {
            fireChanged();
        }
    }

    public void setDone(Task task, boolean done) {
        if (task.isDone() != done) {
            task.setDone(done);
            fireChanged();
        }
    }

    public void setPriority(Task task, Priority priority) {
        if (task.getPriority() != priority) {
            task.setPriority(priority);
            fireChanged();
        }
    }

    public List<String> getCategories() {
        return List.copyOf(categories);
    }

    /** 分類のタスクを登録順に返す。categoryがnullならすべて */
    public List<Task> getTasks(String category) {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (category == null || t.getCategory().equals(category)) {
                result.add(t);
            }
        }
        return result;
    }

    public int countAll(String category) {
        return getTasks(category).size();
    }

    public int countUndone(String category) {
        int count = 0;
        for (Task t : getTasks(category)) {
            if (!t.isDone()) {
                count++;
            }
        }
        return count;
    }
}
