import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BasicMouseEventExample extends JFrame {
    private JLabel statusLabel;
    private JPanel clickPanel;
    
    public BasicMouseEventExample() {
        setTitle("基本的なマウスイベント");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // クリック用パネル
        clickPanel = new JPanel();
        clickPanel.setBackground(Color.LIGHT_GRAY);
        clickPanel.setPreferredSize(new Dimension(400, 300));
        clickPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // ステータスラベル
        statusLabel = new JLabel("パネルをクリックしてください");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // マウスリスナーの追加
        clickPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String button = switch (e.getButton()) {
                    case MouseEvent.BUTTON1 -> "左";
                    case MouseEvent.BUTTON2 -> "中央";
                    case MouseEvent.BUTTON3 -> "右";
                    default -> "不明な";
                };
                statusLabel.setText(button + "ボタンがクリックされました: " + e.getPoint());
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                clickPanel.setBackground(Color.YELLOW);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                clickPanel.setBackground(Color.LIGHT_GRAY);
            }
        });
        
        add(clickPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BasicMouseEventExample().setVisible(true);
        });
    }
}
