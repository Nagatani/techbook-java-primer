# 付録B.14 Java Memory ModelとHappens-Before関係

Java並行プログラミングにおける最も重要かつ難解な概念の1つである、Java Memory Model（JMM）とHappens-Before関係について学習できるプロジェクトです。

## 概要

現代のコンピューターは、パフォーマンスを高めるためにさまざまな最適化を行います。
CPUやコンパイラーによる命令の並べ替え、キャッシュ、ストアバッファなどです。
これらは1つのスレッドの処理を速くしますが、マルチスレッドのプログラムでは予期しない動作を引き起こすことがあります。
Java Memory Modelは、こうした最適化があってもプログラムがどう振る舞うかを、Java言語仕様（JLS）の第17章で定めたものです。

## なぜJava Memory Modelの理解が重要なのか

並行処理のバグは、テストでは再現しにくく、本番の負荷やCPUの違いで初めて現れることがあります。
このプロジェクトで扱う典型的な問題は次のとおりです。

1. 可視性の問題による無限ループ
   - あるスレッドが`stopRequested`を書き換えても、別のスレッドからその変更が見えず、ループが終わらない
2. 初期化の見切り発車
   - 構築の途中のオブジェクトが、別のスレッドから参照される
3. `volatile`のないダブルチェックロッキング
   - 命令の並べ替えにより、初期化の終わっていないオブジェクトの参照が見える
4. 複合操作の競合
   - `volatile`を付けても`count++`は原子的にならず、更新が失われる

## サンプルコード構成

### 1. メモリ可視性問題のデモンストレーション
- `VisibilityProblems.java`
  - BrokenStopFlag: `volatile`のないフラグ。JITがフラグの読み込みをループの外へ出すと、止まらなくなる
  - CorrectStopFlag: `volatile`による修正版
  - UnsafeInitialization と SafeInitialization: 初期化の問題（デモでは、確認と代入のあいだに割り込まれて複数回生成される様子が見える）
  - VolatileVsSynchronized: `volatile`と`synchronized`の違い

### 2. Happens-Before関係の実演
- `HappensBeforeDemo.java`
  - プログラム順序規則
  - モニターロック規則
  - volatile変数規則
  - スレッド開始・終了規則
  - Happens-Beforeの推移性
  - CountDownLatchによる同期の保証

### 3. ロックフリーアルゴリズムの実装
- `LockFreeAlgorithms.java`
  - LockFreeStack: Compare-And-Swap（CAS）によるロックフリースタック
  - ABA問題のデモンストレーション（ノードを再利用すると、取り除いたはずの要素がスタックに戻る）
  - ABASafeStack: スタンプ付き参照（`AtomicStampedReference`）による解決
  - LockFreeCounter: CASによるカウンター

### 4. False Sharingとパフォーマンス最適化
- `FalseSharingDemo.java`
  - FalseSharingCounters: 同じキャッシュラインに載るカウンター
  - PaddedCounters: パディングによる解決
  - ContendedCounters: `@Contended`アノテーションによる解決
  - StripedCounter: カウンターを分割して競合を減らす実装
  - メモリアクセスパターンの比較

## 実行方法

### コンパイルと実行
```bash
cd appendix/b14-java-memory-model

# @Contendedは JDK の内部API（jdk.internal.vm.annotation）なので、--add-exports を付けてコンパイルする
javac -d out --add-exports java.base/jdk.internal.vm.annotation=ALL-UNNAMED src/main/java/com/example/memorymodel/*.java

# 可視性問題のデモ
java -cp out com.example.memorymodel.VisibilityProblems

# Happens-Before関係のデモ
java -cp out com.example.memorymodel.HappensBeforeDemo

# ロックフリーアルゴリズムのデモ
java -cp out com.example.memorymodel.LockFreeAlgorithms

# False Sharingのデモ
java -cp out com.example.memorymodel.FalseSharingDemo
```

### JVMオプション
```bash
# アプリケーションのクラスで@Contendedを有効にする（指定しないとアノテーションは無視される）
java -XX:-RestrictContended -cp out com.example.memorymodel.FalseSharingDemo

# JITコンパイラーを使わずインタープリターだけで実行する
# （BrokenStopFlagは、JITがフラグの読み込みをループの外へ出さないため、止まることが多い）
java -Xint -cp out com.example.memorymodel.VisibilityProblems
```

可視性の問題が実際に起きるかどうかは、JITの判断やCPUによって変わります。
問題が見えなかったからといって、プログラムが正しいとは限りません。

## 学習ポイント

### 1. Java Memory Modelの基礎
- メモリ可視性
  - あるスレッドの変更が、別のスレッドから見えることの保証
- 原子性
  - 操作が分割できない単位として実行されることの保証
- 順序性
  - 操作が決まった順序で行われたように見えることの保証
- volatile
  - 可視性と順序性を保証する。1回の読み書きは原子的（`long`と`double`も含む）だが、`count++`のような複合操作は原子的にならない

### 2. Happens-Before関係の主な規則
JLS 17.4.5に定められた規則のうち、よく使うものを挙げます。

1. プログラム順序
   - 同じスレッドの中では、先の操作は後の操作よりhappens-beforeである
2. モニターロック
   - モニターのアンロックは、同じモニターをその後でロックすることよりhappens-beforeである
3. volatile変数
   - volatile変数への書き込みは、その変数をその後で読むことよりhappens-beforeである
4. スレッド開始
   - `Thread.start()`の呼び出しは、開始されたスレッドの操作よりhappens-beforeである
5. スレッド終了
   - スレッドの操作は、別のスレッドがそのスレッドの`join()`から戻ることよりhappens-beforeである
6. 推移性
   - A HB B かつ B HB C ならば A HB C

このほか、`interrupt()`の呼び出しは割り込まれたスレッドがそれを検出することよりhappens-beforeである、といった規則もあります。
`java.util.concurrent`のクラス（`CountDownLatch`、`ConcurrentHashMap`など）も、APIドキュメントにhappens-beforeの保証を書いています。

### 3. ロックフリープログラミング
- CAS操作
  - Compare-And-Swapによる原子的な更新
- ABA問題
  - 値がA→B→Aと変わったことをCASが見分けられず、誤動作する。Javaでは新しいノードを作れば参照が一致しないため起きないが、ノードを再利用すると起きる
- 解決策
  - スタンプ付き参照（`AtomicStampedReference`）
- パフォーマンス
  - 競合が激しいとCASの失敗と再試行が増える。`LongAdder`のように競合を分散させる工夫がある

### 4. False Sharingの最適化
- キャッシュライン
  - 多くのCPUで64バイト。Apple Siliconでは128バイト
- False Sharing
  - 別々の変数が同じキャッシュラインに載り、別々のスレッドが書き込むと、互いのキャッシュを無効にし合う
- 対策
  - パディング、`@Contended`アノテーション
- トレードオフ
  - メモリ使用量と性能のバランス

## VarHandleとメモリの順序

`java.lang.invoke.VarHandle`（Java 9以降）を使うと、フィールドや配列の要素を、目的に合った強さのメモリの順序で読み書きできます。
`AtomicInteger`などのクラスも、内部ではこの仕組みを使っています。

| アクセスモード | 読み書きのメソッド | 保証 |
|---|---|---|
| plain | `get`、`set` | 通常のフィールドと同じ。ほかのスレッドへの可視性も順序も保証しない |
| opaque | `getOpaque`、`setOpaque` | 同じ変数への書き込みが、いずれほかのスレッドから見える。ほかの変数との順序は保証しない |
| acquire/release | `getAcquire`、`setRelease` | `setRelease`より前の書き込みが、`getAcquire`で値を読んだスレッドから見える |
| volatile | `getVolatile`、`setVolatile` | `volatile`フィールドと同じ。すべてのvolatileのアクセスに1つの順序がある |

`compareAndSet`などの原子的な操作もあります。

```java
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

class Flag {
    private boolean ready;
    private int data;
    private static final VarHandle READY;

    static {
        try {
            READY = MethodHandles.lookup().findVarHandle(Flag.class, "ready", boolean.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    void publish(int value) {
        data = value;              // 通常の書き込み
        READY.setRelease(this, true);  // これより前の書き込みを公開する
    }

    Integer tryRead() {
        if ((boolean) READY.getAcquire(this)) {  // trueが見えたら
            return data;                         // publishで書いたdataも見える
        }
        return null;
    }
}
```

ふつうのアプリケーションでは、`volatile`や`java.util.concurrent`のクラスで足ります。
VarHandleの弱いモードは、ライブラリの作者が性能を詰めるときの道具です。

## 仮想スレッドとメモリモデル

仮想スレッド（Java 21で正式）も`Thread`であり、Java Memory Modelの規則はプラットフォームスレッドとまったく同じです。
`volatile`、`synchronized`、`start()`と`join()`によるhappens-beforeは、仮想スレッドでもそのまま成り立ちます。

- 仮想スレッドが`synchronized`の中でブロックすると、以前は土台のプラットフォームスレッドを手放せなくなる「ピン留め」が起きた
- JDK 24（JEP 491）でこの制約が解消され、`synchronized`の中でブロックしても土台のスレッドを手放せるようになった。Java 25では、ピン留めを避けるために`synchronized`を`ReentrantLock`に書き換える必要はない
- 仮想スレッドは大量に作れるので、`ThreadLocal`に大きなオブジェクトを持たせるとメモリを多く使う点には注意する

## 実践的なガイドライン

### 1. 設計原則

可能な限り不変オブジェクトを使います。

```java
public final class ImmutablePoint {
    private final int x, y;

    public ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
    // finalフィールドだけを持ち、構築後に変わらないので同期が不要
}
```

目的に合った同期の仕組みを選びます。

- 単純な状態フラグ
  - `volatile boolean`
- 複数の変数をまとめて変える状態遷移
  - `synchronized`ブロックや`ReentrantLock`
- 頻繁に更新するカウンター
  - `AtomicInteger`、競合が激しければ`LongAdder`
- 読み込みが多いデータ
  - `ReadWriteLock`、`ConcurrentHashMap`

### 2. パフォーマンス測定

`System.nanoTime()`による簡易な測定は、JITのウォームアップやGCの影響を受けます。
正確に比べるにはJMHを使います（付録B.12のプロジェクトでJMHを使っています）。

### 3. デバッグとテスト
- 並行性のバグは再現しにくく、タイミングに依存する
- OpenJDKのjcstress（Java Concurrency Stress tests）は、並行処理の結果として起こりうる値を網羅的に試すためのツール
- SpotBugsなどの静的解析で、同期の誤りの一部を見つけられる
- ストレステストでは、スレッド数や実行回数を増やし、複数のCPUで試す

## ハードウェアとの関係

### CPUアーキテクチャの違い
- x86/x64
  - 比較的強いメモリモデル（TSO）。ストアの後のロードの並べ替えだけが起こる
- ARM（Apple Siliconを含む）
  - 弱いメモリモデル。より多くの並べ替えが起こり、バリアが多く必要になる

JMMを守って書いたプログラムは、どちらのCPUでも正しく動きます。
x86で動いていた誤ったプログラムが、ARMで初めて失敗することがあります。

### メモリバリアの種類
- LoadLoad
  - 前のロードを、後のロードより先に完了させる
- StoreStore
  - 前のストアを、後のストアより先に見えるようにする
- LoadStore
  - 前のロードを、後のストアより先に完了させる
- StoreLoad
  - 前のストアを、後のロードより先に見えるようにする。多くのCPUで最もコストが高い

## 実行結果について

`VisibilityProblems`の`VolatileVsSynchronized`では、`volatile`のカウンターは更新が失われて期待した値（10000）より小さくなり、`synchronized`のカウンターは10000になります。
`FalseSharingDemo`では、パディングや`@Contended`（`-XX:-RestrictContended`を付けたとき）で、カウンターの更新が速くなります。
時間や倍率は、CPU、OS、JDK、実行のたびに変わります。

## 関連技術

- `java.util.concurrent`
  - 高水準の並行ユーティリティ
- Atomicクラス
  - ロックフリーな原子操作
- Lockの実装
  - `ReentrantLock`、`ReadWriteLock`
- Executorフレームワーク
  - スレッドプールと仮想スレッドの管理
- Fork/Joinフレームワーク
  - 分割統治のアルゴリズム

## まとめ

Java Memory Modelの理解は、正しく高性能な並行プログラムを書くために欠かせません。
このプロジェクトでは、メモリ可視性、Happens-Before関係、ロックフリープログラミング、False Sharingを実際に動かして学べます。
ただし、過度な最適化は避け、まず正しさを優先し、測定に基づいて最適化してください。
