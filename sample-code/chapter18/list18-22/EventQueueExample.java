/**
 * リスト18-22
 * EventQueueExampleクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (1207行目)
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EventQueueExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("イベントキューの例");
            frame.setSize(400, 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new FlowLayout());
            
            JButton button = new JButton("イベント確認");
            JLabel statusLabel = new JLabel("準備完了");
            
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // EDTの確認
                    if (SwingUtilities.isEventDispatchThread()) {
                        statusLabel.setText("正しくEDT上で実行されています");
                    } else {
                        statusLabel.setText("警告: EDT以外のスレッドで実行されています");
                    }
                    
                    // イベントキューの状況を表示
                    System.out.println("現在のスレッド: " + Thread.currentThread().getName());
                    System.out.println("EDT上での実行: " + SwingUtilities.isEventDispatchThread());
                }
            });
            
            frame.add(button);
            frame.add(statusLabel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}