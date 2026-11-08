# 第16章 ネットワークプログラミング：よくあるエラーと対処法

書籍『Java入門』第16章「ネットワークプログラミング」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

本章では、ネットワークプログラミングに特有のエラーを扱います。

## 本章特有のエラー

### 1. 基本的な接続エラー
問題: サーバーへの接続が失敗する

```java
// エラー例
Socket socket = new Socket("example.com", 80);
// ConnectException, UnknownHostException, SocketTimeoutException
```

解決策:

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

### 2. データ送受信エラー
問題: 文字エンコーディングやデータの不完全な受信

```java
// エラー例
out.write(data.getBytes()); // エンコーディング不明
// データが分割されて到着する可能性
```

解決策:

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

### 3. HTTPクライアントエラー
問題: HTTPステータスコードの未チェック

```java
// エラー例
HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
String body = response.body(); // ステータスコードを確認しない
```

解決策:

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

### 4. リソース管理エラー
問題: ソケットやストリームが適切に閉じられない

```java
// エラー例
ServerSocket server = new ServerSocket(8080);
while (true) {
    Socket client = server.accept();
    handleClient(client); // clientが閉じられない
}
```

解決策:

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

## 関連する共通エラー

以下のエラーも本章の内容に関連します。

- **IOExceptionの詳細**（→ 第13章）
  - ネットワークI/Oでの例外処理
- **タイムアウト設定**（→ 付録B.15）
  - 詳細なタイムアウト戦略
- **セキュリティ考慧**（→ 付録B.16）
  - SSL/TLSの詳細設定

## デバッグのヒント

1. 接続診断

   ```java
   InetAddress.getByName(host).isReachable(5000);
   // ping相当の確認
   ```

2. HTTPデバッグ

   ```java
   System.setProperty("java.net.debug", "all");
   // 詳細なネットワークログ
   ```

3. ポートスキャン
   - telnetやncコマンドで接続テスト
   - Wiresharkでパケットキャプチャ

## さらに学ぶには

- 付録H: Java共通エラーガイド（I/Oエラー）
- 付録B.15: ネットワークプログラミング詳細
- 第22章: HTTPクライアントライブラリの活用
