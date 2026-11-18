import java.lang.classfile.ClassFile;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;

/**
 * 型の合わないバイトコードを、JVMが読み込むときの検証（verification）で拒むことを観察する。
 * javacはこのようなコードを作らないので、Class-File API（java.lang.classfile）でクラスファイルを直接組み立てる。
 *
 *   javac Verification.java && java Verification
 */
public class Verification {
    /** バイト列からクラスを定義するだけのクラスローダー */
    static class ByteLoader extends ClassLoader {
        Class<?> define(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length);
        }
    }

    public static void main(String[] args) throws Throwable {
        // public static int answer() { aconst_null; ireturn }
        // nullの参照を積み、intとして返す。Javaのソースでは書けない
        byte[] bytes = ClassFile.of().build(ClassDesc.of("Broken"), cb -> cb
                .withMethodBody("answer", MethodTypeDesc.ofDescriptor("()I"),
                        ClassFile.ACC_PUBLIC | ClassFile.ACC_STATIC,
                        code -> code.aconst_null().ireturn()));
        IO.println("組み立てたクラスファイル: " + bytes.length + "バイト");
        Class<?> c = new ByteLoader().define("Broken", bytes);
        IO.println("読み込めた（まだ検証していない）: " + c.getName());
        try {
            IO.println("answer()を呼ぶ");
            IO.println("answer() = " + c.getMethod("answer").invoke(null));
        } catch (VerifyError e) {
            IO.println("VerifyError: " + e.getMessage().lines().findFirst().orElse(""));
            e.getMessage().lines()
                    .filter(l -> l.contains("is not assignable"))
                    .forEach(l -> IO.println("  " + l.trim()));
        }
    }
}
