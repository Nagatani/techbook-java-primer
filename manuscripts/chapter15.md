# 第15章 ネットワークプログラミング

## はじめに：ネットワーク技術の進化と分散システムの基盤

前章まででJavaの主要な技術について学習してきました。この章では、現代のソフトウェア開発において中核的な役割を果たす「ネットワークプログラミング」について詳細に学習していきます。

ネットワークプログラミングは、単なる通信技術ではありません。これは、インターネット時代の到来とともに、コンピュータシステムを根本的に変革し、グローバルな情報社会の基盤となった、現代文明を支える重要な技術です。

### コンピュータネットワークの歴史的発展

コンピュータネットワーク技術の発展は、現代の情報社会の基盤を形成した技術革命の歴史でもあります。この歴史を理解することは、現代のネットワークプログラミングの意義を深く理解するために重要です。

**孤立システム時代（1940年代〜1960年代）**：初期のコンピュータは完全に独立したシステムで、データの交換は物理媒体（パンチカード、磁気テープ）による手動転送のみでした。計算結果の共有には物理的な移動が必要でした。

**専用回線による接続（1960年代〜1970年代）**：大型機同士を専用回線で接続する試みが始まりました。IBMのSNA（Systems Network Architecture）などの企業独自のネットワークアーキテクチャが開発されました。

**ARPANET の誕生（1969年）**：アメリカ国防総省のARPA（Advanced Research Projects Agency）により、世界初のパケット交換ネットワークが構築されました。これが現在のインターネットの直接的な前身となりました。

**TCP/IP プロトコルの確立（1970年代〜1980年代）**：ヴィント・サーフ（Vint Cerf）とロバート・カーン（Robert Kahn）により、異なるネットワーク間の相互接続を可能にする TCP/IP プロトコルが開発されました。

**インターネットの商用化（1990年代）**：NSFNETからの移行により、インターネットが学術用途から商用利用へと拡大し、World Wide Web の普及とともに爆発的に成長しました。

### ネットワークアーキテクチャの進化

ネットワークシステムの設計思想は、技術の発展とともに段階的に進化してきました：

**集中型アーキテクチャ（1960年代〜1970年代）**：メインフレームを中心とした星型ネットワークにより、端末からの処理要求を集中的に処理する構造でした。

**クライアント/サーバーモデル（1980年代〜1990年代）**：パーソナルコンピュータの普及により、処理能力を持つクライアントと専用サーバーが協働する分散処理モデルが確立されました。

**ピアツーピア（P2P）ネットワーク（1990年代後期〜）**：Napster、BitTorrent などにより、中央サーバーを介さない分散型ネットワークモデルが実用化されました。

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

**非同期・リアクティブプログラミング（2010年代〜）**：Node.js、Netty、Vert.x などにより、高並行性ネットワークアプリケーションの開発が革新されました。

### Javaにおけるネットワークプログラミングの発展

Javaは、「Write Once, Run Anywhere」の思想の下で、プラットフォーム独立なネットワークプログラミング環境を提供してきました：

**Java 1.0（1995年）- 基本ソケット API**：java.net パッケージにより、TCP/UDP ソケットの基本機能が提供されました。アプレットによるWebブラウザでのネットワークアプリケーション実行も実現されました。

**Java 1.1（1997年）- RMI（Remote Method Invocation）**：分散オブジェクト技術により、ネットワーク越しのメソッド呼び出しが透明に実行できるようになりました。

**Java 2 EE（1999年）- 企業向けネットワーク技術**：サーブレット、JSP、EJB などにより、企業レベルのWebアプリケーション開発が標準化されました。

**Java 1.4（2002年）- NIO（New I/O）**：非ブロッキングI/Oとチャネル・セレクタAPIにより、高性能なネットワークアプリケーションの開発が可能になりました。

**Java 7（2011年）- NIO.2**：非同期I/O操作の改善により、さらに効率的なネットワーク処理が実現されました。

**Java 8以降 - 関数型・リアクティブ対応**：CompletableFuture、Reactive Streams などにより、現代的な非同期ネットワークプログラミングが支援されています。

### ネットワークプロトコルスタックの理解

現代のネットワークプログラミングを理解するためには、TCP/IPプロトコルスタックの階層構造を理解することが重要です：

**物理層・データリンク層**：ハードウェアレベルでのデータ伝送を担当します。Ethernet、Wi-Fi、光ファイバーなどの物理媒体上でのビット伝送を制御します。

**ネットワーク層（IP）**：パケットの経路選択と配送を担当します。IPv4/IPv6 により、グローバルなアドレス空間でのパケット転送が実現されています。

**トランスポート層（TCP/UDP）**：アプリケーション間の信頼性のある（TCP）または高速な（UDP）データ転送を提供します。ポート番号により、同一マシン上の複数アプリケーションを識別します。

**アプリケーション層**：HTTP、SMTP、FTP、SSH などの高レベルプロトコルにより、具体的なアプリケーション機能を実現します。

### 現代のネットワークアプリケーションの特徴

現代のネットワークアプリケーションは、従来とは大きく異なる要件と特徴を持っています：

**大規模並行処理**：数万から数十万の同時接続を処理する必要があり、従来のスレッドプールモデルでは限界があります。非同期I/Oとイベント駆動アーキテクチャが重要になっています。

**リアルタイム性**：WebSocket、Server-Sent Events、WebRTC などにより、リアルタイム双方向通信が標準的な機能となっています。

**セキュリティ強化**：HTTPS、WSS（WebSocket Secure）、OAuth、JWT など、セキュリティが設計段階から組み込まれています。

**マイクロサービス対応**：サービス間通信、負荷分散、障害回復、監視・ログ記録など、分散システム特有の課題への対応が必要です。

**クラウドネイティブ**：Kubernetes、Docker、サービスメッシュなど、クラウド環境での運用を前提とした設計が求められています。

### ネットワークプログラミングの課題と対策

ネットワークプログラミングには、従来のローカルプログラミングでは遭遇しない特有の課題があります：

**ネットワーク障害への対応**：接続の切断、タイムアウト、パケット損失など、予期しない障害に対する適切な処理が必要です。

**性能とスケーラビリティ**：増加するトラフィックに対して、性能を維持しながらスケールする設計が重要です。

**セキュリティ脅威**：DDoS攻撃、中間者攻撃、SQLインジェクションなど、多様なセキュリティ脅威への対策が必要です。

**分散システムの複雑性**：CAP定理、結果整合性、分散トランザクションなど、分散システム特有の理論と実装技術の理解が必要です。

### この章で学習する内容の意義

この章では、これらの歴史的背景と現代的な課題を踏まえて、Javaにおけるネットワークプログラミングを体系的に学習していきます。単にソケットの使い方を覚えるのではなく、以下の点を重視して学習を進めます：

**ネットワークアーキテクチャの理解**：クライアント/サーバーモデルの設計原則を理解し、適切な役割分担ができる能力を身につけます。

**プロトコル設計の技術**：TCP/UDPの特性を理解し、用途に応じた適切なプロトコル選択ができるようになります。

**並行性とパフォーマンス**：多数のクライアント接続を効率的に処理するための技術を習得します。

**エラーハンドリング**：ネットワーク特有の障害に対する適切な処理技術を身につけます。

**セキュリティ意識**：ネットワークアプリケーションにおけるセキュリティリスクを理解し、基本的な対策を実装できるようになります。

**現代技術への橋渡し**：WebSocket、REST API、マイクロサービスなど、現代的なネットワーク技術への基礎知識を身につけます。

ネットワークプログラミングを深く理解することは、現代の分散システム開発において不可欠な能力を身につけることにつながります。この章を通じて、単なる「データの送受信技術」を超えて、「分散システムの設計と実装の基礎」を習得していきましょう。

この章では、Javaを用いたTCP/IPネットワークアプリケーションの基礎から実践的な応用まで学習します。ソケット通信を理解することで、チャットアプリケーションやWebサーバーなど、様々なネットワークアプリケーションを開発できるようになります。

## 15.1 ネットワークプログラミングの基礎

### クライアント/サーバーモデル

多くのネットワークアプリケーションは、サービスを要求する「クライアント」と、サービスを提供する「サーバー」から構成されるクライアント/サーバーモデルを採用しています。

* **サーバー**: 特定のポートで待機し、クライアントからの接続要求を待ち受けます
* **クライアント**: サーバーのIPアドレスとポート番号を指定して接続を要求し、サービスの提供を受けます

身近な例では、Webブラウザ（クライアント）がWebサーバー（サーバー）にWebページの表示を要求するのも、このモデルに基づいています。

### TCP/IPとソケット

ソケットとは、プログラムがネットワークを通じて通信を行うための抽象的な出入り口です。TCP/IPプロトコル群においては、通信相手を特定するために「IPアドレス」と「ポート番号」の組み合わせを使用します。

* **IPアドレス**: ネットワーク上のコンピュータを一意に特定するための住所のようなものです
* **ポート番号**: コンピュータ内で動作している特定のアプリケーション（プロセス）を識別するための番号です

Javaでは、`java.net`パッケージがソケット通信に必要なクラス群を提供しており、これによりOSI参照モデルにおけるセッション層以上のプロトコルを比較的容易に実装できます。

### JavaにおけるソケットAPI

JavaでTCPソケット通信を実装する際には、主に以下の2つのクラスを使用します。

* `java.net.ServerSocket`: サーバー側でクライアントからの接続を待ち受けるために使用します。特定のポートにバインドし、接続要求を受け入れると、通信用の`Socket`オブジェクトを生成します
* `java.net.Socket`: クライアントとサーバー間の実際の通信経路上に作成されるエンドポイントです。クライアント側ではサーバーに接続するため、サーバー側では`ServerSocket`が接続を受け入れた結果として生成されます

## 15.2 基本的なソケット通信の実装

まず、基本的な「オウム返し（Echo）」サーバーと、それに接続するクライアントを作成します。クライアントから送信されたメッセージを、サーバーがそのまま送り返すだけの簡単なプログラムです。

### オウム返しサーバー（シングルスレッド）

このサーバーは、一度に1つのクライアントしか処理できない単純な構造です。

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

### クライアントの実装

次に、作成したサーバーに接続し、メッセージを送信するためのクライアントプログラムを作成します。

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

1. まず、`EchoServer.java`をコンパイルして実行します。サーバーがクライアントの接続待機状態になります
2. 次に、別のターミナル（コンソール）で`EchoClient.java`をコンパイルして実行します
3. クライアントのコンソールで何か文字を入力してEnterキーを押すと、サーバーがそれを受け取り、クライアントに応答を返します

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

実用的なサーバーは複数のクライアントと同時に通信できる必要があります。これを実現するために、クライアントごとに新しいスレッドを生成して通信処理を任せる「マルチスレッド」モデルを導入します。

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

メインのサーバークラスは、接続を待ち受け、`ClientHandler`のインスタンスを生成して新しいスレッドで実行することに専念します。

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

このサーバーを実行し、複数の`EchoClient`から同時に接続してみてください。それぞれのクライアントが独立してサーバーと通信できることが確認できます。

## 15.4 スレッドプールの活用（ExecutorService）

クライアントごとにスレッドを生成するモデルは単純で分かりやすいですが、クライアント数が非常に多くなると、スレッドの生成・破棄のコストや、各スレッドで消費するメモリが問題になる可能性があります。

このような問題を解決するため、現代的なJavaプログラミングでは`ExecutorService`（スレッドプール）を使用するのが一般的です。スレッドプールは、あらかじめ一定数のスレッドを作成しておき、タスク（この場合はクライアント処理）が発生するたびに待機中のスレッドに割り当てる仕組みです。

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

この実装では、メインスレッドは`new Thread().start()`を呼び出す代わりに、`ExecutorService`にタスクを`submit()`するだけです。スレッドのライフサイクル管理は`ExecutorService`に任せることができ、より安定したサーバーを構築できます。

## 15.5 簡単なHTTPサーバーの実装

私たちが日常的に利用しているWebは、HTTP (HyperText Transfer Protocol) というプロトコルに基づいています。HTTPはテキストベースのプロトコルであり、その基本的な動作はソケット通信で実装できます。

### HTTPプロトコルの基本

#### HTTP Request

ブラウザ（クライアント）は、サーバーに以下のようなテキストデータを送信します。

```http
GET /hello HTTP/1.1
Host: localhost:12345
Accept: */*
(空行)
```

#### HTTP Response

サーバーは、リクエストに応じて以下のようなテキストデータを返します。

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

### 簡易HTTPサーバーの実装

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

このHTTPサーバーを実行し、Webブラウザで`http://localhost:8080/`や`http://localhost:8080/hello`にアクセスすると、HTMLページが表示されます。

## 15.6 人工無脳サーバーの実装

ソケット通信を活用して、特定のキーワードに反応して会話を行う「人工無脳サーバー」を実装してみましょう。

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

この`AIProcessor`を`MultiThreadEchoServer`と同様のメインクラスから呼び出すことで、複数人が同時に接続し、同時に会話できる人工無脳サーバーが完成します。

## 15.7 UDP通信の実装

これまではTCP通信を学習してきましたが、Javaでは信頼性よりも速度を重視するUDP（User Datagram Protocol）通信も実装できます。

### UDPサーバーの実装

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

## まとめ

この章では、Javaにおけるネットワークプログラミングの基礎から実践的な応用まで学習しました。TCP/IPソケット通信の基本的な仕組みから始まり、マルチスレッド対応、スレッドプール、HTTPサーバー、UDP通信まで幅広い技術を習得しました。

これらの技術を組み合わせることで、チャットアプリケーション、Webサーバー、オンラインゲーム、IoTシステムなど、様々なネットワークアプリケーションを開発できるようになります。ネットワークプログラミングは現代のソフトウェア開発において欠かせない技術です。