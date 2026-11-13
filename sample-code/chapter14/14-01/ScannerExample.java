import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class ScannerExample {
    public static void main(String[] args) {
        Path path = Path.of("data.txt");
        
        // 1. 行単位の読み込み
        try (Scanner scanner = new Scanner(
                Files.newBufferedReader(path, StandardCharsets.UTF_8))) {
            IO.println("--- Scannerによる行単位の読み込み ---");
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                IO.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 2. デリミターを使った読み込み
        String csvData = "リンゴ,150,赤\nバナナ,100,黄";
        try (Scanner scanner = new Scanner(csvData)) {
            IO.println("\n--- デリミターを使った読み込み ---");
            scanner.useDelimiter(",|\\n");  // コンマまたは改行で区切る
            while (scanner.hasNext()) {
                IO.println(scanner.next());
            }
        }
        
        // 3. 型を指定した読み込み
        String numbers = "100 3.14 true";
        try (Scanner scanner = new Scanner(numbers)) {
            IO.println("\n--- 型を指定した読み込み ---");
            int intValue = scanner.nextInt();
            double doubleValue = scanner.nextDouble();
            boolean boolValue = scanner.nextBoolean();
            IO.println("整数: %d, 小数: %.2f, 真偽値: %b".formatted(
                            intValue, doubleValue, boolValue));
        }
    }
}
