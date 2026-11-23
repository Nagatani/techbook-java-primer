import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class DrawingPanelExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("独自の描画");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new DrawingPanel());
        frame.pack();  // パネルの推奨サイズに合わせてウィンドウの大きさを決める
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
