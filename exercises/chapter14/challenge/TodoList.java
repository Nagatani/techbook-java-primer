import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** タスクの一覧 */
public class TodoList implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Task> tasks = new ArrayList<>();
    private int nextId = 1;
    /** 保存していない変更があるか。保存しない情報なのでtransientにする */
    private transient boolean modified = false;

    public Task add(String title, String due) {
        Task task = new Task(nextId++, title, due, false);
        tasks.add(task);
        modified = true;
        return task;
    }

    /** ファイルから読んだタスクを、番号を変えずに加える */
    public void restore(Task task) {
        tasks.add(task);
        nextId = Math.max(nextId, task.id() + 1);
    }

    public boolean complete(int id) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).id() == id) {
                tasks.set(i, tasks.get(i).complete());
                modified = true;
                return true;
            }
        }
        return false;
    }

    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    public boolean isModified() {
        return modified;
    }

    public void markSaved() {
        modified = false;
    }
}
