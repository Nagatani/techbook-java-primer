import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 予定。idが0のものは、まだデータベースに登録していない予定
 */
public record Event(int id, String title, LocalDateTime start, LocalDateTime end, String place) {
    public Event {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("題名がありません");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("終わりの時刻が始まりの時刻より後ではありません: " + title);
        }
    }

    /** 登録前の予定を作る */
    public Event(String title, LocalDateTime start, LocalDateTime end, String place) {
        this(0, title, start, end, place);
    }

    public Event withId(int newId) {
        return new Event(newId, title, start, end, place);
    }

    /** 同じ長さのまま、始まりの時刻を移した予定を返す */
    public Event movedTo(LocalDateTime newStart) {
        return new Event(id, title, newStart, newStart.plus(Duration.between(start, end)), place);
    }
}
