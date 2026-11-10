import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDPServer {
    private static final int PORT = 9999;
    
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            IO.println("UDPサーバーがポート " + PORT + " で起動しました");
            
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            
            while (true) {
                // データを受信
                socket.receive(packet);
                
                String received = new String(packet.getData(), 0, 
                    packet.getLength(), StandardCharsets.UTF_8);
                IO.println("受信: " + received + " from " + 
                    packet.getAddress());
                
                // エコーレスポンスを送信
                String response = "Echo: " + received;
                byte[] responseData = response.getBytes(StandardCharsets.UTF_8);
                DatagramPacket responsePacket = new DatagramPacket(
                    responseData, responseData.length,
                    packet.getAddress(), packet.getPort());
                
                socket.send(responsePacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
