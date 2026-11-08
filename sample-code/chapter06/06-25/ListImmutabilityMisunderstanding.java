import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListImmutabilityMisunderstanding {
    public static void main(String[] args) {
        final List<String> items = new ArrayList<>();
        items.add("item1"); // OK（要素の追加は可能）
        items.add("item2"); // OK
        
        // items = new ArrayList<>(); // コンパイルエラー
        
        // 本当に不変にしたい場合
        final List<String> immutableItems = Collections.unmodifiableList(items);
        // immutableItems.add("item3"); // UnsupportedOperationException
        System.out.println("可変リスト: " + items);
        System.out.println("不変ビュー: " + immutableItems);

        // unmodifiableListは元のリストの「ビュー」なので、元のリストの変更は見えてしまう
        items.add("item3");
        System.out.println("元のリスト変更後の不変ビュー: " + immutableItems);
    }
}
