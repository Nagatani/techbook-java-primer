import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * ログを標準出力に短い形で出す設定
 * 標準のConsoleHandlerは日時を付けて標準エラー出力に出すため、ここでは自前のHandlerを使う
 */
public class LogSetup {
    public static void configure() {
        Logger root = Logger.getLogger("");
        for (Handler handler : root.getHandlers()) {
            root.removeHandler(handler);
        }
        root.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                IO.println("  [" + record.getLevel() + "] " + record.getMessage());
                // 記録した例外に原因が連鎖していれば、その種類とメッセージも出す
                Throwable thrown = record.getThrown();
                if (thrown != null && thrown.getCause() != null) {
                    Throwable cause = thrown.getCause();
                    IO.println("      原因: " + cause.getClass().getSimpleName() + ": " + cause.getMessage());
                }
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        });
    }
}
