import java.util.LinkedHashSet;
import java.util.Set;

/**
 * ToDoリストのタスク。番号・内容・タグ・完了したかどうかを持つ
 */
public class Task {
    private final int id;
    private final String title;
    private final Set<String> tags = new LinkedHashSet<>();  // 同じタグを二重に付けない
    private boolean done;

    public Task(int id, String title, String[] tags) {
        this.id = id;
        this.title = title;
        for (String tag : tags) {
            this.tags.add(tag);
        }
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Set<String> getTags() {
        return tags;
    }

    public boolean isDone() {
        return done;
    }

    public void markDone() {
        done = true;
    }

    @Override
    public String toString() {
        String mark = done ? "[x]" : "[ ]";
        return mark + " " + id + ". " + title + " " + tags;
    }
}
