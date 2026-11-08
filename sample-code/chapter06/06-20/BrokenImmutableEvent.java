import java.util.Date;

public final class BrokenImmutableEvent {
    private final String title;
    private final Date eventDate; // 問題: Dateは可変
    
    public BrokenImmutableEvent(String title, Date eventDate) {
        this.title = title;
        this.eventDate = eventDate; // 問題: 直接代入
    }
    
    public Date getEventDate() {
        return eventDate; // 問題: 直接返却
    }
}
