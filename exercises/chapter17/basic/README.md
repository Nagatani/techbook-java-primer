# 第17章 基礎課題 - ネットワークプログラミング

## 課題概要
Javaのネットワークプログラミングの基礎を学ぶための課題です。

## 課題リスト

### 課題1: TCPクライアント・サーバー通信
`SimpleServer.java`と`SimpleClient.java`を実装し、基本的なTCP通信を実現してください。

**要件:**
- サーバーはポート8080で待ち受ける
- クライアントからメッセージを受信し、大文字に変換して返す
- 適切な例外処理を実装する

### 課題2: URLConnectionを使用したHTTP通信
`HttpClientExample.java`を実装し、指定されたURLからデータを取得してください。

**要件:**
- URLConnectionを使用してHTTP GETリクエストを送信
- レスポンスを読み取って表示
- HTTPステータスコードを確認

### 課題3: UDPソケット通信
`UdpServer.java`と`UdpClient.java`を実装し、UDP通信を実現してください。

**要件:**
- DatagramSocketとDatagramPacketを使用
- パケットの送受信を実装
- タイムアウト処理を含める

### 課題4: マルチスレッドサーバー
`MultiThreadedServer.java`を実装し、複数のクライアントを同時に処理できるサーバーを作成してください。

**要件:**
- 各クライアント接続を別スレッドで処理
- スレッドプールを使用した効率的な実装
- 適切なリソース管理

## 実行方法

### サーバーの起動
```bash
javac SimpleServer.java
java SimpleServer
```

### クライアントの実行
```bash
javac SimpleClient.java
java SimpleClient
```

## 注意事項
- ポートが既に使用されている場合は、別のポート番号を使用してください
- ファイアウォールがブロックしている場合は、適切に設定を変更してください
- 必ずtry-with-resourcesまたはfinallyブロックでリソースをクローズしてください