# 第16章 ネットワークプログラミング：よくあるエラーと対処法

書籍『Java入門』第16章「ネットワークプログラミング」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

本章では、ネットワークプログラミングに特有のエラーを扱います。

## 基本的な接続エラー

サーバーへの接続が失敗します。

### エラー例

```java
// エラー例
Socket socket = new Socket("example.com", 80);
// ConnectException, UnknownHostException, SocketTimeoutException
```

### 対処法

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
        Thread.sleep(RETRY_DELAY * (1L << i)); // 指数バックオフ（1倍、2倍、4倍…）
    }
}
```

### 重要なポイント

- 必ずタイムアウトを設定
- UnknownHostExceptionはリトライ不要
- 指数バックオフで待機時間を増やす

## データ送受信エラー

文字エンコーディングの誤りや、データの不完全な受信が起こります。

### エラー例

```java
// エラー例
out.write(data.getBytes()); // 文字コードを指定していない
// データが分割されて到着する可能性
```

Java 18以降は`getBytes()`の既定がUTF-8なので、Javaどうしの通信なら文字化けしにくくなりました。
しかし、通信相手がJava 17以前で動いている場合や、別の言語で書かれていて別の文字コードを使う場合は、文字化けします。
また、TCPは送ったまとまりの区切りを保たないので、1回の`read()`で全体が届くとは限りません。

### 対処法

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

### 重要なポイント

- 常にUTF-8を使用
- データサイズを事前に送信
- readFully()で完全受信保証

## HTTPクライアントエラー

HTTPステータスコードを確認していません。

### エラー例

```java
// エラー例
HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
String body = response.body(); // ステータスコードを確認しない
```

### 対処法

```java
int status = response.statusCode();
if (status >= 200 && status < 300) {
    return response.body();
} else if (status == 404) {
    throw new IOException("リソースが見つかりません");
} else if (status >= 500) {
    throw new IOException("サーバーエラー: " + status);
} else {
    throw new IOException("予期しないステータス: " + status);
}
```

古い`HttpURLConnection`を使う場合は、エラーのレスポンス（4xx・5xx）で`getInputStream()`が`IOException`をスローします。
先に`getResponseCode()`でステータスコードを確かめ、エラーのときのボディは`getErrorStream()`で読みます。

```java
HttpURLConnection conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
int status = conn.getResponseCode();
InputStream in = (status < 400) ? conn.getInputStream() : conn.getErrorStream();
```

### 重要なポイント

- 2xxは成功、4xxはクライアントエラー、5xxはサーバーエラー
- エラーレスポンスのボディも確認
- リトライ可能なエラーを識別

## リソース管理エラー

ソケットやストリームが適切に閉じられていません。

### エラー例

```java
// エラー例
ServerSocket server = new ServerSocket(8080);
while (true) {
    Socket client = server.accept();
    handleClient(client); // clientが閉じられない
}
```

### 対処法

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

### 重要なポイント

- try-with-resourcesで自動クローズ
- 複数のクライアントを同時に処理するときは、`accept()`で受け取ったソケットをスレッドプールのタスクに渡し、タスクのなかのtry-with-resourcesで閉じる（上のコードのように`accept()`したスレッドで閉じると、別スレッドが使う前に閉じてしまう）
- サーバーを止めるときに`ServerSocket`を閉じると、`accept()`で待っているスレッドに`SocketException`が発生して待ちが終わる

## 関連する共通エラー

以下のエラーも本章の内容に関連します。

- **IOExceptionの詳細**（→ 第13章）
  - ネットワークI/Oでの例外処理

## デバッグのヒント

1. 接続診断

   ```java
   InetAddress address = InetAddress.getByName(host);  // 名前解決の確認（失敗するとUnknownHostException）
   IO.println(address.getHostAddress());

   // ポートに接続できるかの確認
   try (Socket socket = new Socket()) {
       socket.connect(new InetSocketAddress(host, port), 5000);
       IO.println("接続できました");
   }
   ```

   `InetAddress.isReachable()`はpingの代わりにはなりません。ICMPを送る権限がないと、ポート7（echo）へのTCP接続で確かめるため、ホストが動いていても`false`になることがよくあります。

2. HTTPデバッグ

   `java.net.http.HttpClient`の送受信の内容は、JVMの起動オプションで標準エラー出力にログを出せます。

   ```bash
   # 要求と応答の行、ヘッダーを表示する
   java -Djdk.httpclient.HttpClient.log=requests,headers Main
   # HTTPSのTLSの通信を調べる
   java -Djavax.net.debug=ssl:handshake Main
   ```

3. ポートスキャン
   - telnetやncコマンドで接続テスト
   - Wiresharkでパケットキャプチャ

## さらに学ぶには

- 第14章: ファイルI/O（ストリームの扱いと文字コード）
- 第22章: HTTPクライアントライブラリの活用
