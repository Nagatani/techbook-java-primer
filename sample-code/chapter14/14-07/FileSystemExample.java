import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

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
        // Files.copy()はファイルのコピーを行います。StandardCopyOptionで
        // 動作を制御できます。
        // REPLACE_EXISTING: 同名ファイルがある場合は上書き
        // COPY_ATTRIBUTES: 最終更新日時などの属性もコピー（どの属性を
        // コピーできるかはOSによる）
        Path copiedFile = dir.resolve("my_file_copy.txt");
        Files.copy(file, copiedFile,
                StandardCopyOption.REPLACE_EXISTING);
        IO.println("ファイルをコピーしました: " + copiedFile.getFileName());

        // ディレクトリの中身の一覧
        // Files.list()はStream（第12章）を返す。開いたディレクトリを
        // 閉じるため、try-with-resourcesで囲む。並び順はOSによるので並べ替える
        IO.println("ディレクトリの中身:");
        try (Stream<Path> entries = Files.list(dir)) {
            entries.map(p -> p.getFileName().toString())
                    .sorted()
                    .forEach(name -> IO.println("  " + name));
        }

        // ファイルの削除
        Files.delete(copiedFile);
        Files.delete(file);
        Files.delete(dir);
        IO.println("ファイルとディレクトリを削除しました。");
    }
}
