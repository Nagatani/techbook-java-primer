import java.time.LocalDateTime;

public final class ModernImmutableEvent {
    private final String title;
    private final LocalDateTime eventDateTime;
    
    public ModernImmutableEvent(String title, LocalDateTime eventDateTime) {
        this.title = title;
        this.eventDateTime = eventDateTime; // LocalDateTimeは不変
    }
    
    public String getTitle() {
        return title;
    }
    
    public LocalDateTime getEventDateTime() {
        return eventDateTime; // そのまま返却可能
    }
}
