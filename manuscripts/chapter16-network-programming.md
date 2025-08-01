# <b>17章</b> <span>ネットワークプログラミング</span> <small>インターネット通信の実装技術</small>

## 本章の学習目標

### この章で学ぶこと

1. ネットワークプログラミングの基礎
    - TCP/IPプロトコルスタックの理解
    - TCPとUDPの違いと使い分け
    - ソケットの概念とJavaでの実装
2. TCP通信の実装
    - ServerSocketとSocketクラスの活用
    - クライアント・サーバーアプリケーションの構築
    - マルチスレッドによる並行サーバーの実現
3. UDP通信とHTTPクライアント
    - DatagramSocketとDatagramPacketの使い方
    - UDPのブロードキャスト通信
    - HTTPURLConnectionによるWebアクセス
4. 実用的なネットワークアプリケーション
    - チャットアプリケーションの開発
    - ファイル転送プログラムの実装
    - エラー処理とデバッグ技術

### この章を始める前に

第15章のファイルI/Oとストリーム、第16章のマルチスレッドプログラミング、IPアドレスとポート番号の基本概念を理解していれば準備完了です。

## はじめに

現代のソフトウェア開発において、ネットワーク通信は避けて通れない重要な技術です。Webアプリケーション、モバイルアプリ、IoTデバイスなど、ほとんどのアプリケーションは何らかの形でネットワーク通信を行っています。

本章では、Javaにおけるネットワークプログラミングの基礎から実践的な応用まで、体系的に学習します。ネットワークプログラミングは一見複雑に見えますが、基本的な概念を理解すれば、さまざまな応用が可能になります。

## ネットワークプログラミングの基礎





### IPアドレスとポート番号

- IPアドレス
-    + ネットワーク上のコンピュータを一意に識別する番号
  - IPv4: 192.168.1.1（32ビット）
  - IPv6: 2001:db8::1（128ビット）

- ポート番号
-    + 同一コンピュータ上の複数のアプリケーションを識別する番号（0-65535）
  - Well-knownポート（0-1023）: HTTP(80)、HTTPS(443)、FTP(21)など
  - 登録済みポート（1024-49151）
  - 動的・私的ポート（49152-65535）



## ソケットプログラミングの基礎



### 基本的なTCPクライアント

TCPクライアントは、Socketクラスを使用してサーバーに接続します。接続が確立されると、入力ストリームと出力ストリームを通じてデータの送受信を行います。TCPは信頼性の高い接続指向のプロトコルであり、データの順序と到達が保証されます。

以下のコードは、TCPクライアントの基本的な実装例です。Socketオブジェクトを作成してサーバーに接続し、PrintWriterで文字列を送信、BufferedReaderで応答を受信します。try-with-resources文を使用することで、通信終了時に自動的にソケットとストリームがクローズされます。

<span class="listing-number">**サンプルコード17-2**</span>

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

### 基本的なTCPサーバー

TCPサーバーは、ServerSocketを使用してクライアントからの接続を待ち受けます。ServerSocketは指定されたポートで待機し、
クライアントが接続すると新しいSocketインスタンスを生成します。このSocketを使用して、個々のクライアントと通信を行います。

以下のコードは、基本的なTCPサーバーの実装例です。このサーバーは8080番ポートで待機し、クライアントから受信したメッセージをエコーバックします。accept()メソッドは新しい接続があるまでブロックし、接続が確立されると各クライアントとの通信を個別に処理します。

<span class="listing-number">**サンプルコード17-4**</span>

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

## マルチスレッドサーバーの実装

単一スレッドのサーバーでは、一度に1つのクライアントしか処理できません。実用的なサーバーでは、複数のクライアントを同時に処理します。

マルチクライアント対応の重要性。
- 同時接続性
-    + 複数のクライアントが同時にサービスを利用できる
- スケーラビリティクライアント数の増加に対応できる
- 応答性1つのクライアントの処理が他のクライアントに影響しない
- リソース効率スレッドプールを使用することでメモリとCPUの最適な利用が可能

### スレッドベースのサーバー

以下のコードは、マルチスレッドに対応したTCPサーバーの実装例です。ExecutorServiceを使用してスレッドプールを管理し、各クライアントの接続を独立したスレッドで処理します。このアプローチにより、複数のクライアントからの同時接続を効率的に処理できます。サーバーは簡単なプロトコルを実装しており、ECHO、TIME、QUITなどのコマンドに応答します。

<span class="listing-number">**サンプルコード17-6**</span>

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

## HTTPクライアントの実装

### HTTPプロトコルの基礎

HTTPは、Webでもっとも広く使用されているアプリケーション層プロトコルです。

HTTPリクエストの構造。
```
GET /index.html HTTP/1.1
Host: www.example.com
User-Agent: Java/17
Accept: text/html
[空行]
[リクエストボディ（POSTの場合）]
```

HTTPレスポンスの構造。
```
HTTP/1.1 200 OK
Content-Type: text/html
Content-Length: 1234
[空行]
[レスポンスボディ]
```

### 簡単なHTTPクライアント

ソケットを使用して直接HTTPプロトコルを話すクライアントの実装例です。この方法は教育的価値が高く、HTTPプロトコルの動作を理解するのに役立ちます。
実際の開発では、後述するHttpURLConnectionやHTTPクライアントライブラリを使用することが一般的ですが、基礎となる仕組みを理解することは重要です。

<span class="listing-number">**サンプルコード17-8**</span>

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

### HttpURLConnectionの使用

Javaは、より高レベルなHTTP通信のためのAPIも提供しています。

以下のコードは、HttpURLConnectionを使用したHTTP GETリクエストの実装例です。このクラスはソケットレベルの詳細を隠蔽し、HTTPヘッダーの設定、レスポンスコードの取得、タイムアウトの設定などを簡単に行えます。この例では、GitHub APIにアクセスしてJSON形式のユーザー情報を取得しています。

<span class="listing-number">**サンプルコード17-10**</span>

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

### 非同期HTTPクライアント（Java 11+）

Java 11で導入された新しいHTTPクライアントAPIは、非同期処理をサポートし、HTTP/2にも対応しています。CompletableFutureを使用することで、ノンブロッキングなHTTP通信が可能になります。

<span class="listing-number">**サンプルコード17-12**</span>

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

非同期HTTPの利点。
- ノンブロッキング処理により、レスポンスを待つ間も他の処理を継続できる
- 複数のHTTPリクエストを並行して送信できる
- CompletableFutureによる柔軟なエラーハンドリングと処理の連鎖
- HTTP/2のサポートによる多重化と低遅延通信の実現

## 17.5.5 UDP通信の実装

UDPは、コネクションレスで信頼性の低いプロトコルですが、低遅延で高速な通信が可能です。リアルタイムゲーム、ストリーミング、DNSなどで使用されます。

以下のコードは、UDPサーバーの実装例です。DatagramSocketを使用してUDPパケットを受信し、クライアントにエコーレスポンスを送信します。UDPはコネクションレスのため、各パケットにはデータだけでなく送信元のアドレスとポート情報も含まれています。

<span class="listing-number">**サンプルコード17-14**</span>

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

以下のコードは、UDPクライアントの実装例です。このクライアントは、サーバーにメッセージを送信し、エコーレスポンスを受信します。UDP通信では、送信と受信の両方でDatagramPacketを使用し、パケット単位でデータをやり取りします。

<span class="listing-number">**サンプルコード17-16**</span>

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

UDPの特徴と使用場面。
- パケット損失の可能性があるが、高速
- 順序保証がない
- ブロードキャストやマルチキャストが可能
- リアルタイム性が重要なアプリケーションに適している

## 実践的な例：チャットアプリケーション

### チャットサーバー

以下のコードは、マルチクライアント対応のチャットサーバーの実装例です。このサーバーは、接続されたすべてのクライアントからメッセージを受信し、他のすべてのクライアントに転送します。ConcurrentHashMapを使用してスレッドセーフにクライアントのリストを管理し、各クライアントとの通信は独立したスレッドで処理されます。

<span class="listing-number">**サンプルコード17-18**</span>

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

### チャットクライアント

以下のコードは、チャットクライアントの実装例です。このクライアントは、サーバーに接続し、別スレッドでメッセージの受信を処理しながら、メインスレッドでユーザーからの入力を受け付けます。この設計により、メッセージの送受信を同時に行うことができます。

<span class="listing-number">**サンプルコード17-20**</span>

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

実行結果（サーバー接続時の例）：
```
接続エラー: Connection refused
```

> このプログラムはチャットサーバー（ポート5000）への接続を試みます。
> サーバーが起動していない場合は上記のような接続エラーが表示されます。
> 正常に接続できた場合は、メッセージの送受信が可能になり、`/quit`コマンドで終了できます。

## 17.6.3 JSON APIクライアントの実装

現代のWebアプリケーションでは、JSON形式でのデータ交換が標準的です。JavaでJSON APIを扱う方法を学びます。

以下のコードは、汎用的なJSON APIクライアントの実装例です。Jackson ライブラリを使用してJSONのシリアライズ/デシリアライズを行い、Java 11のHttpClientでHTTPリクエストを送信します。このクライアントはGETとPOSTメソッドをサポートし、型安全な方法でJSONデータを扱うことができます。

<span class="listing-number">**サンプルコード17-22**</span>

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

JSON処理のポイント。
- Jackson, Gsonなどのライブラリを使用してJSONのシリアライズ/デシリアライズ
- 正しいContent-TypeとAcceptヘッダーの設定
- エラーハンドリングとHTTPステータスコードの確認
- 型安全なデータマッピング

## 17.6.4 REST API実装の基礎

REST（Representational State Transfer）は、Web APIの設計原則です。JavaでRESTful APIを実装する基本的な概念を理解しましょう。

RESTの原則。
- リソース指向すべてをリソースとして扱う
- 統一インターフェイスHTTPメソッド（GET, POST, PUT, DELETE）のRESTfulな使用
- ステートレス各リクエストが独立している
- キャッシュ可能性レスポンスのキャッシュ制御

実際のREST API実装には、Spring Boot、JAX-RS（Jersey）などのフレームワークを使用することが一般的です。

## 17.6.5 WebSocketクライアントの基礎

WebSocketは、双方向のリアルタイム通信を可能にするプロトコルです。HTTPのアップグレードメカニズムを使用して、永続的な接続を確立します。

WebSocketの特徴。
- 双方向通信サーバーからクライアントへのプッシュが可能
- 低レイテンシHTTPのオーバーヘッドなし
- リアルタイム性チャット、ゲーム、株価配信などに最適

Java標準APIやJettyなどのライブラリを使用してWebSocketクライアントを実装できます。

## セキュリティの考慮事項

ネットワークプログラミングでは、セキュリティを常に意識してください。

### 基本的なセキュリティ対策

1. 入力検証
    + すべての外部入力を検証する
2. 暗号化
    + 機密データはSSL/TLSで暗号化する
3. 認証
    + 安全な認証メカニズムを実装する
4. タイムアウト
    + DoS攻撃を防ぐためタイムアウトを設定する
5. リソース制限
    + 接続数やメモリ使用量を制限する

### セキュアな通信の実装

SSL/TLS（Secure Sockets Layer/Transport Layer Security）は、ネットワーク通信を暗号化し、安全性を確保するプロトコルです。
JavaではSSLSocketを使用してセキュアな通信を実装できます。

以下のコードは、SSLSocketを使用したセキュアなクライアント接続の実装例です。SSLSocketFactoryから生成されたSSLSocketは、通常のSocketと同様に使用できますが、内部的にはすべての通信がSSL/TLSプロトコルで暗号化されます。最新のTLSプロトコルバージョンを明示的に指定することで、より安全な通信を実現します。

<span class="listing-number">**サンプルコード17-24**</span>

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

## パフォーマンスの最適化

### ノンブロッキングI/O（NIO）

Java NIOを使用すると、単一スレッドで複数の接続を管理できるスケーラブルなネットワークプログラミングが可能です。

以下のコードは、NIO（Non-blocking I/O）を使用したサーバーの実装例です。Selectorパターンを利用することで、単一のスレッドで複数のクライアント接続を効率的に管理できます。この方式は、従来のスレッドベースのサーバーと比較して、多数の同時接続を処理する際のリソース効率が格段に向上します。

<span class="listing-number">**サンプルコード17-26**</span>

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

## まとめ

本章では、Javaにおけるネットワークプログラミングの基礎から実践的な応用まで学習しました。

### 重要なポイント

1. ソケットプログラミング
    + TCPとUDPの違いを理解し、用途に応じて使い分ける
2. マルチスレッド
    + 複数のクライアントを同時に処理するサーバーの実装
3. プロトコル
    + HTTPなどのアプリケーション層プロトコルの理解
4. セキュリティ
    + 暗号化、認証、入力検証の重要性
5. パフォーマンス
    + NIOを使用した非同期I/Oによる応答時間の短縮

### 次のステップ

- RESTful APIの設計と実装
- WebSocketを使用したリアルタイム通信
- gRPCなどの最新のRPCフレームワーク
- マイクロサービスアーキテクチャ

## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter17/`

### 課題構成
- 本章の基本概念の理解確認
- 応用的な実装練習
- 実践的な総合問題

詳細な課題内容と実装のヒントは、各課題フォルダ内のREADME.mdを参照してください。

1. 基礎課題
    + シンプルなTCPサーバー・クライアントの実装
2. 発展課題
    + チャットアプリケーションの開発
3. チャレンジ課題
    + HTTPサーバーの実装とRESTful API設計

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

次のステップ： 基礎課題が完了したら、第18章「GUIプログラミング基礎」に進みましょう。

## よくあるエラーと対処法

本章では、ネットワークプログラミングに特有のエラーを扱います。

### 本章特有のエラー

#### 1. 基本的な接続エラー
問題: サーバーへの接続が失敗する

<span class="listing-number">**サンプルコード17-27**</span>

```java
// エラー例
Socket socket = new Socket("example.com", 80);
// ConnectException, UnknownHostException, SocketTimeoutException
```

解決策:

<span class="listing-number">**サンプルコード17-28**</span>

```java
// タイムアウト設定とリトライ
Socket socket = new Socket();
socket.connect(new InetSocketAddress(host, port), 5000); // 5秒タイムアウト

// リトライロジック
for (int i = 0; i < MAX_RETRIES; i++) {
    try {
        // 接続試行
    } catch (IOException e) {
        if (i == MAX_RETRIES - 1) throw e;
        Thread.sleep(RETRY_DELAY * (i + 1)); // 指数バックオフ
    }
}
```

重要なポイント:
- 必ずタイムアウトを設定
- UnknownHostExceptionはリトライ不要
- 指数バックオフで待機時間を増やす

#### 2. データ送受信エラー
問題: 文字エンコーディングやデータの不完全な受信

<span class="listing-number">**サンプルコード17-29**</span>

```java
// エラー例
out.write(data.getBytes()); // エンコーディング不明
// データが分割されて到着する可能性
```

解決策:

<span class="listing-number">**サンプルコード17-30**</span>

```java
// UTF-8を明示的に指定
byte[] bytes = data.getBytes(StandardCharsets.UTF_8);

// データサイズを先に送信
DataOutputStream out = new DataOutputStream(socket.getOutputStream());
out.writeInt(bytes.length);
out.write(bytes);

// 完全な受信を保証
DataInputStream in = new DataInputStream(socket.getInputStream());
int size = in.readInt();
byte[] buffer = new byte[size];
in.readFully(buffer); // すべて読み込むまで待機
```

重要なポイント:
- 常にUTF-8を使用
- データサイズを事前に送信
- readFully()で完全受信保証

#### 3. HTTPクライアントエラー
問題: HTTPステータスコードの未チェック

<span class="listing-number">**サンプルコード17-31**</span>

```java
// エラー例
HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
String body = response.body(); // ステータスコードを確認しない
```

解決策:

<span class="listing-number">**サンプルコード17-32**</span>

```java
int status = response.statusCode();
if (status >= 200 && status < 300) {
    return response.body();
} else if (status == 404) {
    throw new IOException("リソースが見つかりません");
} else if (status >= 500) {
    throw new IOException("サーバーエラー: " + status);
}
```

重要なポイント:
- 2xxは成功、4xxはクライアントエラー、5xxはサーバーエラー
- エラーレスポンスのボディも確認
- リトライ可能なエラーを識別

#### 4. リソース管理エラー
問題: ソケットやストリームが適切に閉じられない

<span class="listing-number">**サンプルコード17-33**</span>

```java
// エラー例
ServerSocket server = new ServerSocket(8080);
while (true) {
    Socket client = server.accept();
    handleClient(client); // clientが閉じられない
}
```

解決策:

<span class="listing-number">**サンプルコード17-34**</span>

```java
// try-with-resources使用
try (ServerSocket server = new ServerSocket(8080)) {
    while (running) {
        try (Socket client = server.accept()) {
            handleClient(client);
        } catch (IOException e) {
            // クライアントエラーをログ
        }
    }
}
```

重要なポイント:
- try-with-resourcesで自動クローズ
- 各クライアントを別スレッドで処理
- シャットダウンフックを実装

### 関連する共通エラー

以下のエラーも本章の内容に関連します。

- **IOExceptionの詳細**（→ 第14章）
  - ネットワークI/Oでの例外処理
- **タイムアウト設定**（→ 付録B.15）
  - 詳細なタイムアウト戦略
- **セキュリティ考慧**（→ 付録B.16）
  - SSL/TLSの詳細設定

### デバッグのヒント

1. 接続診断

<span class="listing-number">**サンプルコード17-35**</span>

   ```java
   InetAddress.getByName(host).isReachable(5000);
   // ping相当の確認
   ```

2. HTTPデバッグ

<span class="listing-number">**サンプルコード17-36**</span>

   ```java
   System.setProperty("java.net.debug", "all");
   // 詳細なネットワークログ
   ```

3. ポートスキャン
   - telnetやncコマンドで接続テスト
   - Wiresharkでパケットキャプチャ

### さらに学ぶには

- 付録H: Java共通エラーガイド（I/Oエラー）
- 付録B.15: ネットワークプログラミング詳細
- 第22章: HTTPクライアントライブラリの活用

