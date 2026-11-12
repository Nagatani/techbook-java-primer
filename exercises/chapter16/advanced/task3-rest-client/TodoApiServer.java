import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

/**
 * 試す相手になる、手元で動く小さなToDoのAPIサーバー
 * JDKに含まれるcom.sun.net.httpserverを使う。データはメモリにだけ持つ
 *
 * GET /todos、GET /todos/番号、POST /todos、PUT /todos/番号、DELETE /todos/番号
 * どのリクエストにも Authorization: Bearer secret-token が要る
 */
public class TodoApiServer {
    static final String TOKEN = "secret-token";

    private final HttpServer server;
    private final Map<Integer, String> todos = new TreeMap<>(); // 番号 -> JSONの本文
    private int nextId = 1;

    public TodoApiServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        server.createContext("/todos", this::handle);
        todos.put(nextId, todoJson(nextId++, "牛乳を買う", false));
        todos.put(nextId, todoJson(nextId++, "レポートを出す", true));
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    /** 実際に使っているポート番号（0を指定したときは空いている番号が選ばれる） */
    public int getPort() {
        return server.getAddress().getPort();
    }

    private static String todoJson(int id, String title, boolean done) {
        return "{\"id\":" + id + ",\"title\":\"" + title + "\",\"done\":" + done + "}";
    }

    // 複数のスレッドから呼ばれることがあるので、synchronizedにする
    private synchronized void handle(HttpExchange ex) throws IOException {
        String auth = ex.getRequestHeaders().getFirst("Authorization");
        if (!("Bearer " + TOKEN).equals(auth)) {
            reply(ex, 401, "{\"error\":\"認証が必要です\"}");
            return;
        }
        String[] parts = ex.getRequestURI().getPath().split("/"); // ["", "todos", 番号]
        String method = ex.getRequestMethod();
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (parts.length == 2) {
            if (method.equals("GET")) {
                reply(ex, 200, "[" + String.join(",", todos.values()) + "]");
            } else if (method.equals("POST")) {
                int id = nextId++;
                String json = "{\"id\":" + id + "," + body.substring(1);
                todos.put(id, json);
                reply(ex, 201, json);
            } else {
                reply(ex, 405, "{\"error\":\"使えないメソッドです\"}");
            }
            return;
        }
        int id;
        try {
            id = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            reply(ex, 400, "{\"error\":\"番号が正しくありません\"}");
            return;
        }
        if (!todos.containsKey(id)) {
            reply(ex, 404, "{\"error\":\"" + id + "番はありません\"}");
            return;
        }
        switch (method) {
            case "GET" -> reply(ex, 200, todos.get(id));
            case "PUT" -> {
                String json = "{\"id\":" + id + "," + body.substring(1);
                todos.put(id, json);
                reply(ex, 200, json);
            }
            case "DELETE" -> {
                todos.remove(id);
                reply(ex, 204, "");
            }
            default -> reply(ex, 405, "{\"error\":\"使えないメソッドです\"}");
        }
    }

    private static void reply(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        // 本文がないとき（204）は-1を渡す
        ex.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }
}
