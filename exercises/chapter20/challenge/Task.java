import java.time.LocalDate;

/**
 * ToDoリストの1件。完了したかどうかと優先度は後から変えられる。
 */
public class Task {
    private final String title;
    private final String category;
    private final LocalDate dueDate;
    private Priority priority;
    private boolean done;

    public Task(String title, String category, Priority priority, LocalDate dueDate) {
        if (title.isBlank() || category.isBlank()) {
            throw new IllegalArgumentException("題名と分類を入力してください");
        }
        this.title = title;
        this.category = category;
        this.priority = priority;
        this.dueDate = dueDate;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    void setPriority(Priority priority) {
        this.priority = priority;
    }

    public boolean isDone() {
        return done;
    }

    void setDone(boolean done) {
        this.done = done;
    }

    /** 未完了で、期限がtodayより前なら期限切れ */
    public boolean isOverdue(LocalDate today) {
        return !done && dueDate.isBefore(today);
    }
}
