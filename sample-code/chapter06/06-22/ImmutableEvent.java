import java.util.Date;

public final class ImmutableEvent {
    private final String title;
    private final Date eventDate;
    
    public ImmutableEvent(String title, Date eventDate) {
        this.title = title;
        // 防御的コピー（入力時）
        this.eventDate = new Date(eventDate.getTime());
    }
    
    public String getTitle() {
        return title;
    }
    
    public Date getEventDate() {
        // 防御的コピー（出力時）
        return new Date(eventDate.getTime());
    }
}
