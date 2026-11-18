import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * ストリームのパイプラインが、段（ステージ）をつないだオブジェクトであることを観察する。
 *   javac PipelineStages.java && java PipelineStages
 */
public class PipelineStages {
    public static void main(String[] args) {
        List<String> words = new ArrayList<>(List.of("stream", "java", "lambda", "api"));

        // 1. 中間操作を呼ぶたびに、新しい段のオブジェクトができる
        Stream<String> head = words.stream();
        Stream<String> filtered = head.filter(w -> w.length() > 3);
        Stream<Integer> mapped = filtered.map(String::length);
        Stream<Integer> sorted = mapped.sorted();
        IO.println("段1 stream(): " + head.getClass().getName());
        IO.println("段2 filter(): " + filtered.getClass().getName());
        IO.println("段3 map():    " + mapped.getClass().getName());
        IO.println("段4 sorted(): " + sorted.getClass().getName());
        IO.println("filterは同じオブジェクトを返すか: " + (head == filtered));

        // 2. 中間操作だけでは何も実行されない（遅延評価）
        IO.println("--- 中間操作だけをつなぐ ---");
        Stream<String> lazy = words.stream()
                .filter(w -> {
                    IO.println("  filter: " + w);
                    return true;
                });
        IO.println("（ここまでfilterのラムダ式は呼ばれていない）");
        IO.println("--- 終端操作toList()を呼ぶ ---");
        IO.println("結果: " + lazy.toList());

        // 3. 一度使ったストリームは再利用できない
        IO.println("--- 同じストリームをもう一度使う ---");
        try {
            lazy.count();
        } catch (IllegalStateException e) {
            IO.println("IllegalStateException: " + e.getMessage());
        }
        // 中間操作を呼んだあとの古い段も使えない
        try {
            head.map(String::toUpperCase);
        } catch (IllegalStateException e) {
            IO.println("古い段に中間操作: IllegalStateException: " + e.getMessage());
        }
    }
}
