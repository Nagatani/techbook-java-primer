package chapter16.solutions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 受け取った行を大文字にして返すTCPサーバー（基礎課題1の解答例）
 *
 * 1つの接続を処理し終えたら、次の接続を待つ。「bye」を受け取ったら、その接続を閉じる。
 * 同時に複数のクライアントを受け付けるには、接続ごとの処理を別のスレッドに任せる（基礎課題4、EchoServer.java）。
 */
public class SimpleServer {
    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            IO.println("ポート" + port + "で待ち受けます（Ctrl+Cで終わる）");
            while (true) {
                // acceptは、クライアントがつないでくるまで待つ
                try (Socket socket = serverSocket.accept()) {
                    handle(socket);
                } catch (IOException e) {
                    // 1つの接続で起きた失敗では、サーバー全体を止めない
                    System.err.println("接続の処理に失敗しました: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("サーバーを起動できません: " + e.getMessage());
        }
    }

    private static void handle(Socket socket) throws IOException {
        IO.println("接続: " + socket.getInetAddress().getHostAddress());
        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        // trueを渡すと、printlnのたびに送り出す（flushする）
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
        String line;
        while ((line = in.readLine()) != null) {
            IO.println("受信: " + line);
            out.println(line.toUpperCase());
            if (line.equals("bye")) {
                break;
            }
        }
        IO.println("切断しました");
    }
}
