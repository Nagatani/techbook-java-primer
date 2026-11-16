// GradeCsvCheck.java - 成績のCSVの読み込みを確かめる
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GradeCsvCheck {
    public static void main(String[] args) throws IOException {
        // 先頭のBOM、カンマを含む科目名、誤りのある行を含むCSV
        String csv = """
            \uFEFF学生ID,科目,単位数,評価点
            S001,線形代数,4,3.0
            S001,"情報科学演習Ⅰ,Ⅱ",2,4.0
            S002,微分積分,4,4.5
            S002,英語,二,3.0
            S003,物理学
            """;
        GradeCsvResult result = new GradeCsvReader()
            .read(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));

        for (GradeRow row : result.rows()) {
            IO.println("%s %s %d単位 %.1f".formatted(row.studentId(), row.grade().course(),
                row.grade().credits(), row.grade().point()));
        }
        for (GradeCsvError error : result.errors()) {
            IO.println("行%d: %s".formatted(error.lineNumber(), error.reason()));
        }
        check(result.rows().size() == 2, "読めた行は2行");
        check(result.errors().size() == 3, "誤りのある行は3行");
        check(result.rows().get(1).grade().course().equals("情報科学演習Ⅰ,Ⅱ"), "カンマを含む科目名");
        IO.println("すべての確認に成功しました");
    }

    private static void check(boolean ok, String label) {
        if (!ok) {
            throw new AssertionError("失敗: " + label);
        }
    }
}
