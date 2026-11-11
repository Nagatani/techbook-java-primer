import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BasicKeyEventExample extends JFrame {
    private JTextArea inputArea;
    private JLabel statusLabel;
    
    public BasicKeyEventExample() {
        setTitle("基本的なキーボードイベント");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 入力エリア
        inputArea = new JTextArea(5, 30);
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(inputArea);
        
        // ステータスラベル
        statusLabel = new JLabel("キーを押してください");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // キーリスナーの追加
        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String keyText = KeyEvent.getKeyText(e.getKeyCode());
                statusLabel.setText("押されたキー: " + keyText);
                
                // Ctrl+Sのショートカット検出
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
                    JOptionPane.showMessageDialog(BasicKeyEventExample.this, 
                        "保存ショートカットが押されました");
                    e.consume(); // デフォルトの動作をキャンセル
                }
            }
        });
        
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BasicKeyEventExample().setVisible(true);
        });
    }
}
