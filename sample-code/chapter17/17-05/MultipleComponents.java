import javax.swing.*;
import java.awt.*;

public class MultipleComponents {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("複数のコンポーネント");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setLayout(new BorderLayout());  // ①
        
        frame.add(new JLabel("北", JLabel.CENTER), BorderLayout.NORTH);   // ②
        frame.add(new JLabel("南", JLabel.CENTER), BorderLayout.SOUTH);   // ②
        frame.add(new JLabel("東", JLabel.CENTER), BorderLayout.EAST);    // ②
        frame.add(new JLabel("西", JLabel.CENTER), BorderLayout.WEST);    // ②
        frame.add(new JLabel("中央", JLabel.CENTER), BorderLayout.CENTER); // ②
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
