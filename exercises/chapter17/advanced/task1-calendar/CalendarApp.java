import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * 1か月分のカレンダーを表示するプログラム（発展課題1の解答例）
 *
 * 実行時の引数で年と月を指定します（例: java CalendarApp 2026 11）。
 * 引数を省くと2026年11月を表示します。
 */
public class CalendarApp extends JFrame {
    private static final String[] WEEKDAYS = {"日", "月", "火", "水", "木", "金", "土"};
    private static final Color SUNDAY = new Color(200, 40, 40);
    private static final Color SATURDAY = new Color(40, 80, 200);

    public CalendarApp(MonthCalendar calendar) {
        super("カレンダー");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel titleLabel = new JLabel(calendar.getTitle(), SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        // 行の数を0にすると、7列で必要なだけ行を作る
        JPanel grid = new JPanel(new GridLayout(0, MonthCalendar.COLUMNS, 2, 2));

        // 1行目: 曜日の見出し
        for (int column = 0; column < MonthCalendar.COLUMNS; column++) {
            JLabel label = new JLabel(WEEKDAYS[column], SwingConstants.CENTER);
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            label.setForeground(colorOf(column));
            grid.add(label);
        }

        // 2行目から: 日付のマス。日付のないマスにも空のラベルを置き、位置をそろえる
        int[] cells = calendar.getCells();
        for (int i = 0; i < cells.length; i++) {
            String text = cells[i] == 0 ? "" : String.valueOf(cells[i]);
            JLabel label = new JLabel(text, SwingConstants.CENTER);
            label.setForeground(colorOf(i % MonthCalendar.COLUMNS));
            if (cells[i] != 0) {
                label.setOpaque(true);
                label.setBackground(Color.WHITE);
                label.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
            }
            grid.add(label);
        }

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.add(titleLabel, BorderLayout.NORTH);
        content.add(grid, BorderLayout.CENTER);
        setContentPane(content);

        setSize(360, 320);
        setLocationRelativeTo(null);
    }

    // 列の番号から文字の色を決める（0が日曜、6が土曜）
    private static Color colorOf(int column) {
        if (column == 0) {
            return SUNDAY;
        }
        if (column == 6) {
            return SATURDAY;
        }
        return Color.BLACK;
    }

    public static void main(String[] args) {
        int year = 2026;
        int month = 11;
        if (args.length >= 2) {
            year = Integer.parseInt(args[0]);
            month = Integer.parseInt(args[1]);
        }
        MonthCalendar calendar = new MonthCalendar(year, month);
        SwingUtilities.invokeLater(() -> new CalendarApp(calendar).setVisible(true));
    }
}
