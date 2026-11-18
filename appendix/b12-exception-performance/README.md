# 付録B.12 例外処理のパフォーマンス

第13章で学んだ例外処理が、バイトコードとJVMでどう表され、どこに時間がかかるのかを、実際に観察しながら学ぶ節です。
あわせて、スタックトレースを作らない例外、JVMが例外に対して行う最適化、例外を調べる道具（詳しい`NullPointerException`のメッセージ、`StackWalker`、JFR）、例外の代わりに結果を値で返す書き方を扱います。

## この節の読み方

この節の解説のうち、事実を述べた段落には`<!-- claim: … -->`という印を付けています（GitHubの画面では見えません）。
印の名前は同じディレクトリの`claims.yml`に対応し、そこにその主張の確かめ方（バイトコードの照合、実行結果、フラグの既定値、出典）を書いています。
リポジトリのルートで`npm run check:deep-dive -- b12`を実行すると、すべての主張をJDK 25で確かめ直せます。
「観察する」の欄に載せた出力は、この検査が実際に出したものです。

時間を測る観察は、数値ではなく「どちらが長いか」だけを表示し、検査もそれだけを照合します。
時間の数値はCPU・OS・JDKの版・JITの状態で変わるためです。

## ファイル構成

### 観察用のプログラム（verify/）

| ファイル | 観察すること |
|---|---|
| `ExceptionTable.java` | try-catchの例外表（`javap`で見る） |
| `FinallyCopies.java` | finallyの中身のコピー（`javap`で見る） |
| `TryWithResources.java` | try-with-resourcesの展開と、抑制された例外 |
| `StackTraceCost.java` | スタックトレースの長さと、例外を作る・投げる時間の内訳 |
| `LazyStackTrace.java` | `StackTraceElement`の配列が作られる時期 |
| `NoStackTrace.java` | スタックトレースを作らない例外の作り方と性質 |
| `FastThrow.java` | 同じ箇所で繰り返す例外の置き換え（`OmitStackTraceInFastThrow`） |
| `HelpfulNpe.java` | `NullPointerException`の詳しいメッセージ |
| `WalkStack.java` | `StackWalker`でスタックをたどる |
| `JfrExceptions.java` | JFRの例外のイベント |
| `ResultSwitch.java` | 成功と失敗を値で返し、`switch`で場合分けする |
| `compile-errors/ResultMissingCase.java` | 場合を書き忘れたときのコンパイルエラー（コンパイルできないファイル） |

`verify/`のプログラムはパッケージを持たないので、`verify/`ディレクトリでそのままコンパイル・実行できます。

```bash
cd verify
javac ExceptionTable.java && javap -c -p ExceptionTable
javac FastThrow.java && java FastThrow && java -XX:-OmitStackTraceInFastThrow FastThrow
```

### 体験用のコード（src/）

```
src/main/java/com/example/exception/
├── performance/          # JMHのベンチマーク
│   ├── BasicExceptionBenchmark.java
│   ├── StackTraceDepthBenchmark.java
│   └── ExceptionTypeBenchmark.java
├── optimization/         # スタックトレースを作らない例外、例外のプール
│   ├── ExceptionPool.java
│   ├── PoolableException.java
│   ├── ValidationException.java
│   └── ConditionalStackTraceException.java
├── patterns/             # 例外の代わりに値で返す
│   ├── Result.java
│   └── NullObject.java
├── monitoring/           # 例外の回数と時間の集計
│   ├── ExceptionMetrics.java
│   └── ExceptionHandler.java
└── Demo.java             # デモプログラム
```

`src/test/java`には、`ExceptionPool`と`Result`のJUnitのテストがあります。

ビルドにはJava 25とMaven 3.9以上を使います。

```bash
# ビルド（テストも実行される）
mvn clean package

# デモの実行
mvn exec:java -Dexec.mainClass="com.example.exception.Demo"

# ベンチマークの一覧と実行
java -jar target/benchmarks.jar -l
java -jar target/benchmarks.jar BasicExceptionBenchmark
java -jar target/benchmarks.jar StackTraceDepthBenchmark -p stackDepth=10,50,100
```

`run-benchmarks.sh`は、3つのベンチマークを順に実行して、結果を`results/`にJSONで保存します。

JDK 25のjavacは、注釈処理を設定するオプション（`--processor-path`や`-proc:full`など）を指定しないと、注釈処理を行いません。
クラスパスに注釈処理器を置くだけでは動きません。
<!-- claim: javac-proc -->

JMHはベンチマークの一覧（`META-INF/BenchmarkList`）を注釈処理で作るため、`pom.xml`では`maven-compiler-plugin`の`annotationProcessorPaths`に`jmh-generator-annprocess`を指定しています。
これがないと、`benchmarks.jar`を実行してもベンチマークが見つかりません。
javacで直接コンパイルするときは`-proc:full`を付けます。

## 1. try-catchはバイトコードでどう表されるか

### 例外表

`verify/ExceptionTable.java`には、同じ呼び出しをtryなしで書いた`plain`と、try-catchで囲んだ`guarded`があります。
`javap -c -p`で見ると、次のようになります。

<!-- output: exception-table -->
```
  static int plain(int);
    Code:
         0: iload_0
         1: invokestatic  // Method compute:(I)I
         4: ireturn
  static int guarded(int);
    Code:
         0: iload_0
         1: invokestatic  // Method compute:(I)I
         4: ireturn
         5: astore_1
         6: iconst_m1
         7: ireturn
      Exception table:
         from    to  target type
             0     4     5   Class java/lang/IllegalArgumentException
```
<!-- /output -->

`guarded`の0〜4の命令は、`plain`とまったく同じです。
try-catchは命令ではなく、メソッドの例外表（`Exception table`）の1行になります。
「0から4の手前までの命令で`IllegalArgumentException`が起きたら、5へ飛ぶ」という意味です。
例外が起きないときは、例外表は参照されず、tryのない場合と同じ命令だけが実行されます。
<!-- claim: exception-table -->

例外が投げられると、JVMは例外表を上から順に調べ、命令の位置と例外の型が合う行を探します。
そのメソッドに合う行がなければ、メソッドを抜けて呼び出し元の例外表を調べます。
例外の時間は、この探索と、次の節で見るスタックトレースの記録にかかります。

### finallyのコピー

`verify/FinallyCopies.java`の`run`は、try・catch・finallyを持つメソッドです。

<!-- output: finally-copies -->
```
  static void run(int);
    Code:
         0: iload_0
         1: invokestatic  // Method work:(I)V
         4: invokestatic  // Method cleanup:()V
         7: goto          28
        10: astore_1
        11: ldc           // String caught
        13: invokestatic  // Method java/lang/IO.println:(Ljava/lang/Object;)V
        16: invokestatic  // Method cleanup:()V
        19: goto          28
        22: astore_2
        23: invokestatic  // Method cleanup:()V
        26: aload_2
        27: athrow
        28: return
      Exception table:
         from    to  target type
             0     4    10   Class java/lang/IllegalStateException
             0     4    22   any
            10    16    22   any
```
<!-- /output -->

finallyの中身（`cleanup()`の呼び出し）が3か所にあります。
tryを正常に抜ける道（4）、catchから抜ける道（16）、それ以外の例外で抜ける道（23）です。
3つ目の道は例外表の`any`（どの例外でも）の行から入り、`cleanup()`のあとで例外を投げ直します（`athrow`）。
<!-- claim: finally-copies -->

JVMSの3.13「Compiling finally」には、finallyを`jsr`命令で呼ぶサブルーチンにする例が載っています。
これはクラスファイルのバージョン50.0以下の古い形です。
4.9.1は、バージョン51.0以上のクラスファイルで`jsr`・`jsr_w`・`ret`を使うことを禁じています。
いまのjavacは、上のようにfinallyの中身をコピーします。
<!-- claim: jvms-jsr-history, finally-copies -->

finallyの中身が大きいと、そのぶんだけバイトコードが増えます。
finallyには後片付けだけを書き、長い処理はメソッドに分けると、コピーされるのは呼び出しの命令だけになります。

### try-with-resourcesの展開

`verify/TryWithResources.java`の`use`は、2つの資源を宣言したtry-with-resourcesです。
`javap`で見ると、`close()`の呼び出しと`addSuppressed`の呼び出しが並びます（呼び出しと`athrow`の行、例外表だけを抜き出しています）。

<!-- output: twr-bytecode -->
```
40: athrow
42: invokevirtual // Method TryWithResources$Res.close:()V
50: invokevirtual // Method TryWithResources$Res.close:()V
61: invokevirtual // Method java/lang/Throwable.addSuppressed:(Ljava/lang/Throwable;)V
65: athrow
67: invokevirtual // Method TryWithResources$Res.close:()V
75: invokevirtual // Method TryWithResources$Res.close:()V
84: invokevirtual // Method java/lang/Throwable.addSuppressed:(Ljava/lang/Throwable;)V
88: athrow
22 41 48 Class java/lang/Throwable
49 53 56 Class java/lang/Throwable
11 66 73 Class java/lang/Throwable
74 78 81 Class java/lang/Throwable
```
<!-- /output -->

資源ごとに、正常に抜けたときの`close()`と、例外で抜けたときの`close()`があります。
例外で抜けたときに`close()`も例外を投げたら、その例外を`addSuppressed`で本体の例外に付け、本体の例外を投げ直します。
JLSの14.20.3.1は、try-with-resourcesの意味をこのような書き換えで定めています。
<!-- claim: twr-bytecode -->

実行すると、資源は宣言と逆の順（`b`、`a`）に閉じられ、本体の例外に2つの`close()`の例外が付いていることがわかります。

<!-- output: twr-run -->
```
open a
open b
body
close b
close a
---
open a
open b
body
close b
close a
caught: body failed
  suppressed: close b failed
  suppressed: close a failed
```
<!-- /output -->
<!-- claim: twr-run -->

## 2. 例外のコストはどこにかかるか

### コンストラクターがスタックトレースを記録する

例外のオブジェクトを作ると、`Throwable`のコンストラクターが`fillInStackTrace()`を呼び、その時点の呼び出しの履歴（スタックトレース）を記録します。
`verify/NoStackTrace.java`は、`fillInStackTrace()`をオーバーライドして呼ばれた回数を数えます。
`new`しただけで1回呼ばれます。
<!-- claim: constructor-fills -->

つまり、スタックトレースは`throw`したときではなく、例外を作ったときに記録されます。
記録するには、いまのスレッドのスタックを上へたどり、フレームごとにメソッドと命令の位置を書き留めます。
そのため、深いところで作った例外ほど、記録する量が増えます。

### スタックトレースの長さ

`verify/StackTraceCost.java`は、再帰呼び出しで深さを変えながら例外を作り、`getStackTrace()`の要素数を表示します。

<!-- output: stack-length -->
```
深さ10で作った例外のスタックトレースの要素数: 12
深さ100で作った例外のスタックトレースの要素数: 102
深さ1000で作った例外のスタックトレースの要素数: 1002
深さ2000で作った例外のスタックトレースの要素数: 1024
```
<!-- /output -->

要素数は深さに`main`などの2つを足した数で、呼び出しの深さとともに増えます。
ただし、深さ2000では1024で止まっています。
HotSpotは、記録するフレームの数を`-XX:MaxJavaStackTraceDepth`（既定値1024）で打ち切ります。
<!-- claim: stack-length, max-depth-flag -->

`getStackTrace()`のAPIドキュメントは、仮想マシンがフレームを省いてよいこと、スタックトレースの情報がなければ長さ0の配列を返してよいことを認めています。
スタックトレースがいつも全部そろっているとは限りません。

### 時間の内訳

同じプログラムは、例外を作る時間と投げる時間をいくつかの条件で測り、比べた結果だけを表示します。

<!-- output: cost-direction -->
```
深さ500で作る例外は深さ10より時間がかかる: true
スタックトレースを作らない例外は作る例外より短い（深さ500）: true
スタックトレースを作らない例外は深さで大きく変わらない: true
getStackTrace()を呼ぶとさらに時間がかかる（深さ500）: true
作っておいた例外を深さ500から投げると深さ10より時間がかかる: true
```
<!-- /output -->

例外を作る時間の大部分は、オブジェクトを割り当てることではなく、スタックをたどって記録すること（`fillInStackTrace()`）です。
スタックトレースを作らない例外（3で説明します）は、深さによらず短い時間で作れます。
<!-- claim: cost-direction -->

`getStackTrace()`を呼ぶと、さらに時間がかかります。
OpenJDKの実装では、`fillInStackTrace()`はメソッドと命令の位置だけを内部の形（`backtrace`フィールド）で記録し、`StackTraceElement`の配列（クラス名・メソッド名・ファイル名・行番号）は`getStackTrace()`や`printStackTrace()`で初めて作ります。
ログにスタックトレースを書き出す処理は、例外を作るよりも重くなります。
<!-- claim: cost-direction -->


最後の行は、スタックトレースを作らない例外をあらかじめ1つ作っておき、深さ500の奥から投げて一番上で受け取る時間です。
例外を作るコストはありませんが、深いところから投げるほど時間がかかります。
例外が抜けるフレームごとに、JVMはそのメソッドの例外表を調べ、受け取る位置がなければフレームを片付けて呼び出し元へ移るためです。
例外のコストは、スタックトレースを記録するコスト（作るとき）と、フレームを抜けていくコスト（投げるとき）の2つに分かれ、どちらも深さとともに増えます。
<!-- claim: unwind-cost -->

`verify/LazyStackTrace.java`は、`Throwable`の`private`なフィールドをリフレクションで覗きます（`--add-opens java.base/java.lang=ALL-UNNAMED`を付けて実行します）。

<!-- output: lazy-elements -->
```
作った直後のstackTrace: StackTraceElement[0]
作った直後のbacktrace: Object[]
作った直後のdepth: 1
getStackTrace()のあとのstackTrace: StackTraceElement[1]
```
<!-- /output -->

作った直後の`stackTrace`は長さ0の配列（まだ作っていないことを表す目印）で、`getStackTrace()`を呼んだあとで中身のある配列になります。
<!-- claim: lazy-elements -->

### JMHで測った例

`src/`のベンチマークを、著者の環境で実行した結果です。
環境はApple M3（8コア）、macOS、OpenJDK 25.0.2（Microsoft Build）、JMH 1.37です。
設定はベンチマークの注釈のとおりで、平均時間（`Mode.AverageTime`）、暖機5回・測定5回（各1秒）、フォーク1回です。
数値は環境で大きく変わるため、自分の環境で`benchmarks.jar`を実行して確かめてください。
<!-- claim: jmh-results -->

`BasicExceptionBenchmark`の結果です（単位はns/op、±は99.9%の誤差の幅）。
<!-- claim: jmh-results -->

| ベンチマーク | 測る内容 | 平均時間（ns/op） |
|---|---|---|
| `normalFlow` | 例外を使わない処理（基準） | 0.392 ± 0.045 |
| `tryCatchOverheadNoException` | 例外が起きないtry-catch | 0.401 ± 0.145 |
| `exceptionFlow` | スタックトレースを作る例外 | 523.991 ± 34.800 |
| `exceptionFlowNoStackTrace` | `fillInStackTrace()`を上書きした例外 | 4.033 ± 0.241 |
| `cachedExceptionFlow` | 作っておいた例外を同じメソッドで投げる | 0.331 ± 0.113 |

`StackTraceDepthBenchmark`は、`stackDepth`段の再帰呼び出しの奥で例外を作って投げ、一番上で受け取ります（単位はns/op）。

| stackDepth | スタックトレースを作る例外 | 作らない例外 | 戻り値（基準） |
|---|---|---|---|
| 1 | 661.033 ± 199.805 | 4.539 ± 1.512 | 0.584 ± 0.048 |
| 10 | 1077.436 ± 119.080 | 398.663 ± 70.933 | 6.290 ± 0.330 |
| 50 | 3594.365 ± 397.597 | 1838.021 ± 669.069 | 25.717 ± 0.727 |
| 100 | 7726.665 ± 2988.055 | 4074.863 ± 393.932 | 288.782 ± 29.934 |
| 200 | 10201.046 ± 674.498 | 6578.502 ± 228.268 | 595.523 ± 34.100 |

`ExceptionTypeBenchmark`の結果です（単位はns/op）。

| ベンチマーク | 投げる例外 | 平均時間（ns/op） |
|---|---|---|
| `checkedException` | `IOException`（検査例外） | 549.658 ± 200.393 |
| `runtimeException` | `RuntimeException` | 758.417 ± 253.760 |
| `error` | `AssertionError` | 533.858 ± 30.917 |
| `exceptionWithCause` | 原因の例外を2段持つ例外 | 1565.660 ± 83.853 |
| `exceptionWithSuppressed` | 抑制された例外を2つ持つ例外 | 1606.384 ± 74.965 |
| `lightweightException` | `writableStackTrace = false`の例外 | 3.160 ± 0.962 |
<!-- claim: jmh-results -->

検査例外・実行時例外・`Error`の差は誤差の幅と重なっており、型による一定の差は読み取れません。
原因や抑制された例外を持つ例は、スタックトレースを作る例外を3つ作るので、約3倍の時間になっています。
<!-- claim: jmh-results -->

この結果から読み取れることは次のとおりです。

- 例外が起きないtry-catchは、tryのない処理と差がない（1の例外表のとおり）
  <!-- claim: jmh-results -->
- スタックトレースを作る例外は、作らない例外や戻り値より桁違いに遅い
  <!-- claim: jmh-results -->
- 深くなるほど、作る例外も作らない例外も遅くなる。作らない例外でも、フレームを抜けるコストは残る
  <!-- claim: jmh-results -->
- 深さ1で作らない例外がほぼ戻り値並みなのは、JITが呼び出しをインライン化し、`throw`から`catch`へ直接移るようにしたためと考えられる。`cachedExceptionFlow`が基準と変わらないのも同じ理由と考えられる

### JITの最適化とベンチマーク

`BasicExceptionBenchmark`の`cachedExceptionFlow`は、あらかじめ作った例外を、同じメソッドで投げて受け取ります。
JITが呼び出しをインライン化して`throw`と`catch`が同じコンパイルの単位に入ると、例外表を探さずに、受け取る位置へ直接移るコードにできます。
上の測定で`cachedExceptionFlow`が基準の`normalFlow`と変わらなかったのは、このためと考えられます。
JITが出したコードは確かめていないので、ここは推測です。

ベンチマークでは、JITの最適化で測りたい処理が消えていないかに注意します。
`BasicExceptionBenchmark`では、定数の引数をJITが畳み込まないように、引数を`@State`のフィールドから読んでいます。
`System.nanoTime()`で自分で測る`Demo`の第1節は、暖機をしてから測り、結果を足し合わせて表示することで、ループが消されないようにしています。
それでも傾向を見る程度のもので、正確に測るにはJMHを使います。

## 3. スタックトレースを作らない例外

スタックトレースが要らない例外は、記録そのものを止められます。
方法は2つあります。

- `Throwable`の4引数のコンストラクター`(message, cause, enableSuppression, writableStackTrace)`に、`writableStackTrace = false`を渡す
- `fillInStackTrace()`をオーバーライドし、記録せずに`this`を返す

`verify/NoStackTrace.java`で、それぞれの性質を確かめます。

<!-- output: writable-false -->
```
writableStackTrace=falseの要素数: 0
setStackTraceのあとの要素数: 0
enableSuppression=falseでaddSuppressedしたあとの数: 0
```
<!-- /output -->

`writableStackTrace = false`で作った例外は、コンストラクターが`fillInStackTrace()`を呼ばず、`getStackTrace()`は空の配列を返します。
あとから`setStackTrace`を呼んでも設定されません。
`enableSuppression = false`にすると、`addSuppressed`で付けた例外も無視されます。
<!-- claim: writable-false -->

`fillInStackTrace()`をオーバーライドして何もしない書き方でも、要素数は0になります。
<!-- claim: override-fill -->

APIドキュメントは、抑制を止めるのは、メモリが足りないときに仮想マシンが例外オブジェクトを使い回す場合などの特別な事情があるときに限るべきだ、と述べています。
スタックトレースを止めた例外は、発生した場所の手がかりを失います。
入力の検証のように、よく起こり、場所が要らない失敗にだけ使います。

### 条件でスタックトレースを作るときの落とし穴

「条件が成り立つときだけスタックトレースを作る」例外を、`fillInStackTrace()`のオーバーライドでフィールドを見て書くと、動きません。

```java
static class FieldCondition extends RuntimeException {
    private final Boolean wanted;

    FieldCondition(boolean wanted) {
        super("field");          // ここでfillInStackTrace()が呼ばれる
        this.wanted = wanted;    // フィールドへの代入はその後
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return wanted ? super.fillInStackTrace() : this;  // wantedはまだnull
    }
}
```

スーパークラスのコンストラクターが`fillInStackTrace()`を呼ぶ時点では、サブクラスのフィールドにはまだ値が入っていません。
`wanted`は`null`なので、自動アンボクシングで`NullPointerException`になります。
<!-- claim: field-condition-npe -->

`src/`の`ConditionalStackTraceException`は、条件を評価した結果を`writableStackTrace`としてスーパークラスのコンストラクターに渡すことで、この問題を避けています。
環境（`-Dapp.environment=development`）、レート制限（1分に1回）、サンプリング（100回に1回）、デバッグモードで切り替えるサブクラスがあります。

### JVM全体で止めるフラグ

`-XX:-StackTraceInThrowable`を付けると、すべての例外がスタックトレースを持たなくなります（既定は有効）。
`verify/NoStackTrace.java`をこのフラグで実行すると、ふつうの例外の要素数も0になります。
障害の調べようがなくなるので、ふだん使うものではありません。
<!-- claim: stack-trace-in-throwable -->

### 例外オブジェクトのプール

`src/`の`ExceptionPool`は、スタックトレースを作らない例外を使い回す例です。
`Demo`の第2節では、スタックトレースを作る例外を毎回作る場合、作らない例外を毎回作る場合、プールの例外を使い回す場合を比べます。
速くなる理由の大部分は、使い回しではなくスタックトレースを作らないことにあります。
2のJMHの例では、スタックトレースを作る例外は約500ns/op、作らない例外を毎回作るのは約4ns/opでした。
<!-- claim: jmh-results -->

オブジェクトを1つ割り当てる時間はもともと短く、プールからの出し入れ（`ConcurrentLinkedQueue`の操作）にも時間がかかります。
そのため、プールを使うほうが毎回作るより速いとは限りません。
`Demo`の第2節で、作らない例外を毎回作る場合とプールの場合を比べてみてください。

例外オブジェクトの使い回しは、ほかのスレッドが同じオブジェクトのメッセージや状態を書き換える危険があり、勧められません。
ほとんどの場合、`writableStackTrace = false`で毎回作る例外で足ります。

## 4. 同じ箇所で繰り返す例外の置き換え

`null`のメソッドを呼んだときの`NullPointerException`や、0で割ったときの`ArithmeticException`は、プログラムの`throw`ではなくJVMが投げます。
HotSpotのC2（最適化するJITコンパイラー）は、同じ箇所でこうした例外が何度も起きると、スタックトレースを記録する処理を省き、あらかじめ作っておいた例外を投げるようにします。

`verify/FastThrow.java`は、`text.length()`（`text`は`null`）と`i / 0`を20万回ずつ繰り返し、受け取った例外を調べます。

<!-- output: fast-throw -->
```
別の箇所で1回だけ起こしたNullPointerExceptionのメッセージ: Cannot invoke "String.length()" because "FastThrow.text" is null
スタックトレースのないNullPointerException: あり
スタックトレースのないNullPointerExceptionのオブジェクトの数: 1
スタックトレースのないArithmeticException: あり
最後のNullPointerExceptionのメッセージ: null
最後のNullPointerExceptionの要素数: 0
```
<!-- /output -->

途中から、スタックトレースが空で、メッセージも`null`の例外が来るようになります。
しかも、それらはすべて同じ1つのオブジェクトです。
<!-- claim: fast-throw -->

この最適化は`-XX:+OmitStackTraceInFastThrow`で、既定で有効です。
<!-- claim: fast-throw-flag -->

`-XX:-OmitStackTraceInFastThrow`を付けると、何度起きても、スタックトレースとメッセージのある例外になります。

<!-- output: fast-throw-off -->
```
別の箇所で1回だけ起こしたNullPointerExceptionのメッセージ: Cannot invoke "String.length()" because "FastThrow.text" is null
スタックトレースのないNullPointerException: なし
スタックトレースのないArithmeticException: なし
最後のNullPointerExceptionのメッセージ: Cannot invoke "String.length()" because "FastThrow.text" is null
最後のNullPointerExceptionの要素数: 2
```
<!-- /output -->
<!-- claim: fast-throw-off -->

C1だけでコンパイルする設定（`-XX:TieredStopAtLevel=1`）やインタープリターだけ（`-Xint`）では、置き換えは起こりません。
C2がコンパイルしたコードで起こる最適化です。
<!-- claim: fast-throw-c2 -->

本番のログに、スタックトレースのない`java.lang.NullPointerException`が大量に並ぶことがあります。
それは同じ箇所で繰り返し起きている証拠です。
最初のころのログには、スタックトレースのある同じ例外が残っているはずなので、そちらで場所を探します。
場所がわからないときは、`-XX:-OmitStackTraceInFastThrow`を付けて再現させます。

## 5. NullPointerExceptionの詳しいメッセージ

JDK 14のJEP 358から、JVMが投げる`NullPointerException`には、何が`null`だったかを説明するメッセージが付きます。
JVMは、例外が起きた命令のバイトコードを調べて、`null`だった参照がどこから来たか（ローカル変数、フィールド、配列の要素、メソッドの戻り値）を書きます。
`verify/HelpfulNpe.java`を、`-g`なしでコンパイルして実行した結果です。

<!-- output: helpful-npe -->
```
ローカル変数: Cannot invoke "String.length()" because "<local0>" is null
静的フィールド: Cannot invoke "String.length()" because "HelpfulNpe.name" is null
配列: Cannot load from int array because "<parameter1>.values" is null
引数: Cannot read field "values" because "<parameter1>" is null
明示的なthrow: null
```
<!-- /output -->

フィールドの名前はクラスファイルにいつもあるので表示されます。
ローカル変数と引数の名前はデバッグ情報にしかないため、`-g`なしでは`<local0>`・`<parameter1>`のような番号になります。
プログラムが`new NullPointerException()`で作って投げた例外には、メッセージは付きません。
<!-- claim: helpful-npe -->

`javac -g`でコンパイルすると、名前が表示されます。

<!-- output: helpful-npe-g -->
```
ローカル変数: Cannot invoke "String.length()" because "s" is null
配列: Cannot load from int array because "h.values" is null
引数: Cannot read field "values" because "h" is null
```
<!-- /output -->
<!-- claim: helpful-npe-g -->

メッセージは`getMessage()`が呼ばれたときに作られます。
`-XX:-ShowCodeDetailsInExceptionMessages`を付けると、メッセージは付かず`null`になります（JDK 25の既定は有効）。
<!-- claim: helpful-npe-off -->

4の置き換えで投げられる例外には、このメッセージもありません。
メッセージが`null`の`NullPointerException`を見たら、プログラムが明示的に投げたものか、4の置き換えを疑います。

## 6. 例外を作らずにスタックをたどる（StackWalker）

呼び出し元を知るために`new Throwable().getStackTrace()`を使うコードがあります。
この書き方は、例外を作ってスタック全体を記録し、さらにすべての`StackTraceElement`を作るので、2で見たとおり重くなります。

Java 9（JEP 259）で入った`StackWalker`は、例外を作らずにスタックをたどります。
`walk`メソッドはフレームを`Stream`で渡すので、`limit`で必要なところまでだけを取り出せます。
JEP 259は、このAPIの目的として、スタックトレースの情報を絞り込み、必要な分だけ遅延して取り出せることを挙げています。

<!-- output: stack-walker -->
```
近い順の呼び出し元: [level:26, level:23, level:23]
例外で数えた深さ: 102
StackWalkerで数えた深さ: 102
```
<!-- /output -->
<!-- claim: stack-walker -->

ログのライブラリが呼び出し元のクラスや行番号を調べる処理は、このAPIで軽くできます。

## 7. 例外を観測する（JFR）

JFR（JDK Flight Recorder）は、例外についての2つのイベントを持っています。

- `jdk.JavaExceptionThrow`
  - 例外が作られるたびに、型・メッセージ・スタックトレースを記録する
- `jdk.ExceptionStatistics`
  - それまでに作られた`Throwable`の累計を、定期的に記録する

`verify/JfrExceptions.java`は、JDKに入っている設定（`default`と`profile`）を読み、`jdk.JavaExceptionThrow`の設定を表示します。

<!-- output: jfr-settings -->
```
設定defaultのjdk.JavaExceptionThrow: enabled=true throttle=100/s
設定profileのjdk.JavaExceptionThrow: enabled=true throttle=300/s
```
<!-- /output -->

JDK 25の設定では、このイベントは有効で、記録する数が1秒あたり100件（`profile`は300件）までに間引かれます。
<!-- claim: jfr-settings -->

同じプログラムは、自分で記録を始め、3回投げた`IllegalStateException`、作っただけで投げない`IllegalArgumentException`、スタックトレースを作らない例外（`Quiet`）を記録します。

<!-- output: jfr-construction -->
```
設定defaultのjdk.JavaExceptionThrow: enabled=true throttle=100/s
設定profileのjdk.JavaExceptionThrow: enabled=true throttle=300/s
jdk.JavaExceptionThrow JfrExceptions$Quiet: 1
jdk.JavaExceptionThrow java.lang.IllegalArgumentException: 1
jdk.JavaExceptionThrow java.lang.IllegalStateException: 3
jdk.ExceptionStatisticsのthrowablesの増加が5以上: true
```
<!-- /output -->

投げていない例外も記録されています。
`jdk.JavaExceptionThrow`は、名前に反して、`throw`ではなく例外を作ったとき（コンストラクター）に記録されます。
<!-- claim: jfr-construction -->

「どこでどれだけ例外が作られているか」は、次のように記録して`jfr`コマンドで集計できます。

```bash
java -XX:StartFlightRecording:filename=rec.jfr,settings=profile -jar app.jar
jfr summary rec.jfr
jfr print --events jdk.JavaExceptionThrow rec.jfr
```

## 8. 例外の代わりに値で返す

例外は、呼び出し元が予期していない失敗を、途中のメソッドを飛ばして伝えるしくみです。
入力の誤りのように「よく起こり、すぐ呼び出し元で処理する」失敗は、成功と失敗を戻り値で表す方法もあります。
失敗を扱うことを型で強制でき、スタックトレースのコストもかかりません。

### sealedインターフェイスで表す

Java 25では、成功と失敗をsealedインターフェイスとレコードで表すと、`switch`で両方の場合を書けます（第8章で学んだsealed・Recordと、パターンマッチング）。

```java
sealed interface Result<T> permits Ok, Err {
}

record Ok<T>(T value) implements Result<T> {
}

record Err<T>(String message) implements Result<T> {
}

static String describe(Result<Integer> r) {
    return switch (r) {
        case Ok<Integer>(Integer v) -> "成功 " + v;
        case Err<Integer>(String m) -> "失敗 " + m;
    };
}
```

`verify/ResultSwitch.java`を実行した結果です。

<!-- output: result-switch -->
```
成功 42
失敗 数ではありません: abc
```
<!-- /output -->

`Result`はsealedなので、2つの場合を書けば`default`は要りません。
<!-- claim: result-switch -->

場合を書き忘れると、コンパイルエラーになります（`verify/compile-errors/ResultMissingCase.java`）。

<!-- output: result-missing-case -->
```
verify/compile-errors/ResultMissingCase.java:16: error: the switch expression does not cover all possible input values
```
<!-- /output -->
<!-- claim: result-missing-case -->

### src/のResult

`src/`の`patterns/Result.java`は、`map`・`flatMap`・`filter`・`fold`などのメソッドを持つ、より多機能な例です。

```java
Result<Integer, String> calculation =
    parseIntWithResult("42")
        .flatMap(n -> divideWithResult(100, n))
        .map(n -> n + 10)
        .filter(n -> n > 0, "Result must be positive");
```

例外を使う検証は、最初に見つかった誤りで止まります。
値で返す検証は、複数の誤りを集めてまとめて返せます。

```java
public Result<ValidInput, List<String>> validateInput(String input) {
    List<String> errors = new ArrayList<>();
    if (input == null) {
        errors.add("Null input");
    } else if (input.isEmpty()) {
        errors.add("Empty input");
    }
    return errors.isEmpty()
        ? Result.success(new ValidInput(input))
        : Result.failure(errors);
}
```

値がないことがふつうに起こるだけなら、標準の`Optional`（第12章）で足ります。
`patterns/NullObject.java`は、`null`の代わりに「何もしないオブジェクト」を返すNull Objectパターンの例です。

### どちらを選ぶか

- 呼び出し元がすぐ処理する、予期される失敗（入力の誤り、在庫切れ）は値で返す
- 呼び出し元が処理できない、予期しない失敗（プログラムの誤り、設定の誤り、I/Oの障害）は例外で伝える
- I/Oの障害のように、処理自体に時間がかかる場面では、例外のコストは問題にならない
- フレームワークが例外を前提にしている境界では、その規約に従う

## 9. 例外の回数と時間を集計するデモ

`src/`の`monitoring/ExceptionMetrics`と`ExceptionHandler`は、例外の型ごとに回数・処理時間・スタックトレースの長さを集計します。
`Demo`の第6節は、種を固定した乱数で例外を起こし、集計の報告を表示します。
時刻と時間は実行のたびに変わります。

実際の運用で例外の傾向を調べるときは、自作の集計より、7のJFRを使うほうが手軽です。

## 仕様が決めていることと、実装の工夫

| 内容 | 決めているもの |
|---|---|
| 例外表と、例外を受け取る位置の探し方 | JVMS（4.7.3、3.12） |
| try-with-resourcesの意味（閉じる順、抑制された例外） | JLS（14.20.3） |
| コンストラクターが`fillInStackTrace()`を呼ぶこと、`writableStackTrace` | `Throwable`のAPIドキュメント |
| スタックトレースを省いてよいこと | `getStackTrace()`のAPIドキュメント（省くことを許している） |
| `MaxJavaStackTraceDepth`、`OmitStackTraceInFastThrow`、`StackTraceElement`を後から作ること | HotSpotの実装 |
| `NullPointerException`の詳しいメッセージ | JEP 358（HotSpotの機能） |

## よくある誤解

- try-catchを書くだけで遅くなる
  - 例外が起きなければ、tryのない場合と同じ命令しか実行されない（1）
  <!-- claim: exception-table -->
- 例外のコストは、オブジェクトを作ることにかかる
  - 大部分はスタックをたどって記録することにかかり、深いほど増える（2）
  <!-- claim: cost-direction -->
- スタックトレースは`throw`したときに記録される
  - 例外を作ったとき（コンストラクター）に記録される（2）。JFRの`jdk.JavaExceptionThrow`も作ったときに記録される（7）
  <!-- claim: constructor-fills, jfr-construction -->
- finallyは`jsr`命令のサブルーチンになる
  - それは古いクラスファイルの形で、いまのjavacはfinallyの中身をコピーする（1）
  <!-- claim: finally-copies -->
- スタックトレースのない`NullPointerException`は、JVMの不具合である
  - 同じ箇所で繰り返し起きたため、C2が置き換えたもの（4）
  <!-- claim: fast-throw -->
- 例外オブジェクトを使い回すと速くなる
  - 速くなる理由の大部分はスタックトレースを作らないことで、使い回しはスレッド間で状態が混ざる危険がある（2・3）
  <!-- claim: jmh-results -->

## 参考資料

- Java仮想マシン仕様（Java SE 25）
  - 3.12「Throwing and Handling Exceptions」、3.13「Compiling finally」
  - 4.7.3「The Code Attribute」、4.9.1「Static Constraints」
  - `https://docs.oracle.com/javase/specs/jvms/se25/html/`
- Java言語仕様（Java SE 25）
  - 14.20.3「try-with-resources」、14.11.1.1「Exhaustive Switch Blocks」
  - `https://docs.oracle.com/javase/specs/jls/se25/html/`
- `java.lang.Throwable`・`java.lang.StackWalker`（Java SE 25 API）
  - `https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/Throwable.html`
- javaコマンドのリファレンス（Java SE 25）
  - `https://docs.oracle.com/en/java/javase/25/docs/specs/man/java.html`
- JEP 358: Helpful NullPointerExceptions
  - `https://openjdk.org/jeps/358`
- JEP 259: Stack-Walking API
  - `https://openjdk.org/jeps/259`
- JMH（Java Microbenchmark Harness）
  - `https://openjdk.org/projects/code-tools/jmh/`
