import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * HttpClientでWeb APIにリクエストを送るクライアント
 */
public class RestApiClient {
    private final HttpClient client;
    private final String baseUrl;
    private final String token; // nullなら認証のヘッダーを付けない

    public RestApiClient(String baseUrl, String token) {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5)) // 接続のタイムアウト
                .build();
        this.baseUrl = baseUrl;
        this.token = token;
    }

    public String get(String path) throws IOException, InterruptedException, ApiException {
        return send(builder(path).GET().build());
    }

    public String post(String path, String json) throws IOException, InterruptedException, ApiException {
        return send(builder(path)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build());
    }

    public String put(String path, String json) throws IOException, InterruptedException, ApiException {
        return send(builder(path)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build());
    }

    public String delete(String path) throws IOException, InterruptedException, ApiException {
        return send(builder(path).DELETE().build());
    }

    /** 複数のGETを同時に送り、すべての応答の本文を、渡した順に返す */
    public List<String> getAll(List<String> paths) {
        List<CompletableFuture<HttpResponse<String>>> futures = new ArrayList<>();
        for (String path : paths) {
            futures.add(client.sendAsync(builder(path).GET().build(), HttpResponse.BodyHandlers.ofString()));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        List<String> bodies = new ArrayList<>();
        for (CompletableFuture<HttpResponse<String>> f : futures) {
            HttpResponse<String> response = f.join();
            bodies.add(response.statusCode() + " " + response.body());
        }
        return bodies;
    }

    // 共通の設定（URL、リクエストのタイムアウト、認証のヘッダー）
    private HttpRequest.Builder builder(String path) {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(5));
        if (token != null) {
            b.header("Authorization", "Bearer " + token);
        }
        return b;
    }

    private String send(HttpRequest request) throws IOException, InterruptedException, ApiException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        if (status < 200 || status >= 300) {
            throw new ApiException(status, response.body());
        }
        return response.body();
    }
}
