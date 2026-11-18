import org.openjdk.jol.info.ClassLayout;

/**
 * レコードのオブジェクトがメモリ上でどう配置されるかを、JOL（Java Object Layout）で観察する。
 * コンポーネントの宣言順を変えても、HotSpotがフィールドを並べ替えるので大きさは変わらない。
 *
 *   javac -cp jol-core-0.17.jar RecordLayout.java
 *   java -cp jol-core-0.17.jar:. RecordLayout
 */
public class RecordLayout {
    record Mixed1(byte a, long b, byte c, String d) { }

    record Mixed2(String d, byte a, byte c, long b) { }

    // 同じコンポーネントを持つふつうのクラス
    static final class MixedClass {
        private final byte a;
        private final long b;
        private final byte c;
        private final String d;

        MixedClass(byte a, long b, byte c, String d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }
    }

    public static void main(String[] args) {
        IO.println(ClassLayout.parseClass(Mixed1.class).toPrintable());
        IO.println(ClassLayout.parseClass(Mixed2.class).toPrintable());
        for (Class<?> c : new Class<?>[] {Mixed1.class, Mixed2.class, MixedClass.class}) {
            IO.println("大きさ: " + c.getSimpleName() + " = "
                    + ClassLayout.parseClass(c).instanceSize() + " bytes");
        }
    }
}
