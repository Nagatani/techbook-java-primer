# 第17章 ネットワークプログラミング - 応用レベル課題

## 概要
応用レベル課題では、実用的なネットワークアプリケーションの開発に必要な高度な技術を習得します。マルチスレッドサーバー、非同期I/O、プロトコル設計など、プロダクションレベルの実装を学びます。

## 課題一覧

### 課題1: マルチクライアント対応チャットサーバー
**ChatServer.java & ChatClient.java**

複数のクライアントが同時に接続できるチャットサーバーを実装します。

**要求仕様：**
- ExecutorServiceを使った効率的なクライアント管理
- ブロードキャスト機能（全クライアントへのメッセージ配信）
- プライベートメッセージ機能
- ユーザー認証とセッション管理
- チャットルームの概念

**実装のポイント：**
```java
public class ChatServer {
    private final ExecutorService executor;
    private final Map<String, ClientHandler> clients;
    private final Map<String, ChatRoom> rooms;
    
    // メッセージプロトコル
    // LOGIN:username
    // MSG:message
    // PRIVMSG:target:message
    // JOIN:roomname
    // LEAVE:roomname
}
```

**追加機能：**
- オンラインユーザーリストの表示
- メッセージ履歴の保存
- ファイル送信機能
- 絵文字や特殊コマンドのサポート

### 課題2: HTTPサーバーの実装
**SimpleHttpServer.java**

基本的なHTTPサーバーを実装し、Webアプリケーションの基礎を理解します。

**要求仕様：**
- HTTP/1.1の基本的なメソッド（GET、POST、HEAD）のサポート
- 静的ファイルの配信
- MIMEタイプの適切な設定
- Keep-Aliveによる永続接続
- 基本的なルーティング機能

**実装のポイント：**
```java
public class SimpleHttpServer {
    private final Map<String, HttpHandler> routes;
    
    public void route(String path, HttpHandler handler) {
        routes.put(path, handler);
    }
    
    // リクエストの解析
    private HttpRequest parseRequest(BufferedReader in) {
        // メソッド、パス、ヘッダーの解析
    }
    
    // レスポンスの生成
    private void sendResponse(PrintWriter out, 
                            int statusCode, 
                            Map<String, String> headers, 
                            String body) {
        // HTTP形式でレスポンスを送信
    }
}
```

**実装要件：**
- エラーページの生成（404、500など）
- 条件付きGET（If-Modified-Since）
- 範囲リクエスト（Range）の基本サポート
- アクセスログの記録

### 課題3: 非同期I/Oによる高性能サーバー
**NioEchoServer.java**

Java NIOを使用して、高性能な非同期サーバーを実装します。

**要求仕様：**
- Selectorを使った非ブロッキングI/O
- 単一スレッドで複数クライアントの処理
- バッファ管理とメモリ効率
- 高負荷時の性能維持

**実装のポイント：**
```java
public class NioEchoServer {
    private Selector selector;
    private ServerSocketChannel serverChannel;
    
    public void start() throws IOException {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        
        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            // イベント処理
        }
    }
}
```

**パフォーマンス要件：**
- 1000以上の同時接続をサポート
- 低レイテンシ（1ms以下）
- 効率的なメモリ使用
- グレースフルシャットダウン

## 学習のポイント

1. **スケーラブルなサーバー設計**
   - スレッドプールのサイジング
   - 接続数の制限とバックプレッシャー
   - リソースリークの防止

2. **プロトコル設計**
   - テキストベース vs バイナリ
   - エラー処理とリカバリー
   - 拡張性の考慮

3. **セキュリティの基礎**
   - 入力検証とサニタイゼーション
   - DoS攻撃への対策
   - 基本的な認証メカニズム

4. **パフォーマンス最適化**
   - バッファリングとバッチ処理
   - 接続の再利用
   - 非同期処理のメリット

## 提出物

1. 各課題のソースコード（完全に動作するもの）
2. 設計ドキュメント（アーキテクチャ図を含む）
3. パフォーマンステストの結果
4. セキュリティ考慮事項のレポート

## 評価基準

- **機能の完成度（30%）**: 要求仕様を満たしているか
- **パフォーマンス（25%）**: 効率的な実装か
- **設計品質（25%）**: 拡張性、保守性
- **エラー処理（15%）**: 異常系の考慮
- **ドキュメント（5%）**: 明確な説明

## 発展課題

- WebSocketサーバーの実装
- SSL/TLSによる暗号化通信
- HTTP/2のサポート
- RESTful APIサーバーの構築

## 参考リソース

- [Java NIO Tutorial](https://docs.oracle.com/javase/8/docs/api/java/nio/package-summary.html)
- [HTTP Made Really Easy](https://www.jmarshall.com/easy/http/)
- [高性能サーバー設計パターン](https://www.nginx.com/blog/inside-nginx-how-we-designed-for-performance-scale/)