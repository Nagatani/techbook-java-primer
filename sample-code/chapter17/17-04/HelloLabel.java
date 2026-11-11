import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class HelloLabel {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("ラベルの表示");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // JLabelを作成して文字を設定
        JLabel label = new JLabel("Hello, Swing!");
        label.setHorizontalAlignment(JLabel.CENTER);  // 中央揃え
        
        // フレームにラベルを追加
        frame.add(label);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
