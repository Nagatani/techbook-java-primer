import java.util.HashMap;
import java.util.Map;

/**
 * enumの定数がいつ作られるか、static初期化子との順序を観察する。
 *   javac InitOrder.java && java InitOrder
 */
public class InitOrder {
    enum Level {
        LOW("L"), HIGH("H");

        // 定数の後に宣言したstaticフィールドは、すべての定数を作り終えてから初期化される
        private static final Map<String, Level> BY_CODE = new HashMap<>();

        static {
            IO.println("  static初期化子: BY_CODEを作る");
            for (Level l : values()) {
                BY_CODE.put(l.code, l);
            }
        }

        private final String code;

        Level(String code) {
            IO.println("  コンストラクター: " + name());
            this.code = code;
        }

        static Level fromCode(String code) {
            return BY_CODE.get(code);
        }
    }

    public static void main(String[] args) {
        IO.println("mainの始まり（Levelはまだ初期化されていない）");
        IO.println("Level.HIGHを使う");
        Level h = Level.HIGH;
        IO.println("fromCode(\"L\") = " + Level.fromCode("L") + ", h = " + h);
    }
}
