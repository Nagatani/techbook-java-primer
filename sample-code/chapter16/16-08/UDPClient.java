import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDPClient {
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName("localhost");
            int serverPort = 9999;
            
            // データを送信
            String message = "Hello UDP Server!";
            byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(
                buffer, buffer.length, serverAddress, serverPort);
            
            socket.send(packet);
            IO.println("送信: " + message);
            
            // レスポンスを受信
            byte[] responseBuffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(
                responseBuffer, responseBuffer.length);
            
            socket.receive(responsePacket);
            
            String response = new String(responsePacket.getData(), 0,
                responsePacket.getLength(), StandardCharsets.UTF_8);
            IO.println("受信: " + response);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
