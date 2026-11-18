import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * インターフェイスにメソッドを足したあと、足す前にコンパイルした実装クラスをそのまま使うと何が起きるかを観察する。
 * 実装クラスは古い版のインターフェイスで、呼び出す側（Client）は新しい版のインターフェイスでコンパイルし、
 * 両方を1つのクラスローダーで読み込む。コンパイルには javax.tools.JavaCompiler を使う。
 */
public class BinaryCompatibility {

    static final JavaCompiler JAVAC = ToolProvider.getSystemJavaCompiler();

    /** ソース（ファイル名 → 中身）をdirに書き、classpathを付けてコンパイルする */
    static void compile(Path dir, Map<String, String> sources, Path classpath) throws IOException {
        Files.createDirectories(dir);
        List<String> args = new ArrayList<>(List.of("-d", dir.toString()));
        if (classpath != null) {
            args.addAll(List.of("-cp", classpath.toString()));
        }
        for (Map.Entry<String, String> e : sources.entrySet()) {
            Path file = dir.resolve(e.getKey());
            Files.writeString(file, e.getValue());
            args.add(file.toString());
        }
        if (JAVAC.run(null, null, null, args.toArray(String[]::new)) != 0) {
            throw new IllegalStateException("コンパイルできない: " + sources.keySet());
        }
    }

    /** 新しい版（newDir）を先に探すクラスローダーで Client.run(new 実装クラス()) を呼ぶ */
    static void runClient(String label, Path newDir, Path oldDir, String implClass) throws Exception {
        URL[] urls = { newDir.toUri().toURL(), oldDir.toUri().toURL() };
        try (URLClassLoader loader = new URLClassLoader(urls, null)) {
            Object impl = loader.loadClass(implClass).getConstructor().newInstance();
            Class<?> client = loader.loadClass("Client");
            Object result = client.getMethod("run", Object.class).invoke(null, impl);
            IO.println(label + ": " + result);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            IO.println(label + ": " + cause.getClass().getName() + ": " + cause.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        Path work = Files.createTempDirectory("binary-compat");
        try {
            // 古い版：Shapeはarea()だけ。Squareはこれを実装してコンパイルしたまま
            Path old = work.resolve("old");
            compile(old, Map.of(
                    "Shape.java", "public interface Shape { double area(); }",
                    "Square.java", "public class Square implements Shape { public double area() { return 4; } }"),
                    null);

            String client = """
                    public class Client {
                        public static String run(Object o) { return "perimeter = " + ((Shape) o).perimeter(); }
                    }""";

            // 新しい版1：抽象メソッドperimeter()を足す
            Path abs = work.resolve("abstract");
            compile(abs, Map.of(
                    "Shape.java", "public interface Shape { double area(); double perimeter(); }",
                    "Client.java", client), null);
            runClient("抽象メソッドを足した", abs, old, "Square");

            // 新しい版2：defaultメソッドperimeter()を足す
            Path def = work.resolve("default");
            compile(def, Map.of(
                    "Shape.java",
                    "public interface Shape { double area(); default double perimeter() { return -1; } }",
                    "Client.java", client), null);
            runClient("defaultメソッドを足した", def, old, "Square");

            // 古い版：LeftとRightは空。Bothは両方を実装してコンパイルしたまま
            Path old2 = work.resolve("old2");
            compile(old2, Map.of(
                    "Left.java", "public interface Left { }",
                    "Right.java", "public interface Right { }",
                    "Both.java", "public class Both implements Left, Right { }"),
                    null);

            // 新しい版3：LeftとRightの両方に、同じシグネチャのdefaultメソッドを足す
            Path conflict = work.resolve("conflict");
            compile(conflict, Map.of(
                    "Left.java", "public interface Left { default String who() { return \"Left\"; } }",
                    "Right.java", "public interface Right { default String who() { return \"Right\"; } }",
                    "Client.java", """
                            public class Client {
                                public static String run(Object o) { return ((Left) o).who(); }
                            }"""), null);
            runClient("2つのインターフェイスに同じdefaultメソッドを足した", conflict, old2, "Both");
        } finally {
            try (Stream<Path> files = Files.walk(work)) {
                for (Path p : files.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(p);
                }
            }
        }
    }
}
