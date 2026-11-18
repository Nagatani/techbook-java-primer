import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Pathはファイルシステム（FileSystem）に属すること、
 * 標準のファイルシステムのほかにZIPとjrt:/のファイルシステムがあることを観察する。
 */
public class FileSystemsTour {
    public static void main(String[] args) throws IOException {
        // 1. インストールされているプロバイダー（スキームの一覧）
        List<String> schemes = FileSystemProvider.installedProviders().stream()
                .map(FileSystemProvider::getScheme).toList();
        IO.println("プロバイダーのスキーム: " + schemes);

        // 2. 既定のファイルシステム
        FileSystem def = FileSystems.getDefault();
        Path p = Path.of("data.txt");
        IO.println("Path.ofのファイルシステムは既定のもの: " + (p.getFileSystem() == def));
        IO.println("既定のスキーム: " + def.provider().getScheme());
        IO.println("既定のプロバイダーのクラス: " + def.provider().getClass().getName());
        IO.println("存在しないファイルのPathも作れる: " + p + " exists=" + Files.exists(p));

        // 3. ZIPファイルシステム（jdk.zipfsモジュール）
        Path dir = Files.createTempDirectory("fstour");
        Path zip = dir.resolve("sample.zip");
        try (FileSystem zipfs = FileSystems.newFileSystem(zip, Map.of("create", "true"))) {
            Path entry = zipfs.getPath("/docs/hello.txt");
            Files.createDirectories(entry.getParent());
            Files.writeString(entry, "ZIPの中のファイル");
            IO.println("ZIPのスキーム: " + zipfs.provider().getScheme());
            IO.println("ZIPのプロバイダーのモジュール: "
                    + zipfs.provider().getClass().getModule().getName());
        }
        try (FileSystem zipfs = FileSystems.newFileSystem(zip)) {
            Path entry = zipfs.getPath("/docs/hello.txt");
            IO.println("ZIPから読んだ内容: " + Files.readString(entry));
            try {
                // 別のファイルシステムのPathどうしは組み合わせられない
                Path.of("a").resolve(entry);
                IO.println("resolveできた");
            } catch (Exception e) {
                IO.println("別のファイルシステムのPathをresolve: " + e.getClass().getName());
            }
            // Files.copyはファイルシステムをまたいでコピーできる
            Path out = dir.resolve("hello.txt");
            Files.copy(entry, out);
            IO.println("ZIPから既定のファイルシステムへコピー: " + Files.readString(out));
        }

        // 4. jrt:/ファイルシステム（JDKのクラスファイルを読む）
        FileSystem jrt = FileSystems.getFileSystem(URI.create("jrt:/"));
        Path object = jrt.getPath("/modules/java.base/java/lang/Object.class");
        byte[] bytes = Files.readAllBytes(object);
        IO.println("jrtのスキーム: " + jrt.provider().getScheme());
        IO.println("Object.classの先頭4バイト: %02X%02X%02X%02X".formatted(
                bytes[0], bytes[1], bytes[2], bytes[3]));
        try (Stream<Path> modules = Files.list(jrt.getPath("/modules"))) {
            IO.println("java.baseモジュールがある: "
                    + modules.anyMatch(m -> m.getFileName().toString().equals("java.base")));
        }

        // 後片付け
        Files.delete(dir.resolve("hello.txt"));
        Files.delete(zip);
        Files.delete(dir);
    }
}
