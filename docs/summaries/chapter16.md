---
source: manuscripts/chapter16-network-programming.md
summarized: 2026-11-11
sections:
  - key: "本章の学習目標"
    hash: f1ef8fd9
  - key: "はじめに"
    hash: c0698d24
  - key: "ネットワークプログラミングの基礎"
    hash: 1828f342
  - key: "ソケットプログラミングの基礎"
    hash: ac9595b4
  - key: "マルチスレッドサーバーの実装"
    hash: fb284b01
  - key: "HTTPクライアントの実装"
    hash: e316a826
  - key: "UDP通信の実装"
    hash: c9e6f978
  - key: "実践的な例：チャットアプリケーション"
    hash: bea458cc
  - key: "JSON APIクライアントの実装"
    hash: f114d08f
  - key: "セキュリティの考慮事項"
    hash: 8c37522b
  - key: "パフォーマンスの最適化"
    hash: 1eb73f48
  - key: "まとめ"
    hash: 510281e7
  - key: "章末演習"
    hash: 27fceed8
  - key: "よくあるエラーと対処法"
    hash: 59e9cab0
---

# 第16章 ネットワークプログラミング 要約

行番号・サンプルコード番号の正確な対応は `docs/book-index/chapter16-network-programming.md` を参照する。

## 章の概要

- TCPのSocket/ServerSocketによるエコーサーバーから始め、スレッドプールによるマルチクライアント化、HTTP、UDP、チャット、JSON API、SSL、NIOまでを一通り示す
- HTTPはソケット直書き、HttpURLConnection、Java 11のHttpClient（非同期）の3段階で扱う
- REST・WebSocketは独立した節をやめ、まとめの「次のステップ」で説明する（2026-11-11）
- 前提は第14章のストリームと第15章のExecutorService・CompletableFuture

## 構成上の問題

- 「ネットワークプログラミングの基礎」（L34–57）は見出しの直後に空行が続き、IPアドレスとポート番号の小節だけがある
    + IPアドレスとポート番号は2026-11-11に文章で説明するようになった
    + 学習目標の「TCP/IPプロトコルスタックの理解」「ソケットの概念」を説明する箇所はまだない
    + TCPとUDPの比較は、16-1前の1文、UDP節冒頭の図（chapter16-tcp-vs-udp）、UDP節末尾の段落に分かれている。まとめでは「違いを理解し」と書いている
    + 見出し直後の空行（L35–39）と小節後の空行（L54–56）が残っている
- 学習目標で予告しているが本文で扱っていない内容
    + UDPのブロードキャスト通信（UDP節末尾で「送れます」と触れるだけ）
    + ファイル転送プログラム
- 学習目標で挙げていないのに節がある内容
    + 非同期HttpClient、JSON API（Jackson）、SSL、NIO
- 「REST API実装の基礎」「WebSocketクライアントの基礎」の短い節は、まとめの「次のステップ」へ統合した（2026-11-11に解消）
- 16-11がJacksonを導入方法なしで使う問題は、外部ライブラリであることと第22章で学ぶことを本文で断るようにした（2026-11-11に解消）

## 節ごとの要約

- 本章の学習目標（L3–27）
    + 4つの大項目（基礎、TCP、UDPとHTTP、実用アプリ）。前提は第14章のストリーム、第15章のマルチスレッド
    + 「HTTPURLConnection」はクラス名の表記誤り（正しくはHttpURLConnection）
- はじめに（L28–33）
    + ネットワーク通信の重要性と章の位置付けを述べる2段落
- ネットワークプログラミングの基礎（L34–57）
    + 通信相手はIPアドレスとポート番号の組で指定する。IPv4（32ビット）とIPv6（128ビット）、ポート番号の役割と0〜65535の範囲を文章で説明し、3区分（Well-known・登録済み・動的）をリストで示す
- ソケットプログラミングの基礎（L58–173）
    + ServerSocketとSocketの役割をシーケンス図（chapter16-tcp-socket）で示す
    + Socket、PrintWriter、BufferedReaderによるTCPクライアント（16-1）と、accept()で待ち受けるエコーサーバー（16-2）。try-with-resourcesで自動クローズする
- マルチスレッドサーバーの実装（L174–253）
    + 単一スレッドサーバーの限界と、複数クライアントを同時に処理する利点を文章で述べる
    + ExecutorServiceの固定スレッドプールでClientHandlerを実行するサーバー（16-3）。ECHO・TIME・QUITの簡易プロトコル
- HTTPクライアントの実装（L254–472）
    + HTTPリクエスト・レスポンスの構造
    + ソケットで直接HTTPを話すクライアント（16-4、HTTP/1.0で送りチャンク形式を避ける理由を説明）、HttpURLConnectionによるGitHub APIへのGET（16-5、`URI.create(...).toURL()`）、Java 11のHttpClientのsendAsyncとCompletableFutureの連鎖（16-6、`done.join()`で完了を待つ）
    + 16-6の後に、非同期で他の処理を続けられること、HTTP/2の多重化を文章で説明する
- UDP通信の実装（L473–586）
    + TCPとUDPの違いを図（chapter16-tcp-vs-udp）で示す
    + DatagramSocketとDatagramPacketによるエコーサーバー（16-7）とクライアント（16-8）。節末でUDPの特徴（損失・順序非保証、低遅延、ブロードキャスト）を文章で述べる
    + H3「UDP受信側」の中身は送信して応答を受け取るクライアントで、見出しと内容が合わない
- 実践的な例：チャットアプリケーション（L587–723）
    + `Collections.synchronizedSet`で管理し全クライアントへブロードキャストするチャットサーバー（16-9、ポート5000）と、受信スレッドとメインの送信ループを分けたクライアント（16-10）
    + 終了時に`shutdownInput()`と`join()`で受信スレッドを終わらせてから資源を閉じる理由（BufferedReaderのロック）を説明する
    + 実行結果はサーバー未起動時の接続エラーの例として示す
- JSON APIクライアントの実装（L724–774）
    + JacksonとHttpClientで汎用GET/POSTクライアントを作る（16-11）。Jacksonは外部ライブラリで、追加方法は第22章で学ぶと断る
    + JSON処理のポイント（ライブラリに任せる、`Class<T>`で型を渡す、Content-Type/Accept、ステータスコードの確認）を文章で述べる
- セキュリティの考慮事項（L775–861）
    + 入力検証・暗号化・認証・タイムアウト・リソース制限の5対策
    + SSLSocketFactoryによるTLS 1.3クライアント（16-12）
- パフォーマンスの最適化（L862–929）
    + SelectorとServerSocketChannelによる単一スレッドのNIOサーバー（16-13）
    + 第14章ではSelector/チャネルを扱っておらず、NIOの説明はこの節だけで完結している
- まとめ（L930–962）
    + 重要なポイント5点
    + 次のステップでREST（リソース、HTTPメソッド、ステートレス、Spring Boot・JAX-RS）とWebSocket（双方向通信、`HttpClient.newWebSocketBuilder()`）を説明し、gRPC、マイクロサービスを挙げる
    + 「NIOを使用した非同期I/Oによる応答時間の短縮」は不正確（NIOのSelectorはノンブロッキングI/Oで、利点は応答時間より接続数に対するスケーラビリティ）
- 章末演習（L963–986）
    + 演習リンクは`exercises/chapter16/`
    + 「詳細な課題内容と実装のヒントは…」が2回ある（L975、L983）
- よくあるエラーと対処法（L987–1001）
    + 本文に挙げるエラー: 接続のエラー、データの送受信のエラー、HTTPのステータスコードの確認漏れ、リソースの解放漏れ。詳細は`appendix/h-common-errors/chapters/chapter16.md`

## 他章との関係

- 第14章（ファイルI/O）: ストリームの使い方を前提とする
- 第15章（マルチスレッド）: ExecutorServiceとCompletableFutureを前提とし、16-3と16-6で再利用する
- 第22章（ドキュメントと外部ライブラリ）: Jacksonの導入方法と使い方を扱う。16-11から誘導している
- 第17章: 章末演習の「次のステップ」で誘導する
- 付録H: エラー集の詳細を置く

## 校正で見つけた個別の問題

- L18: 「HTTPURLConnection」はHttpURLConnectionの誤記
- L260、L271: 「HTTPリクエストの構造。」のように句点でコードブロックを導入している
- L265: HTTPリクエストの例の「User-Agent: Java/17」は、本書の対象（Java 25）と合わない
- 16-5の実行結果は「Response:」の後で改行してJSONを示すが、コードは`"Response: " + response`を1行で出力する
- 16-4の実行結果の不一致、16-7の「レスポンス送信:」行、`new URL(String)`の使用、エラー集の16-15・16-23・太字・「考慧」の誤字は、2026-11-09〜11-11の修正で解消した（エラー集は付録Hへ移動）
