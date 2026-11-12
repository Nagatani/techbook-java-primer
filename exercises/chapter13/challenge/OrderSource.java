import java.io.IOException;

/**
 * 注文データを1行ずつ渡す読み取り口（ファイルの代わり）
 * 使い終わったら閉じる必要があるので、AutoCloseableを実装する
 */
public class OrderSource implements AutoCloseable {
    private final String name;
    private final String[] lines;
    private final int brokenAt;          // この行を読むと失敗する（0なら失敗しない）
    private final boolean failOnClose;   // 閉じる処理で失敗するか
    private int position = 0;

    public OrderSource(String name, String[] lines) {
        this(name, lines, 0, false);
    }

    /** 読み取りの途中で失敗する読み取り口（動作確認用） */
    public OrderSource(String name, String[] lines, int brokenAt, boolean failOnClose) {
        this.name = name;
        this.lines = lines;
        this.brokenAt = brokenAt;
        this.failOnClose = failOnClose;
        IO.println("[" + name + "] を開きました");
    }

    /** 次の行を返す。もう行がなければnullを返す */
    public String readLine() throws IOException {
        if (position >= lines.length) {
            return null;
        }
        position++;
        if (position == brokenAt) {
            throw new IOException(position + "行目を読み取れません（接続が切れました）");
        }
        return lines[position - 1];
    }

    /** いま読んだ行の番号 */
    public int getLineNumber() {
        return position;
    }

    @Override
    public void close() throws IOException {
        IO.println("[" + name + "] を閉じました");
        if (failOnClose) {
            throw new IOException("[" + name + "] の後始末に失敗しました");
        }
    }
}
