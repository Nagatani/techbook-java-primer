import java.io.IOException;
import java.nio.file.*;

public class FileSystemExample {
    public static void main(String[] args) throws IOException {
        Path dir = Paths.get("my_temp_dir");
        Path file = dir.resolve("my_file.txt");

        // ディレクトリ操作
        // Files.createDirectory()は指定されたディレクトリを作成します。
        // createDirectories()を使用すると、必要に応じて親ディレクトリも同時に作成されます。
        // ディレクトリ作成
        if (Files.notExists(dir)) {
            Files.createDirectory(dir);
            System.out.println("ディレクトリを作成しました: " + dir);
        }

        // テキストファイルの一括書き込み
        // writeString()は小容量のテキストデータを簡単にファイルに保存できる便利メソッドです。
        // 内部的にはBufferedWriterが使用されており、効率的な書き込みが行われます。
        // ファイル作成と書き込み
        Files.writeString(file, "Hello, NIO.2!");
        System.out.println("ファイルを作成しました: " + file);

        // ファイル属性の取得
        // Files.copy()はファイルのコピーを行います。StandardCopyOptionで動作を制御できます。
        // REPLACE_EXISTING: 同名ファイルがある場合は上書き
        // COPY_ATTRIBUTES: ファイルの属性（作成日時、アクセス権など）もコピー
        // ファイルのコピー
        Path copiedFile = dir.resolve("my_file_copy.txt");
        Files.copy(file, copiedFile, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("ファイルをコピーしました: " + copiedFile);

        // ファイルの削除
        Files.delete(copiedFile);
        Files.delete(file);
        Files.delete(dir);
        System.out.println("ファイルとディレクトリを削除しました。");
    }
}
