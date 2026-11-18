import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Stringの内部の表現を観察する。
 * フィールドの名前と型はリフレクションで誰でも見られる。
 * 中身（配列の長さとcoder）を読むには、次のオプションが要る。
 *   java --add-opens java.base/java.lang=ALL-UNNAMED StringAnatomy
 *   java --add-opens java.base/java.lang=ALL-UNNAMED -XX:-CompactStrings StringAnatomy
 */
public class StringAnatomy {
    public static void main(String[] args) throws Exception {
        IO.println("Stringはfinalクラス: " + Modifier.isFinal(String.class.getModifiers()));
        for (Field f : String.class.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            String mods = Modifier.toString(f.getModifiers());
            IO.println("フィールド: " + mods + " " + f.getType().getSimpleName() + " " + f.getName());
        }

        Field value = String.class.getDeclaredField("value");
        Field coder = String.class.getDeclaredField("coder");
        try {
            value.setAccessible(true);
            coder.setAccessible(true);
        } catch (RuntimeException e) {
            IO.println("中身を読むには --add-opens java.base/java.lang=ALL-UNNAMED が要る");
            return;
        }
        for (String s : new String[] {"hello", "héllo", "こんにちは"}) {
            byte[] bytes = (byte[]) value.get(s);
            IO.println("\"%s\" 文字数=%d 配列の長さ=%d coder=%d"
                    .formatted(s, s.length(), bytes.length, coder.getByte(s)));
        }

        // ハッシュ値は最初にhashCode()を呼んだときに計算し、フィールドhashに覚える
        Field hash = String.class.getDeclaredField("hash");
        hash.setAccessible(true);
        String fresh = new StringBuilder("immu").append("table").toString();
        IO.println("hashCode()の前のhash=" + hash.getInt(fresh));
        int h = fresh.hashCode();
        IO.println("hashCode()の後のhash=" + hash.getInt(fresh) + "（hashCode()=" + h + "）");
    }
}
