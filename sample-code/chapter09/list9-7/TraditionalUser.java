/**
 * リスト9-7
 * TraditionalUserクラス
 * 
 * 元ファイル: chapter09-records.md (300行目)
 */

// 従来のデータクラス：約100行のコード
public class TraditionalUser {
    private final String id;
    private final String name;
    private final String email;
    private final LocalDateTime createdAt;
    
    // コンストラクタ、getter、equals、hashCode、toString...
    // 約90行のボイラープレートコード
}

// Record：1行で同等の機能
public record User(String id, String name, String email, LocalDateTime createdAt) {}