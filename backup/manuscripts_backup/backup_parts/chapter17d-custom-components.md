# Part D: カスタムコンポーネントの作成

## 17.10 カスタムコンポーネントの基本概念

標準のSwingコンポーネントでは実現できない独自の機能や見た目を持つコンポーネントが必要になることがあります。このセクションでは、カスタムコンポーネントの作成方法を学びます。

カスタムコンポーネントは、既存のSwingコンポーネントを継承して作成します。もっとも一般的なアプローチは、**JComponent**クラスを継承する方法です。JComponentはすべてのSwingコンポーネントの基底クラスで、描画、イベント処理、レイアウトなどの基本機能を提供します。

## 17.11 基本的なカスタムコンポーネント

まず、シンプルなカスタムコンポーネントから始めましょう：

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundButton extends JComponent {
    private String text;
    private boolean pressed = false;
    private Color backgroundColor = Color.LIGHT_GRAY;
    private Color textColor = Color.BLACK;
    private Color pressedColor = Color.GRAY;
    
    public RoundButton(String text) {
        this.text = text;
        setOpaque(false); // 透明にして独自描画を有効にする
        setFocusable(true); // フォーカス可能にする
        
        // マウスイベントリスナーを追加
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint(); // 再描画を要求
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
                
                // ボタンの境界内でリリースされた場合のみアクションを実行
                if (contains(e.getPoint())) {
                    fireActionPerformed();
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // アンチエイリアシングを有効にして滑らかな描画
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);
        
        // ボタンの背景色を決定
        Color currentBgColor = pressed ? pressedColor : backgroundColor;
        
        // 円形のボタンを描画
        int diameter = Math.min(getWidth(), getHeight()) - 4;
        int x = (getWidth() - diameter) / 2;
        int y = (getHeight() - diameter) / 2;
        
        g2d.setColor(currentBgColor);
        g2d.fillOval(x, y, diameter, diameter);
        
        // 境界線を描画
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawOval(x, y, diameter, diameter);
        
        // テキストを描画
        g2d.setColor(textColor);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        int textX = (getWidth() - textWidth) / 2;
        int textY = (getHeight() - textHeight) / 2 + fm.getAscent();
        
        g2d.drawString(text, textX, textY);
        g2d.dispose();
    }
    
    @Override
    public Dimension getPreferredSize() {
        // コンポーネントの推奨サイズを返す
        FontMetrics fm = getFontMetrics(getFont());
        int textWidth = fm.stringWidth(text);
        int size = Math.max(textWidth + 20, 60); // 最小サイズ60px
        return new Dimension(size, size);
    }
    
    // ActionListenerサポートを追加
    private java.util.List<java.awt.event.ActionListener> actionListeners = 
        new java.util.ArrayList<>();
    
    public void addActionListener(java.awt.event.ActionListener listener) {
        actionListeners.add(listener);
    }
    
    public void removeActionListener(java.awt.event.ActionListener listener) {
        actionListeners.remove(listener);
    }
    
    private void fireActionPerformed() {
        java.awt.event.ActionEvent event = new java.awt.event.ActionEvent(
            this, java.awt.event.ActionEvent.ACTION_PERFORMED, text);
        
        for (java.awt.event.ActionListener listener : actionListeners) {
            listener.actionPerformed(event);
        }
    }
    
    // プロパティのアクセサメソッド
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
        repaint();
    }
    
    public Color getBackgroundColor() {
        return backgroundColor;
    }
    
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        repaint();
    }
}
```

### カスタムコンポーネントの使用例

```java
import javax.swing.*;
import java.awt.*;

public class CustomComponentExample extends JFrame {
    public CustomComponentExample() {
        setTitle("カスタムコンポーネントの例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        
        // カスタムの丸いボタンを作成
        RoundButton roundButton1 = new RoundButton("OK");
        RoundButton roundButton2 = new RoundButton("Cancel");
        RoundButton roundButton3 = new RoundButton("Help");
        
        // ボタンの色をカスタマイズ
        roundButton1.setBackgroundColor(Color.GREEN);
        roundButton2.setBackgroundColor(Color.RED);
        roundButton3.setBackgroundColor(Color.YELLOW);
        
        // イベントリスナーを追加
        roundButton1.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "OK がクリックされました"));
        
        roundButton2.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "Cancel がクリックされました"));
        
        roundButton3.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "Help がクリックされました"));
        
        // コンポーネントを追加
        add(roundButton1);
        add(roundButton2);
        add(roundButton3);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CustomComponentExample().setVisible(true);
        });
    }
}
```

## 17.12 高度なカスタムコンポーネント：チャートコンポーネント

より複雑な例として、データを視覚化するチャートコンポーネントを作成してみましょう：

```java
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
```

### カスタムチャートの使用例

```java
import javax.swing.*;
import java.awt.*;

public class ChartExample extends JFrame {
    public ChartExample() {
        setTitle("カスタムチャートの例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // カスタムチャートコンポーネントを作成
        SimpleBarChart chart = new SimpleBarChart();
        chart.setTitle("売上実績");
        
        // データを追加
        chart.addData("1月", 120);
        chart.addData("2月", 150);
        chart.addData("3月", 180);
        chart.addData("4月", 200);
        chart.addData("5月", 170);
        chart.addData("6月", 190);
        
        // パネルに追加
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("月別売上"));
        chartPanel.add(chart, BorderLayout.CENTER);
        
        // コントロールパネルを作成
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("更新");
        JButton clearButton = new JButton("クリア");
        
        refreshButton.addActionListener(e -> {
            chart.clearData();
            // ランダムデータを生成
            String[] months = {"1月", "2月", "3月", "4月", "5月", "6月"};
            for (String month : months) {
                chart.addData(month, Math.random() * 300 + 50);
            }
        });
        
        clearButton.addActionListener(e -> chart.clearData());
        
        controlPanel.add(refreshButton);
        controlPanel.add(clearButton);
        
        add(chartPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChartExample().setVisible(true);
        });
    }
}
```

## 17.13 インタラクティブな描画コンポーネント

マウス操作で線を描画できるコンポーネントを作成してみましょう：

```java
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
```

### 描画アプリケーションの例

```java
import javax.swing.*;
import java.awt.*;

public class DrawingApplication extends JFrame {
    private DrawingPanel drawingPanel;
    
    public DrawingApplication() {
        setTitle("描画アプリケーション");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 描画パネル
        drawingPanel = new DrawingPanel();
        JScrollPane scrollPane = new JScrollPane(drawingPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        // ツールバー
        JPanel toolbar = new JPanel(new FlowLayout());
        
        // 色選択ボタン
        JButton[] colorButtons = {
            createColorButton("黒", Color.BLACK),
            createColorButton("赤", Color.RED),
            createColorButton("青", Color.BLUE),
            createColorButton("緑", Color.GREEN)
        };
        
        for (JButton button : colorButtons) {
            toolbar.add(button);
        }
        
        // 線の太さスライダー
        toolbar.add(new JLabel("線の太さ:"));
        JSlider strokeSlider = new JSlider(1, 10, 2);
        strokeSlider.addChangeListener(e -> {
            drawingPanel.setStrokeWidth(strokeSlider.getValue());
        });
        toolbar.add(strokeSlider);
        
        // クリアボタン
        JButton clearButton = new JButton("クリア");
        clearButton.addActionListener(e -> drawingPanel.clearDrawing());
        toolbar.add(clearButton);
        
        add(toolbar, BorderLayout.NORTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private JButton createColorButton(String name, Color color) {
        JButton button = new JButton(name);
        button.setBackground(color);
        button.setForeground(color.equals(Color.BLACK) ? Color.WHITE : Color.BLACK);
        button.addActionListener(e -> drawingPanel.setDrawingColor(color));
        return button;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DrawingApplication().setVisible(true);
        });
    }
}
```

## 17.14 カスタムコンポーネント設計のベストプラクティス

カスタムコンポーネントを設計する際は、以下の点に注意してください：

### 1. 適切な基底クラスの選択

```java
// 単純な描画コンポーネント
public class MyComponent extends JComponent

// 既存コンポーネントを拡張
public class EnhancedButton extends JButton

// パネルとして使用
public class MyPanel extends JPanel
```

### 2. パフォーマンスの最適化

```java
@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    // 必要な場合のみ再描画
    if (needsRepaint) {
        // 重い描画処理
        needsRepaint = false;
    }
}

// 効率的な境界計算
@Override
public boolean contains(int x, int y) {
    // カスタムの境界判定
    return customBounds.contains(x, y);
}
```

### 3. アクセシビリティの考慮

```java
public MyComponent() {
    // キーボードナビゲーション対応
    setFocusable(true);
    
    // アクセシビリティ情報を設定
    getAccessibleContext().setAccessibleName("カスタムコンポーネント");
    getAccessibleContext().setAccessibleDescription("説明");
}
```

### 4. イベント処理の標準化

```java
// 標準的なイベントリスナーパターンを実装
public void addMyComponentListener(MyComponentListener listener) {
    listeners.add(listener);
}

public void removeMyComponentListener(MyComponentListener listener) {
    listeners.remove(listener);
}

protected void fireMyComponentEvent(MyComponentEvent event) {
    for (MyComponentListener listener : listeners) {
        listener.componentChanged(event);
    }
}
```

### まとめ

カスタムコンポーネントの作成により、標準のSwingコンポーネントでは実現できない独自のUI要素を実装できます：

1. **JComponentを継承**してカスタムコンポーネントを作成
2. **paintComponent()メソッドをオーバーライド**して独自の描画を実装
3. **マウスイベントやキーボードイベントを処理**してインタラクション機能を追加
4. **ActionListenerパターンを実装**して標準的なイベント処理を提供
5. **パフォーマンスとアクセシビリティを考慮**した設計

これらの技術により、プロフェッショナルなGUIアプリケーションに必要な独自のUI要素を作成できます。次のパートでは、これまで学んだ内容を活用した実践的な演習課題に取り組みます。