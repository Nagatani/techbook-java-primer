/**
 * 第18章 GUI イベント処理応用
 * 演習課題: MouseTracker.java
 * 
 * 【課題概要】
 * マウスの詳細な動作を追跡・分析できるアプリケーションを作成してください。
 * 
 * 【要求仕様】
 * 1. リアルタイムマウス座標表示
 * 2. マウスの移動軌跡の記録と表示
 * 3. クリック位置のマーキング（左、右、中央ボタン別）
 * 4. ダブルクリック・トリプルクリックの検出
 * 5. マウスドラッグ操作の軌跡表示
 * 6. マウス移動速度の計算と表示
 * 7. マウスホバー時間の測定
 * 8. マウスイベントログの記録と出力
 * 9. マウスジェスチャー認識機能
 * 10. 統計情報の表示（総移動距離、クリック数等）
 * 
 * 【学習ポイント】
 * - MouseListener、MouseMotionListener の詳細な実装
 * - MouseWheelListener によるスクロールイベント処理
 * - Timer クラスによる時間管理とタイマー処理
 * - Point クラスと座標計算
 * - Graphics2D による軌跡の描画
 * - 数学的計算（距離、速度、角度）
 * - データ構造（List、Queue）によるイベント履歴管理
 * 
 * 【実装のヒント】
 * 1. ArrayList で座標履歴を管理
 * 2. System.currentTimeMillis() で時間測定
 * 3. Point.distance() で移動距離計算
 * 4. Timer で定期的な統計更新
 * 5. Custom painting で軌跡を描画
 * 
 * 【よくある間違いと対策】
 * - 大量のマウスイベントによるメモリ不足
 *   → 古いデータの定期的な削除、CircularBuffer の使用
 * - 描画の負荷による動作の重さ
 *   → 描画の最適化、不要な再描画の抑制
 * - 座標計算の精度問題
 *   → double 型での計算、適切な丸め処理
 * - イベントタイミングの同期問題
 *   → SwingUtilities.invokeLater() での適切な処理
 * 
 * 【段階的な実装指針】
 * 1. 基本的なマウス座標表示機能
 * 2. クリックイベントの記録と表示
 * 3. 移動軌跡の記録と描画機能
 * 4. 速度計算と統計情報の追加
 * 5. ジェスチャー認識機能の実装
 * 6. ログ機能とデータ出力機能
 * 7. UI の改善と詳細設定機能
 * 8. パフォーマンス最適化
 */

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MouseTracker extends JFrame {
    // トラッキングデータ
    private List<TrackPoint> trackPoints;
    private Queue<MouseEvent> eventQueue;
    private Map<Integer, ClickInfo> clickCounts;
    private Point lastPosition;
    private long lastMoveTime;
    private boolean isTracking = true;
    
    // 統計データ
    private double totalDistance = 0;
    private int totalClicks = 0;
    private long sessionStartTime;
    private double currentSpeed = 0;
    private Point hoverStart;
    private long hoverStartTime;
    
    // UI コンポーネント
    private TrackingPanel trackingPanel;
    private JLabel coordinateLabel;
    private JLabel speedLabel;
    private JLabel distanceLabel;
    private JLabel clickLabel;
    private JLabel sessionLabel;
    private JTextArea eventLogArea;
    private JSlider trailLengthSlider;
    private JCheckBox showTrailCheckBox;
    private JCheckBox showClicksCheckBox;
    private JCheckBox showSpeedCheckBox;
    private JButton clearButton;
    private JButton saveButton;
    private JButton pauseButton;
    
    // 設定
    private int maxTrailLength = 1000;
    private boolean showTrail = true;
    private boolean showClicks = true;
    private boolean showSpeed = true;
    private Color trailColor = Color.BLUE;
    private Color clickColor = Color.RED;
    
    // タイマー
    private Timer statisticsTimer;
    private Timer hoverTimer;
    
    // ジェスチャー認識
    private GestureRecognizer gestureRecognizer;
    
    public MouseTracker() {
        trackPoints = Collections.synchronizedList(new ArrayList<>());
        eventQueue = new ConcurrentLinkedQueue<>();
        clickCounts = new HashMap<>();
        sessionStartTime = System.currentTimeMillis();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupTimers();
        setupWindow();
        
        gestureRecognizer = new GestureRecognizer();
        
        resetStatistics();
    }
    
    private void initializeComponents() {
        // トラッキングパネル
        trackingPanel = new TrackingPanel();
        trackingPanel.setPreferredSize(new Dimension(600, 400));
        trackingPanel.setBorder(BorderFactory.createTitledBorder("マウストラッキングエリア"));
        
        // 情報表示ラベル
        coordinateLabel = new JLabel("座標: (0, 0)");
        speedLabel = new JLabel("速度: 0.0 px/s");
        distanceLabel = new JLabel("総移動距離: 0.0 px");
        clickLabel = new JLabel("クリック数: 0");
        sessionLabel = new JLabel("セッション時間: 00:00:00");
        
        // イベントログエリア
        eventLogArea = new JTextArea(8, 30);
        eventLogArea.setEditable(false);
        eventLogArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        
        // コントロールパネル
        trailLengthSlider = new JSlider(10, 5000, maxTrailLength);
        trailLengthSlider.setMajorTickSpacing(1000);
        trailLengthSlider.setMinorTickSpacing(100);
        trailLengthSlider.setPaintTicks(true);
        trailLengthSlider.setPaintLabels(true);
        
        showTrailCheckBox = new JCheckBox("軌跡表示", showTrail);
        showClicksCheckBox = new JCheckBox("クリック表示", showClicks);
        showSpeedCheckBox = new JCheckBox("速度表示", showSpeed);
        
        clearButton = new JButton("クリア");
        saveButton = new JButton("保存");
        pauseButton = new JButton("一時停止");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // メインパネル
        add(trackingPanel, BorderLayout.CENTER);
        
        // 情報パネル（上部）
        JPanel infoPanel = new JPanel(new GridLayout(1, 5, 10, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("リアルタイム情報"));
        infoPanel.add(coordinateLabel);
        infoPanel.add(speedLabel);
        infoPanel.add(distanceLabel);
        infoPanel.add(clickLabel);
        infoPanel.add(sessionLabel);
        
        add(infoPanel, BorderLayout.NORTH);
        
        // サイドパネル（右側）
        JPanel sidePanel = createSidePanel();
        add(sidePanel, BorderLayout.EAST);
        
        // コントロールパネル（下部）
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createSidePanel() {
        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setPreferredSize(new Dimension(300, 0));
        
        // 設定パネル
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBorder(BorderFactory.createTitledBorder("設定"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        settingsPanel.add(new JLabel("軌跡長:"), gbc);
        gbc.gridy = 1;
        settingsPanel.add(trailLengthSlider, gbc);
        
        gbc.gridy = 2; gbc.gridwidth = 1;
        settingsPanel.add(showTrailCheckBox, gbc);
        gbc.gridx = 1;
        settingsPanel.add(showClicksCheckBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        settingsPanel.add(showSpeedCheckBox, gbc);
        
        // 色選択ボタン
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton trailColorButton = new JButton("軌跡色");
        trailColorButton.setBackground(trailColor);
        trailColorButton.setOpaque(true);
        trailColorButton.addActionListener(e -> selectTrailColor());
        settingsPanel.add(trailColorButton, gbc);
        
        // イベントログパネル
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("イベントログ"));
        logPanel.add(new JScrollPane(eventLogArea), BorderLayout.CENTER);
        
        JButton clearLogButton = new JButton("ログクリア");
        clearLogButton.addActionListener(e -> eventLogArea.setText(""));
        logPanel.add(clearLogButton, BorderLayout.SOUTH);
        
        sidePanel.add(settingsPanel, BorderLayout.NORTH);
        sidePanel.add(logPanel, BorderLayout.CENTER);
        
        return sidePanel;
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        controlPanel.add(clearButton);
        controlPanel.add(saveButton);
        controlPanel.add(pauseButton);
        
        // ジェスチャーボタン
        JButton gestureButton = new JButton("ジェスチャー設定");
        gestureButton.addActionListener(e -> showGestureSettings());
        controlPanel.add(gestureButton);
        
        // 統計ボタン
        JButton statsButton = new JButton("詳細統計");
        statsButton.addActionListener(e -> showDetailedStatistics());
        controlPanel.add(statsButton);
        
        return controlPanel;
    }
    
    private void setupEventHandlers() {
        // トラッキングパネルのマウスイベント
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (isTracking) {
                    handleMouseMove(e);
                }
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isTracking) {
                    handleMouseMove(e);
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isTracking) {
                    handleMouseClick(e);
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (isTracking) {
                    logEvent("マウス押下: " + getButtonName(e.getButton()) + " at " + e.getPoint());
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isTracking) {
                    logEvent("マウス離上: " + getButtonName(e.getButton()) + " at " + e.getPoint());
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                hoverStart = e.getPoint();
                hoverStartTime = System.currentTimeMillis();
                logEvent("マウス領域進入: " + e.getPoint());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (hoverStart != null) {
                    long hoverTime = System.currentTimeMillis() - hoverStartTime;
                    logEvent("マウス領域退出: " + e.getPoint() + " (滞在時間: " + hoverTime + "ms)");
                }
                hoverStart = null;
            }
        };
        
        trackingPanel.addMouseListener(mouseHandler);
        trackingPanel.addMouseMotionListener(mouseHandler);
        
        // マウスホイールイベント
        trackingPanel.addMouseWheelListener(e -> {
            if (isTracking) {
                String direction = e.getWheelRotation() > 0 ? "下" : "上";
                logEvent("マウスホイール: " + direction + " (" + e.getWheelRotation() + ") at " + e.getPoint());
            }
        });
        
        // コントロールイベント
        trailLengthSlider.addChangeListener(e -> {
            maxTrailLength = trailLengthSlider.getValue();
            trimTrackPoints();
        });
        
        showTrailCheckBox.addActionListener(e -> {
            showTrail = showTrailCheckBox.isSelected();
            trackingPanel.repaint();
        });
        
        showClicksCheckBox.addActionListener(e -> {
            showClicks = showClicksCheckBox.isSelected();
            trackingPanel.repaint();
        });
        
        showSpeedCheckBox.addActionListener(e -> {
            showSpeed = showSpeedCheckBox.isSelected();
        });
        
        clearButton.addActionListener(e -> clearAllData());
        saveButton.addActionListener(e -> saveTrackingData());
        pauseButton.addActionListener(e -> toggleTracking());
    }
    
    private void setupTimers() {
        // 統計更新タイマー
        statisticsTimer = new Timer(100, e -> updateStatistics());
        statisticsTimer.start();
        
        // ホバータイマー（ホバー時間の詳細監視用）
        hoverTimer = new Timer(1000, e -> {
            if (hoverStart != null) {
                long hoverTime = System.currentTimeMillis() - hoverStartTime;
                if (hoverTime > 5000) { // 5秒以上のホバー
                    logEvent("長時間ホバー検出: " + hoverStart + " (" + hoverTime + "ms)");
                }
            }
        });
        hoverTimer.start();
    }
    
    private void setupWindow() {
        setTitle("Advanced Mouse Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }
    
    private void handleMouseMove(MouseEvent e) {
        Point currentPoint = e.getPoint();
        long currentTime = System.currentTimeMillis();
        
        if (lastPosition != null) {
            double distance = lastPosition.distance(currentPoint);
            totalDistance += distance;
            
            // 速度計算
            if (lastMoveTime > 0) {
                double timeDiff = (currentTime - lastMoveTime) / 1000.0; // 秒
                if (timeDiff > 0) {
                    currentSpeed = distance / timeDiff; // ピクセル/秒
                }
            }
            
            // ジェスチャー認識
            gestureRecognizer.addPoint(currentPoint, currentTime);
        }
        
        // トラックポイントの追加
        synchronized (trackPoints) {
            trackPoints.add(new TrackPoint(currentPoint, currentTime, currentSpeed));
            trimTrackPoints();
        }
        
        lastPosition = currentPoint;
        lastMoveTime = currentTime;
        
        // 座標表示の更新
        updateCoordinateDisplay(currentPoint);
        
        // 描画更新
        trackingPanel.repaint();
    }
    
    private void handleMouseClick(MouseEvent e) {
        totalClicks++;
        int button = e.getButton();
        int clickCount = e.getClickCount();
        
        ClickInfo clickInfo = new ClickInfo(e.getPoint(), System.currentTimeMillis(), 
                                          button, clickCount);
        clickCounts.put(clickCounts.size(), clickInfo);
        
        String clickType = getClickType(clickCount);
        String buttonName = getButtonName(button);
        
        logEvent(clickType + " (" + buttonName + ") at " + e.getPoint());
        
        // 特殊クリックの検出
        if (clickCount >= 3) {
            logEvent("トリプルクリック以上検出!");
        }
        
        trackingPanel.repaint();
    }
    
    private void updateStatistics() {
        // セッション時間の更新
        long sessionTime = System.currentTimeMillis() - sessionStartTime;
        sessionLabel.setText("セッション時間: " + formatTime(sessionTime));
        
        // 統計情報の更新
        DecimalFormat df = new DecimalFormat("#0.0");
        speedLabel.setText("速度: " + df.format(currentSpeed) + " px/s");
        distanceLabel.setText("総移動距離: " + df.format(totalDistance) + " px");
        clickLabel.setText("クリック数: " + totalClicks);
    }
    
    private void updateCoordinateDisplay(Point point) {
        coordinateLabel.setText("座標: (" + point.x + ", " + point.y + ")");
    }
    
    private void trimTrackPoints() {
        synchronized (trackPoints) {
            while (trackPoints.size() > maxTrailLength) {
                trackPoints.remove(0);
            }
        }
    }
    
    private void logEvent(String message) {
        String timestamp = new Date().toString();
        String logEntry = "[" + timestamp + "] " + message + "\n";
        
        SwingUtilities.invokeLater(() -> {
            eventLogArea.append(logEntry);
            eventLogArea.setCaretPosition(eventLogArea.getDocument().getLength());
        });
        
        // ログサイズの制限
        if (eventLogArea.getLineCount() > 1000) {
            SwingUtilities.invokeLater(() -> {
                try {
                    int endOffset = eventLogArea.getLineEndOffset(100);
                    eventLogArea.replaceRange("", 0, endOffset);
                } catch (Exception ex) {
                    // エラー時は全クリア
                    eventLogArea.setText("");
                }
            });
        }
    }
    
    private String getButtonName(int button) {
        switch (button) {
            case MouseEvent.BUTTON1: return "左";
            case MouseEvent.BUTTON2: return "中央";
            case MouseEvent.BUTTON3: return "右";
            default: return "不明(" + button + ")";
        }
    }
    
    private String getClickType(int clickCount) {
        switch (clickCount) {
            case 1: return "シングルクリック";
            case 2: return "ダブルクリック";
            case 3: return "トリプルクリック";
            default: return clickCount + "回クリック";
        }
    }
    
    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        return String.format("%02d:%02d:%02d", hours % 24, minutes % 60, seconds % 60);
    }
    
    private void selectTrailColor() {
        Color newColor = JColorChooser.showDialog(this, "軌跡の色を選択", trailColor);
        if (newColor != null) {
            trailColor = newColor;
            trackingPanel.repaint();
        }
    }
    
    private void clearAllData() {
        synchronized (trackPoints) {
            trackPoints.clear();
        }
        clickCounts.clear();
        resetStatistics();
        eventLogArea.setText("");
        trackingPanel.repaint();
        
        logEvent("全データをクリアしました");
    }
    
    private void resetStatistics() {
        totalDistance = 0;
        totalClicks = 0;
        currentSpeed = 0;
        sessionStartTime = System.currentTimeMillis();
        lastPosition = null;
        lastMoveTime = 0;
    }
    
    private void toggleTracking() {
        isTracking = !isTracking;
        pauseButton.setText(isTracking ? "一時停止" : "再開");
        logEvent(isTracking ? "トラッキング再開" : "トラッキング一時停止");
    }
    
    private void saveTrackingData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("mouse_tracking_" + System.currentTimeMillis() + ".csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("Timestamp,X,Y,Speed\n");
                
                synchronized (trackPoints) {
                    for (TrackPoint point : trackPoints) {
                        writer.write(point.timestamp + "," + point.point.x + "," + 
                                   point.point.y + "," + point.speed + "\n");
                    }
                }
                
                JOptionPane.showMessageDialog(this, "トラッキングデータを保存しました: " + file.getName());
                logEvent("データ保存: " + file.getAbsolutePath());
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "保存に失敗しました: " + ex.getMessage(),
                    "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showGestureSettings() {
        JDialog dialog = new JDialog(this, "ジェスチャー設定", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("ジェスチャー認識設定（実装例）"), BorderLayout.CENTER);
        
        JButton closeButton = new JButton("閉じる");
        closeButton.addActionListener(e -> dialog.setVisible(false));
        panel.add(closeButton, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showDetailedStatistics() {
        JDialog dialog = new JDialog(this, "詳細統計", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== マウストラッキング統計 ===\n\n");
        stats.append("セッション時間: ").append(formatTime(System.currentTimeMillis() - sessionStartTime)).append("\n");
        stats.append("総移動距離: ").append(String.format("%.2f", totalDistance)).append(" ピクセル\n");
        stats.append("総クリック数: ").append(totalClicks).append("\n");
        stats.append("記録ポイント数: ").append(trackPoints.size()).append("\n");
        stats.append("現在の速度: ").append(String.format("%.2f", currentSpeed)).append(" px/s\n\n");
        
        // クリック統計
        stats.append("=== クリック統計 ===\n");
        Map<Integer, Integer> buttonCounts = new HashMap<>();
        Map<Integer, Integer> clickTypeCounts = new HashMap<>();
        
        for (ClickInfo click : clickCounts.values()) {
            buttonCounts.put(click.button, buttonCounts.getOrDefault(click.button, 0) + 1);
            clickTypeCounts.put(click.clickCount, clickTypeCounts.getOrDefault(click.clickCount, 0) + 1);
        }
        
        for (Map.Entry<Integer, Integer> entry : buttonCounts.entrySet()) {
            stats.append(getButtonName(entry.getKey())).append("ボタン: ").append(entry.getValue()).append("回\n");
        }
        
        stats.append("\n");
        for (Map.Entry<Integer, Integer> entry : clickTypeCounts.entrySet()) {
            stats.append(getClickType(entry.getKey())).append(": ").append(entry.getValue()).append("回\n");
        }
        
        statsArea.setText(stats.toString());
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(statsArea), BorderLayout.CENTER);
        
        JButton closeButton = new JButton("閉じる");
        closeButton.addActionListener(e -> dialog.setVisible(false));
        panel.add(closeButton, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    // トラッキングパネルクラス
    private class TrackingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            
            // アンチエイリアス設定
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 背景グリッドの描画
            drawGrid(g2d);
            
            // 軌跡の描画
            if (showTrail && !trackPoints.isEmpty()) {
                drawTrail(g2d);
            }
            
            // クリック位置の描画
            if (showClicks) {
                drawClicks(g2d);
            }
            
            // 速度情報の描画
            if (showSpeed && lastPosition != null) {
                drawSpeedInfo(g2d);
            }
            
            g2d.dispose();
        }
        
        private void drawGrid(Graphics2D g2d) {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.setStroke(new BasicStroke(0.5f));
            
            int gridSize = 50;
            for (int x = 0; x < getWidth(); x += gridSize) {
                g2d.drawLine(x, 0, x, getHeight());
            }
            for (int y = 0; y < getHeight(); y += gridSize) {
                g2d.drawLine(0, y, getWidth(), y);
            }
        }
        
        private void drawTrail(Graphics2D g2d) {
            synchronized (trackPoints) {
                if (trackPoints.size() < 2) return;
                
                Path2D path = new Path2D.Double();
                boolean first = true;
                
                for (TrackPoint tp : trackPoints) {
                    if (first) {
                        path.moveTo(tp.point.x, tp.point.y);
                        first = false;
                    } else {
                        path.lineTo(tp.point.x, tp.point.y);
                    }
                }
                
                // グラデーション効果
                g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                for (int i = 1; i < trackPoints.size(); i++) {
                    TrackPoint prev = trackPoints.get(i - 1);
                    TrackPoint curr = trackPoints.get(i);
                    
                    // 透明度を距離に応じて調整
                    float alpha = (float) i / trackPoints.size();
                    Color segmentColor = new Color(trailColor.getRed(), trailColor.getGreen(), 
                                                 trailColor.getBlue(), (int) (255 * alpha));
                    g2d.setColor(segmentColor);
                    g2d.drawLine(prev.point.x, prev.point.y, curr.point.x, curr.point.y);
                }
            }
        }
        
        private void drawClicks(Graphics2D g2d) {
            for (ClickInfo click : clickCounts.values()) {
                Color color = getClickColor(click.button);
                g2d.setColor(color);
                
                int size = click.clickCount * 6 + 4;
                g2d.fillOval(click.point.x - size / 2, click.point.y - size / 2, size, size);
                
                // クリック数の表示
                if (click.clickCount > 1) {
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
                    String text = String.valueOf(click.clickCount);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textX = click.point.x - fm.stringWidth(text) / 2;
                    int textY = click.point.y + fm.getAscent() / 2;
                    g2d.drawString(text, textX, textY);
                }
            }
        }
        
        private void drawSpeedInfo(Graphics2D g2d) {
            if (currentSpeed > 0) {
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
                String speedText = String.format("%.1f px/s", currentSpeed);
                g2d.drawString(speedText, lastPosition.x + 10, lastPosition.y - 10);
            }
        }
        
        private Color getClickColor(int button) {
            switch (button) {
                case MouseEvent.BUTTON1: return Color.RED;
                case MouseEvent.BUTTON2: return Color.BLUE;
                case MouseEvent.BUTTON3: return Color.GREEN;
                default: return Color.GRAY;
            }
        }
    }
    
    // データクラス
    private static class TrackPoint {
        final Point point;
        final long timestamp;
        final double speed;
        
        TrackPoint(Point point, long timestamp, double speed) {
            this.point = new Point(point);
            this.timestamp = timestamp;
            this.speed = speed;
        }
    }
    
    private static class ClickInfo {
        final Point point;
        final long timestamp;
        final int button;
        final int clickCount;
        
        ClickInfo(Point point, long timestamp, int button, int clickCount) {
            this.point = new Point(point);
            this.timestamp = timestamp;
            this.button = button;
            this.clickCount = clickCount;
        }
    }
    
    // ジェスチャー認識クラス（簡略化）
    private class GestureRecognizer {
        private List<Point> gesturePoints = new ArrayList<>();
        private long lastGestureTime = 0;
        
        public void addPoint(Point point, long timestamp) {
            // 簡単なジェスチャー認識ロジック
            gesturePoints.add(new Point(point));
            
            // 古いポイントを削除
            if (timestamp - lastGestureTime > 2000) {
                gesturePoints.clear();
            }
            
            lastGestureTime = timestamp;
            
            // ジェスチャーパターンの検出（例：円、直線）
            if (gesturePoints.size() > 20) {
                analyzeGesture();
                gesturePoints.clear();
            }
        }
        
        private void analyzeGesture() {
            // 簡単なジェスチャー認識（実装例）
            if (isCircularGesture()) {
                logEvent("円形ジェスチャーを検出");
            } else if (isLinearGesture()) {
                logEvent("直線ジェスチャーを検出");
            }
        }
        
        private boolean isCircularGesture() {
            // 円形ジェスチャーの簡単な検出ロジック
            return gesturePoints.size() > 15;
        }
        
        private boolean isLinearGesture() {
            // 直線ジェスチャーの簡単な検出ロジック
            return false;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new MouseTracker().setVisible(true);
        });
    }
}
