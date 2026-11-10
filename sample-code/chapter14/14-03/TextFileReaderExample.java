import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TextFileReaderExample {
    public static void main(String[] args) {
        Path filePath = Paths.get("sample.txt");
        // 事前にファイルを作成
        try { Files.writeString(filePath, "Line 1\nLine 2"); } catch (IOException e) {}

        // 方法1: 1行ずつ読み込む (大きなファイルに最適)
        // BufferedReaderを使用することで、ファイルから効率的に行単位でデータを読み込めます。
        // 大容量ファイルでもメモリを節約しながら処理できるため、実用的なアプリケーションではもっとも推奨される方法です。
        IO.println("--- 1行ずつ読み込み ---");
        try (BufferedReader reader = Files.newBufferedReader(
                filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                IO.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // readAllLines()は小さなファイル（数MBまで）に適しています。
        // 大容量ファイルではメモリ不足の原因となるため、上記の1行ずつ読み込み方式を使用してください。
        // 方法2: 全行を一度に読み込む (小さなファイル向き)
        IO.println("\n--- 全行を一度に読み込み ---");
        // readAllLines()は小容量ファイル専用の便利メソッドです。
        // 設定ファイルや小さなデータファイルの処理に適していますが、大容量ファイルではOutOfMemoryErrorの原因となります。
        try {
            List<String> lines = Files.readAllLines(
                    filePath, StandardCharsets.UTF_8);
            lines.forEach(IO::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
