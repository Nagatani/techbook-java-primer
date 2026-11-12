import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 期限の列。期限切れは赤、今日が期限なら太字にする。
 */
public class DueDateCellRenderer extends DefaultTableCellRenderer {
    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("M月d日(E)", Locale.JAPAN);

    private final LocalDate today;

    public DueDateCellRenderer(LocalDate today) {
        this.today = today;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        LocalDate due = (LocalDate) value;
        // rowは表示上の行番号なので、モデルの行番号に直してからタスクを取り出す
        TaskTableModel model = (TaskTableModel) table.getModel();
        Task task = model.getTask(table.convertRowIndexToModel(row));

        setText(due.format(FORMAT));
        setFont(table.getFont());
        if (isSelected) {
            setForeground(table.getSelectionForeground());
        } else if (task.isOverdue(today)) {
            setForeground(Color.RED);
        } else {
            setForeground(table.getForeground());
        }
        if (!task.isDone() && due.equals(today)) {
            setFont(table.getFont().deriveFont(Font.BOLD));
        }
        return this;
    }
}
