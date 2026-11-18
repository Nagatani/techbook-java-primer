import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * ArrayListの内部の配列（elementData）の長さが、要素を足すにつれてどう変わるかを見る。
 * 非公開のフィールドを読むので、次のオプションを付けて実行する。
 *
 *   javac ArrayListGrowth.java
 *   java --add-opens java.base/java.util=ALL-UNNAMED ArrayListGrowth
 */
public class ArrayListGrowth {
    private static final Field ELEMENT_DATA = field();

    public static void main(String[] args) throws Exception {
        ArrayList<Integer> list = new ArrayList<>();
        IO.println("new ArrayList<>() の直後: 配列の長さ=" + capacity(list));
        growth("new ArrayList<>()", list, 100);
        growth("new ArrayList<>(0)", new ArrayList<>(0), 10);

        ArrayList<Integer> presized = new ArrayList<>(100);
        IO.println("new ArrayList<>(100) の直後: 配列の長さ=" + capacity(presized));

        ArrayList<Integer> trimmed = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            trimmed.add(i);
        }
        IO.println("11件入れたあと: 配列の長さ=" + capacity(trimmed));
        trimmed.trimToSize();
        IO.println("trimToSize()のあと: 配列の長さ=" + capacity(trimmed));
        trimmed.clear();
        IO.println("clear()のあと: size=" + trimmed.size() + " 配列の長さ=" + capacity(trimmed));
    }

    /** 要素を1つずつ足し、配列の長さが変わったときの長さを並べる */
    static void growth(String label, ArrayList<Integer> list, int count) throws Exception {
        StringBuilder sb = new StringBuilder();
        int last = capacity(list);
        for (int i = 1; i <= count; i++) {
            list.add(i);
            int cap = capacity(list);
            if (cap != last) {
                sb.append(cap).append(" ");
                last = cap;
            }
        }
        IO.println(label + " に" + count + "件足したときの配列の長さ: " + sb.toString().trim());
    }

    static int capacity(ArrayList<?> list) throws Exception {
        return ((Object[]) ELEMENT_DATA.get(list)).length;
    }

    static Field field() {
        try {
            Field f = ArrayList.class.getDeclaredField("elementData");
            f.setAccessible(true);
            return f;
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
