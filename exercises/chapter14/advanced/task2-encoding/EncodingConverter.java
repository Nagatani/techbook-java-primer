import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** テキストファイルの文字エンコーディングを調べ、UTF-8に変換する */
public class EncodingConverter {
    /** UTF-8として正しく読めるかを調べる */
    public static boolean isUtf8(Path path) throws IOException {
        try {
            Files.readString(path, StandardCharsets.UTF_8);
            return true;
        } catch (MalformedInputException e) {
            // UTF-8では表せないバイトの並びがあった
            return false;
        }
    }

    /**
     * fromCharsetで読み、UTF-8で書き出す。変換した行数を返す
     * 1行ずつ読み書きするので、大きなファイルでもメモリをあまり使わない
     */
    public static int convertToUtf8(Path source, Charset fromCharset, Path target) throws IOException {
        int lines = 0;
        try (BufferedReader reader = Files.newBufferedReader(source, fromCharset);
             BufferedWriter writer = Files.newBufferedWriter(target, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.write('\n');  // 改行はLFにそろえる（newLine()はOSによって変わる）
                lines++;
            }
        }
        return lines;
    }
}
