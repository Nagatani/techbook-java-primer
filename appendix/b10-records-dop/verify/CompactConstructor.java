/**
 * コンパクトコンストラクター（コンパクトな正準コンストラクター）の動きを観察する。
 *
 *   javac CompactConstructor.java
 *   javap -c -p 'CompactConstructor$Range'
 *   java CompactConstructor
 */
public class CompactConstructor {
    record Range(int low, int high) {
        Range {
            // ここでのlowとhighは、フィールドではなく暗黙の仮引数
            if (low > high) {
                // 引数を入れ替えて正規化する（仮引数には代入できる）
                int tmp = low;
                low = high;
                high = tmp;
            }
            if (high - low > 100) {
                throw new IllegalArgumentException("範囲が広すぎる: " + low + ".." + high);
            }
            // 本体の最後に、this.low = low; this.high = high; がコンパイラーによって補われる
        }
    }

    public static void main(String[] args) {
        IO.println(new Range(1, 5));
        IO.println(new Range(9, 3));
        try {
            new Range(0, 1000);
        } catch (IllegalArgumentException e) {
            IO.println("例外: " + e.getMessage());
        }
    }
}
