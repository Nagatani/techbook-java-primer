package todo;

/**
 * 指定した番号のタスクがないときに発生する例外です。
 *
 * <p>番号の誤りはプログラムの誤りであることが多いため、非チェック例外にしています。
 * 番号があるかどうかを先に確かめたいときは、{@link TodoList#find(int)}を使います。</p>
 *
 * @since 1.0
 */
public class TaskNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /** 見つからなかったタスクの番号 */
    private final int id;

    /**
     * 例外を作ります。
     *
     * @param id 見つからなかったタスクの番号
     */
    public TaskNotFoundException(int id) {
        super("番号" + id + "のタスクはありません");
        this.id = id;
    }

    /**
     * 見つからなかったタスクの番号を返します。
     *
     * @return タスクの番号
     */
    public int getId() {
        return id;
    }
}
