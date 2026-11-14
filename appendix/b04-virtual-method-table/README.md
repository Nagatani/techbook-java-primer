# 付録B.04 仮想メソッドテーブル（vtable）と動的ディスパッチ

JVMがポリモーフィズムをどのように実現し、JITコンパイラがその呼び出しをどう最適化するかを学習できるプロジェクトです。

## 概要

Javaのポリモーフィズムは、仮想メソッドテーブル（vtable）による動的ディスパッチで実現されています。
JITコンパイラは、呼び出し箇所ごとに実際に現れた型を記録し、その情報を使って仮想メソッドの呼び出しをインライン化します。
この付録では、vtableの仕組み、`invokevirtual`と`invokeinterface`、モノモーフィック・バイモーフィック・メガモーフィックな呼び出し箇所、クラス階層解析（CHA）を順に見ていきます。

## サンプルコード構成

### 1. 仮想メソッドテーブルと呼び出し箇所の最適化
- `VirtualMethodTableDemo.java`
  - Shape階層を例にした、vtableの構造の可視化（簡略化したシミュレーション）
  - 動的ディスパッチの手順のシミュレーション
  - モノモーフィック・バイモーフィック・メガモーフィックな呼び出し箇所の比較
  - `final`の有無による違い（CHAによって、ふつうは差が出ないこと）
  - インターフェイス経由の呼び出しとクラス経由の呼び出しの比較

### 2. メソッドインライン化
- `MethodInliningDemo.java`
  - 繰り返し呼ばれるメソッドがJITコンパイルされる様子
  - インライン化の判断に影響する要因（メソッドの大きさ、再帰など）
  - メソッドチェーンとインライン化
  - インライン化の深さの制限

## 実行方法

### コンパイルと実行
```bash
javac -d . src/main/java/com/example/vtable/*.java

# 仮想メソッドテーブルと動的ディスパッチのデモ
java com.example.vtable.VirtualMethodTableDemo

# メソッドインライン化と最適化のデモ
java com.example.vtable.MethodInliningDemo
```

### JVMフラグ
```bash
# JITコンパイルとインライン化の判断を表示
# （診断用のオプションは-XX:+UnlockDiagnosticVMOptionsを先に書く）
java -XX:+PrintCompilation -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining \
     com.example.vtable.MethodInliningDemo

# JITの閾値などの既定値を確かめる
java -XX:+PrintFlagsFinal -version
```

## 学習ポイント

### 1. メソッド呼び出しのバイトコード

```java
public static void staticMethod() { }      // invokestatic（呼び出し先はコンパイル時に決まる）
private void privateMethod() { }           // invokevirtual（Java 11以降。それより前はinvokespecial）
public final void finalMethod() { }        // invokevirtual（JITは直接呼び出しにできる）
public void virtualMethod() { }            // invokevirtual（実行時に呼び出し先を決める）
interfaceRef.method();                     // invokeinterface
```

`final`メソッドと`private`メソッドの呼び出しも、バイトコードでは`invokevirtual`です（`javap -c -p`で確かめられます）。
オーバーライドできないことがわかっているので、JVMは呼び出し先を1つに決められます。
`invokespecial`は、コンストラクター（`<init>`）と`super.メソッド()`の呼び出しに使われます。

### 2. 仮想メソッドテーブルの構造

#### クラス階層とvtable
```
Animal vtable:                Dog vtable:
+-------+------------------+  +-------+------------------+
| Index | Method           |  | Index | Method           |
+-------+------------------+  +-------+------------------+
| 0     | Object.toString  |  | 0     | Object.toString  |
| 1     | Object.equals    |  | 1     | Object.equals    |
| 2     | Object.hashCode  |  | 2     | Object.hashCode  |
| ...   | (Objectのほかの   |  | ...   | (Objectのほかの   |
|       |  メソッド)        |  |       |  メソッド)        |
| 10    | Animal.move      |  | 10    | Dog.move         | ← Override
| 11    | Animal.eat       |  | 11    | Animal.eat       | ← 継承
| 12    | Animal.sleep     |  | 12    | Animal.sleep     | ← 継承
+-------+------------------+  | 13    | Dog.bark         | ← 新規
                               +-------+------------------+
```

インデックスの値は説明のためのものです。
サブクラスのvtableは親クラスのvtableと同じ並びで始まり、オーバーライドしたメソッドは親クラスと同じインデックスの欄を置き換えます。
そのため、`Animal`型の変数から`move()`を呼ぶとき、実際のオブジェクトが`Dog`でも同じインデックス10を見ればよいことになります。

#### 動的ディスパッチの手順
1. オブジェクトの実際のクラスを取得する（オブジェクトヘッダーから）
2. そのクラスのvtableを参照する
3. 呼び出し箇所に対応するインデックスの欄を読む（インデックスは呼び出し箇所を最初に実行するときに決まる）
4. その欄が指すメソッドを実行する

インターフェイスのメソッドは、クラスごとに並びが違うため、インターフェイス用の表（itable）を使って探します。

### 3. JITコンパイラによる最適化

#### 段階的コンパイル（Tiered Compilation）
```
Level 0: インタープリター
Level 1: C1（プロファイルなし）
Level 2: C1（呼び出し回数などの限定的なプロファイル）
Level 3: C1（完全なプロファイル）
Level 4: C2（プロファイルを使った高度な最適化）
```

よく呼ばれるメソッドは、インタープリター実行、C1でのコンパイル、C2でのコンパイルの順に進みます。
JDK 25の既定では、呼び出し回数がおよそ200回（`Tier3InvocationThreshold`）でC1、5000回（`Tier4InvocationThreshold`）でC2のコンパイルの対象になります（ループの回数なども加味されます）。
値は`java -XX:+PrintFlagsFinal -version`で確かめられます。
段階的コンパイルが有効なとき（既定）は、`-XX:CompileThreshold`は使われません。

#### インライン化の条件
- 通常の呼び出し箇所では、バイトコードが35バイト（`MaxInlineSize`）以下のメソッド
- よく実行される呼び出し箇所では、325バイト（`FreqInlineSize`）以下のメソッド
- インライン化の深さは15段（`MaxInlineLevel`）まで。自分自身の再帰呼び出しは1段（`MaxRecursiveInlineLevel`）まで
- 仮想メソッドの場合は、呼び出し先を絞り込めること（次の節）

`final`・`private`・`static`であることは条件ではありません。

### 4. 呼び出し箇所の最適化（インラインキャッシュ）

JITは呼び出し箇所ごとに、実際に現れたレシーバー（メソッドを呼ばれるオブジェクト）の型を記録します。

#### モノモーフィック（1種類の型）
```java
for (Animal animal : dogs) {   // 実際にはDogしか現れない
    animal.move();             // 「Dogか？」を確かめたうえでDog.move()をインライン化
}
```

#### バイモーフィック（2種類の型）
```java
for (Animal animal : dogsAndCats) {
    animal.move();  // if (Dog) Dog.moveをインライン展開 else if (Cat) Cat.moveをインライン展開
}
```

#### メガモーフィック（3種類以上の型）
```java
for (Animal animal : mixedAnimals) {
    animal.move();  // ふつうはvtable（インターフェイスならitable）を引いて呼び出す
}
```

型の確認が外れたとき（記録になかった型が来たとき）は、JVMはコンパイル済みのコードを捨ててインタープリターに戻り（脱最適化）、あらためてコンパイルし直します。

### 5. クラス階層解析（CHA）

```java
class Calculator {               // finalではない
    public int add(int a, int b) { return a + b; }
}
// Calculatorを継承してaddをオーバーライドするクラスが、まだ読み込まれていない
```

HotSpotのJITは、読み込まれているクラスの階層を調べ（クラス階層解析、CHA）、オーバーライドしているクラスがなければ、`final`でないメソッドでも呼び出し先を1つに決めてインライン化します。
あとでオーバーライドするサブクラスが読み込まれたら、そのコードを脱最適化してコンパイルし直します。
そのため、性能のために`final`を付ける必要はありません。
`final`は、継承させたくない、変更させたくないという設計の意図（不変オブジェクトなど）を表すために付けます。

### 6. インターフェイス呼び出し

```java
interface Drawable { void draw(); }

class Circle implements Drawable {
    public void draw() { }
}
```

`invokeinterface`は、インタープリターやメガモーフィックな呼び出し箇所ではitableを探す分だけ手間がかかります。
一方、呼び出し箇所に現れる実装が1つか2つなら、クラス経由の呼び出しと同じようにインライン化されます。

## 実行結果の形式

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

メガモーフィックな呼び出し箇所だけが遅くなり、`final`の有無とインターフェイス経由かどうかでは差がほとんど出ないのが、ふつうの結果です。
3つの呼び出し箇所はどれも同じ回数だけ`area()`を呼び、測る前に暖機（ウォームアップ）しています。
時間が短すぎて測れなかったときは、比の代わりに`n/a (too fast to measure)`と表示します。

## 実践的なガイドライン

### 1. 設計を優先する
- `final`は設計の意図を表すために付ける（性能のためではない）
- ユーティリティメソッドは、インスタンスの状態を使わないなら`static`にする（これも設計上の理由）
- 小さく、1つのことをするメソッドに分ける。結果としてインライン化もされやすい

### 2. 性能が問題になったとき
- まずプロファイラーで、時間を使っている箇所を特定する
- その箇所が多くの型を扱うメガモーフィックな呼び出し箇所なら、型ごとに処理を分けることを検討する
- 変更の前後をJMHで測って確かめる

### 3. 分析に使う道具

```bash
# コンパイルの様子
-XX:+PrintCompilation

# インライン化の判断
-XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining

# メソッドごとのプロファイル（実行回数や型の記録）
-XX:+UnlockDiagnosticVMOptions -XX:+PrintMethodData
```

- JFR（Java Flight Recorder）は、JVMに組み込まれた低負荷のプロファイラー
- async-profilerは、軽量なサンプリング型のプロファイラー
- JMHは、マイクロベンチマークを正しく測るためのツール

## トラブルシューティング

### 期待した最適化が効かない
```bash
# 原因調査
-XX:+PrintCompilation -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining
```

よくある原因は次のとおりです。

- メソッドが大きすぎる（`PrintInlining`に「too big」「hot method too big」と出る）
- 呼び出し箇所がメガモーフィックになっている
- 測定が短すぎて、まだJITコンパイルされていない

### JITコンパイルの時間を減らしたい
```bash
# C1だけを使う（C2のコンパイルをしない。起動は速いが、長時間動かすと遅くなりやすい）
-XX:TieredStopAtLevel=1
```

### コードキャッシュが足りない
```bash
# コンパイル済みコードを置く領域の大きさを指定
-XX:ReservedCodeCacheSize=256m
```

このプロジェクトを通じて、仮想メソッドの呼び出しの仕組みと、JITコンパイラが行う最適化の考え方を理解することが目標です。
