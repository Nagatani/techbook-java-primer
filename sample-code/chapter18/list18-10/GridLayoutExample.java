/**
 * リスト18-10
 * GridLayoutExampleクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (464行目)
 */

import javax.swing.*;
import java.awt.*;

public class GridLayoutExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("GridLayoutの例");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 3行2列のグリッドレイアウト
        // 3行2列のグリッドレイアウト（行、列、水平間隔、垂直間隔）
        frame.setLayout(new GridLayout(3, 2, 5, 5));
        
        // ボタンを順番に配置
        for (int i = 1; i <= 6; i++) {
            frame.add(new JButton("ボタン " + i));
        }
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}