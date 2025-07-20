/**
 * EmailServiceクラス
 * 
 * 元ファイル: chapter07-abstract-classes-and-interfaces.md (1562行目)
 * 
 * 抽象クラスを継承し、インターフェイスを実装する例
 */
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
        System.out.println(serviceId + " メールサービスを起動");
    }
    
    @Override
    public void shutdown() {
        isActive = false;
        System.out.println(serviceId + " メールサービスを停止");
    }
    
    @Override
    public void checkHealth() {
        System.out.println("メールサービスのヘルスチェック実行");
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