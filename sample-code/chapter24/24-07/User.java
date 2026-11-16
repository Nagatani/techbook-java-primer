// User.java - usersの表の1行に対応するRecord（エンティティ）
import java.time.LocalDateTime;

public record User(int id, String username, String email, LocalDateTime createdAt) {
    // まだ保存していない（IDと登録日時をデータベースが決める）ユーザー
    public static User newUser(String username, String email) {
        return new User(0, username, email, null);
    }

    public User withEmail(String newEmail) {
        return new User(id, username, newEmail, createdAt);
    }
}
