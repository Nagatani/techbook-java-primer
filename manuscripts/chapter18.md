# 第18章 ネットワークプログラミング

## 始めに：ネットワーク技術の進化と分散システムの基盤

前章までJavaの主要な技術について学習してきました。本章では、現代のソフトウェア開発において中核的な役割を果たす「ネットワークプログラミング」について詳細に学習します。

ネットワークプログラミングは単なる通信技術ではありません。インターネット時代の到来とともに、コンピュータシステムを根本的に変革し、グローバルな情報社会の基盤となった、現代文明を支える重要な技術です。

### コンピュータネットワークの歴史的発展

コンピュータネットワーク技術の発展は、現代の情報社会の基盤を形成した技術革命の歴史でもあります。この歴史を理解することは、現代のネットワークプログラミングの意義を深く理解するために重要です。

**孤立システム時代（1940年代〜1960年代）**：初期のコンピュータは完全に独立したシステムで、データの交換は物理媒体（パンチカード、磁気テープ）による手動転送のみでした。計算結果の共有には物理的な移動が必要でした。

**専用回線による接続（1960年代〜1970年代）**：大型機どうしを専用回線で接続する試みが始まりました。IBMのSNA（Systems Network Architecture）などの企業独自のネットワークアーキテクチャが開発されました。

**ARPANET の誕生（1969年）**：アメリヵ国防総省のARPA（Advanced Research Projects Agency）により、世界初のパケット交換ネットワークが構築されました。これが現在のインターネットの直接的な前身となりました。

**TCP/IP プロトコルの確立（1970年代〜1980年代）**：ヴィント・サーフ（Vint Cerf）とロバート・カーン（Robert Kahn）により、異なるネットワーク間の相互接続を可能にするTCP/IPプロトコルが開発されました。

**インターネットの商用化（1990年代）**：NSFNETからの移行により、インターネットが学術用途から商用利用へと拡大し、World Wide Webの普及とともに爆発的に成長しました。

### ネットワークアーキテクチャの進化

ネットワークシステムの設計思想は、技術の発展とともに段階的に進化してきました：

**集中型アーキテクチャ（1960年代〜1970年代）**：メインフレームを中心とした星型ネットワークにより、端末からの処理要求を集中的に処理する構造でした。

**クライアント／サーバモデル（1980年代〜1990年代）**：パーソナルコンピュータの普及により、処理能力を持つクライアントと専用サーバが協働する分散処理モデルが確立されました。

**ピアツーピア（P2P）ネットワーク（1990年代後期〜）**：Napster、BitTorrentなどにより、中央サーバを介さない分散型ネットワークモデルが実用化されました。

**サービス指向アーキテクチャ（SOA）（2000年代）**：Webサービス技術により、ネットワーク上の機能をサービスとして組み合わせる柔軟なシステム構築が可能になりました。

**マイクロサービスアーキテクチャ（2010年代〜）**：大規模システムを小さな独立したサービスに分割し、ネットワーク通信により連携させる現代的なアーキテクチャが確立されました。

### プログラミング言語におけるネットワーク支援の発展

プログラミング言語のネットワーク機能サポートは、抽象化レベルを段階的に向上させてきました：

**低レベルソケットプログラミング（1980年代〜）**：Berkeley socketsに代表される、OSレベルのネットワーク機能を直接使用する手法が確立されました。

```c
#include <sys/socket.h>
#include <netinet/in.h>

int sockfd = socket(AF_INET, SOCK_STREAM, 0);
struct sockaddr_in server_addr;
server_addr.sin_family = AF_INET;
server_addr.sin_port = htons(8080);
```

**オブジェクト指向ネットワーキング（1990年代〜）**：Java、C#などの言語により、ソケット操作がオブジェクト指向的に抽象化され、より安全で使いやすいAPIが提供されました。

**高レベルプロトコル支援（2000年代〜）**：HTTP、SOAP、RESTなどの高レベルプロトコルを直接サポートするライブラリが充実し、Webアプリケーション開発が大幅に簡素化されました。

**非同期・リアクティブプログラミング（2010年代〜）**：Node.js、Netty、Vert.xなどにより、高並行性ネットワークアプリケーションの開発が革新されました。

### Javaにおけるネットワークプログラミングの発展

Javaは、「Write Once, Run Anywhere」の思想の下で、プラットフォーム独立なネットワークプログラミング環境を提供してきました：

**Java 1.0（1995年）- 基本ソケット API**：java.netパッケージにより、TCP/UDPソケットの基本機能が提供されました。アプレットによるブラウザでのネットワークアプリケーション実行も実現されました。

**Java 1.1（1997年）- RMI（Remote Method Invocation）**：分散オブジェクト技術により、ネットワーク越しのメソッド呼び出しが透明に実行できるようになりました。

**Java 2 EE（1999年）- 企業向けネットワーク技術**：サーブレット、JSP、EJBなどにより、企業レベルのWebアプリケーション開発が標準化されました。

**Java 1.4（2002年）- NIO（New I/O）**：非ブロッキングI/Oとチャネル・セレクタAPIにより、高性能なネットワークアプリケーションの開発が可能になりました。

**Java 7（2011年）- NIO.2**：非同期I/O操作の改善により、さらに効率的なネットワーク処理が実現されました。

**Java 8以降 - 関数型・リアクティブ対応**：CompleテーブルFuture、Reactive Streamsなどにより、現代的な非同期ネットワークプログラミングが支援されています。

### ネットワークプロトコルスタックの理解

現代のネットワークプログラミングを理解するためには、TCP/IPプロトコルスタックの階層構造を理解することが重要です：

**物理層・データリンク層**：ハードウェアレベルでのデータ伝送を担当します。Ethernet、Wi-Fi、光ファイバーなどの物理媒体上でのビット伝送を制御します。

**ネットワーク層（IP）**：パケットの経路選択と配送を担当します。IPv4/IPv6により、グローバルなアドレス空間でのパケット転送が実現されています。

**トランスポート層（TCP/UDP）**：アプリケーション間の信頼性のある（TCP）または高速な（UDP）データ転送を提供します。ポート番号により、同一マシン上の複数アプリケーションを識別します。

**アプリケーション層**：HTTP、SMTP、FTP、SSHなどの高レベルプロトコルにより、具体的なアプリケーション機能を実現します。

### 現代のネットワークアプリケーションの特徴

現代のネットワークアプリケーションは、従来とは大きく異なる要件と特徴を持っています：

**大規模並行処理**：数万から数十万の同時接続を処理する必要があり、従来のスレッドプールモデルでは限界があります。非同期I/Oとイベント駆動アーキテクチャが重要になっています。

**リアルタイム性**：WebSocket、Server-Sent Events、WebRTCなどにより、リアルタイム双方向通信が標準的な機能となっています。

**セキュリティ強化**：HTTPS、WSS（WebSocket Secure）、OAuth、JWTなど、セキュリティが設計段階から組み込まれています。

**マイクロサービス対応**：サービス間通信、負荷分散、障害回復、監視・ログ記録など、分散システム特有の課題への対応が必要です。

**クラウドネイティブ**：Kubernetes、Docker、サービスメッシュなど、クラウド環境での運用を前提とした設計が求められています。

### ネットワークプログラミングの課題と対策

ネットワークプログラミングには、従来のローカルプログラミングでは遭遇しない特有の課題があります：

**ネットワーク障害への対応**：接続の切断、タイムアウト、パケット損失など、予期しない障害に対する適切な処理が必要です。

**性能とスケーラビリティ**：増加するトラフィックに対して、性能を維持しながらスケールする設計が重要です。

**セキュリティ脅威**：DDoS攻撃、中間者攻撃、SQLインジェクションなど、多様なセキュリティ脅威への対策が必要です。

**分散システムの複雑性**：CAP定理、結果整合性、分散トランザクションなど、分散システム特有の理論と実装技術の理解が必要です。

### 本章で学習する内容の意義

本章では、これらの歴史的背景と現代的な課題を踏まえて、Javaにおけるネットワークプログラミングを体系的に学習していきます。単にソケットの使い方を覚えるのではなく、以下の点を重視して学習を進めます：

**ネットワークアーキテクチャの理解**：クライアント／サーバモデルの設計原則を理解し、適切な役割分担ができる能力を身につけます。

**プロトコル設計の技術**：TCP/UDPの特性を理解し、用途に応じた適切なプロトコル選択ができます。

**並行性とパフォーマンス**：多数のクライアント接続を効率的に処理するための技術を習得します。

**エラーハンドリング**：ネットワーク特有の障害に対する適切な処理技術を身につけます。

**セキュリティ意識**：ネットワークアプリケーションにおけるセキュリティリスクを理解し、基本的な対策を実装できます。

**現代技術への橋渡し**：WebSocket、REST API、マイクロサービスなど、現代的なネットワーク技術への基礎知識を身につけます。

ネットワークプログラミングを深く理解することは、現代の分散システム開発において不可欠な能力を身につけることにつながります。本章を通じて、単なる「データの送受信技術」を超えて、「分散システムの設計と実装の基礎」を習得していきましょう。

本章では、Javaを用いたTCP/IPネットワークアプリケーションの基礎から実践的な応用まで学習します。ソケット通信を理解することで、チャットアプリケーションやWebサーバなど、さまざまなネットワークアプリケーションを開発できます。

## 15.1 ネットワークプログラミングの基礎

### クライアント／サーバモデル

多くのネットワークアプリケーションは、サービスを要求する「クライアント」と、サービスを提供する「サーバ」から構成されるクライアント／サーバモデルを採用しています。

* **サーバ**: 特定のポートで待機し、クライアントからの接続要求を待ち受けます
* **クライアント**: サーバのIPアドレスとポート番号を指定して接続を要求し、サービスの提供を受けます

身近な例では、ブラウザ（クライアント）がWebサーバ（サーバ）にWebページの表示を要求するのも、このモデルにもとづいています。

### TCP/IPとソケット

ソケットとは、プログラムがネットワークを通じて通信を行うための抽象的な出入り口です。TCP/IPプロトコル群においては、通信相手を特定するために「IPアドレス」と「ポート番号」の組み合わせを使用します。

* **IPアドレス**: ネットワーク上のコンピュータを一意に特定するための住所のようなものです
* **ポート番号**: コンピュータ内で動作している特定のアプリケーション（プロセス）を識別するための番号です

Javaでは、`java.net`パッケージがソケット通信に必要なクラス群を提供しており、これによりOSI参照モデルにおけるセッション層以上のプロトコルを比較的容易に実装できます。

### JavaにおけるソケットAPI

JavaでTCPソケット通信を実装する際には、主に以下の2つのクラスを使用します。

* `java.net.ServerSocket`: サーバ側でクライアントからの接続を待ち受けるために使用します。特定のポートにバインドし、接続要求を受け入れると、通信用の`Socket`オブジェクトを生成します
* `java.net.Socket`: クライアントとサーバ間の実際の通信経路上に作成されるエンドポイントです。クライアント側ではサーバに接続するため、サーバ側では`ServerSocket`が接続を受け入れた結果として生成されます

## 15.2 基本的なソケット通信の実装

まず、基本的な「オウム返し（Echo）」サーバと、それに接続するクライアントを作成します。クライアントから送信されたメッセージを、サーバがそのまま送り返すだけの簡単なプログラムです。

### オウム返しサーバ（シングルスレッド）

このサーバは、一度に1つのクライアントしか処理できない単純な構造です。

```java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class EchoServer {

    public static void main(String[] args) {
        // サーバーが待ち受けるTCPポート番号
        int port = 12345;
        System.out.println("Echo Server is starting on port " + port);

        // try-with-resources文でServerSocketを定義し、自動的にクローズされるようにする
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Waiting for a client connection...");

            // accept()メソッドはクライアントからの接続があるまで処理をブロック（待機）する
            // 接続が確立されると、通信用のSocketオブジェクトを返す
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());

            // try-with-resources文で、通信用のストリームも自動クローズの対象にする
            try (
                // Socketから入力ストリームを取得し、文字として読み取るためのReaderを作成
                // UTF-8エンコーディングを指定して文字化けを防ぐ
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8)
                );
                // Socketから出力ストリームを取得し、文字を書き込むためのWriterを作成
                // autoFlushをtrueに設定し、println()が呼ばれるたびに自動でフラッシュ（送信）する
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true, StandardCharsets.UTF_8)
            ) {
                String line;
                // reader.readLine()はクライアントから1行分のデータが送られてくるまでブロックする
                // クライアントが接続を切断するとnullを返すため、ループが終了する
                while ((line = reader.readLine()) != null) {
                    System.out.println("Received from client: " + line);

                    // 受け取った文字列をそのままクライアントに送信する
                    writer.println(line);

                    // "exit"というメッセージを受け取ったら、能動的にループを終了する
                    if ("exit".equalsIgnoreCase(line)) {
                        break;
                    }
                }
            }
            System.out.println("Client disconnected.");

        } catch (IOException e) {
            // サーバーソケットの作成や接続待機中にエラーが発生した場合
            System.err.println("An error occurred in the server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

#### 実用的なネットワークアプリケーション - マルチプレイヤーゲームサーバ

以下は、ネットワークプログラミングの概念を包括的に示す、リアルタイムマルチプレイヤーゲームサーバの実装例です。このシステムは現代的なネットワークアプリケーション開発の基盤技術を示しています：

```java
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * マルチプレイヤーゲームサーバー - 高度なネットワークプログラミングの実践例
 * 
 * このシステムは以下の先進的な機能を実装しています：
 * - WebSocketスタイルのリアルタイム通信
 * - JSON形式のメッセージプロトコル
 * - 接続管理とセッション追跡
 * - ゲーム状態の同期とブロードキャスト
 * - エラーハンドリングと接続復旧
 */
public class MultiplayerGameServer {
    
    /**
     * ゲームメッセージの基底クラス
     */
    public static abstract class GameMessage {
        @JsonProperty("type")
        public abstract String getType();
        
        @JsonProperty("timestamp")
        public String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    /**
     * プレイヤー参加メッセージ
     */
    public static class PlayerJoinMessage extends GameMessage {
        @JsonProperty("playerId")
        public String playerId;
        
        @JsonProperty("playerName")
        public String playerName;
        
        @Override
        public String getType() { return "player_join"; }
    }
    
    /**
     * プレイヤー移動メッセージ
     */
    public static class PlayerMoveMessage extends GameMessage {
        @JsonProperty("playerId")
        public String playerId;
        
        @JsonProperty("x")
        public double x;
        
        @JsonProperty("y")
        public double y;
        
        @Override
        public String getType() { return "player_move"; }
    }
    
    /**
     * チャットメッセージ
     */
    public static class ChatMessage extends GameMessage {
        @JsonProperty("playerId")
        public String playerId;
        
        @JsonProperty("message")
        public String message;
        
        @Override
        public String getType() { return "chat"; }
    }
    
    /**
     * ゲーム状態更新メッセージ
     */
    public static class GameStateMessage extends GameMessage {
        @JsonProperty("players")
        public Map<String, PlayerState> players;
        
        @Override
        public String getType() { return "game_state"; }
    }
    
    /**
     * プレイヤーの状態
     */
    public static class PlayerState {
        @JsonProperty("id")
        public String id;
        
        @JsonProperty("name")
        public String name;
        
        @JsonProperty("x")
        public double x;
        
        @JsonProperty("y")
        public double y;
        
        @JsonProperty("online")
        public boolean online;
        
        @JsonProperty("lastActivity")
        public String lastActivity;
        
        public PlayerState(String id, String name) {
            this.id = id;
            this.name = name;
            this.x = Math.random() * 1000; // ランダムな初期位置
            this.y = Math.random() * 1000;
            this.online = true;
            this.lastActivity = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }
    
    /**
     * 接続されたクライアントの管理
     */
    public static class ClientConnection {
        private final Socket socket;
        private final BufferedReader reader;
        private final PrintWriter writer;
        private final String clientId;
        private volatile boolean active;
        private PlayerState playerState;
        
        public ClientConnection(Socket socket) throws IOException {
            this.socket = socket;
            this.reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            this.writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
            this.clientId = "client_" + System.currentTimeMillis() + "_" + 
                          (int)(Math.random() * 1000);
            this.active = true;
        }
        
        public void sendMessage(String message) {
            if (active && !socket.isClosed()) {
                writer.println(message);
            }
        }
        
        public String readMessage() throws IOException {
            return reader.readLine();
        }
        
        public void close() {
            active = false;
            try {
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing client connection: " + e.getMessage());
            }
        }
        
        // Getters
        public String getClientId() { return clientId; }
        public boolean isActive() { return active; }
        public PlayerState getPlayerState() { return playerState; }
        public void setPlayerState(PlayerState state) { this.playerState = state; }
        public Socket getSocket() { return socket; }
    }
    
    /**
     * ゲームサーバーのメインクラス
     */
    public static class GameServer {
        private final int port;
        private final ExecutorService executorService;
        private final Map<String, ClientConnection> clients;
        private final Map<String, PlayerState> gameState;
        private final ObjectMapper objectMapper;
        private final AtomicInteger connectionCounter;
        private volatile boolean running;
        
        public GameServer(int port) {
            this.port = port;
            this.executorService = Executors.newCachedThreadPool();
            this.clients = new ConcurrentHashMap<>();
            this.gameState = new ConcurrentHashMap<>();
            this.objectMapper = new ObjectMapper();
            this.connectionCounter = new AtomicInteger(0);
            this.running = false;
        }
        
        /**
         * サーバーの開始
         */
        public void start() {
            running = true;
            System.out.println("=== マルチプレイヤーゲームサーバー起動 ===");
            System.out.println("ポート: " + port);
            
            // ゲーム状態の定期更新タスク
            executorService.submit(this::gameStateUpdateLoop);
            
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("クライアントの接続を待機中...");
                
                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        int connectionId = connectionCounter.incrementAndGet();
                        
                        System.out.printf("新しい接続 #%d: %s%n", 
                            connectionId, clientSocket.getRemoteSocketAddress());
                        
                        // 各クライアント用のハンドラーを非同期で実行
                        executorService.submit(() -> handleClient(clientSocket));
                        
                    } catch (IOException e) {
                        if (running) {
                            System.err.println("クライアント接続エラー: " + e.getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("サーバーソケットエラー: " + e.getMessage());
            }
        }
        
        /**
         * 個別クライアントの処理
         */
        private void handleClient(Socket clientSocket) {
            ClientConnection connection = null;
            
            try {
                connection = new ClientConnection(clientSocket);
                clients.put(connection.getClientId(), connection);
                
                System.out.printf("クライアント登録: %s (%s)%n",
                    connection.getClientId(), 
                    clientSocket.getRemoteSocketAddress());
                
                // ウェルカムメッセージ
                connection.sendMessage(createWelcomeMessage(connection.getClientId()));
                
                // メッセージ処理ループ
                String rawMessage;
                while (connection.isActive() && (rawMessage = connection.readMessage()) != null) {
                    try {
                        processMessage(connection, rawMessage);
                    } catch (Exception e) {
                        System.err.println("メッセージ処理エラー: " + e.getMessage());
                        // エラーメッセージをクライアントに送信
                        connection.sendMessage(createErrorMessage(e.getMessage()));
                    }
                }
                
            } catch (IOException e) {
                System.err.println("クライアント通信エラー: " + e.getMessage());
            } finally {
                // クリーンアップ処理
                if (connection != null) {
                    cleanupClient(connection);
                }
            }
        }
        
        /**
         * メッセージ処理の分岐
         */
        private void processMessage(ClientConnection connection, String rawMessage) throws Exception {
            // JSON解析の試行
            Map<String, Object> messageData = objectMapper.readValue(rawMessage, Map.class);
            String messageType = (String) messageData.get("type");
            
            switch (messageType) {
                case "join" -> handleJoinMessage(connection, messageData);
                case "move" -> handleMoveMessage(connection, messageData);
                case "chat" -> handleChatMessage(connection, messageData);
                case "ping" -> handlePingMessage(connection);
                default -> System.err.println("不明なメッセージタイプ: " + messageType);
            }
        }
        
        /**
         * プレイヤー参加処理
         */
        private void handleJoinMessage(ClientConnection connection, Map<String, Object> data) throws Exception {
            String playerName = (String) data.get("playerName");
            
            PlayerState playerState = new PlayerState(connection.getClientId(), playerName);
            connection.setPlayerState(playerState);
            gameState.put(connection.getClientId(), playerState);
            
            System.out.printf("プレイヤー参加: %s (ID: %s)%n", 
                playerName, connection.getClientId());
            
            // 参加通知をすべてのクライアントにブロードキャスト
            PlayerJoinMessage joinMessage = new PlayerJoinMessage();
            joinMessage.playerId = connection.getClientId();
            joinMessage.playerName = playerName;
            
            broadcastMessage(objectMapper.writeValueAsString(joinMessage));
        }
        
        /**
         * プレイヤー移動処理
         */
        private void handleMoveMessage(ClientConnection connection, Map<String, Object> data) throws Exception {
            PlayerState playerState = connection.getPlayerState();
            if (playerState == null) return;
            
            double x = ((Number) data.get("x")).doubleValue();
            double y = ((Number) data.get("y")).doubleValue();
            
            // 移動範囲の制限（0-1000の範囲内）
            x = Math.max(0, Math.min(1000, x));
            y = Math.max(0, Math.min(1000, y));
            
            playerState.x = x;
            playerState.y = y;
            playerState.lastActivity = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            // 移動メッセージをブロードキャスト
            PlayerMoveMessage moveMessage = new PlayerMoveMessage();
            moveMessage.playerId = connection.getClientId();
            moveMessage.x = x;
            moveMessage.y = y;
            
            broadcastMessage(objectMapper.writeValueAsString(moveMessage));
        }
        
        /**
         * チャットメッセージ処理
         */
        private void handleChatMessage(ClientConnection connection, Map<String, Object> data) throws Exception {
            PlayerState playerState = connection.getPlayerState();
            if (playerState == null) return;
            
            String message = (String) data.get("message");
            
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.playerId = connection.getClientId();
            chatMessage.message = message;
            
            System.out.printf("チャット [%s]: %s%n", playerState.name, message);
            
            broadcastMessage(objectMapper.writeValueAsString(chatMessage));
        }
        
        /**
         * 生存確認処理
         */
        private void handlePingMessage(ClientConnection connection) throws Exception {
            PlayerState playerState = connection.getPlayerState();
            if (playerState != null) {
                playerState.lastActivity = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            
            // Pongレスポンス
            Map<String, Object> pongMessage = Map.of(
                "type", "pong",
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
            connection.sendMessage(objectMapper.writeValueAsString(pongMessage));
        }
        
        /**
         * すべてのクライアントにメッセージをブロードキャスト
         */
        private void broadcastMessage(String message) {
            clients.values().parallelStream()
                .filter(ClientConnection::isActive)
                .forEach(client -> client.sendMessage(message));
        }
        
        /**
         * ゲーム状態の定期更新
         */
        private void gameStateUpdateLoop() {
            while (running) {
                try {
                    Thread.sleep(5000); // 5秒間隔
                    
                    // アクティブなプレイヤーの状態更新
                    GameStateMessage stateMessage = new GameStateMessage();
                    stateMessage.players = new HashMap<>(gameState);
                    
                    String stateJson = objectMapper.writeValueAsString(stateMessage);
                    broadcastMessage(stateJson);
                    
                    System.out.printf("ゲーム状態更新送信 - アクティブプレイヤー: %d人%n", 
                        stateMessage.players.size());
                        
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("ゲーム状態更新エラー: " + e.getMessage());
                }
            }
        }
        
        /**
         * クライアントのクリーンアップ
         */
        private void cleanupClient(ClientConnection connection) {
            connection.close();
            clients.remove(connection.getClientId());
            
            PlayerState playerState = connection.getPlayerState();
            if (playerState != null) {
                gameState.remove(connection.getClientId());
                System.out.printf("プレイヤー退出: %s (ID: %s)%n", 
                    playerState.name, connection.getClientId());
            }
            
            System.out.printf("クライアント切断: %s%n", connection.getClientId());
        }
        
        /**
         * ウェルカムメッセージの作成
         */
        private String createWelcomeMessage(String clientId) throws Exception {
            Map<String, Object> welcomeMessage = Map.of(
                "type", "welcome",
                "clientId", clientId,
                "serverInfo", Map.of(
                    "name", "マルチプレイヤーゲームサーバー",
                    "version", "1.0",
                    "maxPlayers", 100
                ),
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
            return objectMapper.writeValueAsString(welcomeMessage);
        }
        
        /**
         * エラーメッセージの作成
         */
        private String createErrorMessage(String error) {
            try {
                Map<String, Object> errorMessage = Map.of(
                    "type", "error",
                    "message", error,
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                );
                return objectMapper.writeValueAsString(errorMessage);
            } catch (Exception e) {
                return "{\"type\":\"error\",\"message\":\"Unknown error occurred\"}";
            }
        }
        
        /**
         * サーバーの停止
         */
        public void stop() {
            running = false;
            
            // 全クライアントに切断通知
            clients.values().forEach(ClientConnection::close);
            
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
            
            System.out.println("サーバーが停止しました。");
        }
    }
    
    /**
     * テスト用シンプルクライアント
     */
    public static class GameClient {
        private Socket socket;
        private PrintWriter writer;
        private BufferedReader reader;
        private final ObjectMapper objectMapper = new ObjectMapper();
        
        public void connect(String host, int port) throws IOException {
            socket = new Socket(host, port);
            writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
            reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            
            System.out.println("サーバーに接続しました: " + host + ":" + port);
        }
        
        public void joinGame(String playerName) throws Exception {
            Map<String, Object> joinMessage = Map.of(
                "type", "join",
                "playerName", playerName
            );
            sendMessage(joinMessage);
        }
        
        public void movePlayer(double x, double y) throws Exception {
            Map<String, Object> moveMessage = Map.of(
                "type", "move",
                "x", x,
                "y", y
            );
            sendMessage(moveMessage);
        }
        
        public void sendChat(String message) throws Exception {
            Map<String, Object> chatMessage = Map.of(
                "type", "chat",
                "message", message
            );
            sendMessage(chatMessage);
        }
        
        private void sendMessage(Map<String, Object> message) throws Exception {
            String json = objectMapper.writeValueAsString(message);
            writer.println(json);
        }
        
        public void startListening() {
            new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        System.out.println("サーバーからのメッセージ: " + message);
                    }
                } catch (IOException e) {
                    System.err.println("サーバーからの読み込みエラー: " + e.getMessage());
                }
            }).start();
        }
        
        public void disconnect() throws IOException {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
    
    /**
     * デモンストレーション
     */
    public static void main(String[] args) {
        GameServer server = new GameServer(8888);
        
        // サーバーを別スレッドで起動
        Thread serverThread = new Thread(server::start);
        serverThread.start();
        
        // 少し待ってからテストクライアントを接続
        try {
            Thread.sleep(1000);
            
            // テストクライアント1
            GameClient client1 = new GameClient();
            client1.connect("localhost", 8888);
            client1.startListening();
            client1.joinGame("プレイヤー1");
            
            Thread.sleep(1000);
            
            // テストクライアント2
            GameClient client2 = new GameClient();
            client2.connect("localhost", 8888);
            client2.startListening();
            client2.joinGame("プレイヤー2");
            
            // テストメッセージの送信
            Thread.sleep(1000);
            client1.movePlayer(100, 200);
            client1.sendChat("こんにちは！");
            
            Thread.sleep(1000);
            client2.movePlayer(300, 400);
            client2.sendChat("よろしくお願いします！");
            
            // しばらく動作させてから終了
            Thread.sleep(10000);
            
            client1.disconnect();
            client2.disconnect();
            server.stop();
            
        } catch (Exception e) {
            System.err.println("デモ実行エラー: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

**このマルチプレイヤーゲームサーバの特徴と学習ポイント:**

1. **現代的なメッセージング**: JSON形式でのプロトコル設計により、Web技術との親和性が高い通信を実現しています。

2. **リアルタイム通信**: WebSocketスタイルの双方向通信により、即座の状態同期が可能です。

3. **接続管理**: クライアントセッションの追跡、自動クリーンアップ、エラー回復機能を実装しています。

4. **スケーラブル設計**: ConcurrentHashMapと非同期処理により、多数の同時接続に対応できます。

5. **ゲーム状態同期**: 定期的なブロードキャストによる状態同期パターンを示しています。

6. **エラーハンドリング**: ネットワーク障害、不正メッセージ、リソースリークに対する適切な処理を実装しています。

7. **実用的なアーキテクチャ**: 実際のオンラインゲームやリアルタイムアプリケーションで使用される設計パターンを採用しています。

### クライアントの実装

次に、作成したサーバに接続し、メッセージを送信するためのクライアントプログラムを作成します。

```java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class EchoClient {

    public static void main(String[] args) {
        // 接続先サーバーのホスト名とポート番号
        String hostname = "localhost"; // 自分自身を指す
        int port = 12345;

        // try-with-resources文でSocketとストリーム、Scannerを自動クローズする
        try (
            // サーバーに接続するためのSocketを生成する
            Socket socket = new Socket(hostname, port);
            // サーバーへの送信用Writer
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
            // サーバーからの受信用Reader
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            );
            // 標準入力（キーボード）からユーザーの入力を受け取るためのScanner
            Scanner consoleScanner = new Scanner(System.in)
        ) {
            System.out.println("Connected to the echo server. Type 'exit' to quit.");
            
            String userInput;
            // ユーザーがコンソールで入力した文字列を1行ずつ読み込む
            while ((userInput = consoleScanner.nextLine()) != null) {
                // 入力された文字列をサーバーに送信する
                writer.println(userInput);
                
                // サーバーからの応答を受信する
                String serverResponse = reader.readLine();
                System.out.println("Echo from server: " + serverResponse);

                // ユーザーが"exit"を入力した場合、ループを抜けてプログラムを終了する
                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }
            }

        } catch (UnknownHostException e) {
            // 指定されたホストが見つからない場合
            System.err.println("Host unknown: " + hostname);
            e.printStackTrace();
        } catch (IOException e) {
            // サーバーへの接続失敗や、通信中にエラーが発生した場合
            System.err.println("Couldn't get I/O for the connection to " + hostname);
            System.err.println("Please make sure the server is running.");
            e.printStackTrace();
        }
    }
}
```

### 実行と動作確認

1. まず、`EchoServer.java`をコンパイルして実行します。サーバがクライアントの接続待機状態になります
2. 次に、別のターミナル（コンソール）で`EchoClient.java`をコンパイルして実行します
3. クライアントのコンソールで何か文字を入力してEnterキーを押すと、サーバがそれを受け取り、クライアントに応答を返します

**クライアント側の実行例:**

```
Connected to the echo server. Type 'exit' to quit.
hello
Echo from server: hello
Java Socket Programming
Echo from server: Java Socket Programming
exit
Echo from server: exit
```

## 15.3 複数クライアントへの対応（マルチスレッド化）

先の`EchoServer`は、1つのクライアントとの通信が終わるまで、次のクライアントの接続を受け付けられません。これは`accept()`メソッドや`readLine()`メソッドが処理をブロックするためです。

実用的なサーバは複数のクライアントと同時に通信できる必要があります。これを実現するために、クライアントごとに新しいスレッドを生成して通信処理を任せる「マルチスレッド」モデルを導入します。

### スレッドによる並行処理の実装

クライアントからの接続を受け付けるたびに、そのクライアントとの通信を専門に担当する新しいスレッドを作成し、処理を委譲します。これにより、メインスレッドはすぐに次のクライアントの接続待機（`accept()`）に戻ることができます。

#### ClientHandler.java（クライアント処理クラス）

通信ロジックを`Runnable`インターフェイスを実装したクラスに分離します。これにより、コードの責務が明確になります。

```java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

// Runnableインターフェイスを実装し、各クライアントの通信処理を担当するクラス
public class ClientHandler implements Runnable {

    private final Socket clientSocket;

    // コンストラクタで、通信対象のSocketオブジェクトを受け取る
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        // try-with-resources文で、このスレッドが使用するリソースを管理する
        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8)
            );
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true, StandardCharsets.UTF_8)
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.printf("Thread %d: Received from %s: %s\n",
                    Thread.currentThread().getId(),
                    clientSocket.getRemoteSocketAddress(),
                    line
                );
                writer.println(line); // オウム返し
                
                if ("exit".equalsIgnoreCase(line)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("I/O error with client " + clientSocket.getRemoteSocketAddress() + ": " + e.getMessage());
        } finally {
            // try-with-resourcesでsocketは自動クローズされないため、明示的にクローズする
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.printf("Thread %d: Connection with %s closed.\n",
                Thread.currentThread().getId(),
                clientSocket.getRemoteSocketAddress()
            );
        }
    }
}
```

#### MultiThreadEchoServer.java

メインのサーバクラスは、接続を待ち受け、`ClientHandler`のインスタンスを生成して新しいスレッドで実行することに専念します。

```java
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadEchoServer {

    public static void main(String[] args) {
        int port = 12345;
        System.out.println("Multi-threaded Echo Server is starting on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // サーバーが停止するまで無限にクライアントの接続を待ち続ける
            while (true) {
                // 新しいクライアントからの接続を待つ
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getRemoteSocketAddress());
                
                // 新しいクライアントのためのハンドラーを作成
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                
                // 新しいスレッドを作成し、ハンドラーのrun()メソッドを実行させる
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            e.printStackTrace();
        }
    }
}
```

このサーバを実行し、複数の`EchoClient`から同時に接続してみてください。それぞれのクライアントが独立してサーバと通信できることが確認できます。

## 15.4 スレッドプールの活用（ExecutorService）

クライアントごとにスレッドを生成するモデルは単純で分かりやすいですが、クライアント数が非常に多くなると、スレッドの生成・破棄のコストや、各スレッドで消費するメモリが問題になる可能性があります。

このような問題を解決するため、現代的なJavaプログラミングでは`ExecutorService`（スレッドプール）を使用するのが一般的です。スレッドプールは、あらかじめ一定数のスレッドを作成しておき、タスク（この場合はクライアント処理）が発生するたびに待機中のスレッドに割り当てるしくみです。

### ThreadPoolEchoServer.java

```java
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolEchoServer {

    public static void main(String[] args) {
        int port = 12345;
        // 固定数のスレッドを持つスレッドプールを作成（ここでは10スレッド）
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        
        System.out.println("Thread Pool Echo Server is starting on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // ClientHandlerタスクを生成し、スレッドプールに投入する
                // プール内の待機スレッドがこのタスクを実行する
                executorService.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            // サーバーがシャットダウンする際にプールも閉じる
            executorService.shutdown();
        }
    }
}
```

この実装では、メインスレッドは`new Thread().start()`を呼びだす代わりに、`ExecutorService`にタスクを`submit()`するだけです。スレッドのライフサイクル管理は`ExecutorService`に任せることができ、より安定したサーバを構築できます。

## 15.5 簡単なHTTPサーバの実装

私たちが日常的に利用しているWebは、HTTP (HyperText Transfer Protocol) というプロトコルにもとづいています。HTTPはテキストベースのプロトコルであり、その基本的な動作はソケット通信で実装できます。

### HTTPプロトコルの基本

#### HTTP Request

ブラウザ（クライアント）は、サーバに以下のようなテキストデータを送信します。

```http
GET /hello HTTP/1.1
Host: localhost:12345
Accept: */*
(空行)
```

#### HTTP Response

サーバは、リクエストに応じて以下のようなテキストデータを返します。

```http
HTTP/1.1 200 OK
Content-Type: text/html; charset=utf-8
Content-Length: 123

<html>
<body>
<h1>Hello, World!</h1>
</body>
</html>
```

### 簡易HTTPサーバの実装

```java
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

public class SimpleHTTPServer {
    
    public static void main(String[] args) {
        int port = 8080;
        System.out.println("Simple HTTP Server starting on port " + port);
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new HTTPRequestHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}

class HTTPRequestHandler implements Runnable {
    private final Socket clientSocket;
    
    public HTTPRequestHandler(Socket socket) {
        this.clientSocket = socket;
    }
    
    @Override
    public void run() {
        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8)
            );
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true, StandardCharsets.UTF_8)
        ) {
            // HTTPリクエストの最初の行を読み取る
            String requestLine = reader.readLine();
            if (requestLine == null) return;
            
            System.out.println("Request: " + requestLine);
            
            // GET /path HTTP/1.1 の形式をパース
            StringTokenizer tokenizer = new StringTokenizer(requestLine);
            String method = tokenizer.nextToken();
            String path = tokenizer.nextToken();
            
            // 残りのヘッダーを読み飛ばす
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                // ヘッダーを読み飛ばす
            }
            
            // レスポンスを生成
            String responseBody;
            if ("/".equals(path)) {
                responseBody = "<html><body><h1>Welcome to Simple HTTP Server!</h1></body></html>";
            } else if ("/hello".equals(path)) {
                responseBody = "<html><body><h1>Hello, World!</h1></body></html>";
            } else {
                responseBody = "<html><body><h1>404 Not Found</h1></body></html>";
            }
            
            // HTTPレスポンスヘッダー
            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: text/html; charset=utf-8");
            writer.println("Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length);
            writer.println(); // 空行でヘッダー終了
            
            // レスポンスボディ
            writer.println(responseBody);
            
        } catch (IOException e) {
            System.err.println("Error handling request: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```

このHTTPサーバを実行し、ブラウザで`http://localhost:8080/`や`http://localhost:8080/hello`にアクセスすると、HTMLページが表示されます。

## 15.6 人工無脳サーバの実装

ソケット通信を活用して、特定のキーワードに反応して会話を行う「人工無脳サーバ」を実装してみましょう。

```java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class AIProcessor implements Runnable {

    private final Socket clientSocket;

    public AIProcessor(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8)
            );
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true, StandardCharsets.UTF_8)
        ) {
            writer.println("ようこそ！何かお話しください。");

            String message;
            while ((message = reader.readLine()) != null) {
                if ("exit".equalsIgnoreCase(message)) {
                    writer.println("さようなら。");
                    break;
                }

                String response = generateResponse(message);
                writer.println(response);
            }
        } catch (IOException e) {
            System.err.println("An error occurred with a client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                // ignore
            }
            System.out.println("Client disconnected: " + clientSocket.getRemoteSocketAddress());
        }
    }
    
    private String generateResponse(String message) {
        if (message.contains("こんにちは")) {
            return "こんにちは！良い一日ですね。";
        } else if (message.contains("天気")) {
            return "今日の天気は晴れのようです。";
        } else if (message.contains("Java")) {
            return "Javaは素晴らしいプログラミング言語ですね！";
        } else if (message.endsWith("?") || message.endsWith("？")) {
            return "それは興味深い質問ですが、私には分かりかねます。";
        } else {
            return "なるほど。「" + message + "」ですね。";
        }
    }
}
```

この`AIProcessor`を`MultiThreadEchoServer`と同様のメインクラスから呼びだすことで、複数人が同時に接続し、同時に会話できる人工無脳サーバが完成します。

## 15.7 UDP通信の実装

これまではTCP通信を学習してきましたが、Javaでは信頼性よりも速度を重視するUDP（User Datagram Protocol）通信も実装できます。

### UDPサーバの実装

```java
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDPServer {
    public static void main(String[] args) {
        int port = 12345;
        
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("UDP Server is running on port " + port);
            
            byte[] buffer = new byte[1024];
            
            while (true) {
                // データグラムパケットを受信
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(receivePacket);
                
                String receivedMessage = new String(receivePacket.getData(), 0, 
                                                   receivePacket.getLength(), StandardCharsets.UTF_8);
                System.out.println("Received: " + receivedMessage + 
                                 " from " + receivePacket.getAddress());
                
                // エコー応答を送信
                String response = "Echo: " + receivedMessage;
                byte[] responseData = response.getBytes(StandardCharsets.UTF_8);
                
                DatagramPacket sendPacket = new DatagramPacket(
                    responseData, responseData.length,
                    receivePacket.getAddress(), receivePacket.getPort()
                );
                
                socket.send(sendPacket);
            }
        } catch (IOException e) {
            System.err.println("UDP Server error: " + e.getMessage());
        }
    }
}
```

### UDPクライアントの実装

```java
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class UDPClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 12345;
        
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(hostname);
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("Connected to UDP server. Type 'exit' to quit.");
            
            String input;
            while (!(input = scanner.nextLine()).equals("exit")) {
                // メッセージを送信
                byte[] sendData = input.getBytes(StandardCharsets.UTF_8);
                DatagramPacket sendPacket = new DatagramPacket(
                    sendData, sendData.length, address, port
                );
                socket.send(sendPacket);
                
                // 応答を受信
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(
                    receiveBuffer, receiveBuffer.length
                );
                socket.receive(receivePacket);
                
                String response = new String(receivePacket.getData(), 0, 
                                           receivePacket.getLength(), StandardCharsets.UTF_8);
                System.out.println("Server response: " + response);
            }
            
        } catch (IOException e) {
            System.err.println("UDP Client error: " + e.getMessage());
        }
    }
}
```

## 🚀 上級統合プロジェクト：分散タスク管理システム

第15章までの学習内容を統合して、実用的な分散アプリケーションを開発してみましょう。このプロジェクトでは、これまで学習したすべての技術要素を統合的に活用します。

### プロジェクト概要

**システム名**：分散タスク管理システム
**対象技術**：第0章〜第15章で学習したすべての内容
**開発期間目安**：3-4週間

### システム要件

#### 機能要件
- プロジェクト・タスク管理
- チームコラボレーション
- リアルタイム通知
- ファイル共有
- 進捗レポート・ダッシュボード
- カレンダー連携

#### 非機能要件
- クライアント／サーバアーキテクチャ
- 同時接続数：100ユーザー
- リアルタイム同期（1秒以内）
- データベースまたはファイルベース永続化
- REST API提供

### システムアーキテクチャ

```
[クライアント（GUI）] ←→ [サーバー（API）] ←→ [データストレージ]
                              ↓
[WebSocket通知サーバー] ←→ [タスクプロセッサ]
```

### 詳細設計

#### サーバサイド構成
```
com.taskmanager.server
├── TaskManagerServer.java     - メインサーバー
├── api/
│   ├── TaskController.java    - タスク管理API
│   ├── ProjectController.java - プロジェクト管理API
│   └── UserController.java    - ユーザー管理API
├── websocket/
│   ├── NotificationServer.java - WebSocket通知
│   └── MessageHandler.java     - メッセージ処理
├── service/
│   ├── TaskService.java       - タスクビジネスロジック
│   ├── NotificationService.java - 通知管理
│   └── FileService.java       - ファイル管理
└── data/
    ├── Repository.java        - データアクセス層
    └── CacheManager.java      - キャッシュ管理
```

#### クライアントサイド構成
```
com.taskmanager.client
├── TaskManagerClient.java     - メインGUIアプリ
├── gui/
│   ├── MainWindow.java        - メインウィンドウ
│   ├── TaskPanel.java         - タスク表示・編集
│   ├── ProjectPanel.java      - プロジェクト管理
│   └── CalendarPanel.java     - カレンダー表示
├── network/
│   ├── ApiClient.java         - REST API通信
│   └── WebSocketClient.java   - リアルタイム通信
└── model/
    ├── Task.java              - タスクモデル
    ├── Project.java           - プロジェクトモデル
    └── User.java              - ユーザーモデル
```

### 技術要素の統合

#### 1. 並行処理とスレッド管理
```java
public class TaskManagerServer {
    private final ExecutorService clientHandlers = 
        Executors.newFixedThreadPool(100);
    private final ScheduledExecutorService scheduler = 
        Executors.newScheduledThreadPool(10);
    
    public void handleClient(Socket client) {
        clientHandlers.submit(new ClientHandler(client));
    }
}
```

#### 2. WebSocketによるリアルタイム通信
```java
public class NotificationServer {
    private final Set<Session> activeSessions = 
        ConcurrentHashMap.newKeySet();
    
    public void broadcastUpdate(TaskUpdate update) {
        String json = JsonUtil.toJson(update);
        activeSessions.parallelStream()
            .forEach(session -> sendMessage(session, json));
    }
}
```

#### 3. Stream APIによるデータ処理
```java
public class TaskAnalytics {
    public Map<String, Double> getProductivityByUser() {
        return tasks.stream()
            .filter(task -> task.isCompleted())
            .collect(Collectors.groupingBy(
                Task::getAssignee,
                Collectors.averagingDouble(Task::getEstimatedHours)
            ));
    }
}
```

### 実装チェックポイント

#### 技術統合
- [ ] クライアント／サーバ間の安定した通信
- [ ] マルチスレッドによる並行処理の適切な実装
- [ ] GUIの応答性とユーザビリティ
- [ ] リアルタイム通知機能の実装
- [ ] ファイル共有機能の安全な実装

#### 品質管理
- [ ] 外部ライブラリの効果的な活用
- [ ] エラー処理と障害回復のしくみ
- [ ] パフォーマンスとスケーラビリティ
- [ ] セキュリティの考慮

### 学習効果の確認

このプロジェクトを通じて以下の能力が習得できます：

#### 全技術要素の統合力
- オブジェクト指向設計から実装まで一貫した開発
- 複数技術の組み合わせによるシナジー効果の実現
- 実用的なアプリケーションアーキテクチャの設計

#### 実践的な開発スキル
- 大規模システムの設計と実装
- チーム開発を想定した品質管理
- 運用を考慮した堅牢性の実装

#### プロフェッショナルな視点
- 要件分析から運用まで全工程の経験
- 性能とセキュリティの両立
- 継続的な改善とメンテナンス性の確保

### 発展課題

#### 技術的拡張
- モバイルアプリケーション連携（REST API活用）
- 外部カレンダーサービス連携
- 機械学習による作業時間予測
- 分散データベース対応

#### 運用的拡張
- ログ監視とアラートシステム
- 自動バックアップとディザスタリカバリ
- 負荷分散とスケールアウト対応

---

## まとめ

本章では、Javaにおけるネットワークプログラミングの基礎から実践的な応用まで学習しました。TCP/IPソケット通信の基本的なしくみから始まり、マルチスレッド対応、スレッドプール、HTTPサーバ、UDP通信まで幅広い技術を習得しました。

### 総合的な学習の完了

第15章の完了により、Javaプログラミングの基礎から実用的なアプリケーション開発まで、包括的なスキルを習得できました。これまで学習した内容を振り返ると：

#### 技術的成長の軌跡
1. **基礎固め**（第0-2章）：Java環境とオブジェクト指向の理解
2. **設計力向上**（第3-5章）：継承、ポリモーフィズム、構造化設計
3. **実用技術習得**（第6-8章）：例外処理、コレクション、型安全性
4. **現代的手法**（第9-10章）：関数型プログラミング、宣言的データ処理
5. **システム統合**（第11-15章）：外部連携、UI、並行処理、ネットワーク

#### 次のステップ
これらの基礎を基盤として、以下の発展的な学習に進むことができます：
- Spring Frameworkなどのエンタープライズ開発
- Androidアプリケーション開発
- 分散システムとマイクロサービス
- 機械学習とデータサイエンス

Javaの学習は終着点ではなく、より高度で実用的なソフトウェア開発への出発点です。この基盤を活かして、継続的な学習と実践を通じて、優れたソフトウェアエンジニアとして成長していきましょう。

これらの技術を組み合わせることで、チャットアプリケーション、Webサーバ、オンラインゲーム、IoTシステムなど、さまざまなネットワークアプリケーションを開発できます。ネットワークプログラミングは現代のソフトウェア開発において欠かせない技術です。