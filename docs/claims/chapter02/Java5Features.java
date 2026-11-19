// 第2章：拡張for文・ジェネリクス・enum（Java 5で導入）
import java.util.ArrayList;
import java.util.List;

public class Java5Features {
    enum Color { RED, GREEN }

    static int count(List<Color> colors) {
        int n = 0;
        for (Color c : colors) {
            if (c == Color.RED) {
                n++;
            }
        }
        return n;
    }

    static List<Color> sample() {
        return new ArrayList<Color>();
    }
}
