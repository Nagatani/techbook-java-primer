# 第15章 ファイルI/O 解答例

この章では、JavaのNIO.2 APIを活用した高性能なファイルI/O処理を学習します。

## 実装内容

### 1. FileProcessor.java - 基本的なファイル操作
- **NIO.2 APIを活用した高性能なファイル処理**
- ファイル・ディレクトリの操作（コピー、移動、削除）
- ファイル検索とフィルタリング
- ファイル比較と整合性チェック

**重要ポイント：**
- Files クラスによる高レベルAPI
- Path インターフェイスによるパス操作
- ファイル属性の効率的な取得
- パターンマッチングによるファイル検索

### 2. CSVHandler.java - CSV読み書き
- 高性能で柔軟なCSV処理機能
- 設定可能なフォーマット（区切り文字、引用符、エンコーディング）
- バリデーションと統計機能
- ストリーム処理による大容量データ対応

**重要ポイント：**
- BufferedReader/Writer による効率的なI/O
- 引用符とエスケープシーケンスの適切な処理
- メモリ効率を考慮したストリーム処理
- CSV形式の変換機能

### 3. BackupSystem.java - ファイルバックアップ
- 増分バックアップシステム
- ZIP圧縮によるストレージ効率化
- 整合性チェック（MD5チェックサム）
- 自動的な古いバックアップの削除

**重要ポイント：**
- 増分バックアップによる効率的なデータ管理
- ZipOutputStream による圧縮処理
- ファイル変更検出アルゴリズム
- インデックスファイルによる高速な差分検出

### 4. WatchServiceExample.java - ファイル監視
- **NIO.2とファイル監視システム**
- リアルタイムファイル監視
- 再帰的ディレクトリ監視
- イベントフィルタリングとメタデータ取得

**重要ポイント：**
- **WatchService による効率的なファイル監視**
- 非同期イベント処理
- カスタムイベントハンドラー
- 監視統計とパフォーマンス最適化

## 学習のポイント

### 1. NIO.2の基本概念
- Path インターフェイスの活用
- Files クラスの高レベルAPI
- ファイル属性の効率的な取得

### 2. 高性能ファイル処理
- ストリームベースの処理
- メモリ効率を考慮した大容量ファイル処理
- 並列処理による性能向上

### 3. ファイル監視システム
- WatchService による効率的な監視
- イベント駆動プログラミング
- 非同期処理とスレッド管理

### 4. 実用的なファイル操作
- バックアップとリストア
- データ変換と形式変更
- 整合性チェックとバリデーション

## 実行方法

```bash
# コンパイル
javac *.java

# テスト実行（JUnit 5が必要）
java -cp .:junit-platform-console-standalone-1.8.2.jar org.junit.platform.console.ConsoleLauncher --scan-classpath

# 個別実行例
java -cp . FileProcessorTest
java -cp . CSVHandlerTest
java -cp . BackupSystemTest
java -cp . WatchServiceExampleTest

# ファイル監視のデモ実行
java -cp . WatchServiceExample /path/to/watch/directory
```

## 実用的な活用例

### 1. ログファイル処理システム
```java
// 大容量ログファイルの効率的な処理
List<String> errors = FileProcessor.searchInFile(logFile, "ERROR", false);
CSVHandler.CSVStatistics stats = CSVHandler.generateStatistics(records);
```

### 2. 自動バックアップシステム
```java
// 定期的な増分バックアップ
BackupSystem.BackupConfig config = BackupSystem.BackupConfig.defaultConfig(source, backup);
BackupSystem.BackupResult result = BackupSystem.performIncrementalBackup(config);
```

### 3. リアルタイムファイル監視
```java
// 設定ファイルの変更監視
WatchServiceExample.FileWatcher watcher = new WatchServiceExample.FileWatcher(configDir, config);
watcher.addEventHandler(event -> reloadConfiguration());
```

## パフォーマンス最適化

### 1. メモリ効率
- ストリームベースの処理でメモリ使用量を削減
- バッファサイズの適切な設定
- 大容量ファイルの分割処理

### 2. I/O効率
- BufferedReader/Writer の活用
- NIO.2 による高速ファイル操作
- 並列処理による処理時間短縮

### 3. 監視効率
- WatchService による効率的なイベント検出
- フィルタリングによる不要なイベントの除外
- 非同期処理による UI ブロッキングの回避

## 設計パターン

### オブザーバーパターン
WatchServiceExample では、ファイル変更イベントを複数のハンドラーに通知：
- イベント発生時の柔軟な処理
- 疎結合な設計
- 拡張可能なアーキテクチャ

### ストラテジーパターン
BackupSystem では、異なるバックアップ戦略を実装：
- フルバックアップ vs 増分バックアップ
- 圧縮 vs 非圧縮
- 設定による動作変更

### ファクトリーパターン
CSVHandler では、設定に基づく CSV 処理の生成：
- 異なるフォーマットへの対応
- 設定による処理のカスタマイズ
- 再利用可能なコンポーネント

## 注意点とベストプラクティス

### 1. リソース管理
- try-with-resources による確実なリソース解放
- ファイルハンドルのリーク防止
- 適切な例外処理

### 2. エラーハンドリング
- ファイル操作の例外を適切に処理
- ユーザーフレンドリーなエラーメッセージ
- ログ出力による問題の追跡

### 3. セキュリティ
- パストラバーサル攻撃の防止
- ファイル権限の適切な設定
- 機密情報の漏洩防止

### 4. パフォーマンス
- 大容量ファイルの効率的な処理
- メモリ使用量の監視
- I/O ボトルネックの特定と解決

## 応用展開

1. **ログ監視システム**
   - エラーログの自動検出
   - アラート機能
   - ログローテーション

2. **データ同期システム**
   - ファイル変更の自動同期
   - 差分検出と更新
   - 競合解決

3. **コンテンツ管理システム**
   - ファイルのバージョン管理
   - メタデータ管理
   - 全文検索機能