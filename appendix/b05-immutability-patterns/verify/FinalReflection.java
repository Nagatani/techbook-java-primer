import java.lang.reflect.Field;

/**
 * finalのフィールドをリフレクションで書き換えられるかを、
 * ふつうのクラス・レコード・static finalで比べる。
 *   javac FinalReflection.java && java FinalReflection
 */
public class FinalReflection {
    static final class Money {
        private final int amount;

        Money(int amount) {
            this.amount = amount;
        }
    }

    record Point(int x, int y) {
    }

    static final Object LOCK = new Object();

    public static void main(String[] args) throws Exception {
        Money m = new Money(100);
        Field amount = Money.class.getDeclaredField("amount");
        amount.setAccessible(true);
        amount.setInt(m, 999);
        IO.println("ふつうのクラスのfinalフィールド: " + m.amount);

        Point p = new Point(1, 2);
        Field x = Point.class.getDeclaredField("x");
        x.setAccessible(true);
        try {
            x.setInt(p, 99);
            IO.println("レコード: 書き換えられた " + p);
        } catch (IllegalAccessException e) {
            IO.println("レコード: " + e.getClass().getSimpleName());
        }

        Field lock = FinalReflection.class.getDeclaredField("LOCK");
        lock.setAccessible(true);
        try {
            lock.set(null, new Object());
            IO.println("static final: 書き換えられた");
        } catch (IllegalAccessException e) {
            IO.println("static final: " + e.getClass().getSimpleName());
        }
    }
}
