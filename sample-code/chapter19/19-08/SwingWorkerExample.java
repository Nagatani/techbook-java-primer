import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class SwingWorkerExample extends JFrame {
    private JProgressBar progressBar;
    private JTextArea resultArea;
    private JButton startButton;
    private JButton cancelButton;
    private DataProcessingWorker currentWorker;
    
    public SwingWorkerExample() {
        setTitle("SwingWorkerによる非同期処理");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // プログレスバー
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        
        // 結果表示エリア
        resultArea = new JTextArea(15, 50);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        // ボタンパネル
        JPanel buttonPanel = new JPanel();
        startButton = new JButton("処理開始");
        cancelButton = new JButton("キャンセル");
        cancelButton.setEnabled(false);
        
        startButton.addActionListener(e -> startProcessing());
        cancelButton.addActionListener(e -> cancelProcessing());
        
        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);
        
        // レイアウト
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(progressBar, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void startProcessing() {
        startButton.setEnabled(false);
        cancelButton.setEnabled(true);
        resultArea.setText("処理を開始しています...\n");
        progressBar.setValue(0);
        
        currentWorker = new DataProcessingWorker();
        currentWorker.execute();
    }
    
    private void cancelProcessing() {
        if (currentWorker != null && !currentWorker.isDone()) {
            currentWorker.cancel(true);
            resultArea.append("処理がキャンセルされました。\n");
        }
    }
    
    // SwingWorkerの実装
    class DataProcessingWorker extends SwingWorker<String, ProgressInfo> {
        private static final int TOTAL_ITEMS = 100;
        
        @Override
        protected String doInBackground() throws Exception {
            Random random = new Random();
            StringBuilder result = new StringBuilder();
            
            for (int i = 0; i < TOTAL_ITEMS; i++) {
                if (isCancelled()) {
                    return "処理がキャンセルされました";
                }
                
                // 重い処理のシミュレーション
                Thread.sleep(50);
                
                // 仮のデータ処理
                int value = random.nextInt(1000);
                result.append("Item ").append(i).append(": ").append(value).append("\n");
                
                // 進捗を公開
                int progress = (i + 1) * 100 / TOTAL_ITEMS;
                publish(new ProgressInfo(progress, "処理中: アイテム " + (i + 1) + "/" + TOTAL_ITEMS));
            }
            
            return result.toString();
        }
        
        @Override
        protected void process(List<ProgressInfo> chunks) {
            // 最新の進捗情報を使用
            ProgressInfo latest = chunks.get(chunks.size() - 1);
            progressBar.setValue(latest.progress);
            progressBar.setString(latest.message);
            resultArea.append(latest.message + "\n");
        }
        
        @Override
        protected void done() {
            startButton.setEnabled(true);
            cancelButton.setEnabled(false);
            
            try {
                if (!isCancelled()) {
                    String result = get();
                    resultArea.append("\n処理が完了しました。\n");
                    resultArea.append("結果のサマリー:\n");
                    resultArea.append("処理したアイテム数: " + TOTAL_ITEMS + "\n");
                    progressBar.setValue(100);
                    progressBar.setString("完了");
                }
            } catch (InterruptedException | ExecutionException e) {
                resultArea.append("エラーが発生しました: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
    }
    
    // 進捗情報クラス
    class ProgressInfo {
        final int progress;
        final String message;
        
        ProgressInfo(int progress, String message) {
            this.progress = progress;
            this.message = message;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SwingWorkerExample().setVisible(true);
        });
    }
}
