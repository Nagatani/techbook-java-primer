import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 * 左の分類一覧のモデル。先頭に「すべて」を置き、その後に分類を並べる。
 */
public class CategoryListModel extends AbstractListModel<String> {
    public static final String ALL = "すべて";

    private final TaskManager manager;
    private List<String> items = new ArrayList<>();

    public CategoryListModel(TaskManager manager) {
        this.manager = manager;
        refresh();
    }

    /** TaskManagerの内容に合わせて作り直す */
    public void refresh() {
        int oldSize = items.size();
        List<String> newItems = new ArrayList<>();
        newItems.add(ALL);
        newItems.addAll(manager.getCategories());
        items = newItems;
        if (items.size() > oldSize) {
            fireIntervalAdded(this, oldSize, items.size() - 1);
        }
        // 件数の表示が変わるので、すべての行を描き直してもらう
        fireContentsChanged(this, 0, items.size() - 1);
    }

    @Override
    public int getSize() {
        return items.size();
    }

    @Override
    public String getElementAt(int index) {
        return items.get(index);
    }

    /** 「すべて」ならnull、それ以外は分類名（TaskManager.getTasksに渡す形） */
    public static String toCategory(String item) {
        return ALL.equals(item) ? null : item;
    }
}
