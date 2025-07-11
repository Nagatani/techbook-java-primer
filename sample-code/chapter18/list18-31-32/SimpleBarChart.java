/**
 * リスト18-31
 * SimpleBarChartクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (1860行目)
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SimpleBarChart extends JComponent {
    private List<ChartData> data = new ArrayList<>();
    private String title = "チャート";
    private Color[] colors = {
        Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, 
        Color.MAGENTA, Color.CYAN, Color.PINK, Color.YELLOW
    };
    private int hoveredIndex = -1;
    
    public SimpleBarChart() {
        setOpaque(true);
        setBackground(Color.WHITE);
        
        // マウスイベントでホバー効果を実装
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int oldIndex = hoveredIndex;
                hoveredIndex = getBarIndex(e.getX(), e.getY());
                if (oldIndex != hoveredIndex) {
                    repaint();
                }
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredIndex = -1;
                repaint();
            }
        });
    }
    
    public void addData(String label, double value) {
        data.add(new ChartData(label, value));
        repaint();
    }
    
    public void clearData() {
        data.clear();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (data.isEmpty()) return;
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        int margin = 50;
        
        // タイトルを描画
        g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
        FontMetrics titleFm = g2d.getFontMetrics();
        int titleWidth = titleFm.stringWidth(title);
        g2d.setColor(Color.BLACK);
        g2d.drawString(title, (width - titleWidth) / 2, 30);
        
        // チャート領域を計算
        int chartWidth = width - 2 * margin;
        int chartHeight = height - margin - 80;
        int chartX = margin;
        int chartY = 50;
        
        // 最大値を取得
        double maxValue = data.stream().mapToDouble(ChartData::getValue).max().orElse(1.0);
        
        // バーを描画
        int barWidth = chartWidth / data.size();
        for (int i = 0; i < data.size(); i++) {
            ChartData item = data.get(i);
            
            // バーの位置とサイズを計算
            int barX = chartX + i * barWidth + barWidth / 4;
            int barHeight = (int) ((item.getValue() / maxValue) * chartHeight);
            int barY = chartY + chartHeight - barHeight;
            int actualBarWidth = barWidth / 2;
            
            // バーの色を決定（ホバー時は明るく）
            Color barColor = colors[i % colors.length];
            if (i == hoveredIndex) {
                barColor = barColor.brighter();
            }
            
            // バーを描画
            g2d.setColor(barColor);
            g2d.fillRect(barX, barY, actualBarWidth, barHeight);
            
            // 境界線を描画
            g2d.setColor(Color.BLACK);
            g2d.drawRect(barX, barY, actualBarWidth, barHeight);
            
            // ラベルを描画
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
            FontMetrics labelFm = g2d.getFontMetrics();
            int labelWidth = labelFm.stringWidth(item.getLabel());
            int labelX = barX + (actualBarWidth - labelWidth) / 2;
            int labelY = chartY + chartHeight + 20;
            
            g2d.drawString(item.getLabel(), labelX, labelY);
            
            // 値を描画
            String valueStr = String.format("%.1f", item.getValue());
            int valueWidth = labelFm.stringWidth(valueStr);
            int valueX = barX + (actualBarWidth - valueWidth) / 2;
            int valueY = barY - 5;
            
            g2d.drawString(valueStr, valueX, valueY);
        }
        
        // 軸を描画
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        
        // Y軸
        g2d.drawLine(chartX, chartY, chartX, chartY + chartHeight);
        
        // X軸
        g2d.drawLine(chartX, chartY + chartHeight, 
                    chartX + chartWidth, chartY + chartHeight);
        
        g2d.dispose();
    }
    
    private int getBarIndex(int x, int y) {
        if (data.isEmpty()) return -1;
        
        int margin = 50;
        int chartWidth = getWidth() - 2 * margin;
        int barWidth = chartWidth / data.size();
        
        int index = (x - margin) / barWidth;
        return (index >= 0 && index < data.size()) ? index : -1;
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 300);
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
        repaint();
    }
    
    // 内部クラス：チャートデータ
    private static class ChartData {
        private String label;
        private double value;
        
        public ChartData(String label, double value) {
            this.label = label;
            this.value = value;
        }
        
        public String getLabel() { return label; }
        public double getValue() { return value; }
    }
}