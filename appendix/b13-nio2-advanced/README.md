# 付録B.13 NIO.2高度機能の詳細解説

JavaのNIO.2の高度な機能を実演するプロジェクトです。ファイルシステムの監視、非同期のファイル入出力、メモリマップドファイル、リアクティブなストリーム処理を紹介します。

## なぜNIO.2が必要なのか

### 従来のI/Oで起こりやすい問題

1. 多数のファイルの処理に時間がかかる
   - ファイルを1つずつ順に処理すると、I/Oの待ち時間のあいだCPUが何もしない
   - NIO.2の非同期I/Oや並列処理を使うと、待ち時間に別のファイルを処理できる

2. ファイルの変更を自前のポーリングで検出する
   - 一定間隔でディレクトリを調べるコードを自分で書くと、間隔とCPU使用率のバランスを取りにくい
   - WatchServiceを使うと、OSの通知機能を使った監視をJavaの標準APIで書ける。ただし、通知の仕組みはOSによって異なる（後述）

3. 大きなファイルを丸ごと読み込むとメモリが足りなくなる
   - `Files.readAllBytes`はファイル全体をヒープに読み込む
   - メモリマップドファイルを使うと、ファイルをOSの仮想メモリに対応づけ、必要な部分だけを読み書きできる

どれだけ速くなるか、どれだけメモリが減るかは、ファイルの大きさや数、ディスク、OSで大きく変わります。
自分の環境では、`benchmark`パッケージのJMHベンチマークで確かめてください。

## 実演機能

### 1. ファイルシステム監視（WatchService）
- ディレクトリの監視
- 短時間に続く変更をまとめるデバウンス（`AdvancedFileWatcher`）
- サブディレクトリを含めた再帰的な監視
- ファイルの種類ごとの処理

### 2. 非同期ファイルI/O
- `AsynchronousFileChannel`によるノンブロッキングなファイル操作
- 並列のファイル処理
- `CompletableFuture`との組み合わせ
- 進捗の通知とキャンセル

### 3. メモリマップドファイル
- ヒープに読み込まずに大きなファイルを処理する
- プロセス間でファイルを共有メモリとして使う
- FFM API（Java 22で正式）の`Arena`でマップし、閉じたときに確実にアンマップする

### 4. リアクティブI/Oストリーム
- Flow APIによるバックプレッシャー
- 使うメモリを抑えたストリーム処理

### 5. パフォーマンスベンチマーク
- JMHによるI/O方式の比較（`IOBenchmark`、`ConcurrentIOBenchmark`）

## クイックスタート

### 前提条件
- Java 25以上
- Maven 3.9以上

### プロジェクトのビルド
```bash
mvn clean package
```

`target/benchmarks.jar`には、このプロジェクトのクラスと依存ライブラリ（SLF4JとJMH）がすべて入っています。
デモもこのJARから実行します。
`target/nio2-advanced-1.0.jar`には依存ライブラリが入っていないため、`java -cp`に指定すると`NoClassDefFoundError: org/slf4j/LoggerFactory`になります。

### サンプルの実行

#### 1. ファイルウォッチャーデモ
```bash
# ディレクトリの変更を監視（recursiveを付けるとサブディレクトリも監視）
java -cp target/benchmarks.jar com.example.nio2.watcher.FileWatcherDemo /path/to/watch recursive

# デバウンス付きの高度なウォッチャー
java -cp target/benchmarks.jar com.example.nio2.watcher.AdvancedWatcherDemo /path/to/watch
```

#### 2. 非同期ファイル処理
```bash
# 複数ファイルを並列処理
java -cp target/benchmarks.jar com.example.nio2.async.AsyncFileProcessorDemo file1.txt file2.txt file3.txt

# 大きなファイルの非同期コピー
java -cp target/benchmarks.jar com.example.nio2.async.AsyncFileCopyDemo source.dat destination.dat
```

#### 3. メモリマップドファイルの例
```bash
# ファイルがなければテスト用のファイルを作ってから、読み込み・検索・行数の計算・書き換えを行う
java -cp target/benchmarks.jar com.example.nio2.memory.MemoryMappedDemo large-file.dat

# 共有メモリの例（同じプロセスの中でproducerとconsumerを動かす）
java -cp target/benchmarks.jar com.example.nio2.memory.SharedMemoryDemo demo
```

#### 4. リアクティブI/Oデモ
```bash
# バックプレッシャー付きで大きなファイルをストリーム
java -cp target/benchmarks.jar com.example.nio2.reactive.ReactiveFileDemo large-file.log
```

#### 5. パフォーマンスベンチマーク
```bash
# ベンチマークの一覧
java -jar target/benchmarks.jar -l

# JMHベンチマークを実行
java -jar target/benchmarks.jar
```

JDK 23以降のjavacは、指定しないと注釈処理を行いません。
JMHはベンチマークの一覧（`META-INF/BenchmarkList`）を注釈処理で作るため、`pom.xml`では`maven-compiler-plugin`の`annotationProcessorPaths`に`jmh-generator-annprocess`を指定しています。

## WatchServiceのOSによる違い

WatchServiceが変更をどう検出するかは、OSとJDKの実装によって異なります。

- Linux
  - inotifyを使い、変更をほぼすぐに通知する
- Windows
  - OSの変更通知を使い、変更をほぼすぐに通知する
- macOS
  - JDKの実装はポーリング（`sun.nio.fs.PollingWatchService`）で、約2秒ごとにディレクトリを調べる。変更から通知まで数秒かかることがあり、短時間に作って消したファイルは通知されないことがある

即座の通知が必要な処理や、OSをまたいで同じ動きを期待するテストでは、この違いに注意してください。

## 各機能を使用すべき場面

### WatchServiceを使用すべき場面
- ファイルシステムの変更を知りたい
- ファイル同期ツールの構築
- 設定ファイルのホットリロード
- バックアップや監視のシステム

### 非同期I/Oを使用すべき場面
- 多くのファイルを同時に処理する
- I/O操作でメインスレッドを止めたくない
- 応答性の高いアプリケーションの構築

### メモリマップドファイルを使用すべき場面
- ヒープより大きなファイルを扱う
- ファイルの内容にランダムにアクセスする
- プロセス間でデータを共有する

### リアクティブストリームを使用すべき場面
- 非常に大きなストリームの処理
- 処理の速さに合わせて読み込みを抑える（バックプレッシャー）必要がある
- イベント駆動のシステムの構築

## アーキテクチャ

### パッケージ構造
- `com.example.nio2.watcher`
  - ファイルシステムの監視
- `com.example.nio2.async`
  - 非同期I/Oの例
- `com.example.nio2.memory`
  - メモリマップドファイルと共有メモリ
- `com.example.nio2.reactive`
  - リアクティブストリームのプロセッサー
- `com.example.nio2.benchmark`
  - JMHベンチマーク
- `com.example.nio2.utils`
  - 共通のユーティリティ

### 使用されるデザインパターン
- Observerパターン
  - WatchServiceによるファイルシステムのイベントの通知
- Futureパターン
  - `CompletableFuture`による非同期操作
- Producer-Consumer
  - バックプレッシャー付きのリアクティブストリーム
- Strategyパターン
  - 差し替えられるファイルプロセッサー

これらを組み合わせた構成の例は`examples/EXAMPLES.md`にあります。

## ベストプラクティス

### WatchService
- イベントを処理したら、必ず`WatchKey`を`reset()`する
- `OVERFLOW`イベントを処理する（イベントが失われた可能性を示す）
- 監視と処理を別々のスレッドで行う
- 短時間に続けて変わるファイルには、デバウンスを入れる

### 非同期I/O
- 完了ハンドラーでエラーを処理する
- 同時に行う操作の数を制限して、リソースを使い切らないようにする
- すべての経路でチャネルを閉じる

### メモリマップドファイル
- 大きなファイルは、必要な部分だけをマップする
- 重要なデータは`force()`でディスクに書き出す
- `FileChannel.map(mode, offset, size)`が返す`MappedByteBuffer`は、標準のAPIではアンマップできず、GCに回収されるまでマッピングが残る。すぐに解放したいときは、`FileChannel.map(mode, offset, size, arena)`で`MemorySegment`としてマップし、`Arena`を閉じる（`MemoryMappedFile`と`SharedMemoryFile`はこの方法を使っている）
- マップできる大きさやファイルのロックの動きはOSによって異なる

### 一般
- 最適化の前にプロファイルする
- 現実的な大きさのデータでテストする

## トラブルシューティング

### よくある問題

1. WatchServiceが変更を検出しない、または遅れる
   - macOSではポーリングのため、通知まで数秒かかる
   - ネットワークドライブなど、監視に対応していないファイルシステムがある
   - 監視するディレクトリの権限を確認する
   - `WatchKey`を`reset()`しているか確認する

2. メモリマップドファイルのエラー
   - ファイルの権限を確認する
   - マップしている間に、別のプロセスがファイルを切り詰めていないか確認する

3. 非同期操作が終わらない
   - 完了ハンドラーの中でのデッドロックを確認する
   - Executorの設定を確認する
   - 捕まえていない例外がないか確認する

## ライセンス

このプロジェクトはJava入門技術書の一部であり、教育目的で提供されています。
