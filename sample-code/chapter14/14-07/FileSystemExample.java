import java.io.IOException;
import java.nio.file.*;

public class FileSystemExample {
    public static void main(String[] args) throws IOException {
        Path dir = Path.of("my_temp_dir");
        Path file = dir.resolve("my_file.txt");

        // ディレクトリ作成
        // Files.createDirectory()は指定されたディレクトリを作成します。
        // createDirectories()を使用すると、親ディレクトリがなければ同時に作成されます。
        if (Files.notExists(dir)) {
            Files.createDirectory(dir);
            IO.println("ディレクトリを作成しました: " + dir);
        }

        // ファイル作成と書き込み
        // writeString()は、小さなテキストデータをファイルに保存する便利メソッドです。
        // ファイルを開いて書き込み、閉じるまでをメソッドのなかで済ませます。
        Files.writeString(file, "Hello, NIO.2!");
        IO.println("ファイルを作成しました: " + file.getFileName());

        // ファイルのコピー
        // Files.copy()はファイルのコピーを行います。StandardCopyOptionで動作を制御できます。
        // REPLACE_EXISTING: 同名ファイルがある場合は上書き
        // COPY_ATTRIBUTES: 最終更新日時などの属性もコピー（どの属性をコピーできるかはOSによる）
        Path copiedFile = dir.resolve("my_file_copy.txt");
        Files.copy(file, copiedFile, StandardCopyOption.REPLACE_EXISTING);
        IO.println("ファイルをコピーしました: " + copiedFile.getFileName());

        // ファイルの削除
        Files.delete(copiedFile);
        Files.delete(file);
        Files.delete(dir);
        IO.println("ファイルとディレクトリを削除しました。");
    }
}
