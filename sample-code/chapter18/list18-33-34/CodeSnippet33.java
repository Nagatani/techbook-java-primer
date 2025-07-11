/**
 * リスト18-33
 * DrawingPanelクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (2109行目)
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class DrawingPanel extends JComponent {
    private List<Point> currentStroke = new ArrayList<>();
    private List<List<Point>> allStrokes = new ArrayList<>();
    private Color drawingColor = Color.BLACK;
    private int strokeWidth = 2;
    
    public DrawingPanel() {
        setOpaque(true);
        setBackground(Color.WHITE);
        
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                currentStroke = new ArrayList<>();
                currentStroke.add(e.getPoint());
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                currentStroke.add(e.getPoint());
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!currentStroke.isEmpty()) {
                    allStrokes.add(new ArrayList<>(currentStroke));
                    currentStroke.clear();
                }
            }
        };
        
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, 
                                     BasicStroke.JOIN_ROUND));
        g2d.setColor(drawingColor);
        
        // 完成した線を描画
        for (List<Point> stroke : allStrokes) {
            drawStroke(g2d, stroke);
        }
        
        // 現在描画中の線を描画
        if (!currentStroke.isEmpty()) {
            drawStroke(g2d, currentStroke);
        }
        
        g2d.dispose();
    }
    
    private void drawStroke(Graphics2D g2d, List<Point> stroke) {
        if (stroke.size() < 2) {
            if (stroke.size() == 1) {
                Point p = stroke.get(0);
                g2d.fillOval(p.x - strokeWidth/2, p.y - strokeWidth/2, 
                           strokeWidth, strokeWidth);
            }
            return;
        }
        
        for (int i = 1; i < stroke.size(); i++) {
            Point p1 = stroke.get(i - 1);
            Point p2 = stroke.get(i);
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }
    
    public void clearDrawing() {
        allStrokes.clear();
        currentStroke.clear();
        repaint();
    }
    
    public void setDrawingColor(Color color) {
        this.drawingColor = color;
    }
    
    public void setStrokeWidth(int width) {
        this.strokeWidth = width;
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 400);
    }
}