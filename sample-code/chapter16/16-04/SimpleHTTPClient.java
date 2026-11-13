import java.io.*;
import java.net.*;

public class SimpleHTTPClient {
    public static void main(String[] args) {
        String host = "www.example.com";
        int port = 80;
        String path = "/";
        
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(
                 socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()))) {
            
            // HTTPリクエストを送信（HTTP/1.0で送ると、ボディが分割されずに届く）
            // HTTPの行の終わりはCRLF（\r\n）なので、printlnではなくprintで書く
            out.print("GET " + path + " HTTP/1.0\r\n");
            out.print("Host: " + host + "\r\n");
            out.print("Connection: close\r\n");
            out.print("\r\n"); // 空行でヘッダー終了
            out.flush();
            
            // HTTPレスポンスの取得
            // HTTPレスポンスは、ステータスライン、ヘッダー、空行、ボディの順で構成されています。
            // ヘッダーとボディは空行で区切られているため、これを検出して処理を分けます。
            String line;
            boolean isHeader = true;
            StringBuilder headers = new StringBuilder();
            StringBuilder body = new StringBuilder();
            
            while ((line = in.readLine()) != null) {
                if (isHeader) {
                    if (line.isEmpty()) {
                        isHeader = false;
                    } else {
                        headers.append(line).append("\n");
                    }
                } else {
                    body.append(line).append("\n");
                }
            }
            
            IO.println("=== Headers ===");
            IO.println(headers);
            IO.println("=== Body ===");
            IO.println(body);
            
        } catch (IOException e) {
            System.err.println("エラー: " + e.getMessage());
        }
    }
}
