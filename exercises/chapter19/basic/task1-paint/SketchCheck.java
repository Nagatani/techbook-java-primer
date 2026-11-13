import java.awt.Color;

/**
 * Sketchの動作確認
 *
 * 画面を開かずに、線の記録だけを確かめます。
 */
public class SketchCheck {
    private static int total = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        IO.println("=== Sketchの動作確認 ===");
        Sketch sketch = new Sketch();
        sketch.addPoint(5, 5);
        check("線を始める前の点は記録しない", 0, sketch.getPathCount());

        sketch.startPath(Color.BLACK, 10, 10);
        sketch.addPoint(20, 15);
        sketch.addPoint(30, 20);
        check("線は1本", 1, sketch.getPathCount());
        check("1本目の点は3つ", 3, sketch.getPaths().get(0).points().size());

        sketch.startPath(Color.RED, 50, 50);
        check("線は2本", 2, sketch.getPathCount());
        check("2本目の点は1つ", 1, sketch.getPaths().get(1).points().size());

        sketch.undo();
        check("元に戻すと1本", 1, sketch.getPathCount());
        sketch.clear();
        check("すべて消すと0本", 0, sketch.getPathCount());
        sketch.undo();
        check("線がないときに元に戻しても0本のまま", 0, sketch.getPathCount());
        IO.println();
        IO.println(total + "件中" + (total - failed) + "件OK");
    }

    private static void check(String label, int expected, int actual) {
        total++;
        if (expected == actual) {
            IO.println("OK  " + label);
        } else {
            failed++;
            IO.println("NG  " + label + "（期待: " + expected + "、実際: " + actual + "）");
        }
    }
}
