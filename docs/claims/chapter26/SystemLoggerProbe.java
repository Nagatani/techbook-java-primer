// 第26章：System.Logger（Java 9から）
public class SystemLoggerProbe {
    static void log() {
        System.Logger logger = System.getLogger("app");
        logger.log(System.Logger.Level.INFO, "start");
    }
}
