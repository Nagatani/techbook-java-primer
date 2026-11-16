// ImportReport.java - 取り込みの結果。成功した件数と、失敗した行の一覧
import java.util.List;

public record ImportReport(int successCount, List<ImportError> errors) {
    public ImportReport {
        errors = List.copyOf(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
