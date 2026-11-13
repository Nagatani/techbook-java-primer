import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * 時間のかかる計算をSwingWorkerで動かす画面（発展課題2の解答例）
 *
 * 計算している間も、画面の操作（キャンセルボタンなど）を受け付けます。
 */
public class PrimeCounterApp extends JFrame {
    private final JSpinner limitSpinner =
            new JSpinner(new SpinnerNumberModel(20_000_000, 1_000, 100_000_000, 5_000_000));
    private final JButton startButton = new JButton("数える");
    private final JButton cancelButton = new JButton("キャンセル");
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final JLabel countLabel = new JLabel("素数の個数: -");
    private final JLabel resultLabel = new JLabel(" ");
    private PrimeWorker worker;

    public PrimeCounterApp() {
        super("素数を数える");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        progressBar.setStringPainted(true);
        cancelButton.setEnabled(false);
        startButton.addActionListener(e -> start());
        cancelButton.addActionListener(e -> {
            if (worker != null) {
                worker.cancel(false); // 計算のループがisCancelled()を見て止まる
            }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("2から"));
        top.add(limitSpinner);
        top.add(new JLabel("まで"));
        top.add(startButton);
        top.add(cancelButton);

        JPanel center = new JPanel(new BorderLayout(0, 6));
        center.add(progressBar, BorderLayout.NORTH);
        center.add(countLabel, BorderLayout.CENTER);
        center.add(resultLabel, BorderLayout.SOUTH);

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        content.add(top, BorderLayout.NORTH);
        content.add(center, BorderLayout.CENTER);
        setContentPane(content);
        pack();
        setLocationRelativeTo(null);
    }

    private void start() {
        int limit = (Integer) limitSpinner.getValue();
        startButton.setEnabled(false);
        cancelButton.setEnabled(true);
        progressBar.setValue(0);
        resultLabel.setText("計算中...");

        worker = new PrimeWorker(limit);
        // setProgressで変えた値は"progress"プロパティの変化として、EDTで届く
        worker.addPropertyChangeListener(e -> {
            if ("progress".equals(e.getPropertyName())) {
                progressBar.setValue((Integer) e.getNewValue());
            }
        });
        worker.execute();
    }

    /**
     * 結果の型がInteger（素数の個数）、途中経過の型もInteger（その時点の個数）
     */
    private class PrimeWorker extends SwingWorker<Integer, Integer> {
        private final int limit;
        private final long startTime = System.currentTimeMillis();

        PrimeWorker(int limit) {
            this.limit = limit;
        }

        // 別のスレッドで動く。ここでは画面の部品に触らない
        @Override
        protected Integer doInBackground() {
            int count = 0;
            int step = Math.max(1, limit / 100);
            for (int n = 2; n <= limit; n++) {
                if (isCancelled()) {
                    return count;
                }
                if (PrimeMath.isPrime(n)) {
                    count++;
                }
                if (n % step == 0) {
                    setProgress((int) ((long) n * 100 / limit));
                    publish(count);
                }
            }
            setProgress(100);
            return count;
        }

        // publishした値がまとめて届く。EDTで動くので、部品を更新してよい
        @Override
        protected void process(List<Integer> chunks) {
            int latest = chunks.get(chunks.size() - 1);
            countLabel.setText("素数の個数: " + latest + "（途中経過）");
        }

        // 終わったとき（キャンセルを含む）にEDTで呼ばれる
        @Override
        protected void done() {
            startButton.setEnabled(true);
            cancelButton.setEnabled(false);
            long elapsed = System.currentTimeMillis() - startTime;
            try {
                int count = get();
                countLabel.setText("素数の個数: " + count);
                resultLabel.setText("2から" + limit + "までに" + count + "個（" + elapsed + "ミリ秒）");
            } catch (CancellationException e) {
                resultLabel.setText("キャンセルしました（" + elapsed + "ミリ秒）");
            } catch (InterruptedException | ExecutionException e) {
                resultLabel.setText("計算に失敗しました: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PrimeCounterApp().setVisible(true));
    }
}
