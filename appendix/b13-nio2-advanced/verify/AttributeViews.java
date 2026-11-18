import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Map;
import java.util.TreeSet;

/**
 * ファイルの属性を読む「ビュー」は、ファイルシステムによって使えるものが違うことを観察する。
 */
public class AttributeViews {
    public static void main(String[] args) throws IOException {
        FileSystem def = FileSystems.getDefault();
        IO.println("OS: " + System.getProperty("os.name"));
        IO.println("既定のファイルシステムのビュー: " + new TreeSet<>(def.supportedFileAttributeViews()));
        Path dir = Files.createTempDirectory("attr");
        Path zip = dir.resolve("a.zip");
        try (FileSystem zipfs = FileSystems.newFileSystem(zip, Map.of("create", "true"))) {
            IO.println("ZIPのビュー: " + new TreeSet<>(zipfs.supportedFileAttributeViews()));
            Path entry = Files.writeString(zipfs.getPath("/entry.txt"), "zip");
            // 使えないビューを型で求めると、例外ではなくnullが返る
            IO.println("ZIPの中のファイルのposixビュー: "
                    + Files.getFileAttributeView(entry, PosixFileAttributeView.class));
        }
        Files.deleteIfExists(zip);
        Files.delete(dir);
        FileSystem jrt = FileSystems.getFileSystem(URI.create("jrt:/"));
        IO.println("jrtのビュー: " + new TreeSet<>(jrt.supportedFileAttributeViews()));

        Path file = Files.createTempFile("attr", ".txt");
        Files.writeString(file, "12345");
        BasicFileAttributes basic = Files.readAttributes(file, BasicFileAttributes.class);
        IO.println("basic: size=" + basic.size() + " regularFile=" + basic.isRegularFile()
                + " directory=" + basic.isDirectory());
        // 名前で読むこともできる（"ビュー名:属性名"、ビュー名を省くとbasic）
        IO.println("Files.getAttribute(\"size\"): " + Files.getAttribute(file, "size"));
        Map<String, Object> many = Files.readAttributes(file, "basic:size,isDirectory");
        IO.println("Files.readAttributes(\"basic:size,isDirectory\"): " + new TreeSet<>(many.keySet()));

        // 既定のファイルシステムで使えるビューはOSによって違う
        PosixFileAttributeView posix = Files.getFileAttributeView(file, PosixFileAttributeView.class);
        DosFileAttributeView dos = Files.getFileAttributeView(file, DosFileAttributeView.class);
        IO.println("posixビュー: " + (posix == null ? "null" : "あり")
                + " / dosビュー: " + (dos == null ? "null" : "あり"));
        if (posix != null) {
            Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-------"));
            IO.println("posixの許可: "
                    + PosixFilePermissions.toString(posix.readAttributes().permissions()));
        }
        // 名前で使えないビューを指定すると例外
        try {
            Files.getAttribute(file, "nosuchview:size");
        } catch (UnsupportedOperationException e) {
            IO.println("存在しないビュー名: " + e.getClass().getSimpleName());
        }
        Files.delete(file);
    }
}
