import java.util.EnumMap;
import java.util.Map;

/**
 * enumのキーで値を取り出す汎用のメソッド（<T> T get(Key)）は、戻り値の型を検査できないことを観察する。
 * configuration/Configuration.javaのget(ConfigKey)と同じ形。
 *   javac GenericGetter.java && java GenericGetter
 */
public class GenericGetter {
    enum Key { PORT, HOST }

    static final Map<Key, Object> VALUES = new EnumMap<>(Key.class);

    @SuppressWarnings("unchecked")
    static <T> T get(Key key) {
        return (T) VALUES.get(key);
    }

    public static void main(String[] args) {
        VALUES.put(Key.PORT, 8080);
        VALUES.put(Key.HOST, "localhost");

        int port = get(Key.PORT);
        IO.println("port = " + port);
        try {
            // 誤った型で受けてもコンパイルでき、実行時に失敗する
            String wrong = get(Key.PORT);
            IO.println("wrong = " + wrong);
        } catch (ClassCastException e) {
            IO.println("String wrong = get(Key.PORT): ClassCastException");
        }
    }
}
