import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EventOptimizationExample extends JFrame {
    private JPanel drawPanel;
    private JLabel statusLabel;
    private JTextArea logArea;
    private Point currentMousePos;
    private Timer debounceTimer;
    private long lastThrottleTime = 0;
    private static final int DEBOUNCE_DELAY = 300; // ミリ秒
    private static final int THROTTLE_DELAY = 100; // ミリ秒
    
    public EventOptimizationExample() {
        setTitle("イベント処理の最適化");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 描画パネル
        drawPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (currentMousePos != null) {
                    g.setColor(Color.RED);
                    g.fillOval(currentMousePos.x - 5, currentMousePos.y - 5, 10, 10);
                }
            }
        };
        drawPanel.setBackground(Color.WHITE);
        drawPanel.setPreferredSize(new Dimension(400, 300));
        drawPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // ステータスラベル
        statusLabel = new JLabel("マウスを動かしてください");
        
        // ログエリア
        logArea = new JTextArea(8, 40);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        
        // マウス移動イベント（位置の表示は毎回、描画とログはデバウンスとスロットルで間引く）
        drawPanel.addMouseMotionListener(new MouseMotionAdapter() {
            private int eventCount = 0;
            
            @Override
            public void mouseMoved(MouseEvent e) {
                eventCount++;
                statusLabel.setText(String.format("マウス位置: (%d, %d) - イベント数: %d", 
                    e.getX(), e.getY(), eventCount));
                
                // デバウンス処理
                debounceMouseMove(e.getPoint());
                
                // スロットル処理
                throttleMouseMove(e.getPoint());
            }
        });
        
        // デバウンスタイマーの初期化
        debounceTimer = new Timer(DEBOUNCE_DELAY, e -> {
            log("デバウンス: マウス移動が停止しました - " + currentMousePos);
        });
        debounceTimer.setRepeats(false);
        
        // コントロールパネル
        JPanel controlPanel = new JPanel();
        JButton clearButton = new JButton("ログクリア");
        clearButton.addActionListener(e -> logArea.setText(""));
        controlPanel.add(clearButton);
        
        add(drawPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.EAST);
        add(controlPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    // デバウンス: 最後のイベントから一定時間後に処理
    private void debounceMouseMove(Point point) {
        currentMousePos = point;
        debounceTimer.restart();
    }
    
    // スロットル: 一定間隔で処理
    private void throttleMouseMove(Point point) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastThrottleTime >= THROTTLE_DELAY) {
            lastThrottleTime = currentTime;
            drawPanel.repaint();
            log("スロットル: 描画更新 - " + point);
        }
    }
    
    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EventOptimizationExample().setVisible(true);
        });
    }
}
