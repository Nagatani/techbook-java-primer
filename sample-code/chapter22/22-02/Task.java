/**
 * タスク管理システムのコアクラスである
 * <p>
 * このクラスは、ToDoリストアプリケーションのタスクを表現し、
 * タスクの作成、更新、完了状態の管理を行う
 * </p>
 * 
 * <h2>使用例</h2>
 * <pre>{@code
 * TaskManager manager = new TaskManager();
 * Task task = manager.createTask("買い物", Priority.HIGH);
 * task.setCompleted(true);
 * }</pre>
 * 
 * @author Taro Yamada
 * @version 2.0
 * @since 1.0
 * @see TaskManager
 * @see Priority
 */
public class Task {
    /**
     * タスクの優先度を表す列挙型
     */
    public enum Priority {
        /** 低優先度 */
        LOW,
        /** 中優先度 */
        MEDIUM,
        /** 高優先度 */
        HIGH
    }
    
    /** タスクの一意識別子 */
    private final long id;
    
    /** タスクのタイトル（必須、最大100文字） */
    private String title;
    
    /** タスクの詳細説明（オプション） */
    private String description;
    
    /** タスクの完了状態 */
    private boolean completed;
    
    /** タスクの優先度 */
    private Priority priority;
    
    /**
     * 指定されたタイトルとデフォルトの優先度で新しいタスクを作成する
     * 
     * @param title タスクのタイトル（null不可、空文字不可）
     * @throws IllegalArgumentException titleがnullまたは空文字の場合
     */
    public Task(String title) {
        this(title, Priority.MEDIUM);
    }
    
    /**
     * 指定されたタイトルと優先度で新しいタスクを作成する
     * 
     * @param title タスクのタイトル（null不可、空文字不可）
     * @param priority タスクの優先度（null不可）
     * @throws IllegalArgumentException titleがnullまたは空文字、
     *         またはpriorityがnullの場合
     */
    public Task(String title, Priority priority) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("タイトルは必須です");
        }
        if (priority == null) {
            throw new IllegalArgumentException("優先度は必須です");
        }
        
        this.id = System.currentTimeMillis();
        this.title = title;
        this.priority = priority;
        this.completed = false;
    }
    
    /**
     * タスクのタイトルを取得する
     * 
     * @return タスクのタイトル（nullではない）
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * タスクのタイトルを設定する
     * 
     * @param title 新しいタイトル（null不可、空文字不可）
     * @throws IllegalArgumentException titleがnullまたは空文字の場合
     */
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("タイトルは必須です");
        }
        this.title = title;
    }
    
    /**
     * タスクを完了状態にする
     * <p>
     * このメソッドは {@code setCompleted(true)} と同等である
     * </p>
     * 
     * @see #setCompleted(boolean)
     */
    public void complete() {
        this.completed = true;
    }
    
    /**
     * タスクの優先度を1段階上げる
     * <p>
     * 既にHIGHの場合は変更されません
     * </p>
     * 
     * @return 優先度が変更された場合はtrue、そうでない場合はfalse
     */
    public boolean increasePriority() {
        switch (priority) {
            case LOW:
                priority = Priority.MEDIUM;
                return true;
            case MEDIUM:
                priority = Priority.HIGH;
                return true;
            default:
                return false;
        }
    }
    
    /**
     * タスクを文字列表現に変換する
     * 
     * @return タスクの文字列表現
     * @deprecated {@link #toDetailedString()} を使用してください
     */
    @Deprecated(since = "2.0", forRemoval = true)
    @Override
    public String toString() {
        return String.format("Task[id=%d, title='%s']", id, title);
    }
    
    /**
     * タスクの詳細な文字列表現を返す
     * 
     * @return タスクの詳細情報を含む文字列
     * @since 2.0
     */
    public String toDetailedString() {
        return String.format(
            "Task[id=%d, title='%s', priority=%s, completed=%s]",
            id, title, priority, completed
        );
    }
}
