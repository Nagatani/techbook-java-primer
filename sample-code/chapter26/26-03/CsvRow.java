// CsvRow.java - CSVの1行（ファイルの行番号と、見出しから値を引くMap）
import java.util.Map;

public record CsvRow(long lineNumber, Map<String, String> values) {
    public CsvRow {
        values = Map.copyOf(values);
    }

    public String get(String header) {
        String value = values.get(header);
        if (value == null) {
            throw new IllegalArgumentException("列「" + header + "」がありません");
        }
        return value;
    }
}
