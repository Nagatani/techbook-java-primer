import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * JSONライブラリJacksonのTypeReferenceが、スーパータイプトークンと同じ形のクラスであることを
 * リフレクションで確かめる。実行にはjackson-coreのJARが要る（コンパイルには要らない）。
 *
 *   javac JacksonTypeReference.java
 *   java -cp .:jackson-core-2.23.0.jar JacksonTypeReference
 */
public class JacksonTypeReference {
    public static void main(String[] args) throws Exception {
        Class<?> c = Class.forName("com.fasterxml.jackson.core.type.TypeReference");
        IO.println("クラス: " + c.getName());
        IO.println("抽象クラスか: " + Modifier.isAbstract(c.getModifiers()));
        IO.println("型パラメーター: " + Arrays.toString(c.getTypeParameters()));
        for (Constructor<?> k : c.getDeclaredConstructors()) {
            IO.println("コンストラクター: " + Modifier.toString(k.getModifiers())
                    + " " + Arrays.toString(k.getParameterTypes()));
        }
        IO.println("getTypeの戻り値: " + c.getMethod("getType").getReturnType().getName());
    }
}
