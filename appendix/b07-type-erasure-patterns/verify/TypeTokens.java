import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;

/**
 * 型消去を補う2つの方法を観察する。
 *   1. 型トークン: Class<T>を引数で受け取る
 *   2. スーパータイプトークン: 匿名クラスのスーパークラスに型引数を書き、リフレクションで読む
 *
 *   javac TypeTokens.java && java TypeTokens
 */
public class TypeTokens {
    // 1. 型トークン
    static <T> T[] newArray(Class<T> type, int size) {
        @SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(type, size);
        return array;
    }

    static <T> T firstOf(List<?> values, Class<T> type) {
        for (Object v : values) {
            if (type.isInstance(v)) {
                return type.cast(v);    // 実行時に検査するキャスト
            }
        }
        return null;
    }

    // 2. スーパータイプトークン
    abstract static class TypeRef<T> {
        final Type type;

        protected TypeRef() {
            Type superclass = getClass().getGenericSuperclass();
            this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        }
    }

    static <T> Type tokenInGenericMethod() {
        return new TypeRef<T>() { }.type;   // Tのまま記録される
    }

    public static void main(String[] args) {
        String[] names = newArray(String.class, 2);
        IO.println("配列のクラス: " + names.getClass().getSimpleName());
        IO.println("最初のInteger: " + firstOf(List.of("a", 7, 8), Integer.class));

        Type t1 = new TypeRef<List<String>>() { }.type;
        Type t2 = new TypeRef<Map<String, List<Integer>>>() { }.type;
        IO.println("TypeRef<List<String>>: " + t1);
        IO.println("TypeRef<Map<String, List<Integer>>>: " + t2);
        Type t3 = TypeTokens.<String>tokenInGenericMethod();
        IO.println("ジェネリックメソッドの中で作ると: " + t3
                + "（型変数か: " + (t3 instanceof TypeVariable<?>) + "）");
    }
}
