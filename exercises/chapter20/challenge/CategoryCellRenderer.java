import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * 分類の名前に、未完了の件数と全体の件数を添えて表示する。
 */
public class CategoryCellRenderer extends DefaultListCellRenderer {
    private final TaskManager manager;

    public CategoryCellRenderer(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        // 選択時の色などは標準の描き方に任せる
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        String category = CategoryListModel.toCategory((String) value);
        setText("%s（未完了 %d / %d件）".formatted(value,
                manager.countUndone(category), manager.countAll(category)));
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        return this;
    }
}
