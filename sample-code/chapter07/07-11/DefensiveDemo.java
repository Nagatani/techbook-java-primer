import java.util.ArrayList;
import java.util.List;

// nullを返してしまう実装
class UnsafeImpl implements DefensiveInterface {
    @Override
    public List<String> getItems() {
        return null;
    }
}

// リストを持つ実装
class SafeImpl implements DefensiveInterface {
    private final List<String> items = new ArrayList<>();

    @Override
    public List<String> getItems() {
        return items;
    }
}

public class DefensiveDemo {
    public static void main(String[] args) {
        IO.println("=== nullを返す実装 ===");
        DefensiveInterface unsafe = new UnsafeImpl();
        IO.println("getSafeItems(): " + unsafe.getSafeItems());
        IO.println("getFirstItemOrDefault(): " + unsafe.getFirstItemOrDefault("(なし)"));

        IO.println();
        IO.println("=== リストを持つ実装 ===");
        DefensiveInterface safe = new SafeImpl();
        safe.addItem("Item 1");
        safe.addItem("");  // 空文字列は追加されない
        safe.addItem("Item 2");
        IO.println("getSafeItems(): " + safe.getSafeItems());
        IO.println("getFirstItemOrDefault(): " + safe.getFirstItemOrDefault("(なし)"));
    }
}
