/** コンパクトコンストラクターの中では、フィールドに代入できない（代入はコンパイラーが最後に補う） */
public class CompactAssignField {
    record Point(int x, int y) {
        Point {
            this.x = Math.abs(x);
        }
    }
}
