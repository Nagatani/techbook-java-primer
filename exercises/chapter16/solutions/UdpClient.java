package chapter16.solutions;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

/**
 * UDPのクライアント（基礎課題3の解答例）
 * 文字列を1つずつ送り、返事を待つ。返事が来なければタイムアウトとして扱う
 */
public class UdpClient {
    private static final int TIMEOUT_MS = 2000;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 9876;
        String[] messages = {"hello", "udp", "bye"};
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(TIMEOUT_MS); // receiveで待つ時間の上限
            InetAddress address = InetAddress.getByName(host);
            for (String message : messages) {
                byte[] data = message.getBytes(StandardCharsets.UTF_8);
                socket.send(new DatagramPacket(data, data.length, address, port));
                byte[] buffer = new byte[1024];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(reply);
                    String text = new String(reply.getData(), 0, reply.getLength(), StandardCharsets.UTF_8);
                    IO.println("送信: " + message + " -> 返事: " + text);
                } catch (SocketTimeoutException e) {
                    // UDPは届いたかどうかを知らせないので、待つ時間を決めておく
                    IO.println("送信: " + message + " -> " + TIMEOUT_MS + "ミリ秒待っても返事がありません");
                }
            }
        } catch (IOException e) {
            System.err.println("通信エラー: " + e.getMessage());
        }
    }
}
