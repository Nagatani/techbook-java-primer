import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;

public class DrawingPanel extends JPanel {
    public DrawingPanel() {
        setBackground(Color.WHITE);
    }

    @Override
    public Dimension getPreferredSize() {           // ①
        return new Dimension(300, 200);
    }

    @Override
    protected void paintComponent(Graphics g) {     // ②
        super.paintComponent(g);                    // ③

        g.setColor(Color.BLUE);                     // ④
        g.fillRect(20, 20, 100, 60);

        g.setColor(Color.RED);
        g.drawOval(150, 20, 120, 120);

        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("Hello, Graphics!", 20, 170);
    }
}
