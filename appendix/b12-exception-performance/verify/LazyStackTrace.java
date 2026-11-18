import java.lang.reflect.Field;

/**
 * StackTraceElementの配列が、getStackTrace()を呼んだときに初めて作られることを、
 * Throwableのprivateなフィールドを覗いて確かめる（HotSpotとOpenJDKのクラスライブラリの実装）。
 *
 *   javac LazyStackTrace.java
 *   java --add-opens java.base/java.lang=ALL-UNNAMED LazyStackTrace
 */
public class LazyStackTrace {

    static Object field(Throwable t, String name) throws ReflectiveOperationException {
        Field f = Throwable.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(t);
    }

    static String describe(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof StackTraceElement[] a) {
            return "StackTraceElement[" + a.length + "]";
        }
        return value.getClass().getSimpleName();
    }

    public static void main(String[] args) throws ReflectiveOperationException {
        RuntimeException e = new RuntimeException();
        IO.println("作った直後のstackTrace: " + describe(field(e, "stackTrace")));
        IO.println("作った直後のbacktrace: " + describe(field(e, "backtrace")));
        IO.println("作った直後のdepth: " + field(e, "depth"));
        e.getStackTrace();
        IO.println("getStackTrace()のあとのstackTrace: " + describe(field(e, "stackTrace")));
    }
}
