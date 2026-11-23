import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TextFileReaderExample {
    public static void main(String[] args) {
        Path filePath = Path.of("sample.txt");
        // 事前にファイルを作成
        try { Files.writeString(filePath, "Line 1\nLine 2"); } catch (IOException e) { e.printStackTrace(); }

        // 方法1: 1行ずつ読み込む (大きなファイルに最適)
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

        // 方法2: 全行を一度に読み込む (小さなファイル向き)
        IO.println("\n--- 全行を一度に読み込み ---");
        try {
            List<String> lines = Files.readAllLines(
                    filePath, StandardCharsets.UTF_8);
            lines.forEach(IO::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
