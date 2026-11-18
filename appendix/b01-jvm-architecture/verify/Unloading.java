import java.net.URL;
import java.net.URLClassLoader;

/**
 * クラスローダーごと参照されなくなったクラスが、GCでアンロードされることを観察する。
 *
 *   javac Unloading.java
 *   java -Xlog:class+unload Unloading
 */
public class Unloading {
    public static class Plugin { }

    public static void main(String[] args) throws Exception {
        URL here = Unloading.class.getProtectionDomain().getCodeSource().getLocation();
        URLClassLoader loader = new URLClassLoader("plugin", new URL[] { here }, ClassLoader.getPlatformClassLoader());
        Class<?> plugin = loader.loadClass("Unloading$Plugin");
        IO.println("読み込んだ: " + plugin.getName() + "（クラスローダー: " + plugin.getClassLoader().getName() + "）");

        loader.close();
        loader = null;
        plugin = null;
        IO.println("--- クラスローダーとクラスへの参照を捨てて、GCを実行する ---");
        System.gc();
        Thread.sleep(200);
        IO.println("終わり");
    }
}
