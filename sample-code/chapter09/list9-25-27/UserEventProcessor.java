/**
 * リスト9-26
 * UserEventProcessorクラス
 * 
 * 元ファイル: chapter09-records.md (1042行目)
 */

// イベントストリームの処理
public class UserEventProcessor {
    
    // イベントからスナップショットへの集約
    public static Map<String, UserSnapshot> processEvents(Stream<UserEvent> events) {
        return events.reduce(
            new HashMap<String, UserSnapshot>(),
            UserEventProcessor::applyEvent,
            (map1, map2) -> { map1.putAll(map2); return map1; }
        );
    }
    
    // 単一イベントの適用
    private static Map<String, UserSnapshot> applyEvent(
            Map<String, UserSnapshot> snapshots, 
            UserEvent event) {
        
        var updated = new HashMap<>(snapshots);
        
        switch (event) {
            case UserCreated(var id, var name, var email, var time) -> {
                updated.put(id, new UserSnapshot(id, name, email, true, time));
            }
            
            case UserUpdated(var id, var changes, var time) -> {
                var current = updated.get(id);
                if (current != null) {
                    var newName = (String) changes.getOrDefault("name", current.name());
                    var newEmail = (String) changes.getOrDefault("email", current.email());
                    updated.put(id, new UserSnapshot(id, newName, newEmail, current.active(), time));
                }
            }
            
            case UserDeleted(var id, var reason, var time) -> {
                var current = updated.get(id);
                if (current != null) {
                    updated.put(id, new UserSnapshot(
                        id, current.name(), current.email(), false, time));
                }
            }
        }
        
        return updated;
    }
    
    // 特定時点での状態復元
    public static Map<String, UserSnapshot> replayEventsUntil(
            Stream<UserEvent> events, 
            Instant cutoff) {
        return events
            .filter(event -> getEventTimestamp(event).isBefore(cutoff))
            .reduce(
                new HashMap<String, UserSnapshot>(),
                UserEventProcessor::applyEvent,
                (map1, map2) -> { map1.putAll(map2); return map1; }
            );
    }
    
    private static Instant getEventTimestamp(UserEvent event) {
        return switch (event) {
            case UserCreated(_, _, _, var timestamp) -> timestamp;
            case UserUpdated(_, _, var timestamp) -> timestamp;
            case UserDeleted(_, _, var timestamp) -> timestamp;
        };
    }
}