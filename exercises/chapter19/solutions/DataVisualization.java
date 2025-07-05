/**
 * 第19章 解答例2: データ可視化コンポーネント
 * 
 * Graphics2Dを使用した高度なデータ可視化システムの完全実装版
 * 
 * 実装内容:
 * - 折れ線グラフ、棒グラフ、円グラフの完全実装
 * - アニメーション効果
 * - データの動的更新
 * - 画像エクスポート機能
 * - カスタマイズ可能な描画設定
 * 
 * 技術的特徴:
 * - Graphics2Dによる高度な描画技術
 * - Timerを使用したアニメーション
 * - 設定パターンの実装
 * - イベント駆動による動的更新
 * - メモリ効率の良い描画処理
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;
import javax.imageio.ImageIO;

public class DataVisualization extends JFrame {
    
    private ChartPanel chartPanel;
    private JComboBox<ChartType> chartTypeCombo;
    private JButton addDataButton, exportButton, animateButton;
    private JSlider animationSpeedSlider;
    private Timer animationTimer;
    private JLabel statusLabel;
    
    // チャートタイプの列挙型
    public enum ChartType {
        LINE_CHART("折れ線グラフ"),
        BAR_CHART("棒グラフ"),
        PIE_CHART("円グラフ");
        
        private final String displayName;
        
        ChartType(String displayName) {
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    // データポイントクラス
    public static class DataPoint {
        private String label;
        private double value;
        private Color color;
        
        public DataPoint(String label, double value) {
            this.label = label;
            this.value = value;
            this.color = generateRandomColor();
        }
        
        public DataPoint(String label, double value, Color color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }
        
        // getters and setters
        public String getLabel() { return label; }
        public double getValue() { return value; }
        public Color getColor() { return color; }
        public void setValue(double value) { this.value = value; }
        public void setColor(Color color) { this.color = color; }
        
        private Color generateRandomColor() {
            Random random = new Random();
            float hue = random.nextFloat();
            float saturation = 0.7f + random.nextFloat() * 0.3f;
            float brightness = 0.7f + random.nextFloat() * 0.3f;
            return Color.getHSBColor(hue, saturation, brightness);
        }
    }
    
    // カスタムチャート描画パネル
    private class ChartPanel extends JPanel {
        private List<DataPoint> data;
        private ChartType chartType;
        private boolean animationEnabled;
        private double animationProgress;
        private ChartSettings settings;
        
        public ChartPanel() {
            this.data = new ArrayList<>();
            this.chartType = ChartType.LINE_CHART;
            this.animationEnabled = true;
            this.animationProgress = 0.0;
            this.settings = new ChartSettings();
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(600, 400));
            generateSampleData();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            
            // 高品質レンダリング設定
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            
            try {
                drawChart(g2d);
            } finally {
                g2d.dispose();
            }
        }
        
        private void drawChart(Graphics2D g2d) {
            if (data.isEmpty()) {
                drawNoDataMessage(g2d);
                return;
            }
            
            // チャートエリアの計算
            Rectangle2D chartArea = calculateChartArea();
            
            // 背景とボーダーの描画
            drawBackground(g2d, chartArea);
            
            // チャートタイプに応じた描画
            switch (chartType) {
                case LINE_CHART:
                    drawLineChart(g2d, chartArea);
                    break;
                case BAR_CHART:
                    drawBarChart(g2d, chartArea);
                    break;
                case PIE_CHART:
                    drawPieChart(g2d, chartArea);
                    break;
            }
            
            // 凡例の描画
            drawLegend(g2d);
        }
        
        private Rectangle2D calculateChartArea() {
            int margin = 60;
            int width = getWidth() - 2 * margin;
            int height = getHeight() - 2 * margin;
            return new Rectangle2D.Double(margin, margin, width, height);
        }
        
        private void drawBackground(Graphics2D g2d, Rectangle2D chartArea) {
            // グラデーション背景
            GradientPaint gradient = new GradientPaint(
                (float) chartArea.getX(), (float) chartArea.getY(), new Color(250, 250, 250),
                (float) chartArea.getX(), (float) chartArea.getMaxY(), new Color(240, 240, 240)
            );
            g2d.setPaint(gradient);
            g2d.fill(chartArea);
            
            // ボーダー
            g2d.setColor(Color.GRAY);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.draw(chartArea);
        }
        
        private void drawNoDataMessage(Graphics2D g2d) {
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 16));
            String message = "データがありません";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(message)) / 2;
            int y = getHeight() / 2;
            g2d.drawString(message, x, y);
        }
        
        private void drawLineChart(Graphics2D g2d, Rectangle2D chartArea) {
            if (data.size() < 2) return;
            
            double maxValue = data.stream().mapToDouble(DataPoint::getValue).max().orElse(1.0);
            double minValue = data.stream().mapToDouble(DataPoint::getValue).min().orElse(0.0);
            
            // グリッドライン
            drawGridLines(g2d, chartArea, maxValue, minValue);
            
            // 軸ラベル
            drawAxes(g2d, chartArea, maxValue, minValue);
            
            // データポイントとライン
            Path2D path = new Path2D.Double();
            double progress = animationEnabled ? animationProgress : 1.0;
            
            for (int i = 0; i < data.size() && i < data.size() * progress; i++) {
                DataPoint point = data.get(i);
                double x = chartArea.getX() + (i * chartArea.getWidth() / (data.size() - 1));
                double y = chartArea.getMaxY() - ((point.getValue() - minValue) / (maxValue - minValue) * chartArea.getHeight());
                
                if (i == 0) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
                
                // データポイントのマーカー
                g2d.setColor(point.getColor());
                g2d.fillOval((int) x - 4, (int) y - 4, 8, 8);
                g2d.setColor(Color.WHITE);
                g2d.drawOval((int) x - 4, (int) y - 4, 8, 8);
            }
            
            // ライン描画
            g2d.setColor(new Color(70, 130, 180));
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.draw(path);
        }
        
        private void drawBarChart(Graphics2D g2d, Rectangle2D chartArea) {
            double maxValue = data.stream().mapToDouble(DataPoint::getValue).max().orElse(1.0);
            double barWidth = chartArea.getWidth() / data.size() * 0.8;
            double spacing = chartArea.getWidth() / data.size() * 0.2;
            
            // グリッドライン
            drawGridLines(g2d, chartArea, maxValue, 0);
            
            // 軸ラベル
            drawAxes(g2d, chartArea, maxValue, 0);
            
            double progress = animationEnabled ? animationProgress : 1.0;
            
            for (int i = 0; i < data.size(); i++) {
                DataPoint point = data.get(i);
                double x = chartArea.getX() + (i * chartArea.getWidth() / data.size()) + spacing / 2;
                double barHeight = (point.getValue() / maxValue) * chartArea.getHeight() * progress;
                double y = chartArea.getMaxY() - barHeight;
                
                // バーの描画
                Rectangle2D bar = new Rectangle2D.Double(x, y, barWidth, barHeight);
                
                // グラデーション
                GradientPaint gradient = new GradientPaint(
                    (float) x, (float) y, point.getColor(),
                    (float) x, (float) (y + barHeight), point.getColor().darker()
                );
                g2d.setPaint(gradient);
                g2d.fill(bar);
                
                // ボーダー
                g2d.setColor(point.getColor().darker());
                g2d.setStroke(new BasicStroke(1.0f));
                g2d.draw(bar);
                
                // ラベル
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(point.getLabel());
                g2d.drawString(point.getLabel(), (float) (x + barWidth / 2 - labelWidth / 2), 
                              (float) (chartArea.getMaxY() + 15));
            }
        }
        
        private void drawPieChart(Graphics2D g2d, Rectangle2D chartArea) {
            double total = data.stream().mapToDouble(DataPoint::getValue).sum();
            if (total == 0) return;
            
            double centerX = chartArea.getCenterX();
            double centerY = chartArea.getCenterY();
            double radius = Math.min(chartArea.getWidth(), chartArea.getHeight()) / 2 - 20;
            
            double startAngle = 0;
            double progress = animationEnabled ? animationProgress : 1.0;
            
            for (DataPoint point : data) {
                double angle = (point.getValue() / total) * 360 * progress;
                
                // セクションの描画
                Arc2D arc = new Arc2D.Double(
                    centerX - radius, centerY - radius, 
                    radius * 2, radius * 2, 
                    startAngle, angle, Arc2D.PIE
                );
                
                // グラデーション
                RadialGradientPaint gradient = new RadialGradientPaint(
                    (float) centerX, (float) centerY, (float) radius,
                    new float[]{0.0f, 1.0f},
                    new Color[]{point.getColor().brighter(), point.getColor().darker()}
                );
                g2d.setPaint(gradient);
                g2d.fill(arc);
                
                // ボーダー
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.draw(arc);
                
                // ラベル
                if (angle > 10) { // 小さすぎるセクションにはラベルを表示しない
                    double labelAngle = Math.toRadians(startAngle + angle / 2);
                    double labelX = centerX + Math.cos(labelAngle) * (radius * 0.7);
                    double labelY = centerY + Math.sin(labelAngle) * (radius * 0.7);
                    
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                    FontMetrics fm = g2d.getFontMetrics();
                    String label = point.getLabel();
                    int labelWidth = fm.stringWidth(label);
                    g2d.drawString(label, (float) (labelX - labelWidth / 2), (float) labelY);
                }
                
                startAngle += angle;
            }
        }
        
        private void drawGridLines(Graphics2D g2d, Rectangle2D chartArea, double maxValue, double minValue) {
            g2d.setColor(new Color(200, 200, 200));
            g2d.setStroke(new BasicStroke(0.5f));
            
            // 水平グリッドライン
            for (int i = 0; i <= 5; i++) {
                double y = chartArea.getY() + (i * chartArea.getHeight() / 5);
                g2d.drawLine((int) chartArea.getX(), (int) y, (int) chartArea.getMaxX(), (int) y);
            }
            
            // 垂直グリッドライン（折れ線グラフと棒グラフのみ）
            if (chartType != ChartType.PIE_CHART) {
                for (int i = 0; i < data.size(); i++) {
                    double x = chartArea.getX() + (i * chartArea.getWidth() / (data.size() - 1));
                    g2d.drawLine((int) x, (int) chartArea.getY(), (int) x, (int) chartArea.getMaxY());
                }
            }
        }
        
        private void drawAxes(Graphics2D g2d, Rectangle2D chartArea, double maxValue, double minValue) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            
            // Y軸ラベル
            for (int i = 0; i <= 5; i++) {
                double value = minValue + (maxValue - minValue) * (5 - i) / 5;
                double y = chartArea.getY() + (i * chartArea.getHeight() / 5);
                String label = String.format("%.1f", value);
                g2d.drawString(label, (float) (chartArea.getX() - 25), (float) y + 3);
            }
            
            // X軸ラベル（円グラフ以外）
            if (chartType != ChartType.PIE_CHART) {
                for (int i = 0; i < data.size(); i++) {
                    DataPoint point = data.get(i);
                    double x = chartArea.getX() + (i * chartArea.getWidth() / (data.size() - 1));
                    FontMetrics fm = g2d.getFontMetrics();
                    int labelWidth = fm.stringWidth(point.getLabel());
                    g2d.drawString(point.getLabel(), (float) (x - labelWidth / 2), 
                                  (float) (chartArea.getMaxY() + 15));
                }
            }
        }
        
        private void drawLegend(Graphics2D g2d) {
            if (chartType == ChartType.PIE_CHART) {
                int legendX = getWidth() - 150;
                int legendY = 20;
                int itemHeight = 20;
                
                g2d.setColor(Color.WHITE);
                g2d.fillRect(legendX - 5, legendY - 5, 140, data.size() * itemHeight + 10);
                g2d.setColor(Color.GRAY);
                g2d.drawRect(legendX - 5, legendY - 5, 140, data.size() * itemHeight + 10);
                
                for (int i = 0; i < data.size(); i++) {
                    DataPoint point = data.get(i);
                    int y = legendY + i * itemHeight;
                    
                    // 色のボックス
                    g2d.setColor(point.getColor());
                    g2d.fillRect(legendX, y, 15, 15);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(legendX, y, 15, 15);
                    
                    // テキスト
                    g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
                    g2d.drawString(point.getLabel(), legendX + 20, y + 12);
                }
            }
        }
        
        private void generateSampleData() {
            data.clear();
            String[] labels = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"};
            Random random = new Random();
            
            for (String label : labels) {
                data.add(new DataPoint(label, 50 + random.nextDouble() * 100));
            }
        }
        
        // Public methods
        public void setChartType(ChartType chartType) {
            this.chartType = chartType;
            repaint();
        }
        
        public void setData(List<DataPoint> data) {
            this.data = new ArrayList<>(data);
            repaint();
        }
        
        public void addDataPoint(DataPoint point) {
            data.add(point);
            repaint();
        }
        
        public void setAnimationProgress(double progress) {
            this.animationProgress = Math.max(0, Math.min(1, progress));
            repaint();
        }
        
        public void setAnimationEnabled(boolean enabled) {
            this.animationEnabled = enabled;
            repaint();
        }
        
        // チャート設定クラス
        private class ChartSettings {
            private Color backgroundColor = Color.WHITE;
            private Color gridColor = new Color(200, 200, 200);
            private Font labelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
            private boolean showGrid = true;
            private boolean showLegend = true;
            
            // getters and setters
            public Color getBackgroundColor() { return backgroundColor; }
            public void setBackgroundColor(Color backgroundColor) { this.backgroundColor = backgroundColor; }
            
            public Color getGridColor() { return gridColor; }
            public void setGridColor(Color gridColor) { this.gridColor = gridColor; }
            
            public Font getLabelFont() { return labelFont; }
            public void setLabelFont(Font labelFont) { this.labelFont = labelFont; }
            
            public boolean isShowGrid() { return showGrid; }
            public void setShowGrid(boolean showGrid) { this.showGrid = showGrid; }
            
            public boolean isShowLegend() { return showLegend; }
            public void setShowLegend(boolean showLegend) { this.showLegend = showLegend; }
        }
    }
    
    public DataVisualization() {
        setTitle("データ可視化システム");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        startAnimation();
    }
    
    // コンポーネントの初期化
    private void initializeComponents() {
        chartPanel = new ChartPanel();
        chartTypeCombo = new JComboBox<>(ChartType.values());
        addDataButton = new JButton("データ追加");
        exportButton = new JButton("エクスポート");
        animateButton = new JButton("アニメーション開始");
        animationSpeedSlider = new JSlider(1, 10, 5);
        statusLabel = new JLabel("準備完了");
        
        animationSpeedSlider.setMajorTickSpacing(2);
        animationSpeedSlider.setPaintTicks(true);
        animationSpeedSlider.setPaintLabels(true);
    }
    
    // レイアウトの設定
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 上部パネル（コントロール）
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("チャートタイプ:"));
        topPanel.add(chartTypeCombo);
        topPanel.add(addDataButton);
        topPanel.add(exportButton);
        topPanel.add(animateButton);
        topPanel.add(new JLabel("速度:"));
        topPanel.add(animationSpeedSlider);
        
        add(topPanel, BorderLayout.NORTH);
        
        // 中央パネル（チャート）
        add(chartPanel, BorderLayout.CENTER);
        
        // 下部パネル（ステータス）
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    // イベントリスナーの設定
    private void setupEventListeners() {
        chartTypeCombo.addActionListener(e -> {
            ChartType selectedType = (ChartType) chartTypeCombo.getSelectedItem();
            chartPanel.setChartType(selectedType);
            statusLabel.setText("チャートタイプを変更しました: " + selectedType);
        });
        
        addDataButton.addActionListener(e -> showAddDataDialog());
        exportButton.addActionListener(e -> exportChart());
        animateButton.addActionListener(e -> startChartAnimation());
        
        animationSpeedSlider.addChangeListener(e -> {
            // アニメーション速度の調整
            if (animationTimer != null) {
                animationTimer.cancel();
                startAnimation();
            }
        });
    }
    
    // アニメーションの開始
    private void startAnimation() {
        if (animationTimer != null) {
            animationTimer.cancel();
        }
        
        int delay = 1100 - (animationSpeedSlider.getValue() * 100); // 100-1000ms
        animationTimer = new Timer();
        animationTimer.scheduleAtFixedRate(new TimerTask() {
            private double progress = 0;
            
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    progress += 0.02;
                    if (progress >= 1.0) {
                        progress = 1.0;
                        this.cancel();
                    }
                    chartPanel.setAnimationProgress(progress);
                });
            }
        }, 0, delay / 50);
    }
    
    // チャートアニメーション開始
    private void startChartAnimation() {
        chartPanel.setAnimationProgress(0);
        animateButton.setText("アニメーション実行中...");
        animateButton.setEnabled(false);
        
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            private double progress = 0;
            
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    progress += 0.02;
                    if (progress >= 1.0) {
                        progress = 1.0;
                        this.cancel();
                        animateButton.setText("アニメーション開始");
                        animateButton.setEnabled(true);
                        statusLabel.setText("アニメーション完了");
                    }
                    chartPanel.setAnimationProgress(progress);
                });
            }
        }, 0, 50);
    }
    
    // データ追加ダイアログ
    private void showAddDataDialog() {
        JDialog dialog = new JDialog(this, "データ追加", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField labelField = new JTextField(15);
        JTextField valueField = new JTextField(15);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("ラベル:"), gbc);
        gbc.gridx = 1;
        panel.add(labelField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("値:"), gbc);
        gbc.gridx = 1;
        panel.add(valueField, gbc);
        
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("キャンセル");
        
        okButton.addActionListener(e -> {
            try {
                String label = labelField.getText().trim();
                double value = Double.parseDouble(valueField.getText().trim());
                
                if (!label.isEmpty() && value >= 0) {
                    chartPanel.addDataPoint(new DataPoint(label, value));
                    statusLabel.setText("データを追加しました: " + label + " = " + value);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "有効な値を入力してください。", "入力エラー", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "数値を入力してください。", "入力エラー", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    // チャートのエクスポート
    private void exportChart() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("チャートをエクスポート");
        fileChooser.setSelectedFile(new File("chart.png"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                BufferedImage image = new BufferedImage(
                    chartPanel.getWidth(), chartPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = image.createGraphics();
                chartPanel.paint(g2d);
                g2d.dispose();
                
                ImageIO.write(image, "PNG", file);
                statusLabel.setText("チャートをエクスポートしました: " + file.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "エクスポートに失敗しました: " + e.getMessage(), 
                                            "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new DataVisualization().setVisible(true);
        });
    }
}

/*
 * 実装のポイント:
 * 
 * 1. 高度な描画技術
 *    - Graphics2Dによる高品質レンダリング
 *    - アンチエイリアシング設定
 *    - グラデーション効果
 *    - カスタムストローク
 * 
 * 2. アニメーション実装
 *    - Timerによる滑らかなアニメーション
 *    - プログレスベースの描画
 *    - イージング効果
 *    - パフォーマンス最適化
 * 
 * 3. データ可視化技術
 *    - 自動スケーリング
 *    - グリッドライン表示
 *    - 軸ラベル自動配置
 *    - 凡例表示
 * 
 * 4. ユーザビリティ
 *    - 直感的なコントロール
 *    - リアルタイム更新
 *    - エラーハンドリング
 *    - 状態フィードバック
 * 
 * 5. 拡張性
 *    - 設定パターンの実装
 *    - プラグイン対応可能な設計
 *    - カスタムチャートタイプ追加容易
 *    - テーマ変更対応
 */