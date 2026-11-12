import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

/**
 * 成績一覧表。列見出しのクリックで並べ替え、氏名と赤点の有無で絞り込む。
 */
public class GradeTableApp {
    private final GradeTableModel model = createSampleModel();
    private final JTable table = new JTable(model);
    private final TableRowSorter<GradeTableModel> sorter = new TableRowSorter<>(model);
    private final JTextField nameField = new JTextField(10);
    private final JCheckBox failingOnly = new JCheckBox("60点未満の科目がある人だけ");
    private final JLabel summaryLabel = new JLabel();
    private final JLabel detailLabel = new JLabel(" ");

    public GradeTableApp() {
        // 点数を編集したら並べ替えと絞り込みをやり直す
        sorter.setSortsOnUpdates(true);
        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        for (int c = GradeTableModel.FIRST_SUBJECT_COLUMN; c < GradeTableModel.TOTAL_COLUMN; c++) {
            table.getColumnModel().getColumn(c).setCellEditor(new ScoreCellEditor());
        }

        // 入力のたびに絞り込む（DocumentListenerは第19章で学んだ）
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });
        failingOnly.addActionListener(e -> applyFilter());

        // 並べ替え・絞り込み・編集のあとで集計し直す
        sorter.addRowSorterListener(e -> updateSummary());
        model.addTableModelListener(e -> SwingUtilities.invokeLater(() -> {
            updateSummary();
            updateDetail();
        }));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetail();
            }
        });

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("氏名:"));
        filterPanel.add(nameField);
        filterPanel.add(failingOnly);

        JPanel south = new JPanel(new BorderLayout());
        south.add(summaryLabel, BorderLayout.NORTH);
        south.add(detailLabel, BorderLayout.SOUTH);
        south.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        JFrame frame = new JFrame("成績一覧");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(filterPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.add(south, BorderLayout.SOUTH);
        frame.setSize(620, 320);
        frame.setLocationRelativeTo(null);

        updateSummary();
        frame.setVisible(true);
    }

    /** ソーターを作る前にデータをそろえておく */
    private static GradeTableModel createSampleModel() {
        GradeTableModel m = new GradeTableModel();
        m.addStudent(new Student("S001", "青木健太", 78, 92, 65));
        m.addStudent(new Student("S002", "井上美咲", 88, 54, 90));
        m.addStudent(new Student("S003", "上田翔", 45, 70, 58));
        m.addStudent(new Student("S004", "江藤由紀", 95, 85, 99));
        m.addStudent(new Student("S005", "大野拓海", 62, 48, 71));
        m.addStudent(new Student("S006", "加藤真央", 70, 66, 80));
        return m;
    }

    /** 氏名と赤点の条件を組み合わせて絞り込む */
    private void applyFilter() {
        List<RowFilter<GradeTableModel, Integer>> filters = new ArrayList<>();
        String text = nameField.getText().strip();
        if (!text.isEmpty()) {
            // Pattern.quoteで、入力の記号を正規表現の記号として扱わないようにする
            filters.add(RowFilter.regexFilter(Pattern.quote(text), GradeTableModel.NAME_COLUMN));
        }
        if (failingOnly.isSelected()) {
            filters.add(new RowFilter<GradeTableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends GradeTableModel, ? extends Integer> entry) {
                    Student s = entry.getModel().getStudent(entry.getIdentifier());
                    return s.hasFailingSubject();
                }
            });
        }
        sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
    }

    /** 表示されている行だけで平均を求める */
    private void updateSummary() {
        List<Integer> rows = new ArrayList<>();
        for (int viewRow = 0; viewRow < sorter.getViewRowCount(); viewRow++) {
            rows.add(sorter.convertRowIndexToModel(viewRow));
        }
        StringBuilder sb = new StringBuilder("表示 %d人 / 全 %d人　平均".formatted(rows.size(), model.getRowCount()));
        for (int c = GradeTableModel.FIRST_SUBJECT_COLUMN; c <= GradeTableModel.TOTAL_COLUMN; c++) {
            sb.append("　%s %.1f".formatted(model.getColumnName(c), model.average(c, rows)));
        }
        summaryLabel.setText(sb.toString());
    }

    /** 選択した学生の合計と平均を表示する */
    private void updateDetail() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            detailLabel.setText(" ");
            return;
        }
        Student s = model.getStudent(table.convertRowIndexToModel(viewRow));
        detailLabel.setText("%s %s　合計 %d点　平均 %.1f点".formatted(
                s.getId(), s.getName(), s.getTotal(),
                (double) s.getTotal() / Student.SUBJECTS.length));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GradeTableApp());
    }
}
