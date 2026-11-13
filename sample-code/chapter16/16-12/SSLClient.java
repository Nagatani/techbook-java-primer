import javax.net.ssl.*;
import java.io.*;

public class SSLClient {
    public static void main(String[] args) {
        String host = "www.example.com";
        int port = 443;

        SSLSocketFactory factory =
            (SSLSocketFactory) SSLSocketFactory.getDefault();
        try (SSLSocket socket =
                (SSLSocket) factory.createSocket(host, port)) {

            // 使うプロトコルをTLS 1.2以上に限る（JDK 25の既定と同じ）
            socket.setEnabledProtocols(
                new String[] {"TLSv1.2", "TLSv1.3"});

            // 証明書が接続先のホスト名のものかを確かめる（既定では確かめない）
            SSLParameters params = socket.getSSLParameters();
            params.setEndpointIdentificationAlgorithm("HTTPS");
            socket.setSSLParameters(params);

            // ハンドシェイクを開始
            socket.startHandshake();
            SSLSession session = socket.getSession();
            IO.println("SSL接続が確立されました");
            IO.println("使用しているプロトコル: " + session.getProtocol());
            IO.println("暗号スイート: " + session.getCipherSuite());

            // 以降は通常のソケットと同様に使用
            PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

            // HTTPSのリクエストを送り、レスポンスのヘッダーを表示
            // HTTPの行の終わりはCRLF（\r\n）
            out.print("GET / HTTP/1.0\r\n");
            out.print("Host: " + host + "\r\n");
            out.print("\r\n");
            out.flush();
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                IO.println(line);
            }

        } catch (IOException e) {
            System.err.println("SSL接続エラー: " + e.getMessage());
        }
    }
}
