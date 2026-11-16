import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Map;
import java.util.HashMap;

// 共通の状態と実装を持つ場合は抽象クラス
abstract class BaseService {
    protected String serviceId;
    protected boolean isActive;
    protected LocalDateTime startTime;
    
    public BaseService(String serviceId) {
        this.serviceId = serviceId;
        this.isActive = false;
    }
    
    public String getServiceId() {
        return serviceId;
    }
    
    public abstract void initialize();
    public abstract void shutdown();
    
    public final boolean isActive() {
        return isActive;
    }
    
    public Duration getUptime() {
        return isActive ? Duration.between(startTime, LocalDateTime.now()) : Duration.ZERO;
    }
}

// 行動の契約のみを定義する場合はインターフェイス
interface Monitorable {
    void checkHealth();
    Map<String, Object> getMetrics();
    
    default void scheduleHealthCheck() {
        IO.println("ヘルスチェックをスケジュール");
    }
}

class EmailService extends BaseService implements Monitorable {
    private int sentCount = 0;
    private int failureCount = 0;
    
    public EmailService(String serviceId) {
        super(serviceId);
    }
    
    @Override
    public void initialize() {
        isActive = true;
        startTime = LocalDateTime.now();
        IO.println(serviceId + " メールサービスを起動");
    }
    
    @Override
    public void shutdown() {
        isActive = false;
        IO.println(serviceId + " メールサービスを停止");
    }
    
    @Override
    public void checkHealth() {
        IO.println("メールサービスのヘルスチェック実行");
    }
    
    @Override
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("sent_count", sentCount);
        metrics.put("failure_count", failureCount);
        metrics.put("uptime_seconds", getUptime().getSeconds());
        return metrics;
    }
}
