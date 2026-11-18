import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;

/**
 * シングルトンの書き方を比べる。
 * 実行: javac SingletonVariants.java && java SingletonVariants
 */
public class SingletonVariants {
    // クラスの初期化を使う遅延初期化（Holderクラスは、getInstanceで初めて使うときに初期化される）
    static class Lazy {
        private Lazy() {
            IO.println("  Lazyを作る");
        }

        private static class Holder {
            static final Lazy INSTANCE = new Lazy();
        }

        static Lazy getInstance() {
            return Holder.INSTANCE;
        }

        static void hello() {
            IO.println("  Lazy.hello()（まだ作らない）");
        }
    }

    // クラスで書いたシングルトン（readResolveを書いていない）
    static class ClassSingleton implements Serializable {
        static final ClassSingleton INSTANCE = new ClassSingleton();

        private ClassSingleton() {
        }
    }

    // enumで書いたシングルトン
    enum EnumSingleton {
        INSTANCE
    }

    static Object roundTrip(Object o) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(o);
        }
        try (ObjectInputStream in = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()))) {
            return in.readObject();
        }
    }

    public static void main(String[] args) throws Exception {
        IO.println("1. 遅延初期化");
        Lazy.hello();
        IO.println("  getInstanceを呼ぶ");
        Lazy a = Lazy.getInstance();
        IO.println("  同じオブジェクト: " + (a == Lazy.getInstance()));

        IO.println("2. 直列化して読み戻す");
        IO.println("  クラス: 同じオブジェクト = "
                + (roundTrip(ClassSingleton.INSTANCE) == ClassSingleton.INSTANCE));
        IO.println("  enum: 同じオブジェクト = "
                + (roundTrip(EnumSingleton.INSTANCE) == EnumSingleton.INSTANCE));

        IO.println("3. リフレクションでコンストラクターを呼ぶ");
        Constructor<ClassSingleton> c1 = ClassSingleton.class.getDeclaredConstructor();
        c1.setAccessible(true);
        IO.println("  クラス: 2つ目ができた = " + (c1.newInstance() != ClassSingleton.INSTANCE));
        Constructor<?> c2 = EnumSingleton.class.getDeclaredConstructors()[0];
        c2.setAccessible(true);
        try {
            c2.newInstance("SECOND", 1);
        } catch (IllegalArgumentException e) {
            IO.println("  enum: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
