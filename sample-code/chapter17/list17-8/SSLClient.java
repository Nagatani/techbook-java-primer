/**
 * リスト17-8
 * SSLClientクラス
 * 
 * 元ファイル: chapter17-network-programming.md (588行目)
 */

import javax.net.ssl.*;
import java.io.*;

public class SSLClient {
    public static void main(String[] args) {
        String host = "www.example.com";
        int port = 443;
        
        try {
            SSLSocketFactory factory = 
                (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = 
                (SSLSocket) factory.createSocket(host, port);
            
            // サポートされているプロトコルを設定
            socket.setEnabledProtocols(
                new String[] {"TLSv1.2", "TLSv1.3"});
            
            // ハンドシェイクを開始
            socket.startHandshake();
            
            // 以降は通常のソケットと同様に使用
            PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            
            // HTTPS通信...
            
            socket.close();
            
        } catch (IOException e) {
            System.err.println("SSL接続エラー: " + e.getMessage());
        }
    }
}