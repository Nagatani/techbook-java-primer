/**
 * リスト9-4
 * CsvReaderクラス
 * 
 * 元ファイル: chapter09-records.md (191行目)
 */

// CsvReader.java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class CsvReader {
    public static void main(String[] args) {
        Path filePath = Path.of("data.csv");
        try {
            List<PersonRecord> persons = Files.lines(filePath) // ファイルを1行ずつのStreamに
                .map(line -> line.split(",")) // 各行をカンマで分割
                .filter(fields -> fields.length == 3) // 配列の長さが3であることを確認
                .map(fields -> new PersonRecord(
                    fields[0],                      // name
                    Integer.parseInt(fields[1]),    // age
                    fields[2]                       // city
                ))
                .collect(Collectors.toList()); // 結果をListに集約

            persons.forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("ファイルの読み込みに失敗しました: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("年齢の形式が正しくありません: " + e.getMessage());
        }
    }
}