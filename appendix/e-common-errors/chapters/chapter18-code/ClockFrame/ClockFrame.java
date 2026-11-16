// ClockFrame.java - 1秒ごとに時刻を表示する（使うTimerをパッケージ名まで書いて指定する）
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class ClockFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("時計");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JLabel label = new JLabel("--:--:--", SwingConstants.CENTER);
            frame.add(label);
            frame.setSize(200, 80);

            DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");
            // javax.swing.Timerは、指定した間隔でEDTの上でリスナーを呼ぶ
            javax.swing.Timer timer = new javax.swing.Timer(1000,
                e -> label.setText(LocalTime.now().format(format)));
            timer.start();

            frame.setVisible(true);
        });
    }
}
