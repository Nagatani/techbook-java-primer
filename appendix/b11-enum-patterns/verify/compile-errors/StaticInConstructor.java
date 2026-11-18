import java.util.HashMap;
import java.util.Map;

/** enumのコンストラクターからstaticフィールド（定数でないもの）は参照できない（コンパイルエラーになることを確かめるプローブ） */
public class StaticInConstructor {
    enum Color {
        RED("r"), GREEN("g"), BLUE("b");

        private static final Map<String, Color> BY_CODE = new HashMap<>();

        Color(String code) {
            BY_CODE.put(code, this);
        }
    }

    public static void main(String[] args) {
        IO.println(Color.RED);
    }
}
