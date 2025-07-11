/**
 * リスト9-19
 * コードスニペット
 * 
 * 元ファイル: chapter09-records.md (697行目)
 */

// 基本的なコンパクトコンストラクタ
public record ValidatedEmail(String value) {
    public ValidatedEmail {
        if (value == null || !value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email: " + value);
        }
        value = value.toLowerCase(); // 正規化
    }
    
    // 追加のメソッド
    public String domain() {
        return value.substring(value.indexOf('@') + 1);
    }
    
    public String localPart() {
        return value.substring(0, value.indexOf('@'));
    }
}