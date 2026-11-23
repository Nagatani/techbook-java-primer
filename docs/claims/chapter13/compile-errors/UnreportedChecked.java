// 第13章：検査例外をtry-catchもthrowsもせずに呼ぶとコンパイルエラー
import java.io.FileReader;

public class UnreportedChecked {
    static void run() {
        new FileReader("x");
    }
}
