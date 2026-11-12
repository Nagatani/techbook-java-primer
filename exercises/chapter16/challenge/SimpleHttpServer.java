import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 静的ファイルを配信する簡易HTTPサーバー。GETとHEADに応える
 */
public class SimpleHttpServer {
    private static final int MAX_LINE_LENGTH = 8192;   // リクエストの1行の上限
    private static final int MAX_HEADERS = 100;        // ヘッダーの数の上限
    private static final int READ_TIMEOUT_MILLIS = 5000;
    private static final Map<String, String> MIME_TYPES = Map.of(
            "html", "text/html; charset=UTF-8",
            "css", "text/css; charset=UTF-8",
            "js", "text/javascript; charset=UTF-8",
            "txt", "text/plain; charset=UTF-8",
            "png", "image/png",
            "jpg", "image/jpeg");

    private final int port;
    private final Path root;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private ServerSocket serverSocket;
    private Thread acceptThread;

    public SimpleHttpServer(int port, Path root) {
        this.port = port;
        this.root = root.toAbsolutePath().normalize();
    }

    /** 待ち受けを始める。接続の受け付けは別のスレッドで行う */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        acceptThread = new Thread(this::acceptLoop, "accept");
        acceptThread.start();
    }

    /** 実際に待ち受けているポート番号（0を指定したときは自動で選ばれた番号） */
    public int getPort() {
        return serverSocket.getLocalPort();
    }

    /** 待ち受けをやめ、処理中の接続が終わるのを待つ */
    public void stop() throws IOException, InterruptedException {
        serverSocket.close();  // accept()がSocketExceptionで抜ける
        acceptThread.join();
        threadPool.shutdown();
        if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
            threadPool.shutdownNow();
        }
    }

    private void acceptLoop() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                threadPool.submit(() -> handleClient(socket));
            } catch (SocketException e) {
                return;  // stop()でサーバーソケットが閉じられた
            } catch (IOException e) {
                System.err.println("接続を受け付けられませんでした: " + e.getMessage());
            }
        }
    }

    private void handleClient(Socket socket) {
        try (socket;
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.ISO_8859_1));
             OutputStream out = socket.getOutputStream()) {
            socket.setSoTimeout(READ_TIMEOUT_MILLIS);
            String client = socket.getInetAddress().getHostAddress();
            HttpRequest request;
            try {
                request = parseRequest(reader);
            } catch (IllegalArgumentException e) {
                log(client, "-", "-", 400);
                sendError(out, 400, "Bad Request", true);
                return;
            }
            Response response = createResponse(request);
            log(client, request.method(), request.path(), response.status());
            sendResponse(out, response, !request.method().equals("HEAD"));
        } catch (SocketTimeoutException e) {
            // 一定時間リクエストが届かなかった接続は、何も返さずに閉じる
        } catch (IOException e) {
            System.err.println("通信中にエラーが起きました: " + e.getMessage());
        }
    }

    /** リクエストラインとヘッダーを読む。形が正しくなければIllegalArgumentException */
    private HttpRequest parseRequest(BufferedReader reader) throws IOException {
        String requestLine = readLine(reader);
        if (requestLine == null) {
            throw new IllegalArgumentException("リクエストがありません");
        }
        String[] parts = requestLine.split(" ");
        if (parts.length != 3 || !parts[1].startsWith("/") || !parts[2].startsWith("HTTP/")) {
            throw new IllegalArgumentException("リクエストラインの形が正しくありません");
        }
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = readLine(reader)) != null && !line.isEmpty()) {
            int colon = line.indexOf(':');
            if (colon <= 0 || headers.size() >= MAX_HEADERS) {
                throw new IllegalArgumentException("ヘッダーの形が正しくありません");
            }
            headers.put(line.substring(0, colon).trim().toLowerCase(), line.substring(colon + 1).trim());
        }
        String path = parts[1];
        int query = path.indexOf('?');
        if (query >= 0) {
            path = path.substring(0, query);  // ?より後ろ（クエリ文字列）は使わない
        }
        return new HttpRequest(parts[0], path, parts[2], headers);
    }

    /** 1行を読む。長すぎる行はIllegalArgumentException */
    private String readLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line != null && line.length() > MAX_LINE_LENGTH) {
            throw new IllegalArgumentException("行が長すぎます");
        }
        return line;
    }

    /** 返す内容 */
    private record Response(int status, String reason, String contentType, byte[] body) {
    }

    private Response createResponse(HttpRequest request) {
        if (!request.method().equals("GET") && !request.method().equals("HEAD")) {
            return errorResponse(405, "Method Not Allowed");
        }
        String path = request.path().equals("/") ? "/index.html" : request.path();
        Path file;
        try {
            file = root.resolve(path.substring(1)).normalize();
        } catch (InvalidPathException e) {
            return errorResponse(400, "Bad Request");  // ファイル名に使えない文字を含む
        }
        if (!file.startsWith(root)) {
            return errorResponse(403, "Forbidden");  // ../でルートの外を指している
        }
        if (!Files.isRegularFile(file)) {
            return errorResponse(404, "Not Found");
        }
        try {
            return new Response(200, "OK", contentTypeOf(file), Files.readAllBytes(file));
        } catch (IOException e) {
            return errorResponse(500, "Internal Server Error");
        }
    }

    private String contentTypeOf(Path file) {
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        String extension = dot >= 0 ? name.substring(dot + 1).toLowerCase() : "";
        return MIME_TYPES.getOrDefault(extension, "application/octet-stream");
    }

    private Response errorResponse(int status, String reason) {
        String html = "<!DOCTYPE html><html><body><h1>" + status + " " + reason + "</h1></body></html>\n";
        return new Response(status, reason, "text/html; charset=UTF-8", html.getBytes(StandardCharsets.UTF_8));
    }

    private void sendError(OutputStream out, int status, String reason, boolean withBody) throws IOException {
        sendResponse(out, errorResponse(status, reason), withBody);
    }

    private void sendResponse(OutputStream out, Response response, boolean withBody) throws IOException {
        StringBuilder head = new StringBuilder();
        head.append("HTTP/1.1 ").append(response.status()).append(" ").append(response.reason()).append("\r\n");
        head.append("Content-Type: ").append(response.contentType()).append("\r\n");
        head.append("Content-Length: ").append(response.body().length).append("\r\n");
        if (response.status() == 405) {
            head.append("Allow: GET, HEAD\r\n");
        }
        head.append("Connection: close\r\n");
        head.append("\r\n");
        out.write(head.toString().getBytes(StandardCharsets.ISO_8859_1));
        if (withBody) {
            out.write(response.body());  // HEADのときは本文を送らない
        }
        out.flush();
    }

    /** アクセスログ。複数のスレッドから呼ばれるので、1行ずつまとめて出力する */
    private synchronized void log(String client, String method, String path, int status) {
        IO.println("[log] " + client + " " + method + " " + path + " " + status);
    }

    public static void main(String[] args) throws IOException {
        int port = args.length >= 1 ? Integer.parseInt(args[0]) : 8080;
        Path root = Path.of(args.length >= 2 ? args[1] : "public");
        SimpleHttpServer server = new SimpleHttpServer(port, root);
        server.start();
        IO.println("http://localhost:" + server.getPort() + "/ で待ち受けています（Ctrl+Cで終了）");
        IO.println("ドキュメントルート: " + root);
    }
}
