import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Arrays;
import javax.swing.JPanel;

/**
 * 棒グラフを描くパネル（基礎課題4の解答例）
 *
 * JPanelを継承し、paintComponentで棒・軸・文字を描きます。
 */
public class BarChartPanel extends JPanel {
    private static final int MARGIN_LEFT = 50;
    private static final int MARGIN_RIGHT = 20;
    private static final int MARGIN_TOP = 50;
    private static final int MARGIN_BOTTOM = 40;

    private final String title;
    private final String[] labels;
    private final int[] values;

    public BarChartPanel(String title, String[] labels, int[] values) {
        if (labels.length != values.length) {
            throw new IllegalArgumentException("ラベルと値の数が合いません");
        }
        this.title = title;
        // 受け取った配列を外から書き換えられないように、コピーを持つ（第6章）
        this.labels = Arrays.copyOf(labels, labels.length);
        this.values = Arrays.copyOf(values, values.length);
        setBackground(Color.WHITE);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(480, 320);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // 背景を塗ってから描く

        int width = getWidth();
        int height = getHeight();
        int areaWidth = width - MARGIN_LEFT - MARGIN_RIGHT;
        int areaHeight = height - MARGIN_TOP - MARGIN_BOTTOM;
        int baseY = MARGIN_TOP + areaHeight; // 棒の下端（x軸）のy座標

        // タイトル
        g.setColor(Color.BLACK);
        g.setFont(getFont().deriveFont(Font.BOLD, 16f));
        FontMetrics titleMetrics = g.getFontMetrics();
        g.drawString(title, (width - titleMetrics.stringWidth(title)) / 2, 25);

        // 軸。y座標は下へ行くほど大きいので、x軸はbaseYの高さに引く
        g.drawLine(MARGIN_LEFT, MARGIN_TOP, MARGIN_LEFT, baseY);
        g.drawLine(MARGIN_LEFT, baseY, MARGIN_LEFT + areaWidth, baseY);

        if (values.length == 0) {
            return;
        }
        int max = ChartScale.max(values);
        int slot = areaWidth / values.length; // 1本の棒に使える幅
        int barWidth = slot * 6 / 10;

        g.setFont(getFont().deriveFont(12f));
        FontMetrics metrics = g.getFontMetrics();
        for (int i = 0; i < values.length; i++) {
            int barHeight = ChartScale.barHeight(values[i], max, areaHeight);
            int x = MARGIN_LEFT + slot * i + (slot - barWidth) / 2;
            int y = baseY - barHeight; // 棒の左上の角

            // 最大値の棒だけ色を変える
            g.setColor(values[i] == max ? new Color(220, 90, 60) : new Color(70, 130, 180));
            g.fillRect(x, y, barWidth, barHeight);

            // 棒の上に値、下にラベル。文字の幅を測って棒の中央にそろえる
            g.setColor(Color.BLACK);
            String valueText = String.valueOf(values[i]);
            g.drawString(valueText, x + (barWidth - metrics.stringWidth(valueText)) / 2, y - 4);
            g.drawString(labels[i], x + (barWidth - metrics.stringWidth(labels[i])) / 2,
                    baseY + metrics.getAscent() + 4);
        }
    }
}
