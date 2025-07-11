package chapter17.solutions;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.*;

/**
 * TCPエコークライアントの実装
 * サーバーに接続し、ユーザーが入力したメッセージを送信してエコーレスポンスを受信する
 * 
 * @author Hidehiro Nagatani
 * @version 1.0
 */
public class EchoClient {
    private static final Logger logger = Logger.getLogger(EchoClient.class.getName());
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8080;
    private static final int TIMEOUT_MS = 30000; // 30秒のタイムアウト
    
    private final String host;
    private final int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    
    /**
     * EchoClientのコンストラクタ
     * 
     * @param host 接続先サーバーのホスト名またはIPアドレス
     * @param port 接続先サーバーのポート番号
     */
    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
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
        
        logger.info("サーバーに接続しました");
    }
    
    /**
     * メッセージを送信し、レスポンスを受信する
     * 
     * @param message 送信するメッセージ
     * @return サーバーからのレスポンス
     * @throws IOException 通信エラーが発生した場合
     */
    public String sendMessage(String message) throws IOException {
        if (socket == null || socket.isClosed()) {
            throw new IOException("サーバーに接続されていません");
        }
        
        out.println(message);
        logger.info("送信: " + message);
        
        String response = in.readLine();
        if (response == null) {
            throw new IOException("サーバーからの応答がありません");
        }
        
        logger.info("受信: " + response);
        return response;
    }
    
    /**
     * サーバーとの接続を切断する
     */
    public void disconnect() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            logger.info("サーバーとの接続を切断しました");
        } catch (IOException e) {
            logger.log(Level.WARNING, "切断処理中のエラー", e);
        }
    }
    
    /**
     * 対話的にメッセージを送受信する
     */
    public void interactiveSession() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("エコークライアントを起動しました。");
            System.out.println("メッセージを入力してください（'quit'で終了）:");
            
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine();
                
                if (input.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    String response = sendMessage(input);
                    System.out.println("< " + response);
                    
                    if ("quit".equalsIgnoreCase(input)) {
                        break;
                    }
                } catch (IOException e) {
                    System.err.println("通信エラー: " + e.getMessage());
                    break;
                }
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
        
        EchoClient client = new EchoClient(host, port);
        
        try {
            // サーバーに接続
            client.connect();
            
            // 対話的セッションを開始
            client.interactiveSession();
            
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