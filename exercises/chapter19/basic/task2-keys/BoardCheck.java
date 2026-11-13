/**
 * Boardの動作確認
 *
 * 画面を開かずに、四角の位置の計算だけを確かめます。
 */
public class BoardCheck {
    private static int total = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        IO.println("=== Boardの動作確認 ===");
        Board board = new Board(400, 300, 40);
        check("最初は中央（x）", 180, board.getX());
        check("最初は中央（y）", 130, board.getY());
        board.move(10, -10);
        check("右へ10（x）", 190, board.getX());
        check("上へ10（y）", 120, board.getY());
        board.move(1000, 0);
        check("右端より先へは行かない", 360, board.getX());
        board.move(0, -1000);
        check("上端より先へは行かない", 0, board.getY());
        board.move(-1000, 1000);
        check("左端より先へは行かない", 0, board.getX());
        check("下端より先へは行かない", 260, board.getY());
        board.reset();
        check("中央に戻す", 180, board.getX());
        IO.println();
        IO.println(total + "件中" + (total - failed) + "件OK");
    }

    private static void check(String label, int expected, int actual) {
        total++;
        if (expected == actual) {
            IO.println("OK  " + label);
        } else {
            failed++;
            IO.println("NG  " + label + "（期待: " + expected + "、実際: " + actual + "）");
        }
    }
}
