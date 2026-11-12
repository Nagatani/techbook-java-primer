package chapter16.solutions;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

/**
 * UDPのサーバー（基礎課題3の解答例）
 * 受け取った文字列を大文字にして送り返す。"bye"を受け取ると終わる
 */
public class UdpServer {
    private static final int DEFAULT_PORT = 9876;

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        try (DatagramSocket socket = new DatagramSocket(port)) {
            IO.println("UDPサーバー: ポート" + port + "で待ち受けます");
            serve(socket);
        } catch (SocketException e) {
            System.err.println("ポートを開けません: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("通信エラー: " + e.getMessage());
        }
        IO.println("UDPサーバー: 終了します");
    }

    /** "bye"を受け取るまで、パケットを受け取って返す */
    static void serve(DatagramSocket socket) throws IOException {
        byte[] buffer = new byte[1024];
        while (true) {
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            socket.receive(request); // 届くまで待つ
            // 受け取った長さだけを文字列にする（配列の残りは前のデータ）
            String text = new String(request.getData(), 0, request.getLength(), StandardCharsets.UTF_8);
            IO.println("受信: " + text);
            byte[] reply = text.toUpperCase().getBytes(StandardCharsets.UTF_8);
            // 送ってきた相手のアドレスとポートへ返す
            socket.send(new DatagramPacket(reply, reply.length, request.getAddress(), request.getPort()));
            if (text.equals("bye")) {
                return;
            }
        }
    }
}
