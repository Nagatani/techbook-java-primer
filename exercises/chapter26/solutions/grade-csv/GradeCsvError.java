// GradeCsvError.java - 読めなかった行（見出しを1行目とした行の番号と理由）
public record GradeCsvError(long lineNumber, String reason) {
}
