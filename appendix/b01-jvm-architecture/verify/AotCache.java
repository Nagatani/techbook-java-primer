import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * AOTキャッシュ（Java 24のJEP 483、Java 25のJEP 514）を作り、
 * 次の起動でクラスがキャッシュから読み込まれることを観察する。
 *
 * このプログラムは、次のコマンドを子プロセスとして順に実行する（一時ディレクトリで行い、最後に消す）。
 *   jar  （AotCache$App をJARファイルにまとめる。AOTキャッシュはクラスパスにディレクトリを使えない）
 *   java -XX:AOTCacheOutput=app.aot -cp app.jar AotCache$App        （訓練の実行とキャッシュの作成）
 *   java -Xlog:class+load -cp app.jar AotCache$App                  （キャッシュなし）
 *   java -XX:AOTCache=app.aot -Xlog:class+load -cp app.jar AotCache$App   （キャッシュあり）
 *
 *   javac AotCache.java && java AotCache
 */
public class AotCache {
    /** キャッシュを作る対象のアプリケーション */
    public static class App {
        public static void main(String[] args) {
            List<String> words = new ArrayList<>(List.of("loader", "jvm", "bytecode", "class"));
            String joined = words.stream()
                    .map(String::toUpperCase)
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.joining(","));
            IO.println(joined);
        }
    }

    static final String JAVA = Path.of(System.getProperty("java.home"), "bin", "java").toString();
    static final String MAIN = "AotCache$App";

    public static void main(String[] args) throws Exception {
        Path work = Files.createTempDirectory("aot-cache-");
        try {
            Path jar = work.resolve("app.jar");
            writeJar(jar);

            List<String> training = run(work, JAVA, "-XX:AOTCacheOutput=app.aot", "-cp", "app.jar", MAIN);
            training.stream().filter(l -> l.startsWith("AOTCache creation is complete"))
                    .map(l -> l.replaceAll("\\d+ bytes", "(大きさ) bytes"))
                    .forEach(l -> IO.println("訓練の実行: " + l));

            List<String> plain = run(work, JAVA, "-Xlog:class+load", "-cp", "app.jar", MAIN);
            List<String> cached = run(work, JAVA, "-XX:AOTCache=app.aot", "-Xlog:class+load", "-cp", "app.jar", MAIN);

            IO.println("キャッシュなし: " + source(plain));
            IO.println("キャッシュあり: " + source(cached));
            IO.println("キャッシュなしで、CDSのアーカイブ以外から読み込んだクラスの数: " + notShared(plain));
            IO.println("キャッシュありで、CDSのアーカイブ以外から読み込んだクラスの数: " + notShared(cached));
        } finally {
            try (Stream<Path> files = Files.walk(work)) {
                files.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
            }
        }
    }

    /** AotCache$App のクラスファイルをJARファイルにまとめる */
    static void writeJar(Path jar) throws IOException {
        String name = MAIN + ".class";
        try (var in = AotCache.class.getResourceAsStream(name);
             JarOutputStream out = new JarOutputStream(Files.newOutputStream(jar))) {
            out.putNextEntry(new JarEntry(name));
            in.transferTo(out);
            out.closeEntry();
        }
    }

    /** 子プロセスでコマンドを実行し、標準出力と標準エラー出力の行を返す */
    static List<String> run(Path dir, String... command) throws IOException, InterruptedException {
        Process p = new ProcessBuilder(command).directory(dir.toFile()).redirectErrorStream(true).start();
        p.getOutputStream().close();
        List<String> lines = new String(p.getInputStream().readAllBytes()).lines().toList();
        p.waitFor();
        return lines;
    }

    /** -Xlog:class+loadの出力から、AotCache$Appの読み込み元（source:）を取り出す */
    static String source(List<String> log) {
        return log.stream().filter(l -> l.contains(" " + MAIN + " source: "))
                .map(l -> l.substring(l.indexOf(MAIN)).replaceAll("file:.*/app\\.jar", "file:.../app.jar"))
                .findFirst().orElse("(見つからない)");
    }

    static long notShared(List<String> log) {
        return log.stream().filter(l -> l.contains("[class,load]") && !l.contains("source: shared objects file")).count();
    }
}
