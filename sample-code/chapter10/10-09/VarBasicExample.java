import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VarBasicExample {
    public static void main(String[] args) {
        // 従来の書き方
        List<String> languages = new ArrayList<String>();
        Map<String, Integer> scores = new HashMap<String, Integer>();

        // ダイヤモンド演算子を使った書き方（Java 7以降）
        List<String> languagesV7 = new ArrayList<>();
        Map<String, Integer> scoresV7 = new HashMap<>();

        // var型推論を使った書き方（Java 10以降）
        var languagesVar = new ArrayList<String>();
        var scoresVar = new HashMap<String, Integer>();

        // varの変数の型は右辺と同じArrayList<String>になる（List<String>ではない）
        languagesVar.add("Java");
        languagesVar.ensureCapacity(10);  // ArrayListのメソッドも呼べる
        // languages.ensureCapacity(10);  // List<String>型なのでコンパイルエラー
        scoresVar.put("Java", 90);

        IO.println("languagesVar: " + languagesVar);
        IO.println("scoresVar: " + scoresVar);
    }
}
