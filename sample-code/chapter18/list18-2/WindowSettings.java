/**
 * リスト18-2
 * WindowSettingsクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (138行目)
 */

import javax.swing.JFrame;

public class WindowSettings {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        
        frame.setTitle("カスタマイズされたウィンドウ");     // ①
        frame.setSize(500, 400);                      // ②
        frame.setResizable(false);                    // ③
        frame.setLocation(100, 50);                   // ④
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ⑤
        frame.setVisible(true);                       // ⑥
    }
}