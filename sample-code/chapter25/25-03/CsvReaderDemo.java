// CsvReaderDemo.java - 引用符やBOMを含むCSVを読む
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CsvReaderDemo {
    public static void main(String[] args) throws IOException {
        String csv = """
            \uFEFF学生ID,氏名,学部,入学年度
            S001,山田太郎,工学部,2025
            S002,"Smith, John",理学部,2025
            S003,"佐藤 ""ハナ"" 花子",文学部,2024
            """;
        var in = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

        for (CsvRow row : new StudentCsvReader().read(in)) {
            IO.println("行%d: %s / %s".formatted(
                row.lineNumber(), row.get("学生ID"), row.get("氏名")));
        }
    }
}
