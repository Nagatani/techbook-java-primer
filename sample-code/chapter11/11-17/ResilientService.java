import java.util.function.Supplier;

public class ResilientService {
    // 関数型リトライメカニズム
    public <T> Supplier<T> withRetry(Supplier<T> supplier, int maxAttempts) {
        return () -> {
            for (int attempt = 1; ; attempt++) {
                try {
                    return supplier.get();  // 成功したら結果を返す
                } catch (RuntimeException e) {
                    if (attempt >= maxAttempts) {
                        throw new RuntimeException("Max attempts reached", e);
                    }
                    // 失敗したら、もう一度呼び出す
                }
            }
        };
    }
    
    // 使用例
    public String fetchDataWithRetry() {
        Supplier<String> unreliableService = () -> {
            if (Math.random() < 0.7) throw new RuntimeException("Service unavailable");
            return "Success!";
        };
        
        Supplier<String> resilientService = withRetry(unreliableService, 3);
        return resilientService.get();
    }
}
