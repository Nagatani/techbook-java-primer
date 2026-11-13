import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;

/**
 * 分類ごとの合計を円グラフで描くパネル
 */
public class PieChartPanel extends JPanel {
    private final List<Integer> amounts = new ArrayList<>();
    private final List<Color> colors;

    public PieChartPanel(Map<String, Integer> totals, List<Color> colors) {
        this.amounts.addAll(totals.values());
        this.colors = List.copyOf(colors);
        setBackground(Color.WHITE);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(260, 260);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight()) - 40;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        int total = 0;
        for (int amount : amounts) {
            total += amount;
        }
        if (total == 0) {
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawOval(x, y, size, size);
            return;
        }

        // fillArcの角度は3時の方向が0度で、反時計回りに増える
        // 12時の方向（90度）から時計回りに並べるので、角度を引いていく
        int start = 90;
        for (int i = 0; i < amounts.size(); i++) {
            int degrees = Household.toDegrees(amounts.get(i), total);
            if (i == amounts.size() - 1) {
                // 丸めの誤差で隙間や重なりができないよう、最後の扇は残りの角度すべてにする
                int used = 90 - start;
                degrees = 360 - used;
            }
            g2.setColor(colors.get(i % colors.size()));
            g2.fillArc(x, y, size, size, start, -degrees);
            start -= degrees;
        }
        g2.setColor(Color.WHITE);
        g2.drawOval(x, y, size, size);
    }
}
