import java.util.*;

interface DefensiveInterface {
    List<String> getItems();
    
    // 防御的なdefaultメソッド
    default List<String> getSafeItems() {
        List<String> items = getItems();
        return items != null ? new ArrayList<>(items) : Collections.emptyList();
    }
    
    default Optional<String> getFirstItem() {
        List<String> items = getSafeItems();
        return items.isEmpty() ? Optional.empty() : Optional.of(items.get(0));
    }
    
    default Iterator<String> iterateItems() {
        return getSafeItems().iterator();
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
