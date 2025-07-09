# 第20章 ネットワークプログラミング

## 20.1 はじめに

現代のソフトウェア開発において、ネットワーク通信は避けて通れない重要な技術です。Webアプリケーション、モバイルアプリ、IoTデバイスなど、ほとんどのアプリケーションは何らかの形でネットワーク通信を行っています。

本章では、Javaにおけるネットワークプログラミングの基礎から実践的な応用まで、体系的に学習します。

### 学習目標

- TCP/IPプロトコルの基本概念を理解する
- ソケットプログラミングの基礎を習得する
- クライアント・サーバーアプリケーションを実装できる
- HTTPプロトコルを理解し、簡単なWebクライアントを作成できる
- マルチスレッドを活用した並行サーバーを実装できる

## 20.2 ネットワークプログラミングの基礎

### 20.2.1 TCP/IPプロトコルスタック

ネットワーク通信は、複数の層（レイヤー）から構成されるプロトコルスタックによって実現されています。

```
アプリケーション層（HTTP、FTP、SMTPなど）
    ↓↑
トランスポート層（TCP、UDP）
    ↓↑
インターネット層（IP）
    ↓↑
ネットワークインターフェース層（Ethernet、Wi-Fiなど）
```

### 20.2.2 IPアドレスとポート番号

- **IPアドレス**: ネットワーク上のコンピュータを一意に識別する番号
  - IPv4: 192.168.1.1（32ビット）
  - IPv6: 2001:db8::1（128ビット）

- **ポート番号**: 同一コンピュータ上の複数のアプリケーションを識別する番号（0-65535）
  - Well-knownポート（0-1023）: HTTP(80)、HTTPS(443)、FTP(21)など
  - 登録済みポート（1024-49151）
  - 動的・私的ポート（49152-65535）

### 20.2.3 TCPとUDPの違い

| 特性 | TCP | UDP |
|------|-----|-----|
| 接続性 | コネクション型 | コネクションレス型 |
| 信頼性 | 高い（順序保証、再送制御） | 低い（ベストエフォート） |
| 速度 | 比較的遅い | 高速 |
| 用途 | Web、メール、ファイル転送 | 動画ストリーミング、ゲーム、DNS |

## 20.3 ソケットプログラミングの基礎

### 20.3.1 ソケットとは

ソケットは、ネットワーク通信のエンドポイントを表す抽象的な概念です。Javaでは`java.net`パッケージでソケット関連のクラスが提供されています。

### 20.3.2 基本的なTCPクライアント

```java
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
```

### 20.3.3 基本的なTCPサーバー

```java
import java.io.*;
import java.net.*;

public class SimpleTCPServer {
    public static void main(String[] args) {
        int port = 8080;
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("サーバーがポート " + port + " で起動しました");
            
            while (true) {
                // クライアントからの接続を待機
                Socket clientSocket = serverSocket.accept();
                System.out.println("クライアントが接続しました: " + 
                    clientSocket.getInetAddress());
                
                // クライアントとの通信を処理
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("サーバーエラー: " + e.getMessage());
        }
    }
    
    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(
                 new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(
                 clientSocket.getOutputStream(), true)) {
            
            // クライアントからのメッセージを受信
            String message = in.readLine();
            System.out.println("受信: " + message);
            
            // 応答を送信
            out.println("Echo: " + message);
            
        } catch (IOException e) {
            System.err.println("クライアント処理エラー: " + e.getMessage());
        }
    }
}
```

## 20.4 マルチスレッドサーバーの実装

単一スレッドのサーバーでは、一度に一つのクライアントしか処理できません。実用的なサーバーでは、複数のクライアントを同時に処理する必要があります。

### 20.4.1 スレッドベースのサーバー

```java
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class MultiThreadedServer {
    private static final int PORT = 8080;
    private static final int THREAD_POOL_SIZE = 10;
    
    public static void main(String[] args) {
        ExecutorService executor = 
            Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("マルチスレッドサーバーが起動しました");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // 新しいクライアントの処理を別スレッドで実行
                executor.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("サーバーエラー: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket clientSocket;
    
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }
    
    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(
                 new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(
                 clientSocket.getOutputStream(), true)) {
            
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(
                    Thread.currentThread().getName() + 
                    " - 受信: " + inputLine);
                
                // 簡単なプロトコル処理
                if (inputLine.equalsIgnoreCase("quit")) {
                    out.println("Goodbye!");
                    break;
                } else if (inputLine.startsWith("ECHO ")) {
                    out.println(inputLine.substring(5));
                } else if (inputLine.equals("TIME")) {
                    out.println(new java.util.Date().toString());
                } else {
                    out.println("Unknown command: " + inputLine);
                }
            }
        } catch (IOException e) {
            System.err.println(
                "クライアント処理エラー: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                // ログに記録
            }
        }
    }
}
```

## 20.5 HTTPクライアントの実装

### 20.5.1 HTTPプロトコルの基礎

HTTPは、Webで最も広く使用されているアプリケーション層プロトコルです。

**HTTPリクエストの構造**：
```
GET /index.html HTTP/1.1
Host: www.example.com
User-Agent: Java/17
Accept: text/html
[空行]
[リクエストボディ（POSTの場合）]
```

**HTTPレスポンスの構造**：
```
HTTP/1.1 200 OK
Content-Type: text/html
Content-Length: 1234
[空行]
[レスポンスボディ]
```

### 20.5.2 簡単なHTTPクライアント

```java
import java.io.*;
import java.net.*;

public class SimpleHTTPClient {
    public static void main(String[] args) {
        String host = "www.example.com";
        int port = 80;
        String path = "/";
        
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(
                 socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()))) {
            
            // HTTPリクエストを送信
            out.println("GET " + path + " HTTP/1.1");
            out.println("Host: " + host);
            out.println("Connection: close");
            out.println(); // 空行でヘッダー終了
            
            // レスポンスを読み取る
            String line;
            boolean isHeader = true;
            StringBuilder headers = new StringBuilder();
            StringBuilder body = new StringBuilder();
            
            while ((line = in.readLine()) != null) {
                if (isHeader) {
                    if (line.isEmpty()) {
                        isHeader = false;
                    } else {
                        headers.append(line).append("\n");
                    }
                } else {
                    body.append(line).append("\n");
                }
            }
            
            System.out.println("=== Headers ===");
            System.out.println(headers);
            System.out.println("=== Body ===");
            System.out.println(body);
            
        } catch (IOException e) {
            System.err.println("エラー: " + e.getMessage());
        }
    }
}
```

### 20.5.3 HttpURLConnectionの使用

Javaは、より高レベルなHTTP通信のためのAPIも提供しています。

```java
import java.io.*;
import java.net.*;

public class HttpURLConnectionExample {
    public static void main(String[] args) {
        try {
            URL url = new URL("https://api.github.com/users/github");
            HttpURLConnection conn = 
                (HttpURLConnection) url.openConnection();
            
            // リクエストの設定
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            // レスポンスコードの確認
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // レスポンスの読み取り
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                            conn.getInputStream()))) {
                    String line;
                    StringBuilder response = new StringBuilder();
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    System.out.println(
                        "Response: " + response.toString());
                }
            }
            
            conn.disconnect();
            
        } catch (IOException e) {
            System.err.println("エラー: " + e.getMessage());
        }
    }
}
```

## 20.6 実践的な例：チャットアプリケーション

### 20.6.1 チャットサーバー

```java
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private static final int PORT = 5000;
    private static Set<ClientHandler> clients = 
        Collections.synchronizedSet(new HashSet<>());
    
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("チャットサーバーが起動しました");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket);
                clients.add(handler);
                executor.submit(handler);
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
                
                // ユーザー名を取得
                out.println("ユーザー名を入力してください:");
                username = in.readLine();
                
                // 入室メッセージをブロードキャスト
                broadcast(username + " が入室しました", this);
                
                // メッセージを受信してブロードキャスト
                String message;
                while ((message = in.readLine()) != null) {
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
```

### 20.6.2 チャットクライアント

```java
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
```

## 20.7 セキュリティの考慮事項

ネットワークプログラミングでは、セキュリティを常に意識する必要があります。

### 20.7.1 基本的なセキュリティ対策

1. **入力検証**: すべての外部入力を検証する
2. **暗号化**: 機密データはSSL/TLSで暗号化する
3. **認証**: 適切な認証メカニズムを実装する
4. **タイムアウト**: DoS攻撃を防ぐためタイムアウトを設定する
5. **リソース制限**: 接続数やメモリ使用量を制限する

### 20.7.2 SSL/TLSの使用例

```java
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
```

## 20.8 パフォーマンスの最適化

### 20.8.1 ノンブロッキングI/O（NIO）

Java NIOを使用すると、より効率的なネットワークプログラミングが可能です。

```java
import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import java.util.*;

public class NIOServer {
    private static final int PORT = 8080;
    
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverChannel = 
            ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(PORT));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        
        System.out.println("NIOサーバーが起動しました");
        
        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();
            
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                
                if (key.isAcceptable()) {
                    register(selector, serverChannel);
                }
                
                if (key.isReadable()) {
                    answerWithEcho(key);
                }
                
                iter.remove();
            }
        }
    }
    
    private static void register(Selector selector, 
                                ServerSocketChannel serverChannel) 
            throws IOException {
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }
    
    private static void answerWithEcho(SelectionKey key) 
            throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(256);
        client.read(buffer);
        
        if (buffer.position() > 0) {
            buffer.flip();
            client.write(buffer);
            buffer.clear();
        }
    }
}
```

## 20.9 まとめ

本章では、Javaにおけるネットワークプログラミングの基礎から実践的な応用まで学習しました。

### 重要なポイント

1. **ソケットプログラミング**: TCPとUDPの違いを理解し、適切に使い分ける
2. **マルチスレッド**: 複数のクライアントを同時に処理するサーバーの実装
3. **プロトコル**: HTTPなどのアプリケーション層プロトコルの理解
4. **セキュリティ**: 暗号化、認証、入力検証の重要性
5. **パフォーマンス**: NIOを使用した効率的なI/O処理

### 次のステップ

- RESTful APIの設計と実装
- WebSocketを使用したリアルタイム通信
- gRPCなどの最新のRPCフレームワーク
- マイクロサービスアーキテクチャ

## 演習問題

### 演習20-1: エコーサーバーの拡張

基本的なエコーサーバーを拡張して、以下の機能を追加してください：
- クライアントごとの接続時間を記録
- 同時接続数の制限（最大10接続）
- 特定のコマンド（DATE、UPPER、LOWER）への対応

### 演習20-2: HTTPプロキシの実装

簡単なHTTPプロキシサーバーを実装してください：
- クライアントからのHTTPリクエストを受信
- 実際のWebサーバーに転送
- レスポンスをクライアントに返す
- アクセスログの記録

### 演習20-3: ファイル転送プロトコル

独自のファイル転送プロトコルを設計・実装してください：
- ファイルのアップロード・ダウンロード
- 進捗表示
- 転送の中断・再開
- チェックサムによる整合性確認