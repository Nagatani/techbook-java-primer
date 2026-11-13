import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * 棒グラフを表示するプログラム（基礎課題4の解答例）
 */
public class BarChartApp {

    private static void createAndShowGUI() {
        String[] months = {"4月", "5月", "6月", "7月", "8月", "9月"};
        int[] visitors = {120, 95, 140, 180, 210, 160};

        JFrame frame = new JFrame("図書館の来館者数");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new BarChartPanel("月ごとの来館者数（人）", months, visitors));
        frame.pack(); // パネルのgetPreferredSizeに合わせてウィンドウの大きさが決まる
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
}
