import java.util.Arrays;

/**
 * enumの正体を観察する。
 *   javac EnumAnatomy.java && javap -p 'EnumAnatomy$Color'
 *   javap -c -p 'EnumAnatomy$Color'   （values・valueOf・static初期化子の中身）
 *   java EnumAnatomy                   （values()が毎回新しい配列を返すことなど）
 */
public class EnumAnatomy {
    enum Color { RED, GREEN, BLUE }

    public static void main(String[] args) {
        Class<Color> c = Color.class;
        IO.println("スーパークラス: " + c.getSuperclass().getName());
        IO.println("isEnum: " + c.isEnum());

        // name()とordinal()は、コンストラクターに渡された名前と番号
        for (Color color : Color.values()) {
            IO.println(color.name() + " ordinal=" + color.ordinal());
        }

        // values()は呼ぶたびに配列を複製して返す
        Color[] a = Color.values();
        Color[] b = Color.values();
        IO.println("values()は同じ配列か: " + (a == b));
        a[0] = null;
        IO.println("書き換えたあとのvalues(): " + Arrays.toString(Color.values()));

        // valueOfは名前で定数を探す。ない名前・nullは例外になる
        IO.println("valueOf(\"GREEN\") == GREEN: " + (Color.valueOf("GREEN") == Color.GREEN));
        try {
            Color.valueOf("PURPLE");
        } catch (IllegalArgumentException e) {
            IO.println("valueOf(\"PURPLE\"): " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        try {
            Color.valueOf(null);
        } catch (NullPointerException e) {
            IO.println("valueOf(null): " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // compareToは宣言の順（ordinal）で比べる
        IO.println("RED.compareTo(BLUE): " + Color.RED.compareTo(Color.BLUE));
        IO.println("BLUE.compareTo(RED): " + Color.BLUE.compareTo(Color.RED));
    }
}
