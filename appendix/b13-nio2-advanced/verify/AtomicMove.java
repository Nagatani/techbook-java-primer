import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * 設定ファイルなどを安全に書き換える「一時ファイルに書いてから移動する」方法と、
 * Files.moveのオプションによる違いを観察する。
 */
public class AtomicMove {
    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("move");
        Path config = dir.resolve("config.txt");
        Files.writeString(config, "version=1");

        // 1. オプションなしで、移動先がすでにあると例外
        Path tmp1 = dir.resolve("config.txt.tmp");
        Files.writeString(tmp1, "version=2");
        try {
            Files.move(tmp1, config);
        } catch (FileAlreadyExistsException e) {
            IO.println("オプションなし: " + e.getClass().getSimpleName());
        }

        // 2. REPLACE_EXISTINGで置き換える
        Files.move(tmp1, config, StandardCopyOption.REPLACE_EXISTING);
        IO.println("REPLACE_EXISTING: " + Files.readString(config));

        // 3. ATOMIC_MOVE。移動先があるときの扱いは実装によって決まる
        Path tmp2 = dir.resolve("config.txt.tmp");
        Files.writeString(tmp2, "version=3");
        try {
            Files.move(tmp2, config, StandardCopyOption.ATOMIC_MOVE);
            IO.println("ATOMIC_MOVE（移動先あり）: 置き換わった " + Files.readString(config));
        } catch (IOException e) {
            IO.println("ATOMIC_MOVE（移動先あり）: " + e.getClass().getSimpleName());
        }
        IO.println("一時ファイルは残っていない: " + Files.notExists(tmp2));

        // 後片付け
        Files.deleteIfExists(tmp2);
        Files.delete(config);
        Files.delete(dir);
    }
}
