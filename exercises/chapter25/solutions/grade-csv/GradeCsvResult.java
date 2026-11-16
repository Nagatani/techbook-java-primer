// GradeCsvResult.java - 読み込みの結果（読めた成績と、読めなかった行）
import java.util.List;

public record GradeCsvResult(List<GradeRow> rows, List<GradeCsvError> errors) {
    public GradeCsvResult {
        rows = List.copyOf(rows);
        errors = List.copyOf(errors);
    }
}
