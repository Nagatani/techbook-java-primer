/**
 * リスト13-8
 * ResilientServiceクラス
 * 
 * 元ファイル: chapter13-lambda-and-functional-interfaces.md (282行目)
 */

public class ResilientService {
    // 関数型リトライメカニズム
    public <T> Supplier<T> withRetry(Supplier<T> supplier, int maxAttempts) {
        return () -> {
            AtomicInteger attempts = new AtomicInteger(0);
            
            return Stream.generate(() -> {
                try {
                    return Optional.of(supplier.get());
                } catch (Exception e) {
                    if (attempts.incrementAndGet() >= maxAttempts) {
                        throw new RuntimeException("Max attempts reached", e);
                    }
                    return Optional.<T>empty();
                }
            })
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .orElseThrow();
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