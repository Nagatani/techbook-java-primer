import java.util.ArrayList;
import java.util.List;

interface DefensiveInterface {
    List<String> getItems();
    
    // 防御的なdefaultメソッド：nullの代わりに空のリストを返し、
    // 内部のリストではなくコピーを渡す
    default List<String> getSafeItems() {
        List<String> items = getItems();
        return items != null ? new ArrayList<>(items) : new ArrayList<>();
    }
    
    // 要素がないときは、nullではなく引数の既定値を返す
    default String getFirstItemOrDefault(String defaultValue) {
        List<String> items = getSafeItems();
        return items.isEmpty() ? defaultValue : items.get(0);
    }
    
    // バリデーション付きメソッド
    default boolean addItem(String item) {
        if (item == null || item.trim().isEmpty()) {
            return false;
        }
        getItems().add(item);
        return true;
    }
}
