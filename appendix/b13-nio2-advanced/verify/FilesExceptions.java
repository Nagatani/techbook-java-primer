import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * java.io.Fileのメソッドは失敗をbooleanで返し、Filesのメソッドは理由ごとの例外で知らせることを観察する。
 */
public class FilesExceptions {
    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("files-ex");
        Path file = dir.resolve("a.txt");
        Path missing = dir.resolve("missing.txt");
        Path sub = dir.resolve("sub");
        Files.writeString(file, "a");
        Files.createDirectory(sub);
        Files.writeString(sub.resolve("inner.txt"), "x");

        IO.println("--- java.io.File（失敗はfalse）");
        IO.println("File.delete（ない）: " + missing.toFile().delete());
        IO.println("File.delete（空でないディレクトリ）: " + sub.toFile().delete());
        IO.println("File.createNewFile（ある）: " + file.toFile().createNewFile());
        IO.println("File.mkdir（ある）: " + new File(sub.toString()).mkdir());

        IO.println("--- java.nio.file.Files（失敗は例外）");
        attempt("Files.delete（ない）", () -> Files.delete(missing));
        attempt("Files.delete（空でないディレクトリ）", () -> Files.delete(sub));
        attempt("Files.createFile（ある）", () -> Files.createFile(file));
        attempt("Files.createDirectory（ある）", () -> Files.createDirectory(sub));
        attempt("Files.copy（コピー先がある）", () -> Files.copy(file, sub.resolve("inner.txt")));
        attempt("Files.readString（ない）", () -> Files.readString(missing));
        attempt("Files.copy（REPLACE_EXISTING）",
                () -> Files.copy(file, sub.resolve("inner.txt"), StandardCopyOption.REPLACE_EXISTING));
        try {
            Files.delete(missing);
        } catch (FileSystemException e) {
            // NoSuchFileExceptionなどはFileSystemExceptionのサブクラスで、対象のファイルを持つ
            IO.println("FileSystemExceptionとして捕まえた: " + e.getClass().getSimpleName()
                    + " getFile()のファイル名=" + Path.of(e.getFile()).getFileName());
        }
        IO.println("Files.deleteIfExists（ない）: " + Files.deleteIfExists(missing));
        IO.println("Files.createDirectories（ある）: "
                + Files.createDirectories(sub).getFileName());

        // 後片付け
        Files.delete(sub.resolve("inner.txt"));
        Files.delete(sub);
        Files.delete(file);
        Files.delete(dir);
    }

    interface IOAction {
        void run() throws IOException;
    }

    private static void attempt(String label, IOAction action) {
        try {
            action.run();
            IO.println(label + ": 成功");
        } catch (IOException e) {
            IO.println(label + ": " + e.getClass().getSimpleName());
        }
    }
}
