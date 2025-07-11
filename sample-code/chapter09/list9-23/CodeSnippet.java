/**
 * リスト9-23
 * コードスニペット
 * 
 * 元ファイル: chapter09-records.md (897行目)
 */

import java.io.Serializable;

public record SerializableUser(
    String id,
    String name,
    String email,
    LocalDateTime createdAt
) implements Serializable {
    
    // SerialVersionUIDの明示的定義（推奨）
    private static final long serialVersionUID = 1L;
    
    public SerializableUser {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(createdAt, "Created at cannot be null");
    }
    
    // カスタムシリアライゼーション制御（必要に応じて）
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject();
        // カスタム処理があれば追加
    }
    
    private void readObject(java.io.ObjectInputStream in) 
            throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        // デシリアライゼーション後のバリデーション
        if (id == null || name == null || email == null || createdAt == null) {
            throw new java.io.InvalidObjectException("Required fields cannot be null");
        }
    }
}