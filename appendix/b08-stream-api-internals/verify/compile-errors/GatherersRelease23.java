import java.util.List;
import java.util.stream.Gatherers;

/** Java 23ではGatherersがプレビューのAPIなので、--enable-previewなしではコンパイルできない（IOクラスもJava 25からなのでSystem.outで書く） */
public class GatherersRelease23 {
    public static void main(String[] args) {
        System.out.println(List.of(1, 2, 3).stream().gather(Gatherers.windowFixed(2)).toList());
    }
}
