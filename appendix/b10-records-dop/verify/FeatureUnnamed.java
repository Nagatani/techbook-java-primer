/**
 * 名前のないパターン変数（_）が使える版を確かめる（javac --release 21 と 22 で比べる）。
 */
public class FeatureUnnamed {
    record Pair(String first, String second) { }

    static String first(Object o) {
        if (o instanceof Pair(String f, _)) {
            return f;
        }
        return "";
    }
}
