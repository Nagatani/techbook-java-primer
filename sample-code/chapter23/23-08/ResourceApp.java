// ResourceApp.java
import javax.swing.*;
import java.awt.*;

public class ResourceApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("リソース付きアプリ");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // アイコンの読み込み
            ImageIcon icon = new ImageIcon(
                ResourceApp.class.getResource("/icon.png")
            );
            JLabel label = new JLabel("画像リソースの例", icon, JLabel.CENTER);
            
            frame.add(label);
            frame.setSize(300, 200);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
