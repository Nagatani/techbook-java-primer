import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * クラスローダーの階層と、親への委譲を観察する。
 *
 *   javac ClassLoaders.java && java ClassLoaders
 */
public class ClassLoaders {
    public static class Greeter { }

    /** loadClassとfindClassが呼ばれたことを表示するクラスローダー */
    static class TracingLoader extends ClassLoader {
        TracingLoader(ClassLoader parent) {
            super("tracing", parent);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            IO.println("  loadClass(" + name + ")");
            return super.loadClass(name, resolve);       // まず親に頼み、見つからなければfindClassを呼ぶ
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            IO.println("  findClass(" + name + ") ← 親が見つけられなかったので自分で探す");
            String file = name.replace('.', '/') + ".class";
            try (InputStream in = ClassLoader.getSystemResourceAsStream(file)) {
                if (in == null) {
                    throw new ClassNotFoundException(name);
                }
                byte[] bytes = in.readAllBytes();
                return defineClass(name, bytes, 0, bytes.length);
            } catch (IOException e) {
                throw new ClassNotFoundException(name, e);
            }
        }
    }

    static String describe(ClassLoader loader) {
        return loader == null ? "null（ブートストラップクラスローダー）" : loader.getName() + "（" + loader.getClass().getName() + "）";
    }

    static String module(Class<?> c) {
        return c.getModule().isNamed() ? c.getModule().getName() : "名前のないモジュール";
    }

    static void show(Class<?> c) {
        IO.println(c.getName() + " -> " + describe(c.getClassLoader()) + " / " + module(c));
    }

    public static void main(String[] args) throws Exception {
        IO.println("[どのクラスローダーが読み込んだか]");
        show(String.class);
        show(java.sql.Driver.class);
        show(ClassLoaders.class);

        IO.println("[親をたどる]");
        ClassLoader app = ClassLoader.getSystemClassLoader();
        IO.println("システムクラスローダー: " + describe(app));
        IO.println("その親: " + describe(app.getParent()));
        IO.println("さらにその親: " + describe(app.getParent().getParent()));
        IO.println("getPlatformClassLoader()と同じ: " + (app.getParent() == ClassLoader.getPlatformClassLoader()));

        IO.println("[親への委譲: 親がプラットフォームクラスローダーのとき]");
        TracingLoader tracing = new TracingLoader(ClassLoader.getPlatformClassLoader());
        IO.println("java.lang.Stringを頼む");
        IO.println("  -> " + describe(tracing.loadClass("java.lang.String").getClassLoader()));
        IO.println("ClassLoaders$Greeterを頼む");
        IO.println("  -> " + describe(tracing.loadClass("ClassLoaders$Greeter").getClassLoader()));

        IO.println("[同じクラスファイルを別のクラスローダーで読み込む]");
        URL here = ClassLoaders.class.getProtectionDomain().getCodeSource().getLocation();
        try (URLClassLoader a = new URLClassLoader("a", new URL[] { here }, ClassLoader.getPlatformClassLoader());
             URLClassLoader b = new URLClassLoader("b", new URL[] { here }, ClassLoader.getPlatformClassLoader())) {
            Class<?> ga = a.loadClass("ClassLoaders$Greeter");
            Class<?> gb = b.loadClass("ClassLoaders$Greeter");
            IO.println("名前が同じ: " + ga.getName().equals(gb.getName()));
            IO.println("同じクラス: " + (ga == gb));
            IO.println("aのGreeterをappのGreeterの型に代入できる: " + Greeter.class.isAssignableFrom(ga));
        }
    }
}
