import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class OptimizedImmutableClass {
    private final List<String> items;
    private volatile List<String> cachedView; // 遅延初期化（volatileはスレッド間での可視性を保証）
    
    public OptimizedImmutableClass(Collection<String> items) {
        this.items = new ArrayList<>(items);
    }
    
    public List<String> getItems() {
        if (cachedView == null) {
            synchronized (this) {
                if (cachedView == null) {
                    cachedView = Collections.unmodifiableList(items);
                }
            }
        }
        return cachedView;
    }
}
