/**
 * 文字列の連結のバイトコードが、Java 9で変わったことを観察する（JEP 280）。
 * mainを持たない、javapで見るためだけの型。
 * コンストラクターを持たないインターフェイスにして、javapの出力をgreetだけにしている。
 *
 *   javac --release 8 Concat.java && javap -c Concat     （StringBuilderのappendの連続）
 *   javac Concat.java && javap -c Concat                 （invokedynamicの1命令）
 */
public interface Concat {
    static String greet(String name, int count) {
        return "Hello, " + name + " x" + count;
    }
}
