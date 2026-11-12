/**
 * 第20章 課題4: データのグラフ（解答例）
 *
 * ラベルと値の組を、折れ線グラフ・棒グラフ・円グラフで描きます。
 * 描く位置はパネルの幅と高さと最大の値から計算するので、ウィンドウの大きさを変えるとグラフも合わせて描き直されます。
 * 伸びて現れるアニメーションは、javax.swing.Timer（第18章）で進み具合を少しずつ上げて描き直します。
 */

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;

public class DataVisualization extends JFrame {

    // グラフの色。データの順に使い、足りなくなったら最初に戻る
    private static final Color[] PALETTE = {
        new Color(0x1f77b4), new Color(0xff7f0e), new Color(0x2ca02c), new Color(0xd62728),
        new Color(0x9467bd), new Color(0x8c564b), new Color(0xe377c2), new Color(0x17becf)
    };
    // グラフの周りの余白（ピクセル）
    private static final int MARGIN = 50;

    private ChartPanel chartPanel;
    private JComboBox<ChartType> chartTypeCombo;
    private JButton addDataButton, exportButton, animateButton;
    private JSlider animationSpeedSlider;
    private Timer animationTimer;

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

    // データの1つの点（ラベル・値・色）
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

        public String getLabel() { return label; }
        public double getValue() { return value; }
        public Color getColor() { return color; }
        public void setValue(double value) { this.value = value; }

        // 解答例では色を指定するコンストラクターだけを使う（起動のたびに色が変わらないように）
        private Color generateRandomColor() {
            Random random = new Random();
            return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }
    }

    // グラフを描くパネル
    private class ChartPanel extends JPanel {
        private List<DataPoint> data;
        private ChartType chartType;
        private boolean animationEnabled;
        // 0.0（何も描かない）から1.0（全部描く）まで。アニメーション中に少しずつ上がる
        private double animationProgress;

        public ChartPanel() {
            this.data = new ArrayList<>();
            this.chartType = ChartType.LINE_CHART;
            this.animationEnabled = true;
            this.animationProgress = 0.0;
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(700, 450));
        }

        public void addData(DataPoint point) {
            data.add(point);
            repaint();
        }

        public List<DataPoint> getData() {
            return data;
        }

        public void setChartType(ChartType chartType) {
            this.chartType = chartType;
            repaint();
        }

        public void setAnimationProgress(double progress) {
            this.animationProgress = Math.min(1.0, progress);
            repaint();
        }

        public double getAnimationProgress() {
            return animationProgress;
        }

        // アニメーションを使わないときは、いつも全部を描く
        private double progress() {
            return animationEnabled ? animationProgress : 1.0;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
            switch (chartType) {
                case LINE_CHART -> drawLineChart(g2d);
                case BAR_CHART -> drawBarChart(g2d);
                case PIE_CHART -> drawPieChart(g2d);
            }
        }

        // パネルの中央に文字を描く。FontMetricsで文字の幅を測って、左端の位置を決める
        private void drawNoDataMessage(Graphics2D g2d) {
            String message = "データがありません";
            g2d.setColor(Color.GRAY);
            g2d.setFont(getFont().deriveFont(18f));
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(message)) / 2;
            int y = (getHeight() + fm.getAscent()) / 2;
            g2d.drawString(message, x, y);
        }

        private double maxValue() {
            double max = 0;
            for (DataPoint point : data) {
                max = Math.max(max, point.getValue());
            }
            // 第12章のStream APIを使うと、次のように書ける
            // double max = data.stream().mapToDouble(DataPoint::getValue).max().orElse(0);
            return max == 0 ? 1 : max;
        }

        // 縦軸と横軸、縦軸の目盛り（最大値を4等分）を描く
        private void drawAxes(Graphics2D g2d, double max) {
            int left = MARGIN;
            int bottom = getHeight() - MARGIN;
            int top = MARGIN;
            int right = getWidth() - MARGIN;
            g2d.setColor(Color.BLACK);
            g2d.drawLine(left, top, left, bottom);
            g2d.drawLine(left, bottom, right, bottom);
            g2d.setFont(getFont().deriveFont(11f));
            for (int i = 0; i <= 4; i++) {
                int y = bottom - (bottom - top) * i / 4;
                g2d.setColor(new Color(230, 230, 230));
                g2d.drawLine(left + 1, y, right, y);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawString(String.valueOf(Math.round(max * i / 4)), 5, y + 4);
            }
        }

        // 値をパネルの縦の位置に直す。Javaの座標は下に行くほどyが大きいので、下端から引く
        private double valueToY(double value, double max) {
            int plotHeight = getHeight() - 2 * MARGIN;
            return getHeight() - MARGIN - plotHeight * (value / max) * progress();
        }

        private void drawLineChart(Graphics2D g2d) {
            double max = maxValue();
            drawAxes(g2d, max);
            int plotWidth = getWidth() - 2 * MARGIN;
            int n = data.size();
            double step = n > 1 ? (double) plotWidth / (n - 1) : 0;

            double prevX = 0, prevY = 0;
            g2d.setStroke(new BasicStroke(2f));
            for (int i = 0; i < n; i++) {
                DataPoint point = data.get(i);
                double x = MARGIN + (n > 1 ? step * i : plotWidth / 2.0);
                double y = valueToY(point.getValue(), max);
                if (i > 0) {
                    g2d.setColor(PALETTE[0]);
                    g2d.draw(new Line2D.Double(prevX, prevY, x, y));
                }
                g2d.setColor(point.getColor());
                g2d.fillOval((int) x - 4, (int) y - 4, 8, 8);
                drawLabelUnderAxis(g2d, point.getLabel(), x);
                prevX = x;
                prevY = y;
            }
        }

        private void drawBarChart(Graphics2D g2d) {
            double max = maxValue();
            drawAxes(g2d, max);
            int plotWidth = getWidth() - 2 * MARGIN;
            double slot = (double) plotWidth / data.size();
            double barWidth = slot * 0.6;
            int bottom = getHeight() - MARGIN;
            for (int i = 0; i < data.size(); i++) {
                DataPoint point = data.get(i);
                double x = MARGIN + slot * i + (slot - barWidth) / 2;
                double y = valueToY(point.getValue(), max);
                g2d.setColor(point.getColor());
                g2d.fill(new Rectangle2D.Double(x, y, barWidth, bottom - y));
                drawLabelUnderAxis(g2d, point.getLabel(), x + barWidth / 2);
            }
        }

        private void drawPieChart(Graphics2D g2d) {
            double total = 0;
            for (DataPoint point : data) {
                total += point.getValue();
            }
            if (total <= 0) {
                drawNoDataMessage(g2d);
                return;
            }
            // 円は、パネルの短いほうの辺に合わせた正方形の中に描く
            int size = Math.min(getWidth(), getHeight()) - 2 * MARGIN;
            int x = MARGIN;
            int y = (getHeight() - size) / 2;
            // 3時の位置から反時計回りに角度が増えるので、90度（12時）から時計回り（負の角度）に描く
            double start = 90;
            for (DataPoint point : data) {
                double extent = -360 * point.getValue() / total * progress();
                g2d.setColor(point.getColor());
                g2d.fill(new Arc2D.Double(x, y, size, size, start, extent, Arc2D.PIE));
                start += extent;
            }
            drawLegend(g2d, x + size + 30, y + 20, total);
        }

        private void drawLabelUnderAxis(Graphics2D g2d, String label, double centerX) {
            g2d.setColor(Color.DARK_GRAY);
            g2d.setFont(getFont().deriveFont(12f));
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(label, (int) (centerX - fm.stringWidth(label) / 2.0), getHeight() - MARGIN + 18);
        }

        // 円グラフの凡例（色・ラベル・割合）
        private void drawLegend(Graphics2D g2d, int x, int y, double total) {
            g2d.setFont(getFont().deriveFont(12f));
            for (int i = 0; i < data.size(); i++) {
                DataPoint point = data.get(i);
                int rowY = y + i * 22;
                g2d.setColor(point.getColor());
                g2d.fillRect(x, rowY - 11, 14, 14);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawString(String.format("%s（%.1f%%）", point.getLabel(), point.getValue() / total * 100),
                        x + 20, rowY);
            }
        }
    }

    public DataVisualization() {
        setTitle("データのグラフ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeComponents();
        setupLayout();
        setupEventListeners();
        generateSampleData();
        startAnimation();

        pack();
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        chartPanel = new ChartPanel();
        chartTypeCombo = new JComboBox<>(ChartType.values());
        addDataButton = new JButton("データ追加");
        exportButton = new JButton("画像で保存");
        animateButton = new JButton("アニメーション開始");
        // 1回の描き直しで進む量（1〜10%）
        animationSpeedSlider = new JSlider(1, 10, 3);
        // 約60分の1秒ごとに進み具合を上げる。javax.swing.TimerはEDTで動くので、画面を直接操作してよい
        animationTimer = new Timer(16, e -> {
            double step = animationSpeedSlider.getValue() / 100.0;
            chartPanel.setAnimationProgress(chartPanel.getAnimationProgress() + step);
            if (chartPanel.getAnimationProgress() >= 1.0) {
                ((Timer) e.getSource()).stop();
            }
        });
    }

    private void setupLayout() {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("種類:"));
        controls.add(chartTypeCombo);
        controls.add(addDataButton);
        controls.add(animateButton);
        controls.add(new JLabel("速さ:"));
        controls.add(animationSpeedSlider);
        controls.add(exportButton);

        add(controls, BorderLayout.NORTH);
        add(chartPanel, BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        chartTypeCombo.addActionListener(e -> {
            chartPanel.setChartType((ChartType) chartTypeCombo.getSelectedItem());
        });
        addDataButton.addActionListener(e -> showAddDataDialog());
        animateButton.addActionListener(e -> startAnimation());
        exportButton.addActionListener(e -> exportChart());
    }

    // 進み具合を0に戻して、タイマーで少しずつ伸ばす
    private void startAnimation() {
        animationTimer.stop();
        chartPanel.setAnimationProgress(0.0);
        animationTimer.start();
    }

    private void showAddDataDialog() {
        JTextField labelField = new JTextField();
        JTextField valueField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("ラベル:"));
        panel.add(labelField);
        panel.add(new JLabel("値（0以上）:"));
        panel.add(valueField);

        int result = JOptionPane.showConfirmDialog(this, panel, "データ追加", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        String label = labelField.getText().trim();
        try {
            double value = Double.parseDouble(valueField.getText().trim());
            if (label.isEmpty() || value < 0) {
                throw new NumberFormatException();
            }
            Color color = PALETTE[chartPanel.getData().size() % PALETTE.length];
            chartPanel.addData(new DataPoint(label, value, color));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ラベルと、0以上の数の値を入力してください",
                    "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    // パネルと同じ大きさの画像にグラフを描き、PNGファイルに保存する
    private void exportChart() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PNG画像", "png"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".png")) {
            file = new File(file.getParentFile(), file.getName() + ".png");
        }
        BufferedImage image = new BufferedImage(chartPanel.getWidth(), chartPanel.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        try {
            // paintは、画面に描くときと同じ処理で画像に描く
            chartPanel.paint(g2d);
        } finally {
            g2d.dispose();
        }
        try {
            ImageIO.write(image, "png", file);
            JOptionPane.showMessageDialog(this, file.getName() + "に保存しました");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "保存できませんでした: " + e.getMessage(),
                    "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 見本のデータ（月ごとの売上）。値と色は決めておき、起動のたびに同じグラフになるようにする
    private void generateSampleData() {
        String[] months = {"1月", "2月", "3月", "4月", "5月", "6月"};
        double[] values = {120, 150, 90, 180, 160, 210};
        for (int i = 0; i < months.length; i++) {
            chartPanel.addData(new DataPoint(months[i], values[i], PALETTE[i % PALETTE.length]));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DataVisualization().setVisible(true));
    }
}
