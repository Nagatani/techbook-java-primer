/**
 * ToDoリスト（やることの一覧）を管理するライブラリです。
 *
 * <p>{@link todo.TodoList}にタスクを登録し、完了にしたり、
 * 期限を過ぎたタスクを取り出したりします。
 * タスクは不変の{@link todo.Task}として返されるので、受け取った側が書き換えても、
 * リストの中身は変わりません。</p>
 *
 * <h2>使用例</h2>
 * <pre>{@code
 * TodoList list = new TodoList();
 * Task report = list.add("レポートを書く", Priority.HIGH, LocalDate.of(2026, 6, 10));
 * list.complete(report.id());
 * List<Task> rest = list.pending();
 * }</pre>
 *
 * <h2>このライブラリの約束</h2>
 * <ul>
 *   <li>引数に{@code null}を渡すと{@link java.lang.NullPointerException}が発生する</li>
 *   <li>一覧を返すメソッドは、変更できないリストを返す</li>
 *   <li>今日の日付が必要なメソッドは、日付を引数で受け取る（ライブラリの中で現在の日付を読まない）</li>
 * </ul>
 *
 * @since 1.0
 */
package todo;
