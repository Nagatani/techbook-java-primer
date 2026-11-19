// 第14章：直列化フィルターObjectInputFilter（Java 9で追加）
import java.io.ObjectInputFilter;

public class InputFilter {
    static ObjectInputFilter f = ObjectInputFilter.Config.createFilter("java.base/*;!*");
}
