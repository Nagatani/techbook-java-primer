package todo;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 1つのタスクです。
 *
 * <p>このレコードは不変です。
 * タスクを完了にするには{@link TodoList#complete(int)}を使います。</p>
 *
 * @param id       タスクの番号（{@link TodoList}が1から順に割り当てる）
 * @param title    タスクの名前（空でない文字列）
 * @param priority 優先度
 * @param due      期限の日
 * @param done     完了していれば{@code true}
 * @since 1.0
 */
public record Task(int id, String title, Priority priority, LocalDate due, boolean done) {

    /**
     * 引数を検査してタスクを作ります。
     *
     * @param id       タスクの番号（1以上）
     * @param title    タスクの名前（空でない文字列）
     * @param priority 優先度
     * @param due      期限の日
     * @param done     完了していれば{@code true}
     * @throws NullPointerException     {@code title}、{@code priority}、{@code due}のいずれかが{@code null}のとき
     * @throws IllegalArgumentException 番号が1未満のとき、または名前が空白だけのとき
     */
    public Task {
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(due, "due");
        if (id < 1) {
            throw new IllegalArgumentException("番号は1以上です: " + id);
        }
        if (title.isBlank()) {
            throw new IllegalArgumentException("タスクの名前が空です");
        }
    }

    /**
     * 指定した日の時点で、期限を過ぎていて未完了かどうかを返します。
     *
     * <p>期限の当日は、まだ過ぎていないものとします。</p>
     *
     * @param today 基準にする日
     * @return 期限を過ぎていて未完了なら{@code true}
     * @throws NullPointerException {@code today}が{@code null}のとき
     */
    public boolean isOverdue(LocalDate today) {
        Objects.requireNonNull(today, "today");
        return !done && due.isBefore(today);
    }

    /**
     * 完了にしたタスクを返します。このタスク自身は変わりません。
     *
     * @return 番号・名前・優先度・期限が同じで、完了したタスク
     */
    Task completed() {
        return new Task(id, title, priority, due, true);
    }
}
