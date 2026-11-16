import java.util.logging.*;
import java.time.Instant;
import java.util.Map;

public class StructuredExceptionLogger {
    private static final Logger logger = 
        Logger.getLogger(StructuredExceptionLogger.class.getName());
    
    public static void logException(String operation, 
                                  Exception exception, 
                                  Map<String, Object> context) {
        // 構造化されたログエントリを作成
        LogRecord record = new LogRecord(Level.SEVERE, 
            createStructuredMessage(operation, exception, context));
        
        record.setThrown(exception);
        record.setSourceClassName(getCallerClassName());
        record.setSourceMethodName(getCallerMethodName());
        
        logger.log(record);
    }
    
    private static String createStructuredMessage(String operation, 
                                                Exception exception, 
                                                Map<String, Object> context) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"timestamp\":\"").append(Instant.now()).append("\",");
        sb.append("\"operation\":\"").append(operation).append("\",");
        sb.append("\"error_type\":\"").append(exception.getClass().getName()).append("\",");
        sb.append("\"error_message\":\"").append(escapeJson(exception.getMessage())).append("\",");
        sb.append("\"context\":{");
        
        boolean first = true;
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":\"")
              .append(escapeJson(String.valueOf(entry.getValue()))).append("\"");
            first = false;
        }
        
        sb.append("}}");
        return sb.toString();
    }
    
    private static String escapeJson(String text) {
        if (text == null) return "null";
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r");
    }
    
    private static String getCallerClassName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace.length > 3 ? stackTrace[3].getClassName() : "Unknown";
    }
    
    private static String getCallerMethodName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace.length > 3 ? stackTrace[3].getMethodName() : "Unknown";
    }
}
