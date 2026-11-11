import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class LambdaButtonExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("ラムダ式によるイベント処理");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JButton button = new JButton("クリックしてね");
        JButton shortButton = new JButton("こちらも押してね");

        // サンプルコード18-1の匿名クラスの部分をラムダ式で書き換える
        button.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "ボタンがクリックされました！");
        });

        // 処理が1行なら波括弧も省略可能
        shortButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "ボタンがクリックされました！"));

        frame.add(button);
        frame.add(shortButton);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
