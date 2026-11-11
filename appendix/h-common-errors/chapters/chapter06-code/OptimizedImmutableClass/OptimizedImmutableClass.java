import java.util.Collection;
import java.util.List;

public final class OptimizedImmutableClass {
    private final List<String> items;
    
    public OptimizedImmutableClass(Collection<String> items) {
        this.items = List.copyOf(items); // 変更できないリストを作る（防御的コピーを兼ねる）
    }
    
    public List<String> getItems() {
        return items; // 変更できないリストなのでそのまま返せる
    }
}
