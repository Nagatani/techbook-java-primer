// ImportError.java - 取り込めなかった行（行番号・行の内容・理由）
public record ImportError(int lineNumber, String line, String reason) {
}
