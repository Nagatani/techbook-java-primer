import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;

/**
 * enumの定数が、リフレクションでもシリアライズでも増えないことを観察する。
 *   javac ReflectAndSerialize.java && java ReflectAndSerialize
 */
public class ReflectAndSerialize {
    /** enumによるシングルトン */
    enum Registry {
        INSTANCE;

        private int count;

        int next() { return ++count; }
    }

    /** 比較のための、クラスで書いたシングルトン（readResolveを書いていない） */
    static final class ClassSingleton implements Serializable {
        private static final long serialVersionUID = 1L;
        static final ClassSingleton INSTANCE = new ClassSingleton();

        private ClassSingleton() {
        }
    }

    public static void main(String[] args) throws Exception {
        // 1. リフレクションでコンストラクターを呼んでも、enumのインスタンスは作れない
        Constructor<?> ctor = Registry.class.getDeclaredConstructors()[0];
        IO.println("Registryのコンストラクター: " + ctor);
        ctor.setAccessible(true);
        try {
            ctor.newInstance("SECOND", 1);
            IO.println("作れた");
        } catch (IllegalArgumentException e) {
            IO.println("newInstance: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // クラスのシングルトンは、privateのコンストラクターでもリフレクションで2つ目を作れる
        Constructor<ClassSingleton> cs = ClassSingleton.class.getDeclaredConstructor();
        cs.setAccessible(true);
        IO.println("ClassSingleton: リフレクションで作ったものは別のオブジェクトか: "
            + (cs.newInstance() != ClassSingleton.INSTANCE));

        // 2. シリアライズして読み戻しても、同じ定数が返る
        Registry.INSTANCE.next();
        Registry.INSTANCE.next();
        byte[] bytes = serialize(Registry.INSTANCE);
        Object back = deserialize(bytes);
        IO.println("Registry: 読み戻したものは同じ定数か: " + (back == Registry.INSTANCE));
        IO.println("Registry: 読み戻したあとのnext(): " + ((Registry) back).next());

        // ストリームに書かれるのは型と定数の名前で、フィールド（count）は書かれない
        String text = new String(bytes, StandardCharsets.ISO_8859_1);
        IO.println("ストリームに名前INSTANCEがあるか: " + text.contains("INSTANCE"));
        IO.println("ストリームにフィールド名countがあるか: " + text.contains("count"));

        // クラスのシングルトンは、readResolveがなければ別のオブジェクトになる
        Object back2 = deserialize(serialize(ClassSingleton.INSTANCE));
        IO.println("ClassSingleton: 読み戻したものは同じオブジェクトか: " + (back2 == ClassSingleton.INSTANCE));
    }

    static byte[] serialize(Object o) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bout)) {
            out.writeObject(o);
        }
        return bout.toByteArray();
    }

    static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return in.readObject();
        }
    }
}
