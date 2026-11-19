// 第5章：java.util.Objects（Java 7で追加）
import java.util.Objects;

public class ObjectsProbe {
    static boolean f(Object a, Object b) {
        return Objects.equals(a, b);
    }
}
