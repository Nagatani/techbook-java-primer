import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class CharacterEncodingExample {
    public static void main(String[] args) {
        Path path = Path.of("japanese.txt");

        // UTF-8で書き込み
        try {
            Files.writeString(path, "こんにちは", StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Shift_JISで読み込み（文字化けの例）
        // UTF-8のバイト列をShift_JISの表で解釈するため、別の文字になる
        IO.println("--- Shift_JISで読み込み（文字化け） ---");
        try (BufferedReader reader = Files.newBufferedReader(
                path, Charset.forName("Shift_JIS"))) {
            IO.println(reader.readLine());
        } catch (MalformedInputException e) {
            // Shift_JISとして解釈できないバイト列があると、文字化けではなく例外になる
            IO.println("エラー: MalformedInputException - 文字エンコーディングの不一致");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // UTF-8で正しく読み込み
        IO.println("\n--- UTF-8で正しく読み込み ---");
        try (BufferedReader reader = Files.newBufferedReader(
                path, StandardCharsets.UTF_8)) {
            IO.println(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // システムデフォルトのエンコーディング確認
        IO.println("\nデフォルトエンコーディング: " + Charset.defaultCharset());
    }
}
