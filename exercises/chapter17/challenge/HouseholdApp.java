import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

/**
 * 家計簿の画面（チャレンジ課題の解答例）
 *
 * メニューバー・タブ・分割ペイン・自作の円グラフを組み合わせます。
 * メニューやボタンの処理は、第18章で学ぶイベント処理で付けます。
 */
public class HouseholdApp extends JFrame {
    private static final List<Color> COLORS = List.of(
            new Color(231, 111, 81), new Color(244, 162, 97), new Color(233, 196, 106),
            new Color(42, 157, 143), new Color(38, 70, 83));

    public HouseholdApp(Household household) {
        super("家計簿 - 2026年11月");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(createMenuBar());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("一覧", createListTab(household));
        tabs.addTab("グラフ", createChartTab(household));
        tabs.addTab("入力", createInputTab(household));

        JLabel statusLabel = new JLabel("支出 %d件  合計 %,d円".formatted(
                household.getExpenses().size(), household.getTotal()));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        add(tabs, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        setSize(560, 420);
        setLocationRelativeTo(null);
    }

    private JMenuBar createMenuBar() {
        JMenu fileMenu = new JMenu("ファイル");
        fileMenu.add(new JMenuItem("開く"));
        fileMenu.add(new JMenuItem("保存"));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem("終了"));
        JMenu viewMenu = new JMenu("表示");
        viewMenu.add(new JMenuItem("前の月"));
        viewMenu.add(new JMenuItem("次の月"));
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        return menuBar;
    }

    // 一覧のタブ: 等幅の文字で桁をそろえ、テキストエリアに並べる
    // 日本語の文字は幅が英数字の2倍ほどあり、%-6sなどで文字数をそろえても列がずれる
    // そのため、幅の決まった日付と金額を先に、日本語の分類と内容を後ろに置く
    private JScrollPane createListTab(Household household) {
        StringBuilder text = new StringBuilder();
        for (Expense e : household.getExpenses()) {
            text.append("%s  %,7d円  %s  %s%n".formatted(e.date(), e.amount(), e.category(), e.item()));
        }
        JTextArea area = new JTextArea(text.toString());
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        return new JScrollPane(area);
    }

    // グラフのタブ: 左に円グラフ、右に凡例。境目はドラッグで動かせる
    private JSplitPane createChartTab(Household household) {
        Map<String, Integer> totals = household.totalsByCategory();
        PieChartPanel chart = new PieChartPanel(totals, COLORS);

        JPanel legend = new JPanel(new GridLayout(0, 1, 0, 6));
        legend.setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 12));
        int i = 0;
        for (Map.Entry<String, Integer> entry : totals.entrySet()) {
            JLabel swatch = new JLabel();
            swatch.setOpaque(true);
            swatch.setBackground(COLORS.get(i % COLORS.size()));
            swatch.setPreferredSize(new Dimension(16, 16));
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            row.add(swatch);
            row.add(new JLabel("%s  %,d円".formatted(entry.getKey(), entry.getValue())));
            legend.add(row);
            i++;
        }

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chart, legend);
        split.setResizeWeight(0.6); // ウィンドウを広げたとき、増えた幅の6割を円グラフに配る
        return split;
    }

    // 入力のタブ: 項目名と入力部品の格子と、右寄せのボタン
    private JPanel createInputTab(Household household) {
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("日付"));
        form.add(new JTextField(LocalDate.of(2026, 11, 30).toString()));
        form.add(new JLabel("分類"));
        form.add(new JComboBox<>(household.getCategories().toArray(new String[0])));
        form.add(new JLabel("内容"));
        form.add(new JTextField());
        form.add(new JLabel("金額（円）"));
        form.add(new JSpinner(new SpinnerNumberModel(1000, 1, 1_000_000, 100)));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(new JButton("追加"));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.add(form, BorderLayout.NORTH);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    static Household sampleData() {
        Household household = new Household(List.of("食費", "交通費", "日用品", "娯楽", "その他"));
        household.add(new Expense(LocalDate.of(2026, 11, 2), "食費", "スーパー", 3200));
        household.add(new Expense(LocalDate.of(2026, 11, 3), "交通費", "定期券", 9800));
        household.add(new Expense(LocalDate.of(2026, 11, 7), "日用品", "洗剤", 650));
        household.add(new Expense(LocalDate.of(2026, 11, 9), "食費", "学食", 2400));
        household.add(new Expense(LocalDate.of(2026, 11, 14), "娯楽", "映画", 1900));
        household.add(new Expense(LocalDate.of(2026, 11, 16), "食費", "スーパー", 4100));
        household.add(new Expense(LocalDate.of(2026, 11, 21), "娯楽", "本", 1650));
        household.add(new Expense(LocalDate.of(2026, 11, 25), "日用品", "文房具", 800));
        return household;
    }

    public static void main(String[] args) {
        Household household = sampleData();
        SwingUtilities.invokeLater(() -> new HouseholdApp(household).setVisible(true));
    }
}
