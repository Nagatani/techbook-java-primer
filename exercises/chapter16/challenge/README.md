# 第16章 チャレンジ課題

## 学習目標
- HTTPのリクエスト（リクエストライン・ヘッダー）とレスポンス（ステータスライン・ヘッダー・本文）の形を、自分で読み書きして理解する
- `ServerSocket`とスレッドプールで、複数の接続を同時に処理するサーバーを作る
- ステータスコード（200・400・403・404・405・500）を状況に合わせて返す
- ディレクトリトラバーサル、大きすぎるリクエスト、届かないリクエストといった、外からの入力への備えを入れる
- `HttpClient`とソケットの両方から、自分のサーバーにリクエストを送って確かめる

## 課題一覧

### 課題1: 簡易Webサーバー
**ファイル**（このフォルダーのファイルは解答例です）:
```
challenge/
├── HttpRequest.java       解析したリクエスト（レコード）
├── SimpleHttpServer.java  サーバー本体（mainで単独でも起動できる）
├── HttpServerDemo.java    サーバーを起動してリクエストを送る確認用のmain
└── public/                配信するファイル（ドキュメントルート）
    ├── index.html
    └── style.css
```

指定したフォルダー（ドキュメントルート）のファイルを、ブラウザーに配信するHTTPサーバーを作成してください。

**要求仕様**:
- 接続の処理
    - `ServerSocket`で待ち受け、受け付けた接続をスレッドプール（10スレッド）で処理する
    - 接続の受け付けは、mainとは別のスレッドで行う。`stop()`でサーバーソケットを閉じ、処理中の接続が終わるのを待つ
    - 一定時間（5秒）リクエストが届かない接続は閉じる（`setSoTimeout`）
- リクエストの解析
    - 1行目（リクエストライン）を「メソッド パス バージョン」に分け、続くヘッダーを空行まで読む
    - 形が正しくないリクエスト、長すぎる行（8192文字を超える）、多すぎるヘッダーには400を返す
    - パスの`?`より後ろ（クエリ文字列）は使わない
- レスポンス
    - GETとHEADに応える。それ以外のメソッドには405と`Allow: GET, HEAD`を返す
    - `/`は`/index.html`として扱う
    - ドキュメントルートの外を指すパス（`/../`を含むものなど）には403、ファイルがなければ404、読み込みに失敗したら500を返す
    - 拡張子から`Content-Type`を決める（html・css・js・txt・png・jpg。それ以外は`application/octet-stream`）
    - `Content-Type`・`Content-Length`・`Connection: close`のヘッダーを付ける。HEADのときは本文を送らない
    - エラーのときは、ステータスコードを書いた短いHTMLを本文にする
- アクセスログ
    - 1件のリクエストごとに、クライアントのIPアドレス・メソッド・パス・ステータスコードを1行で出力する
- 確認用のmain（`HttpServerDemo`）
    - ポート番号0でサーバーを起動し、`HttpClient`で`/`を取得する
    - ソケットで直接リクエストを送り、正常な取得・HEAD・存在しないファイル・POST・ドキュメントルートの外・形の正しくないリクエストの応答を確かめる

**実行例**（`HttpServerDemo`）:
```
=== HttpClientでGET ===
[log] 127.0.0.1 GET / 200
ステータス: 200
Content-Type: text/html; charset=UTF-8
本文の1行目: <!DOCTYPE html>

=== ソケットで直接送る ===
[log] 127.0.0.1 GET /style.css 200
GET /style.css HTTP/1.1 -> HTTP/1.1 200 OK（本文はContent-Lengthと同じ長さ）
[log] 127.0.0.1 HEAD /index.html 200
HEAD /index.html HTTP/1.1 -> HTTP/1.1 200 OK（本文なし）
[log] 127.0.0.1 GET /nothing.html 404
GET /nothing.html HTTP/1.1 -> HTTP/1.1 404 Not Found（本文はContent-Lengthと同じ長さ）
[log] 127.0.0.1 POST /index.html 405
POST /index.html HTTP/1.1 -> HTTP/1.1 405 Method Not Allowed（本文はContent-Lengthと同じ長さ）
[log] 127.0.0.1 GET /../SimpleHttpServer.java 403
GET /../SimpleHttpServer.java HTTP/1.1 -> HTTP/1.1 403 Forbidden（本文はContent-Lengthと同じ長さ）
[log] 127.0.0.1 - - 400
HELLO -> HTTP/1.1 400 Bad Request（本文はContent-Lengthと同じ長さ）

サーバーを停止しました
```

`[log]`の行はサーバーのスレッドが出力します。
サーバーはログを出してから応答を送り、確認用のmainは応答を受け取ってから表示するので、この順に並びます。
ポート番号は実行ごとに変わるので表示していません。

サーバーを単独で起動すると、ブラウザーや`curl`からアクセスできます。

```bash
java SimpleHttpServer 8080 public
# ブラウザーで http://localhost:8080/ を開く。別のターミナルからは次のように試せる
curl -i http://localhost:8080/style.css
curl -i --path-as-is http://localhost:8080/../README.md
```

`localhost`がIPv6のアドレスになる環境では、ログのIPアドレスは`0:0:0:0:0:0:0:1`と表示されます。

## コンパイルと実行

```bash
javac *.java
java HttpServerDemo
```

`public/`フォルダーを相対パスで読むので、このフォルダーで実行します。

## 実装のヒント

```java
private Response createResponse(HttpRequest request) {
    if (!request.method().equals("GET") && !request.method().equals("HEAD")) {
        return errorResponse(405, "Method Not Allowed");
    }
    String path = request.path().equals("/") ? "/index.html" : request.path();
    Path file = root.resolve(path.substring(1)).normalize();  // ..を取り除いた形にする
    if (!file.startsWith(root)) {
        return errorResponse(403, "Forbidden");  // ドキュメントルートの外
    }
    // ...
}
```

- HTTPの行の区切りは`\r\n`。ヘッダーの終わりは空行で示す。`BufferedReader`の`readLine()`は`\r\n`も1つの区切りとして扱う
- `Content-Length`は文字数ではなくバイト数。ファイルは`Files.readAllBytes`でバイト列として読み、その長さを使う
- ヘッダーは`StandardCharsets.ISO_8859_1`で書くと、1文字が1バイトになる
- `root`は`toAbsolutePath().normalize()`しておく。`normalize()`したパスが`root`で始まらなければ、`../`でルートの外へ出ている
- `stop()`で`serverSocket.close()`を呼ぶと、`accept()`で待っているスレッドに`SocketException`が投げられる。これを終わりの合図にして受け付けのループを抜ける
- 複数のスレッドが同時にログを書くので、ログを出すメソッドを`synchronized`にして1行ずつ出力する
- サーバーのポート番号に0を指定すると、空いているポートが選ばれる。実際の番号は`getLocalPort()`で調べる

## 応用のポイント

1. プロトコルは文字の決まり
    - HTTP/1.1のリクエストとレスポンスは、決まった形の行の並び。ソケットで直接送ると、ブラウザーやHttpClientが代わりにしていることが見える
2. 外からの入力を信用しない
    - パス・行の長さ・ヘッダーの数・届くまでの時間に上限やチェックを設けないと、ファイルを盗まれたり、スレッドを使い切られたりする
3. 接続ごとのスレッド
    - 1つの接続の処理が遅くても、ほかの接続はプールの別のスレッドで処理される

## 発展（任意）

- 1つの接続で複数のリクエストを処理するKeep-Aliveに対応する（`Connection: keep-alive`のときは接続を閉じずに次のリクエストを読む）
- `If-Modified-Since`ヘッダーを読み、ファイルが変わっていなければ304を返す（`Last-Modified`ヘッダーも付ける）
- `/api/time`のような決まったパスに、ファイルではなくプログラムで作った内容（JSONの文字列など）を返す
- 第16章の「セキュリティの考慮事項」を参考に、`SSLServerSocket`でHTTPSに対応する（自己署名証明書を`keytool`で作る）

## 完了チェックリスト

- [ ] ブラウザーで`http://localhost:8080/`を開くと、`index.html`がスタイル付きで表示される
- [ ] 存在しないファイルに404、GETとHEAD以外に405を返す
- [ ] `/../`を含むパスで、ドキュメントルートの外のファイルを読ませない
- [ ] HEADのとき、ヘッダーだけを返して本文を送らない
- [ ] `Content-Length`が本文のバイト数と一致する
- [ ] 複数の接続を同時に処理できる
- [ ] `stop()`で、受け付けのスレッドとスレッドプールが終わる

**次のステップ**: チャレンジ課題が完了したら、第17章「GUIプログラミングの基礎」に進みましょう。
