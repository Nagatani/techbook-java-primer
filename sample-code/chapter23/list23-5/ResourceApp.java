/**
 * リスト23-5
 * ResourceAppクラス
 * 
 * 元ファイル: chapter23-build-and-deploy.md (398行目)
 */

// ResourceApp.java
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResourceApp {
    private Properties config = new Properties();
    
    public ResourceApp() {
        loadConfiguration();
        createGUI();
    }
    
    private void loadConfiguration() {
        try (InputStream is = getClass().getResourceAsStream("/config.properties")) {
            if (is != null) {
                config.load(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void createGUI() {
        JFrame frame = new JFrame(config.getProperty("app.title", "Default App"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // アイコンの読み込み
        ImageIcon icon = new ImageIcon(getClass().getResource("/icon.png"));
        JLabel label = new JLabel("リソースを含むアプリケーション", icon, JLabel.CENTER);
        label.setFont(new Font("Sans-serif", Font.BOLD, 16));
        
        frame.add(label);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ResourceApp::new);
    }
}