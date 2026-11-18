import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.TreeSet;

/**
 * java.util.concurrent.atomicのクラスが、内部で何を使って原子的な操作をしているかを、
 * フィールドの型で調べる。あわせて、VarHandleのフェンスのメソッドを一覧にする。
 *
 *   java AtomicInternals
 *   javap -p java.util.concurrent.atomic.AtomicInteger   （同じことをjavapで見る）
 */
public class AtomicInternals {

    public static void main(String[] args) throws Exception {
        String[] names = {
            "AtomicInteger", "AtomicLong", "AtomicReference", "AtomicBoolean", "AtomicIntegerArray",
        };
        for (String name : names) {
            Class<?> c = Class.forName("java.util.concurrent.atomic." + name);
            StringBuilder sb = new StringBuilder();
            for (Field f : c.getDeclaredFields()) {
                String type = f.getType().getName();
                boolean isStatic = Modifier.isStatic(f.getModifiers());
                if (isStatic && (type.endsWith(".Unsafe") || type.endsWith(".VarHandle"))) {
                    sb.append(sb.isEmpty() ? "" : ", ").append(type).append(' ').append(f.getName());
                }
            }
            IO.println(name + ": " + sb);
        }

        TreeSet<String> fences = new TreeSet<>();
        for (Method m : VarHandle.class.getMethods()) {
            if (Modifier.isStatic(m.getModifiers()) && m.getName().endsWith("Fence")) {
                fences.add(m.getName());
            }
        }
        IO.println("VarHandleのフェンス: " + String.join(" ", fences));
    }
}
