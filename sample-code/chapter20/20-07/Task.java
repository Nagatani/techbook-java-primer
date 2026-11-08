import java.time.LocalDateTime;

// サンプルコード20-5のTaskクラスと同じもの
class Task {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private boolean completed;
    
    public Task(String title, String description, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = false;
    }
    
    // getter/setter
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getDueDate() { return dueDate; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
