/**
 * リスト18-25
 * ProperGUIInitializationクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (1382行目)
 */

public class ProperGUIInitialization {
    public static void main(String[] args) {
        // システムのLook&Feelを設定（EDT外でも可）
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // GUIの作成と表示はEDT上で
        SwingUtilities.invokeLater(() -> {
            new MyApplication().createAndShowGUI();
        });
    }
}

class MyApplication {
    public void createAndShowGUI() {
        JFrame frame = new JFrame("適切な初期化の例");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // コンポーネントの作成と設定
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("適切に初期化されたGUI", JLabel.CENTER));
        
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}