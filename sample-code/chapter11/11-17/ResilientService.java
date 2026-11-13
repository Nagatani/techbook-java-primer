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
    
    // 失敗を再現するため、呼び出した回数をフィールドで数える
    private int calls = 0;

    // 使用例：2回失敗し、3回目で成功するサービスを呼び出す
    public String fetchDataWithRetry() {
        Supplier<String> unreliableService = () -> {
            calls++;
            IO.println(calls + "回目の呼び出し");
            if (calls < 3) {
                throw new RuntimeException("Service unavailable");
            }
            return "Success!";
        };

        Supplier<String> resilientService = withRetry(unreliableService, 3);
        return resilientService.get();
    }

    public static void main(String[] args) {
        ResilientService service = new ResilientService();
        IO.println("結果: " + service.fetchDataWithRetry());
    }
}
