# 第17章 発展課題 - ネットワークプログラミング

## 課題概要
高度なネットワークプログラミング技術を習得するための発展課題です。

## 課題リスト

### 課題1: チャットアプリケーション
`ChatServer.java`と`ChatClient.java`を実装し、複数クライアント間でのリアルタイム通信を実現してください。

**要件:**
- マルチスレッドでの複数クライアント同時接続
- ユーザー名の管理
- プライベートメッセージ機能
- ユーザーの入退室通知
- 履歴の保存機能

**実装のポイント:**
- ConcurrentHashMapを使用したスレッドセーフなユーザー管理
- ブロードキャスト機能の実装
- プロトコルの設計（コマンド形式など）

### 課題2: ファイル転送プロトコル
`FileTransferServer.java`と`FileTransferClient.java`を実装し、ファイル転送機能を作成してください。

**要件:**
- 大容量ファイルの分割転送
- 転送進捗の表示
- レジューム機能（中断からの再開）
- チェックサムによる整合性確認
- 複数ファイルの同時転送

**実装のポイント:**
- バイト配列によるチャンク転送
- プログレスバーの更新
- MD5/SHA-256によるファイル検証

### 課題3: RESTful APIクライアント
`RestApiClient.java`を実装し、REST APIとの通信を行うクライアントを作成してください。

**要件:**
- HTTPメソッド（GET, POST, PUT, DELETE）の実装
- JSONデータの送受信
- 認証機能（Bearer Token）
- エラーハンドリング
- 非同期リクエスト処理

**実装のポイント:**
- HttpURLConnectionまたはHttpClientの使用
- Jackson/GsonによるJSON処理
- CompletableFutureによる非同期処理

## 実行方法

### チャットアプリケーション
```bash
# サーバー起動
javac ChatServer.java
java ChatServer

# クライアント起動（複数端末で実行）
javac ChatClient.java
java ChatClient
```

### ファイル転送
```bash
# サーバー起動
javac FileTransferServer.java
java FileTransferServer

# クライアントからファイル送信
javac FileTransferClient.java
java FileTransferClient send large_file.zip
```

## 評価基準
- プロトコル設計の適切性
- エラー処理の完全性
- パフォーマンスの考慮
- コードの可読性と保守性
- セキュリティへの配慮