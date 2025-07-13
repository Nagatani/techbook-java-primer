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

TCPクライアントは、Socketクラスを使用してサーバーに接続します。接続が確立されると、入力ストリームと出力ストリームを通じてデータの送受信を行います。TCPは信頼性の高い接続指向のプロトコルであり、データの順序と到達が保証されます。

<span class="listing-number">**サンプルコード17-1**</span>

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

TCPサーバーは、ServerSocketを使用してクライアントからの接続を待ち受けます。ServerSocketは指定されたポートで待機し、クライアントが接続すると新しいSocketインスタンスを生成します。このSocketを使用して、個々のクライアントと通信を行います。

<span class="listing-number">**サンプルコード17-2**</span>

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

**マルチクライアント対応の重要性**：
- 同時接続性：複数のクライアントが同時にサービスを利用できる
- スケーラビリティ：クライアント数の増加に対応できる
- 応答性：一つのクライアントの処理が他のクライアントに影響しない
- リソース効率：スレッドプールを使用することで効率的なリソース管理が可能

### 17.4.1 スレッドベースのサーバー

<span class="listing-number">**サンプルコード17-3**</span>

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

ソケットを使用して直接HTTPプロトコルを話すクライアントの実装例です。この方法は教育的価値が高く、HTTPプロトコルの動作を理解するのに役立ちます。実際の開発では、後述するHttpURLConnectionやHTTPクライアントライブラリを使用することが一般的ですが、基礎となる仕組みを理解することは重要です。

<span class="listing-number">**サンプルコード17-4**</span>

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
            
            // HTTPレスポンスの取得
            // HTTPレスポンスは、ステータスライン、ヘッダー、空行、ボディの順で構成されています。
            // ヘッダーとボディは空行で区切られているため、これを検出して処理を分けます。
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

<span class="listing-number">**サンプルコード17-5**</span>

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

### 17.5.4 非同期HTTPクライアント（Java 11+）

Java 11で導入された新しいHTTPクライアントAPIは、非同期処理をサポートし、HTTP/2にも対応しています。CompletableFutureを使用することで、ノンブロッキングなHTTP通信が可能になります。

<span class="listing-number">**サンプルコード17-6**</span>

```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class AsyncHttpClientExample {
    
    public static void main(String[] args) throws Exception {
        // HTTPクライアントの作成
        HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        
        // HTTPリクエストの作成
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://jsonplaceholder.typicode.com/posts/1"))
            .timeout(Duration.ofMinutes(1))
            .header("Accept", "application/json")
            .GET()
            .build();
        
        // 非同期でリクエストを送信
        CompletableFuture<HttpResponse<String>> future = 
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        
        // 非同期処理の連鎖
        future
            .thenApply(response -> {
                System.out.println("Status: " + response.statusCode());
                return response.body();
            })
            .thenAccept(body -> {
                System.out.println("Response Body: " + body);
            })
            .exceptionally(e -> {
                System.err.println("Error: " + e.getMessage());
                return null;
            });
        
        // 他の処理を並行して実行可能
        System.out.println("他の処理を継続中...");
        
        // 結果を待つ（必要な場合）
        future.join();
    }
}
```

**非同期HTTPの利点**：
- ノンブロッキング処理により、レスポンスを待つ間も他の処理を継続できる
- 複数のHTTPリクエストを並行して送信できる
- CompletableFutureによる柔軟なエラーハンドリングと処理の連鎖
- HTTP/2のサポートによる効率的な通信

## 17.5.5 UDP通信の実装

UDPは、コネクションレスで信頼性の低いプロトコルですが、低遅延で高速な通信が可能です。リアルタイムゲーム、ストリーミング、DNS などで使用されます。

<span class="listing-number">**サンプルコード17-7**</span>

```java
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDPServer {
    private static final int PORT = 9999;
    
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("UDPサーバーがポート " + PORT + " で起動しました");
            
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            
            while (true) {
                // データを受信
                socket.receive(packet);
                
                String received = new String(packet.getData(), 0, 
                    packet.getLength(), StandardCharsets.UTF_8);
                System.out.println("受信: " + received + " from " + 
                    packet.getAddress());
                
                // エコーレスポンスを送信
                String response = "Echo: " + received;
                byte[] responseData = response.getBytes(StandardCharsets.UTF_8);
                DatagramPacket responsePacket = new DatagramPacket(
                    responseData, responseData.length,
                    packet.getAddress(), packet.getPort());
                
                socket.send(responsePacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### UDP受信側

<span class="listing-number">**サンプルコード17-8**</span>

```java
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDPClient {
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName("localhost");
            int serverPort = 9999;
            
            // データを送信
            String message = "Hello UDP Server!";
            byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(
                buffer, buffer.length, serverAddress, serverPort);
            
            socket.send(packet);
            System.out.println("送信: " + message);
            
            // レスポンスを受信
            byte[] responseBuffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(
                responseBuffer, responseBuffer.length);
            
            socket.receive(responsePacket);
            
            String response = new String(responsePacket.getData(), 0,
                responsePacket.getLength(), StandardCharsets.UTF_8);
            System.out.println("受信: " + response);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

**UDPの特徴と使用場面**：
- パケット損失の可能性があるが、高速
- 順序保証がない
- ブロードキャストやマルチキャストが可能
- リアルタイム性が重要なアプリケーションに適している

## 17.6 実践的な例：チャットアプリケーション

### 17.6.1 チャットサーバー

<span class="listing-number">**サンプルコード17-6**</span>

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

<span class="listing-number">**サンプルコード17-7**</span>

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

## 17.6.3 JSON APIクライアントの実装

現代のWebアプリケーションでは、JSON形式でのデータ交換が標準的です。JavaでJSON APIを扱う方法を学びます。

<span class="listing-number">**サンプルコード17-8**</span>

```java
import java.net.URI;
import java.net.http.*;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonApiClient {
    private final HttpClient client;
    private final ObjectMapper mapper;
    
    public JsonApiClient() {
        this.client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
        this.mapper = new ObjectMapper();
    }
    
    // GETリクエスト
    public <T> T get(String url, Class<T> responseType) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Accept", "application/json")
            .GET()
            .build();
        
        HttpResponse<String> response = client.send(request, 
            HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), responseType);
        } else {
            throw new RuntimeException("HTTP error: " + response.statusCode());
        }
    }
    
    // POSTリクエスト
    public <T> T post(String url, Object data, Class<T> responseType) 
            throws Exception {
        String json = mapper.writeValueAsString(data);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();
        
        HttpResponse<String> response = client.send(request,
            HttpResponse.BodyHandlers.ofString());
        
        return mapper.readValue(response.body(), responseType);
    }
    
    public static void main(String[] args) throws Exception {
        JsonApiClient apiClient = new JsonApiClient();
        
        // GET例：ユーザー情報の取得
        Map<String, Object> user = apiClient.get(
            "https://jsonplaceholder.typicode.com/users/1",
            Map.class);
        System.out.println("User: " + user);
        
        // POST例：新しい投稿の作成
        Map<String, Object> newPost = new HashMap<>();
        newPost.put("title", "New Post");
        newPost.put("body", "This is a new post");
        newPost.put("userId", 1);
        
        Map<String, Object> result = apiClient.post(
            "https://jsonplaceholder.typicode.com/posts",
            newPost, Map.class);
        System.out.println("Created: " + result);
    }
}
```

**JSON処理のポイント**：
- Jackson, Gson などのライブラリを使用してJSONのシリアライズ/デシリアライズ
- 適切なContent-TypeとAcceptヘッダーの設定
- エラーハンドリングとHTTPステータスコードの確認
- 型安全なデータマッピング

## 17.6.4 REST API実装の基礎

REST（Representational State Transfer）は、Web APIの設計原則です。JavaでRESTful APIを実装する基本的な概念を理解しましょう。

**RESTの原則**：
- リソース指向：すべてをリソースとして扱う
- 統一インターフェース：HTTPメソッド（GET, POST, PUT, DELETE）の適切な使用
- ステートレス：各リクエストが独立している
- キャッシュ可能性：レスポンスのキャッシュ制御

実際のREST API実装には、Spring Boot、JAX-RS（Jersey）などのフレームワークを使用することが一般的です。

## 17.6.5 WebSocketクライアントの基礎

WebSocketは、双方向のリアルタイム通信を可能にするプロトコルです。HTTPのアップグレードメカニズムを使用して、永続的な接続を確立します。

**WebSocketの特徴**：
- 双方向通信：サーバーからクライアントへのプッシュが可能
- 低レイテンシ：HTTPのオーバーヘッドなし
- リアルタイム性：チャット、ゲーム、株価配信などに最適

Java標準APIやJettyなどのライブラリを使用してWebSocketクライアントを実装できます。

## 17.7 セキュリティの考慮事項

ネットワークプログラミングでは、セキュリティを常に意識する必要があります。

### 17.7.1 基本的なセキュリティ対策

1. **入力検証**: すべての外部入力を検証する
2. **暗号化**: 機密データはSSL/TLSで暗号化する
3. **認証**: 適切な認証メカニズムを実装する
4. **タイムアウト**: DoS攻撃を防ぐためタイムアウトを設定する
5. **リソース制限**: 接続数やメモリ使用量を制限する

### 17.7.2 セキュアな通信の実装

SSL/TLS（Secure Sockets Layer/Transport Layer Security）は、ネットワーク通信を暗号化し、安全性を確保するプロトコルです。JavaではSSLSocketを使用してセキュアな通信を実装できます。

<span class="listing-number">**サンプルコード17-9**</span>

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

<span class="listing-number">**サンプルコード17-10**</span>

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

