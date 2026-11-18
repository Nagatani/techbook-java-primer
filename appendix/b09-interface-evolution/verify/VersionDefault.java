/**
 * defaultメソッドとstaticメソッドを持つインターフェイス（Java 8から書ける）。
 * javac --release 8 でコンパイルし、javap -v でクラスファイルの版とフラグを見る。
 * 古い版でもコンパイルできるように、java.lang.IO（Java 25）は使わない。
 */
public interface VersionDefault {
    String name();

    default String greet() {
        return "Hello, " + name();
    }

    static VersionDefault of(String name) {
        return () -> name;
    }
}
