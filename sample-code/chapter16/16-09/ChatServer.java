import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private static final int PORT = 5000;
    // 複数のスレッドから追加・削除・反復しても安全な集合
    private static Set<ClientHandler> clients =
        ConcurrentHashMap.newKeySet();
    
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            IO.println("チャットサーバーが起動しました");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("サーバーエラー: " + e.getMessage());
        }
    }
    
    // すべてのクライアントにメッセージをブロードキャスト
    public static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }
    
    // クライアントを削除
    public static void removeClient(ClientHandler client) {
        clients.remove(client);
    }
    
    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;
        
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }
        
        @Override
        public void run() {
            try {
                in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(
                    socket.getOutputStream(), true);
                // 送信の準備ができてから、ブロードキャストの対象に加える
                clients.add(this);
                
                // ユーザー名を取得
                out.println("ユーザー名を入力してください:");
                username = in.readLine();
                
                // 入室メッセージをブロードキャスト
                broadcast(username + " が入室しました", this);
                
                // メッセージを受信してブロードキャスト
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("/quit")) {
                        break;
                    }
                    broadcast(username + ": " + message, this);
                }
            } catch (IOException e) {
                System.err.println(
                    "クライアントエラー: " + e.getMessage());
            } finally {
                // 退室処理
                if (username != null) {
                    broadcast(username + " が退室しました", this);
                }
                removeClient(this);
                try {
                    socket.close();
                } catch (IOException e) {
                    // ログに記録
                }
            }
        }
        
        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
