import javax.swing.*;

public class EDTBasicExample {
    public static void main(String[] args) {
        // GUIの組み立てをEDT（イベントディスパッチスレッド）に任せる
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    // 画面を組み立てて表示する（EDT上で呼び出される）
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("EDTでGUIを組み立てる");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("このGUIはEDT上で作成されました", JLabel.CENTER);
        frame.add(label);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
