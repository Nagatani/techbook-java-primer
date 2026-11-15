# 7. 並行プログラミングの理論

## この項目で学ぶこと

第15章では、`synchronized`や`volatile`、Atomicクラスの使い方を学びました。
この項目では、それらが「なぜ必要なのか」「何を保証しているのか」を、並行プログラミングの理論から説明します。

- インターリービングと非決定性
- 競合状態とデータ競合の違い
- 臨界区間と相互排除
- デッドロックとコフマンの4条件、その防ぎ方
- happens-before関係とJavaメモリモデル
- 線形化可能性（入口だけ）

## なぜ必要か

### 逐次のプログラムとの違い

C言語で書いてきた逐次のプログラムは、同じ入力を与えれば、毎回同じ順序で文が実行され、同じ結果になります。
複数のスレッドが同時に動くプログラムでは、各スレッドの文がどの順序で混ざり合うか（インターリービング）が、実行のたびに変わります。
順序はOSのスケジューラー、CPUのコアの数、ほかのプログラムの負荷などで決まり、プログラムからは制御できません。

その結果、並行プログラムの誤りには次の性質があります。

- 再現しにくい
    + 1万回に1回だけ起きる誤りは、テストを何回か通しただけでは見つからない
- 観察すると消えることがある
    + デバッガーで止めたり、`IO.println`を足したりすると、タイミングが変わって誤りが起きなくなる
- コードを読んだだけでは気づきにくい
    + 1行の`counter++`が、実は3つの操作に分かれている

テストで見つけにくいからこそ、「どんな順序で実行されても正しい」ことを、理屈で確かめる必要があります。

### 歴史的な背景

- 1965年
    + Edsger Dijkstraが論文「Solution of a Problem in Concurrent Programming Control」で、相互排除の問題を定式化して解いた。同じころ、セマフォを提案した
- 1971年
    + Edward Coffman、Melanie Elphick、Arie Shoshaniが論文「System Deadlocks」で、デッドロックが起きる4つの条件を示した
- 1974年
    + C. A. R. Hoareが論文「Monitors: An Operating System Structuring Concept」でモニターを提案した（Per Brinch Hansenも同時期に同様の概念を発表している）。Javaの`synchronized`と`wait`/`notify`は、モニターの考え方にもとづく
- 1978年
    + Leslie Lamportが論文「Time, Clocks, and the Ordering of Events in a Distributed System」で、分散システムの出来事の順序として「happened before」関係を定義した
- 1990年
    + Maurice HerlihyとJeannette Wingが、並行オブジェクトの正しさの基準として線形化可能性を定義した
- 2004年
    + Java 5で、JSR 133により改訂されたJavaメモリモデルが入った。現在のJava言語仕様（JLS）の17章（Threads and Locks）の内容である

### 並行と並列

似た言葉に「並行（concurrent）」と「並列（parallel）」があります。

- 並行
    + 複数の処理の実行期間が重なっていること。1つのCPUコアでも、処理を細かく切り替えれば並行に動く
- 並列
    + 複数の処理が、物理的に同じ瞬間に実行されていること。複数のコアが要る

この項目の問題（競合状態・デッドロック）は、並列でなく並行であるだけで起きます。
1つのコアでも、スレッドの切り替えは任意の位置で起こりうるからです。
一方、次に述べるメモリの可視性の問題は、複数のコアがそれぞれキャッシュを持つ並列の環境で、とくに表に出やすくなります。

## インターリービングと競合状態

### 1行の文は1つの操作ではない

`src/main/java/com/example/theory/concurrent/RaceConditionExample.java`の`unsafeIncrement`は、`counter++`だけのメソッドです。

```java
public void unsafeIncrement() {
    // この操作は原子的でない:
    // 1. counter の読み取り
    // 2. 値の増加
    // 3. counter への書き込み
    counter++; // 複数のスレッドで同時実行すると競合状態
}
```

`counter++`は、読む・1足す・書くの3段階に分かれます。
2つのスレッドAとBが、counterが5のときに同時に呼ぶと、次のような順序がありえます。

| 時刻 | スレッドA | スレッドB | counterの値 |
|------|------|------|------|
| 1 | counterを読む（5） | | 5 |
| 2 | | counterを読む（5） | 5 |
| 3 | 5 + 1 = 6 を計算 | | 5 |
| 4 | | 5 + 1 = 6 を計算 | 5 |
| 5 | 6を書く | | 6 |
| 6 | | 6を書く | 6 |

2回増やしたのに、counterは1しか増えていません。
Aの更新がBの書き込みで上書きされて消えたので、この現象を更新の消失（lost update）と呼びます。

2つのスレッドがそれぞれ3段階の操作をするとき、混ざり方は6つの位置から3つを選ぶ組合せの数、20通りあります。
そのうち正しい結果になるのは、一方の3段階がまとまって実行される2通りだけです。
操作の数が増えると混ざり方の数は爆発的に増えるので、すべてを試して確かめることはできません。

### 実行して確かめる

`appendix/c-theoretical-foundations/`で次のように実行します。

```bash
javac -d out $(find src -name '*.java')
java -ea -cp out com.example.theory.Main concurrent
```

最初に、10個のスレッドがそれぞれ1000回`unsafeIncrement`を呼んだ結果と、Atomicクラスで数えた結果が表示されます。

```
--- Race Condition Demo ---
Unsafe counter (expected 10000): 9666
Atomic counter (expected 10000): 10000
```

1行目の値は実行のたびに変わり、10000になることもあります。
「10000になった」ことは、正しいことの証拠になりません。
2行目の`AtomicInteger.incrementAndGet`は、読む・足す・書くを1つの分割できない操作（原子的な操作）として行うので、つねに10000になります。

### 競合状態とデータ競合

似た言葉に「競合状態」と「データ競合」があり、区別して使います。

- 競合状態（race condition）
    + 処理の結果が、スレッドの実行のタイミングによって変わり、その一部が誤りになる状況。設計上の誤りを指す広い言葉である
- データ競合（data race）
    + Java言語仕様で定義された言葉。2つのスレッドが同じ変数にアクセスし、少なくとも一方が書き込みで、2つのアクセスがあとで述べるhappens-before関係で順序づけられていない状態

`counter++`の例は両方に当たります。
しかし、データ競合がなくても競合状態は起こります。
次の例は、スレッドセーフな`ConcurrentHashMap`を使っているのでデータ競合はありませんが、「調べてから更新する（check-then-act）」の2つの操作の間に、別のスレッドが割り込めます。

```java
// 調べてから更新する（check-then-act）: 2つの操作のあいだに別のスレッドが割り込める
static void countUnsafe(Map<String, Integer> map) {
    for (int i = 0; i < TIMES; i++) {
        Integer old = map.get("java");                 // 調べる
        map.put("java", old == null ? 1 : old + 1);    // 更新する
    }
}

// merge は「調べて更新する」を1つの原子的な操作として行う
static void countSafe(ConcurrentHashMap<String, Integer> map) {
    for (int i = 0; i < TIMES; i++) {
        map.merge("java", 1, Integer::sum);
    }
}
```

4つのスレッドがそれぞれ10万回（`TIMES`は100,000）呼んだ結果の例です（1行目の値は実行のたびに変わります）。

```
check-then-act: 121724
merge:          400000
```

`get`と`put`はそれぞれ正しく動いても、2つを続けて呼ぶ処理は原子的になりません。
スレッドセーフなクラスを使えば安全になる、というわけではないことに注意してください。

## 臨界区間と相互排除

### 相互排除の問題

共有するデータを操作するコードの範囲を、臨界区間（critical section）と呼びます。
臨界区間に同時に入れるスレッドを1つに限ることを、相互排除（mutual exclusion）と呼びます。
相互排除の仕組みには、次の性質が求められます。

- 相互排除
    + 2つ以上のスレッドが同時に臨界区間に入らない
- 進行（デッドロックしない）
    + 臨界区間に入りたいスレッドがあり、誰も入っていないなら、どれかのスレッドはいずれ入れる
- 飢餓がない
    + 入りたいスレッドは、いつかは必ず入れる。ほかのスレッドに追い越され続けて永遠に待つことがない

### ハードウェアの支援なしの解法

Dijkstraの1965年の論文以来、普通の読み書きだけで相互排除を実現する方法が研究されました。
2つのスレッドの場合の簡潔な解法として、Gary Petersonが1981年に発表したものが知られています。
考え方を示すための疑似コードです。

```
共有変数: want[0] = want[1] = false, turn

スレッドi（jはもう一方）:
    want[i] = true      // 入りたいと宣言する
    turn = j            // 相手に順番を譲る
    while (want[j] && turn == j) { 待つ }
    ... 臨界区間 ...
    want[i] = false     // 出たことを知らせる
```

両方が同時に入ろうとしても、`turn`には最後に書いた側の譲った相手が残るので、どちらか一方だけが進めます。

ただし、このコードをJavaの普通のフィールドでそのまま書くと、正しく動く保証がありません。
コンパイラーやCPUが読み書きの順序を入れ替えることがあり、この解法は「書いた順序で読み書きされ、ほかのスレッドからもその順序で見える」ことを前提にしているからです。
この問題は、後のJavaメモリモデルの節で扱います。

### Javaの相互排除の道具

実際のプログラムでは、自分で相互排除の手順を組まず、言語とライブラリの道具を使います。

- `synchronized`
    + モニターによる相互排除。同じオブジェクトのロックを持つスレッドは一度に1つだけである
- `java.util.concurrent.locks.ReentrantLock`
    + `synchronized`と同じ相互排除に加え、待つ時間を限る`tryLock`、待っている間の割り込みへの応答（`lockInterruptibly`）を使える
- Atomicクラス
    + CPUのCAS（compare-and-swap、「値が予想どおりなら書き換える」を1命令で行う）などを使い、ロックなしで1つの変数を原子的に更新する

## デッドロック

### デッドロックとは

デッドロック（deadlock）は、複数のスレッドが互いに相手の持つ資源（ロックなど）を待ち合い、どのスレッドも先へ進めなくなった状態です。

`src/main/java/com/example/theory/concurrent/DeadlockExample.java`の`method1`と`method2`は、2つのロックを逆の順序で取ります。

```java
public void method1() {
    synchronized (lock1) {
        // ...（100ミリ秒待つ）
        synchronized (lock2) { // 循環待機の原因
            // ...
        }
    }
}

public void method2() {
    synchronized (lock2) {
        // ...（100ミリ秒待つ）
        synchronized (lock1) { // 循環待機の原因
            // ...
        }
    }
}
```

スレッドAが`method1`でlock1を取り、スレッドBが`method2`でlock2を取ったとします。
このときAはlock2を、Bはlock1を待つので、どちらも進めなくなります。
資源とスレッドの関係を矢印で描くと、待ちの関係が輪（閉路）になっています。

```
    スレッドA ──持つ──> lock1
        ^                 |
       待つ              待たれる
        |                 v
      lock2 <──持つ── スレッドB
```

デモを実行すると、次のように表示されます（スレッドの番号は実行のたびに変わります）。

```
--- Deadlock Demo ---
=== Demonstrating potential deadlock ===
Starting threads that may deadlock...
Thread 45 acquired lock1
Thread 46 acquired lock2
Threads appear to be deadlocked!
```

ロックを取った後に100ミリ秒待つので、ほぼ毎回デッドロックします。
デッドロックしたスレッドはデーモンスレッドにしてあり、2つのスレッドの終了をそれぞれ最大5秒ずつ待った後、プログラムは次のデモへ進みます。

### コフマンの4条件

Coffmanらは、次の4つの条件がすべて同時に成り立つときにだけデッドロックが起きることを示しました。

1. 相互排除
    + 資源を一度に1つのスレッドしか使えない
    + 例: `synchronized`のロック
2. 保持と待機（hold and wait）
    + 資源を持ったまま、別の資源を待つ
    + 例: lock1を持ったまま、lock2を待つ
3. 横取り不可（no preemption）
    + ほかのスレッドが持つ資源を、強制的に取り上げられない
    + 例: `synchronized`のロックを待つスレッドは、割り込み（`interrupt`）でも待つのをやめない
4. 循環待機（circular wait）
    + 待ちの関係が輪になっている
    + 例: Aはlock2（Bが持つ）を待ち、Bはlock1（Aが持つ）を待つ

逆に言えば、4つのうちどれか1つを崩せばデッドロックは起きません。

### 防ぎ方

デッドロックへの対策は、大きく3つの考え方に分かれます。

- 予防（prevention）
    + 4条件のどれかが成り立たないように設計する
- 回避（avoidance）
    + 資源を渡す前に、渡してもデッドロックに至らないかを調べて判断する。Dijkstraの銀行家のアルゴリズムが代表例で、各スレッドが最大でいくつの資源を使うかを前もって知っている必要がある
- 検出と回復（detection and recovery）
    + デッドロックが起きたことを見つけて、スレッドを止めるなどして回復する

アプリケーションのプログラムでは、予防がもっとも実用的です。
4条件ごとの崩し方を表にまとめます。

| 崩す条件 | 方法 | Javaでの例 |
|------|------|------|
| 相互排除 | 共有をやめる、不変にする | 不変オブジェクト、スレッドごとのデータ |
| 保持と待機 | 必要な資源をまとめて取る。取れなければ全部手放す | 1つのロックで全体を守る |
| 横取り不可 | 待つ時間を限り、取れなければ手放してやり直す | `ReentrantLock.tryLock(時間, 単位)` |
| 循環待機 | すべてのスレッドが同じ順序でロックを取る | ロックに順序を決める |

`DeadlockExample`の`safeMethod1`と`safeMethod2`は、どちらも「lock1、lock2の順」でロックを取ります。
すべてのスレッドが同じ順序で取れば、lock2を持ってlock1を待つスレッドがいなくなるので、待ちの輪ができません。
デモの後半「Demonstrating safe implementation」は、つねに最後まで終わります。

ロックが固定の2つなら、順序を決めるのは簡単です。
「口座Aから口座Bへ送金する」のようにロックの対象が引数で決まる場合は、口座番号など一意な値の小さい順に取る、といった規則を決めておきます。

`tryLock`で待つ時間を限る方法は、デッドロックを防げますが、スレッドが手放しとやり直しを延々と繰り返すライブロック（livelock）になることがあります。
やり直す前にランダムな時間だけ待つなどの工夫が要ります。

### デッドロックを見つける

JDKの`jcmd`（または`jstack`）でスレッドの状態を出力すると、JVMは`synchronized`のロックによるデッドロックを検出して報告します。
デモの実行中に、別の端末で`jps -l`でプロセスの番号を調べ、`jcmd プロセス番号 Thread.print`を実行した結果の一部です（番地は実行のたびに変わります）。

```
Found one Java-level deadlock:
=============================
"Thread-20":
  waiting to lock monitor 0x00000009bed13100 (object 0x00000003cfe573d8, a java.lang.Object),
  which is held by "Thread-21"

"Thread-21":
  waiting to lock monitor 0x00000009bed13020 (object 0x00000003cfe573c8, a java.lang.Object),
  which is held by "Thread-20"
...
Found 1 deadlock.
```

プログラムのなかからは、`java.lang.management.ThreadMXBean`の`findDeadlockedThreads`で調べられます。
検出できるのは、ロックの待ちの輪です。
「来ないメッセージを待ち続ける」ような、ロック以外の原因で止まった状態は検出できません。

## happens-before関係とJavaメモリモデル

### 書いた順序で見えるとは限らない

逐次のプログラムでは、「前の文で代入した値は、後の文で読める」ことを当然と考えます。
複数のスレッドの間では、これが当然ではありません。
理由は2つあります。

- 命令の並べ替え
    + コンパイラー（JITコンパイラーを含む）やCPUは、1つのスレッドのなかで結果が変わらない範囲で、読み書きの順序を入れ替えて速くする。ほかのスレッドから見ると、順序が入れ替わって見える
- キャッシュとバッファー
    + 各CPUコアは、書き込みをいったん自分のキャッシュや書き込み用のバッファーにためる。ほかのコアから、その値がいつ見えるかは決まっていない

`src/main/java/com/example/theory/concurrent/MemoryModelExample.java`の`writer`と`reader`は、この問題を示す例です。

```java
public void writer() {
    value = 42;      // 1
    ready = true;    // 2
}

public void reader() {
    while (!ready) { // 3
        Thread.yield();
    }
    IO.println(value); // 4
}
```

`writer`を呼ぶスレッドと`reader`を呼ぶスレッドがあるとき、Javaの規則のもとでは次のことが起こりえます。

- 4で0が表示される（readerからは、2の書き込みが1より先に見える）
- 3のループが終わらない（readerから、readyの変化がいつまでも見えない）

デモ（`Main concurrent`の「Memory Model Demo」）では、多くの環境で42と表示されて終わります。
問題は、42になることがJavaの規則で保証されていないことです。
別のJVM、別のCPU、JITコンパイラーの最適化の具合によって、結果が変わりえます。

### Lamportのhappened-before関係

Lamportは1978年の論文で、分散システムの出来事の間の順序を、次のように定義しました。

- 同じプロセスのなかで、aがbより前に起きたなら、aはbより前（a → b）
- aがメッセージの送信で、bがその受信なら、a → b
- a → b かつ b → c なら、a → c（推移律）

どちらとも言えない2つの出来事は「並行」です。
すべての出来事を一列に並べる全順序ではなく、並べられない組を含む半順序である点が要点です。
物理的な時計に頼らず、因果関係だけで順序を決める考え方は、Javaメモリモデルのhappens-before関係の土台になっています。

### Javaのhappens-before規則

Java言語仕様（JLS）の17.4.5節は、2つの操作の間のhappens-before関係を定めています。
操作Xが操作Yにhappens-beforeするなら、Xの結果（Xが書いた値）はYから見えることが保証されます。
主な規則は次のとおりです。

- プログラム順序
    + 同じスレッドのなかで、前にある操作は後の操作にhappens-beforeする
- モニターのロック
    + あるモニターのロックの解放（`synchronized`ブロックの終わり）は、同じモニターのその後のロックの取得にhappens-beforeする
- `volatile`変数
    + `volatile`変数への書き込みは、同じ変数のその後の読み取りにhappens-beforeする
- スレッドの開始
    + `Thread.start()`の呼び出しは、開始されたスレッドのなかのすべての操作にhappens-beforeする
- スレッドの終了
    + スレッドのなかのすべての操作は、ほかのスレッドがそのスレッドの`join()`から戻ることにhappens-beforeする
- 推移律
    + XがYにhappens-beforeし、YがZにhappens-beforeするなら、XはZにhappens-beforeする

`java.util.concurrent`のクラスも、パッケージの説明の「Memory Consistency Properties」で、happens-before関係を約束しています。
たとえば、スレッドセーフなコレクションに要素を入れる前の操作は、別のスレッドがその要素を取り出した後の操作にhappens-beforeします。

### volatileで順序をつなぐ

`MemoryModelExample`の`writerVolatile`と`readerVolatile`は、`readyVolatile`を`volatile`にしています。

```java
public void writerVolatile() {
    valueVolatile = 42;      // 1
    readyVolatile = true;    // 2: volatile write
}

public void readerVolatile() {
    while (!readyVolatile) { // 3: volatile read
        Thread.yield();
    }
    IO.println(valueVolatile); // 4: 必ず 42 が出力
}
```

happens-before関係は次のようにつながります。

```
writerのスレッド               readerのスレッド
  1: valueVolatile = 42
        | プログラム順序
        v
  2: readyVolatile = true ───volatileの規則───> 3: readyVolatileがtrueと読む
                                                     | プログラム順序
                                                     v
                                               4: valueVolatileを読む
```

推移律により1は4にhappens-beforeするので、4は42を読みます。
`valueVolatile`自体は`volatile`でないのに正しく見えるのは、`volatile`の書き込みと読み取りが、その前後の普通の書き込みの可視性まで運ぶからです。

`writerSync`と`readerSync`は、同じことを`synchronized`で行います。
writerがロックを解放し、readerが同じロックを取ると、モニターの規則でhappens-before関係がつながります。
writerとreaderが別々のオブジェクトをロックしたのでは、この規則は働きません。

### 正しく同期されたプログラム

Java言語仕様は、データ競合のないプログラム（正しく同期されたプログラム）は、逐次一貫性（sequential consistency）を持つように振る舞うことを保証しています。
逐次一貫性はLamportが1979年に定義した性質で、「すべてのスレッドの操作を、各スレッドのなかの順序を保ったまま一列に並べた、ある1つの実行と同じ結果になる」ことです。
つまり、データ競合さえなくせば、並べ替えやキャッシュのことを考えずに、「操作がどこかの順序で1つずつ実行される」と考えてよくなります。
この保証は、英語の頭文字からDRF-SC（data-race-free implies sequential consistency）と呼ばれることがあります。

裏返すと、データ競合のあるプログラムの動作を考えるには、メモリモデルの細かな規則が必要になります。
並行プログラムを書くときの実用的な目標は、「データ競合をなくす」ことです。

### 注意しておきたい規則

- `long`と`double`の書き込み
    + `volatile`でない`long`と`double`の書き込みは、32ビットずつ2回に分けて行われてもよいとされている（JLSの17.7節）。別のスレッドから、上半分と下半分が別の書き込みの値になった中途半端な値が見えることがある。参照と、`long`・`double`以外の基本型の書き込みは分割されない
- `final`フィールド
    + コンストラクターのなかで`final`フィールドに代入し、コンストラクターの実行中に`this`を外へ漏らさなければ、ほかのスレッドは同期なしでもその値を正しく読める（JLSの17.5節）。不変オブジェクトがスレッドセーフと言われる理由の1つである
- ダブルチェックロッキング
    + `synchronized`を避けるために「`null`なら、ロックを取ってもう一度調べて生成する」と書くシングルトンは、フィールドを`volatile`にしないと、生成途中のオブジェクトが見えることがある。`src/main/java/com/example/theory/patterns/SingletonPattern.java`は`volatile`を付けている

## 線形化可能性（入口）

### スレッドセーフとは何か

「このクラスはスレッドセーフです」と言うとき、何が保証されているのでしょうか。
HerlihyとWingの線形化可能性（linearizability）は、これに答える基準の1つです。

並行に呼ばれた操作は、それぞれ呼び出しから戻りまでの時間の幅を持ちます。
オブジェクトが線形化可能であるとは、次の2つが成り立つことです。

- 各操作は、呼び出しから戻りまでの間のある一瞬（線形化点）に、まとめて一度に効いたように見える
- その一瞬の順に操作を並べた結果が、オブジェクトの逐次の仕様（スレッドが1つのときの正しい振る舞い）に合っている

```
スレッドA:  |--- increment() → 1 ---|
スレッドB:        |--- increment() → 2 ---|
スレッドC:                                  |--- get() → 2 ---|
                 ^           ^
                Aの線形化点  Bの線形化点
```

AとBの`increment`は時間が重なっているので、どちらが先に効いてもかまいません。
しかし、戻り値が1と2なら「Aが先」と並べられ、Bが終わった後に始まったCの`get`が2を返すことも、この順序と合っています。
もしCの`get`が1を返したら、Bが終わったのに効果が見えていないので、線形化可能ではありません。

`AtomicInteger`の`incrementAndGet`や`compareAndSet`は、それぞれが原子的な操作で、線形化可能な振る舞いをします。
CASを使ったカウンターの例で、線形化点がどこかを見てみます。

```java
// compareAndSetで「読んだ値から変わっていなければ書く」を繰り返す
public int increment() {
    while (true) {
        int current = value.get();
        int next = current + 1;
        if (value.compareAndSet(current, next)) {
            return next; // ここで更新が効いたことになる（線形化点）
        }
        // ほかのスレッドが先に書き換えたので、読み直してやり直す
    }
}
```

`compareAndSet`が成功した瞬間が線形化点です。
4つのスレッドがそれぞれ10万回呼ぶと、結果はつねに400000になります。
ロックを使わず、失敗したスレッドが読み直してやり直すこの形を、ロックフリー（lock-free）と呼びます。
どれかのスレッドの`compareAndSet`は必ず成功するので、全体としてはつねに処理が進みます。

### 原子的な操作をつなげても原子的にはならない

線形化可能性は操作1つずつについての性質です。
上の`check-then-act`の例のように、線形化可能な`get`と`put`を続けて呼んでも、その2つの組は原子的になりません。
組の操作を原子的にしたいときは、次のどれかを使います。

- 組の操作を1つにしたメソッドを使う
    + `ConcurrentHashMap`の`merge`、`compute`、`putIfAbsent`など
- 組の操作全体をロックで囲む
    + 関係するすべてのコードが同じロックを使う必要がある
- 不変オブジェクトを`AtomicReference`で差し替える
    + 新しいオブジェクトを作り、`compareAndSet`で差し替える

`ConcurrentHashMap`の`size()`のように、ほかのスレッドが更新している最中には目安の値しか返さないメソッドもあります（Javadocにそう書かれています）。
スレッドセーフなクラスでも、メソッドごとに何が保証されるかはJavadocで確かめてください。

## よくある誤解・注意点

- 「テストで問題が出なかったので正しい」
    + 並行プログラムの誤りは、特定のタイミングでしか起きない。デモの`Unsafe counter`も、たまたま10000になることがある
- 「`volatile`を付ければスレッドセーフになる」
    + `volatile`は可視性と順序を保証するが、`counter++`のような読み・書きの組を原子的にはしない。`volatile int counter`でも更新は消失する
- 「読むだけなら同期は要らない」
    + 別のスレッドが書き込む変数を読むなら、読む側にも同期（同じロック、`volatile`など）が要る。書く側だけを`synchronized`にしても、読む側はhappens-before関係でつながらない
- 「`synchronized`メソッドにすれば安全」
    + ロックするのはそのオブジェクトだけである。別のオブジェクトのロックで守った変数や、check-then-actの組には効かない
- 「デッドロックは`synchronized`をたくさん使うと起きる」
    + 数より順序の問題である。ロックを持ったまま、外から渡されたコード（コールバックやリスナー）を呼ぶと、知らないうちにロックの順序が逆になることがある
- `Thread.yield`や`Thread.sleep`で順序を制御する
    + どちらもhappens-before関係を作らない。タイミングをずらすだけで、正しさの保証にはならない

## 実務での使いどころ

並行プログラムを安全に書くための指針を、理論との対応とともにまとめます。

- 共有しない
    + スレッドの間で変更可能なデータを共有しなければ、競合は起きない。SwingでGUIの部品をイベントディスパッチスレッドだけで扱う規則は、この考え方である
- 変更しない
    + 不変オブジェクトは、`final`フィールドの規則により、同期なしで共有できる
- 共有して変更するなら、高水準の道具を使う
    + `java.util.concurrent`のコレクション、`ExecutorService`、Atomicクラスなど。自作のロックの手順より、検証済みの道具のほうが誤りが少ない
- ロックを使うなら、守る変数とロックの対応を決めて書き残す
    + 「この変数はこのロックを持っているときだけ読み書きする」をコメントやドキュメントに書く
- 複数のロックを取るなら、順序を決める
    + コフマンの循環待機の条件を崩す
- クラスのスレッドセーフ性をドキュメントに書く
    + スレッドセーフかどうか、どのメソッドの組が原子的かを、利用者が知れるようにする

## 本文・付録との関連

- 第15章「マルチスレッドプログラミング」
    + スレッドの作成、`synchronized`、`volatile`、Atomicクラス、並行コレクション、`ExecutorService`の使い方
- 第6章「不変性とfinalキーワード」
    + 不変オブジェクトの作り方。不変オブジェクトは同期なしで共有できる
- 第12章「Stream APIと高度なコレクション操作」
    + 並列ストリーム。ラムダ式のなかで共有の変数を書き換えると、競合状態になる
- 第18章「基本的なイベント処理」
    + イベントディスパッチスレッド（EDT）。GUIの部品を1つのスレッドに閉じ込めて競合を避ける設計
- 付録B.14「Javaメモリモデル」
    + happens-before関係と、メモリモデルの実装の詳細
- 付録B.05「不変性の設計パターン」
    + 不変オブジェクトの設計

## 参考文献

- Edsger W. Dijkstra「Solution of a Problem in Concurrent Programming Control」『Communications of the ACM』8巻9号、1965年
- E. G. Coffman・M. J. Elphick・A. Shoshani「System Deadlocks」『ACM Computing Surveys』3巻2号、1971年
- C. A. R. Hoare「Monitors: An Operating System Structuring Concept」『Communications of the ACM』17巻10号、1974年
- Leslie Lamport「Time, Clocks, and the Ordering of Events in a Distributed System」『Communications of the ACM』21巻7号、1978年
- Leslie Lamport「How to Make a Multiprocessor Computer That Correctly Executes Multiprocess Programs」『IEEE Transactions on Computers』C-28巻9号、1979年
- Gary L. Peterson「Myths About the Mutual Exclusion Problem」『Information Processing Letters』12巻3号、1981年
- Maurice P. Herlihy・Jeannette M. Wing「Linearizability: A Correctness Condition for Concurrent Objects」『ACM Transactions on Programming Languages and Systems』12巻3号、1990年
- Jeremy Manson・William Pugh・Sarita V. Adve「The Java Memory Model」『Proceedings of the 32nd ACM SIGPLAN-SIGACT Symposium on Principles of Programming Languages（POPL）』2005年
- Brian Goetz ほか『Java Concurrency in Practice』Addison-Wesley、2006年（邦訳『Java並行処理プログラミング』ソフトバンククリエイティブ）
- Maurice Herlihy・Nir Shavit・Victor Luchangco・Michael Spear『The Art of Multiprocessor Programming』第2版、Morgan Kaufmann、2020年
- Java言語仕様（JLS）Java SE 25版の17章（Threads and Locks）
    + `https://docs.oracle.com/javase/specs/jls/se25/html/jls-17.html`
- Java SE 25のAPIドキュメント（`java.util.concurrent`パッケージの説明「Memory Consistency Properties」）
    + `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/package-summary.html`
