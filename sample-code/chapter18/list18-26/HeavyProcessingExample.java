/**
 * リスト18-26
 * HeavyProcessingExampleクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (1421行目)
 */

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HeavyProcessingExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("重い処理の例");
            frame.setSize(400, 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            
            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            
            JButton processButton = new JButton("データ処理開始");
            JProgressBar progressBar = new JProgressBar();
            progressBar.setStringPainted(true);
            
            processButton.addActionListener(e -> {
                processButton.setEnabled(false);
                textArea.setText("");
                
                SwingWorker<String, String> worker = new SwingWorker<String, String>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        StringBuilder result = new StringBuilder();
                        
                        // 重い処理のシミュレーション
                        for (int i = 1; i <= 10; i++) {
                            Thread.sleep(500); // 0.5秒の処理
                            
                            String message = "処理ステップ " + i + " 完了\n";
                            publish(message);
                            setProgress(i * 10);
                            result.append(message);
                        }
                        
                        return result.toString();
                    }
                    
                    @Override
                    protected void process(List<String> chunks) {
                        for (String chunk : chunks) {
                            textArea.append(chunk);
                        }
                    }
                    
                    @Override
                    protected void done() {
                        processButton.setEnabled(true);
                        progressBar.setValue(0);
                        try {
                            textArea.append("\n=== 処理完了 ===\n" + get());
                        } catch (Exception ex) {
                            textArea.append("エラーが発生しました: " + ex.getMessage());
                        }
                    }
                };
                
                // 進捗バーとWorkerを連携
                worker.addPropertyChangeListener(evt -> {
                    if ("progress".equals(evt.getPropertyName())) {
                        int progress = (Integer) evt.getNewValue();
                        progressBar.setValue(progress);
                        progressBar.setString(progress + "%");
                    }
                });
                
                worker.execute();
            });
            
            frame.add(scrollPane, BorderLayout.CENTER);
            frame.add(processButton, BorderLayout.NORTH);
            frame.add(progressBar, BorderLayout.SOUTH);
            
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}