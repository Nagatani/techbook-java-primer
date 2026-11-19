// 第26章：java.timeパッケージ（Java 8で標準）
import java.time.LocalDate;

public class JavaTimeProbe {
    static LocalDate tomorrow() {
        return LocalDate.now().plusDays(1);
    }
}
