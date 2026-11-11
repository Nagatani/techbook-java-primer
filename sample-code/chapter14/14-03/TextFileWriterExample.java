import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class TextFileWriterExample {
    public static void main(String[] args) {
        Path filePath = Paths.get("output.txt");

        // ファイルへの書き込み
        // BufferedWriterを使用してテキストデータを効率的にファイルに書き込みます。
        // CREATE: ファイルが存在しない場合は新規作成
        // TRUNCATE_EXISTING: 既存ファイルがある場合は内容を消去して上書き
        try (BufferedWriter writer = Files.newBufferedWriter(
                filePath, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, 
                StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write("最初の行。");
            writer.newLine(); // 改行
            writer.write("次の行。");
            IO.println("ファイルに書き込みました。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
