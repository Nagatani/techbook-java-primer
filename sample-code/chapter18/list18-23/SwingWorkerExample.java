/**
 * リスト18-23
 * SwingWorkerExampleクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (1254行目)
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingWorkerExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("SwingWorkerの例");
            frame.setSize(400, 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            
            JProgressBar progressBar = new JProgressBar(0, 100);
            JButton startButton = new JButton("長時間処理を開始");
            JLabel statusLabel = new JLabel("準備完了", JLabel.CENTER);
            
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startButton.setEnabled(false);
                    progressBar.setValue(0);
                    statusLabel.setText("処理中...");
                    
                    // SwingWorkerで背景処理を実行
                    SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            // 重い処理のシミュレーション
                            for (int i = 0; i <= 100; i++) {
                                Thread.sleep(50); // 50msの遅延
                                publish(i); // 進捗を報告
                            }
                            return null;
                        }
                        
                        @Override
                        protected void process(java.util.List<Integer> chunks) {
                            // EDT上で進捗バーを更新
                            for (Integer progress : chunks) {
                                progressBar.setValue(progress);
                            }
                        }
                        
                        @Override
                        protected void done() {
                            // EDT上で完了処理
                            startButton.setEnabled(true);
                            statusLabel.setText("処理完了");
                        }
                    };
                    
                    worker.execute();
                }
            });
            
            frame.add(progressBar, BorderLayout.NORTH);
            frame.add(statusLabel, BorderLayout.CENTER);
            frame.add(startButton, BorderLayout.SOUTH);
            
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}