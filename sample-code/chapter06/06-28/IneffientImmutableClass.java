import java.util.ArrayList;
import java.util.List;

public final class IneffientImmutableClass {
    private final List<String> items;
    
    public IneffientImmutableClass(List<String> items) {
        this.items = new ArrayList<>(items); // 防御的コピー
    }
    
    public List<String> getItems() {
        return new ArrayList<>(items); // 毎回新しいリストを作成
    }
    
    public String getItem(int index) {
        List<String> copy = new ArrayList<>(items); // 不必要なコピー
        return copy.get(index);
    }
}
