import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BasicMouseEventExample {
    private final JFrame frame = new JFrame("基本的なマウスイベント");
    private JLabel statusLabel;
    private JPanel clickPanel;

    public BasicMouseEventExample() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // クリック用パネル
        clickPanel = new JPanel();
        clickPanel.setBackground(Color.LIGHT_GRAY);
        clickPanel.setPreferredSize(new Dimension(400, 300));
        clickPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // ステータスラベル
        statusLabel = new JLabel("パネルをクリックしてください");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);

        clickPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String button = switch (e.getButton()) {
                    case MouseEvent.BUTTON1 -> "左";
                    case MouseEvent.BUTTON2 -> "中央";
                    case MouseEvent.BUTTON3 -> "右";
                    default -> "不明な";
                };
                statusLabel.setText(button + "ボタンがクリックされました: "
                        + e.getPoint());
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

        frame.add(clickPanel, BorderLayout.CENTER);
        frame.add(statusLabel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    private void show() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BasicMouseEventExample().show();
        });
    }
}
