# 第17章 ネットワークプログラミング

## 本章の学習目標

### 前提知識

本章を学習するための前提として、以下の知識が必要です。

**技術的前提**：
- 第15章のファイルI/Oとストリームの理解（InputStreamとOutputStreamの使い方）
- 第14章の例外処理の知識（IOException、SocketExceptionなどの処理）
- 第16章のマルチスレッドプログラミングの基礎（並行サーバーの実装で必要）
- 基本的なコンピュータネットワークの概念（IPアドレス、ポート番号の意味）

**概念的前提**：
- クライアント・サーバーモデルの基本的な理解
- 通信プロトコルの概念（データをやり取りするための約束事）
- 同期・非同期通信の違い

### 学習目標

**知識理解**：
- TCP/IPプロトコルスタックの階層構造と各層の役割を理解する
- TCPとUDPの違いと使い分けを理解する
- ソケットの概念とJavaでの実装方法を理解する
- HTTPプロトコルの基本的な仕組みを理解する

**技能習得**：
- 基本的なTCPクライアント・サーバーアプリケーションを実装できる
- UDPを使った通信プログラムを作成できる
- 簡単なHTTPクライアントを実装できる
- マルチスレッドを使った並行サーバーを構築できる
- ネットワーク通信のエラー処理を適切に実装できる

**実践応用**：
- チャットアプリケーションやファイル転送プログラムなど、実用的なネットワークアプリケーションを開発できる
- セキュアな通信の基礎を理解し、基本的なセキュリティ対策を実装できる
- ネットワークプログラムのデバッグとトラブルシューティングができる

## 17.1 はじめに

現代のソフトウェア開発において、ネットワーク通信は避けて通れない重要な技術です。Webアプリケーション、モバイルアプリ、IoTデバイスなど、ほとんどのアプリケーションは何らかの形でネットワーク通信を行っています。

本章では、Javaにおけるネットワークプログラミングの基礎から実践的な応用まで、体系的に学習します。ネットワークプログラミングは一見複雑に見えますが、基本的な概念を理解すれば、さまざまな応用が可能になります。

## 17.2 ネットワークプログラミングの基礎

### 17.2.1 TCP/IPプロトコルスタック

ネットワーク通信は、複数の層（レイヤー）から構成されるプロトコルスタックによって実現されています。各層は特定の役割を担い、上位層は下位層のサービスを利用します。

```
┌─────────────────────────────────────────┐
│ アプリケーション層                         │
│ （HTTP、FTP、SMTP、SSH、DNS など）       │
│ 役割：アプリケーション固有のプロトコル     │
└─────────────────────────────────────────┘
                    ↓↑
┌─────────────────────────────────────────┐
│ トランスポート層                          │
│ （TCP、UDP）                            │
│ 役割：プロセス間の通信、信頼性の確保       │
└─────────────────────────────────────────┘
                    ↓↑
┌─────────────────────────────────────────┐
│ インターネット層                          │
│ （IP、ICMP）                            │
│ 役割：ネットワーク間のパケット配送         │
└─────────────────────────────────────────┘
                    ↓↑
┌─────────────────────────────────────────┐
│ ネットワークインターフェース層              │
│ （Ethernet、Wi-Fi、PPPなど）            │
│ 役割：物理的な通信媒体でのデータ伝送       │
└─────────────────────────────────────────┘
```

**各層の詳細：**

1. **アプリケーション層**
   - ユーザーが直接使用するサービスを提供
   - HTTPでWebページを取得、SMTPでメール送信など
   - JavaのSocketクラスはこの層で動作

2. **トランスポート層**
   - エンドツーエンドの通信を保証
   - TCP：信頼性のある通信（Webブラウジング、ファイル転送）
   - UDP：高速だが信頼性は低い（動画ストリーミング、オンラインゲーム）

3. **インターネット層**
   - パケットを宛先まで届ける経路制御
   - IPアドレスによる識別
   - ルーティング（最適な経路の選択）

4. **ネットワークインターフェース層**
   - 物理的な通信を担当
   - MACアドレスによる隣接機器との通信
   - エラー検出と修正

### 17.2.2 IPアドレスとポート番号

- **IPアドレス**: ネットワーク上のコンピュータを一意に識別する番号
  - IPv4: 192.168.1.1（32ビット）
  - IPv6: 2001:db8::1（128ビット）

- **ポート番号**: 同一コンピュータ上の複数のアプリケーションを識別する番号（0-65535）
  - Well-knownポート（0-1023）: HTTP(80)、HTTPS(443)、FTP(21)など
  - 登録済みポート（1024-49151）
  - 動的・私的ポート（49152-65535）

### 17.2.3 TCPとUDPの違い

| 特性 | TCP | UDP |
|------|-----|-----|
| 接続性 | コネクション型 | コネクションレス型 |
| 信頼性 | 高い（順序保証、再送制御） | 低い（ベストエフォート） |
| 速度 | 比較的遅い | 高速 |
| 用途 | Web、メール、ファイル転送 | 動画ストリーミング、ゲーム、DNS |

## 17.3 ソケットプログラミングの基礎

### 17.3.1 ソケットとは

ソケットは、ネットワーク通信のエンドポイントを表す抽象的な概念です。Javaでは`java.net`パッケージでソケット関連のクラスが提供されています。

### 17.3.2 基本的なTCPクライアント

**リスト17-1**
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

### 17.3.3 基本的なTCPサーバー

**リスト17-2**
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

## 17.4 マルチスレッドサーバーの実装

単一スレッドのサーバーでは、一度に一つのクライアントしか処理できません。実用的なサーバーでは、複数のクライアントを同時に処理する必要があります。

### 17.4.1 スレッドベースのサーバー

**リスト17-3**
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
                    out.println(java.time.LocalDateTime.now().toString());
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

## 17.5 HTTPクライアントの実装

### 17.5.1 HTTPプロトコルの基礎

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

### 17.5.2 簡単なHTTPクライアント

**リスト17-4**
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

### 17.5.3 HttpURLConnectionの使用

Javaは、より高レベルなHTTP通信のためのAPIも提供しています。

**リスト17-5**
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

## 17.6 実践的な例：チャットアプリケーション

### 17.6.1 チャットサーバー

**リスト17-6**
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

### 17.6.2 チャットクライアント

**リスト17-7**
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

## 17.7 セキュリティの考慮事項

ネットワークプログラミングでは、セキュリティを常に意識する必要があります。

### 17.7.1 基本的なセキュリティ対策

1. **入力検証**: すべての外部入力を検証する
2. **暗号化**: 機密データはSSL/TLSで暗号化する
3. **認証**: 適切な認証メカニズムを実装する
4. **タイムアウト**: DoS攻撃を防ぐためタイムアウトを設定する
5. **リソース制限**: 接続数やメモリ使用量を制限する

### 17.7.2 SSL/TLSの使用例

**リスト17-8**
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

## 17.8 パフォーマンスの最適化

### 17.8.1 ノンブロッキングI/O（NIO）

Java NIOを使用すると、より効率的なネットワークプログラミングが可能です。

**リスト17-9**
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

## 17.9 まとめ

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

## 章末演習

本章で学んだネットワークプログラミングを実践的な課題で確認しましょう。

### 演習課題へのアクセス

本書の演習課題は、以下のGitHubリポジトリで提供されています：

**リポジトリ**: `https://github.com/[your-repo]/java-primer-exercises`

### 第17章の課題構成

```
exercises/chapter17/
├── basic/              # 基礎課題（必須）
│   ├── README.md       # 詳細な課題説明
│   └── SimpleServer.java
├── advanced/           # 発展課題（推奨）
├── challenge/          # チャレンジ課題（任意）
└── solutions/          # 解答例（実装後に参照）
```

### 学習の目標

本章の演習を通じて以下のスキルを習得します：
- ソケットプログラミングとTCP/IP通信の実装
- クライアント・サーバーアーキテクチャの構築
- HTTPプロトコルの理解と活用

### 課題の概要

1. **基礎課題**: シンプルなTCPサーバー・クライアントの実装
2. **発展課題**: チャットアプリケーションの開発
3. **チャレンジ課題**: HTTPサーバーの実装とRESTful API設計

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

**次のステップ**: 基礎課題が完了したら、第18章「GUIプログラミング基礎」に進みましょう。

