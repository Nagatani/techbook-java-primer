import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class CSVProcessingExample {
    public static void main(String[] args) {
        Path csvFile = Path.of("sample.csv");
        // 読み書きの失敗は、呼び出し側のmainでまとめて受け止める
        try {
            createSampleCSV(csvFile);
            
            List<List<String>> csvData = readCSV(csvFile);
            IO.println("読み込んだCSVデータ:");
            csvData.forEach(row -> IO.println(String.join(" | ", row)));
            
            writeCSV(Path.of("output.csv"), csvData);
        } catch (IOException e) {
            System.err.println("CSVファイルを読み書きできません: "
                    + e.getMessage());
        }
    }
    
    public static List<List<String>> readCSV(Path filePath)
            throws IOException {
        List<List<String>> result = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath,
                StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> row = parseCSVLine(line);
                result.add(row);
            }
        }
        return result;
    }
    
    // 簡単なCSV行解析（引用符などの複雑なケースは考慮していません）
    private static List<String> parseCSVLine(String line) {
        // -1: 末尾の空の項目も残す
        return Arrays.asList(line.split(",", -1));
    }
    
    public static void writeCSV(Path filePath, List<List<String>> data)
            throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                StandardCharsets.UTF_8)) {
            for (List<String> row : data) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
            IO.println("CSVファイルを書き込みました: " + filePath);
        }
    }
    
    // サンプルCSVファイルの作成
    private static void createSampleCSV(Path filePath)
            throws IOException {
        List<List<String>> sampleData = Arrays.asList(
            Arrays.asList("名前", "年齢", "職業"),
            Arrays.asList("田中太郎", "30", "エンジニア"),
            Arrays.asList("佐藤花子", "25", "デザイナー"),
            Arrays.asList("鈴木一郎", "35", "マネージャー")
        );
        writeCSV(filePath, sampleData);
    }
}
