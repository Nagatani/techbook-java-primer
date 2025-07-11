/**
 * リスト17-7
 * ChatClientクラス
 * 
 * 元ファイル: chapter17-network-programming.md (526行目)
 */

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;
    
    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(
                 socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {
            
            // 受信スレッドを開始
            Thread receiveThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.err.println("受信エラー: " + e.getMessage());
                }
            });
            receiveThread.start();
            
            // メッセージ送信ループ
            String userInput;
            while ((userInput = scanner.nextLine()) != null) {
                out.println(userInput);
                
                if (userInput.equalsIgnoreCase("/quit")) {
                    break;
                }
            }
            
        } catch (IOException e) {
            System.err.println("接続エラー: " + e.getMessage());
        }
    }
}