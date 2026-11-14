# 付録B.01 JVMアーキテクチャとバイトコード

Java Virtual Machine (JVM) の内部アーキテクチャ、ランタイムデータエリア、バイトコード命令、JITコンパイレーションについて詳細に学習できるプロジェクトです。

## 概要

JavaプログラムはJavaソースコードからバイトコードにコンパイルされ、JVM上で実行されます。JVMの内部構造、メモリ管理、クラスローディング、JITコンパイレーションなどの仕組みを理解することで、より効率的なJavaプログラムの設計と、パフォーマンス問題のトラブルシューティングが可能になります。

## なぜJVMアーキテクチャを理解するのか

JVMの仕組みを知っていると、次のような問題の原因を考えやすくなります。

- `OutOfMemoryError`
    + 参照を持ち続けるコレクション（`static`なキャッシュなど）がGCで回収されず、ヒープを使い切る
- GCによる停止
    + ヒープの大きさやオブジェクトの寿命によって、GCの頻度や停止時間が変わる
- 起動直後だけ遅い
    + 起動直後はインタープリターで実行され、よく呼ばれるメソッドから順にJITコンパイルされる

```java
// 参照を持ち続けるために回収されない例
public class MemoryLeak {
    private static final List<Object> cache = new ArrayList<>();
    
    public void processData() {
        for (int i = 0; i < 1000000; i++) {
            cache.add(new BigObject()); // cacheから消さない限りGCで回収されない
        }
    }
}
```

## サンプルコード構成

### 1. JVMアーキテクチャの全体像
- `JVMArchitectureDemo.java`: JVMの内部構造を標準APIで観察するデモ
  - クラスローダー階層の調査と可視化
  - ランタイムデータエリア（ヒープ、スタック、メタスペース）の分析
  - メモリ世代とオブジェクトライフサイクルの実証
  - スレッドとスタック領域の動作確認（スタックの深さはフレームの数で表示）
  - ガベージコレクション情報の監視
  - JVMランタイム情報の詳細表示

### 2. バイトコード分析とJIT最適化
- `BytecodeAnalysisDemo.java`: バイトコードレベルの詳細理解
  - 基本的なバイトコード命令セット（算術、制御フロー、オブジェクト操作）
  - JITコンパイレーション前後の実行時間の比較
  - 予測しやすい分岐と予測しにくい分岐の比較
  - 小さなメソッドの呼び出しと直接の計算の比較（インライン化の効果）

## 実行方法

### コンパイルと実行
```bash
javac -d . src/main/java/com/example/jvm/*.java

# JVMアーキテクチャと内部構造のデモ
java com.example.jvm.JVMArchitectureDemo

# バイトコード分析とJIT最適化のデモ
java com.example.jvm.BytecodeAnalysisDemo
```

### JVMフラグを使用した詳細分析
```bash
# JITコンパイレーション情報を表示
java -XX:+PrintCompilation com.example.jvm.BytecodeAnalysisDemo

# インライン化決定を表示（診断用のオプションは-XX:+UnlockDiagnosticVMOptionsを先に書く）
java -XX:+PrintCompilation -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining \
     com.example.jvm.BytecodeAnalysisDemo

# インライン化を止めて比べる
java '-XX:CompileCommand=dontinline,*InlineCandidate.*' com.example.jvm.BytecodeAnalysisDemo

# ガベージコレクション情報を表示
java -Xlog:gc* com.example.jvm.JVMArchitectureDemo

# クラスローディング情報を表示
java -Xlog:class+load com.example.jvm.JVMArchitectureDemo

# ヒープの初期サイズと最大サイズを指定
java -Xms256m -Xmx512m com.example.jvm.JVMArchitectureDemo

# JITの閾値やGCなどの既定値を確かめる
java -XX:+PrintFlagsFinal -version
```

### バイトコード表示
```bash
# コンパイル済みクラスのバイトコードを表示（$をシェルに展開させないよう引用符で囲む）
javap -c 'com.example.jvm.BytecodeAnalysisDemo$BasicBytecodeDemo'

# より詳細な情報（定数プール、アクセス修飾子等）
javap -v com.example.jvm.JVMArchitectureDemo
```

## 学習ポイント

### 1. JVMアーキテクチャの全体構造

#### 主要コンポーネント
```
┌─────────────────────────────────────────────────────────────┐
│                         JVM                                   │
├─────────────────────────────────────────────────────────────┤
│  Class Loader Subsystem                                      │
│  ├─ Bootstrap Class Loader (java.base etc.)                  │
│  ├─ Platform Class Loader (java.sql etc.)                    │
│  └─ Application Class Loader (user classes)                  │
├─────────────────────────────────────────────────────────────┤
│  Runtime Data Areas                                          │
│  ├─ Method Area (Metaspace)                                  │
│  ├─ Heap (Young Gen + Old Gen)                              │
│  ├─ Java Stacks (per thread)                                │
│  ├─ PC Registers (per thread)                               │
│  └─ Native Method Stacks                                    │
├─────────────────────────────────────────────────────────────┤
│  Execution Engine                                            │
│  ├─ Interpreter                                             │
│  ├─ JIT Compiler (C1 + C2)                                  │
│  └─ Garbage Collector                                       │
└─────────────────────────────────────────────────────────────┘
```

#### クラスローダー階層
- Bootstrap ClassLoader
    + `java.base`など中核のモジュールのクラス（`String`、`Object`、`HashMap`など）を読み込む。Javaからは`null`に見える
- Platform ClassLoader
    + `java.sql`など、Java SEのそのほかの一部のモジュールを読み込む
- Application ClassLoader
    + クラスパス（モジュールパス）上のアプリケーションのクラスを読み込む
- Custom ClassLoader
    + アプリケーションが独自に定義するクラスローダー

`JVMArchitectureDemo`を実行すると、`String`・`HashMap`・`ConcurrentHashMap`はどれもBootstrap ClassLoaderで読み込まれていることを確かめられます。

### 2. ランタイムデータエリアの詳細

#### ヒープメモリ構造（世代別GCの概念図）
```
┌─────────────────────────────────────────────────────────┐
│                    Heap Memory                          │
├─────────────────────────────────────────────────────────┤
│  Young Generation                                       │
│  ├─ Eden Space      (新規オブジェクト)                    │
│  └─ Survivor x 2    (GCを生き延びたオブジェクト。         │
│                      2つを交互に使う)                    │
├─────────────────────────────────────────────────────────┤
│  Old Generation                                         │
│  └─ Tenured Space   (何回かGCを生き延びた長寿命          │
│                      オブジェクト)                       │
└─────────────────────────────────────────────────────────┘
```

これは世代別GCの考え方を示す概念図です。
Java 25の既定のGCであるG1は、ヒープを同じ大きさの多数のリージョンに分け、Eden・Survivor・Oldをリージョンの集合として扱います（図のように連続した領域ではありません）。
低遅延のZGC（`-XX:+UseZGC`）も、JDK 24以降は世代別のモードだけになりました。

#### オブジェクトヘッダー
ヒープ上の各オブジェクトの先頭には、GCやロック、クラスの情報を持つヘッダーがあります。
Java 25では、ヘッダーを小さくしてメモリを節約するCompact Object Headers（JEP 519）が正式な機能になりました。
既定では無効で、`-XX:+UseCompactObjectHeaders`で有効にします。

#### スタックフレーム構造
```
Each Stack Frame contains:
├─ Local Variable Array  (ローカル変数)
├─ Operand Stack        (計算用スタック)
└─ Frame Data           (定数プール参照、例外処理情報)
```

### 3. バイトコード命令セットの分類

#### 主要な命令カテゴリ
| カテゴリ | 命令例 | 用途 |
|----------|--------|------|
| ロード/ストア | `iload`, `astore` | 変数の読み書き |
| 算術演算 | `iadd`, `imul` | 数値計算 |
| 型変換 | `i2l`, `f2d` | データ型変換 |
| オブジェクト操作 | `new`, `invokevirtual` | オブジェクト生成・操作 |
| 制御フロー | `ifeq`, `goto` | 条件分岐・ループ |
| メソッド呼び出し | `invokestatic`, `invokeinterface` | メソッド実行 |

#### メソッド呼び出し命令の詳細
```java
// 静的メソッド
Math.sqrt(4.0)                    // invokestatic

// インスタンスメソッド
"hello".length()                  // invokevirtual

// インターフェイスメソッド
list.add("item")                  // invokeinterface

// コンストラクター・super.メソッド()
new Object()                      // new + invokespecial <init>

// privateメソッド（Java 11以降）とfinalメソッドもinvokevirtual
// （オーバーライドされないので、JVMは呼び出し先を1つに決められる）

// ラムダ式・メソッド参照
() -> IO.println("hi")    // invokedynamic
```

### 4. JITコンパイレーションの最適化階層

#### ティアードコンパイレーション
```
Level 0: Interpreter                    (解釈実行)
Level 1: C1 with full optimization     (軽量最適化)
Level 2: C1 with invocation counters   (カウンター付き)
Level 3: C1 with full profiling        (完全プロファイリング)
Level 4: C2 (Server compiler)          (重最適化)
```

#### 主要な最適化技術
1. **メソッドインライン化**: 呼び出しオーバーヘッド除去
2. **デッドコード除去**: 不要コードの削除
3. **ループ最適化**: ループアンローリング、不変式移動
4. **エスケープ解析**: スタック割り当て最適化
5. **プロファイルに基づく分岐の配置**: よく通る側の分岐を速く実行できるようにコードを並べる

## 実行結果の形式

`BytecodeAnalysisDemo`は、次のような形式で測定結果を表示します（数値は省略しています）。
測定値はCPU・OS・JVMの状態によって大きく変わり、実行するたびにも変わります。
このデモはJITの効果を体験するためのもので、正確に測るにはJMH（Java Microbenchmark Harness）を使います。

```
=== JIT Compilation Effect Demonstration ===
Cold execution time: ... μs
Hot execution time:  ... μs
Speedup: ...x

=== Branch Prediction and Profile-Guided Optimization ===
Predictable branching time:   ... μs
Unpredictable branching time: ... μs
Unpredictable / predictable: ...x

=== Method Inlining Analysis ===
Via small methods:  ... μs
Direct calculation: ... μs
Methods / direct: ...x
```

インライン化の比較では、小さなメソッドを呼ぶ書き方と直接計算する書き方が、ほぼ同じ時間になるのがふつうです。
`-XX:CompileCommand=dontinline,*InlineCandidate.*`を付けてインライン化を止めると、メソッドを呼ぶ書き方だけが遅くなります。
時間が短すぎて測れなかったときは、比の代わりに`n/a (too fast to measure)`と表示します。

## 実践的な最適化ガイドライン

### 1. メモリ効率の改善

#### ヒープメモリ最適化
```java
// ○ 適切な初期容量設定
List<String> list = new ArrayList<>(1000); // 事前にサイズを指定

// ○ 不要な参照の削除
list.clear(); // 明示的なクリア
list = null;  // 参照の削除

// × メモリリークの原因
static final Map<String, Object> cache = new HashMap<>(); // 永続的な参照
```

#### GCフレンドリーなコード
```java
// ○ 短命オブジェクトの使用
StringBuilder sb = new StringBuilder();
sb.append("Hello").append(" World");
return sb.toString(); // sbはすぐにGC対象

// × 不要な長寿命オブジェクト
static List<String> results = new ArrayList<>(); // 静的参照で長寿命化
```

### 2. JIT最適化の促進

#### インライン化しやすいコード
```java
// ○ 小さなメソッド（インライン化候補）
public int getValue() { return value; }
public boolean isEmpty() { return size == 0; }

// × 大きなメソッド（インライン化困難）
public int complexMethod() {
    // 50行以上の処理...
    return result;
}
```

HotSpotは、オーバーライドされていないメソッドなら`final`がなくてもインライン化します（クラス階層解析、CHA）。
性能のために`final`を付ける必要はなく、`final`は継承させたくないという設計の意図を表すために付けます（付録B.04）。

#### 分岐の予測しやすさ
```java
// 予測しやすい分岐（ほとんど同じ側を通る）
if (condition_that_is_usually_true) {
    commonPath();
} else {
    rareCase();
}

// 予測しにくい分岐（どちらを通るかが毎回ばらばら）
if (Math.random() > 0.5) {
    pathA();
} else {
    pathB();
}
```

予測しにくい分岐では、CPUの分岐予測が外れて処理が遅くなることがあります。
これはCPUの性質で、JVMがメソッドを「よく呼ばれる（ホット）」と判断してJITコンパイルすることとは関係ありません。

### 3. パフォーマンス監視とプロファイリング

#### JVMフラグによる分析
```bash
# 本番環境での継続監視
-Xlog:gc*:file=gc.log:time,uptime

# 開発時の詳細分析
-XX:+UnlockDiagnosticVMOptions -XX:+PrintCompilation -XX:+PrintInlining -XX:+LogCompilation

# OutOfMemoryErrorのときにヒープダンプを取る
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/
```

#### プロファイリングツールの活用
- **JProfiler**: 包括的なGUIプロファイラ
- **async-profiler**: 軽量プロダクション対応プロファイラ
- **JFR (Java Flight Recorder)**: JVM組み込み低オーバーヘッドプロファイラ
- **VisualVM**: 無料のJVM監視ツール

### 4. トラブルシューティング指針

#### よくある問題と対策

##### OutOfMemoryError
```bash
# ヒープサイズの調整
-Xms2g -Xmx4g

# メタスペースサイズの調整
-XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m

# ヒープダンプの取得（jcmdを使うのが推奨の方法）
jcmd <pid> GC.heap_dump heap.hprof
```

##### 性能問題
```bash
# JITコンパイル情報の確認
-XX:+PrintCompilation -Xlog:class+load

# GCログの分析
-Xlog:gc*,safepoint:file=gc.log:time,uptime
```

##### スレッドダンプ分析
```bash
# スレッドダンプの取得
jstack <pid> > threads.dump

# デッドロック検出
jstack <pid> | grep -A 5 "Java-level deadlock"
```

## 高度なトピック

### 1. カスタムクラスローダー

```java
public class PluginClassLoader extends ClassLoader {
    private final String pluginPath;
    
    public PluginClassLoader(String pluginPath, ClassLoader parent) {
        super(parent);
        this.pluginPath = pluginPath;
    }
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classBytes = loadClassBytes(name);
        return defineClass(name, classBytes, 0, classBytes.length);
    }
}
```

### 2. JNI (Java Native Interface)

```java
public class NativeDemo {
    static {
        System.loadLibrary("nativelib");
    }
    
    public native int nativeCalculation(int x, int y);
    
    public static void main(String[] args) {
        NativeDemo demo = new NativeDemo();
        int result = demo.nativeCalculation(10, 20);
        IO.println("Native result: " + result);
    }
}
```

### 3. アドバンスドGCチューニング

```bash
# G1GCの使用
-XX:+UseG1GC -XX:MaxGCPauseMillis=200

# 低遅延GCの設定（CMSはJava 14で削除されたため、ZGCを使う）
-XX:+UseZGC

```

G1では若い世代の大きさを停止時間の目標に合わせて自動で調整するため、`-XX:NewRatio`などで固定しないのがふつうです。

このプロジェクトを通じて、JVMの内部動作を理解し、性能の問題の原因を考える手がかりを得ることが目標です。性能を改善するときは、推測で直さず、プロファイラーやJMHで測ってから直します。