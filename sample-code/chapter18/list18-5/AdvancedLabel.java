/**
 * リスト18-5
 * AdvancedLabelクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (242行目)
 */

import javax.swing.*;
import java.awt.*;

public class AdvancedLabel {
    public static void main(String[] args) {
        JFrame frame = new JFrame("高度なJLabel");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        
        // テキストのみのラベル
        JLabel textLabel = new JLabel("通常のテキスト");
        
        // HTMLを使用したリッチテキスト
        JLabel htmlLabel = new JLabel(
            "<html><b>太字</b>と<i>斜体</i>と<font color='red'>赤色</font></html>");
        
        // 複数行テキスト
        JLabel multiLineLabel = new JLabel("<html>複数行の<br>テキストを<br>表示</html>");
        
        frame.add(textLabel);
        frame.add(htmlLabel);
        frame.add(multiLineLabel);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}