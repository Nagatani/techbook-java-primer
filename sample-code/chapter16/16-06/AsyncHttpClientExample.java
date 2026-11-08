import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class AsyncHttpClientExample {
    
    public static void main(String[] args) throws Exception {
        // HTTPクライアントの作成
        HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        
        // HTTPリクエストの作成
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://jsonplaceholder.typicode.com/posts/1"))
            .timeout(Duration.ofMinutes(1))
            .header("Accept", "application/json")
            .GET()
            .build();
        
        // 非同期でリクエストを送信
        CompletableFuture<HttpResponse<String>> future = 
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        
        // 非同期処理の連鎖
        CompletableFuture<Void> done = future
            .thenApply(response -> {
                System.out.println("Status: " + response.statusCode());
                return response.body();
            })
            .thenAccept(body -> {
                System.out.println("Response Body: " + body);
            })
            .exceptionally(e -> {
                System.err.println("Error: " + e.getMessage());
                return null;
            });
        
        // 他の処理を並行して実行可能
        System.out.println("他の処理を継続中...");
        
        // 連鎖させた処理がすべて終わるまで待つ（mainが先に終わるとプログラムが終了するため）
        done.join();
    }
}
