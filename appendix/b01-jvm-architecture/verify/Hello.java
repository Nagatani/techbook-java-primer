/**
 * クラスがどこから読み込まれたか（source:）を観察するための、最小のプログラム。
 *
 *   javac Hello.java
 *   java -Xlog:class+load Hello               （JDKのクラスはCDSのアーカイブ（shared objects file）から）
 *   java -Xshare:off -Xlog:class+load Hello   （CDSを使わないと、jrt:/java.base から）
 */
public class Hello {
    public static void main(String[] args) {
        IO.println("Hello, JVM");
    }
}
