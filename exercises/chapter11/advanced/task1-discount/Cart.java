import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 買い物かご。割引ルールはこのクラスの情報だけを見て割引額を決める
 */
public class Cart {
    private final List<Item> items = new ArrayList<>();

    public void add(Item item) {
        items.add(item);
    }

    // 合計金額（割引前）
    public int total() {
        int sum = 0;
        for (Item item : items) {
            sum += item.price();
        }
        return sum;
    }

    // 指定した分類の商品の数
    public int countOf(String category) {
        int count = 0;
        for (Item item : items) {
            if (item.category().equals(category)) {
                count++;
            }
        }
        return count;
    }

    // 商品を1つずつ受け取って処理する。何をするかは呼び出し側がラムダ式で決める
    public void forEachItem(Consumer<Item> action) {
        items.forEach(action);
    }
}
