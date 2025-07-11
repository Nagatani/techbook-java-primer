/**
 * リスト9-25
 * コードスニペット
 * 
 * 元ファイル: chapter09-records.md (1008行目)
 */

// イベントの定義
public sealed interface UserEvent permits UserCreated, UserUpdated, UserDeleted {}

public record UserCreated(
    String userId, 
    String name, 
    String email, 
    Instant timestamp
) implements UserEvent {}

public record UserUpdated(
    String userId, 
    Map<String, Object> changes, 
    Instant timestamp
) implements UserEvent {}

public record UserDeleted(
    String userId, 
    String reason, 
    Instant timestamp
) implements UserEvent {}

// ユーザーの状態スナップショット
public record UserSnapshot(
    String userId,
    String name,
    String email,
    boolean active,
    Instant lastModified
) {}