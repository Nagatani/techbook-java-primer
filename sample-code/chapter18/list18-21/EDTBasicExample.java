/**
 * リスト18-21
 * EDTBasicExampleクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (1172行目)
 */

import javax.swing.*;

public class EDTBasicExample {
    public static void main(String[] args) {
        // 間違った方法：メインスレッドから直接GUI作成
        // JFrame frame = new JFrame("Bad Example");
        
        // 正しい方法：EDT上でGUI作成
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }
    
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("正しいEDTの使用例");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JLabel label = new JLabel("このGUIはEDT上で作成されました", JLabel.CENTER);
        frame.add(label);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}