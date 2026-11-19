// 第20章：TableRowSorter（Java 6で追加）
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class TableRowSorterProbe {
    static TableRowSorter<DefaultTableModel> make(DefaultTableModel model) {
        return new TableRowSorter<DefaultTableModel>(model);
    }
}
