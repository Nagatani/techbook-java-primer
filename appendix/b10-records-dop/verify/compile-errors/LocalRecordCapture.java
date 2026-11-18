/** ローカルレコードは暗黙にstaticなので、囲むメソッドのローカル変数を使えない */
public class LocalRecordCapture {
    static void run() {
        int base = 10;
        record Point(int x) {
            int shifted() {
                return x + base;
            }
        }
    }
}
