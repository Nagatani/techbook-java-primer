// StudentCsvImporter.java - CSVの行から学部生を登録する
// 1行の失敗で全体を止めず、失敗した行と理由を結果に集める
import java.util.ArrayList;
import java.util.List;

public class StudentCsvImporter {
    private final StudentService service;

    public StudentCsvImporter(StudentService service) {
        this.service = service;
    }

    // lines の1行目はヘッダー（学生ID,氏名,学部,入学年度）
    public ImportReport importLines(List<String> lines) {
        int successCount = 0;
        List<ImportError> errors = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            int lineNumber = i + 1; // ファイルの行番号（ヘッダーが1行目）
            String line = lines.get(i);
            try {
                service.register(parse(line));
                successCount++;
            } catch (IllegalArgumentException e) {
                // 列の数・数値の形式・空の項目など、行の内容の誤り
                errors.add(new ImportError(lineNumber, line, e.getMessage()));
            } catch (DuplicateStudentException e) {
                errors.add(new ImportError(lineNumber, line, e.getMessage()));
            }
        }
        return new ImportReport(successCount, errors);
    }

    private Student parse(String line) {
        String[] cols = line.split(",", -1);
        if (cols.length != 4) {
            throw new IllegalArgumentException("列の数が4ではありません（" + cols.length + "列）");
        }
        int year;
        try {
            year = Integer.parseInt(cols[3].strip());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("入学年度が数値ではありません: " + cols[3]);
        }
        return new Student(cols[0], cols[1], cols[2], new Undergraduate(year));
    }
}
