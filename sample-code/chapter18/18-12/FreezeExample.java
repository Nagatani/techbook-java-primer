import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class FreezeExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("長時間処理の回避");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JButton badButton = new JButton("悪い例");
        JButton goodButton = new JButton("よい例");
        JLabel label = new JLabel("待機中");

        // 悪い例：UIがフリーズする
        badButton.addActionListener(e -> {
            // 長時間かかる処理（3秒待機）
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            label.setText("完了");
        });

        // よい例：別スレッドで実行
        goodButton.addActionListener(e -> {
            label.setText("処理中...");
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    // UI更新はEDT上で行う
                    SwingUtilities.invokeLater(() -> label.setText("完了"));
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        });

        frame.add(badButton);
        frame.add(goodButton);
        frame.add(label);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
