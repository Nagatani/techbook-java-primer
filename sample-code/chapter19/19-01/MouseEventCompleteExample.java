import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MouseEventCompleteExample extends JFrame {
    private JTextArea logArea;
    private JPanel drawPanel;
    private Point startPoint;
    private Point currentPoint;
    
    public MouseEventCompleteExample() {
        setTitle("マウスイベント完全例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 描画パネル
        drawPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (startPoint != null && currentPoint != null) {
                    g.setColor(Color.BLUE);
                    g.drawLine(startPoint.x, startPoint.y, currentPoint.x, currentPoint.y);
                }
            }
        };
        drawPanel.setBackground(Color.WHITE);
        drawPanel.setPreferredSize(new Dimension(400, 300));
        drawPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // ログエリア
        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        
        // マウスリスナーの追加
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String button = getButtonName(e.getButton());
                int clickCount = e.getClickCount();
                log("クリック: " + button + " (回数: " + clickCount + ") at " + 
                    e.getPoint());
                
                // ダブルクリックの検出
                if (clickCount == 2) {
                    log("ダブルクリック検出！");
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                log("マウス押下: " + getButtonName(e.getButton()) + " at " + startPoint);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                log("マウス解放: " + getButtonName(e.getButton()) + " at " + e.getPoint());
                startPoint = null;
                currentPoint = null;
                drawPanel.repaint();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                log("マウスがパネルに入りました");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                drawPanel.setCursor(Cursor.getDefaultCursor());
                log("マウスがパネルから出ました");
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                currentPoint = e.getPoint();
                drawPanel.repaint();
                log("ドラッグ中: " + currentPoint);
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                // 頻繁に発生するので通常はログしない
            }
            
            private String getButtonName(int button) {
                return switch (button) {
                    case MouseEvent.BUTTON1 -> "左ボタン";
                    case MouseEvent.BUTTON2 -> "中央ボタン";
                    case MouseEvent.BUTTON3 -> "右ボタン";
                    default -> "不明なボタン";
                };
            }
        };
        
        drawPanel.addMouseListener(mouseHandler);
        drawPanel.addMouseMotionListener(mouseHandler);
        
        // コンテキストメニュー（右クリックメニュー）
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(new JMenuItem("切り取り"));
        popupMenu.add(new JMenuItem("コピー"));
        popupMenu.add(new JMenuItem("貼り付け"));
        
        // メニューを開く操作はOSによって異なる（ボタンを押したとき・離したとき）。
        // setComponentPopupMenuは、どちらの場合もメニューを表示する
        drawPanel.setComponentPopupMenu(popupMenu);
        
        add(drawPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MouseEventCompleteExample().setVisible(true);
        });
    }
}
