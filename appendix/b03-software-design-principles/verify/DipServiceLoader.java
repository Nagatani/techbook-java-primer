import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 依存関係逆転の原則：使う側はインターフェイスだけを知り、実装はServiceLoaderで見つける。
 * 実装の一覧は、クラスパス上の META-INF/services/（インターフェイスの名前）というファイルに書く。
 * ふつうはJARに入れるが、ここでは一時ディレクトリに書き、そこを読むクラスローダーで探す。
 * 実行: javac DipServiceLoader.java && java DipServiceLoader
 * 観察: javap -c -p DipServiceLoader （greetAllが実装クラスの名前を含まないこと）
 */
public class DipServiceLoader {
    // 上位の方針が決めるインターフェイス
    public interface Greeter {
        String greet(String name);
    }

    // 下位の詳細（実装）。publicで引数なしのコンストラクターが要る
    public static class JapaneseGreeter implements Greeter {
        public String greet(String name) {
            return "こんにちは、" + name;
        }
    }

    public static class EnglishGreeter implements Greeter {
        public String greet(String name) {
            return "Hello, " + name;
        }
    }

    // 使う側：Greeterだけを知っている
    static void greetAll(ClassLoader loader) {
        ServiceLoader<Greeter> found = ServiceLoader.load(Greeter.class, loader);
        for (Greeter g : found) {
            IO.println(g.greet("Java"));
        }
    }

    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("services");
        Path config = dir.resolve("META-INF/services/" + Greeter.class.getName());
        Files.createDirectories(config.getParent());
        Files.writeString(config, """
                # 実装クラスの名前を1行に1つ書く
                DipServiceLoader$JapaneseGreeter
                DipServiceLoader$EnglishGreeter
                """);
        IO.println("設定ファイル: META-INF/services/" + Greeter.class.getName());
        try (URLClassLoader loader = new URLClassLoader(new URL[] {dir.toUri().toURL()},
                DipServiceLoader.class.getClassLoader())) {
            greetAll(loader);
        } finally {
            Files.delete(config);
            Files.delete(config.getParent());
            Files.delete(config.getParent().getParent());
            Files.delete(dir);
        }

        // JDK自身も同じしくみでファイルシステムの実装を見つけている
        List<String> schemes = FileSystemProvider.installedProviders().stream()
                .map(FileSystemProvider::getScheme)
                .sorted(Comparator.naturalOrder())
                .toList();
        IO.println("インストールされたファイルシステムの実装: " + schemes);
    }
}
