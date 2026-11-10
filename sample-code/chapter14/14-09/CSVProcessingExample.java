import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class CSVProcessingExample {
    public static void main(String[] args) {
        Path csvFile = Paths.get("sample.csv");
        
        // CSVファイルの作成（サンプルデータ）
        createSampleCSV(csvFile);
        
        // CSVファイルの読み込み
        List<List<String>> csvData = readCSV(csvFile);
        IO.println("読み込んだCSVデータ:");
        csvData.forEach(row -> IO.println(String.join(" | ", row)));
        
        // CSVファイルの書き込み
        writeCSV(Paths.get("output.csv"), csvData);
    }
    
    // CSVファイルの読み込み
    public static List<List<String>> readCSV(Path filePath) {
        List<List<String>> result = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> row = parseCSVLine(line);
                result.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // 簡単なCSV行解析（引用符などの複雑なケースは考慮していません）
    private static List<String> parseCSVLine(String line) {
        return Arrays.asList(line.split(","));
    }
    
    // CSVファイルの書き込み
    public static void writeCSV(Path filePath, List<List<String>> data) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            for (List<String> row : data) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
            IO.println("CSVファイルを書き込みました: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // サンプルCSVファイルの作成
    private static void createSampleCSV(Path filePath) {
        List<List<String>> sampleData = Arrays.asList(
            Arrays.asList("名前", "年齢", "職業"),
            Arrays.asList("田中太郎", "30", "エンジニア"),
            Arrays.asList("佐藤花子", "25", "デザイナー"),
            Arrays.asList("鈴木一郎", "35", "マネージャー")
        );
        writeCSV(filePath, sampleData);
    }
}
