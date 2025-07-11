/**
 * リスト23-3
 * TodoItemクラス
 * 
 * 元ファイル: chapter23-build-and-deploy.md (225行目)
 */

// TodoItem.java
public class TodoItem {
    private String title;
    private boolean completed;
    
    public TodoItem(String title) {
        this.title = title;
        this.completed = false;
    }
    
    public String getTitle() {
        return title;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    @Override
    public String toString() {
        return (completed ? "[✓] " : "[ ] ") + title;
    }
}