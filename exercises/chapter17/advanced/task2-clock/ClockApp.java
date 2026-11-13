import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * アナログ時計を表示するプログラム（発展課題2の解答例）
 *
 * 実行時の引数で時刻を「時:分:秒」の形で指定します（例: java ClockApp 3:45:00）。
 * 引数を省くと10:10:30を表示します。
 * 針を動かし続けるには、第18章で学ぶjavax.swing.Timerを使います。
 */
public class ClockApp {

    private static void createAndShowGUI(int hour, int minute, int second) {
        JFrame frame = new JFrame("アナログ時計");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ClockPanel(hour, minute, second));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        String time = args.length > 0 ? args[0] : "10:10:30";
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        int second = Integer.parseInt(parts[2]);
        SwingUtilities.invokeLater(() -> createAndShowGUI(hour, minute, second));
    }
}
