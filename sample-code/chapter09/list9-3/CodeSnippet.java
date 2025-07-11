/**
 * リスト9-3
 * コードスニペット
 * 
 * 元ファイル: chapter09-records.md (160行目)
 */

public record PositivePoint(int x, int y) {
    // コンパクトコンストラクタ
    public PositivePoint {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("座標は負の値にできません");
        }
        // ここで this.x = x; のような代入は不要（自動的に行われる）
    }
}