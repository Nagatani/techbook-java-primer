import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * アナログ時計の文字盤と針を描くパネル（発展課題2の解答例）
 */
public class ClockPanel extends JPanel {
    private final int hour;
    private final int minute;
    private final int second;

    public ClockPanel(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        setBackground(Color.WHITE);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(320, 320);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Graphics2Dにすると、線の太さの指定と、線の縁を滑らかにする設定が使える
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int radius = Math.min(getWidth(), getHeight()) / 2 - 20;

        // 文字盤の円
        g2.setColor(new Color(245, 245, 235));
        g2.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);

        // 目盛り: 60本。5分ごとの目盛りは長く太くする
        for (int i = 0; i < 60; i++) {
            double degrees = i * 6.0;
            boolean major = i % 5 == 0;
            g2.setStroke(new BasicStroke(major ? 3 : 1));
            double inner = radius - (major ? 14 : 7);
            g2.drawLine(ClockMath.pointX(cx, degrees, inner), ClockMath.pointY(cy, degrees, inner),
                    ClockMath.pointX(cx, degrees, radius - 2), ClockMath.pointY(cy, degrees, radius - 2));
        }

        // 数字: 文字の幅と高さを測り、文字の中心が点に来るように置く
        g2.setFont(getFont().deriveFont(Font.BOLD, 18f));
        FontMetrics metrics = g2.getFontMetrics();
        for (int n = 1; n <= 12; n++) {
            double degrees = n * 30.0;
            int x = ClockMath.pointX(cx, degrees, radius - 30);
            int y = ClockMath.pointY(cy, degrees, radius - 30);
            String text = String.valueOf(n);
            g2.drawString(text, x - metrics.stringWidth(text) / 2, y + metrics.getAscent() / 2 - 2);
        }

        // 針: 時針は太く短く、分針は細く長く、秒針は赤く細く
        drawHand(g2, cx, cy, ClockMath.hourAngle(hour, minute), radius * 0.5, 7, Color.BLACK);
        drawHand(g2, cx, cy, ClockMath.minuteAngle(minute, second), radius * 0.75, 4, Color.BLACK);
        drawHand(g2, cx, cy, ClockMath.secondAngle(second), radius * 0.85, 1.5f, Color.RED);

        // 中心の軸
        g2.setColor(Color.RED);
        g2.fillOval(cx - 5, cy - 5, 10, 10);

        // 下に時刻を文字でも出す
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(getFont().deriveFont(14f));
        String time = "%02d:%02d:%02d".formatted(hour, minute, second);
        g2.drawString(time, cx - g2.getFontMetrics().stringWidth(time) / 2, cy + radius / 2);
    }

    private void drawHand(Graphics2D g2, int cx, int cy, double degrees, double length,
            float width, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx, cy, ClockMath.pointX(cx, degrees, length), ClockMath.pointY(cy, degrees, length));
    }
}
