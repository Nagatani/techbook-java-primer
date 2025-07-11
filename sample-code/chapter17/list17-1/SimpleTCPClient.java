/**
 * リスト17-1
 * SimpleTCPClientクラス
 * 
 * 元ファイル: chapter17-network-programming.md (129行目)
 */

import java.io.*;
import java.net.*;

public class SimpleTCPClient {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 8080;
        
        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(
                 socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()))) {
            
            // サーバーにメッセージを送信
            out.println("Hello, Server!");
            
            // サーバーからの応答を受信
            String response = in.readLine();
            System.out.println("サーバーからの応答: " + response);
            
        } catch (UnknownHostException e) {
            System.err.println("ホストが見つかりません: " + serverAddress);
        } catch (IOException e) {
            System.err.println("I/Oエラーが発生しました: " + e.getMessage());
        }
    }
}