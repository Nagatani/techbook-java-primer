# 付録B.14 Javaメモリモデル<small>Java Memory ModelとHappens-Before関係</small>

第15章で学んだ`synchronized`・`volatile`・Atomicクラスが、なぜスレッドの間で値を正しく受け渡せるのかを、Java言語仕様（JLS）の第17章が決めているJavaメモリモデル（JMM）から説明し、JDK 25で実際に観察しながら確かめる節です。

## この節の読み方

この節の解説のうち、事実を述べた段落には`<!-- claim: … -->`という印を付けています（GitHubの画面では見えません）。
印の名前は同じディレクトリの`claims.yml`に対応し、そこにその主張の確かめ方（観察用のプログラムの実行、バイトコードの照合、フラグの既定値、仕様の出典）を書いています。
リポジトリのルートで`npm run check:deep-dive -- b14`を実行すると、すべての主張をJDK 25で確かめ直せます。
「観察する」の欄に載せた出力は、この検査が実際に出したものです。

並行処理の観察には注意が要ります。
データ競合のあるプログラムの結果は、JITの判断・CPU・タイミングで変わり、起こりうる結果が実際には一度も出ないこともあります。
この節では、何度試しても同じになる観察だけを検査で照合し、「起こりうる」ことは仕様を出典にして説明します。
問題が見えなかったからといって、プログラムが正しいとは限りません。

## ファイル構成

### 観察用のプログラム（verify/）

| ファイル | 観察すること |
|---|---|
| `StopFlag.java` | フラグを待つループが終わるか（plain・volatile・VarHandleのopaqueとacquire/release） |
| `StoreBuffer.java` | 書いてから読む2つのスレッドで、両方が古い値を読むか |
| `LostUpdates.java` | `count++`で更新が失われるか（int・volatile・synchronized・AtomicInteger・LongAdder） |
| `Bytecode.java` | `count++`とオブジェクトの公開のバイトコード、`volatile`のフラグ |
| `LazyHolder.java` | ホルダークラスによる遅延初期化 |
| `AtomicInternals.java` | Atomicクラスの内部の道具、VarHandleのフェンス |
| `ContendedLayout.java` | カウンターのフィールドの配置と`@Contended`の詰め物（JOLを使う） |
| `VirtualPinning.java` | 仮想スレッドが`synchronized`やクラスの初期化のなかでキャリアを手放すか |

`verify/`のプログラムはパッケージを持たないので、`verify/`ディレクトリでそのままコンパイル・実行できます。
`ContendedLayout.java`だけは、JOL（`org.openjdk.jol:jol-core:0.17`）のJARをクラスパスに加えます。

```bash
cd verify
javac StopFlag.java && java StopFlag plain && java StopFlag volatile
javac LostUpdates.java && java LostUpdates
javac -cp jol-core-0.17.jar ContendedLayout.java
java -XX:-RestrictContended -Djdk.attach.allowAttachSelf=true -cp jol-core-0.17.jar:. ContendedLayout
```

### 体験用のデモ（src/）

- `VisibilityProblems.java`
  - `volatile`のないフラグで止まらないループ、確認と代入のあいだに割り込まれて複数回作られる初期化、`volatile`と`synchronized`のカウンターの違い
- `HappensBeforeDemo.java`
  - happens-beforeの規則（プログラム順、モニター、volatile、スレッドの開始と終了、推移性、`CountDownLatch`）を使ってデータを受け渡す例
- `LockFreeAlgorithms.java`
  - CASによるロックフリーのスタックとカウンター、ノードを再利用したときのABA問題、`AtomicStampedReference`による解決
- `FalseSharingDemo.java`
  - 同じキャッシュラインに載るカウンター、パディング、`@Contended`、セルに分けたカウンター（`LongAdder`の簡易版）の時間を比べる

`@Contended`はJDKの内部のアノテーション（`jdk.internal.vm.annotation.Contended`）なので、コンパイルに`--add-exports`が要ります。

```bash
javac -d out --add-exports java.base/jdk.internal.vm.annotation=ALL-UNNAMED src/main/java/com/example/memorymodel/*.java
java -cp out com.example.memorymodel.VisibilityProblems
java -cp out com.example.memorymodel.HappensBeforeDemo
java -cp out com.example.memorymodel.LockFreeAlgorithms
java -XX:-RestrictContended -cp out com.example.memorymodel.FalseSharingDemo
```

`FalseSharingDemo`と`LockFreeAlgorithms`が表示する時間と倍率は、CPU・OS・JVMの状態によって変わり、実行するたびにも変わります。
この節の観察の出力は、Apple M3（AArch64）のmacOSとJDK 25.0.2で得たものです。

## 1. メモリモデルは何を決めているか

1つのスレッドのなかでは、プログラムは書いた順に実行されたように見えます。
しかし、JITコンパイラーは結果が変わらない範囲で命令を並べ替え、値をレジスターに置いたままにします。
CPUも書き込みをいったんバッファーにためたり、キャッシュを通して読み書きしたりします。
JLSは、正しく同期していないプログラムで直感に反する結果が出る原因として、JITコンパイラーによる並べ替えとプロセッサーとメモリ階層を挙げています。
JLSはこれらをまとめて「コンパイラー」と呼んでいます。
<!-- claim: jmm-reordering-sources -->

メモリモデルは、こうした最適化があっても、ある読み込みがどの書き込みの値を見てよいかを決めた規則です。
JLSは規則だけを決め、それをどう実現するか（どの命令を使うか、どこで最適化を止めるか）はJVMとCPUに任せています。
この節では、仕様が決めていることと、JDK 25のHotSpotで観察できることを分けて書きます。

JLSの17.3には、メモリモデルがなぜ必要かをよく示す例があります。
`Thread.sleep`と`Thread.yield`には同期の意味がなく、`volatile`でないフィールド`done`を待つループでは、コンパイラーは`done`を一度だけ読んで使い回してよい、というものです。
その結果、ほかのスレッドが`done`を書き換えても、ループは終わらなくてかまいません。
<!-- claim: sleep-no-sync -->

## 2. 可視性：終わらないループ

### 観察する

`verify/StopFlag.java`は、ワーカースレッドに`while (!plainStop) { count++; }`を回させ、1秒後にmainスレッドが`plainStop = true`を書きます。
その後2秒待ってもワーカーが止まらなければ、そう表示して終了します。
<!-- claim: stop-plain-hangs -->
`-XX:+PrintCompilation`を付けて実行すると、次のように表示されます。

<!-- output: stop-osr -->
```
% 3 StopFlag::spinPlain @ 2 (16 bytes)
3 StopFlag::spinPlain (16 bytes)
% 4 StopFlag::spinPlain @ 2 (16 bytes)
% 3 StopFlag::spinPlain @ 2 (16 bytes) made not entrant
plainのフラグ: ワーカーは2秒待っても止まらない
```
<!-- /output -->

`%`の付いた行は、実行中のループの途中からコンパイル済みのコードへ切り替えるOSR（On-Stack Replacement）のコンパイルです。
ループはまずC1（レベル3）、続いてC2（レベル4）でコンパイルされ、フラグを立てても止まらなくなりました。
<!-- claim: stop-osr, stop-plain-hangs -->

同じプログラムを条件を変えて動かすと、結果は次のようになりました。

| 条件 | 結果 |
|---|---|
| `volatile`でないフラグ（既定） | 止まらない |
| `volatile`でないフラグ、`-Xint`（インタープリターだけ） | 止まる |
| `volatile`でないフラグ、`-XX:TieredStopAtLevel=1`（C1だけ） | 止まる |
| `volatile`のフラグ | 止まる |
| VarHandleの`setOpaque`・`getOpaque` | 止まる |
| VarHandleの`setRelease`・`getAcquire` | 止まる |
<!-- claim: stop-plain-hangs, stop-xint, stop-c1, stop-volatile, stop-opaque, stop-acquire -->

### 説明

C2は、ループのなかでフラグを書き換えるコードがないことを見て、フラグの読み込みをループの外へ出した（持ち上げた）と考えられます。
1で見たとおり、`volatile`でないフィールドではこの変形が仕様で許されています。
インタープリターやC1で止まったのは、たまたまその変形をしなかったからで、`volatile`のないプログラムが正しいことを意味しません。
`volatile`のフィールドでは、ほかのスレッドの書き込みと読み込みのあいだにあとで述べるhappens-beforeの関係ができるので、JITは読み込みを省けません。

第15章の`ThreadStopExample`がフラグに`volatile`を付けているのは、このためです。
ループのなかで`IO.println`のようなメソッドを呼ぶと、JITが持ち上げをしないことが多く、問題が見えにくくなります（`StopFlag`のループのなかがカウンターを増やすだけなのは、そのためです）。

## 3. happens-before

### 規則

JLSは、2つの操作のあいだのhappens-before（hb）という関係で、どの書き込みが見えるかを決めます。
hb(x, y)なら、xの結果はyから見え、xはyより前に順序づけられます。
hbの関係は、次の規則とその推移性から決まります。

- 同じスレッドのなかでは、プログラム順で前の操作は後の操作よりhbである
- モニターのアンロックは、同じモニターをその後でロックすることよりhbである
- `volatile`のフィールドへの書き込みは、その後の同じフィールドの読み込みよりhbである
- スレッドの`start()`の呼び出しは、開始したスレッドのすべての操作よりhbである
- スレッドのすべての操作は、ほかのスレッドがそのスレッドの`join()`から戻ることよりhbである
- オブジェクトのフィールドを既定値（0・`false`・`null`）で初期化することは、ほかのすべての操作よりhbである
- hb(x, y)かつhb(y, z)なら、hb(x, z)である
<!-- claim: hb-rules -->

これらの規則の多くは、JLSの17.4.4が決めるsynchronizes-withという関係から導かれます。
synchronizes-withの関係は、アンロックとその後のロック、`volatile`の書き込みとその後の読み込み、スレッドの開始、スレッドの終了とそれを`isAlive()`や`join()`で検出する操作、`interrupt()`と割り込みの検出のあいだにあります。
<!-- claim: sync-with -->

`java.util.concurrent`のクラスも、APIドキュメントにメモリの一貫性の保証を書いています。
たとえば`CountDownLatch`では、`countDown()`を呼ぶ前の操作は、`await()`から戻った後の操作よりhbです。
<!-- claim: countdownlatch-hb -->
`HappensBeforeDemo`の`CountDownLatchDemo`は、この保証でワーカーの結果を受け取っています。

### hbは「時間の順」ではない

hbは、実装がその順に実行することまでは求めません。
結果が仕様に合う限り並べ替えてよく、hbの関係にない別のスレッドからは、順序が入れ替わって見えることがあります。
<!-- claim: hb-not-time-order -->
第15章では「`volatile`の書き込みは即座にほかのスレッドから見える」と説明しましたが、仕様が決めているのは時間ではなく順序です。
`volatile`の書き込みの後に（同期の順序で）行われた読み込みは、その値を見る、という形で保証されています。

### データ競合と逐次一貫性

同じ変数への2つのアクセスで、少なくとも一方が書き込みのものを「競合するアクセス」といいます。
競合するアクセスがhbで順序づけられていなければ、プログラムにはデータ競合があります。
データ競合のないプログラム（正しく同期したプログラム）では、すべての実行が逐次一貫に見えます。
つまり、すべてのスレッドの操作を1列に並べ、各スレッドのなかではプログラム順を保ったものとして考えれば十分です。
<!-- claim: data-race -->

この保証があるので、共有する変数へのアクセスを`synchronized`や`volatile`で順序づけておけば、並べ替えやキャッシュを考えずにプログラムを読めます。
並べ替えが問題になるのは、データ競合のあるプログラムだけです。

## 4. 並べ替えを観察する

### 観察する

`verify/StoreBuffer.java`は、2つのスレッドに次の操作を20万回くり返させます（`x`と`y`は毎回0に戻します）。

| スレッド1 | スレッド2 |
|---|---|
| `x = 1;` | `y = 1;` |
| `r1 = y;` | `r2 = x;` |

書いた順に実行されるなら、どちらかの書き込みが先に起こるので、`r1`と`r2`の少なくとも一方は1になるはずです。
ところが、`volatile`でない変数では、両方が0を読む回が実際にありました。
<!-- claim: store-buffer-plain -->

<!-- output: store-buffer-plain -->
```
plain: 200000回のうち、両方が0を読んだ回数は1回以上
```
<!-- /output -->

4つの変数をすべて`volatile`にすると、両方が0になる回は一度もありません。
<!-- claim: store-buffer-volatile -->

<!-- output: store-buffer-volatile -->
```
volatile: 200000回のうち、両方が0を読んだ回数は0回
```
<!-- /output -->

### 説明

`volatile`でない場合、どちらのスレッドも、読み込みを書き込みより先に行ってかまいません（1つのスレッドのなかでは、`x`への書き込みと`y`の読み込みは互いに影響しないからです）。
JLSの17.4のTable 17.4-Aも、同じ形の並べ替えで直感に反する結果が出る例を示しています。
JITが命令の順を入れ替えても、CPUが書き込みをバッファーにためて後からメモリに反映しても、同じ結果になります。
この観察では、何回が両方0になったかは実行のたびに大きく変わり、インタープリターだけで動かすと一度も出ないこともありました。

`volatile`の場合は、4つの変数へのアクセスがすべて同期の操作になります。
JLSの17.4.4により、すべての同期の操作には、各スレッドのプログラム順と矛盾しない1つの全体の順序（同期の順序）があります。
その順序でどちらかの書き込みが先に来るので、両方が0を読むことはありません。

## 5. 原子性：失われる更新

### 観察する

`count++`は1つの式ですが、バイトコードでは読む・足す・書くの3つの命令になります。
`verify/Bytecode.java`の`incrementVolatile`を`javap -c -p`で見ると、次のようになります。

<!-- output: increment-bytecode -->
```
0: getstatic // Field count:I
3: iconst_1
4: iadd
5: putstatic // Field count:I
8: return
```
<!-- /output -->

`volatile`でないフィールドを増やす`incrementPlain`も、まったく同じ命令です。
<!-- claim: increment-bytecode -->
`volatile`は命令ではなく、フィールドのアクセスフラグ（`ACC_VOLATILE`）としてクラスファイルに記録されます（`javap -v`で見る）。

<!-- output: volatile-flag -->
```
static volatile int count;
flags: (0x0048) ACC_STATIC, ACC_VOLATILE
static int plainCount;
flags: (0x0008) ACC_STATIC
```
<!-- /output -->

JVMは、このフラグを見て、フィールドの読み書きに必要な順序の保証を付けます。
<!-- claim: volatile-flag -->

`verify/LostUpdates.java`では、4つのスレッドが同時に100万回ずつカウンターを増やします。

<!-- output: lost-updates -->
```
int（plain）のcount++: 400万より少ない（更新が失われた）
volatile intのcount++: 400万より少ない（更新が失われた）
synchronizedの中のcount++: 400万（正しい）
AtomicInteger.incrementAndGet: 400万（正しい）
LongAdder.increment: 400万（正しい）
```
<!-- /output -->
<!-- claim: lost-updates -->

### 説明

2つのスレッドが同じ値を読み、それぞれ1を足して書くと、2回の増加のうち1回が失われます。
`volatile`は1回ずつの読み書きの可視性と順序を保証しますが、読んでから書くまでのあいだにほかのスレッドが割り込むことは防ぎません。
`synchronized`は読む・足す・書くを1つのモニターのなかで行うので、ほかのスレッドが割り込めません。
`AtomicInteger`は、読む・足す・書くを1つの原子的な操作（CPUの原子的な命令やCAS）で行います。
`LongAdder`は、スレッドごとに別のセルを増やし、`sum()`で合計します（8で見るように、セルには`@Contended`が付いています）。

### longとdouble

JLSの17.7により、`volatile`でない`long`と`double`への1回の書き込みは、32ビットずつ2回の書き込みとして扱われてもかまいません。
`volatile`の`long`と`double`、および参照の読み書きは、常に原子的です。
<!-- claim: long-double -->
JLSは、64ビットの値を分けない実装を勧めていますが、それに頼らず、共有する`long`には`volatile`を付けるか同期を使います。

### Atomicクラスの中身

JDK 25のAtomicクラスがどの道具で原子的な操作をしているかを、`verify/AtomicInternals.java`でフィールドの型から調べると、次のようになります。

<!-- output: atomic-internals -->
```
AtomicInteger: jdk.internal.misc.Unsafe U
AtomicLong: jdk.internal.misc.Unsafe U
AtomicReference: java.lang.invoke.VarHandle VALUE
AtomicBoolean: java.lang.invoke.VarHandle VALUE
AtomicIntegerArray: java.lang.invoke.VarHandle AA
```
<!-- /output -->

`AtomicInteger`と`AtomicLong`はJDKの内部の`Unsafe`を、`AtomicReference`などは公開されたAPIの`VarHandle`（7）を使っています。
<!-- claim: atomic-internals -->
どちらを使うかは実装の選択で、APIドキュメントが保証するメモリの効果は同じです。

## 6. 安全な公開：ダブルチェックロッキングとfinal

### オブジェクトの公開は3段階

`instance = new Bytecode(42);`を`javap -c -p`で見ると、次の命令になります。

<!-- output: publish-bytecode -->
```
0: new // class Bytecode
3: dup
4: bipush 42
6: invokespecial // Method "<init>":(I)V
9: putstatic // Field instance:LBytecode;
12: return
```
<!-- /output -->

領域を確保する`new`、コンストラクターを実行する`invokespecial`、参照をフィールドに書く`putstatic`は、別々の操作です。
<!-- claim: publish-bytecode -->
同じスレッドのなかではこの順に見えますが、hbの関係にない別のスレッドからは、コンストラクターのなかの書き込みと`putstatic`の順序が入れ替わって見えてもかまいません（3）。
つまり、参照は見えたのに、フィールドはまだ既定値のまま、ということが仕様上は起こりえます。

### ダブルチェックロッキング

ロックの回数を減らすために、次のように書くことがあります（ダブルチェックロッキング）。

```java
class Singleton {
    private static volatile Singleton instance;   // volatileが要る

    static Singleton getInstance() {
        Singleton result = instance;              // 1回目の確認（ロックなし）
        if (result == null) {
            synchronized (Singleton.class) {
                result = instance;                // 2回目の確認（ロックの中）
                if (result == null) {
                    instance = result = new Singleton();
                }
            }
        }
        return result;
    }
}
```

`instance`が`volatile`でないと、1回目の確認はロックを取らないので、作ったスレッドの書き込みとのあいだにhbの関係がありません。
そのため、ほかのスレッドは`null`でない参照を見ても、コンストラクターの書き込みが見えるとは限りません。
`volatile`にすると、`instance`への書き込みとその後の読み込みのあいだにhbができ、プログラム順と推移性により、コンストラクターの書き込みも見えます。
`src/`の`VisibilityProblems.SafeInitialization`は、この形で書いています。

### finalフィールドの保証

finalフィールドには特別な保証があります。
コンストラクターでfinalフィールドを設定し、構築中に`this`をほかのスレッドから見える場所に書かなければ、オブジェクトへの参照を見たスレッドは、同期がなくてもfinalフィールドの正しい値を見ます。
finalフィールドが指す配列やオブジェクトも、少なくともその時点の内容が見えます。
<!-- claim: final-fields -->
第6章で学んだ不変オブジェクトを、同期なしにスレッドの間で共有できるのは、この保証があるからです。
ただし、保証されるのはfinalフィールドだけで、finalでないフィールドの値は保証されません。

### 遅延初期化のホルダークラス

ダブルチェックロッキングの代わりに、クラスの初期化の規則を使う方法があります。
クラスは、そのstaticメソッドの呼び出しや、定数でないstaticフィールドの使用の直前に初期化されます。
初期化は、JVMがクラスごとの初期化ロックを取って1回だけ行います。
<!-- claim: class-init -->

```java
class Config {
    private static final class Holder {
        static final Config INSTANCE = new Config();   // Holderの初期化で1回だけ作られる
    }

    static Config getInstance() {
        return Holder.INSTANCE;                         // ここで初めてHolderが初期化される
    }
}
```

`verify/LazyHolder.java`で、8つのスレッドから同時に`getInstance()`を呼ぶと、次のようになります。

<!-- output: lazy-holder -->
```
mainの開始（まだConfigは作られていない）
Configを作った（worker-N）
8つのスレッドが同じインスタンスを得た: true
```
<!-- /output -->
<!-- claim: lazy-holder -->

`volatile`も`synchronized`も書かずに、遅延初期化と安全な公開ができます。
インスタンスが1つでよいだけなら、enum（第8章）を使う方法もあります。

## 7. VarHandleのアクセスモードとフェンス

`java.lang.invoke.VarHandle`を使うと、フィールドや配列の要素を、目的に合った強さのアクセスモードで読み書きできます。
アクセスモードは、次の順に強くなります。

| アクセスモード | 読み書きのメソッド | 保証 |
|---|---|---|
| plain | `get`、`set` | ふつうのフィールドと同じ。ほかのスレッドに対する順序の制約はない |
| opaque | `getOpaque`、`setOpaque` | 同じ変数へのアクセスについて、一貫した順序がある |
| acquire/release | `getAcquire`、`setRelease` | `setRelease`より前のアクセスが、その値を読んだ`getAcquire`より後のアクセスより前に順序づけられる |
| volatile | `getVolatile`、`setVolatile` | `volatile`のフィールドと同じ。すべてのvolatileの操作に1つの全体の順序がある |
<!-- claim: varhandle-modes -->

plainの読み書きが原子的なのは、参照と32ビット以下の基本型だけです（JLSの17.7と同じく、`long`と`double`は含まれません）。
<!-- claim: varhandle-plain-atomic -->
2の観察では、opaqueでもacquire/releaseでもループは止まりました。
opaqueはほかの変数との順序を保証しないので、フラグと一緒にデータを渡すときは、少なくともacquire/releaseを使います。

```java
class Flag {
    private static final VarHandle READY;
    private boolean ready;
    private int data;

    static {
        try {
            READY = MethodHandles.lookup()
                    .findVarHandle(Flag.class, "ready", boolean.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    void publish(int value) {
        data = value;                   // ふつうの書き込み
        READY.setRelease(this, true);   // これより前の書き込みを公開する
    }

    Integer tryRead() {
        if ((boolean) READY.getAcquire(this)) {   // trueが見えたら
            return data;                          // publishで書いたdataも見える
        }
        return null;
    }
}
```

`VarHandle`には、変数を指定せずに順序だけを強制するフェンスもあります。

<!-- output: varhandle-fences -->
```
VarHandleのフェンス: acquireFence fullFence loadLoadFence releaseFence storeStoreFence
```
<!-- /output -->
<!-- claim: varhandle-fences -->

`loadLoadFence`は前の読み込みと後の読み込み、`storeStoreFence`は前の書き込みと後の書き込みが入れ替わらないようにし、`fullFence`はすべての読み書きについて入れ替わりを防ぎます。
CPUごとに必要な命令（メモリバリア）は違いますが、JVMがこれらのフェンスやアクセスモードをそのCPUの命令に置き換えます。
ふつうのアプリケーションでは、`volatile`や`java.util.concurrent`のクラスで足ります。
VarHandleの弱いモードは、ライブラリの作者が性能を詰めるときの道具です。

## 8. false sharingと@Contended

### しくみ

CPUのキャッシュは、キャッシュラインという決まった大きさの単位でメモリを扱います（大きさはCPUによって異なります）。
別々のスレッドが別々の変数に書き込んでも、2つの変数が同じキャッシュラインに載っていると、CPUは書き込みのたびにそのラインをコア間で受け渡します。
これをfalse sharing（偽の共有）といい、結果は正しいまま、更新が遅くなります。
<!-- claim: contended-jep142 -->
JMMは正しさだけを決める規則なので、false sharingは仕様の外の、性能の問題です。

### 観察する

`verify/ContendedLayout.java`は、JOLでフィールドのオフセット（オブジェクトの先頭からの位置）を表示します。

<!-- output: plain-layout -->
```
== PlainCounters ==
counter1: オフセット16
counter2: オフセット24（前のカウンターから8バイト）
counter3: オフセット32（前のカウンターから8バイト）
counter4: オフセット40（前のカウンターから8バイト）
インスタンスの大きさ: 48バイト
== ManualPadding ==
counter1: オフセット72
counter2: オフセット192（前のカウンターから120バイト）
インスタンスの大きさ: 200バイト
```
<!-- /output -->

`volatile long`のカウンターを4つ並べると8バイトおきに置かれるので、4つとも1つのキャッシュラインに載りえます。
7つの`long`で前後を埋めた`ManualPadding`では、隣のカウンターと120バイト離れました。
<!-- claim: plain-layout -->
64バイトのキャッシュラインなら別々の行になりますが、128バイトのキャッシュラインでは、置かれる位置によって同じ行に載ることがあります。
また、フィールドを宣言の順に並べるという仕様はないので、手で埋める方法はJVMの実装に頼っています。

HotSpotには、フィールドの前に詰め物を入れる`@Contended`（`jdk.internal.vm.annotation.Contended`）があります。
このアノテーションは既定で有効ですが、アプリケーションのクラスでは無視され、詰め物の幅は既定で128バイトです。
<!-- claim: contended-flags -->

<!-- output: contended-restricted -->
```
== ContendedCounters（アプリケーションのクラス） ==
counter1: オフセット16
counter2: オフセット24（前のカウンターから8バイト）
インスタンスの大きさ: 32バイト
```
<!-- /output -->
<!-- claim: contended-restricted -->

`-XX:-RestrictContended`を付けると、各フィールドの前に128バイトの詰め物が入り、2つのカウンターは136バイト離れます。
<!-- claim: contended-padded -->

<!-- output: contended-padded -->
```
== ContendedCounters（アプリケーションのクラス） ==
counter1: オフセット144
counter2: オフセット280（前のカウンターから136バイト）
インスタンスの大きさ: 288バイト
```
<!-- /output -->
<!-- claim: contended-padded -->

JDKのクラスでは、`-XX:-RestrictContended`がなくても`@Contended`が効きます。
`LongAdder`が内部で使う`Striped64$Cell`には、クラスに`@Contended`が付いています（`javap -v`で`RuntimeVisibleAnnotations`に表示されます）。
<!-- claim: striped64-annotation -->

<!-- output: striped64-cell -->
```
OFF  SZ   TYPE DESCRIPTION               VALUE
  0   8        (object header: mark)     N/A
  8   4        (object header: class)    N/A
 12 132        (alignment/padding gap)
144   8   long Cell.value                N/A
Instance size: 152 bytes
```
<!-- /output -->
<!-- claim: striped64-cell -->

### 説明

`@Contended`は、JDK 8（JEP 142）で入ったHotSpotの機能で、Javaの言語仕様ではありません。
<!-- claim: contended-jep142 -->
公開されたAPIではないので、アプリケーションで使うには、コンパイル時の`--add-exports java.base/jdk.internal.vm.annotation=ALL-UNNAMED`と実行時の`-XX:-RestrictContended`の両方が要ります。
`ContendedLayout`は、だれでも同じ手順で動かせるように、`@Contended`を付けたクラスを実行中に`javax.tools`のjavacでコンパイルして読み込んでいます。
アプリケーションでfalse sharingを避けたいときは、まず`LongAdder`や`java.util.concurrent`のクラスを使い、効果は`FalseSharingDemo`やJMHで測って確かめます。

## 9. 仮想スレッドとsynchronized

JLSの第17章のスレッドは`Thread`クラスで表され、仮想スレッドも`Thread`のインスタンスです。
そのため、この節のhappens-beforeの規則は、仮想スレッドにもそのまま当てはまります。
<!-- claim: threads-are-thread -->

仮想スレッドは、プラットフォームスレッド（キャリア）に載って動き、ブロックするとキャリアから降りて、キャリアをほかの仮想スレッドに譲ります。
以前は、`synchronized`のなかでブロックした仮想スレッドはキャリアから降りられず（ピン留め）、そのためにReentrantLockへ書き換えることが勧められていました。
JDK 24（JEP 491）からは、`synchronized`のなかでブロックしてもキャリアを手放すので、この書き換えは必要ありません。
<!-- claim: jep491 -->

### 観察する

`verify/VirtualPinning.java`は、キャリアを1つに制限し（`jdk.virtualThreadScheduler.parallelism=1`）、4つの仮想スレッドにそれぞれ200ミリ秒眠らせて、同時に眠っていた数を数えます。
<!-- claim: vthread-sync-unpinned, vthread-clinit-pinned -->

<!-- output: vthread-sync-unpinned -->
```
synchronizedの中で同時に眠れた仮想スレッドの数（キャリアは1つ）: 4
```
<!-- /output -->
<!-- claim: vthread-sync-unpinned -->

`synchronized`のなかで眠っても、4つが同時に眠れました。
1つしかないキャリアを、眠っているあいだに手放しているということです。
一方、クラスの初期化（static初期化子）のなかで眠ると、同時に眠れるのは1つだけでした。
JEP 491も、クラスの初期化のなかでブロックする場合を、残っているピン留めの例に挙げています。

<!-- output: vthread-clinit-pinned -->
```
clinitの中で同時に眠れた仮想スレッドの数（キャリアは1つ）: 1
```
<!-- /output -->
<!-- claim: vthread-clinit-pinned -->

JDK 21で使えたシステムプロパティ`jdk.tracePinnedThreads`は、JEP 491で削除され、指定しても何も起こりません（上のクラスの初期化の例に付けても、何も表示されません）。
ピン留めを調べるには、JFR（JDK Flight Recorder）の`jdk.VirtualThreadPinned`イベントを使います。
<!-- claim: trace-pinned-removed -->

## 10. ロックフリーのアルゴリズムとABA問題

`src/`の`LockFreeAlgorithms`は、`AtomicReference`の`compareAndSet`（CAS）でスタックを作ります。
CASは「値がまだ予想どおりなら新しい値に変える」を原子的に行い、失敗したら読み直してやり直します。
ロックを持つスレッドがいないので、デッドロックは起きません。
ただし、競合が激しいとCASの失敗とやり直しが増えます。

CASは参照が同じかどうかしか比べないので、値がA→B→Aと変わったことを見分けられません（ABA問題）。
Javaでは、新しいノードを作る限り、取り除かれたノードと同じ参照が戻ってくることはないので、GCがABA問題を防いでくれます。
デモの`simulateABAProblem`は、取り出したノードを再利用して、取り除いたはずのBがスタックに戻る様子を見せます。
`AtomicStampedReference`は参照と一緒にスタンプ（版の番号）を比べるので、参照が元に戻ってもスタンプが違えばCASは失敗します。

## よくある誤解

- `volatile`の書き込みは即座にほかのスレッドから見える
  - 仕様が決めているのは時間ではなく順序で、その後に行われた読み込みが値を見る（3）
- `volatile`を付ければ`count++`は安全になる
  - 読む・足す・書くの3つの操作のままで、更新は失われる（5）
  <!-- claim: increment-bytecode, lost-updates -->
- 自分のPCで問題が起きなければ、同期は要らない
  - 起こりうる結果がJITやCPUによって出たり出なかったりする（インタープリターやC1では止まったループが、C2では止まらなかった。2）
  <!-- claim: stop-plain-hangs, stop-xint -->
- ダブルチェックロッキングは`synchronized`があるので`volatile`は要らない
  - 1回目の確認はロックの外でhbの関係がないので、`volatile`にするかホルダークラスを使う（6）
- 不変オブジェクトでも、ほかのスレッドに渡すには同期が要る
  - finalフィールドは、構築中に`this`を漏らさなければ、同期なしで正しい値が見える（6）
  <!-- claim: final-fields -->
- 仮想スレッドでは`synchronized`を避けて`ReentrantLock`を使う
  - JDK 24からは、`synchronized`のなかでブロックしてもキャリアを手放す（9）
  <!-- claim: jep491, vthread-sync-unpinned -->

## 実践的なガイドライン

- まず、共有する状態を減らす（不変オブジェクト、スレッドごとのデータ、メッセージの受け渡し）
- 単純なフラグは`volatile boolean`にする
- 複数の変数をまとめて変えるときは、`synchronized`か`ReentrantLock`を使う
- カウンターは`AtomicInteger`、多くのスレッドから更新するなら`LongAdder`を使う
- 遅延初期化は、ホルダークラスのイディオムか、`volatile`を付けたダブルチェックロッキングで書く
- 性能の問題は推測せず、JFRやJMHで測ってから手を入れる
- 並行処理の誤りはテストで見つからないことがあるので、仕様の規則で正しさを確かめ、テストはその補助と考える

## 参考資料

- Java言語仕様（Java SE 25）
  - Chapter 17 Threads and Locks
    + 17.3「Sleep and Yield」
    + 17.4「Memory Model」
    + 17.4.4「Synchronization Order」
    + 17.4.5「Happens-before Order」
    + 17.5「final Field Semantics」
    + 17.7「Non-Atomic Treatment of double and long」
  - `https://docs.oracle.com/javase/specs/jls/se25/html/jls-17.html`
  - 12.4「Initialization of Classes and Interfaces」
  - `https://docs.oracle.com/javase/specs/jls/se25/html/jls-12.html`
- Java仮想マシン仕様（Java SE 25）4.5「Fields」（`ACC_VOLATILE`）
  - `https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html`
- `java.lang.invoke.VarHandle`（Java SE 25 API）
  - `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/invoke/VarHandle.html`
- `java.util.concurrent`パッケージの「Memory Consistency Properties」（Java SE 25 API）
  - `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/package-summary.html`
- JEP 142: Reduce Cache Contention on Specified Fields
  - `https://openjdk.org/jeps/142`
- JEP 491: Synchronize Virtual Threads without Pinning
  - `https://openjdk.org/jeps/491`
- JOL（Java Object Layout）
  - `https://github.com/openjdk/jol`
