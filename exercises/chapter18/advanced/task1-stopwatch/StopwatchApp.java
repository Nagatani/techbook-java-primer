import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * javax.swing.Timerで動くストップウォッチ。
 */
public class StopwatchApp {
    private final StopwatchModel model = new StopwatchModel();
    private final JLabel timeLabel = new JLabel(StopwatchModel.format(0), JLabel.CENTER);
    private final JButton startStopButton = new JButton("開始");
    private final JButton lapButton = new JButton("ラップ");
    private final JButton resetButton = new JButton("リセット");
    private final JTextArea lapArea = new JTextArea(8, 20);
    private final Timer timer;

    public StopwatchApp() {
        // 0.1秒ごとにEDT上で呼ばれる
        timer = new Timer(100, e -> {
            model.tick();
            timeLabel.setText(StopwatchModel.format(model.getElapsedTenths()));
        });

        startStopButton.addActionListener(e -> {
            if (model.isRunning()) {
                model.stop();
                timer.stop();
            } else {
                model.start();
                timer.start();
            }
            updateButtons();
        });

        lapButton.addActionListener(e -> {
            if (model.lap()) {
                showLaps();
            }
        });

        resetButton.addActionListener(e -> {
            if (model.reset()) {
                timeLabel.setText(StopwatchModel.format(0));
                lapArea.setText("");
                updateButtons();
            }
        });

        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 40));
        lapArea.setEditable(false);
        lapArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(startStopButton);
        buttonPanel.add(lapButton);
        buttonPanel.add(resetButton);

        JPanel top = new JPanel(new BorderLayout());
        top.add(timeLabel, BorderLayout.CENTER);
        top.add(buttonPanel, BorderLayout.SOUTH);

        JFrame frame = new JFrame("ストップウォッチ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(top, BorderLayout.NORTH);
        frame.add(new JScrollPane(lapArea), BorderLayout.CENTER);
        frame.setSize(360, 340);
        frame.setLocationRelativeTo(null);

        updateButtons();
        frame.setVisible(true);
    }

    /** 状態に合わせてボタンの文字と押せるかどうかを変える */
    private void updateButtons() {
        boolean running = model.isRunning();
        startStopButton.setText(running ? "停止" : (model.getElapsedTenths() == 0 ? "開始" : "再開"));
        lapButton.setEnabled(running);
        resetButton.setEnabled(!running && model.getElapsedTenths() > 0);
    }

    /** ラップの一覧を作り直す */
    private void showLaps() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < model.getLapTimes().size(); i++) {
            sb.append("ラップ%d  %s  (経過 %s)%n".formatted(
                    i + 1,
                    StopwatchModel.format(model.getLapLength(i)),
                    StopwatchModel.format(model.getLapTimes().get(i))));
        }
        lapArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StopwatchApp());
    }
}
