import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;
    
    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(
                 socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()))) {
            
            // 受信スレッドを開始
            Thread receiveThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        IO.println(message);
                    }
                } catch (IOException e) {
                    System.err.println("受信エラー: " + e.getMessage());
                }
            });
            receiveThread.start();
            
            // メッセージ送信ループ（入力の終わりでIO.readlnはnullを返す）
            String userInput;
            while ((userInput = IO.readln()) != null) {
                out.println(userInput);
                
                if (userInput.equalsIgnoreCase("/quit")) {
                    break;
                }
            }
            
            // 受信側を閉じて受信スレッドのreadLine()を終わらせてから、資源を閉じる
            socket.shutdownInput();
            receiveThread.join();
            
        } catch (IOException e) {
            System.err.println("接続エラー: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
