import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * 手書きの線を記録するクラス（画面に依存しないロジック）
 *
 * マウスを押してから離すまでの点の並びを、1本の線として持ちます。
 */
public class Sketch {

    /** 1本の線。色と、通った点の並び */
    public record Path(Color color, List<Point> points) {
    }

    private final List<Path> paths = new ArrayList<>();

    /** 新しい線を始める */
    public void startPath(Color color, int x, int y) {
        List<Point> points = new ArrayList<>();
        points.add(new Point(x, y));
        paths.add(new Path(color, points));
    }

    /** いま描いている線に点を足す。線を始めていなければ何もしない */
    public void addPoint(int x, int y) {
        if (!paths.isEmpty()) {
            paths.get(paths.size() - 1).points().add(new Point(x, y));
        }
    }

    /** 最後に描いた線を消す */
    public void undo() {
        if (!paths.isEmpty()) {
            paths.remove(paths.size() - 1);
        }
    }

    public void clear() {
        paths.clear();
    }

    public List<Path> getPaths() {
        return List.copyOf(paths);
    }

    public int getPathCount() {
        return paths.size();
    }
}
