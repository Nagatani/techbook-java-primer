import java.util.HashMap;
import java.util.Map;

// サンプルコード11-10を動かすための最小限のクラス

class Request {
    private final String method;
    private final String path;
    private final String clientId;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, Object> attributes = new HashMap<>();

    Request(String method, String path, String clientId, String token) {
        this.method = method;
        this.path = path;
        this.clientId = clientId;
        headers.put("Authorization", token);
    }

    String getMethod() { return method; }
    String getPath() { return path; }
    String getClientId() { return clientId; }
    String getHeader(String name) { return headers.get(name); }
    void setAttribute(String name, Object value) { attributes.put(name, value); }
    Object getAttribute(String name) { return attributes.get(name); }
}

class Response {
    private final int status;
    private final String body;

    private Response(int status, String body) {
        this.status = status;
        this.body = body;
    }

    static Response success() { return new Response(200, "OK"); }
    static Response success(String body) { return new Response(200, body); }
    static Response unauthorized() { return new Response(401, "Unauthorized"); }
    static Response tooManyRequests() { return new Response(429, "Too Many Requests"); }

    boolean isSuccess() { return status == 200; }

    @Override
    public String toString() { return status + " " + body; }
}

class Logger {
    // "{}" を引数で順に置き換えて出力する
    void info(String format, Object... args) {
        String message = format;
        for (Object arg : args) {
            message = message.replaceFirst("\\{\\}", String.valueOf(arg));
        }
        System.out.println("[INFO] " + message);
    }
}

class TokenService {
    boolean isValid(String token) { return "valid-token".equals(token); }
    String getUser(String token) { return "alice"; }
}

class RateLimiter {
    private final int limit;
    private final Map<String, Integer> counts = new HashMap<>();

    RateLimiter(int limit) { this.limit = limit; }

    boolean tryAcquire(String clientId) {
        int count = counts.merge(clientId, 1, Integer::sum);
        return count <= limit;
    }
}

class BusinessLogic {
    Response handle(Request request) {
        return Response.success("注文一覧（ユーザー: " + request.getAttribute("user") + "）");
    }
}
