import java.time.LocalDate;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * 右のタスク表のモデル。選んだ分類のタスクを表示する。
 * 完了と優先度の列は編集でき、変更はTaskManagerに渡す。
 */
public class TaskTableModel extends AbstractTableModel {
    public static final int DONE_COLUMN = 0;
    public static final int TITLE_COLUMN = 1;
    public static final int CATEGORY_COLUMN = 2;
    public static final int PRIORITY_COLUMN = 3;
    public static final int DUE_COLUMN = 4;

    private static final String[] COLUMN_NAMES = {"完了", "題名", "分類", "優先度", "期限"};
    private static final Class<?>[] COLUMN_CLASSES = {
        Boolean.class, String.class, String.class, Priority.class, LocalDate.class
    };

    private final TaskManager manager;
    private String category;  // nullならすべて
    private List<Task> rows;

    public TaskTableModel(TaskManager manager) {
        this.manager = manager;
        this.rows = manager.getTasks(null);
    }

    public void setCategory(String category) {
        this.category = category;
        refresh();
    }

    /** TaskManagerの内容に合わせて作り直す */
    public void refresh() {
        List<Task> newRows = manager.getTasks(category);
        if (newRows.equals(rows)) {
            // 行の顔ぶれが同じなら、値だけ描き直す（選択が外れない）
            rows = newRows;
            if (!rows.isEmpty()) {
                fireTableRowsUpdated(0, rows.size() - 1);
            }
        } else {
            rows = newRows;
            fireTableDataChanged();
        }
    }

    public Task getTask(int row) {
        return rows.get(row);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    // Booleanはチェックボックスで表示・編集され、PriorityとLocalDateはcompareToで並べ替えられる
    @Override
    public Class<?> getColumnClass(int column) {
        return COLUMN_CLASSES[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == DONE_COLUMN || column == PRIORITY_COLUMN;
    }

    @Override
    public Object getValueAt(int row, int column) {
        Task t = rows.get(row);
        return switch (column) {
            case DONE_COLUMN -> t.isDone();
            case TITLE_COLUMN -> t.getTitle();
            case CATEGORY_COLUMN -> t.getCategory();
            case PRIORITY_COLUMN -> t.getPriority();
            default -> t.getDueDate();
        };
    }

    /** 表で編集された値をTaskManagerに渡す。表示はリスナー経由で更新される */
    @Override
    public void setValueAt(Object value, int row, int column) {
        Task t = rows.get(row);
        if (column == DONE_COLUMN && value instanceof Boolean done) {
            manager.setDone(t, done);
        } else if (column == PRIORITY_COLUMN && value instanceof Priority priority) {
            manager.setPriority(t, priority);
        }
    }
}
