/**
 * BaseServiceクラス（抽象クラスの適切な使用例）
 * 
 * 元ファイル: chapter07-abstract-classes-and-interfaces.md (1526行目)
 * 
 * 共通の状態と実装を持つ場合は抽象クラスを使用する良い例
 */
import java.time.Duration;
import java.time.LocalDateTime;

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