import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimerExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("タイマーの例");
            frame.setSize(300, 150);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            
            JLabel timeLabel = new JLabel("", JLabel.CENTER);
            timeLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
            
            JButton toggleButton = new JButton("開始");
            
            // javax.swing.Timerを使用（EDT上で実行される）
            Timer timer = new Timer(1000, e -> {
                // 定期的にEDT上で実行される処理
                LocalTime now = LocalTime.now();
                String timeText = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                timeLabel.setText(timeText);
            });
            // 開始したらすぐに1回目を実行する（既定では最初の実行も1秒後）
            timer.setInitialDelay(0);
            
            toggleButton.addActionListener(e -> {
                if (timer.isRunning()) {
                    timer.stop();
                    toggleButton.setText("開始");
                } else {
                    timer.start();
                    toggleButton.setText("停止");
                }
            });
            
            frame.add(timeLabel, BorderLayout.CENTER);
            frame.add(toggleButton, BorderLayout.SOUTH);
            
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
