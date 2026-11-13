import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 3つの列にタスクを並べるボード（画面に依存しないロジック）
 *
 * 中身が変わるたびに、PropertyChangeSupportで"tasks"プロパティの変化を知らせます。
 */
public class TaskBoard {
    public static final String PROPERTY = "tasks";

    private final Map<Column, List<String>> columns = new EnumMap<>(Column.class);
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public TaskBoard() {
        for (Column column : Column.values()) {
            columns.put(column, new ArrayList<>());
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(PROPERTY, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(PROPERTY, listener);
    }

    /** 未着手の列の末尾に足す。空の名前と、どこかの列にある名前は足さない */
    public boolean add(String task) {
        String name = task.strip();
        if (name.isEmpty() || find(name) != null) {
            return false;
        }
        columns.get(Column.TODO).add(name);
        fireChanged();
        return true;
    }

    /**
     * タスクを別の列のindexの位置へ移す。indexが範囲外なら末尾に置く
     * 同じ列の中での並べ替えにも使える
     */
    public boolean move(String task, Column to, int index) {
        Column from = find(task);
        if (from == null) {
            return false;
        }
        // 同じ列で下へ動かすときは、取り除いた分だけ後ろの位置が1つ前に詰まる
        int position = index;
        if (from == to && columns.get(from).indexOf(task) < index) {
            position--;
        }
        columns.get(from).remove(task);
        List<String> target = columns.get(to);
        if (position < 0 || position > target.size()) {
            target.add(task);
        } else {
            target.add(position, task);
        }
        fireChanged();
        return true;
    }

    public boolean remove(String task) {
        Column from = find(task);
        if (from == null) {
            return false;
        }
        columns.get(from).remove(task);
        fireChanged();
        return true;
    }

    /** タスクがある列。どこにもなければnull */
    public Column find(String task) {
        for (Column column : Column.values()) {
            if (columns.get(column).contains(task)) {
                return column;
            }
        }
        return null;
    }

    public List<String> getTasks(Column column) {
        return List.copyOf(columns.get(column));
    }

    /** 列ごとのタスクの数 */
    public Map<Column, Integer> counts() {
        Map<Column, Integer> counts = new EnumMap<>(Column.class);
        for (Column column : Column.values()) {
            counts.put(column, columns.get(column).size());
        }
        return counts;
    }

    // 新しい値として列ごとの数を渡す
    // firePropertyChangeは古い値と新しい値が等しいと通知しない。同じ列の中で並べ替えたときは
    // 数が変わらないので、古い値にはnullを渡して必ず通知する
    private void fireChanged() {
        support.firePropertyChange(PROPERTY, null, counts());
    }
}
