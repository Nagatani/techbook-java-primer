/**
 * ToDoリストのタスク（不変）。完了にするときは、新しいTaskを作って置き換える
 *
 * @param id       タスク番号
 * @param title    題名
 * @param priority 優先度
 * @param dueDay   期限（日の番号）
 * @param done     完了したかどうか
 */
public record Task(String id, String title, Priority priority, int dueDay, boolean done) {
    public Task complete() {
        return new Task(id, title, priority, dueDay, true);
    }

    public Task postpone(int days) {
        return new Task(id, title, priority, dueDay + days, done);
    }
}
