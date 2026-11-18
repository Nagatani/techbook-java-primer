/**
 * privateメソッドを持つインターフェイス（Java 9から書ける）。
 * javac --release 8 ではコンパイルできない。
 * --release 10 と --release 11 でコンパイルし、javap -c -p で呼び出し命令を見比べる。
 * 古い版でもコンパイルできるように、java.lang.IO（Java 25）は使わない。
 */
public interface VersionPrivate {
    String name();

    default String greet() {
        return decorate("Hello, " + name());
    }

    private String decorate(String text) {
        return "[" + text + "]";
    }
}
