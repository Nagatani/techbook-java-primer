package chapter17.solutions;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.logging.*;

/**
 * チャットクライアントの実装
 * ChatServerに接続してチャットを行うクライアントアプリケーション
 * 
 * @author Hidehiro Nagatani
 * @version 1.0
 */
public class ChatClient {
    private static final Logger logger = Logger.getLogger(ChatClient.class.getName());
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8888;
    private static final int TIMEOUT_MS = 30000;
    
    private final String host;
    private final int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean connected;
    private ExecutorService executor;
    
    /**
     * ChatClientのコンストラクタ
     * 
     * @param host 接続先サーバーのホスト名
     * @param port 接続先サーバーのポート番号
     */
    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.connected = false;
        this.executor = Executors.newSingleThreadExecutor();
    }
    
    /**
     * サーバーに接続する
     * 
     * @throws IOException 接続エラーが発生した場合
     */
    public void connect() throws IOException {
        logger.info("サーバーに接続中: " + host + ":" + port);
        
        socket = new Socket();
        socket.setSoTimeout(TIMEOUT_MS);
        socket.connect(new InetSocketAddress(host, port), TIMEOUT_MS);
        
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        connected = true;
        
        logger.info("サーバーに接続しました");
        
        // 受信スレッドを開始
        executor.submit(new MessageReceiver());
    }
    
    /**
     * メッセージを送信する
     * 
     * @param message 送信するメッセージ
     */
    public void sendMessage(String message) {
        if (connected && out != null) {
            out.println(message);
        }
    }
    
    /**
     * サーバーとの接続を切断する
     */
    public void disconnect() {
        connected = false;
        
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "切断処理エラー", e);
        }
        
        logger.info("サーバーとの接続を切断しました");
    }
    
    /**
     * 対話的なチャットセッションを開始する
     */
    public void startChatSession() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("\n=== チャットクライアント ===");
            System.out.println("メッセージを入力してください（/help でヘルプ、/quit で終了）:");
            System.out.println();
            
            while (connected) {
                // プロンプトを表示せず、ユーザー入力を待つ
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine();
                    
                    if (input.trim().isEmpty()) {
                        continue;
                    }
                    
                    sendMessage(input);
                    
                    if ("/quit".equalsIgnoreCase(input.trim())) {
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * メッセージ受信を処理する内部クラス
     */
    private class MessageReceiver implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while (connected && (message = in.readLine()) != null) {
                    // 受信メッセージを表示
                    System.out.println(message);
                    
                    // サーバーからの切断メッセージをチェック
                    if (message.contains("Disconnecting...")) {
                        connected = false;
                        break;
                    }
                }
            } catch (SocketTimeoutException e) {
                System.err.println("接続タイムアウト");
            } catch (IOException e) {
                if (connected) {
                    System.err.println("受信エラー: " + e.getMessage());
                    logger.log(Level.WARNING, "メッセージ受信エラー", e);
                }
            } finally {
                connected = false;
            }
        }
    }
    
    /**
     * メインメソッド
     * 
     * @param args コマンドライン引数（オプション: ホスト名 ポート番号）
     */
    public static void main(String[] args) {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        
        // コマンドライン引数の処理
        if (args.length > 0) {
            host = args[0];
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("無効なポート番号: " + args[1]);
                System.err.println("デフォルトポート " + DEFAULT_PORT + " を使用します");
                port = DEFAULT_PORT;
            }
        }
        
        ChatClient client = new ChatClient(host, port);
        
        try {
            // サーバーに接続
            client.connect();
            
            // チャットセッションを開始
            client.startChatSession();
            
        } catch (ConnectException e) {
            System.err.println("サーバーに接続できません: " + host + ":" + port);
            System.err.println("サーバーが起動していることを確認してください。");
        } catch (SocketTimeoutException e) {
            System.err.println("接続タイムアウト: サーバーが応答しません");
        } catch (IOException e) {
            System.err.println("エラーが発生しました: " + e.getMessage());
            logger.log(Level.SEVERE, "通信エラー", e);
        } finally {
            // 接続を切断
            client.disconnect();
        }
    }
}