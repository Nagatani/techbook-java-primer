# 第16章 ネットワークプログラミング - 解答例

本ディレクトリには、第16章の課題に対する解答例が含まれています。

## 基礎レベル課題の解答

### 1. TCPクライアント・サーバー（基礎課題1）
- **SimpleServer.java**: 受け取った行を大文字にして返すTCPサーバー（ポート8080）。`bye`を受け取ると、その接続を閉じて次の接続を待つ
- **SimpleClient.java**: 3つの文字列を送り、返事を表示するクライアント

**実行方法：**
```bash
# コンパイル（このフォルダーで実行。クラスはパッケージchapter16.solutionsにある）
javac -d out *.java

# サーバーを起動（別ターミナル）
java -cp out chapter16.solutions.SimpleServer [ポート番号]

# クライアントを起動
java -cp out chapter16.solutions.SimpleClient [ホスト名] [ポート番号]
```

**実行例（クライアント）：**
```
送信: hello -> 返事: HELLO
送信: java network -> 返事: JAVA NETWORK
送信: bye -> 返事: BYE
```

**実装のポイント：**
- `PrintWriter`の2つ目の引数に`true`を渡し、`println`のたびに送り出す（渡さないと、相手に届かないまま待ち続けることがある）
- 1つの接続で起きた`IOException`ではサーバー全体を止めず、次の接続を待つ
- サーバーが起動していないときの`ConnectException`は、原因がわかるメッセージにして表示する

### 2. HTTP通信（基礎課題2）
- **HttpClientExample.java**: `HttpURLConnection`でGETリクエストを送り、ステータスが200のときだけ本文を表示する。接続と読み取りに5秒のタイムアウトを設定する
- 引数を省くと、`com.sun.net.httpserver.HttpServer`（JDKに含まれる簡単なHTTPサーバー）を手元で立て、200を返すページと404を返すページに順にアクセスする。ネットワークにつながっていなくても試せる
- **SimpleHttpClient.java**（参考）: `HttpURLConnection`を使わず、ソケットでHTTPのリクエストを直接書く例。HTTPの中身を知る参考になる

**実行方法：**
```bash
javac -d out *.java
java -cp out chapter16.solutions.HttpClientExample                      # 手元のサーバーで試す
java -cp out chapter16.solutions.HttpClientExample https://example.com/  # 指定したURLから取得する
```

**実行例（引数を省いた場合）：**
```
GET http://localhost:(ポート)/hello
ステータス: 200
本文: こんにちは、HTTP！
本文: 2行目の本文です

GET http://localhost:(ポート)/missing
ステータス: 404
200ではないので、本文は読みません
```

ポート番号は実行のたびに変わるので、表示では「(ポート)」に置き換えています。

### 3. UDPソケット通信（基礎課題3）
- **UdpServer.java**: 受け取った文字列を大文字にして返すUDPサーバー。`bye`を受け取ると終わる
- **UdpClient.java**: 3つの文字列を送り、返事を待つクライアント。2秒待っても返事がなければその旨を表示する

**実行方法：**
```bash
javac -d out *.java

# サーバーを起動（別ターミナル）
java -cp out chapter16.solutions.UdpServer [ポート番号]

# クライアントを起動
java -cp out chapter16.solutions.UdpClient [ホスト名] [ポート番号]
```

**実装のポイント：**
- `DatagramPacket`の`getLength()`で、受け取った長さだけを文字列にする
- 返事は、受け取ったパケットの`getAddress()`と`getPort()`へ送る
- UDPは届いたかどうかを知らせないので、`setSoTimeout`で待つ時間の上限を決める

### 4. マルチスレッドサーバー（基礎課題4）
- **EchoServer.java**: 接続ごとの処理をスレッドプール（ExecutorService）に任せるTCPエコーサーバー。シャットダウンフックで終わる
- **EchoClient.java**: 入力した行を送り、返事を表示するクライアント。複数起動して、同時に処理されることを確かめる
**実行方法：**
```bash
javac -d out *.java
java -cp out chapter16.solutions.EchoServer [ポート番号]          # 別ターミナル
java -cp out chapter16.solutions.EchoClient [ホスト名] [ポート番号]  # 複数のターミナルで起動する
```

## 応用レベル課題の解答

### 1. マルチクライアント対応チャットサーバー（発展課題1）
- **ChatServer.java**: 高機能チャットサーバー
- **ChatClient.java**: 対応するチャットクライアント

**機能：**
- ユーザー認証とセッション管理
- 複数のチャットルーム
- プライベートメッセージ
- メッセージ履歴
- コマンドシステム（/help, /list, /join, /pm など）

**実行方法：**
```bash
javac -d out *.java

# サーバーを起動
java -cp out chapter16.solutions.ChatServer [ポート番号]

# クライアントを起動（複数起動可能）
java -cp out chapter16.solutions.ChatClient [ホスト名] [ポート番号]
```

**実装の特徴：**
- ConcurrentHashMapによるスレッドセーフなデータ管理
- ExecutorServiceによる効率的な接続管理
- プロトコル設計（コマンドとメッセージの区別）
- ルームごとのメッセージ配信

## 学習のポイント

### 1. ソケットプログラミングの基礎
- ServerSocketとSocketの使い分け
- ブロッキングI/Oとタイムアウト設定
- 双方向通信の実装パターン

### 2. マルチスレッド処理
- スレッドプールの適切なサイジング
- 共有リソースへの安全なアクセス
- スレッドの適切な終了処理

### 3. プロトコル設計
- テキストベースプロトコルの利点と欠点
- コマンドとデータの区別
- エラーメッセージの設計

### 4. エラーハンドリング
- ネットワーク固有の例外処理
- 接続の異常終了への対処
- リソースリークの防止

## 発展的な学習

これらの解答例を基に、以下のような拡張を試みることができます：

1. **セキュリティの追加**
   - SSL/TLSによる暗号化通信
   - より堅牢な認証メカニズム

2. **パフォーマンスの向上**
   - NIOを使用した非同期I/O
   - より効率的なメッセージ配信

3. **機能の拡張**
   - ファイル転送機能
   - 音声/ビデオチャット
   - 永続的なメッセージストレージ

4. **プロトコルの改良**
   - バイナリプロトコルへの移行
   - 圧縮の実装
   - より効率的なシリアライゼーション

## 注意事項

- これらのコードは教育目的のサンプルです
- プロダクション環境では、より堅牢なエラー処理とセキュリティ対策が必要です
- ネットワークプログラミングでは、常にリソースの適切な管理を心がけてください