# 付録B.13 NIO.2の高度な機能

第14章で学んだ`Path`と`Files`の裏側にあるしくみ（ファイルシステムとプロバイダー、属性のビュー）と、ファイルの監視・メモリマップドファイル・非同期のファイル入出力・ファイルのロックを、実際に観察しながら学ぶ節です。
NIO.2の機能には、APIの仕様が「OSや実装に任せる」と書いている部分が多くあります。
この節では、仕様が決めていることと、macOSとLinuxのJDKで実際に起きることを分けて示します。

## この節の読み方

この節の解説のうち、事実を述べた段落には`<!-- claim: … -->`という印を付けています（GitHubの画面では見えません）。
印の名前は同じディレクトリの`claims.yml`に対応し、そこにその主張の確かめ方（実行結果、出典）を書いています。
リポジトリのルートで`npm run check:deep-dive -- b13`を実行すると、すべての主張をJDK 25で確かめ直せます。
「観察する」として載せた出力は、この検査がmacOSで実際に出したものです。
OSによって結果が変わる観察は、macOSとLinuxのどちらの結果でも検査に合格するように書き、Linuxの結果はDockerのLinux（`eclipse-temurin:25-jdk`）で確かめました。

## ファイル構成

### 観察用のプログラム（verify/）

| ファイル | 観察すること |
|---|---|
| `FileSystemsTour.java` | プロバイダーの一覧、ZIPファイルシステム、`jrt:/`ファイルシステム |
| `PathBasics.java` | `Path`の要素、`normalize`・`startsWith`・`relativize`・`toRealPath` |
| `FilesExceptions.java` | `java.io.File`の`false`と、`Files`の例外の違い |
| `AttributeViews.java` | ファイルシステムごとに使える属性のビュー |
| `StreamsMustClose.java` | `Files.list`のストリームを閉じないと残るファイル記述子、循環するリンク |
| `AtomicMove.java` | `Files.move`のオプションと、一時ファイルを使った安全な書き換え |
| `WatchServiceImpl.java` | `WatchService`の実装クラス、通知までの時間、`OVERFLOW` |
| `MapModes.java` | `FileChannel.map`の3つのモード |
| `MappedLimits.java` | 2GBの上限と、`MemorySegment`による大きなマッピング |
| `AsyncChannelFacts.java` | `AsynchronousFileChannel`の実装と、完了ハンドラーのスレッド |
| `FileLocks.java` | 同じJVMと別のプロセスでの`FileLock`の動き |
<!-- claim: map-limit, segment-large -->

`verify/`のプログラムはパッケージを持たないので、`verify/`ディレクトリでそのまま実行できます。
ファイルを作る観察は、一時ディレクトリ（`Files.createTempDirectory`）のなかで行い、終わったら消します。
`MappedLimits.java`は、最後の1バイトだけを書いた3GBのファイルを一時ディレクトリに作ります。
ディスクの空きが少ない環境では、実行しないでください。
<!-- claim: segment-large -->

```bash
cd verify
java FileSystemsTour.java
java WatchServiceImpl.java
javac -d out FileLocks.java && java -cp out FileLocks
```

`FileLocks.java`は自分自身を子のプロセスとして起動するので、`javac`でコンパイルしてから実行します。

### 体験用のデモ（src/）

| パッケージ | 内容 |
|---|---|
| `com.example.nio2.watcher` | `WatchService`による監視（`FileWatcher`）、デバウンスとファイルの種類ごとの処理（`AdvancedFileWatcher`） |
| `com.example.nio2.async` | `AsynchronousFileChannel`による読み書きとコピー（`AsyncFileIO`）、複数ファイルの並列処理（`ParallelFileProcessor`） |
| `com.example.nio2.memory` | `Arena`でマップするメモリマップドファイル（`MemoryMappedFile`）、ファイルを共有メモリにしたキュー（`SharedMemoryFile`） |
| `com.example.nio2.reactive` | Flow APIでファイルを少しずつ流す`ReactiveFileReader`と、その処理の例 |
| `com.example.nio2.benchmark` | JMHによる読み込み方法の比較（`IOBenchmark`、`ConcurrentIOBenchmark`） |
| `com.example.nio2.utils` | ディレクトリのコピー・削除、検索、分割と結合などの小さな道具（`FileUtils`） |

デモの実行方法は「11. デモのビルドと実行」にあります。
これらを組み合わせた構成の例は`examples/EXAMPLES.md`にあります。

## 1. PathとFileSystem

### しくみ

`Path`は、ファイルの場所を表す「名前の要素の並び」です。
`Path`はどれかのファイルシステム（`FileSystem`）に属し、ファイルシステムはプロバイダー（`FileSystemProvider`）が作ります。
`Files`のメソッドは、渡された`Path`のプロバイダーに処理を任せます。
そのため、同じ`Files.readString`で、ディスクのファイルもZIPファイルのなかのファイルも読めます。

### 観察する

`verify/FileSystemsTour.java`は、インストールされているプロバイダーと、3種類のファイルシステムを使います。

<!-- output: fs-providers -->
```
プロバイダーのスキーム: [file, jar, jrt]
```
<!-- /output -->

<!-- output: path-default-fs -->
```
Path.ofのファイルシステムは既定のもの: true
既定のスキーム: file
既定のプロバイダーのクラス: sun.nio.fs.MacOSXFileSystemProvider
存在しないファイルのPathも作れる: data.txt exists=false
ZIPから既定のファイルシステムへコピー: ZIPの中のファイル
```
<!-- /output -->

<!-- output: zipfs -->
```
ZIPのスキーム: jar
ZIPのプロバイダーのモジュール: jdk.zipfs
ZIPから読んだ内容: ZIPの中のファイル
ZIPから既定のファイルシステムへコピー: ZIPの中のファイル
```
<!-- /output -->

<!-- output: jrt-fs -->
```
プロバイダーのスキーム: [file, jar, jrt]
jrtのスキーム: jrt
Object.classの先頭4バイト: CAFEBABE
java.baseモジュールがある: true
```
<!-- /output -->

### 説明

標準で入っているプロバイダーは、ディスクのファイルを扱う`file`、ZIPファイルを扱う`jar`、JDK自身のクラスを扱う`jrt`の3つです。
<!-- claim: fs-providers -->

`Path.of`で作る`Path`は、既定のファイルシステム（`FileSystems.getDefault()`）に属します。
`Path`を作るだけではファイルがあるかどうかを確かめないので、まだないファイルの`Path`も作れます。
既定のプロバイダーの実装クラスはOSごとに違い、macOSでは`MacOSXFileSystemProvider`、Linuxでは`LinuxFileSystemProvider`でした。
<!-- claim: path-default-fs -->

ZIPファイルは`FileSystems.newFileSystem(ZIPのPath, 設定)`で開けます。
設定に`"create", "true"`を渡すと新しいZIPファイルを作ります。
開いたファイルシステムの`getPath`で作った`Path`には、`Files.writeString`や`Files.createDirectories`がそのまま使えます。
このプロバイダーは`jdk.zipfs`モジュールにあります。
<!-- claim: zipfs -->

ファイルシステムが違う`Path`どうしは組み合わせられず、`resolve`は`ProviderMismatchException`になります。
一方、`Files.copy`はファイルシステムをまたいでコピーできるので、ZIPのなかのファイルをディスクに取り出すのに使えます。
<!-- claim: provider-mismatch -->

`jrt:/`ファイルシステムは、JDKのモジュールに入っているクラスファイルを読むためのものです（JEP 220、Java 9）。
`/modules/java.base/java/lang/Object.class`を読むと、クラスファイルの目印である`CAFEBABE`から始まるバイト列が得られます。
`javap`のようなツールは、このしくみでJDKのクラスを読みます。
<!-- claim: jrt-fs -->

### Pathは文字列ではない

`verify/PathBasics.java`で、`Path`の操作が要素の単位で行われることを確かめます。

<!-- output: path-elements -->
```
要素の数: 4
normalize後の要素の数: 2
normalize後のファイル名: summary.txt
Path.startsWith("rep"): false
Path.startsWith("reports"): true
String.startsWith("rep"): true
```
<!-- /output -->

- `Path.of("reports", "2026", "..", "summary.txt")`は4つの要素を持ち、`normalize`で「..」とその前の要素が取り除かれる
- `Path`の`startsWith`は要素ごとに比べるので、`reports/2026`は`rep`で始まらない。文字列の`startsWith`とは結果が違う
<!-- claim: path-elements -->

<!-- output: path-relativize -->
```
relativizeの要素: [..][c][d.txt]
resolveで戻すと等しい: true
```
<!-- /output -->

`a/b`から`a/c/d.txt`への相対パスは`../c/d.txt`になり、`a/b`に`resolve`して`normalize`すると元に戻ります。
<!-- claim: path-relativize -->

<!-- output: path-realpath -->
```
toAbsolutePathは成功: true
toRealPath: java.nio.file.NoSuchFileException
```
<!-- /output -->

`toAbsolutePath`は現在のディレクトリを前に付けるだけで、ファイルの有無を確かめません。
`toRealPath`はシンボリックリンクをたどった実際の場所を調べるので、ファイルがないと`NoSuchFileException`になります。
<!-- claim: path-realpath -->

## 2. Filesのメソッドと例外

### しくみ

`java.io.File`のメソッドの多くは、失敗すると`false`を返すだけです。
`Files`のメソッドは、失敗の理由ごとに`IOException`のサブクラスを投げます。

### 観察する

`verify/FilesExceptions.java`で、同じ失敗を両方のAPIで起こします。

<!-- output: file-boolean -->
```
File.delete（ない）: false
File.delete（空でないディレクトリ）: false
File.createNewFile（ある）: false
File.mkdir（ある）: false
```
<!-- /output -->

<!-- output: files-exceptions -->
```
Files.delete（ない）: NoSuchFileException
Files.delete（空でないディレクトリ）: DirectoryNotEmptyException
Files.createFile（ある）: FileAlreadyExistsException
Files.createDirectory（ある）: FileAlreadyExistsException
Files.copy（コピー先がある）: FileAlreadyExistsException
Files.readString（ない）: NoSuchFileException
Files.copy（REPLACE_EXISTING）: 成功
FileSystemExceptionとして捕まえた: NoSuchFileException getFile()のファイル名=missing.txt
Files.deleteIfExists（ない）: false
Files.createDirectories（ある）: sub
```
<!-- /output -->

### 説明

`File.delete`の`false`からは、ファイルがなかったのか、ディレクトリが空でなかったのか、権限がなかったのかがわかりません。
<!-- claim: file-boolean -->

`Files`では、理由が例外の型でわかります。

- ファイルがない
  + `NoSuchFileException`
- 作ろうとした場所にすでにある
  + `FileAlreadyExistsException`（コピー先がある`Files.copy`も同じ。`REPLACE_EXISTING`を付けると置き換える）
- 空でないディレクトリを消そうとした
  + `DirectoryNotEmptyException`
<!-- claim: files-exceptions -->

これらは`FileSystemException`のサブクラスで、`getFile()`で対象のファイルを取り出せます。
<!-- claim: fs-exception-file -->

「なければ何もしない」「あってもよい」という処理には、`Files.deleteIfExists`（なければ`false`を返す）と`Files.createDirectories`（すでにあっても例外にしない）を使います。
<!-- claim: delete-if-exists -->

## 3. ファイルの属性とビュー

### しくみ

ファイルの大きさや更新日時、所有者や許可などの属性は、「ビュー」と呼ぶ単位でまとめられています。
どのファイルシステムにもある基本の属性が`basic`（`BasicFileAttributeView`）で、POSIXの許可は`posix`、Windowsの読み取り専用や隠しファイルの属性は`dos`です。
どのビューが使えるかは、ファイルシステムによって違います。

### 観察する

<!-- output: attr-views -->
```
OS: Mac OS X
既定のファイルシステムのビュー: [basic, owner, posix, unix, user]
ZIPのビュー: [basic, zip]
jrtのビュー: [basic, jrt]
```
<!-- /output -->

### 説明

`basic`はどのファイルシステムでも使えます。
macOSの既定のファイルシステムでは`basic`・`owner`・`posix`・`unix`・`user`が使え、ZIPでは`basic`と`zip`、`jrt`では`basic`と`jrt`です。
<!-- claim: attr-views -->

Linux（Docker）では、これに加えて`dos`ビューも使えました。
同じJavaのコードでも、使える属性はOSで変わります。
<!-- claim: attr-views-linux -->

使えないビューを`Files.getFileAttributeView(path, PosixFileAttributeView.class)`のようにクラスで求めると、例外ではなく`null`が返ります（ZIPのなかのファイルで確かめた）。
`"nosuchview:size"`のように名前で存在しないビューを指定すると、`UnsupportedOperationException`になります。
POSIXの許可を扱うコードは、`null`かどうかを確かめるか、`supportedFileAttributeViews()`に`"posix"`があるかを先に調べます。
<!-- claim: attr-null-view -->

<!-- output: attr-by-name -->
```
既定のファイルシステムのビュー: [basic, owner, posix, unix, user]
ZIPの中のファイルのposixビュー: null
basic: size=5 regularFile=true directory=false
Files.getAttribute("size"): 5
Files.readAttributes("basic:size,isDirectory"): [isDirectory, size]
posixビュー: あり / dosビュー: null
posixの許可: rw-------
```
<!-- /output -->

属性は、`Files.readAttributes(path, BasicFileAttributes.class)`のように型で読むほか、`"ビュー名:属性名"`の文字列でも読めます。
ビュー名を省くと`basic`になります。
<!-- claim: attr-by-name -->

## 4. ディレクトリのストリームは閉じる

### しくみ

`Files.list`・`Files.walk`・`Files.find`が返す`Stream`は、中で開いたディレクトリを持っています。
APIドキュメントは、これらをtry-with-resourcesのなかで使い、ストリームの処理が終わったらすぐに閉じるよう求めています。
一方、ファイルの中身を読む`Files.readAllLines`や`Files.readString`は、メソッドのなかで閉じるので閉じる必要はありません。
<!-- claim: list-must-close, read-closes -->

### 観察する

`verify/StreamsMustClose.java`は、`Files.list`を閉じずに100回呼び、開いているファイル記述子の数を比べます（UnixのOSで使える`UnixOperatingSystemMXBean`で数えます）。

<!-- output: list-must-close -->
```
閉じずに100回Files.listしたあとの増分: 200
closeしたあとの増分: 0
```
<!-- /output -->

### 説明

`count()`のような終端操作を呼んでも、ストリームは閉じられません。
閉じるまでファイル記述子が残り、`close()`を呼ぶと元に戻ります（この環境では1回の`Files.list`で2つ増えました）。
ディレクトリを何度も走査する長く動くプログラムで閉じ忘れると、やがて「Too many open files」で失敗します。
<!-- claim: list-must-close -->

ディレクトリの要素を返す順序は決まっていません。
決まった順序で表示したいときは、`sorted()`で並べます。
<!-- claim: list-order -->

<!-- output: walk-loop -->
```
名前順に並べた一覧: [a.txt, b.txt, c.txt]
FOLLOW_LINKSで循環: UncheckedIOException（原因 FileSystemLoopException）
FOLLOW_LINKSなしの要素数: 6
```
<!-- /output -->

`Files.walk`は、既定ではシンボリックリンクをたどりません。
`FileVisitOption.FOLLOW_LINKS`を付けると、親のディレクトリを指すリンクのような循環を見つけて`FileSystemLoopException`を報告します。
ストリームの処理の途中で起きた入出力の例外は`IOException`のままでは投げられないので、`UncheckedIOException`に包まれて届きます。
<!-- claim: walk-loop -->

## 5. ファイルを安全に書き換える（ATOMIC_MOVE）

### しくみ

設定ファイルのように、ほかのプログラムが読むファイルを書き換えるとき、元のファイルに直接書くと、書きかけの内容を読まれることがあります。
よく使われるのは、同じディレクトリの一時ファイルに全部を書いてから、`Files.move`で元の名前に移す方法です。

### 観察する

<!-- output: move-options -->
```
オプションなし: FileAlreadyExistsException
REPLACE_EXISTING: version=2
ATOMIC_MOVE（移動先あり）: 置き換わった version=3
一時ファイルは残っていない: true
```
<!-- /output -->

### 説明

`Files.move`は、オプションなしでは移動先があると`FileAlreadyExistsException`になり、`REPLACE_EXISTING`を付けると置き換えます。
<!-- claim: move-options -->

`ATOMIC_MOVE`を付けると、移動を1つの不可分な操作として行います。
読む側には、古い内容か新しい内容のどちらかだけが見え、書きかけは見えません。
ただし、移動先がすでにあるときに置き換えるか例外にするかは、仕様では実装に任されています。
macOSとLinuxでは置き換わりました。
<!-- claim: atomic-move-replace -->

別のファイルシステムへの移動のように、不可分な操作として移動できないときは`AtomicMoveNotSupportedException`になります。
一時ファイルを元のファイルと同じディレクトリに作るのはこのためです。
<!-- claim: atomic-move-unsupported -->

## 6. ファイルの変更を監視する（WatchService）

### しくみ

`WatchService`は、ディレクトリに登録しておくと、ファイルの作成・変更・削除を`WatchEvent`として知らせます。
基本の流れは次のとおりです。

1. `FileSystems.getDefault().newWatchService()`で作る
2. `dir.register(ws, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)`でディレクトリを登録し、`WatchKey`を受け取る
3. `ws.take()`（待つ）か`ws.poll(時間)`で、イベントのある`WatchKey`を取り出す
4. `key.pollEvents()`でイベントを読み、処理する
5. `key.reset()`で、次のイベントを受け取れる状態に戻す（忘れると以後のイベントが届かない）

監視はディレクトリ単位で、サブディレクトリのなかの変更は知らせません。
サブディレクトリも監視するには、`Files.walkFileTree`で見つけたディレクトリをそれぞれ登録し、新しく作られたディレクトリも登録します（`FileWatcher`の`recursive`の処理）。
<!-- claim: watch-dir-only -->

APIドキュメントは、OSに変更通知の機能があればそれを使い、なければポーリングのような単純な方法を使う、と書いています。
実際にどの方法を使うかは、OSとJDKの実装によって決まります。

### 観察する

`verify/WatchServiceImpl.java`は、実装クラスを表示し、ファイルを作ってからイベントが届くまでの時間と、作ってすぐ消したファイルのイベントを調べます。

<!-- output: watch-impl -->
```
WatchServiceの実装: sun.nio.fs.PollingWatchService
最初のイベント: [ENTRY_CREATE first.txt]（100ミリ秒以上、約2秒）
作ってすぐ消したファイルのイベント: なし
```
<!-- /output -->

同じプログラムをLinux（Docker）で実行すると、次のようになりました。

```
WatchServiceの実装: sun.nio.fs.LinuxWatchService
最初のイベント: [ENTRY_CREATE first.txt]（100ミリ秒未満）
作ってすぐ消したファイルのイベント: [ENTRY_CREATE flash.txt, ENTRY_DELETE flash.txt]
```

### 説明

macOSのJDKの実装は`PollingWatchService`で、ディレクトリを一定の間隔で調べて前回との違いをイベントにします。
この環境では、ファイルを作ってから通知まで約2秒かかり、2回の調査のあいだに作って消したファイルは通知されませんでした。
Linuxの実装は`LinuxWatchService`で、OSの変更通知（inotify）を使い、すぐに通知が届き、作成と削除の両方が届きます。
<!-- claim: watch-impl -->

この違いから、次のことがいえます。

- 監視の結果だけを頼りにファイルの状態を決めない。イベントを受け取ったら、そのファイルが今どうなっているかを`Files.exists`などで確かめる
- イベントが届くまでの時間をテストで決め打ちしない。macOSでは数秒かかる
- ファイルを作って書き込むと、作成のあとに変更が続くことが多い。短い時間に続くイベントはまとめる（デバウンス）。`AdvancedFileWatcher`は、作成のあとに続く変更を「作成」のまままとめる

### イベントがたまりすぎたとき（OVERFLOW）

<!-- output: watch-overflow -->
```
520個作ったときのイベント: 0件、OVERFLOW=true
```
<!-- /output -->

イベントを取り出さないうちに多数の変更が起きると、`WatchKey`にたまっていたイベントは捨てられ、`OVERFLOW`だけが残ります。
この実装では、500個のファイルを作ったときは500件すべてが届き、520個では1件も届かず`OVERFLOW`だけになりました（macOSとLinuxで同じ）。
`OVERFLOW`を受け取ったら、イベントを失ったものとして、ディレクトリ全体を調べ直します。
<!-- claim: watch-overflow -->

ネットワークドライブのような、ローカルの記憶装置にないファイルの変更を検出できるかどうかは、実装によって決まります。
<!-- claim: watch-remote -->

## 7. FileChannelとメモリマップドファイル

### しくみ

`FileChannel.map`は、ファイルの一部をプロセスの仮想メモリに対応づけ（マップし）、`MappedByteBuffer`として返します。
バッファーを読むとOSがファイルの内容をメモリに読み込み、書くとOSがいずれファイルに書き戻します。
ファイルの内容をJavaのヒープにコピーしないので、大きなファイルの一部を何度も読み書きするのに向きます。

マップの仕方は3つのモードから選びます。

- `READ_ONLY`
  + 読み取り専用
- `READ_WRITE`
  + 書いた内容がファイルに反映される
- `PRIVATE`
  + コピーオンライト。書いた内容はこのバッファーだけのもので、ファイルには反映されない

### 観察する

<!-- output: map-modes -->
```
READ_WRITEで書いたあとのファイルの先頭: A
PRIVATEのバッファーの先頭: B / ファイルの先頭: A
READ_ONLYのisReadOnly: true
READで開いてREAD_WRITEでマップ: NonWritableChannelException
```
<!-- /output -->

<!-- output: map-extend -->
```
マップ前の大きさ: 0
16バイトをREAD_WRITEでマップしたあとの大きさ: 16
```
<!-- /output -->

### 説明

`READ_WRITE`で書いた`A`はファイルに反映され、`PRIVATE`で書いた`B`は反映されませんでした。
`READ_WRITE`でマップするには、チャネルを読み書きの両方で開く必要があり、読み取り専用で開いたチャネルでは`NonWritableChannelException`になります。
<!-- claim: map-modes -->

ファイルより大きな範囲をマップしたときの動きは、仕様では決まっていません（unspecified）。
この環境では、空のファイルを`READ_WRITE`で16バイトマップすると、ファイルが16バイトに伸びました。
これに頼らず、必要な大きさは先に`setLength`や書き込みで確保しておくのが安全です。
<!-- claim: map-extend -->

### 2GBの壁

`MappedByteBuffer`は`ByteBuffer`の一種で、位置と大きさを`int`で表します。
`verify/MappedLimits.java`は3GBのファイルを作り、マップを試します。
<!-- claim: map-limit -->

<!-- output: map-limit -->
```
ファイルの大きさ: 3221225472バイト
map(Integer.MAX_VALUE + 1): IllegalArgumentException: Size exceeds Integer.MAX_VALUE
map(Integer.MAX_VALUE)のcapacity: 2147483647
```
<!-- /output -->

1回の`map`でマップできるのは`Integer.MAX_VALUE`バイト（約2GB）までで、それを超えると`IllegalArgumentException`になります。
2GBを超えるファイルを`MappedByteBuffer`で扱うには、2GB以下の範囲に分けてマップします。
<!-- claim: map-limit -->

`Files.readAllBytes`は、ファイル全体を1つの`byte`の配列に読むので、同じ理由で2GBを超えるファイルを読めません。
3GBのファイルでは、配列を作る前に`OutOfMemoryError`（Required array size too large）になりました。
<!-- claim: readallbytes-limit -->

### マッピングはいつ解放されるか

`MappedByteBuffer`のマッピングは、マップに使ったチャネルを閉じても有効なままで、バッファーがGCに回収されるまで残ります。
`MappedByteBuffer`には、すぐにアンマップするpublicのメソッドがありません。
大きなファイルを次々にマップすると、GCが動くまで仮想メモリを使い続けます。
<!-- claim: mapped-gc -->

## 8. FFM APIのMemorySegmentでマップする

### しくみ

Java 22で正式な機能になったFFM API（Foreign Function & Memory API、JEP 454）には、ヒープの外のメモリを表す`MemorySegment`と、その寿命を管理する`Arena`があります。
`FileChannel.map(mode, offset, size, arena)`は、ファイルを`MemorySegment`としてマップし、`Arena`を閉じたときに確実にアンマップします。
<!-- claim: ffm-final -->

### 観察する

<!-- output: segment-large -->
```
MemorySegmentの大きさ: 3221225472バイト
最後のバイト: 42
途中のバイト（書いていない部分）: 0
```
<!-- /output -->

<!-- output: segment-asbytebuffer -->
```
3GBのsegment.asByteBuffer(): IllegalStateException: Segment is too large to wrap as ByteBuffer. Size: 3221225472
```
<!-- /output -->

<!-- output: arena-closed -->
```
Arenaを閉じたあとのアクセス: IllegalStateException: Already closed
```
<!-- /output -->

### 説明

`MemorySegment`は大きさと位置を`long`で表すので、3GBのファイルを1回でマップし、最後のバイトを読めます。
書いていない途中の部分は0として読めます。
<!-- claim: segment-large -->

2GBを超えるセグメントは`ByteBuffer`にできません。
`asByteBuffer`のAPIドキュメントはこのとき`UnsupportedOperationException`になると書いていますが、JDK 25.0.2で実行すると`IllegalStateException`になりました。
どちらにしても、2GBを超える範囲は`MemorySegment`のメソッド（`get(ValueLayout.JAVA_BYTE, 位置)`など）で扱います。
<!-- claim: segment-asbytebuffer -->

`try (Arena arena = Arena.ofConfined()) { … }`のブロックを出ると、マッピングはその場で解放されます。
解放したあとにセグメントを使うと`IllegalStateException`になり、解放済みのメモリを読んでしまうことはありません。
`src/`の`MemoryMappedFile`と`SharedMemoryFile`は、この方法でマップしています。
<!-- claim: arena-closed -->

## 9. AsynchronousFileChannel

### しくみ

`AsynchronousFileChannel`の`read`・`write`は、すぐに戻り、終わったら`Future`か完了ハンドラー（`CompletionHandler`）で結果を知らせます。
`FileChannel`と違って現在位置を持たないので、読み書きのたびにファイルの位置を渡します。
<!-- claim: async-position -->

### 観察する

<!-- output: async-impl -->
```
実装クラス: sun.nio.ch.SimpleAsynchronousFileChannelImpl
既定のスレッドプールでの完了ハンドラー: 読んだバイト数=5 mainスレッドか=false デーモンか=true スレッドプールのタスクの中か=true
```
<!-- /output -->

<!-- output: async-executor -->
```
自分のExecutorServiceを渡したときのスレッド: my-io-thread
```
<!-- /output -->

<!-- output: async-append -->
```
APPENDを指定: UnsupportedOperationException: APPEND not allowed
```
<!-- /output -->

### 説明

macOSとLinuxの実装クラスは`SimpleAsynchronousFileChannelImpl`でした。
完了ハンドラーはmainではなくスレッドプールのデーモンスレッドで動き、呼び出し元をたどると、スレッドプールが実行するタスクのなかから呼ばれていました。
これらのOSでは、読み書きそのものはスレッドプールのスレッドがふつうの（待つ）入出力で行い、終わったらそのスレッドで完了ハンドラーを呼びます。
「非同期」はOSの機能ではなく、呼び出したスレッドを待たせないという意味だと考えるとよいでしょう。
<!-- claim: async-impl -->

スレッドプールを指定しないと、ほかのチャネルと共有されることもある既定のスレッドプールが使われます。
`AsynchronousFileChannel.open(path, オプションのSet, ExecutorService)`で自分のスレッドプールを渡すと、完了ハンドラーはそのスレッドで動きます。
完了ハンドラーのなかで長い処理をすると、そのスレッドプールのほかの入出力が待たされます。
<!-- claim: async-executor -->

`AsynchronousFileChannel`は`APPEND`を指定できず、`UnsupportedOperationException`になります。
追記するときは、`size()`で得たファイルの末尾の位置を渡して書きます（`AsyncFileIO.appendFileAsync`）。
<!-- claim: async-append -->

## 10. ファイルのロック（FileLock）

### しくみ

`FileChannel.lock`・`tryLock`は、ファイルの範囲を排他ロック（書き込み用）または共有ロック（読み取り用）します。
APIドキュメントは、このロックがOSのロックの機能にそのまま対応し、JVM全体のものとして持たれると書いています。
同じJVMのなかのスレッドどうしの排他には向きません。

### 観察する

<!-- output: lock-jvm -->
```
排他ロック: isShared=false isValid=true
同じJVMの別のチャネルで重なる範囲: OverlappingFileLockException
重ならない範囲: ロックできた
```
<!-- /output -->

<!-- output: lock-process -->
```
子のプロセス（ロック中）: tryLockはnull
解放後: isValid=false
子のプロセス（解放後）: ロックできた
```
<!-- /output -->

<!-- output: lock-advisory -->
```
ロック中の範囲へ、ロックを取らずに書き込み: 3バイト書けた
```
<!-- /output -->

### 説明

同じJVMのなかで重なる範囲をロックしようとすると、別のチャネルからでも、待たずに`OverlappingFileLockException`になります。
同じJVMのスレッドどうしは、`synchronized`や`ReentrantLock`で排他し、そのうえで`FileLock`でプロセス間を排他します（`SharedMemoryFile`はこの形）。
<!-- claim: lock-jvm -->

別のプロセス（子として起動したJVM）からの`tryLock`は、ロックされている間は`null`を返し、解放すると成功しました。
`FileLock`が役に立つのは、プロセスどうしの排他です。
<!-- claim: lock-process -->

ロックが、ロックを取らないプログラムの読み書きまで止めるかどうかは、OSによって決まります（仕様では決まっていない）。
macOSとLinuxでは、ロック中の範囲にロックを取らずに書き込めました。
これらのOSでは、ファイルを使うすべてのプログラムがロックを取るという約束を守って、はじめて排他になります。
<!-- claim: lock-advisory -->

共有ロックを取るには、読み取りのできるチャネルが要り、書き込み専用で開いたチャネルでは`NonReadableChannelException`になります。
<!-- claim: lock-shared-readable -->

OSによっては、同じファイルのチャネルを1つ閉じただけで、そのJVMが持つそのファイルのロックがすべて解放されます。
ロックしている間は、同じファイルを別のチャネルで開いて閉じる処理を避けます。
<!-- claim: lock-close-releases -->

## 11. デモのビルドと実行

### 前提条件

- Java 25以上
- Maven 3.9以上

### ビルド

```bash
mvn clean package
```

`target/benchmarks.jar`には、このプロジェクトのクラスと依存ライブラリ（SLF4JとJMH）がすべて入っています。
デモもこのJARから実行します。
`target/nio2-advanced-1.0.jar`には依存ライブラリが入っていないため、`java -cp`に指定すると、ログを使うクラスで`NoClassDefFoundError`になります（原因は`org/slf4j/LoggerFactory`が見つからないこと）。

JDK 25のjavacは、注釈処理器のパス（`--processor-path`）などで注釈処理を明示しないと、注釈処理を行いません。
JMHはベンチマークの一覧（`META-INF/BenchmarkList`）を注釈処理で作るため、`pom.xml`では`maven-compiler-plugin`の`annotationProcessorPaths`に`jmh-generator-annprocess`を指定しています。
<!-- claim: javac-proc-default -->

### 実行

```bash
# ディレクトリの変更を監視（recursiveを付けるとサブディレクトリも監視）
java -cp target/benchmarks.jar com.example.nio2.watcher.FileWatcherDemo /path/to/watch recursive

# デバウンス付きのウォッチャー
java -cp target/benchmarks.jar com.example.nio2.watcher.AdvancedWatcherDemo /path/to/watch

# 複数ファイルを並列に処理
java -cp target/benchmarks.jar com.example.nio2.async.AsyncFileProcessorDemo file1.txt file2.txt

# 非同期のコピー、書き込み、読み込み、追記
java -cp target/benchmarks.jar com.example.nio2.async.AsyncFileCopyDemo source.dat destination.dat

# ファイルがなければテスト用のファイルを作り、読み込み・検索・行数の計算・書き換えを行う
java -cp target/benchmarks.jar com.example.nio2.memory.MemoryMappedDemo large-file.dat

# 共有メモリのキュー（同じプロセスの中でproducerとconsumerを動かす）
java -cp target/benchmarks.jar com.example.nio2.memory.SharedMemoryDemo demo

# バックプレッシャー付きでファイルを少しずつ処理
java -cp target/benchmarks.jar com.example.nio2.reactive.ReactiveFileDemo large-file.dat

# JMHのベンチマークの一覧と実行
java -jar target/benchmarks.jar -l
java -jar target/benchmarks.jar IOBenchmark
```

`SharedMemoryDemo`は、`producer`と`consumer`を別々のターミナルで起動すると、プロセス間でファイルを共有メモリとして使う様子を見られます。

### ベンチマークについて

`IOBenchmark`は、`FileInputStream`、`BufferedInputStream`、`FileChannel`（ヒープとダイレクトのバッファー）、`MappedByteBuffer`、`Files.readAllBytes`で、1MB・10MB・100MBのファイル全体を読む時間を比べます。
`ConcurrentIOBenchmark`は、複数のファイルを順に処理する場合と、並列ストリーム・`CompletableFuture`・`AsynchronousFileChannel`で処理する場合を比べます。
結果は、ファイルの大きさ、OSのファイルキャッシュ、ディスク、CPUで大きく変わるので、この節には数値を載せていません。
自分の環境で実行して確かめてください。

## よくある誤解

- `Path.of("x")`を作ると、ファイルがあるかどうかが確かめられる
  - `Path`は名前の並びで、作るだけではファイルを調べない。`toRealPath`や`Files`のメソッドで初めて調べる（1）
  <!-- claim: path-default-fs, path-realpath -->
- `Path`の`startsWith`は文字列の前方一致である
  - 要素ごとに比べる。`reports/2026`は`rep`で始まらない（1）
  <!-- claim: path-elements -->
- `File.delete()`が`false`を返したら、ファイルはなかった
  - 空でないディレクトリや権限の不足でも`false`になる。理由を知りたいなら`Files.delete`を使う（2）
  <!-- claim: file-boolean, files-exceptions -->
- `Files.list`のストリームは、`forEach`などの終端操作が終われば閉じる
  - 閉じない。try-with-resourcesで閉じる（4）
  <!-- claim: list-must-close -->
- `Files.list`はファイルを名前順に返す
  - 順序は決まっていない。必要なら`sorted()`で並べる（4）
  <!-- claim: list-order -->
- `WatchService`は、どのOSでもすぐに、すべての変更を知らせる
  - macOSではポーリングで約2秒かかり、短い間の作成と削除は届かない。どのOSでも、イベントがたまりすぎると`OVERFLOW`だけになる（6）
  <!-- claim: watch-impl, watch-overflow -->
- `WatchService`にディレクトリを登録すれば、サブディレクトリの変更も届く
  - 登録したディレクトリの直下だけである。サブディレクトリはそれぞれ登録する（6）
  <!-- claim: watch-dir-only -->
- メモリマップドファイルなら、どんなに大きなファイルも1つのバッファーで扱える
  - `MappedByteBuffer`は2GBまで。それより大きい範囲は分けてマップするか、`MemorySegment`を使う（7、8）
  <!-- claim: map-limit, segment-large -->
- `FileChannel`を閉じれば、マップしたメモリも解放される
  - `MappedByteBuffer`のマッピングはGCに回収されるまで残る。すぐに解放するには`Arena`でマップして閉じる（7、8）
  <!-- claim: mapped-gc, arena-closed -->
- `AsynchronousFileChannel`はOSの非同期I/Oを使うので、スレッドを使わない
  - macOSとLinuxでは、スレッドプールのスレッドが入出力を行い、そのスレッドで完了ハンドラーを呼ぶ（9）
  <!-- claim: async-impl -->
- `FileLock`で同じJVMのスレッドどうしを排他できる
  - 重なる範囲をロックすると`OverlappingFileLockException`になる。スレッドの排他には`synchronized`などを使う（10）
  <!-- claim: lock-jvm -->
- ファイルをロックすれば、ほかのプログラムはそのファイルに書き込めない
  - macOSとLinuxでは、ロックを取らない書き込みは止められない（10）
  <!-- claim: lock-advisory -->

## 参考資料

- APIドキュメント（Java SE 25）
  - `java.nio.file.Path`、`Files`、`FileSystem`、`FileSystems`、`DirectoryStream`、`WatchService`
  - `java.nio.file.attribute`パッケージ
  - `java.nio.channels.FileChannel`、`FileLock`、`AsynchronousFileChannel`
  - `java.nio.MappedByteBuffer`
  - `java.lang.foreign.MemorySegment`、`Arena`
  - `jdk.zipfs`モジュール（ZIPファイルシステムの設定）
  - `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/nio/file/package-summary.html`
- JEP
  - JEP 220: Modular Run-Time Images（`jrt:/`ファイルシステム、`https://openjdk.org/jeps/220`）
  - JEP 454: Foreign Function & Memory API（`https://openjdk.org/jeps/454`）
