# 第17章 チャレンジ課題 - ネットワークプログラミング

## 課題概要
ネットワークプログラミングの高度な技術に挑戦するための最上級課題です。

## 課題

### HTTPサーバーの実装
`SimpleHttpServer.java`を実装し、基本的なHTTPサーバーを作成してください。

**要件:**
- HTTPリクエストの解析（GET, POST, HEAD）
- 静的ファイルの配信
- MIMEタイプの適切な設定
- Keep-Aliveサポート
- 404/500エラーページ
- アクセスログの記録
- マルチスレッドによる同時接続処理

**実装すべきクラス:**
```java
public class SimpleHttpServer {
    private final int port;
    private final String rootDirectory;
    private final ExecutorService threadPool;
    
    public SimpleHttpServer(int port, String rootDirectory) {
        this.port = port;
        this.rootDirectory = rootDirectory;
        this.threadPool = Executors.newCachedThreadPool();
    }
    
    public void start() throws IOException {
        // サーバーソケットの作成と接続待ち受け
    }
    
    private void handleClient(Socket clientSocket) {
        // HTTPリクエストの解析と応答
    }
    
    private HttpRequest parseRequest(BufferedReader reader) {
        // HTTPリクエストのパース
    }
    
    private void sendResponse(HttpResponse response, OutputStream out) {
        // HTTPレスポンスの送信
    }
}
```

**プロトコル実装の詳細:**

1. **HTTPリクエスト解析**
   - リクエストライン（メソッド、URI、HTTPバージョン）
   - ヘッダーの解析
   - Content-Lengthに基づくボディの読み取り

2. **HTTPレスポンス生成**
   - ステータスライン
   - 適切なヘッダー（Content-Type, Content-Length, Date等）
   - ファイル内容の送信

3. **エラーハンドリング**
   - 400 Bad Request（不正なリクエスト）
   - 404 Not Found（ファイルが見つからない）
   - 500 Internal Server Error（サーバーエラー）

4. **セキュリティ考慮事項**
   - ディレクトリトラバーサル攻撃の防止
   - 大きすぎるリクエストの拒否
   - タイムアウト処理

**実行例:**
```bash
# サーバー起動
javac SimpleHttpServer.java
java SimpleHttpServer 8080 ./public

# ブラウザでアクセス
http://localhost:8080/index.html

# コマンドラインでテスト
curl -v http://localhost:8080/
```

**サンプル出力:**
```
=== Simple HTTP Server ===
Starting server on port 8080
Document root: ./public
Thread pool size: 10

[2024-01-15 10:30:45] Server started successfully
[2024-01-15 10:31:02] 127.0.0.1 - GET /index.html HTTP/1.1 200 1234
[2024-01-15 10:31:03] 127.0.0.1 - GET /style.css HTTP/1.1 200 567
[2024-01-15 10:31:03] 127.0.0.1 - GET /script.js HTTP/1.1 200 890
[2024-01-15 10:31:05] 127.0.0.1 - GET /notfound.html HTTP/1.1 404 145
[2024-01-15 10:31:10] 127.0.0.1 - POST /api/data HTTP/1.1 405 0
```

## 評価基準

- HTTPプロトコルの正確な実装
- 並行処理の適切な実装
- エラーハンドリングの完全性
- セキュリティへの配慮
- コードの可読性と拡張性
- パフォーマンスの考慮

## 発展課題（オプション）

以下の機能を追加実装してみましょう：

1. **HTTPSサポート**
   - SSLServerSocketの使用
   - 自己署名証明書の生成

2. **動的コンテンツ**
   - 簡単なテンプレートエンジン
   - CGI風の動的処理

3. **圧縮転送**
   - gzip圧縮のサポート
   - Accept-Encodingヘッダーの処理

4. **キャッシュ機能**
   - ETagの生成
   - If-Modified-Sinceの処理

5. **WebSocketサポート**
   - HTTPからWebSocketへのアップグレード
   - 双方向通信の実装

## 学習のポイント

- HTTPプロトコルの深い理解
- ソケットプログラミングの実践
- マルチスレッドサーバーの設計
- プロトコル実装の基礎
- セキュリティの基本概念