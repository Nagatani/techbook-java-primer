package chapter17.solutions;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.logging.*;

/**
 * TCPエコーサーバーの実装
 * 複数のクライアントからの接続を受け付け、受信したメッセージをエコーバックする
 * 
 * @author Hidehiro Nagatani
 * @version 1.0
 */
public class EchoServer {
    private static final Logger logger = Logger.getLogger(EchoServer.class.getName());
    private static final int DEFAULT_PORT = 8080;
    private static final int THREAD_POOL_SIZE = 10;
    
    private final int port;
    private final ExecutorService executor;
    private ServerSocket serverSocket;
    private volatile boolean running;
    
    /**
     * EchoServerのコンストラクタ
     * 
     * @param port サーバーがリッスンするポート番号
     */
    public EchoServer(int port) {
        this.port = port;
        this.executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.running = false;
    }
    
    /**
     * サーバーを起動し、クライアントからの接続を待機する
     * 
     * @throws IOException ソケット操作でエラーが発生した場合
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        logger.info("サーバー起動: ポート " + port + " でリッスン中...");
        
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                logger.info("クライアント接続: " + clientSocket.getRemoteSocketAddress());
                
                // クライアント処理を別スレッドで実行
                executor.submit(new ClientHandler(clientSocket));
            } catch (IOException e) {
                if (running) {
                    logger.log(Level.SEVERE, "クライアント接続エラー", e);
                }
            }
        }
    }
    
    /**
     * サーバーを停止する
     */
    public void stop() {
        running = false;
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "サーバーソケットのクローズエラー", e);
        }
        
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        logger.info("サーバー停止");
    }
    
    /**
     * クライアントとの通信を処理する内部クラス
     */
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        
        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }
        
        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true)) {
                
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    logger.info("受信: " + inputLine);
                    
                    // "quit"を受信したら接続を終了
                    if ("quit".equalsIgnoreCase(inputLine)) {
                        out.println("Goodbye!");
                        break;
                    }
                    
                    // エコーバック
                    String response = "Echo: " + inputLine;
                    out.println(response);
                    logger.info("送信: " + response);
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "クライアント通信エラー", e);
            } finally {
                try {
                    clientSocket.close();
                    logger.info("クライアント切断: " + clientSocket.getRemoteSocketAddress());
                } catch (IOException e) {
                    logger.log(Level.WARNING, "ソケットクローズエラー", e);
                }
            }
        }
    }
    
    /**
     * メインメソッド
     * 
     * @param args コマンドライン引数（オプション: ポート番号）
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("無効なポート番号: " + args[0]);
                System.err.println("デフォルトポート " + DEFAULT_PORT + " を使用します");
            }
        }
        
        EchoServer server = new EchoServer(port);
        
        // シャットダウンフックを追加
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("シャットダウン処理を開始します...");
            server.stop();
        }));
        
        try {
            server.start();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "サーバー起動エラー", e);
            System.exit(1);
        }
    }
}