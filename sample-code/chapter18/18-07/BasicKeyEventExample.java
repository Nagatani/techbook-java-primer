import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BasicKeyEventExample {
    private final JFrame frame = new JFrame("基本的なキーボードイベント");
    private JTextArea inputArea;
    private JLabel statusLabel;

    public BasicKeyEventExample() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 入力エリア
        inputArea = new JTextArea(5, 30);
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(inputArea);

        // ステータスラベル
        statusLabel = new JLabel("キーを押してください");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String keyText = KeyEvent.getKeyText(e.getKeyCode());
                statusLabel.setText("押されたキー: " + keyText);

                // Ctrl+Sのショートカット検出
                if (e.isControlDown()
                        && e.getKeyCode() == KeyEvent.VK_S) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "保存ショートカットが押されました");
                    // 処理済みにして、このあとのキーバインディングに渡さない
                    e.consume();
                }
            }
        });

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(statusLabel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    private void show() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BasicKeyEventExample().show();
        });
    }
}
