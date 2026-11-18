import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;

/**
 * レコードがどのようなクラスにコンパイルされるかを観察する。
 *
 *   javac RecordAnatomy.java
 *   javap -p 'RecordAnatomy$Point'
 *   javap -c -p -v 'RecordAnatomy$Point'
 *   java RecordAnatomy
 */
public class RecordAnatomy {
    record Point(int x, int y) { }

    // 比べるための、finalフィールドを持つふつうのクラス
    static final class PlainPoint {
        private final int x;

        PlainPoint(int x) {
            this.x = x;
        }
    }

    public static void main(String[] args) {
        Class<?> c = Point.class;
        IO.println("isRecord: " + c.isRecord());
        IO.println("スーパークラス: " + c.getSuperclass().getName());
        IO.println("クラスの修飾子: " + Modifier.toString(c.getModifiers()));
        for (Field f : c.getDeclaredFields()) {
            IO.println("フィールド: " + Modifier.toString(f.getModifiers()) + " " + f.getType() + " " + f.getName());
        }
        for (RecordComponent rc : c.getRecordComponents()) {
            IO.println("コンポーネント: " + rc.getName() + " アクセサー: " + rc.getAccessor().getName() + "()");
        }
        Point p = new Point(1, 2);
        IO.println("toString: " + p);
        IO.println("equals: " + p.equals(new Point(1, 2)));

        // リフレクションでfinalフィールドを書き換えてみる
        try {
            Field f = PlainPoint.class.getDeclaredField("x");
            f.setAccessible(true);
            var plain = new PlainPoint(1);
            f.setInt(plain, 99);
            IO.println("ふつうのクラスのfinalフィールド: 書き換えられた（x=" + f.getInt(plain) + "）");
        } catch (ReflectiveOperationException e) {
            IO.println("ふつうのクラスのfinalフィールド: " + e.getClass().getSimpleName());
        }
        try {
            Field f = Point.class.getDeclaredField("x");
            f.setAccessible(true);
            f.setInt(p, 99);
            IO.println("レコードのフィールド: 書き換えられた（x=" + p.x() + "）");
        } catch (ReflectiveOperationException e) {
            IO.println("レコードのフィールド: " + e.getClass().getSimpleName());
        }
    }
}
