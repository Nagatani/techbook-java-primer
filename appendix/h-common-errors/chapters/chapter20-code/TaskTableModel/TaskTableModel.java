import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class TaskTableModel extends AbstractTableModel {
    private final String[] columns = {"タスク", "完了"};
    private final List<Task> tasks = new ArrayList<>();

    public void addTask(Task task) {
        tasks.add(task);
        int row = tasks.size() - 1;
        // 追加した行をテーブルに知らせる。これがないと画面が更新されない
        fireTableRowsInserted(row, row);
    }

    public void removeTask(int row) {
        tasks.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void setDone(int row, boolean done) {
        tasks.set(row, new Task(tasks.get(row).title(), done));
        fireTableCellUpdated(row, 1);
    }

    @Override
    public int getRowCount() {
        return tasks.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return column == 1 ? Boolean.class : String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        Task task = tasks.get(row);
        return column == 0 ? task.title() : task.done();
    }
}
