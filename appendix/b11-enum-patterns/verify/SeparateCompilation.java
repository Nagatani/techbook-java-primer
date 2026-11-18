import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * enumとそれを使うクラスを別々にコンパイルし、enumだけを変えたときに何が起きるかを観察する。
 * JDKのコンパイラーAPI（javax.tools）で、一時ディレクトリに次のものを作る。
 *   client/    Color（RED, GREEN, BLUE）と一緒にコンパイルしたClient
 *   client17/  同じClientを--release 17でコンパイルしたもの
 *   v1/        元のColor
 *   added/     末尾にPURPLEを足したColor
 *   reordered/ 並びを BLUE, GREEN, RED に変えたColor
 * そのうえで、Clientと各版のColorを組み合わせて実行する。
 *   javac SeparateCompilation.java && java SeparateCompilation
 */
public class SeparateCompilation {
    static final String CLIENT = """
        public class Client {
            // 定数をすべて書いたswitch式（defaultなし）
            public static String expr(Color c) {
                return switch (c) {
                    case RED -> "赤";
                    case GREEN -> "緑";
                    case BLUE -> "青";
                };
            }

            // 従来のswitch文（defaultなし）
            public static String stmt(Color c) {
                String s = "（どのcaseにも当たらない）";
                switch (c) {
                    case RED: s = "赤"; break;
                    case GREEN: s = "緑"; break;
                    case BLUE: s = "青"; break;
                }
                return s;
            }
        }
        """;

    public static void main(String[] args) throws Exception {
        Path work = Files.createTempDirectory("enum-sep");
        try {
            Path v1 = compile(work, "v1", "public enum Color { RED, GREEN, BLUE }", List.of());
            Path added = compile(work, "added", "public enum Color { RED, GREEN, BLUE, PURPLE }", List.of());
            Path reordered = compile(work, "reordered", "public enum Color { BLUE, GREEN, RED }", List.of());
            Path client = compileClient(work, "client", v1, List.of());
            Path client17 = compileClient(work, "client17", v1, List.of("--release", "17"));

            IO.println("== 元のColor");
            run(client, v1, "RED");
            IO.println("== 並べ替えたColor（BLUE, GREEN, RED）");
            run(client, reordered, "RED");
            run(client, reordered, "BLUE");
            IO.println("== PURPLEを足したColor");
            run(client, added, "PURPLE");
            IO.println("== PURPLEを足したColor（Clientは--release 17でコンパイル）");
            run(client17, added, "PURPLE");
        } finally {
            try (Stream<Path> s = Files.walk(work)) {
                s.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
            }
        }
    }

    /** ColorとClientを組み合わせて読み込み、指定した定数でexprとstmtを呼ぶ */
    static void run(Path clientDir, Path colorDir, String constant) throws Exception {
        URL[] urls = { clientDir.toUri().toURL(), colorDir.toUri().toURL() };
        try (URLClassLoader loader = new URLClassLoader(urls, null)) {
            Class<?> color = loader.loadClass("Color");
            Object value = color.getMethod("valueOf", String.class).invoke(null, constant);
            int ordinal = ((Enum<?>) value).ordinal();
            Class<?> cls = loader.loadClass("Client");
            for (String name : List.of("expr", "stmt")) {
                Method m = cls.getMethod(name, color);
                String result;
                try {
                    result = (String) m.invoke(null, value);
                } catch (InvocationTargetException e) {
                    result = e.getCause().getClass().getName();
                }
                IO.println("  %s（ordinal=%d） %s → %s".formatted(constant, ordinal, name, result));
            }
        }
    }

    static Path compile(Path work, String dir, String source, List<String> options) throws IOException {
        Path out = Files.createDirectories(work.resolve(dir));
        Path src = Files.createDirectories(work.resolve(dir + "-src")).resolve("Color.java");
        Files.writeString(src, source);
        javac(out, options, List.of(), src);
        return out;
    }

    static Path compileClient(Path work, String dir, Path colorDir, List<String> options) throws IOException {
        Path out = Files.createDirectories(work.resolve(dir));
        Path src = Files.createDirectories(work.resolve(dir + "-src")).resolve("Client.java");
        Files.writeString(src, CLIENT);
        javac(out, options, List.of("-cp", colorDir.toString()), src);
        return out;
    }

    static void javac(Path out, List<String> options, List<String> cp, Path src) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        List<String> args = new ArrayList<>(options);
        args.addAll(cp);
        args.addAll(List.of("-encoding", "UTF-8", "-d", out.toString(), src.toString()));
        int status = compiler.run(null, null, null, args.toArray(String[]::new));
        if (status != 0) {
            throw new IllegalStateException("コンパイルできない: " + src);
        }
    }
}
