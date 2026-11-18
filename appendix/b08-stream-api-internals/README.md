# 付録B.08 Stream APIの内部実装

第12章で学んだStream APIが、内部でどのように要素を流し、並列ストリームでどう仕事を分けているかを、実際に観察しながら学ぶ節です。
Stream API（`java.util.stream`）はJava 8で加わりました（JEP 107）。
<!-- claim: stream-api-since-8 -->

## この節の読み方

この節の解説のうち、事実を述べた段落には`<!-- claim: … -->`という印を付けています（GitHubの画面では見えません）。
印の名前は同じディレクトリの`claims.yml`に対応し、そこにその主張の確かめ方（プログラムの実行結果、コンパイラーの出力、出典）を書いています。
リポジトリのルートで`npm run check:deep-dive -- b08`を実行すると、すべての主張をJDK 25で確かめ直せます。
「観察する」の欄に載せた出力は、この検査が実際に出したものです。

APIドキュメントが約束している動作と、OpenJDKの実装がたまたまそうなっている動作は分けて書きます。
クラス名（`ReferencePipeline$Head`など）や分割の大きさは実装の話で、版が変われば変わることがあります。

## ファイル構成

### 観察用のプログラム（verify/）

| ファイル | 観察すること |
|---|---|
| `PipelineStages.java` | 段のオブジェクト、遅延評価、ストリームの再利用 |
| `SinkChain.java` | 終端操作から各段の処理が呼ばれる道筋（呼び出し履歴） |
| `EvaluationOrder.java` | 要素の流れる順序、短絡、`sorted`・`distinct`・`count` |
| `SpliteratorTraits.java` | ソースごとのSpliteratorの特性と、中間操作による変化 |
| `SplitShape.java` | `trySplit`による分割の形 |
| `CommonPool.java` | 並列ストリームを動かすスレッドと共通プールの並列度 |
| `ParallelPitfalls.java` | 並列での順序、`reduce`の約束、副作用 |
| `CollectorTraits.java` | コレクターの特性と、並列の`collect`で作られる入れ物の数 |
| `NewOperations.java` | `toList`・`mapMulti`・Stream Gatherers |
| `compile-errors/` | 古い`--release`ではコンパイルできないことを確かめるプログラム |

`verify/`のプログラムはパッケージを持たないので、`verify/`ディレクトリでそのままコンパイル・実行できます。

```bash
cd verify
javac EvaluationOrder.java && java EvaluationOrder
javac CommonPool.java && java -XX:ActiveProcessorCount=4 CommonPool
```

`-XX:ActiveProcessorCount=4`は、JVMに「使えるプロセッサーは4つ」と思わせるオプションです。
並列ストリームの分け方はプロセッサーの数で変わるので、検査ではこのオプションで条件をそろえています。

### 体験用のデモ（src/）

- `CustomSpliterator.java`
  - 範囲を半分ずつ分ける`Spliterator`を自作し、`StreamSupport.stream`でストリームにする
  - `Spliterator.OfInt`を実装して、ボクシングなしの`IntStream`にする
- `CustomCollectors.java`
  - `Collector`の4つの関数（supplier・accumulator・combiner・finisher）と特性を自作して学ぶ
  - 文字列の結合（`Collectors.joining`と同じ働き）、ヒストグラム、1回の走査での統計、上位K件
- `ParallelStreamOptimization.java`
  - データの大きさごとに逐次と並列の時間を比べる、自作の`ForkJoinPool`のなかで動かすなど
  - I/O待ちは`Thread.sleep`で真似ている（**シミュレーション**）

```bash
javac -d out src/main/java/com/example/streams/*.java
java -cp out com.example.streams.CustomSpliterator
java -cp out com.example.streams.CustomCollectors
java -cp out com.example.streams.ParallelStreamOptimization
```

デモが表示する時間は、CPU・OS・JVMの状態で大きく変わり、実行のたびにも変わります。
この節では時間の数値を載せません。
正確に比べるにはJMH（Java Microbenchmark Harness）を使います。

## 1. パイプラインの構造

APIドキュメントは、ストリームのパイプラインを「ソース」「0個以上の中間操作」「1つの終端操作」からなるものと定めています。
OpenJDKの実装では、中間操作を呼ぶたびに、前の段につながった新しい段のオブジェクトが作られます。
`verify/PipelineStages.java`は、各段のクラス名を表示します。

<!-- output: pipeline-stages -->
```
段1 stream(): java.util.stream.ReferencePipeline$Head
段2 filter(): java.util.stream.ReferencePipeline$2
段3 map(): java.util.stream.ReferencePipeline$3
段4 sorted(): java.util.stream.SortedOps$OfRef
filterは同じオブジェクトを返すか: false
```
<!-- /output -->

コレクションの`stream()`が返すのは先頭の段（`ReferencePipeline$Head`）で、`filter`・`map`はそれぞれ別の匿名クラスの段を返します。
`sorted`は`SortedOps`という別のクラスの段です。
中間操作は元のストリームを書き換えず、新しい段を返します。
<!-- claim: pipeline-stages -->

### 遅延評価

中間操作を呼んだだけでは、渡したラムダ式は1度も呼ばれません。
終端操作（ここでは`toList()`）を呼んだときに、はじめてソースから要素を読み始めます。

<!-- output: lazy-intermediate -->
```
--- 中間操作だけをつなぐ ---
（ここまでfilterのラムダ式は呼ばれていない）
--- 終端操作toList()を呼ぶ ---
  filter: stream
  filter: java
  filter: lambda
  filter: api
結果: [stream, java, lambda, api]
```
<!-- /output -->

<!-- claim: lazy-intermediate -->

### ストリームは1回しか使えない

終端操作を実行したストリームは「使い終わった」ものになり、もう一度操作すると`IllegalStateException`になります。
中間操作を呼んだあとの元の段（上の`head`）も、もう使えません。
同じデータをもう一度処理するときは、ソース（コレクションなど）から新しいストリームを作ります。

<!-- output: stream-consumed -->
```
IllegalStateException: stream has already been operated upon or closed
古い段に中間操作: IllegalStateException: stream has already been operated upon or closed
```
<!-- /output -->

<!-- claim: stream-consumed -->

## 2. 要素は1つずつ縦に流れる

`filter`→`map`→`forEach`というパイプラインは、全要素に`filter`をかけてから全要素に`map`をかける、という順には動きません。
1つの要素がパイプラインの最後まで流れてから、次の要素に移ります。
`verify/EvaluationOrder.java`の[1]は、各段の手前に`peek`をはさんで順序を表示します。

<!-- output: vertical-order -->
```
[1] filter → map → forEach（1要素ずつ縦に流れる）
  filter前: b
  map前:    b
  forEach:  B
  filter前: c
  filter前: a
  map前:    a
  forEach:  A
```
<!-- /output -->

`filter`で落とされた`c`は、`map`にも`forEach`にも届きません。
APIドキュメントは、この形で`filter`・`map`・集計を「データの1回の走査にまとめられる」と説明しています。
途中の結果を入れるリストは作られません。
<!-- claim: vertical-order -->

`peek`は、パイプラインのある地点を流れる要素を見るための、主にデバッグ用のメソッドです。
処理の本体を`peek`に書かないようにします（後の`count`の例のように、呼ばれないことがあります）。
<!-- claim: peek-for-debugging -->

### Sinkの連鎖（実装の工夫）

この「縦の流れ」を、OpenJDKは`Sink`（受け取り口）の連鎖で実現しています。
終端操作を呼ぶと、各段が「次の段へ要素を渡す`Sink`」を作り、それを後ろからつないでから、ソースの`Spliterator`の`forEachRemaining`で要素を1つずつ先頭の`Sink`に渡します。
`verify/SinkChain.java`は、`map`のラムダ式のなかから呼び出し履歴を表示します。

<!-- output: sink-chain -->
```
  java.util.stream.ReferencePipeline$3$1.accept
  java.util.stream.ReferencePipeline$2$1.accept
  java.util.ArrayList$ArrayListSpliterator.forEachRemaining
  java.util.stream.AbstractPipeline.copyInto
  java.util.stream.AbstractPipeline.wrapAndCopyInto
  java.util.stream.ReduceOps$ReduceOp.evaluateSequential
  java.util.stream.ReduceOps$5.evaluateSequential
  java.util.stream.ReduceOps$5.evaluateSequential
  java.util.stream.AbstractPipeline.evaluate
  java.util.stream.ReferencePipeline.count
count = 2
```
<!-- /output -->

下から読むと、`count`（終端操作）から`copyInto`を経て`ArrayList`の`forEachRemaining`が呼ばれています。
そのなかで`filter`の段の`Sink`（`ReferencePipeline$2$1`）の`accept`が呼ばれ、そのなかから`map`の段の`Sink`（`ReferencePipeline$3$1`）の`accept`が呼ばれています。
要素は、メソッドの呼び出しの入れ子として次の段へ渡されます。
<!-- claim: sink-chain -->

## 3. 短絡

`findFirst`・`anyMatch`・`allMatch`のような終端操作は、答えが決まった時点で残りの要素を読みません。
`limit`は、無限のストリームからも有限のストリームを作れる中間操作です。
APIドキュメントはこれらを「短絡（short-circuiting）」の操作と呼んでいます。

<!-- output: short-circuit -->
```
[2] findFirst（見つかったら残りを読まない）
  読んだ: x1
  読んだ: y2
  結果: y2
[3] 無限のストリームもlimitで止まる
  生成: 1
  生成: 2
  生成: 3
  結果: [1, 4, 9]
[4] anyMatch / allMatch（答えが決まった時点で止まる）
  anyMatchが見た: 1
  anyMatchが見た: 3
  anyMatchが見た: 4
  allMatchが見た: 1
  allMatchが見た: 3
  allMatchが見た: 4
  anyMatch=true allMatch=false
```
<!-- /output -->

`findFirst`は2つ目の要素で見つかったので、3つ目以降を読みません。
`Stream.iterate`は無限のストリームですが、`limit(3)`があるので3つ作ったところで止まります。
`anyMatch`は最初の偶数（4）で`true`に、`allMatch`は最初の偶数で`false`に決まります。
<!-- claim: short-circuit -->

## 4. 全要素を待つ中間操作

`filter`や`map`は、要素を1つ見れば処理できます（状態を持たない中間操作）。
`sorted`は、すべての要素を見るまで最初の結果を出せません（状態を持つ中間操作）。

<!-- output: sorted-barrier -->
```
  sorted前: b
  sorted前: c
  sorted前: a
  sorted後: a
  sorted後: b
  sorted後: c
```
<!-- /output -->

`sorted`の手前の`peek`がすべての要素について先に呼ばれ、そのあとで並べ替えた順に流れています。
`sorted`は、パイプラインの縦の流れをいったんせき止めます。
そのため、無限のストリームに`sorted`を付けると、終端操作が終わりません。
<!-- claim: sorted-barrier -->

ただし、OpenJDKの実装は、ソースがもともと自然順に並んでいるとわかっている（あとで説明する`SORTED`の特性がある）とき、自然順の`sorted`を省きます。
`TreeSet`をソースにすると、要素はせき止められずに流れます。

<!-- output: sorted-noop-on-sorted -->
```
  sorted前: a
  sorted後: a
  sorted前: b
  sorted後: b
  sorted前: c
  sorted後: c
```
<!-- /output -->

<!-- claim: sorted-noop-on-sorted -->

`distinct`も状態を持つ中間操作ですが、逐次のストリームでは、初めて現れた要素をすぐ次の段へ流します。
順序のあるストリームでは、重複のうち最初に現れた要素が残ることを、APIドキュメントが約束しています。

<!-- output: distinct-stable -->
```
  distinct前: a
  distinct後: a
  distinct前: b
  distinct後: b
  distinct前: a
  distinct前: c
  distinct後: c
  distinct前: b
```
<!-- /output -->

<!-- claim: distinct-stable -->

### count()がパイプラインを実行しないことがある

`count()`のAPIドキュメントには、要素の数をソースから直接求められるなら、パイプラインを実行しなくてよいと書かれています。
JDK 25では、`map`のあとの`count()`は`peek`を呼ばずに3を返します。
`filter`は要素の数を変えうるので、`filter`のあとでは実際に要素を流して数えます。

<!-- output: count-may-skip -->
```
  map後のcount = 3（peekは呼ばれない）
  peek: 1
  peek: 2
  peek: 3
  filter後のcount = 2（filterは数を変えうるので実行する）
```
<!-- /output -->

<!-- claim: count-may-skip -->

## 5. Spliterator：ソースの性質を伝える

ストリームのソースは`Spliterator`（分割できるイテレーター）として扱われます。
`Spliterator`は、要素を1つずつ渡す`tryAdvance`・`forEachRemaining`、残りを2つに分ける`trySplit`、残りの数の見積もり`estimateSize`のほかに、ソースの性質を表す特性（characteristics）を報告します。

| 特性 | 意味 |
|---|---|
| `ORDERED` | 要素に決まった順序（出現順）がある |
| `DISTINCT` | 重複がない |
| `SORTED` | 並べ替えた順になっている |
| `SIZED` | 要素の数が正確にわかる |
| `SUBSIZED` | 分割したあとの部分も、要素の数が正確にわかる |
| `NONNULL` | `null`を含まない |
| `IMMUTABLE` | ソースが変更されない |
| `CONCURRENT` | 走査中にほかのスレッドが安全に変更できる |

`verify/SpliteratorTraits.java`は、ソースごとの特性を表示します。

<!-- output: spliterator-characteristics -->
```
ArrayList                  ORDERED SIZED SUBSIZED
LinkedList                 ORDERED SIZED SUBSIZED
Arrays.asList              ORDERED SIZED SUBSIZED
List.of                    ORDERED SIZED SUBSIZED
HashSet                    DISTINCT SIZED
TreeSet                    ORDERED DISTINCT SORTED SIZED
HashMap.keySet             DISTINCT SIZED
IntStream.range            ORDERED DISTINCT SORTED SIZED NONNULL IMMUTABLE SUBSIZED
Stream.of                  ORDERED SIZED IMMUTABLE SUBSIZED
Stream.iterate             ORDERED IMMUTABLE
Stream.generate            IMMUTABLE
```
<!-- /output -->

リスト（`ArrayList`・`LinkedList`）は順序と数がわかり、`HashSet`は重複がないが順序がなく、`TreeSet`は並んでいます。
`IntStream.range`は、順序・重複なし・並び・数・`null`なし・変更なしのすべてを報告します。
`Stream.iterate`は無限のストリームなので`SIZED`がなく、`Stream.generate`には順序もありません。
<!-- claim: spliterator-characteristics -->

ストリームの実装は、この特性を手がかりに処理を省きます。
4の`sorted`の省略（`SORTED`）や、`count()`の省略（`SIZED`）がその例です。

### 中間操作は特性を変える

中間操作を通すと、特性が変わります。

<!-- output: ops-change-characteristics -->
```
--- 中間操作のあとの特性（ソースはTreeSet） ---
stream()                   ORDERED DISTINCT SORTED SIZED
map()                      ORDERED SIZED
filter()                   ORDERED DISTINCT SORTED
unordered()                DISTINCT SIZED
--- ソースはArrayList ---
stream()                   ORDERED SIZED SUBSIZED
map()                      ORDERED SIZED SUBSIZED
sorted()                   ORDERED SORTED SIZED SUBSIZED
distinct()                 ORDERED DISTINCT
```
<!-- /output -->

`map`は要素の値を変えうるので、`DISTINCT`と`SORTED`がなくなります。
`filter`は要素の数を変えうるので、`SIZED`がなくなります。
`unordered()`は`ORDERED`を外し、`sorted()`は`SORTED`を、`distinct()`は`DISTINCT`を付けます。
<!-- claim: ops-change-characteristics -->

`estimateSize`は見積もりで、`filter`のあとでは実際より大きい値を返します。
無限のソースは`Long.MAX_VALUE`を返します。

<!-- output: estimate-size -->
```
ArrayList(3件): 3
filter後: 3
Stream.iterate: 9223372036854775807（Long.MAX_VALUE = 9223372036854775807）
```
<!-- /output -->

<!-- claim: estimate-size -->

## 6. trySplit：並列ストリームの仕事の分け方

並列ストリームは、ソースの`Spliterator`を`trySplit`でくり返し分け、分けた部分を別々のスレッドで処理します。
どれだけ均等に分けられるかは、ソースのデータ構造で決まります。
`verify/SplitShape.java`は、10000個の要素を持つソースを分けた結果を表示します。

### 配列をもとにしたソース

<!-- output: split-array -->
```
ArrayList(10000): (((1250 | 1250) | (1250 | 1250)) | ((1250 | 1250) | (1250 | 1250)))
IntStream.range(10000): (((1250 | 1250) | (1250 | 1250)) | ((1250 | 1250) | (1250 | 1250)))
ArrayList(10000): 5000, 2500, 1250, 625, 312（残りの見積もり 313）
ArrayList(1): null（残りの見積もり 1）
ArrayList(2): 1, null（残りの見積もり 1）
```
<!-- /output -->

`ArrayList`と`IntStream.range`は、範囲の真ん中で分けるので、何段分けてもほぼ同じ大きさになります。
要素が1つになると、`trySplit`は`null`を返して分割をやめます。
<!-- claim: split-array -->

### 先頭からしかたどれないソース

<!-- output: split-linked -->
```
LinkedList(10000): (((256 | 256) | (256 | 256)) | ((1024 | 1024) | (3072 | 3856)))
LinkedList(10000): 1024, 2048, 3072, 3856, null（残りの見積もり 0）
Stream.iterate: 1024, 2048, 3072, 4096, 5120（残りの見積もり 9223372036854775807）
```
<!-- /output -->

`LinkedList`は真ん中の要素へすぐには行けません。
そこでOpenJDKの実装は、先頭から1024個の要素を配列に写して切り出します。
次は2048個、その次は3072個と、切り出す数を増やしていきます。
`Stream.iterate`のように、次の要素を順に作るしかないソースも同じ方法で分けられます（`Spliterators.AbstractSpliterator`）。
最初の部分が小さく、写す手間もかかるので、配列をもとにしたソースより並列化の効果が出にくくなります。
<!-- claim: split-linked -->

### ハッシュ表をもとにしたソース

<!-- output: split-hash -->
```
HashSet(10000): 8192, 1808, 0, 0, 0（残りの見積もり 312）
```
<!-- /output -->

`HashSet`は、ハッシュ表のバケット（表の欄）の範囲を半分に分けます。
`Integer`のハッシュ値はその数自身なので、0〜9999の整数は表の前のほうのバケットに集まります。
そのため最初の分割で8192個、次に1808個が切り出され、残りは空です。
見積もり（`estimateSize`）は単純に半分にしているので、実際の数とずれます。
<!-- claim: split-hash -->

## 7. 並列ストリームとForkJoinPool

### 共通プール

並列ストリームの処理は、Fork/Joinフレームワーク（第15章）の共通プール（`ForkJoinPool.commonPool()`）で動きます。
`verify/CommonPool.java`を`-XX:ActiveProcessorCount=4`で実行すると、次のように表示されます。

<!-- output: common-pool-threads -->
```
availableProcessors = 4
getCommonPoolParallelism = 3
--- 並列ストリームを動かしたスレッド ---
呼び出したスレッド: main
使ったスレッドの種類: [ForkJoinPool.commonPool-worker-N, main]
commonPoolのワーカーの数は並列度以下か: true
```
<!-- /output -->

処理を分担したのは、共通プールのワーカースレッド（`ForkJoinPool.commonPool-worker-番号`）と、終端操作を呼んだ`main`スレッドです。
呼び出したスレッドは、結果を待つあいだ休まず、自分も分割した仕事を処理します。
<!-- claim: common-pool-threads -->

OpenJDKの実装では、共通プールの並列度の既定値は、使えるプロセッサーの数より1小さい値です（4なら3）。
呼び出したスレッドが加わるので、合わせてプロセッサーの数だけのスレッドが働きます。
<!-- claim: common-pool-parallelism -->

並列度は、システムプロパティ`java.util.concurrent.ForkJoinPool.common.parallelism`で変えられます。

```bash
java -XX:ActiveProcessorCount=4 -Djava.util.concurrent.ForkJoinPool.common.parallelism=2 CommonPool
```

<!-- output: common-pool-property -->
```
availableProcessors = 4
getCommonPoolParallelism = 2
```
<!-- /output -->

ただし、Java SE 25のAPIドキュメントは、このプロパティの使用を勧めず、`ForkJoinPool.setParallelism`（Java 19〜）を使うよう書いています。
<!-- claim: common-pool-property -->

共通プールは、並列ストリームのほかに、`Executor`を指定しない`CompletableFuture`の非同期処理なども使う、プログラム全体の共有の資源です。
<!-- claim: completablefuture-common-pool -->

ForkJoinPoolは、ワークスティーリング（仕事の盗み合い）という方式で動きます。
すべてのスレッドが、投入されたタスクや、ほかのタスクが作ったタスクを探して実行します。
仕事を早く終えたスレッドがほかのスレッドの仕事を引き受けるので、分割した部分の大きさがそろっていなくても、スレッドが遊びにくくなります。
<!-- claim: work-stealing -->

### 自分で作ったプールのなかで動かす

自分で作った`ForkJoinPool`に投入したタスクのなかで並列ストリームを動かすと、OpenJDKの実装では、そのプールのスレッドで処理されます。

<!-- output: custom-pool -->
```
--- 自分で作ったForkJoinPoolの中から呼ぶ ---
使ったスレッドの種類: [ForkJoinPool-1-worker-N]
```
<!-- /output -->

これはAPIが約束している動作ではなく、実装に依存します。
共通プールを長い処理でふさがないための工夫として紹介されることがありますが、使うときはこの点を承知しておきます。
<!-- claim: custom-pool -->

## 8. 並列でも結果を変えないための約束

APIドキュメントは、`findAny`のように明示的に非決定的とされた操作を除き、逐次か並列かで計算の結果が変わってはならないと定めています。
ただし、それは渡す関数が約束を守っているときの話です。
`verify/ParallelPitfalls.java`は、1から20までの整数で、約束を守る場合と守らない場合を比べます。

### 順序

<!-- output: foreach-order -->
```
forEachの順序は元の順序と同じか: false
forEachOrderedの順序は元の順序と同じか: true
```
<!-- /output -->

並列ストリームの`forEach`は、要素の出現順を守りません。
順序を守ると並列化の利点が失われるからです。
順序どおりに処理したいときは`forEachOrdered`を使います。
<!-- claim: foreach-order -->

一方、`toList`や`findFirst`は、順序のあるソースなら並列でも逐次と同じ結果を返します。
実装は、分割した部分の結果を元の順序どおりにつなぎ直しています。

<!-- output: ordered-results -->
```
並列のmap→toListの結果: [10, 20, 30, 40, 50]
並列のfindFirst: 11
```
<!-- /output -->

<!-- claim: ordered-results -->

`findAny`は、どの要素を返してもよいと決められています。
並列で速く答えを出すための約束で、実行のたびに違う要素が返ることがあります。
<!-- claim: findany-nondeterministic -->

### reduceの単位元と結合性

`reduce(初期値, 関数)`の初期値は、関数の単位元（足し算なら0、かけ算なら1）でなければなりません。
並列では、分割した部分ごとに初期値から計算を始め、最後に部分の結果をまとめるからです。

<!-- output: reduce-identity -->
```
reduce(100, sum) 逐次: 310 並列: 2210 一致: false
reduce(0, sum) 逐次: 210 並列: 210
```
<!-- /output -->

初期値を100にすると、逐次では1回だけ100が足されますが、並列では部分の数だけ100が足されます。
<!-- claim: reduce-identity -->

関数は結合的（`(a op b) op c`と`a op (b op c)`が等しい）でなければなりません。
引き算は結合的でないので、部分の結果のまとめ方で答えが変わります。

<!-- output: reduce-associativity -->
```
引き算 逐次: -210 並列: 0 一致: false
```
<!-- /output -->

<!-- claim: reduce-associativity -->

### 副作用

ラムダ式から外の変数やコレクションを書き換える（副作用）と、並列では正しく動きません。
スレッドセーフでない`ArrayList`に、並列ストリームの`forEach`から10万件を追加すると、要素が失われます（実行によっては例外になることもあります）。
`collect`で集めれば、各スレッドが別々の入れ物に集めてからまとめるので正しく集まります。

<!-- output: side-effect-unsafe -->
```
ArrayListに100000件追加: 要素数が100000件か: false
collectで集めた要素数: 100000
```
<!-- /output -->

<!-- claim: side-effect-unsafe -->

## 9. コレクターの特性と並列のcollect

`Collector`は、supplier（入れ物を作る）・accumulator（要素を入れる）・combiner（2つの入れ物をまとめる）・finisher（最後の変換）の4つの関数と、特性の集合を持ちます。
特性は、実装が処理を省くための手がかりです。

| 特性 | 意味 |
|---|---|
| `IDENTITY_FINISH` | finisherは何もしない（入れ物をそのまま結果にできる） |
| `UNORDERED` | 結果が要素の順序に依存しない |
| `CONCURRENT` | 1つの入れ物に複数のスレッドから同時に入れてよい |

`verify/CollectorTraits.java`は、標準のコレクターの特性を表示します。

<!-- output: collector-characteristics -->
```
toList(): [IDENTITY_FINISH]
toSet(): [UNORDERED, IDENTITY_FINISH]
joining(): []
toUnmodifiableList(): []
groupingBy(): [IDENTITY_FINISH]
groupingByConcurrent(): [CONCURRENT, UNORDERED, IDENTITY_FINISH]
```
<!-- /output -->

<!-- claim: collector-characteristics -->

ふつうのコレクターで並列に`collect`すると、分割した部分ごとにsupplierで入れ物を作り、combinerでまとめます。
`CONCURRENT`かつ`UNORDERED`のコレクター（`groupingByConcurrent`など）では、入れ物を1つだけ作り、すべてのスレッドがそこへ入れます。
supplierが呼ばれた回数を数えると、違いがわかります。

<!-- output: concurrent-collector -->
```
ふつうのコレクター: 要素数=1000 supplierは2回以上呼ばれたか: true
CONCURRENTかつUNORDEREDのコレクター: 要素数=1000 supplierが呼ばれた回数=1
```
<!-- /output -->

<!-- claim: concurrent-collector -->

`src/`の`CustomCollectors.java`は、`Collectors.joining`と同じ働きのコレクターを`StringBuilder`で自作しています。
標準の`joining(区切り, 前, 後)`は内部で`StringJoiner`を、引数のない`joining()`は`StringBuilder`を入れ物にしており、自作のほうが速いわけではありません。
`javap -c -p java.util.stream.Collectors`で、`joining`のラムダ式の本体を確かめられます。
<!-- claim: joining-uses-stringjoiner -->

<!-- output: joining-uses-stringjoiner -->
```
private static java.util.StringJoiner lambda$joining$1(java.lang.CharSequence, java.lang.CharSequence, java.lang.CharSequence);
private static java.lang.StringBuilder lambda$joining$0(java.lang.StringBuilder, java.lang.StringBuilder);
```
<!-- /output -->

<!-- claim: joining-uses-stringjoiner -->

## 10. Java 16以降に加わった操作

### toList()

Java 16で、`Stream`に`toList()`が加わりました。
`--release 15`でコンパイルすると、メソッドが見つからないというエラーになります。
<!-- claim: tolist-since-16 -->

`toList()`が返すのは変更できないリストで、`add`すると`UnsupportedOperationException`になります。
`Collectors.toList()`は、JDK 25では変更できる`ArrayList`を返します（APIは型を約束していません）。
`List.of`と違い、`toList()`の結果は`null`を含められます。

<!-- output: tolist-unmodifiable -->
```
toList(): java.util.ImmutableCollections$ListN
Collectors.toList(): java.util.ArrayList
toList()の結果にadd: UnsupportedOperationException
Collectors.toList()の結果にadd: [1, 2, 3, 4]
toList()はnullを含められる: [x, null]
List.ofはnullを含められない: NullPointerException
```
<!-- /output -->

<!-- claim: tolist-unmodifiable -->

### mapMulti

`mapMulti`（Java 16〜）は、1つの要素から0個以上の要素を出す中間操作です。
`flatMap`は要素ごとに新しいストリームを返しますが、`mapMulti`は渡された`Consumer`（下の`sink`）に出したい要素を入れます。
出す要素が少ないときや、型で振り分けたいときに向いています。

<!-- output: mapmulti -->
```
mapMulti: [1, 2, 3, 4]
```
<!-- /output -->

<!-- claim: mapmulti, mapmulti-since-16 -->

### Stream Gatherers（Java 24〜）

`Stream.gather`は、自作の中間操作をはさむためのメソッドです。
`collect`が終端操作を自作するしくみであるのに対し、`gather`は中間操作を自作するしくみです。
Stream Gatherersは、Java 22（JEP 461）と23（JEP 473）のプレビューを経て、Java 24（JEP 485）で正式な機能になりました。
<!-- claim: gatherers-history -->

`--release 23`でコンパイルすると、プレビューのAPIだというエラーになります。

<!-- output: gatherers-preview-23 -->
```
GatherersRelease23.java: error: <R>gather(Gatherer<? super T,?,R>) is a preview API and is disabled by default.
GatherersRelease23.java: error: Gatherers is a preview API and is disabled by default.
```
<!-- /output -->

<!-- claim: gatherers-preview-23 -->

標準の`Gatherers`クラスには、よく使う中間操作が用意されています。
`Gatherer.ofSequential`で、状態を持つ中間操作を自作することもできます（下の例は、直前の値を覚えて連続する重複を除くもの）。

<!-- output: gatherers -->
```
--- Gatherers（Java 24〜） ---
windowFixed(3): [[1, 2, 3], [4, 5, 6], [7]]
windowSliding(3): [[1, 2, 3], [2, 3, 4], [3, 4, 5], [4, 5, 6], [5, 6, 7]]
scan(累計): [1, 3, 6, 10, 15, 21, 28]
fold(合計): [28]
自作(連続する重複を除く): [1, 2, 3, 1]
無限のストリーム+windowFixed+limit: [[1, 2], [3, 4], [5, 6]]
```
<!-- /output -->

`windowFixed`は決まった数ずつの組、`windowSliding`は1つずつずらした組を作ります。
`scan`は途中経過を、`fold`は最後の結果1つだけを出します。
`gather`も遅延評価なので、無限のストリームにも`limit`と組み合わせて使えます。
<!-- claim: gatherers -->

## 11. 並列化するかどうかの判断

並列ストリームが速くなるかどうかに影響する主な条件は、次のとおりです。
どれも環境によって効き方が変わるので、`ParallelStreamOptimization`のようなデモで傾向をつかみ、実際の判断はJMHで測って行います。

- 要素の数と、1要素あたりの処理の重さ
  - 分割・スレッドへの受け渡し・結果をまとめる手間を上回るだけの仕事が要る
- ソースの分けやすさ（6）
  - 配列をもとにしたソースは均等に分けられ、`LinkedList`や`Stream.iterate`は分けにくい
- 順序の制約（8）
  - `limit`・`findFirst`・`forEachOrdered`は順序をそろえる手間がかかる。順序が要らなければ`unordered()`や`findAny`を使える
- 状態を持つ中間操作（4）
  - 並列の`sorted`や順序のある`distinct`は、要素をためて部分ごとの結果をまとめる
- 処理の中身
  - I/O待ちのような処理を並列ストリームで動かすと、共有の共通プールのスレッドをふさぐ（7）

## よくある誤解

- 中間操作を呼んだ時点で処理が実行される
  - 終端操作を呼ぶまで何も実行されない（1）
  <!-- claim: lazy-intermediate -->
- `filter`→`map`は、全要素に`filter`をかけてから`map`をかける
  - 1要素ずつ最後の段まで流れる（2）
  <!-- claim: vertical-order -->
- `peek`のなかの処理は必ず実行される
  - `count()`のように、パイプラインを実行しない終端操作がある（4）
  <!-- claim: count-may-skip -->
- 並列ストリームは自分専用のスレッドを作る
  - プログラム全体で共有する共通プールと、呼び出したスレッドで動く（7）
  <!-- claim: common-pool-threads -->
- 並列にしても`forEach`の順序は変わらない
  - 並列の`forEach`は順序を守らない。`forEachOrdered`を使う（8）
  <!-- claim: foreach-order -->
- `reduce`の初期値は、計算の最初に1回だけ使われる
  - 並列では分割した部分ごとに使われるので、単位元でなければならない（8）
  <!-- claim: reduce-identity -->
- `toList()`と`collect(Collectors.toList())`は同じ
  - `toList()`の結果は変更できない（10）
  <!-- claim: tolist-unmodifiable -->

## 参考資料

- java.util.stream パッケージの説明（Java SE 25 API）
  - Stream operations and pipelines、Parallelism、Non-interference、Side-effects、Ordering、Reduction operations
  - `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/stream/package-summary.html`
- java.util.stream.Stream（Java SE 25 API）
  - `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/stream/Stream.html`
- java.util.Spliterator（Java SE 25 API）
  - `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Spliterator.html`
- java.util.stream.Collector（Java SE 25 API）
  - `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/stream/Collector.html`
- java.util.stream.Gatherers（Java SE 25 API）
  - `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/stream/Gatherers.html`
- java.util.concurrent.ForkJoinPool（Java SE 25 API）
  - `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/ForkJoinPool.html`
- JEP 107: Bulk Data Operations for Collections
  - `https://openjdk.org/jeps/107`
- JEP 485: Stream Gatherers
  - `https://openjdk.org/jeps/485`
