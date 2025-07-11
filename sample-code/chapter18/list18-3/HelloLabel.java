/**
 * リスト18-3
 * HelloLabelクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (170行目)
 */

import javax.swing.JFrame;
import javax.swing.JLabel;

public class HelloLabel {
    public static void main(String[] args) {
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