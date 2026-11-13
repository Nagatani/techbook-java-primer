/**
 * 開いたことと閉じたことを表示するリソース（課題3の解答例）
 *
 * AutoCloseableを実装したクラスは、try-with-resourcesで使える。tryを抜けるときに、自動でcloseが呼ばれる。
 */
public class TrackedResource implements AutoCloseable {
    private final String name;

    public TrackedResource(String name) {
        this.name = name;
        IO.println(name + " を開きました");
    }

    // 空の作業を渡されたら失敗する
    public void use(String work) {
        if (work.isEmpty()) {
            throw new IllegalStateException(name + " で処理に失敗しました");
        }
        IO.println(name + " で処理: " + work);
    }

    // AutoCloseableのcloseはthrows Exceptionと宣言されているが、ここでは例外を投げないので宣言を外す
    @Override
    public void close() {
        IO.println(name + " を閉じました");
    }
}
