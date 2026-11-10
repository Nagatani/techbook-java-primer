import java.util.concurrent.Callable;
import java.io.IOException;

public class RetryableOperation {
    private static final int MAX_RETRY_COUNT = 3;
    private static final long RETRY_DELAY_MS = 1000;
    
    public static <T> T executeWithRetry(Callable<T> operation, 
                                       Class<? extends Exception> retryableException) 
                                       throws Exception {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= MAX_RETRY_COUNT; attempt++) {
            try {
                return operation.call();
            } catch (Exception e) {
                if (!retryableException.isInstance(e)) {
                    throw e; // リトライ対象外の例外は即座に再スロー
                }
                
                lastException = e;
                IO.println("試行 %d/%d 失敗: %s".formatted(
                    attempt, MAX_RETRY_COUNT, e.getMessage()));
                
                if (attempt < MAX_RETRY_COUNT) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("リトライ中に中断されました", ie);
                    }
                }
            }
        }
        
        throw new RuntimeException("最大リトライ回数に達しました", lastException);
    }
    
    // 使用例
    public static void main(String[] args) {
        try {
            String result = executeWithRetry(() -> {
                // 一時的に失敗する可能性のある処理
                if (Math.random() < 0.7) {
                    throw new IOException("ネットワークエラー");
                }
                return "成功";
            }, IOException.class);
            
            IO.println("結果: " + result);
        } catch (Exception e) {
            System.err.println("最終的に失敗: " + e.getMessage());
        }
    }
}
