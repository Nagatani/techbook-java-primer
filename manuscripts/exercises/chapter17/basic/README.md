# 第17章 ネットワークプログラミング - 基礎レベル課題

## 概要
本ディレクトリでは、Javaにおけるネットワークプログラミングの基礎を学習します。TCP/IP通信の基本から始め、クライアント・サーバーモデルの実装を通じて、ネットワークアプリケーションの基礎を身につけます。

## 課題一覧

### 課題1: TCPエコークライアント・サーバー
**EchoServer.java & EchoClient.java**

基本的なTCP通信を実装し、クライアント・サーバーモデルを理解します。

**要求仕様：**
- TCPサーバーの実装（指定ポートでリッスン）
- クライアントからのメッセージをエコーバック
- 複数回のメッセージ送受信に対応
- 適切な例外処理とリソース管理

**実装のポイント：**
```java
// サーバー側
ServerSocket serverSocket = new ServerSocket(port);
Socket clientSocket = serverSocket.accept();

// クライアント側
Socket socket = new Socket(host, port);
PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
BufferedReader in = new BufferedReader(
    new InputStreamReader(socket.getInputStream()));
```

**期待される動作：**
```
サーバー起動: ポート 8080 でリッスン中...
クライアント接続: 127.0.0.1
受信: Hello, Server!
送信: Echo: Hello, Server!
受信: How are you?
送信: Echo: How are you?
クライアント切断
```

### 課題2: UDPチャットプログラム
**UDPChat.java**

UDPを使った簡単なチャットプログラムを実装します。

**要求仕様：**
- DatagramSocketを使用したUDP通信
- ブロードキャストによる同一ネットワーク内の通信
- 送信と受信を別スレッドで処理
- メッセージフォーマット: "ユーザー名: メッセージ"

**実装のポイント：**
```java
// UDP送信
DatagramSocket socket = new DatagramSocket();
byte[] buffer = message.getBytes();
DatagramPacket packet = new DatagramPacket(
    buffer, buffer.length, 
    InetAddress.getByName("255.255.255.255"), port);
socket.send(packet);

// UDP受信
DatagramSocket socket = new DatagramSocket(port);
byte[] buffer = new byte[1024];
DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
socket.receive(packet);
```

### 課題3: 簡易HTTPクライアント
**SimpleHttpClient.java**

HTTPプロトコルを理解し、基本的なHTTPクライアントを実装します。

**要求仕様：**
- ソケットを使ったHTTP GETリクエストの送信
- HTTPレスポンスのヘッダーとボディの解析
- リダイレクト（3xx）の処理
- Content-Lengthに基づく適切な読み込み

**実装のポイント：**
```java
// HTTPリクエストの構築
String request = "GET " + path + " HTTP/1.1\r\n" +
                "Host: " + host + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";

// レスポンスの解析
String statusLine = in.readLine();
// ヘッダーの読み込み
Map<String, String> headers = new HashMap<>();
String line;
while (!(line = in.readLine()).isEmpty()) {
    String[] parts = line.split(": ", 2);
    headers.put(parts[0], parts[1]);
}
```

### 課題4: ポートスキャナー
**PortScanner.java**

ネットワークセキュリティの基礎を理解するため、シンプルなポートスキャナーを実装します。

**要求仕様：**
- 指定されたホストの特定範囲のポートをスキャン
- 開いているポート（接続可能）を検出
- タイムアウト設定による高速化
- 並列スキャンによる効率化（オプション）

**実装のポイント：**
```java
for (int port = startPort; port <= endPort; port++) {
    try (Socket socket = new Socket()) {
        socket.connect(
            new InetSocketAddress(host, port), 
            timeout);
        System.out.println("Port " + port + " is open");
    } catch (IOException e) {
        // ポートが閉じているかタイムアウト
    }
}
```

**注意事項：**
- 自分が管理するホストのみをスキャンすること
- 教育目的での使用に限定すること

## 学習のポイント

1. **ソケットの基本操作**
   - ServerSocketとSocketの違い
   - ストリームの取得と使用方法
   - 正しいクローズ処理

2. **プロトコルの理解**
   - TCPの信頼性とUDPの高速性
   - HTTPプロトコルの構造
   - テキストプロトコルとバイナリプロトコル

3. **エラー処理**
   - ネットワーク固有の例外
   - タイムアウトの設定
   - リトライロジック

4. **リソース管理**
   - try-with-resourcesの活用
   - ソケットのクローズ順序
   - スレッドの適切な終了

## 提出物

1. 各課題のソースコード（*.java）
2. 実行結果のスクリーンショットまたはログ
3. 各課題で遭遇した問題とその解決方法のメモ
4. ネットワークプログラミングの基礎概念の理解度確認レポート

## 評価基準

- **正確性（40%）**: プログラムが仕様通りに動作するか
- **エラー処理（25%）**: 異常系の適切な処理
- **コード品質（20%）**: 可読性、適切な設計
- **理解度（15%）**: プロトコルやネットワーク概念の理解

## 参考リソース

- [Java Networking Tutorial](https://docs.oracle.com/javase/tutorial/networking/)
- [RFC 2616 - HTTP/1.1](https://www.ietf.org/rfc/rfc2616.txt)
- 本書第17章: ネットワークプログラミング