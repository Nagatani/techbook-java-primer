// ImportDemo.java - CSVの取り込み結果を表示する
import java.util.List;

public class ImportDemo {
    public static void main(String[] args) {
        StudentService service = new StudentService(new InMemoryStudentRepository());
        StudentCsvImporter importer = new StudentCsvImporter(service);

        List<String> lines = List.of(
            "学生ID,氏名,学部,入学年度",
            "S001,山田太郎,工学部,2025",
            "S002,佐藤花子,理学部,二〇二五",
            "S003,鈴木一郎,工学部",
            "S001,田中次郎,文学部,2024",
            "S004,高橋美咲,文学部,2024");

        ImportReport report = importer.importLines(lines);
        IO.println("成功: " + report.successCount() + "件");
        for (ImportError error : report.errors()) {
            IO.println("行%d: %s".formatted(error.lineNumber(), error.reason()));
        }
    }
}
