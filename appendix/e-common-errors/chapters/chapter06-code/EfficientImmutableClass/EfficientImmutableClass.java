import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EfficientImmutableClass {
    private final List<String> items;
    
    public EfficientImmutableClass(List<String> items) {
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
    }
    
    public List<String> getItems() {
        return items; // 不変リストなのでそのまま返却
    }
    
    public String getItem(int index) {
        return items.get(index); // 直接アクセス
    }
    
    public int size() {
        return items.size(); // 直接アクセス
    }
}
