import java.util.Map;

/**
 * 解析したHTTPリクエスト（リクエストラインとヘッダー）
 */
public record HttpRequest(String method, String path, String version, Map<String, String> headers) {
}
