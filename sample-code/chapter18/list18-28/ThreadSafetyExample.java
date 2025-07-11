/**
 * リスト18-28
 * ThreadSafetyExampleクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (1561行目)
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ThreadSafetyExample extends JFrame {
    private volatile boolean running = false;
    private JLabel statusLabel;
    private JButton startButton;
    private JButton stopButton;
    
    public ThreadSafetyExample() {
        setTitle("スレッドセーフティの例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        statusLabel = new JLabel("停止中", JLabel.CENTER);
        startButton = new JButton("開始");
        stopButton = new JButton("停止");
        stopButton.setEnabled(false);
        
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startProcessing();
            }
        });
        
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopProcessing();
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        
        add(statusLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void startProcessing() {
        running = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        
        // バックグラウンド処理開始
        new Thread(() -> {
            int counter = 0;
            while (running) {
                try {
                    Thread.sleep(100);
                    final int currentCount = ++counter;
                    
                    // EDT上でUI更新
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("処理中... " + currentCount);
                    });
                } catch (InterruptedException e) {
                    break;
                }
            }
            
            // 処理終了時のUI更新
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("停止中");
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            });
        }).start();
    }
    
    private void stopProcessing() {
        running = false;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ThreadSafetyExample().setVisible(true);
        });
    }
}