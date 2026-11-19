// 第26章：java.net.httpのHttpClient（Java 11で標準）
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class HttpClientProbe {
    static void make() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder(URI.create("https://example.com/")).build();
    }
}
