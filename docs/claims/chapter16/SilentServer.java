// 第16章コラム「タイムアウトを設定しない通信は、いつか障害になる」：何も返さないサーバーへの要求
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/** 接続は受け付けるが、何も返さないサーバーにHTTPで要求する */
public class SilentServer {
    public static void main(String[] args) throws Exception {
        List<Socket> accepted = new CopyOnWriteArrayList<>();
        try (ServerSocket server = new ServerSocket(0, 50, InetAddress.getLoopbackAddress())) {
            Thread.ofPlatform().daemon().start(() -> {
                try {
                    while (true) {
                        accepted.add(server.accept());   // 受け付けるだけで、何も返さない
                    }
                } catch (Exception e) {
                    // サーバーを閉じたら終わる
                }
            });
            URI uri = URI.create("http://127.0.0.1:" + server.getLocalPort() + "/");
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            IO.println("HttpURLConnectionの既定: connect=" + conn.getConnectTimeout() + " read=" + conn.getReadTimeout());
            HttpClient client = HttpClient.newHttpClient();
            IO.println("connectTimeoutの既定: " + client.connectTimeout());

            HttpRequest noTimeout = HttpRequest.newBuilder(uri).build();
            IO.println("timeoutの既定: " + noTimeout.timeout());
            CompletableFuture<HttpResponse<String>> waiting =
                    client.sendAsync(noTimeout, HttpResponse.BodyHandlers.ofString());
            TimeUnit.SECONDS.sleep(3);
            IO.println("タイムアウトなし: 3秒後も終わっていない = " + !waiting.isDone());
            waiting.cancel(true);

            HttpRequest withTimeout = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(1)).build();
            long start = System.nanoTime();
            try {
                client.send(withTimeout, HttpResponse.BodyHandlers.ofString());
            } catch (HttpTimeoutException e) {
                long ms = (System.nanoTime() - start) / 1_000_000;
                IO.println("タイムアウト1秒: " + e.getClass().getSimpleName() + "（約" + Math.round(ms / 1000.0) + "秒）");
            }
        }
        for (Socket s : accepted) {
            s.close();
        }
    }
}
