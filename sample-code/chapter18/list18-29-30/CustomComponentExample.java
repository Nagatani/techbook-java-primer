/**
 * リスト18-30
 * CustomComponentExampleクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (1808行目)
 */

import javax.swing.*;
import java.awt.*;

public class CustomComponentExample extends JFrame {
    public CustomComponentExample() {
        setTitle("カスタムコンポーネントの例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        
        // カスタムの丸いボタンを作成
        RoundButton roundButton1 = new RoundButton("OK");
        RoundButton roundButton2 = new RoundButton("Cancel");
        RoundButton roundButton3 = new RoundButton("Help");
        
        // ボタンの色をカスタマイズ
        roundButton1.setBackgroundColor(Color.GREEN);
        roundButton2.setBackgroundColor(Color.RED);
        roundButton3.setBackgroundColor(Color.YELLOW);
        
        // イベントリスナーを追加
        roundButton1.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "OK がクリックされました"));
        
        roundButton2.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "Cancel がクリックされました"));
        
        roundButton3.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "Help がクリックされました"));
        
        // コンポーネントを追加
        add(roundButton1);
        add(roundButton2);
        add(roundButton3);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CustomComponentExample().setVisible(true);
        });
    }
}