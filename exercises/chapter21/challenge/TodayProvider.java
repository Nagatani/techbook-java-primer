import java.time.LocalDate;

/** 今日の日付を返す。実際のアプリでは LocalDate::now を渡す */
public interface TodayProvider {
    LocalDate today();
}
