/**
 * リスト15-2
 * ScannerExampleクラス
 * 
 * 元ファイル: chapter15-file-io.md (99行目)
 */

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class ScannerExample {
    public static void main(String[] args) {
        Path path = Paths.get("data.txt");
        
        // 1. 行単位の読み込み
        try (Scanner scanner = new Scanner(
                Files.newBufferedReader(path, StandardCharsets.UTF_8))) {
            System.out.println("--- Scannerによる行単位の読み込み ---");
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 2. デリミターを使った読み込み
        String csvData = "リンゴ,150,赤\nバナナ,100,黄";
        try (Scanner scanner = new Scanner(csvData)) {
            scanner.useDelimiter(",|\\n");  // コンマまたは改行で区切る
            while (scanner.hasNext()) {
                System.out.println(scanner.next());
            }
        }
        
        // 3. 型を指定した読み込み
        String numbers = "100 3.14 true";
        try (Scanner scanner = new Scanner(numbers)) {
            int intValue = scanner.nextInt();
            double doubleValue = scanner.nextDouble();
            boolean boolValue = scanner.nextBoolean();
            System.out.printf("整数: %d, 小数: %.2f, 真偽値: %b%n", 
                            intValue, doubleValue, boolValue);
        }
    }
}