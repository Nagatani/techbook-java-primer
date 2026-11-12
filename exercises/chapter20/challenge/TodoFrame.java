import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;

/**
 * ToDoリストの画面（View）。部品を組み立てて並べるだけで、イベントの処理はTodoControllerが行う。
 */
public class TodoFrame extends JFrame {
    final JList<String> categoryList;
    final JTable table;
    final TableRowSorter<TaskTableModel> sorter;
    final JCheckBox hideDoneCheck = new JCheckBox("完了したタスクを隠す");
    final JTextField titleField = new JTextField(14);
    final JComboBox<String> categoryBox = new JComboBox<>();
    final JComboBox<Priority> priorityBox = new JComboBox<>(Priority.values());
    final JTextField dueField = new JTextField(8);
    final JButton addButton = new JButton("追加");
    final JButton removeButton = new JButton("選択したタスクを削除");
    final JLabel statusLabel = new JLabel(" ");
    final JLabel messageLabel = new JLabel(" ");

    public TodoFrame(CategoryListModel categoryModel, TaskTableModel tableModel,
            TaskManager manager, LocalDate today) {
        super("ToDoリスト");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 左: 分類の一覧
        categoryList = new JList<>(categoryModel);
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryList.setCellRenderer(new CategoryCellRenderer(manager));
        categoryList.setSelectedIndex(0);

        // 右: タスクの表。期限の早い順に並べる
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        sorter = new TableRowSorter<>(tableModel);
        sorter.setSortsOnUpdates(true);
        sorter.setSortKeys(List.of(new RowSorter.SortKey(TaskTableModel.DUE_COLUMN, SortOrder.ASCENDING)));
        table.setRowSorter(sorter);
        table.getColumnModel().getColumn(TaskTableModel.PRIORITY_COLUMN)
                .setCellEditor(new DefaultCellEditor(new JComboBox<>(Priority.values())));
        table.getColumnModel().getColumn(TaskTableModel.DUE_COLUMN)
                .setCellRenderer(new DueDateCellRenderer(today));
        table.getColumnModel().getColumn(TaskTableModel.DONE_COLUMN).setMaxWidth(50);
        table.getColumnModel().getColumn(TaskTableModel.PRIORITY_COLUMN).setMaxWidth(60);

        JScrollPane listScroll = new JScrollPane(categoryList);
        listScroll.setMinimumSize(new Dimension(150, 100));
        JPanel right = new JPanel(new BorderLayout());
        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rightTop.add(hideDoneCheck);
        rightTop.add(removeButton);
        right.add(rightTop, BorderLayout.NORTH);
        right.add(new JScrollPane(table), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, right);
        split.setDividerLocation(220);

        // 下: 追加の入力欄と状態の表示
        categoryBox.setEditable(true);  // 新しい分類の名前も入力できる
        dueField.setText(today.toString());
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("題名"));
        form.add(titleField);
        form.add(new JLabel("分類"));
        form.add(categoryBox);
        form.add(new JLabel("優先度"));
        form.add(priorityBox);
        form.add(new JLabel("期限"));
        form.add(dueField);
        form.add(addButton);

        messageLabel.setForeground(Color.RED);
        JPanel status = new JPanel(new GridLayout(1, 2));
        status.add(statusLabel);
        status.add(messageLabel);
        status.setBorder(BorderFactory.createEmptyBorder(2, 8, 4, 8));

        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER);
        south.add(status, BorderLayout.SOUTH);

        add(split, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
        setSize(860, 440);
        setLocationRelativeTo(null);
    }
}
