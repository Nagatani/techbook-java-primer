import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class SmoothPanel extends JPanel {
    private BufferedImage buffer;
    private boolean needsRedraw = true;
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // バッファが未作成または再描画が必要な場合のみ描画
        if (buffer == null || needsRedraw) {
            createBuffer();
            needsRedraw = false;
        }
        
        // バッファの内容を描画
        g.drawImage(buffer, 0, 0, this);
    }
    
    private void createBuffer() {
        buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = buffer.createGraphics();
        
        // アンチエイリアシングを有効化
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 重い描画処理
        for (int i = 0; i < 1000; i++) {
            g2d.drawLine(i, 0, i, getHeight());
        }
        
        g2d.dispose();
    }
    
    public void invalidateBuffer() {
        needsRedraw = true;
        repaint();
    }
}
