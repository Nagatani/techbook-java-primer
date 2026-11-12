# 第16章 基礎課題 - ネットワークプログラミング

## 課題概要

Javaのネットワークプログラミングの基礎を学ぶための課題です。

このフォルダーには解答例のコードを置いていません。各課題のファイル名は、自分で作成するファイルの名前の例です。
近い内容の解答例は[solutions/](../solutions/)にあり、課題ごとに対応する解答例を書いています。

## 課題リスト

### 課題1: TCPクライアント・サーバー通信

`SimpleServer.java`と`SimpleClient.java`を作成し、基本的なTCP通信を実現してください。

**要件:**

- サーバーはポート8080で待ち受ける
- クライアントからメッセージを1行ずつ受信し、大文字に変換して返す
- 適切な例外処理を実装する

解答例: 受け取った行をそのまま返すエコーサーバーの`EchoServer.java`と`EchoClient.java`が近い内容です。大文字に変える処理を足せば、この課題の答えになります。

### 課題2: HttpURLConnectionを使用したHTTP通信

`HttpClientExample.java`を作成し、指定されたURLからデータを取得してください。

**要件:**

- `HttpURLConnection`でHTTPのGETリクエストを送る（第16章の節「HttpURLConnectionの使用」）
- ステータスコードを確認し、200のときだけ本文を読み取って表示する
- 接続と読み取りにタイムアウトを設定する

解答例: この課題そのものの解答例はありません。`SimpleHttpClient.java`は、`HttpURLConnection`を使わずにソケットでHTTPのリクエストを直接書く例で、HTTPの中身を知る参考になります。

### 課題3: UDPソケット通信

`UdpServer.java`と`UdpClient.java`を作成し、UDP通信を実現してください。

**要件:**

- `DatagramSocket`と`DatagramPacket`を使用する
- サーバーは受け取った文字列を大文字にして、送ってきた相手へ返す
- クライアントは`setSoTimeout`で待つ時間の上限を決め、返事が来なければその旨を表示する

解答例: `UdpServer.java`と`UdpClient.java`。

**実行例**（解答例。サーバーを起動してからクライアントを実行した場合）:

```
送信: hello -> 返事: HELLO
送信: udp -> 返事: UDP
送信: bye -> 返事: BYE
```

サーバーを起動せずにクライアントを実行すると、`送信: hello -> 2000ミリ秒待っても返事がありません`のように表示されます。

### 課題4: マルチスレッドサーバー

`MultiThreadedServer.java`を作成し、複数のクライアントを同時に処理できるサーバーを作成してください。

**要件:**

- 各クライアント接続を別スレッドで処理する
- スレッドプール（`ExecutorService`）を使う
- ソケットをtry-with-resourcesで確実に閉じる

解答例: `EchoServer.java`は、接続ごとの処理をスレッドプールに任せるエコーサーバーです。`EchoClient.java`を複数起動して試せます。

## 実行方法

自分で作ったプログラムは、サーバーを先に起動し、別のターミナルでクライアントを実行します。

```bash
javac SimpleServer.java SimpleClient.java
java SimpleServer
# 別のターミナルで
java SimpleClient
```

解答例はパッケージ`chapter16.solutions`にあるので、`solutions/`で次のように実行します。

```bash
cd ../solutions
javac -d out *.java
java -cp out chapter16.solutions.UdpServer
# 別のターミナルで
java -cp out chapter16.solutions.UdpClient
```

## 注意事項

- ポートが既に使用されている場合は、別のポート番号を使用してください
- ファイアウォールがブロックしている場合は、適切に設定を変更してください
- 必ずtry-with-resourcesまたはfinallyブロックでリソースをクローズしてください
