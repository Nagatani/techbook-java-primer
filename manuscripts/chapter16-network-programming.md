# <b>16章</b> <span>ネットワークプログラミング</span> <small>インターネット通信の実装技術</small>

## 本章の学習目標

### この章で学ぶこと

1. ネットワークプログラミングの基礎
    - TCP/IPの階層とそれぞれの層の役割
    - TCPとUDPの違いと使い分け
    - ソケットの考え方とクライアント・サーバーの役割
2. TCP通信の実装
    - ServerSocketとSocketクラスの活用
    - クライアント・サーバーアプリケーションの構築
    - マルチスレッドによる並行サーバーの実現
3. UDP通信とHTTPクライアント
    - DatagramSocketとDatagramPacketの使い方
    - HttpURLConnectionとHttpClientによるWebアクセス
4. 実用的なネットワークアプリケーション
    - チャットアプリケーションの開発
    - JSON APIクライアントの実装
    - エラー処理とデバッグ技術

### この章を始める前に

第14章のファイルI/Oとストリーム、第15章のマルチスレッドプログラミングを理解していれば準備完了です。

## はじめに

現代のソフトウェア開発において、ネットワーク通信は避けて通れない重要な技術です。Webアプリケーション、モバイルアプリ、IoTデバイスなど、ほとんどのアプリケーションは何らかの形でネットワーク通信を行っています。

本章では、Javaにおけるネットワークプログラミングの基礎から実践的な応用まで、体系的に学習します。ネットワークプログラミングは一見複雑に見えますが、基本的な概念を理解すれば、さまざまな応用が可能になります。

## ネットワークプログラミングの基礎

ネットワークで通信するプログラムを書く前に、通信を支えるしくみの全体像と、通信の相手を指定する方法を確かめておきます。

### TCP/IPの階層

インターネットの通信は、TCP/IPと呼ばれるプロトコル（通信の決まりごと）の組み合わせで成り立っています。
TCP/IPでは、通信に必要な仕事をいくつかの層に分け、層ごとに別のプロトコルが受け持ちます。
この層の積み重なりをプロトコルスタックと呼びます。

| 層 | 主なプロトコル | 受け持つ仕事 | 本章で使うクラス |
|---|---|---|---|
| アプリケーション層 | HTTP、DNS、SMTP | やり取りする内容の形式を決める | `HttpClient`など |
| トランスポート層 | TCP、UDP | ポート番号で相手のアプリケーションへ届ける | `Socket`、`DatagramSocket` |
| インターネット層 | IP | IPアドレスで相手のコンピューターへ届ける | （OSが受け持つ。アドレスは`InetAddress`で表す） |
| ネットワークインターフェイス層 | イーサネット、Wi-Fi | つながった機器の間で信号を送る | （OSと機器が受け持つ） |

データを送る側では、アプリケーション層から下の層へ順にデータを渡し、各層が自分の仕事に必要な情報（宛先のポート番号やIPアドレスなど）を付け加えます。
受け取る側では逆に、下の層から上の層へデータを渡し、各層が付け加えられた情報を読んで取り除きます。
このしくみにより、各層は自分の仕事だけを受け持てばよくなります。
たとえばHTTPで通信するプログラムは、データがどの経路を通って届くかを気にせず、HTTPのリクエストとレスポンスの形式だけを考えて書けます。
Javaのプログラムが直接扱うのはトランスポート層から上で、インターネット層から下はOSが受け持ちます。

### TCPとUDP

トランスポート層の代表的なプロトコルが、TCPとUDPです。

TCP（Transmission Control Protocol）は、データを送る前に相手との接続を確立するプロトコルです。
受け取った側は届いたことを送った側へ知らせ、その知らせが来なければ、送った側は同じデータを送り直します（再送）。
また、データには順番を表す番号が付いており、受け取った側で送った順に並べ直します。
そのため、データは欠けることなく、送った順に届きます。

UDP（User Datagram Protocol）は、接続を確立せずに、データをパケット（データグラム）と呼ぶまとまりのまま送るプロトコルです。
届いたことの確認も再送もしないので、パケットが途中で失われたり、送った順と違う順に届いたりすることがあります。
その代わりに、接続や確認のやり取りがないぶん、少ない遅れでデータを送れます。

次の図は、2つのプロトコルの送り方の違いを示したものです。

<div class="figure">

![TCPとUDPの通信の違い](images/diagrams/chapter16-tcp-vs-udp.svg)

</div>

違いをまとめると、次の表のようになります。

| 項目 | TCP | UDP |
|---|---|---|
| 接続 | 送る前に確立する | 確立しない |
| 届く順序 | 送った順に届く | 保証されない |
| 失われたデータ | 送り直す | 送り直さない |
| 遅れ | 確認と再送のぶん大きい | 小さい |
| Javaのクラス | `Socket`、`ServerSocket` | `DatagramSocket`、`DatagramPacket` |

データが少しでも欠けると困る通信には、TCPを使います。
Webページの取得（HTTP）、メール、ファイル転送などがその例です。
多少のデータが欠けても遅れなく届くことが大事な通信には、UDPを使います。
音声通話、動画の配信、オンラインゲームなどがその例です。
DNSの問い合わせのように、短い問い合わせと応答を1回ずつやり取りするだけの通信にもUDPが使われます。

本章では、「ソケットプログラミングの基礎」「マルチスレッドサーバーの実装」とチャットアプリケーションでTCPを、「UDP通信の実装」でUDPを使います。
HTTPはTCPを使って通信するアプリケーション層のプロトコルで、「HTTPクライアントの実装」で扱います。

### IPアドレスとポート番号

通信の相手は、IPアドレスとポート番号の組で指定します。

IPアドレスは、ネットワーク上のコンピューターを一意に識別する番号です。
IPv4では`192.168.1.1`のような32ビットの番号を、IPv6では`2001:db8::1`のような128ビットの番号を使います。

1台のコンピューターでは、複数のアプリケーションが同時に通信します。
ポート番号は、そのうちのどのアプリケーションと通信するかを識別する番号で、0から65535までの値をとります。
ポート番号は、範囲によって次の3つに分かれます。

- Well-knownポート（0〜1023）
    + HTTP（80）、HTTPS（443）、FTP（21）など
- 登録済みポート（1024〜49151）
- 動的・私的ポート（49152〜65535）

### ソケットとクライアント・サーバー

ソケットは、プログラムがネットワークで通信するための出入り口です。
1つのソケットは、IPアドレスとポート番号の組で表される通信の端点（エンドポイント）に対応します。
TCPの1本の接続は、自分と相手のIPアドレスとポート番号、合わせて4つの値で区別されます。
プログラムが自分のソケットへ書き込んだデータは、相手のソケットから読み出せます。
そのため、ファイルを読み書きするのと同じように、ストリームを使って通信できます。

通信する2つのプログラムは、クライアントとサーバーという役割に分かれます。
サーバーは、決まったポート番号で待ち受けて、クライアントからの要求に応える側です。
クライアントは、サーバーのIPアドレスとポート番号を指定して、通信を始める側です。
クライアント側のポート番号は、ふつうはOSが動的・私的ポートの範囲から自動で割り当てます。
WebブラウザーとWebサーバーをはじめ、多くのネットワークアプリケーションはこの形で作られています。

Javaでは、TCPのサーバーは`ServerSocket`で待ち受け、接続してきたクライアントごとに`Socket`を得ます。
クライアントは`Socket`を作ってサーバーに接続します。
UDPでは、サーバーもクライアントも`DatagramSocket`を使います。

## ソケットプログラミングの基礎

TCPの通信では、サーバーが`ServerSocket`で接続を待ち、クライアントが`Socket`で接続します。接続が確立すると、両者はそれぞれの`Socket`から得たストリームでデータを送り合います。この節のクライアント（サンプルコード16-1）とサーバー（サンプルコード16-2）のやりとりは、次の図の順に進みます。

<div class="figure">

![TCPのクライアントとサーバーのやりとり](images/diagrams/chapter16-tcp-socket.svg)

</div>

### 基本的なTCPクライアント

TCPクライアントは、Socketクラスを使用してサーバーに接続します。接続が確立されると、入力ストリームと出力ストリームを通じてデータの送受信を行います。TCPは信頼性の高い接続指向のプロトコルであり、データの順序と到達が保証されます。

以下のコードは、TCPクライアントの基本的な実装例です。Socketオブジェクトを作成してサーバーに接続し、PrintWriterで文字列を送信、BufferedReaderで応答を受信します。try-with-resources文を使用することで、通信終了時に自動的にソケットとストリームがクローズされます。

<span class="listing-number">**サンプルコード16-1**　SimpleTCPClient.java</span>

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
            IO.println("サーバーからの応答: " + response);
            
        } catch (UnknownHostException e) {
            System.err.println("ホストが見つかりません: " + serverAddress);
        } catch (IOException e) {
            System.err.println("I/Oエラーが発生しました: " + e.getMessage());
        }
    }
}
```

次は実行結果の一例です。

```
サーバーからの応答: Echo: Hello, Server!
```

※実際の実行にはサーバーが起動している必要があります。

### 基本的なTCPサーバー

TCPサーバーは、ServerSocketを使用してクライアントからの接続を待ち受けます。ServerSocketは指定されたポートで待機し、クライアントが接続すると新しいSocketインスタンスを生成します。このSocketを使用して、個々のクライアントと通信を行います。

以下のコードは、基本的なTCPサーバーの実装例です。このサーバーは8080番ポートで待機し、クライアントから受信したメッセージをエコーバックします。accept()メソッドは新しい接続があるまでブロックし、接続が確立されると各クライアントとの通信を個別に処理します。

<span class="listing-number">**サンプルコード16-2**　SimpleTCPServer.java（4〜42行目）</span>

```java
public class SimpleTCPServer {
    public static void main(String[] args) {
        int port = 8080;
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            IO.println("サーバーがポート " + port + " で起動しました");
            
            while (true) {
                // クライアントからの接続を待機
                Socket clientSocket = serverSocket.accept();
                IO.println("クライアントが接続しました: " + 
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
            IO.println("受信: " + message);
            
            // 応答を送信
            out.println("Echo: " + message);
            
        } catch (IOException e) {
            System.err.println("クライアント処理エラー: " + e.getMessage());
        }
    }
}
```

サーバー側の実行結果は次のとおりです。

```
サーバーがポート 8080 で起動しました
クライアントが接続しました: /127.0.0.1
受信: Hello, Server!
```

## マルチスレッドサーバーの実装

単一スレッドのサーバーでは、一度に1つのクライアントしか処理できません。実用的なサーバーでは、複数のクライアントを同時に処理します。

複数のクライアントを同時に処理できれば、どのクライアントも待たされずにサービスを利用できます。
1つのクライアントの処理に時間がかかっても、ほかのクライアントへの応答には影響しません。
クライアントの数が増えたときにも対応しやすくなります。
さらに、スレッドプールを使えば、使うスレッドの数に上限を設けて、メモリとCPUを無駄なく使えます。

### スレッドベースのサーバー

以下のコードは、マルチスレッドに対応したTCPサーバーの実装例です。ExecutorServiceを使用してスレッドプールを管理し、各クライアントの接続を独立したスレッドで処理します。このアプローチにより、複数のクライアントからの同時接続を効率的に処理できます。サーバーは簡単なプロトコルを実装しており、ECHO、TIME、QUITなどのコマンドに応答します。

<div class="figure">

![接続ごとにスレッドで処理するサーバーの構成](images/diagrams/chapter16-multithreaded-server.svg)

</div>

<span class="listing-number">**サンプルコード16-3**　MultiThreadedServer.java（5〜27、29、36〜61、70〜72行目）</span>

```java
public class MultiThreadedServer {
    private static final int PORT = 8080;
    private static final int THREAD_POOL_SIZE = 10;
    
    public static void main(String[] args) {
        ExecutorService executor = 
            Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            IO.println("マルチスレッドサーバーが起動しました");
            
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
// ...
class ClientHandler implements Runnable {
    // ...
    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(
                 new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(
                 clientSocket.getOutputStream(), true)) {
            
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                IO.println(
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
        // ...
        }
    }
}
```

## HTTPクライアントの実装

### HTTPプロトコルの基礎

HTTPは、Webでもっとも広く使用されているアプリケーション層プロトコルです。

HTTPリクエストは、次の形をしています。

```
GET /index.html HTTP/1.1
Host: www.example.com
User-Agent: Java/25
Accept: text/html
[空行]
[リクエストボディ（POSTの場合）]
```

HTTPレスポンスは、次の形をしています。

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

<span class="listing-number">**サンプルコード16-4**　SimpleHTTPClient.java（4〜21、25〜51行目）</span>

```java
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
            
            // HTTPリクエストを送信（HTTP/1.0で送ると、ボディが分割されずに届く）
            out.println("GET " + path + " HTTP/1.0");
            out.println("Host: " + host);
            out.println("Connection: close");
            out.println(); // 空行でヘッダー終了
            
            // ...
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
            
            IO.println("=== Headers ===");
            IO.println(headers);
            IO.println("=== Body ===");
            IO.println(body);
            
        } catch (IOException e) {
            System.err.println("エラー: " + e.getMessage());
        }
    }
}
```

次は実行結果の一例です。
ヘッダーの項目や値は、接続先のサーバーや実行した日時によって変わります。

```
=== Headers ===
HTTP/1.1 200 OK
Date: Wed, 11 Nov 2026 03:43:02 GMT
Content-Type: text/html; charset=utf-8
Connection: close
Server: cloudflare
Last-Modified: Sun, 08 Nov 2026 18:05:34 GMT
...（中略）

=== Body ===
<!doctype html><html lang=en><head><meta charset=utf-8>...（以下省略）
```

リクエストをHTTP/1.1で送ると、サーバーがボディを複数のかたまり（チャンク）に分けて返すことがあります。
そのときはボディの前後にかたまりの大きさを表す16進数の行が入るため、この例ではHTTP/1.0でリクエストを送っています。

### HttpURLConnectionの使用

Javaは、より高レベルなHTTP通信のためのAPIも提供しています。

以下のコードは、HttpURLConnectionを使用したHTTP GETリクエストの実装例です。このクラスはソケットレベルの詳細を隠蔽し、HTTPヘッダーの設定、レスポンスコードの取得、タイムアウトの設定などを簡単に行えます。この例では、GitHub APIにアクセスしてJSON形式のユーザー情報を取得しています。

<span class="listing-number">**サンプルコード16-5**　HttpURLConnectionExample.java（4〜9、17〜37、43〜44行目）</span>

```java
public class HttpURLConnectionExample {
    public static void main(String[] args) {
        try {
            URL url = URI.create("https://api.github.com/users/github").toURL();
            HttpURLConnection conn = 
                (HttpURLConnection) url.openConnection();
                // ...
            // レスポンスコードの確認
            int responseCode = conn.getResponseCode();
            IO.println("Response Code: " + responseCode);
            
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
                    
                    IO.println(
                        "Response: " + response.toString());
                }
            }
            
            // ...
    }
}
```

次は実行結果の一例です。

```
Response Code: 200
Response: {"login":"github","id":9919,"node_id":"MDEyOk9yZ2FuaXphdGlvbjk5MTk=","avatar_url":"https://avatars.githubusercontent.com/u/9919?v=4",...}
```

※JSONレスポンスの内容は実行時によって異なる場合があります。

### 非同期HTTPクライアント（Java 11+）

Java 11で導入された新しいHTTPクライアントAPIは、非同期処理をサポートし、HTTP/2にも対応しています。CompletableFutureを使用することで、ノンブロッキングなHTTP通信が可能になります。

<span class="listing-number">**サンプルコード16-6**　AsyncHttpClientExample.java（8〜10、17〜49行目）</span>

```java
public class AsyncHttpClientExample {
    
    public static void main(String[] args) throws Exception {
        // ...
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
        CompletableFuture<Void> done = future
            .thenApply(response -> {
                IO.println("Status: " + response.statusCode());
                return response.body();
            })
            .thenAccept(body -> {
                IO.println("Response Body: " + body);
            })
            .exceptionally(e -> {
                System.err.println("Error: " + e.getMessage());
                return null;
            });
        
        // 他の処理を並行して実行可能
        IO.println("他の処理を継続中...");
        
        // 連鎖させた処理がすべて終わるまで待つ（mainが先に終わるとプログラムが終了するため）
        done.join();
    }
}
```

実行結果：

```
他の処理を継続中...
Status: 200
Response Body: {
  "userId": 1,
  "id": 1,
  "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
  "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
}
```

実行結果の1行目が「他の処理を継続中...」になっているのは、`sendAsync`がレスポンスを待たずに戻るためです。
このように非同期のHTTPクライアントでは、レスポンスを待つ間もほかの処理を続けられ、複数のリクエストを並行して送ることもできます。
受け取ったあとの処理やエラー処理は、CompletableFutureのメソッドで順につなげて書けます。
また、`HttpClient`はHTTP/2に対応しているため、1つの接続で複数のリクエストを多重化して、通信の遅延を減らせます。

## UDP通信の実装

「TCPとUDP」で見たように、UDPは接続を確立せず、届いたことの確認も再送もしないプロトコルです。
この節では、UDPでメッセージを送り返すエコーサーバーとクライアントを作ります。

### UDPサーバー

以下のコードは、UDPサーバーの実装例です。DatagramSocketを使用してUDPパケットを受信し、クライアントにエコーレスポンスを送信します。UDPはコネクションレスのため、各パケットにはデータだけでなく送信元のアドレスとポート情報も含まれています。

<span class="listing-number">**サンプルコード16-7**　UDPServer.java</span>

```java
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDPServer {
    private static final int PORT = 9999;
    
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            IO.println("UDPサーバーがポート " + PORT + " で起動しました");
            
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            
            while (true) {
                // データを受信
                socket.receive(packet);
                
                String received = new String(packet.getData(), 0, 
                    packet.getLength(), StandardCharsets.UTF_8);
                IO.println("受信: " + received + " from " + 
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

サーバー側の実行結果は次のとおりです。

```
UDPサーバーがポート 9999 で起動しました
受信: Hello UDP Server! from /127.0.0.1
```

### UDPクライアント

以下のコードは、UDPクライアントの実装例です。このクライアントは、サーバーにメッセージを送信し、エコーレスポンスを受信します。UDP通信では、送信と受信の両方でDatagramPacketを使用し、パケット単位でデータをやり取りします。

<span class="listing-number">**サンプルコード16-8**　UDPClient.java</span>

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
            IO.println("送信: " + message);
            
            // レスポンスを受信
            byte[] responseBuffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(
                responseBuffer, responseBuffer.length);
            
            socket.receive(responsePacket);
            
            String response = new String(responsePacket.getData(), 0,
                responsePacket.getLength(), StandardCharsets.UTF_8);
            IO.println("受信: " + response);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

クライアント側の実行結果は次のとおりです。

```
送信: Hello UDP Server!
受信: Echo: Hello UDP Server!
```

UDPでは、送ったパケットや応答のパケットが途中で失われることがあります。
このクライアントは応答が届かないと`receive()`で待ち続けるので、実用のプログラムでは`setSoTimeout()`で待つ時間に上限を設け、送り直すかどうかをプログラム側で決めます。
また、TCPと違って1対1の接続を作らないので、ブロードキャストやマルチキャストで複数の相手へ同時に送れます。

## 実践的な例：チャットアプリケーション

### チャットサーバー

以下のコードは、マルチクライアント対応のチャットサーバーの実装例です。このサーバーは、接続されたすべてのクライアントからメッセージを受信し、他のすべてのクライアントに転送します。`Collections.synchronizedSet`でスレッドセーフにクライアントの集合を管理し、各クライアントとの通信は独立したスレッドで処理されます。

<span class="listing-number">**サンプルコード16-9**　ChatServer.java（6〜35、42、52〜74、90〜96行目）</span>

```java
public class ChatServer {
    private static final int PORT = 5000;
    private static Set<ClientHandler> clients = 
        Collections.synchronizedSet(new HashSet<>());
    
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            IO.println("チャットサーバーが起動しました");
            
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
    // ...
    static class ClientHandler implements Runnable {
        // ...
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
                    if (message.equalsIgnoreCase("/quit")) {
                        break;
                    }
                    broadcast(username + ": " + message, this);
                }
                // ...
        }
        
        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
```

### チャットクライアント

以下のコードは、チャットクライアントの実装例です。このクライアントは、サーバーに接続し、別スレッドでメッセージの受信を処理しながら、メインスレッドでキーボードからの入力を受け付けます。この設計により、メッセージの送受信を同時に行うことができます。

<span class="listing-number">**サンプルコード16-10**　ChatClient.java（4、8〜40、47〜48行目）</span>

```java
public class ChatClient {
    // ...
    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(
                 socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()))) {
            
            // 受信スレッドを開始
            Thread receiveThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        IO.println(message);
                    }
                } catch (IOException e) {
                    System.err.println("受信エラー: " + e.getMessage());
                }
            });
            receiveThread.start();
            
            // メッセージ送信ループ（入力の終わりでIO.readlnはnullを返す）
            String userInput;
            while ((userInput = IO.readln()) != null) {
                out.println(userInput);
                
                if (userInput.equalsIgnoreCase("/quit")) {
                    break;
                }
            }
            
            // 受信側を閉じて受信スレッドのreadLine()を終わらせてから、資源を閉じる
            socket.shutdownInput();
            receiveThread.join();
            // ...
    }
}
```

このプログラムはチャットサーバー（ポート5000）への接続を試みます。
サーバーが起動していない場合は、次のような接続エラーが表示されます。

```
接続エラー: Connection refused
```

正常に接続できた場合は、メッセージの送受信が可能になり、`/quit`コマンドで終了できます。
サーバーは`/quit`を受け取ると、その接続を閉じて退室を知らせます。

終了するときは、try-with-resources文が資源を閉じる前に、`socket.shutdownInput()`で受信側を閉じ、受信スレッドの終了を`join()`で待っています。
受信スレッドは`in.readLine()`で読み込みを待っているあいだ`BufferedReader`のロックを持っています。
そのまま`BufferedReader`を閉じようとすると、メインスレッドがそのロックを待ち続けて終わらなくなります。
受信側を閉じると`readLine()`が`null`を返して受信スレッドが終わるため、そのあとで資源を閉じられます。

## JSON APIクライアントの実装

現代のWebアプリケーションでは、JSON形式でのデータ交換が標準的です。JavaでJSON APIを扱う方法を学びます。

以下のコードは、汎用的なJSON APIクライアントの実装例です。Jacksonライブラリを使用してJSONのシリアライズ/デシリアライズを行い、Java 11のHttpClientでHTTPリクエストを送信します。このクライアントはGETとPOSTメソッドをサポートし、型安全な方法でJSONデータを扱うことができます。

JacksonはJavaの標準APIではなく、外部ライブラリです。
このサンプルを動かすには、Jacksonをプロジェクトに追加します。
外部ライブラリの追加方法とJacksonの使い方は、第22章「ドキュメントと外部ライブラリ」で学びます。
ここでは、`ObjectMapper`の`readValue`メソッドがJSONの文字列を指定した型のオブジェクトに変換する、とだけ押さえてコードを読んでください。

<span class="listing-number">**サンプルコード16-11**　JsonApiClient.java（7〜34、74行目）</span>

```java
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
    // ...
}
```

JSONとJavaのオブジェクトとの変換（シリアライズとデシリアライズ）は自分で書かず、JacksonやGsonなどのライブラリに任せます。
`get`メソッドでは、受け取りたい型を`Class<T>`で渡すので、JSONの内容を型の決まったオブジェクトとして受け取れます。
リクエストには、送るデータの形式を表すContent-Typeヘッダーや、受け取りたい形式を表すAcceptヘッダーを正しく付けます。
また、レスポンスを使う前にHTTPステータスコードを確かめ、200以外のときはエラーとして扱います。

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

<span class="listing-number">**サンプルコード16-12**　SSLClient.java</span>

```java
import javax.net.ssl.*;
import java.io.*;

public class SSLClient {
    public static void main(String[] args) {
        String host = "www.example.com";
        int port = 443;

        SSLSocketFactory factory =
            (SSLSocketFactory) SSLSocketFactory.getDefault();
        try (SSLSocket socket =
                (SSLSocket) factory.createSocket(host, port)) {

            // サポートされているプロトコルを設定
            socket.setEnabledProtocols(
                new String[] {"TLSv1.2", "TLSv1.3"});

            // ハンドシェイクを開始
            socket.startHandshake();
            SSLSession session = socket.getSession();
            IO.println("SSL接続が確立されました");
            IO.println("使用しているプロトコル: " + session.getProtocol());
            IO.println("暗号スイート: " + session.getCipherSuite());

            // 以降は通常のソケットと同様に使用
            PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

            // HTTPSのリクエストを送り、レスポンスのヘッダーを表示
            out.println("GET / HTTP/1.0");
            out.println("Host: " + host);
            out.println();
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                IO.println(line);
            }

        } catch (IOException e) {
            System.err.println("SSL接続エラー: " + e.getMessage());
        }
    }
}
```

次は実行結果の一例です。
プロトコル・暗号スイート・ヘッダーの内容は、接続先のサーバーや実行した日時によって変わります。

```
SSL接続が確立されました
使用しているプロトコル: TLSv1.3
暗号スイート: TLS_AES_256_GCM_SHA384
HTTP/1.1 200 OK
Date: Wed, 11 Nov 2026 03:47:19 GMT
Content-Type: text/html; charset=utf-8
Connection: close
...（以下省略）
```

## パフォーマンスの最適化

### ノンブロッキングI/O（NIO）

Java NIOを使用すると、単一スレッドで複数の接続を管理できるスケーラブルなネットワークプログラミングが可能です。

以下のコードは、NIO（Non-blocking I/O）を使用したサーバーの実装例です。Selectorパターンを利用することで、単一のスレッドで複数のクライアント接続を効率的に管理できます。この方式では、接続ごとにスレッドを作るサーバーと違い、同時接続が増えてもスレッドの数は増えません。

<span class="listing-number">**サンプルコード16-13**　NIOServer.java（7〜61行目）</span>

```java
public class NIOServer {
    private static final int PORT = 8080;
    
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverChannel = 
            ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(PORT));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        
        IO.println("NIOサーバーが起動しました");
        
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
    + NIOのSelectorを使い、1つのスレッドで多数の接続を扱う

### 次のステップ

本章で学んだHTTPとソケットをもとにした技術として、次のようなものがあります。

- RESTful APIの設計と実装
    + REST（Representational State Transfer）はWeb APIの設計原則である
    + APIが扱うデータを「リソース」とみなしてURLで指し示し、GET（取得）・POST（作成）・PUT（更新）・DELETE（削除）のHTTPメソッドで操作する
    + 各リクエストは独立し、サーバーは前のリクエストの状態を覚えない（ステートレス）。レスポンスにはキャッシュしてよいかどうかを示す
    + 実装には、Spring BootやJAX-RS（Jersey）などのフレームワークを使うことが多い
- WebSocketを使用したリアルタイム通信
    + HTTPのアップグレードのしくみで接続を切り替え、接続を保ったまま双方向に通信するプロトコルである
    + 接続が続いている間はサーバーからもクライアントへデータを送れ（プッシュ）、メッセージごとにリクエストを送らないぶん遅延が小さい
    + チャット、ゲーム、株価配信などに向く。クライアントは標準APIの`HttpClient`の`newWebSocketBuilder()`や、Jettyなどのライブラリで作れる
- gRPCなどのRPCフレームワーク
- マイクロサービスアーキテクチャ

## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter16/`

### 課題構成
- 本章の基本概念の理解確認
- 応用的な実装練習
- 実践的な総合問題

1. 基礎課題
    + シンプルなTCPサーバー・クライアントの実装
2. 発展課題
    + チャットアプリケーションの開発
3. チャレンジ課題
    + HTTPサーバーの実装とRESTful API設計

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダー内のREADME.mdを参照してください。

次のステップ： 基礎課題が完了したら、第17章「GUIプログラミングの基礎」に進みましょう。

## よくあるエラーと対処法

ネットワークプログラムでは、相手のサーバーや通信経路といった、プログラムの外の要因によってエラーが起こります。本章の内容に関連して出会いやすいエラーは次のとおりです。

- 接続のエラー
    + 接続先のサーバーが起動していない、ポート番号が違う、ファイアウォールで遮断されているといった原因で`ConnectException`などが発生する。タイムアウトも設定する
- データの送受信のエラー
    + 送信側と受信側で文字エンコーディングがそろっていない、データを最後まで受信していないといった原因がある
- HTTPのステータスコードの確認漏れ
    + HTTPクライアントは404や500のレスポンスでも例外をスローしないため、ステータスコードを確かめる
- リソースの解放漏れ
    + ソケットやストリームはtry-with-resources文で確実に閉じる

エラーの例と修正したコード、接続を診断するデバッグのヒントは、GitHubリポジトリにまとめています。<br>
`https://github.com/Nagatani/techbook-java-primer/blob/main/appendix/h-common-errors/chapters/chapter16.md`
