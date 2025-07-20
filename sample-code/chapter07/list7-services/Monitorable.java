/**
 * Monitorableインターフェイス
 * 
 * 元ファイル: chapter07-abstract-classes-and-interfaces.md (1553行目)
 * 
 * 行動の契約のみを定義する場合はインターフェイスを使用
 */
import java.util.Map;

// 行動の契約のみを定義する場合はインターフェイス
interface Monitorable {
    void checkHealth();
    Map<String, Object> getMetrics();
    
    default void scheduleHealthCheck() {
        System.out.println("ヘルスチェックをスケジュール");
    }
}