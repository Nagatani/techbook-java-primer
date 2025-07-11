/**
 * リスト23-1
 * SimpleAppクラス
 * 
 * 元ファイル: chapter23-build-and-deploy.md (185行目)
 */

// SimpleApp.java
import javax.swing.*;

public class SimpleApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("配布アプリ");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 150);
            frame.add(new JLabel("JARファイルからの実行に成功しました！"));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}