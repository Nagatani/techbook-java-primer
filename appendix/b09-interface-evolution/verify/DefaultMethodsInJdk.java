import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/**
 * 標準ライブラリのインターフェイスに、あとからdefaultメソッドやstaticメソッドとして足されたメソッドを確かめる。
 */
public class DefaultMethodsInJdk {

    static void show(Class<?> type, String name, Class<?>... params) throws NoSuchMethodException {
        Method m = type.getDeclaredMethod(name, params);
        String kind = m.isDefault() ? "default"
                : Modifier.isStatic(m.getModifiers()) ? "static"
                : Modifier.isAbstract(m.getModifiers()) ? "abstract" : "other";
        IO.println(type.getSimpleName() + "." + name + ": " + kind);
    }

    public static void main(String[] args) throws NoSuchMethodException {
        show(Iterable.class, "forEach", Consumer.class);
        show(Collection.class, "stream");
        show(Collection.class, "removeIf", Predicate.class);
        show(Collection.class, "toArray", IntFunction.class);
        show(List.class, "sort", Comparator.class);
        show(List.class, "reversed");
        show(Map.class, "getOrDefault", Object.class, Object.class);
        show(List.class, "of", Object[].class);
        show(Comparator.class, "comparing", Function.class);
        show(Collection.class, "size");
    }
}
