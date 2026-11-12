/**
 * 1件の注文。食券番号（例: A1）は窓口ごとに付ける
 */
public record Order(String ticket, String customer, Menu menu) {
}
