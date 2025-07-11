/**
 * リスト15-5
 * CharacterEncodingExampleクラス
 * 
 * 元ファイル: chapter15-file-io.md (230行目)
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CharacterEncodingExample {
    public static void main(String[] args) {
        Path path = Paths.get("japanese.txt");
        
        // UTF-8で書き込み
        try {
            Files.writeString(path, "こんにちは、世界！", StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Shift_JISで読み込み（文字化けの例）
        System.out.println("--- Shift_JISで読み込み（文字化け） ---");
        try (BufferedReader reader = Files.newBufferedReader(
                path, Charset.forName("Shift_JIS"))) {
            System.out.println(reader.readLine());  // 文字化けが発生
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // UTF-8で正しく読み込み
        System.out.println("\n--- UTF-8で正しく読み込み ---");
        try (BufferedReader reader = Files.newBufferedReader(
                path, StandardCharsets.UTF_8)) {
            System.out.println(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // システムデフォルトのエンコーディング確認
        System.out.println("\nデフォルトエンコーディング: " + Charset.defaultCharset());
    }
}