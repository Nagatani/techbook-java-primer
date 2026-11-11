import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// 防御的コピーの実装例
public final class DateRange {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final List<LocalDate> holidays;
    
    public DateRange(LocalDate start, LocalDate end, List<LocalDate> holidays) {
        // パラメーターの検証
        if (start == null || end == null) {
            throw new IllegalArgumentException("日付はnullにできません");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("開始日は終了日より前である必要があります");
        }
        
        this.startDate = start;  // LocalDateは不変なのでコピー不要
        this.endDate = end;
        
        // 防御的コピー：外部のリストが変更されても影響を受けない
        this.holidays = holidays != null 
            ? new ArrayList<>(holidays) 
            : new ArrayList<>();
    }
    
    public List<LocalDate> getHolidays() {
        // 防御的コピー：内部リストの参照を返さない
        return new ArrayList<>(holidays);
    }
    
    // 配列の場合の防御的コピー
    public LocalDate[] getHolidaysAsArray() {
        return holidays.toArray(new LocalDate[0]);
    }
}
