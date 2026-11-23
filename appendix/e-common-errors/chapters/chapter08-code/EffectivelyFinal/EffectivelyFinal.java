import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EffectivelyFinal {
    public static void main(String[] args) {
        // 数えたい値は、配列の要素などのオブジェクトのなかに持たせる
        // （変数countsそのものは書き換えないので、実質的にfinal）
        int[] counts = {0};
        Comparator<String> byLength = new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                counts[0]++;
                return Integer.compare(a.length(), b.length());
            }
        };

        List<String> words = new ArrayList<>(List.of("banana", "fig", "apple"));
        words.sort(byLength);
        IO.println(words);
        IO.println("比べた回数が0より多い: " + (counts[0] > 0));
    }
}
