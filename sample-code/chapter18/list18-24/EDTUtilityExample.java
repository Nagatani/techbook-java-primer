/**
 * リスト18-24
 * EDTUtilityExampleクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (1327行目)
 */

import javax.swing.*;
import java.awt.*;

public class EDTUtilityExample {
    public static void main(String[] args) {
        // 1. SwingUtilities.invokeLater() - EDT上で後で実行
        SwingUtilities.invokeLater(() -> {
            System.out.println("invokeLater: " + Thread.currentThread().getName());
            createGUI();
        });
        
        // 2. SwingUtilities.invokeAndWait() - EDT上で実行し、完了まで待機
        try {
            SwingUtilities.invokeAndWait(() -> {
                System.out.println("invokeAndWait: " + Thread.currentThread().getName());
                JOptionPane.showMessageDialog(null, "EDT上で実行され、完了まで待機しました");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 3. SwingUtilities.isEventDispatchThread() - 現在のスレッドがEDTかチェック
        System.out.println("メインスレッドはEDT？: " + SwingUtilities.isEventDispatchThread());
    }
    
    private static void createGUI() {
        JFrame frame = new JFrame("EDT ユーティリティの例");
        frame.setSize(300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JButton checkThreadButton = new JButton("スレッドを確認");
        checkThreadButton.addActionListener(e -> {
            String threadInfo = String.format(
                "現在のスレッド: %s%nEDT上での実行: %s",
                Thread.currentThread().getName(),
                SwingUtilities.isEventDispatchThread()
            );
            JOptionPane.showMessageDialog(frame, threadInfo);
        });
        
        frame.add(checkThreadButton);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}