package chapter16.solutions;

import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * HttpURLConnectionでGETリクエストを送る（基礎課題2の解答例）
 *
 * 引数にURLを渡すと、そのURLから取得する。引数を省くと、手元で小さなHTTPサーバーを立て、
 * 200を返すページと404を返すページに順にアクセスする（ネットワークにつながっていなくても試せる）。
 */
public class HttpClientExample {
    private static final int TIMEOUT_MS = 5000;

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            fetch(args[0]);
            return;
        }
        HttpServer server = startLocalServer();
        try {
            int port = server.getAddress().getPort();
            fetch("http://localhost:" + port + "/hello");
            IO.println();
            fetch("http://localhost:" + port + "/missing");
        } finally {
            server.stop(0);
        }
    }

    static void fetch(String address) {
        IO.println("GET " + address.replaceFirst(":\\d+/", ":(ポート)/"));
        HttpURLConnection connection = null;
        try {
            URL url = URI.create(address).toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // 相手が応答しないときに、いつまでも待たないようにする
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);

            int status = connection.getResponseCode();
            IO.println("ステータス: " + status);
            if (status != HttpURLConnection.HTTP_OK) {
                IO.println("200ではないので、本文は読みません");
                return;
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    IO.println("本文: " + line);
                }
            }
        } catch (IOException e) {
            // つながらない、タイムアウトした、などの失敗
            System.err.println("取得できませんでした: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // /helloだけに200で答え、ほかのパスには404を返すサーバー。ポート0を渡すと、空いているポートが選ばれる
    private static HttpServer startLocalServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", exchange -> {
            boolean found = exchange.getRequestURI().getPath().equals("/hello");
            byte[] body = (found ? "こんにちは、HTTP！\n2行目の本文です\n" : "見つかりません\n")
                    .getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(found ? 200 : 404, body.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        server.start();
        return server;
    }
}
