# 付録B.04 仮想メソッドテーブル（vtable）と動的ディスパッチ

第5章で学んだポリモーフィズムを、JVMがどのように実現し、JITコンパイラーがその呼び出しをどう最適化するかを、実際に観察しながら学ぶ節です。

## この節の読み方

この節の解説のうち、事実を述べた段落には`<!-- claim: … -->`という印を付けています（GitHubの画面では見えません）。
印の名前は同じディレクトリの`claims.yml`に対応し、そこにその主張の確かめ方（バイトコードの照合、JVMのログ、フラグの既定値、出典）を書いています。
リポジトリのルートで`npm run check:deep-dive -- b04`を実行すると、すべての主張をJDK 25で確かめ直せます。
「観察する」の欄に載せた出力は、この検査が実際に出したものです（JITのログは実行のたびに数値や順序が変わります）。

## ファイル構成

### 観察用のプログラム（verify/）

| ファイル | 観察すること |
|---|---|
| `InvokeKinds.java` | メソッドの種類ごとの呼び出し命令（`javap`で見る） |
| `PrivateCall.java` | Java 10と11での`private`メソッドの呼び出し命令の違い |
| `CallSiteProfile.java` | 呼び出し箇所に現れる型の数とインライン化 |
| `ClassHierarchy.java` | クラス階層解析（CHA）と脱最適化 |
| `InliningLimits.java` | インライン化の大きさと深さの上限 |

`verify/`のプログラムはパッケージを持たないので、`verify/`ディレクトリでそのままコンパイル・実行できます。

```bash
cd verify
javac InvokeKinds.java && javap -c -p 'InvokeKinds$Child'
javac CallSiteProfile.java && java -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining CallSiteProfile mono
```

### 体験用のデモ（src/）

- `VirtualMethodTableDemo.java`
  - vtableの構造と動的ディスパッチの手順を、Javaのコードで真似て表示する（**シミュレーション**であり、JVMの内部を見ているのではない）
  - モノモーフィック・バイモーフィック・メガモーフィックな呼び出し箇所、`final`の有無、インターフェイス経由の呼び出しの時間を比べる
- `MethodInliningDemo.java`
  - 繰り返し呼ばれるメソッドがJITコンパイルされる様子、インライン化の判断に影響する要因を体験する

```bash
javac -d out src/main/java/com/example/vtable/*.java
java -cp out com.example.vtable.VirtualMethodTableDemo
java -cp out com.example.vtable.MethodInliningDemo
```

## 1. メソッド呼び出しのバイトコード

Javaのメソッド呼び出しは、メソッドの種類によって異なるバイトコード命令になります。
`verify/InvokeKinds.java`の`callAll`メソッドを`javap -c -p`で見ると、次のように並びます。

<!-- output: invoke-instructions -->
```
0: invokestatic // Method staticMethod:()V
4: invokevirtual // Method privateMethod:()V
8: invokevirtual // Method finalMethod:()V
12: invokevirtual // Method virtualMethod:()V
16: invokeinterface // InterfaceMethod InvokeKinds$Drawable.draw:()V
```
<!-- /output -->

`static`メソッドは`invokestatic`、インターフェイスのメソッドは`invokeinterface`で呼ばれます。
`private`メソッドと`final`メソッドの呼び出しも、ふつうのインスタンスメソッドと同じ`invokevirtual`です。
オーバーライドできないことがわかっているので、JVMは実行時に呼び出し先を1つに決められます。
<!-- claim: invoke-instructions -->

`invokespecial`は、コンストラクター（`<init>`）と`super.メソッド()`の呼び出しに使われます。
<!-- claim: invokespecial-uses -->

`private`メソッドの呼び出しは、Java 10までは`invokespecial`でした。
Java 11で、入れ子のクラスどうしが互いの`private`メンバーを使えるしくみ（ネストメイト、JEP 181）が入ったときに、`invokevirtual`に変わりました。
`verify/PrivateCall.java`を`javac --release 10`と`--release 11`でコンパイルし、`javap -c -p`で見比べると確かめられます。
<!-- claim: private-nestmates -->

## 2. 仕様が決めていることと、JVMの工夫

Java仮想マシン仕様（JVMS）が決めているのは、`invokevirtual`がどのメソッドを選ぶか（メソッドの選択）という結果です。
実際のオブジェクトのクラスから順にスーパークラスへさかのぼり、オーバーライドしているメソッドを探す、という規則です。
仕様には「vtable」という言葉は出てきません。
JVMSの第2章は、内部の最適化のような仕様に含まれない実装の詳細を、JVMを作る側に任せると述べています。
以下で説明するvtableやインライン化は、HotSpot（OpenJDKのJVM）がこの規則を速く実行するための工夫です。
<!-- claim: jvms-no-vtable -->

## 3. 仮想メソッドテーブルの構造

呼び出すたびにスーパークラスをさかのぼると時間がかかります。
そこでHotSpotは、クラスごとに「オーバーライドできるメソッドの表」（vtable）を作り、呼び出しを表の参照1回で済ませます。

```
Animal vtable:                  Dog vtable:
+-------+------------------+    +-------+------------------+
| Index | Method           |    | Index | Method           |
+-------+------------------+    +-------+------------------+
| 0..4  | Objectの5つの     |    | 0..4  | Objectの5つの     |
|       | メソッド          |    |       | メソッド          |
| 5     | Animal.move      |    | 5     | Dog.move         | ← オーバーライド
| 6     | Animal.eat       |    | 6     | Animal.eat       | ← 継承
+-------+------------------+    | 7     | Dog.bark         | ← 新しいメソッド
                                +-------+------------------+
```

インデックスの値は説明のためのものです。
サブクラスのvtableは親クラスのvtableと同じ並びで始まり、オーバーライドしたメソッドは親クラスと同じインデックスの欄を置き換えます。
そのため、`Animal`型の変数から`move()`を呼ぶとき、実際のオブジェクトが`Dog`でも同じインデックス5を見ればよいことになります。

vtableに入るのはオーバーライドできるメソッドだけです。
`Object`のメソッドのうちオーバーライドできるのは`clone`・`equals`・`finalize`・`hashCode`・`toString`の5つで、`getClass`・`notify`・`wait`などは`final`です。
`verify/InvokeKinds.java`は、この5つをリフレクションで数えて表示します。
<!-- claim: object-overridable -->

動的ディスパッチの手順は次のとおりです。

1. オブジェクトの実際のクラスを取得する（オブジェクトヘッダーから）
2. そのクラスのvtableを参照する
3. 呼び出し箇所に対応するインデックスの欄を読む（インデックスは呼び出し箇所を最初に実行するときに決まる）
4. その欄が指すメソッドを実行する

インターフェイスのメソッドは、クラスごとに並びが違うため、インターフェイス用の表（itable）を使って探します。

## 4. 段階的コンパイル（Tiered Compilation）

```
Level 0: インタープリター
Level 1: C1（プロファイルなし）
Level 2: C1（呼び出し回数などの限定的なプロファイル）
Level 3: C1（完全なプロファイル）
Level 4: C2（プロファイルを使った高度な最適化）
```

段階的コンパイルは既定で有効です。
<!-- claim: tiered-default -->

よく呼ばれるメソッドは、インタープリター実行、C1でのコンパイル、C2でのコンパイルの順に進みます。
JDK 25の既定では、呼び出し回数がおよそ200回（`Tier3InvocationThreshold`）でC1、5000回（`Tier4InvocationThreshold`）でC2のコンパイルの対象になります（ループの回数なども加味されます）。
値は`java -XX:+PrintFlagsFinal -version`で確かめられます。
<!-- claim: tier3-threshold, tier4-threshold -->

古い解説には`-XX:CompileThreshold`（既定値10000）で時期を変えると書かれていることがあります。
段階的コンパイルが有効なときは、このフラグを大きくしてもコンパイルの時期は変わりません。
`-XX:-TieredCompilation`で段階的コンパイルを止めたときだけ効きます。
<!-- claim: compile-threshold-unused -->

`-XX:TieredStopAtLevel=1`にすると、C1だけでコンパイルし、C2のコンパイルをしません。
起動は速くなりますが、長く動かすプログラムは遅くなりやすくなります。
<!-- claim: stop-at-level-1 -->

## 5. 呼び出し箇所の最適化（インラインキャッシュ）

JITは呼び出し箇所ごとに、実際に現れたレシーバー（メソッドを呼ばれるオブジェクト）の型を記録します（型のプロファイル）。
C2はこの記録を見て、呼び出し先のメソッドの中身を呼び出し箇所に展開（インライン化）するかを決めます。

`verify/CallSiteProfile.java`は、`shape.area()`という1つの呼び出し箇所に、1種類・2種類・3種類の型を流します。
`-XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining`を付けて実行すると、インライン化の判断が表示されます。

### モノモーフィック（1種類の型）

<!-- output: callsite-mono -->
```
@ 27 CallSiteProfile$Circle::area (4 bytes) inline
@ 27 CallSiteProfile$Circle::area (4 bytes) inline (hot) callee changed to CallSiteProfile::total (42 bytes) \-> TypeProfile (47651/47651 counts) = CallSiteProfile$Circle
@ 27 CallSiteProfile$Circle::area (4 bytes) inline (hot) callee changed to CallSiteProfile::total (42 bytes) \-> TypeProfile (990918/990918 counts) = CallSiteProfile$Circle
```
<!-- /output -->

`TypeProfile (n/n counts) = …Circle`は、記録した呼び出しがすべて`Circle`だったことを表します。
C2は「`Circle`か」を確かめたうえで、`Circle.area()`をインライン化します（`inline (hot)`）。
<!-- claim: callsite-mono -->

JDK 25の`PrintInlining`は、行の途中に「callee changed to」という別の判断の文字が混じることがあります。
行の先頭の「メソッド名（バイト数）判断」の部分を読んでください。

### バイモーフィック（2種類の型）

<!-- output: callsite-bi -->
```
@ 27 CallSiteProfile$Circle::area (4 bytes) inline (hot) callee changed to CallSiteProfile$Square::area (4 bytes) inline (hot) callee changed to CallSiteProfile::total (42 bytes) \-> TypeProfile (22059/44118 counts) = CallSiteProfile$Square callee changed to CallSiteProfile::total (42 bytes) \-> TypeProfile (22059/44118 counts) = CallSiteProfile$Circle
@ 27 CallSiteProfile$Circle::area (4 bytes) inline (hot) callee changed to CallSiteProfile$Square::area (4 bytes) inline (hot) callee changed to CallSiteProfile::total (42 bytes) \-> TypeProfile (265945/531890 counts) = CallSiteProfile$Square callee changed to CallSiteProfile::total (42 bytes) \-> TypeProfile (265945/531890 counts) = CallSiteProfile$Circle
@ 27 CallSiteProfile$Shape::area (0 bytes) failed to inline: no static binding
```
<!-- /output -->

2種類の型が現れると、C2は両方の型のメソッドを、型の確認を付けてインライン化します。
`if (Circle) Circle.areaを展開 else if (Square) Square.areaを展開`のような形になります。
<!-- claim: callsite-bi -->

### メガモーフィック（3種類以上の型）

<!-- output: callsite-mega -->
```
@ 27 CallSiteProfile$Shape::area (0 bytes) failed to inline: no static binding
@ 27 CallSiteProfile$Shape::area (0 bytes) failed to inline: virtual call
```
<!-- /output -->

3種類の型がほぼ同じ割合で現れると、C2はインライン化せず、仮想呼び出し（`failed to inline: virtual call`）のままにします。
このときはvtable（インターフェイスならitable）を引いて呼び出します。
<!-- claim: callsite-mega -->

ただし、1つの型が呼び出しの90%以上（`TypeProfileMajorReceiverPercent`）を占めていれば、ほかの型が混じっていても、その型のメソッドだけはインライン化します。
<!-- claim: major-receiver -->

型の確認が外れたとき（記録になかった型が来たとき）は、JVMはコンパイル済みのコードを捨ててインタープリターに戻り（脱最適化）、あらためてコンパイルし直します。

## 6. クラス階層解析（CHA）

上の出力の`inline`（`hot`の付かない行）は、C1の判断です。
C1は型のプロファイルを使わず、読み込まれているクラスの階層を調べます（クラス階層解析、CHA）。
`mono`の実行では`Square`と`Triangle`のクラスがまだ読み込まれていないので、`Shape`の実装は`Circle`だけで、C1もインライン化できます。
`bi`の実行では実装が2つ読み込まれているので、C1は`failed to inline: no static binding`と表示してインライン化しません。
<!-- claim: c1-cha -->

C2も同じ解析をします。
`final`でないメソッドでも、オーバーライドするクラスが読み込まれていなければ呼び出し先を1つに決めてインライン化し、あとでオーバーライドするサブクラスが読み込まれたら、そのコードを脱最適化します。
`verify/ClassHierarchy.java`を`-XX:+PrintCompilation`で実行すると、サブクラスを読み込んだ直後に、コンパイル済みの`sum`が捨てられる様子が見えます。

<!-- output: cha-deopt -->
```
3 ClassHierarchy::sum (24 bytes)
% 4 ClassHierarchy::sum @ 4 (24 bytes)
4 ClassHierarchy::sum (24 bytes)
3 ClassHierarchy::sum (24 bytes) made not entrant: not used
--- サブクラスを読み込む ---
% 4 ClassHierarchy::sum @ 4 (24 bytes) made not entrant: marked for deoptimization
4 ClassHierarchy::sum (24 bytes) made not entrant: marked for deoptimization
```
<!-- /output -->

`made not entrant: marked for deoptimization`は、コンパイルしたときの前提（オーバーライドするクラスがない）が崩れたため、そのコードをもう使わないという意味です。
<!-- claim: cha-deopt -->

そのため、性能のために`final`を付ける必要はありません。
`final`は、継承させたくない、変更させたくないという設計の意図（不変オブジェクトなど）を表すために付けます。

## 7. インライン化の条件

インライン化するかどうかは、主に呼び出し先のバイトコードの大きさと、呼び出しの深さで決まります。

- よく実行されない呼び出し箇所では、バイトコードが35バイト（`MaxInlineSize`）以下のメソッド
  <!-- claim: max-inline-size -->
- よく実行される呼び出し箇所では、325バイト（`FreqInlineSize`）以下のメソッド
  <!-- claim: freq-inline-size -->
- インライン化の深さは15段（`MaxInlineLevel`）まで
  <!-- claim: max-inline-level -->
- 自分自身の再帰呼び出しは1段（`MaxRecursiveInlineLevel`）まで
  <!-- claim: max-recursive-inline-level -->
- 仮想メソッドの場合は、呼び出し先を絞り込めること（5・6）

`final`・`private`・`static`であることは条件ではありません。

`verify/InliningLimits.java`は、6バイト・36バイト・379バイトのメソッドと再帰のメソッドを繰り返し呼びます。
<!-- claim: inlining-messages -->

<!-- output: inlining-messages -->
```
@ 12 InliningLimits::depth (17 bytes) failed to inline: recursive inlining is too deep
@ 12 InliningLimits::depth (17 bytes) failed to inline: recursive inlining too deep
@ 12 InliningLimits::depth (17 bytes) inline
@ 12 InliningLimits::depth (17 bytes) inline (hot)
@ 12 InliningLimits::small (6 bytes) inline
@ 12 InliningLimits::small (6 bytes) inline (hot)
@ 16 InliningLimits::medium (36 bytes) failed to inline: callee is too large
@ 16 InliningLimits::medium (36 bytes) inline (hot)
@ 21 InliningLimits::large (379 bytes) failed to inline: callee is too large
@ 21 InliningLimits::large (379 bytes) failed to inline: hot method too big
@ 29 InliningLimits::depth (17 bytes) inline
@ 29 InliningLimits::depth (17 bytes) inline (hot)
```
<!-- /output -->

36バイトの`medium`は、C1では`callee is too large`でしたが、よく呼ばれるのでC2ではインライン化されています（`inline (hot)`）。
379バイトの`large`は、よく呼ばれても325バイトを超えるので`hot method too big`です。
再帰の`depth`は1段だけ展開され、その先は`recursive inlining is too deep`になります。
<!-- claim: inlining-messages -->

## 8. インターフェイス呼び出し

`invokeinterface`は、インタープリターやメガモーフィックな呼び出し箇所ではitableを探す分だけ手間がかかります。
一方、5の観察のとおり、呼び出し箇所に現れる実装が1つか2つなら、クラス経由の呼び出しと同じようにインライン化されます（`CallSiteProfile`の`Shape`はインターフェイスです）。

## 9. 時間を測るデモ

`VirtualMethodTableDemo`は、次のような形式で測定結果を表示します（数値は省略しています）。
測定値はCPU・OS・JVMの状態によって大きく変わり、実行するたびにも変わります。
このデモは傾向を体験するためのもので、正確に測るにはJMH（Java Microbenchmark Harness）を使います。

```
=== Monomorphic / Bimorphic / Megamorphic Call Sites ===
Each call site calls area() 1200000 times
Monomorphic (Circle only):            ... μs
Bimorphic (Circle, Rectangle):        ... μs
Megamorphic (Circle, Rectangle, Triangle): ... μs
Megamorphic / monomorphic: ...x

=== Final Modifier and Inlining ===
Non-final class: ... μs
Final class:     ... μs
Non-final / final: ...x

=== Interface vs Class Call ===
Class call:     ... μs
Interface call: ... μs
Interface / class: ...x
```

3つの呼び出し箇所はどれも同じ回数だけ`area()`を呼び、測る前に暖機（ウォームアップ）しています。
時間が短すぎて測れなかったときは、比の代わりに`n/a (too fast to measure)`と表示します。
6・8で見たとおり、`final`の有無やインターフェイス経由かどうかは、インライン化の判断を変えません。
時間に差が出るとすれば、メガモーフィックな呼び出し箇所でインライン化されないことによるものです。

## よくある誤解

- `final`を付けるとメソッドの呼び出しが速くなる
  - オーバーライドするクラスがなければ、`final`がなくてもCHAでインライン化される（6）
  <!-- claim: cha-deopt -->
- インターフェイス経由の呼び出しは遅い
  - 呼び出し箇所に現れる実装が1つか2つなら、クラス経由と同じくインライン化される（5・8）
  <!-- claim: callsite-mono, callsite-bi -->
- `private`メソッドは`invokespecial`で呼ばれる
  - Java 11からは`invokevirtual`である（1）
  <!-- claim: private-nestmates -->
- vtableはJavaの仕様で決まっている
  - 仕様が決めているのはメソッドの選び方で、vtableはJVMの実装の工夫である（2）

## 実践的なガイドライン

- `final`は設計の意図を表すために付ける（性能のためではない）
- ユーティリティメソッドは、インスタンスの状態を使わないなら`static`にする（これも設計上の理由）
- 小さく、1つのことをするメソッドに分ける。結果としてインライン化もされやすい
- 性能が問題になったら、まずプロファイラー（JFRなど）で時間を使っている箇所を特定する
- その箇所が多くの型を扱うメガモーフィックな呼び出し箇所なら、型ごとに処理を分けることを検討する
- 変更の前後をJMHで測って確かめる

## 分析に使う道具

```bash
# コンパイルの様子
java -XX:+PrintCompilation ...

# インライン化の判断（診断用のオプションは-XX:+UnlockDiagnosticVMOptionsを先に書く）
java -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining ...

# フラグの既定値
java -XX:+PrintFlagsFinal -version
```

- JFR（Java Flight Recorder）は、JVMに組み込まれた低負荷のプロファイラー
- JMHは、マイクロベンチマークを正しく測るためのツール

## 参考資料

- Java仮想マシン仕様（Java SE 25）
  - Chapter 2 The Structure of the Java Virtual Machine
  - 5.4.6「Method Selection」
  - 6.5「Instructions」（`invokevirtual`・`invokeinterface`・`invokespecial`・`invokestatic`）
  - `https://docs.oracle.com/javase/specs/jvms/se25/html/`
- javaコマンドのリファレンス（Java SE 25）
  - `https://docs.oracle.com/en/java/javase/25/docs/specs/man/java.html`
- JEP 181: Nest-Based Access Control
  - `https://openjdk.org/jeps/181`
