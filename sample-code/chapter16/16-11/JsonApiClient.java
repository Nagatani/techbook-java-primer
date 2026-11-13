import java.net.URI;
import java.net.http.*;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonApiClient {
    private final HttpClient client;
    private final ObjectMapper mapper;
    
    public JsonApiClient() {
        this.client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
        this.mapper = new ObjectMapper();
    }
    
    // GETリクエスト
    public <T> T get(String url, Class<T> responseType) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Accept", "application/json")
            .GET()
            .build();
        
        HttpResponse<String> response = client.send(request, 
            HttpResponse.BodyHandlers.ofString());
        
        return readBody(response, responseType);
    }
    
    // POSTリクエスト
    public <T> T post(String url, Object data, Class<T> responseType) 
            throws Exception {
        String json = mapper.writeValueAsString(data);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();
        
        HttpResponse<String> response = client.send(request,
            HttpResponse.BodyHandlers.ofString());
        
        return readBody(response, responseType);
    }
    
    // ステータスコードが成功（200番台）のときだけ、JSONをオブジェクトに変換する
    private <T> T readBody(HttpResponse<String> response, Class<T> responseType)
            throws Exception {
        int status = response.statusCode();
        if (status >= 200 && status < 300) {
            return mapper.readValue(response.body(), responseType);
        } else {
            throw new RuntimeException("HTTP error: " + status);
        }
    }
    
    public static void main(String[] args) throws Exception {
        JsonApiClient apiClient = new JsonApiClient();
        
        // GET例：ユーザー情報の取得
        Map<String, Object> user = apiClient.get(
            "https://jsonplaceholder.typicode.com/users/1",
            Map.class);
        IO.println("User: " + user);
        
        // POST例：新しい投稿の作成
        Map<String, Object> newPost = new HashMap<>();
        newPost.put("title", "New Post");
        newPost.put("body", "This is a new post");
        newPost.put("userId", 1);
        
        Map<String, Object> result = apiClient.post(
            "https://jsonplaceholder.typicode.com/posts",
            newPost, Map.class);
        IO.println("Created: " + result);
    }
}
