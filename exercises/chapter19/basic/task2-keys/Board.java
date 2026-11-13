/**
 * 盤面の中を動く四角の位置を管理するクラス（画面に依存しないロジック）
 */
public class Board {
    private final int width;
    private final int height;
    private final int size;
    private int x;
    private int y;

    public Board(int width, int height, int size) {
        this.width = width;
        this.height = height;
        this.size = size;
        reset();
    }

    /** 四角を動かす。盤面の外へははみ出さない */
    public void move(int dx, int dy) {
        x = clamp(x + dx, 0, width - size);
        y = clamp(y + dy, 0, height - size);
    }

    /** 四角を盤面の中央に戻す */
    public void reset() {
        x = (width - size) / 2;
        y = (height - size) / 2;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSize() {
        return size;
    }
}
