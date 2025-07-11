/**
 * リスト9-27
 * DataPipelineExampleクラス
 * 
 * 元ファイル: chapter09-records.md (1114行目)
 */

public class DataPipelineExample {
    
    // 複雑なデータ変換パイプライン
    public static List<UserAnalytics> generateUserAnalytics(
            Stream<UserEvent> events,
            Duration timeWindow) {
        
        Instant now = Instant.now();
        Instant windowStart = now.minus(timeWindow);
        
        return events
            .filter(event -> getEventTimestamp(event).isAfter(windowStart))
            .collect(Collectors.groupingBy(
                event -> switch (event) {
                    case UserCreated(var id, _, _, _) -> id;
                    case UserUpdated(var id, _, _) -> id;
                    case UserDeleted(var id, _, _) -> id;
                }
            ))
            .entrySet()
            .stream()
            .map(entry -> analyzeUserEvents(entry.getKey(), entry.getValue()))
            .filter(analytics -> analytics.eventCount() > 0)
            .sorted(Comparator.comparing(UserAnalytics::lastActivity).reversed())
            .collect(Collectors.toList());
    }
    
    private static UserAnalytics analyzeUserEvents(String userId, List<UserEvent> events) {
        int eventCount = events.size();
        Instant lastActivity = events.stream()
            .map(DataPipelineExample::getEventTimestamp)
            .max(Instant::compareTo)
            .orElse(Instant.EPOCH);
        
        long creationEvents = events.stream()
            .filter(event -> event instanceof UserCreated)
            .count();
        
        long updateEvents = events.stream()
            .filter(event -> event instanceof UserUpdated)
            .count();
        
        long deletionEvents = events.stream()
            .filter(event -> event instanceof UserDeleted)
            .count();
        
        return new UserAnalytics(
            userId,
            eventCount,
            lastActivity,
            creationEvents,
            updateEvents,
            deletionEvents
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

public record UserAnalytics(
    String userId,
    int eventCount,
    Instant lastActivity,
    long creationEvents,
    long updateEvents,
    long deletionEvents
) {}