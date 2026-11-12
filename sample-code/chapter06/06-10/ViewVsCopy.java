import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewVsCopy {
    public static void main(String[] args) {
        List<String> original = new ArrayList<>();
        original.add("読書");
        
        List<String> view = Collections.unmodifiableList(original);  // 読み取り専用のビュー
        List<String> copy = List.copyOf(original);  // 変更できないコピー
        
        original.add("登山");  // 元のリストに要素を追加する
        
        IO.println("ビュー: " + view);
        IO.println("コピー: " + copy);
    }
}
