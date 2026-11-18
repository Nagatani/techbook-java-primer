/** レコードは、コンポーネント以外のインスタンスフィールドを宣言できない */
public class RecordInstanceField {
    record Point(int x, int y) {
        private int cache;
    }
}
