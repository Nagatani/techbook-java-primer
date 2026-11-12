import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * サーバーを起動し、いくつかのリクエストを送って応答を確かめる
 */
public class HttpServerDemo {
    private static final String HOST = "127.0.0.1";

    public static void main(String[] args) throws IOException, InterruptedException {
        // ポート番号0を指定すると、空いているポートが自動で選ばれる
        SimpleHttpServer server = new SimpleHttpServer(0, Path.of("public"));
        server.start();
        int port = server.getPort();
        try {
            IO.println("=== HttpClientでGET ===");
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create("http://" + HOST + ":" + port + "/")).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            IO.println("ステータス: " + response.statusCode());
            IO.println("Content-Type: " + response.headers().firstValue("Content-Type").orElse("なし"));
            IO.println("本文の1行目: " + response.body().lines().findFirst().orElse(""));

            IO.println("");
            IO.println("=== ソケットで直接送る ===");
            sendRaw(port, "GET /style.css HTTP/1.1");
            sendRaw(port, "HEAD /index.html HTTP/1.1");
            sendRaw(port, "GET /nothing.html HTTP/1.1");
            sendRaw(port, "POST /index.html HTTP/1.1");
            sendRaw(port, "GET /../SimpleHttpServer.java HTTP/1.1");
            sendRaw(port, "HELLO");
        } finally {
            server.stop();
        }
        IO.println("");
        IO.println("サーバーを停止しました");
    }

    /** リクエストラインとHostヘッダーだけのリクエストを送り、応答の要点を表示する */
    private static void sendRaw(int port, String requestLine) throws IOException {
        try (Socket socket = new Socket(HOST, port);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {
            String request = requestLine + "\r\nHost: " + HOST + "\r\n\r\n";
            out.write(request.getBytes(StandardCharsets.ISO_8859_1));
            out.flush();

            // サーバーは応答を送ると接続を閉じるので、終わりまで読めば応答の全体になる
            byte[] all = in.readAllBytes();
            String text = new String(all, StandardCharsets.ISO_8859_1);
            int headerEnd = text.indexOf("\r\n\r\n");
            String[] headerLines = text.substring(0, headerEnd).split("\r\n");
            int bodyBytes = all.length - (headerEnd + 4);
            String contentLength = "";
            for (String line : headerLines) {
                if (line.startsWith("Content-Length:")) {
                    contentLength = line.substring("Content-Length:".length()).trim();
                }
            }
            String body = bodyBytes == 0 ? "本文なし"
                    : String.valueOf(bodyBytes).equals(contentLength) ? "本文はContent-Lengthと同じ長さ"
                    : "本文の長さがContent-Lengthと違う";
            IO.println(requestLine + " -> " + headerLines[0] + "（" + body + "）");
        }
    }
}
