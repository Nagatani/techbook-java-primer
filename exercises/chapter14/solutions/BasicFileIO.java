import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * テキストとバイナリの読み書き（課題1の解答例）
 *
 * 実行したフォルダーの下にio-basic-workフォルダーを作り、その中にファイルを書く。
 */
public class BasicFileIO {
    public static void main(String[] args) {
        Path dir = Path.of("io-basic-work");
        Path text = dir.resolve("diary.txt");
        Path data = dir.resolve("data.bin");
        Path copy = dir.resolve("data-copy.bin");
        try {
            if (Files.notExists(dir)) {
                Files.createDirectory(dir);
            }
            // 文字ストリームで書く
            try (BufferedWriter writer = Files.newBufferedWriter(text, StandardCharsets.UTF_8)) {
                writer.write("今日はJavaのファイル入出力を学んだ");
                writer.newLine();
                writer.write("BufferedWriterで1行ずつ書く");
                writer.newLine();
                writer.write("最後の行");
                writer.newLine();
            }
            IO.println("書き込み: " + text.getFileName());
            // 1行ずつ読む
            IO.println("--- BufferedReaderで読む ---");
            try (BufferedReader reader = Files.newBufferedReader(text, StandardCharsets.UTF_8)) {
                String line;
                int number = 1;
                while ((line = reader.readLine()) != null) {
                    IO.println(number + ": " + line);
                    number++;
                }
            }
            // まとめて読む
            List<String> lines = Files.readAllLines(text, StandardCharsets.UTF_8);
            IO.println("readAllLinesの行数: " + lines.size());
            IO.println("ファイルの大きさ: " + Files.size(text) + "バイト");
            // バイトストリームで書いてコピーする
            try (OutputStream out = new FileOutputStream(data.toFile())) {
                for (int i = 0; i < 256; i++) {
                    out.write(i);
                }
            }
            int copied = 0;
            try (InputStream in = new FileInputStream(data.toFile());
                 OutputStream out = new FileOutputStream(copy.toFile())) {
                byte[] buffer = new byte[100];
                int n;
                while ((n = in.read(buffer)) != -1) {
                    out.write(buffer, 0, n);
                    copied += n;
                }
            }
            IO.println("--- バイナリのコピー ---");
            IO.println("コピーしたバイト数: " + copied);
            byte[] a = Files.readAllBytes(data);
            byte[] b = Files.readAllBytes(copy);
            IO.println("先頭の4バイト: " + a[0] + " " + a[1] + " " + a[2] + " " + a[3]);
            IO.println("中身が同じ: " + Arrays.equals(a, b));
        } catch (IOException e) {
            System.err.println("入出力エラー: " + e.getMessage());
        }
    }
}
