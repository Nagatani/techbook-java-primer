package chapter10.solutions;

/**
 * 値を1つだけ入れられる箱（課題1の解答例）
 *
 * @param <T> 入れる値の型
 */
public class GenericBox<T> {
    private T content;

    public GenericBox() {
        this.content = null;
    }

    public GenericBox(T content) {
        this.content = content;
    }

    public void put(T content) {
        this.content = content;
    }

    /**
     * 中身を返す。戻り値の型がTなので、受け取る側でキャストは要らない
     */
    public T get() {
        return content;
    }

    public boolean isEmpty() {
        return content == null;
    }

    public void clear() {
        content = null;
    }

    /**
     * 中身の型名と値を表示する。型名は実行時のオブジェクトから取り出す
     * （型パラメーターTそのものは実行時に消えているので、T.classとは書けない）
     */
    public void displayInfo() {
        IO.println("GenericBox情報:");
        if (content == null) {
            IO.println("  空");
        } else {
            IO.println("  型: " + content.getClass().getSimpleName() + ", 値: " + content);
        }
    }

    @Override
    public String toString() {
        return "GenericBox{content=" + content + "}";
    }
}
