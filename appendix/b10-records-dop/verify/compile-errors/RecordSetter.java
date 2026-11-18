/** レコードのフィールドはfinalなので、メソッドから代入できない */
public class RecordSetter {
    record Point(int x, int y) {
        void setX(int x) {
            this.x = x;
        }
    }
}
