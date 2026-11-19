// 第6章：java.util.Date（Java 1.0）とjava.util.Calendar（Java 1.1）
import java.util.Calendar;
import java.util.Date;

public class DateCalendar {
    static Date f() {
        Calendar c = Calendar.getInstance();
        c.set(2026, Calendar.JANUARY, 1);
        return c.getTime();
    }
}
